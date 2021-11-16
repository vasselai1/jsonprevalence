package br.tec.jsonprevayler.pojojsonrepository.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.normalization.LoadInstruction;
import br.tec.jsonprevayler.infrastrutuctre.normalization.MappingType;
import br.tec.jsonprevayler.infrastrutuctre.normalization.PrevalentAtributesValuesIdentificator;

public class RecursiveLoadInFile {

	@SuppressWarnings("unchecked")
	public static <T extends PrevalenceEntity> T reloadEntityGraph(FileCore fileCore, MemoryCore memoryCore, T entityOld) throws NoSuchFieldException, SecurityException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InstantiationException, InterruptedException {
		Class<T> classe = MemoryCore.getClassRepository(entityOld.getClass());
		T updatedEntity = fileCore.readRegistry(classe, entityOld.getId());
		List<LoadInstruction> loadInstructions = PrevalentAtributesValuesIdentificator.getLoadInstructions(entityOld);		
		for (LoadInstruction loadInstruction : loadInstructions) {			
			Object objectEntityToReload = loadInstruction.getOriginalValue();
			if (objectEntityToReload == null) {
				continue;
			}			
			if (MappingType.ENTITY.equals(loadInstruction.getMappingType())) {
				T entityToReload = (T) loadInstruction.getOriginalValue();
				T entityReloaded = reloadEntityGraph(fileCore, memoryCore, entityToReload);
				loadInstruction.getSetMethod().invoke(updatedEntity, entityReloaded);
				memoryCore.updateMemory(classe, OperationType.UPDATE, entityReloaded, false);
				continue;
			}
			Collection<T> entiesToReload = null;
			if (MappingType.ENTITY_COLLECTION.equals(loadInstruction.getMappingType())) {
				entiesToReload = (Collection<T>) loadInstruction.getOriginalValue();
			}
			if (MappingType.ENTITY_MAP.equals(loadInstruction.getMappingType())) {
				entiesToReload = (Collection<T>) ((Map<Long, T>) loadInstruction.getOriginalValue()).values();
			}
			if ((entiesToReload == null) || entiesToReload.isEmpty()) {
				continue;
			}
			List<T> entitiesReloaded = new ArrayList<T>();
			for (T entityLoopReload : entiesToReload) {
				T entityReloaded = reloadEntityGraph(fileCore, memoryCore, entityLoopReload);
				memoryCore.updateMemory(classe, OperationType.UPDATE, entityReloaded, false);
				entitiesReloaded.add(entityReloaded);
			}
			if (MappingType.ENTITY_COLLECTION.equals(loadInstruction.getMappingType())) {
				Collection<T> collectionReloaded = (Collection<T>) loadInstruction.getGetMethod().getReturnType().getDeclaredConstructor().newInstance();
				collectionReloaded.addAll(entitiesReloaded);
				loadInstruction.getSetMethod().invoke(updatedEntity, collectionReloaded);
				continue;
			}
			if (MappingType.ENTITY_MAP.equals(loadInstruction.getMappingType())) {
				Map<Long, T> mapReloaded = convertToMap(entitiesReloaded, loadInstruction.getGetMethod().getReturnType());
				loadInstruction.getSetMethod().invoke(updatedEntity, mapReloaded);
				continue;
			}
		}
		memoryCore.updateMemory(classe, OperationType.UPDATE, updatedEntity, false);
		return updatedEntity;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends PrevalenceEntity> Map<Long, T> convertToMap(List<T> entitiesReloaded, Class mapTypeImplementation) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<Long, T> mapReloaded = (Map<Long, T>) mapTypeImplementation.getDeclaredConstructor().newInstance();
		for (T entityLoop : entitiesReloaded) {
			mapReloaded.put(entityLoop.getId(), entityLoop);
		}
		return mapReloaded;
	}
	
}
