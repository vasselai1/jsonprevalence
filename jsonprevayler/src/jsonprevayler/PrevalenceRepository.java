package jsonprevayler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import jsonprevayler.entity.PersistenceEntity;
import jsonprevayler.entity.PrevalenceEntity;
import jsonprevayler.entity.VersionedEntity;
import jsonprevayler.exceptions.ValidationException;
import jsonprevayler.search.PrevalenceFilter;
import jsonprevayler.search.processing.SearchProcessor;
import jsonprevayler.search.processing.SingleThreadSearchProcessor;
import jsonprevayler.util.ObjectCopyUtil;

public class PrevalenceRepository {

	public enum OperationType {
		INITIALIZE,
		SAVE,
		UPDATE,
		DELETE;
	}
	
	private static final Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository = new HashMap<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>>();
	private static final Map<Class<? extends PrevalenceEntity>, Map<Long, String>> jsonRepository = new HashMap<Class<? extends PrevalenceEntity>, Map<Long, String>>();
	private static final List<PrevalenceChangeObserver> observers = new ArrayList<PrevalenceChangeObserver>();
	
	private final SequenceProvider sequenceUtil;
	private final String FS = File.separator;
	private final String systemPath;
	private SearchProcessor searchProcessor;
	
	private Logger log = Logger.getLogger(getClass().getName());
	
	public PrevalenceRepository(String path, String systemName) {
		systemPath = path + FS + systemName;
		sequenceUtil = new SequenceProvider(systemPath);
		searchProcessor = new SingleThreadSearchProcessor();
	}

	public PrevalenceRepository(String path, String systemName, SearchProcessor searchProcessor) {
		this.systemPath = path + FS + systemName;
		sequenceUtil = new SequenceProvider(systemPath);
		searchProcessor = new SingleThreadSearchProcessor();
		this.searchProcessor = searchProcessor;
	}	
	
	public void register(PrevalenceChangeObserver observer) {
		observers.add(observer);
	}
	
	public <T extends PrevalenceEntity> void save(Class<T> classe, T entity) throws ValidationException, IOException {
		if (classe == null) {
			throw new ValidationException("Classe is null!");
		}
		if (entity == null) {
			throw new ValidationException("Entity is null!");
		}
		if (entity instanceof PersistenceEntity) {
			throw new ValidationException("This entity is persistence only!");
		}
		if (entity.getId() != null) {
			throw new ValidationException("Id is seted!");
		}
		synchronized (classe) {
			Long id = sequenceUtil.get(classe);
			if (isIdUsed(classe, id)) {
				throw new ValidationException("Id " + id + " is repeated!");
			}
			if (entity instanceof VersionedEntity) {
				VersionedEntity newVersionedEntity = (VersionedEntity) entity;
				newVersionedEntity.setVersion(1);
			}
			entity.setId(id);
			writeRegister(classe, entity);
			updateMemory(classe, OperationType.SAVE, entity);;
		}
		sendOperationInfo(OperationType.SAVE, classe, entity.getId());
	}
	
	public  <T extends PrevalenceEntity> void update(Class<T> classe, T entity) throws ValidationException, IOException, ClassNotFoundException {
		if (entity == null) {
			throw new ValidationException("Entity is null!");
		}
		if (entity instanceof PersistenceEntity) {
			throw new ValidationException("This entity is persistence only!");
		}
		if (entity.getId() == null) {
			throw new ValidationException("Id is not seted!");
		}
		synchronized (classe) {
			if (entity instanceof VersionedEntity) {
				VersionedEntity newVersionedEntity = (VersionedEntity) entity;
				VersionedEntity oldVersionedEntity = (VersionedEntity) getPojo(classe, entity.getId());
				if (newVersionedEntity.getVersion() != oldVersionedEntity.getVersion()) {
					throw new ValidationException("Version " + newVersionedEntity.getVersion() + " is deprecated!");
				}
				newVersionedEntity.setVersion(oldVersionedEntity.getVersion() + 1);
			}
			writeRegister(classe, entity);
			updateMemory(classe, OperationType.UPDATE, entity);
		}
		sendOperationInfo(OperationType.UPDATE, classe, entity.getId());
	}	

	public  <T extends PrevalenceEntity> void delete(Class<T> classe, Long id) throws ValidationException, IOException {
		if (classe == null) {
			throw new ValidationException("Classe is null!");
		}
		if (id == null) {
			throw new ValidationException("Id is null!");
		}
		synchronized (classe) {
			deleteRegister(classe, id);
			updateMemory(classe, OperationType.DELETE, null);
		}
		sendOperationInfo(OperationType.DELETE, classe, id);
	}	
	
	public <T extends PrevalenceEntity> T getPojo(Class<T> classe, Long id) throws IOException, ClassNotFoundException {
		updateMemory(classe, OperationType.INITIALIZE);
		return (T) ObjectCopyUtil.copyEntity(pojoRepository.get(classe).get(id));
	}
	
	public <T extends PrevalenceEntity> String getJson(Class<T> classe, Long id) throws IOException {
		updateMemory(classe, OperationType.INITIALIZE);
		return  jsonRepository.get(classe).get(id);
	}
	
	public <T extends PrevalenceEntity> List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException {
		updateMemory(classe, OperationType.INITIALIZE);
		List<T> retorno = new ArrayList<T>();
		if (!pojoRepository.containsKey(classe)) {
			return retorno;
		}
		
		searchProcessor.setPrevalence(this);
		searchProcessor.process(classe, filter, retorno, pojoRepository);

		filter.setTotal(retorno.size());
		retorno.sort(filter.getComparator());
		int finalRegister = filter.getFirstResult() + filter.getPageSize();
		if (finalRegister > (retorno.size())) {
			finalRegister = retorno.size();
		}
		if (filter.getPageSize() <= 0) {
			return retorno;
		}
		return ObjectCopyUtil.copyList(classe, retorno.subList(filter.getFirstResult(), finalRegister));
	}

	public <T extends PrevalenceEntity> Collection<String> listJson(Class<T> classe) throws IOException {
		updateMemory(classe, OperationType.INITIALIZE);
		Map<Long, String> mapJson = jsonRepository.get(classe);
		return mapJson.values();
	}

	public <T extends PrevalenceEntity> Collection<T> listPojo(Class<T> classe) throws IOException, ClassNotFoundException {
		updateMemory(classe, OperationType.INITIALIZE);
		Map<Long, T> mapPojo = (Map<Long, T>) pojoRepository.get(classe);
		return ObjectCopyUtil.copyList(classe, mapPojo.values());
	}	
	
	public <T extends PrevalenceEntity> List<String> listJson(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException {
		updateMemory(classe, OperationType.INITIALIZE);
		List<T> filtrados = listPojo(classe, filter);
		List<String> retorno = new ArrayList<String>();
		for (T entityLoop : filtrados) {
			String json = jsonRepository.get(classe).get(entityLoop.getId());
			retorno.add(json);
		}
		return retorno;
	}
	
	public <T extends PrevalenceEntity> long count(Class<? extends PrevalenceEntity> classe, PrevalenceFilter<T> filter) throws IOException {
		updateMemory(classe, OperationType.INITIALIZE);
		if (!pojoRepository.containsKey(classe)) {
			return 0l;
		}
		long count = 0;
		Collection<Long> ids = pojoRepository.get(classe).keySet();
		for (Long id : ids) {
			T entity = (T) pojoRepository.get(classe).get(id);
			if (filter.isAcepted(entity)) {
				count++;
			}
		}
		return count;
	}
	
	public <T extends PrevalenceEntity> long count(Class<? extends PrevalenceEntity> classe) throws IOException {
		updateMemory(classe, OperationType.INITIALIZE);
		return pojoRepository.get(classe).size();
	}
	
	private <T extends PrevalenceEntity> File getFilePath(Class<T> classe) throws IOException {
		File system = new File(systemPath);
		if (!system.exists()) {
			system.mkdir();
		}
		File dirPathEntities = new File(system, "PREVALENCE");
		if (!dirPathEntities.exists()) {
			dirPathEntities.mkdir();
		}
		File dirPathClassName = new File(dirPathEntities, classe.getCanonicalName());
		if (!dirPathClassName.exists()) {
			log.info("Json Prevacelence for " + classe.getCanonicalName() + " initialized in " + dirPathClassName.getAbsolutePath());
			dirPathClassName.mkdir();
		}
		return dirPathClassName;
	}

	private <T extends PrevalenceEntity> List<T> readRegistries(Class<T> classe) throws IOException {
		List<T> retorno = new ArrayList<T>();
		JSONDeserializer<T> deserializer = new JSONDeserializer<T>();
		File file = getFilePath(classe);
		File[] dataFiles = file.listFiles();
		if (dataFiles == null) {
			return retorno;
		}
		log.info(classe.getSimpleName() + " " + dataFiles.length + " registries");
		int count = 0;
		final int sizePrint = 100000;
		int printInterval = sizePrint;
		boolean printStatus = (dataFiles.length > printInterval);
		for (File dataFile : dataFiles) {
			retorno.add(deserializer.deserialize(Files.readString(dataFile.toPath())));
			count++;
			if (printStatus && (count == printInterval)) {
				log.info(count + " of " +dataFiles.length);
				printInterval = printInterval + sizePrint;
			}
		}
		log.info("End of read " + dataFiles.length + " registries");
		return retorno;
	} 
	
	private <T extends PrevalenceEntity> void writeRegister(Class<T> classe, T entity) throws IOException {
		File dirPathClassName = getFilePath(classe);
		File fileRegister = new File(dirPathClassName, getFileRegisterName(classe, entity.getId()));
		if (!fileRegister.exists()) {
			fileRegister.createNewFile();
		}
		JSONSerializer serializer = new JSONSerializer();
		String json = serializer.deepSerialize(entity);		
		Files.write(fileRegister.toPath(), json.getBytes());
	}
	
	private <T extends PrevalenceEntity> void deleteRegister(Class<T> classe, Long id) throws IOException {
		File dirPathClassName = getFilePath(classe);
		File fileRegister = new File(dirPathClassName, getFileRegisterName(classe, id));
		if (fileRegister.exists()) {
			Files.delete(fileRegister.toPath());
		}
	}
	
	private <T extends PrevalenceEntity> String getFileRegisterName(Class<T> classe, Long id) {
		StringBuilder retorno = new StringBuilder();
		retorno.append(classe.getSimpleName()).append("_").append(id).append(".json");
		return retorno.toString();
	}
	
	public <T extends PrevalenceEntity> void initilize(Class<T> classe) throws IOException {
		log.info("Initilizing Prevalence for " + classe.getSimpleName() + " whith search processor " + searchProcessor.getClass().getSimpleName());
		long initial = System.currentTimeMillis();
		updateMemory(classe, OperationType.INITIALIZE, null);
		log.info("Up time " + (System.currentTimeMillis() - initial) + "ms for " + count(classe) + " " + classe.getSimpleName() + " registries");
	}
	
	private <T extends PrevalenceEntity> void updateMemory(Class<T> classe, OperationType operationType) throws IOException {
		updateMemory(classe, operationType, null);
	}
	
	private <T extends PrevalenceEntity> void updateMemory(Class<T> classe, OperationType operationType, T entity) throws IOException {
		JSONSerializer serializer = new JSONSerializer();
		if ((pojoRepository.get(classe) == null) || (jsonRepository.get(classe) == null)) {
			pojoRepository.put(classe, new HashMap<Long, PrevalenceEntity>());
			jsonRepository.put(classe, new HashMap<Long, String>());
			List<? extends PrevalenceEntity> registries = readRegistries(classe);
			for (PrevalenceEntity entityLoop : registries) {
				String json = serializer.deepSerialize(entityLoop);
				pojoRepository.get(classe).put(entityLoop.getId(), entityLoop);
				jsonRepository.get(classe).put(entityLoop.getId(), json);
			}
		}
		if (OperationType.INITIALIZE.equals(operationType)) {
			return;
		}
		if (OperationType.SAVE.equals(operationType)) {
			String json = serializer.deepSerialize(entity);
			pojoRepository.get(classe).put(entity.getId(), entity);
			jsonRepository.get(classe).put(entity.getId(), json);
			return;
		}
		if (OperationType.UPDATE.equals(operationType)) {
			String json = serializer.deepSerialize(entity);
			pojoRepository.get(classe).replace(entity.getId(), entity);
			jsonRepository.get(classe).replace(entity.getId(), json);
			return;
		}
		if (OperationType.DELETE.equals(operationType)) {
			pojoRepository.get(classe).remove(entity.getId());
			jsonRepository.get(classe).remove(entity.getId());
			return;
		}
	}
	
	private <T extends PrevalenceEntity> boolean isIdUsed(Class<T> classe, Long id) throws IOException {
		updateMemory(classe, OperationType.INITIALIZE);
		if (pojoRepository.get(classe) == null) {
			return false;
		}
		return pojoRepository.get(classe).containsKey(id);
	}
	
	private <T extends PrevalenceEntity> void sendOperationInfo(OperationType operationType, Class<T> classe, Long id) {
		for (PrevalenceChangeObserver observer : observers) {
			switch (operationType) {
				case SAVE: {
					observer.receiveNew(classe, id);
					break;
				}
				case UPDATE: {
					observer.receiveUpdate(classe, id);
					break;
				}
				case DELETE: {
					observer.receiveDelete(classe, id);
					break;
				}
				default: {
					break;
				}
			}
		}
	}
		
}