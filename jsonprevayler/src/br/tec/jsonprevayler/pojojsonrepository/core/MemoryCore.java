package br.tec.jsonprevayler.pojojsonrepository.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.tec.jsonprevayler.annotations.MappedSuperClassPrevalenceRepository;
import br.tec.jsonprevayler.entity.OperationLog;
import br.tec.jsonprevayler.entity.OperationStatus;
import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.PrevalenceChangeObserver;
import flexjson.JSONSerializer;

public class MemoryCore implements MemorySearchEngineInterface {
	
	private static final List<Class<? extends PrevalenceEntity>> controlClasses = Arrays.asList(OperationLog.class, OperationStatus.class);
	private static Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository = null;
	private static Map<Class<? extends PrevalenceEntity>, Map<Long, String>> jsonRepository = null;
	private static final List<PrevalenceChangeObserver> observers = new ArrayList<PrevalenceChangeObserver>();
	private static boolean inMaintenance = false;
	private FileCore fileCore;
	
	public MemoryCore(FileCore fileCore) {
		this.fileCore = fileCore;
	}
	
	public static void register(PrevalenceChangeObserver observer) {
		observers.add(observer);
	}

	public static void deRegister(PrevalenceChangeObserver observer) {
		observers.remove(observer);
	}
	
	private static <T extends PrevalenceEntity> boolean isInitialized(Class<T> classe) {
		if (pojoRepository == null) {
			return false;
		}
		return (pojoRepository.get(classe) != null);
	}
	
	public <T extends PrevalenceEntity> Class<T> updateMemory(Class<T> classe, MemoryOperationType operationType, T entity) throws InternalPrevalenceException, ValidationPrevalenceException {
		classe = getClassRepository(classe);
		if (inMaintenance) {
			throw new ValidationPrevalenceException("Prevalence don't be start when maintenance is running! Plese wait the end maintenance.");
		}
		JSONSerializer serializer = new JSONSerializer();
		if (!isInitialized(classe)) {
			synchronized (classe) {
				if (!isInitialized(classe)) {
					initPrevalence(classe, serializer);					
				}
			}
		}
		if (MemoryOperationType.INITIALIZE.equals(operationType)) {
			return classe;
		}
		if (MemoryOperationType.SAVE.equals(operationType) || MemoryOperationType.UPDATE.equals(operationType)) {			
			pojoRepository.get(classe).put(entity.getId(), entity);
			jsonRepository.get(classe).put(entity.getId(), serializer.deepSerialize(entity));
		}
		if (MemoryOperationType.DELETE.equals(operationType)) {
			pojoRepository.get(classe).remove(entity.getId());
			jsonRepository.get(classe).remove(entity.getId());
		}
		sendOperationInfo(operationType, classe, entity.getId());
		return classe;
	}
	private <T extends PrevalenceEntity> void initPrevalence(Class<T> classe, JSONSerializer serializer) throws InternalPrevalenceException, ValidationPrevalenceException {
		initAllStoredPrevalentRepositories(fileCore, serializer);
		if (pojoRepository.containsKey(classe)) {
			return;
		}
		pojoRepository.put(classe, new HashMap<Long, PrevalenceEntity>());
	}	
	
	private static synchronized void initAllStoredPrevalentRepositories(FileCore fileCore, JSONSerializer serializer) throws InternalPrevalenceException, ValidationPrevalenceException {
		if (pojoRepository != null) {
			return;
		}
		pojoRepository = new HashMap<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>>();
		jsonRepository = new HashMap<Class<? extends PrevalenceEntity>, Map<Long,String>>();
		List<Class<? extends PrevalenceEntity>> initializedPrevalentClasses = new ArrayList<Class<? extends PrevalenceEntity>>();
		for (Class<? extends PrevalenceEntity> classe : fileCore.listAllPrevalentClasses()) {			
			if (ignoreLoad(classe)) {
				continue;
			}
			Class<? extends PrevalenceEntity> classeRepository = getClassRepository(classe);
			pojoRepository.put(classeRepository, new HashMap<Long, PrevalenceEntity>());
			jsonRepository.put(classeRepository, new HashMap<Long, String>());
			Map<String, ? extends PrevalenceEntity> registries = fileCore.readRegistries(classe);
			for (String jsonLoop : registries.keySet()) {
				PrevalenceEntity entityLoop = registries.get(jsonLoop);
				pojoRepository.get(classeRepository).put(entityLoop.getId(), entityLoop);
				jsonRepository.get(classeRepository).put(entityLoop.getId(), jsonLoop);
			}
			initializedPrevalentClasses.add(classeRepository);
		}
	}
	
	private static <T extends PrevalenceEntity> boolean ignoreLoad(Class<T> classe) {
		return controlClasses.contains(classe);
	}
	
	
	@Override
	public <T extends PrevalenceEntity> Integer count(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		classe = updateMemory(classe, MemoryOperationType.INITIALIZE, null);
		return pojoRepository.get(classe).size();
	}		
	
	@SuppressWarnings("unchecked")
	public <T extends PrevalenceEntity> T getPojo(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		classe = updateMemory(classe, MemoryOperationType.INITIALIZE, null);
		return (T) pojoRepository.get(classe).get(id);
	}	
	
	public <T extends PrevalenceEntity> String getJson(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		classe = updateMemory(classe, MemoryOperationType.INITIALIZE, null);
		return jsonRepository.get(classe).get(id);
	}
	
	public <T extends PrevalenceEntity> String listJson(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		classe = updateMemory(classe, MemoryOperationType.INITIALIZE, null);
		return new JSONSerializer().serialize(jsonRepository.values());
	}	

	
	public <T extends PrevalenceEntity> boolean isIdUsed(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		classe = updateMemory(classe, MemoryOperationType.INITIALIZE, null);
		return pojoRepository.get(classe).containsKey(id);
	}		
	
	public <T extends PrevalenceEntity> Collection<Long> getKeys(Class<T> classe) throws ValidationPrevalenceException, InternalPrevalenceException {
		classe = updateMemory(classe, MemoryOperationType.INITIALIZE, null);
		return pojoRepository.get(classe).keySet();
	}

	@SuppressWarnings("unchecked")
	public <T extends PrevalenceEntity> Collection<T> getValues(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		classe = updateMemory(classe, MemoryOperationType.INITIALIZE, null);
		return (Collection<T>) pojoRepository.get(classe).values();
	}	
	
	@SuppressWarnings("unchecked")
	static <T extends PrevalenceEntity> Class<T> getClassRepository(Class<? extends PrevalenceEntity> classe) throws ValidationPrevalenceException {
		if (classe.isAnnotationPresent(MappedSuperClassPrevalenceRepository.class)) {
			MappedSuperClassPrevalenceRepository mappingAnnotation = classe.getAnnotation(MappedSuperClassPrevalenceRepository.class);
			if (mappingAnnotation.mapping() == null) {
				throw new ValidationPrevalenceException("The annotation" + MappedSuperClassPrevalenceRepository.class.getName()  +  "in class " + classe.getCanonicalName() + " doesn't have mapping");
			}
			return (Class<T>) mappingAnnotation.mapping();
		}
		return (Class<T>) classe;
	}	
	
	private <T extends PrevalenceEntity> void sendOperationInfo(MemoryOperationType operationType, Class<T> classe, Long id) {
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
			pojoRepository = null;
			inMaintenance = true;
		}		
	}
	
	public void startPosMaintenance() {
		synchronized (pojoRepository) {
			inMaintenance = false;			
		}
	}
	
}