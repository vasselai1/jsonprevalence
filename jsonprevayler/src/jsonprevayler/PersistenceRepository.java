package jsonprevayler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import jsonprevayler.PrevalenceRepository.OperationType;
import jsonprevayler.entity.PersistenceEntity;
import jsonprevayler.entity.PrevalenceEntity;
import jsonprevayler.exceptions.ValidationException;
import jsonprevayler.search.PersistenceFilter;

public class PersistenceRepository {

	private static final List<PersistenceChangeObserver> observers = new ArrayList<PersistenceChangeObserver>();

	private final SequenceProvider sequenceUtil;
	private final String systemPath;
	private final String FS = File.separator;
	private Logger log = Logger.getLogger(getClass().getName());
	
	public PersistenceRepository(String path, String systemName) {
		systemPath = path + FS + systemName;
		sequenceUtil = new SequenceProvider(systemPath);
	}
	
	public void register(PersistenceChangeObserver observer) {
		observers.add(observer);
	}
	
	public <T extends PersistenceEntity> void save(Class<T> classe, T entity) throws ValidationException, IOException {
		if (classe == null) {
			throw new ValidationException("Classe is null!");
		}
		if (entity == null) {
			throw new ValidationException("Entity is null!");
		}
		if (entity instanceof PrevalenceEntity) {
			throw new ValidationException("This entity is prevalence only!");
		}
		if (entity.getId() != null) {
			throw new ValidationException("Id is seted!");
		}
		synchronized (classe) {
			Long id = sequenceUtil.getSequence(classe);
			if (getPojo(classe, id) != null) {
				throw new ValidationException("Id " + id + " is repeated!");
			}
			entity.setId(id);
			writeRegister(classe, entity);
		}
		sendOperationInfo(OperationType.SAVE, classe, entity.getId());
	}
	
	public  <T extends PersistenceEntity> void update(Class<T> classe, T entity) throws ValidationException, IOException {
		if (entity == null) {
			throw new ValidationException("Entity is null!");
		}
		if (entity instanceof PrevalenceEntity) {
			throw new ValidationException("This entity is prevalence only!");
		}
		if (entity.getId() == null) {
			throw new ValidationException("Id is not seted!");
		}
		synchronized (classe) {
			writeRegister(classe, entity);
		}
	}	

	public  <T extends PersistenceEntity> void delete(Class<T> classe, Long id) throws ValidationException, IOException {
		if (classe == null) {
			throw new ValidationException("Classe is null!");
		}
		if (id == null) {
			throw new ValidationException("Id is null!");
		}
		synchronized (classe) {
			String dataFileName = getFileRegisterName(classe, id);
			File dataFile = new File(getFilePath(classe), dataFileName);
			if (dataFile.exists()) {
				dataFile.delete();
			}
		}
	}	

	public <T extends PersistenceEntity> T getPojo(Class<T> classe, Long id) throws IOException {
		String dataFileName = getFileRegisterName(classe, id);
		File dataFile = new File(getFilePath(classe), dataFileName);
		if ((dataFile == null) || !dataFile.exists()) {
			return null;
		}
		JSONDeserializer<T> jsonDeserializer = new JSONDeserializer<T>();
		return jsonDeserializer.deserialize(Files.readString(dataFile.toPath()));
	}
	
	public <T extends PersistenceEntity> String getJson(Class<T> classe, Long id) throws IOException {
		return new JSONSerializer().deepSerialize(getPojo(classe, id));
	}
	
	public <T extends PersistenceEntity> List<T> listPojo(Class<T> classe, PersistenceFilter<T> filter) throws IOException, InterruptedException {
		List<T> retorno = new ArrayList<T>();
		JSONDeserializer<T> jsonDeserializer = new JSONDeserializer<T>();
		for (File dataFile : listAll(classe)) {
			T entity = jsonDeserializer.deserialize(Files.readString(dataFile.toPath()));
			if (filter.isAcepted(entity)) {
				retorno.add(entity);	
			}
		}
		filter.setTotal(retorno.size());
		retorno.sort(filter.getComparator());
		int finalRegister = filter.getFirstResult() + filter.getPageSize();
		if (finalRegister > (retorno.size())) {
			finalRegister = retorno.size();
		}
		if (filter.getPageSize() <= 0) {
			return retorno;
		}
		return retorno.subList(filter.getFirstResult(), finalRegister);
	}	

	public <T extends PersistenceEntity> List<String> listJson(Class<T> classe, PersistenceFilter<T> filter) throws IOException, InterruptedException {
		List<String> retorno = new ArrayList<String>();
		JSONSerializer jsonSerializer = new JSONSerializer();
		for (T entity : listPojo(classe, filter)) {
			retorno.add(jsonSerializer.deepSerialize(entity));
		}
		return retorno;
	}	
	
	public <T extends PersistenceEntity> long count(Class<? extends PersistenceEntity> classe, PersistenceFilter<T> filter) throws IOException {
		JSONDeserializer<T> jsonDeserializer = new JSONDeserializer<T>();
		long count = 0;
		for (File dataFile : listAll(classe)) {
			T entity = jsonDeserializer.deserialize(Files.readString(dataFile.toPath()));
			if (filter.isAcepted(entity)) {
				count++;	
			}
		}
		return count;
	}	
	
	private <T extends PersistenceEntity> void writeRegister(Class<T> classe, T entity) throws IOException {
		File dirPathClassName = getFilePath(classe);
		File fileRegister = new File(dirPathClassName, getFileRegisterName(classe, entity.getId()));
		if (!fileRegister.exists()) {
			fileRegister.createNewFile();
		}
		JSONSerializer serializer = new JSONSerializer();
		String json = serializer.deepSerialize(entity);		
		Files.write(fileRegister.toPath(), json.getBytes());
	}
	
	private <T extends PersistenceEntity> String getFileRegisterName(Class<T> classe, Long id) {
		StringBuilder retorno = new StringBuilder();
		retorno.append(classe.getSimpleName()).append("_").append(id).append(".json");
		return retorno.toString();
	}
	
	private <T extends PersistenceEntity> File getFilePath(Class<T> classe) throws IOException {
		File system = new File(systemPath);
		if (!system.exists()) {
			system.mkdir();
		}
		File dirPathEntities = new File(system, "PERSISTENCE");
		if (!dirPathEntities.exists()) {
			dirPathEntities.mkdir();
		}
		File dirPathClassName = new File(dirPathEntities, classe.getCanonicalName());
		if (!dirPathClassName.exists()) {
			log.info("Persistence for " + classe.getCanonicalName() + " initialized in " + dirPathClassName.getAbsolutePath());
			dirPathClassName.mkdir();
		}
		return dirPathClassName;
	}
	
	private <T extends PersistenceEntity> void sendOperationInfo(OperationType operationType, Class<T> classe, Long id) {
		for (PersistenceChangeObserver observer : observers) {
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
	
	private <T extends PersistenceEntity> File[] listAll(Class<T> classe) throws IOException {
		return getFilePath(classe).listFiles();
	}

}