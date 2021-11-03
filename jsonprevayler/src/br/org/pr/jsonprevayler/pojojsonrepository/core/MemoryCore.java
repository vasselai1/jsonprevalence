package br.org.pr.jsonprevayler.pojojsonrepository.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import br.org.pr.jsonprevayler.annotations.MappedSuperClassPrevalenceRepository;
import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.PrevalenceChangeObserver;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.LoadInstruction;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.MappingType;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.PrevalentAtributesValuesIdentificator;
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
			throw new RuntimeException("InitializationType is " + initilizationType + " and can only be informed once!");
		}
		initilizationType = initilization;
	}
	
	public static void register(PrevalenceChangeObserver observer) {
		observers.add(observer);
	}
	
	public InitializationMemoryCoreType getInitializationMemoryCoreType() {
		return initilizationType;
	}
	
	public <T extends PrevalenceEntity> void initialize(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		log.info("Initilizing Prevalence for " + classe.getCanonicalName());
		long initial = System.currentTimeMillis();
		updateMemory(classe, OperationType.INITIALIZE, null, false);		
		log.info("Up time " + (System.currentTimeMillis() - initial) + "ms for " + pojoRepository.get(classe).size() + " " + classe.getSimpleName() + " registries");
	}		
	
	public static <T extends PrevalenceEntity> boolean isInitialized(Class<T> classe) {
		return (pojoRepository.get(classe) != null);
	}
	
	public <T extends PrevalenceEntity> void updateMemory(Class<T> classe, OperationType operationType, T entity, boolean deep) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {

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
			T updatedEntity = entity;
			if (deep) {
				updatedEntity = RecursiveLoadInFile.reloadEntityGraph(fileCore, this, updatedEntity);
			}
			RecursiveLoadInMemory.reloadObjectInRelations(fileCore, this, updatedEntity);
			pojoRepository.get(classe).replace(entity.getId(), updatedEntity);
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
	}

	private <T extends PrevalenceEntity> void initPrevalence(Class<T> classe, JSONSerializer serializer) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
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
		loadPrevalentObjetcs(classe);
	}	
	
	@Override
	public <T extends PrevalenceEntity> Integer count(Class<T> classe) throws ValidationPrevalenceException, ClassNotFoundException, IOException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = getClassRepository(classe);
		initialize(classe);
		return pojoRepository.get(classe).size();
	}		
	
	@SuppressWarnings("unchecked")
	public <T extends PrevalenceEntity> T getPojo(Class<T> classe, Long id) throws IOException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = getClassRepository(classe);
		initialize(classe);
		return (T) ObjectCopyUtil.copyEntity(pojoRepository.get(classe).get(id));
	}	
	
	public <T extends PrevalenceEntity> String getJson(Class<T> classe, Long id) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException {
		classe = getClassRepository(classe);
		initialize(classe);
		if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
			return jsonRepository.get(classe).get(id);
		}
		return new JSONSerializer().deepSerialize(pojoRepository.get(classe).get(id));
	}
	
	public <T extends PrevalenceEntity> String listJson(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = getClassRepository(classe);
		initialize(classe);
		if (InitializationMemoryCoreType.POJO_AND_ENUM.equals(initilizationType)) {
			return new JSONSerializer().serialize(jsonRepository.get(classe).values());
		}
		return new JSONSerializer().serialize(pojoRepository.values());
	}	
	
	private <T extends PrevalenceEntity> void initializePrevalentRelations(Class<T> classe) throws ClassNotFoundException, NoSuchFieldException, SecurityException, ValidationPrevalenceException, IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		Set<Class<T>> prevalenceRelations = PrevalentAtributesValuesIdentificator.listPrevalenceRelations(classe);
		for (Class<T> classeInitializeLoop : prevalenceRelations) {
			Class<T> classeInitilize = getClassRepository(classeInitializeLoop);
			if (pojoRepository.containsKey(classeInitilize)) {
				continue;
			}
			initialize(classeInitilize);
		}
	}	

	@SuppressWarnings("unchecked")
	private <T extends PrevalenceEntity> void loadPrevalentObjetcs(Class<T> classe) throws ValidationPrevalenceException, NoSuchFieldException, SecurityException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		classe = getClassRepository(classe);
		Collection<T> objectsVerify = (Collection<T>) pojoRepository.get(classe).values();
		for (T entity: objectsVerify) {
			List<LoadInstruction> loadInstructions = PrevalentAtributesValuesIdentificator.getLoadInstructions(entity);
			executeLoadInstructions(entity, loadInstructions);
		}
	}
	
	private <T extends PrevalenceEntity> void executeLoadInstructions(T entity, List<LoadInstruction> loadInstructions) {
		for (LoadInstruction loadInstruction : loadInstructions) {
			//TODO carregar recursivamente antes dessa execução, cuidado com os locks..·
			
			if (MappingType.ENTITY.equals(loadInstruction.getMappingType())) {
				
				reSetEntity(entity, loadInstruction);
			}
			if (MappingType.ENTITY_COLLECTION.equals(loadInstruction.getMappingType())) {
				reSetColletion(entity, loadInstruction);
			}
			if (MappingType.ENTITY_MAP.equals(loadInstruction.getMappingType())) {
				reSetMap(entity, loadInstruction);
			}
		}
	}
	
	private <T extends PrevalenceEntity> void reSetEntity(T entity, LoadInstruction loadInstruction) {
		T relactionedEntity = (T) loadInstruction.getOriginalValue();
	//TODO	
	}
	
	private <T extends PrevalenceEntity> void reSetColletion(T entity, LoadInstruction loadInstruction) {
		
	}
	
	private <T extends PrevalenceEntity> void reSetMap(T entity, LoadInstruction loadInstruction) {
		
	}

	
	public <T extends PrevalenceEntity> boolean isIdUsed(Class<T> classe, Long id) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		initialize(classe);
		return pojoRepository.get(classe).containsKey(id);
	}		
	
	
	public <T extends PrevalenceEntity> Collection<Long> getKeys(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		initialize(classe);
		return pojoRepository.get(classe).keySet();
	}

	@SuppressWarnings("unchecked")
	public <T extends PrevalenceEntity> Collection<T> getValues(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		initialize(classe);
		return (Collection<T>) pojoRepository.get(classe).values();
	}	
	
	@SuppressWarnings("unchecked")
	public static <T extends PrevalenceEntity> Class<T> getClassRepository(Class<? extends PrevalenceEntity> classe) throws ValidationPrevalenceException {
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