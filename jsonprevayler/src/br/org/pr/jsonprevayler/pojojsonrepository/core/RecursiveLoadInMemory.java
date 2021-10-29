package br.org.pr.jsonprevayler.pojojsonrepository.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
				T entityToReplace = getEntityRelationed(loadInstruction, updatedEntity);
				if (entityToReplace == null) {
					continue;
				}
				
			}
		}		
	}
	
	private static <T extends PrevalenceEntity> T getEntityRelationed(LoadInstruction loadInstruction, T updatedEntity) {
		if (MappingType.ENTITY.equals(loadInstruction.getMappingType())) {
			T entityToReload = (T) loadInstruction.getOriginalValue();
			return getOnlyEqualEntity(updatedEntity, entityToReload);
		}
		
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
