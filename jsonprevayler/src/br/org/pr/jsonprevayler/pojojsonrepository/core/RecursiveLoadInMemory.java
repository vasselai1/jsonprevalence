package br.org.pr.jsonprevayler.pojojsonrepository.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.configuration.PojoPrevalenceConfigurator;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.IntegrityInspector;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.LoadInstruction;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.MappingType;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.PrevalentAtributesValuesIdentificator;
import br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory.SingleThreadSearchProcessorFactory;

public class RecursiveLoadInMemory {

	public static <T extends PrevalenceEntity> void reloadObjectInRelations(FileCore fileCore, MemoryCore memoryCore, T updatedEntity) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, IOException, ValidationPrevalenceException, InterruptedException {
		//TODO Percorrer indices é mais rapido...
		PojoPrevalenceConfigurator pojoPrevalenceConfigurator = new PojoPrevalenceConfigurator(new SingleThreadSearchProcessorFactory(), fileCore.getPrevalencePath(), fileCore.getSystemName(), memoryCore.getInitializationMemoryCoreType());
		List<T> relations = (List<T>) new IntegrityInspector(pojoPrevalenceConfigurator).listPrevalentRelations(updatedEntity);
		for (T entityToUpdate : relations) {
			List<LoadInstruction> loadInstructions = PrevalentAtributesValuesIdentificator.getLoadInstructions(entityToUpdate);
			for (LoadInstruction loadInstruction : loadInstructions) {
				List<? extends PrevalenceEntity> entitiesToReplace = getEntityRelationed(loadInstruction, updatedEntity);
				if ((entitiesToReplace == null) || entitiesToReplace.isEmpty()) {
					continue;
				}
				List<? extends PrevalenceEntity> reloadedEntities = reloadAll(entitiesToReplace, memoryCore);
				if (MappingType.ENTITY.equals(loadInstruction.getMappingType())) {			
					loadInstruction.getSetMethod().invoke(updatedEntity, reloadedEntities.get(0)); 
				}
				if (MappingType.ENTITY_COLLECTION.equals(loadInstruction.getMappingType())) {
					Collection collectionReloaded = (Collection) loadInstruction.getGetMethod().getReturnType().getDeclaredConstructor().newInstance();
					collectionReloaded.addAll(reloadedEntities);
					loadInstruction.getSetMethod().invoke(updatedEntity, collectionReloaded);
				}
				if (MappingType.ENTITY_MAP.equals(loadInstruction.getMappingType())) {
					Map mapReloaded = (Map) loadInstruction.getGetMethod().getReturnType().getDeclaredConstructor().newInstance();
					for (PrevalenceEntity entityReloaded : reloadedEntities) {
						mapReloaded.put(entityReloaded.getId(), entityReloaded);
					}
					loadInstruction.getSetMethod().invoke(updatedEntity, mapReloaded);
				}
			}
		}		
	}
	
	private static List<PrevalenceEntity> reloadAll(List<? extends PrevalenceEntity> entitiesToReload, MemoryCore memoryCore) throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, ValidationPrevalenceException, InterruptedException {
		ArrayList<PrevalenceEntity> reloadedEntities = new ArrayList<PrevalenceEntity>();
		for (PrevalenceEntity entityToReload : entitiesToReload) {						
			PrevalenceEntity entityReloaded = memoryCore.getPojo(entityToReload.getClass(), entityToReload.getId());
			reloadedEntities.add(entityReloaded);
		}
		return reloadedEntities;
	}
	
	private static <T extends PrevalenceEntity> List<T> getEntityRelationed(LoadInstruction loadInstruction, T updatedEntity) {
		if (MappingType.ENTITY.equals(loadInstruction.getMappingType())) {
			T entityToReload = (T) loadInstruction.getOriginalValue();
			entityToReload = getOnlyEqualEntity(updatedEntity, entityToReload);
			if (entityToReload != null) {
				return Arrays.asList(getOnlyEqualEntity(updatedEntity, entityToReload));
			}
		}
		Collection<T> entities = null;
		if (MappingType.ENTITY_COLLECTION.equals(loadInstruction.getMappingType())) {
			 entities = (Collection<T>) loadInstruction.getOriginalValue();
		}
		if (MappingType.ENTITY_MAP.equals(loadInstruction.getMappingType())) {
			 entities = ((Map<Long, T>) loadInstruction.getOriginalValue()).values();
		}
		if (entities != null) {
			ArrayList<T> returnEntities = new ArrayList<T>();
			for (T entityLoop : entities) {
				T entityToReload = getOnlyEqualEntity(updatedEntity, entityLoop);
				if (entityToReload != null) {
					returnEntities.add(entityToReload);
				}
			}
			return returnEntities;
		}
		return null;
	}

	private static <T extends PrevalenceEntity> T getOnlyEqualEntity(T updatedEntity, T entityToReload) {
		if (entityToReload == null) {
			return null;
		}
		if (updatedEntity.getClass().isAssignableFrom(entityToReload.getClass()) && (updatedEntity.getId().equals(entityToReload.getId()))) {
			return entityToReload; 
		}
		return null;
	}
	
	
	
}
