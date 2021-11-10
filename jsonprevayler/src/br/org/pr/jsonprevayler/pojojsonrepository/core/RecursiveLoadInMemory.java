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
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.IntegrityInspector;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.LoadInstruction;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.MappingType;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.PrevalentAtributesValuesIdentificator;

public class RecursiveLoadInMemory {

	private final FileCore fileCore;
	private final Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository;
	
	public RecursiveLoadInMemory(FileCore fileCore, Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository) {
		this.fileCore = fileCore;
		this.pojoRepository = pojoRepository;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends PrevalenceEntity> void reloadObjectInRelations(T updatedEntity) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, IOException, ValidationPrevalenceException, InterruptedException {
		//TODO Percorrer indices é mais rapido...
		List<PrevalenceEntity> relations = (List<PrevalenceEntity>) new IntegrityInspector(fileCore, pojoRepository).listPrevalentRelations(updatedEntity);
		for (PrevalenceEntity entityToUpdate : relations) {
			List<LoadInstruction> loadInstructions = PrevalentAtributesValuesIdentificator.getLoadInstructions(entityToUpdate);
			for (LoadInstruction loadInstruction : loadInstructions) {
				List<? extends PrevalenceEntity> entitiesToReplace = getEntityRelationed(loadInstruction, updatedEntity);
				if ((entitiesToReplace == null) || entitiesToReplace.isEmpty()) {
					continue;
				}
				ArrayList<PrevalenceEntity> reloadedEntities = new ArrayList<PrevalenceEntity>();
				for (PrevalenceEntity entityToReload : entitiesToReplace) {//TODO verificar se polimorfismo está ok
					Class<? extends PrevalenceEntity> classe =  MemoryCore.getClassRepository(entityToReload.getClass());
					if (!pojoRepository.containsKey(classe)) {
						throw new RuntimeException("Initialization Error: MemoryCore don't contain " + classe);
					}
					PrevalenceEntity entityReloaded = (PrevalenceEntity) pojoRepository.get(classe).get(entityToReload.getId());
					if (entityReloaded == null) {
						throw new RuntimeException("Initialization Error: MemoryCore don't contain " + classe + " : " + entityToReload.getId());
					}
					reloadObjectInRelations(entityReloaded);
					reloadedEntities.add(entityReloaded);
				}
				if (MappingType.ENTITY.equals(loadInstruction.getMappingType())) {			
					loadInstruction.getSetMethod().invoke(updatedEntity, reloadedEntities.get(0)); 
				}
				if (MappingType.ENTITY_COLLECTION.equals(loadInstruction.getMappingType())) {
					Collection collectionReloaded = (Collection) loadInstruction.getGetMethod().getReturnType().getDeclaredConstructor().newInstance();
					collectionReloaded.addAll(reloadedEntities);
					loadInstruction.getSetMethod().invoke(updatedEntity, collectionReloaded);
				}
				if (!MappingType.ENTITY_MAP.equals(loadInstruction.getMappingType())) {
					continue;
				}
				Map mapReloaded = (Map) loadInstruction.getGetMethod().getReturnType().getDeclaredConstructor().newInstance();
				for (PrevalenceEntity entityReloaded : reloadedEntities) {
					mapReloaded.put(entityReloaded.getId(), entityReloaded);
				}
				loadInstruction.getSetMethod().invoke(updatedEntity, mapReloaded);
			}
		}
	}

	
	@SuppressWarnings("unchecked")
	private <T extends PrevalenceEntity> List<T> getEntityRelationed(LoadInstruction loadInstruction, T updatedEntity) {
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

	private <T extends PrevalenceEntity> T getOnlyEqualEntity(T updatedEntity, T entityToReload) {
		if (entityToReload == null) {
			return null;
		}
		if (updatedEntity.getClass().isAssignableFrom(entityToReload.getClass()) && (updatedEntity.getId().equals(entityToReload.getId()))) {
			return entityToReload; 
		}
		return null;
	}
	
}