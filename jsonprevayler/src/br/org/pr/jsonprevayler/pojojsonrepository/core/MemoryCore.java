package br.org.pr.jsonprevayler.pojojsonrepository.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import br.org.pr.jsonprevayler.annotations.MappedSuperClassPrevalenceRepository;
import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.PrevalenceChangeObserver;
import br.org.pr.jsonprevayler.util.ObjectCopyUtil;
import flexjson.JSONSerializer;

public class MemoryCore implements MemorySearchEngineInterface {
	
	private static final Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository = new HashMap<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>>();
	private static final Map<Class<? extends PrevalenceEntity>, Map<Long, String>> jsonRepository = new HashMap<Class<? extends PrevalenceEntity>, Map<Long, String>>();
	private static final List<PrevalenceChangeObserver> observers = new ArrayList<PrevalenceChangeObserver>();
	private static boolean inMaintenance = false;
	private static InitializationMemoryCoreType initilizationType = null;
	private FileCore fileCore;
	private Logger log = Logger.getLogger(getClass().getName());
	
	public MemoryCore(FileCore fileCore) {
		this.fileCore = fileCore;
	}

	public static void setInitializationType(InitializationMemoryCoreType initilization) {
		if ((initilizationType != null) && (!initilizationType.equals(initilization))) {
			throw new RuntimeException("InitializationType is " + initilizationType + " and can only be informed once time!");
		}
		initilizationType = initilization;
	}
	
	public static void register(PrevalenceChangeObserver observer) {
		observers.add(observer);
	}
	
	public <T extends PrevalenceEntity> void initilize(Class<T> classe) throws IOException, ValidationPrevalenceException {
		log.info("Initilizing Prevalence for " + classe.getCanonicalName());
		long initial = System.currentTimeMillis();
		updateMemory(classe, OperationType.INITIALIZE, null);
		log.info("Up time " + (System.currentTimeMillis() - initial) + "ms for " + pojoRepository.get(classe).size() + " " + classe.getSimpleName() + " registries");
	}		
	
	public <T extends PrevalenceEntity> void updateMemory(Class<T> classe, OperationType operationType, T entity) throws IOException, ValidationPrevalenceException {
		if (initilizationType == null) {
			throw new ValidationPrevalenceException("Initialization Type don't seted!");
		}
		if (inMaintenance) {
			throw new ValidationPrevalenceException("Prevalence don't be start when maintenance is running! Plese wait the end maintenance.");
		}
		JSONSerializer serializer = new JSONSerializer();
		if (pojoRepository.get(classe) == null) {
			initPrevalence(classe, serializer);
		}
		if (OperationType.INITIALIZE.equals(operationType)) {
			return;
		}
		if (OperationType.SAVE.equals(operationType)) {			
			pojoRepository.get(classe).put(entity.getId(), entity);
			if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
				String json = serializer.deepSerialize(entity);
				jsonRepository.get(classe).put(entity.getId(), json);
			}
		}
		if (OperationType.UPDATE.equals(operationType)) {			
			pojoRepository.get(classe).replace(entity.getId(), entity);
			if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
				String json = serializer.deepSerialize(entity);
				jsonRepository.get(classe).replace(entity.getId(), json);
			}
		}
		if (OperationType.DELETE.equals(operationType)) {
			pojoRepository.get(classe).remove(entity.getId());
			if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
				jsonRepository.get(classe).remove(entity.getId());
			}
		}
		sendOperationInfo(operationType, classe, entity.getId());
	}

	private <T extends PrevalenceEntity> void initPrevalence(Class<T> classe, JSONSerializer serializer) throws IOException, ValidationPrevalenceException {
		classe = getClassRepository(classe);
		pojoRepository.put(classe, new HashMap<Long, PrevalenceEntity>());
		jsonRepository.put(classe, new HashMap<Long, String>());			
		List<? extends PrevalenceEntity> registries = fileCore.readRegistries(classe);
		for (PrevalenceEntity entityLoop : registries) {			
			pojoRepository.get(classe).put(entityLoop.getId(), entityLoop);
			if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
				String json = serializer.deepSerialize(entityLoop);
				jsonRepository.get(classe).put(entityLoop.getId(), json);
			}
		}
		initializePrevalentRelations(classe);
	}	
	
	@Override
	public <T extends PrevalenceEntity> Integer count(Class<T> classe) throws IOException, ClassNotFoundException, ValidationPrevalenceException {
		classe = getClassRepository(classe);
		initializePrevalentRelations(classe);
		return pojoRepository.get(classe).size();
	}		
	
	@SuppressWarnings("unchecked")
	public <T extends PrevalenceEntity> T getPojo(Class<T> classe, Long id) throws IOException, ClassNotFoundException, ValidationPrevalenceException {
		classe = getClassRepository(classe);
		initilize(classe);
		return (T) ObjectCopyUtil.copyEntity(pojoRepository.get(classe).get(id));
	}	
	
	public <T extends PrevalenceEntity> String getJson(Class<T> classe, Long id) throws IOException, ValidationPrevalenceException {
		classe = getClassRepository(classe);
		initializePrevalentRelations(classe);
		if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
			return jsonRepository.get(classe).get(id);
		}
		return new JSONSerializer().deepSerialize(pojoRepository.get(classe).get(id));
	}
	
	public <T extends PrevalenceEntity> String listJson(Class<T> classe) throws IOException, ValidationPrevalenceException {
		classe = getClassRepository(classe);
		initilize(classe);
		if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
			return new JSONSerializer().serialize(jsonRepository.get(classe).values());
		}
		return new JSONSerializer().serialize(pojoRepository.values());
	}	
	
	private <T extends PrevalenceEntity> void initializePrevalentRelations(Class<T> classe) {
		//TODO
	}	

	public <T extends PrevalenceEntity> boolean isIdUsed(Class<T> classe, Long id) throws IOException, ValidationPrevalenceException {
		initilize(classe);
		return pojoRepository.get(classe).containsKey(id);
	}		
	
	
	public <T extends PrevalenceEntity> Collection<Long> getKeys(Class<T> classe) throws IOException, ValidationPrevalenceException {
		initilize(classe);
		return pojoRepository.get(classe).keySet();
	}

	@SuppressWarnings("unchecked")
	public <T extends PrevalenceEntity> Collection<T> getValues(Class<T> classe) throws IOException, ValidationPrevalenceException {
		initilize(classe);
		return (Collection<T>) pojoRepository.get(classe).values();
	}	
	
	@SuppressWarnings("unchecked")
	public <T extends PrevalenceEntity> Class<T> getClassRepository(Class<? extends PrevalenceEntity> classe) throws ValidationPrevalenceException {
		if (classe.isAnnotationPresent(MappedSuperClassPrevalenceRepository.class)) {
			MappedSuperClassPrevalenceRepository mappingAnnotation = classe.getAnnotation(MappedSuperClassPrevalenceRepository.class);
			if (mappingAnnotation.mapping() == null) {
				throw new ValidationPrevalenceException("The annotation" + MappedSuperClassPrevalenceRepository.class.getName()  +  "in class " + classe.getCanonicalName() + " doesn't have mapping");
			}
			return (Class<T>) mappingAnnotation.mapping();
		}
		return (Class<T>) classe;
	}	
	
	private <T extends PrevalenceEntity> void sendOperationInfo(OperationType operationType, Class<T> classe, Long id) {
		if (observers == null) {
			return;
		}
		
		Runnable sayOperationForObservers = new Runnable() {
			@Override
			public void run() {
				for (PrevalenceChangeObserver observer : observers) {
					if (observer == null) {
						continue;
					}
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
		}; 
		
		new Thread(sayOperationForObservers).run();
	}	
	
	
	public static boolean isMainInitialized() {
		return (!pojoRepository.isEmpty());
	}
	
	public void stopForMaintenance() {
		synchronized (pojoRepository) {
			pojoRepository.clear();
			jsonRepository.clear();
			inMaintenance = true;
		}		
	}
	
	public void startPosMaintenance() {
		synchronized (pojoRepository) {
			inMaintenance = false;			
		}
	}
	
}