package br.org.pr.jsonprevayler.pojojsonrepository.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.org.pr.jsonprevayler.annotations.MappedSuperClassPrevalenceRepository;
import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.PrevalenceChangeObserver;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.IntegrityInspector;
import br.org.pr.jsonprevayler.util.ObjectCopyUtil;
import flexjson.JSONSerializer;

public class MemoryCore implements MemorySearchEngineInterface {
	
	private static Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository = null;
	private static Map<Class<? extends PrevalenceEntity>, Map<Long, String>> jsonRepository = null;
	private static final List<PrevalenceChangeObserver> observers = new ArrayList<PrevalenceChangeObserver>();
	private static boolean inMaintenance = false;
	private static InitializationMemoryCoreType initilizationType = null;
	private FileCore fileCore;
	
	public MemoryCore(FileCore fileCore) {
		this.fileCore = fileCore;
	}

	public static void setInitializationType(InitializationMemoryCoreType initilization) {
		if ((initilizationType != null) && (!initilizationType.equals(initilization))) {
			throw new RuntimeException("InitializationType is " + initilizationType + " and can only be informed once!");
		}
		initilizationType = initilization;
	}
	
	public static void register(PrevalenceChangeObserver observer) {
		observers.add(observer);
	}

	public static void deRegister(PrevalenceChangeObserver observer) {
		observers.remove(observer);
	}
	
	public InitializationMemoryCoreType getInitializationMemoryCoreType() {
		return initilizationType;
	}
	
	private static <T extends PrevalenceEntity> boolean isInitialized(Class<T> classe) {
		if (pojoRepository == null) {
			return false;
		}
		return (pojoRepository.get(classe) != null);
	}
	
	public <T extends PrevalenceEntity> Class<T> updateMemory(Class<T> classe, OperationType operationType, T entity, boolean deep) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = getClassRepository(classe);
		if (initilizationType == null) {
			throw new ValidationPrevalenceException("Initialization Type don't seted!");
		}
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
		if (OperationType.INITIALIZE.equals(operationType)) {
			return classe;
		}
		if (OperationType.SAVE.equals(operationType)) {			
			pojoRepository.get(classe).put(entity.getId(), entity);
			if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
				String json = serializer.deepSerialize(entity);
				jsonRepository.get(classe).put(entity.getId(), json);
			}
		}
		if (OperationType.UPDATE.equals(operationType)) {
			T updatedEntity = entity;
			if (deep) {
				updatedEntity = RecursiveLoadInFile.reloadEntityGraph(fileCore, this, updatedEntity);
				new  RecursiveLoadInMemory(fileCore, pojoRepository).reloadObjectInRelations(updatedEntity);
			}
			pojoRepository.get(classe).put(entity.getId(), updatedEntity);
			if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
				String json = serializer.deepSerialize(updatedEntity);
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
		return classe;
	}

	private <T extends PrevalenceEntity> void initPrevalence(Class<T> classe, JSONSerializer serializer) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		initAllStoredPrevalentRepositories(fileCore, serializer);
		if (pojoRepository.containsKey(classe)) {
			return;
		}
		pojoRepository.put(classe, new HashMap<Long, PrevalenceEntity>());
		jsonRepository.put(classe, new HashMap<Long, String>());			
	}	
	
	@SuppressWarnings("unchecked")
	private static synchronized void initAllStoredPrevalentRepositories(FileCore fileCore, JSONSerializer serializer) throws ClassNotFoundException, IOException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, ValidationPrevalenceException, InterruptedException {
		if (pojoRepository != null) {
			return;
		}
		pojoRepository = new HashMap<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>>();
		jsonRepository = new HashMap<Class<? extends PrevalenceEntity>, Map<Long, String>>();
		List<Class<? extends PrevalenceEntity>> initializedPrevalentClasses = new ArrayList<Class<? extends PrevalenceEntity>>();
		for (Class<? extends PrevalenceEntity> classe : fileCore.listAllPrevalentClasses()) {			
			Class<? extends PrevalenceEntity> classeRepository = getClassRepository(classe);
			pojoRepository.put(classeRepository, new HashMap<Long, PrevalenceEntity>());
			List<? extends PrevalenceEntity> registries = fileCore.readRegistries(classe);
			for (PrevalenceEntity entityLoop : registries) {			
				pojoRepository.get(classeRepository).put(entityLoop.getId(), entityLoop);
			}
			initializedPrevalentClasses.add(classeRepository);
		}
		RecursiveLoadInMemory recursiveLoadInMemory = new RecursiveLoadInMemory(fileCore, pojoRepository);
		for (Class<? extends PrevalenceEntity> classLoop : initializedPrevalentClasses) {
			Collection<PrevalenceEntity> entitiesToReloadRelactions = (Collection<PrevalenceEntity>) pojoRepository.get(classLoop).values();
			for (PrevalenceEntity entityLoop : entitiesToReloadRelactions) {
				recursiveLoadInMemory.reloadObjectInRelations(entityLoop);
			}
		}
		if (!InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
			return;
		}
		for (Class<? extends PrevalenceEntity> classLoop : initializedPrevalentClasses) {
			jsonRepository.put(classLoop, new HashMap<Long, String>());
			Collection<? extends PrevalenceEntity> entitiesToReloadRelactions = (Collection<? extends PrevalenceEntity>) pojoRepository.get(classLoop).values();
			for (PrevalenceEntity entityLoop : entitiesToReloadRelactions) {
				String json = serializer.deepSerialize(entityLoop);
				jsonRepository.get(classLoop).put(entityLoop.getId(), json);					
			}
		}		
	}
	
	public <T extends PrevalenceEntity> void validateExclusion(T entity)  throws ClassNotFoundException, IOException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, InterruptedException {
		new IntegrityInspector(fileCore, pojoRepository).validateExclusion(entity);
	}
	
	@Override
	public <T extends PrevalenceEntity> Integer count(Class<T> classe) throws ValidationPrevalenceException, ClassNotFoundException, IOException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = updateMemory(classe, OperationType.INITIALIZE, null, false);
		return pojoRepository.get(classe).size();
	}		
	
	@SuppressWarnings("unchecked")
	public <T extends PrevalenceEntity> T getPojo(Class<T> classe, Long id) throws IOException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = updateMemory(classe, OperationType.INITIALIZE, null, false);
		return (T) ObjectCopyUtil.copyEntity(pojoRepository.get(classe).get(id));
	}	
	
	public <T extends PrevalenceEntity> String getJson(Class<T> classe, Long id) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException {
		classe = updateMemory(classe, OperationType.INITIALIZE, null, false);
		if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
			return jsonRepository.get(classe).get(id);
		}
		return new JSONSerializer().deepSerialize(pojoRepository.get(classe).get(id));
	}
	
	public <T extends PrevalenceEntity> String listJson(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = updateMemory(classe, OperationType.INITIALIZE, null, false);
		if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
			return new JSONSerializer().serialize(jsonRepository.get(classe).values());
		}
		return new JSONSerializer().serialize(pojoRepository.values());
	}	

	
	public <T extends PrevalenceEntity> boolean isIdUsed(Class<T> classe, Long id) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = updateMemory(classe, OperationType.INITIALIZE, null, false);
		return pojoRepository.get(classe).containsKey(id);
	}		
	
	public <T extends PrevalenceEntity> Collection<Long> getKeys(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = updateMemory(classe, OperationType.INITIALIZE, null, false);
		return pojoRepository.get(classe).keySet();
	}

	@SuppressWarnings("unchecked")
	public <T extends PrevalenceEntity> Collection<T> getValues(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = updateMemory(classe, OperationType.INITIALIZE, null, false);
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
			pojoRepository = null;
			jsonRepository = null;
			inMaintenance = true;
		}		
	}
	
	public void startPosMaintenance() {
		synchronized (pojoRepository) {
			inMaintenance = false;			
		}
	}
	
}