package br.org.pr.jsonprevayler.pojojsonrepository.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.entity.TotalChangesPrevalenceSystem;
import br.org.pr.jsonprevayler.entity.VersionedEntity;
import br.org.pr.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.org.pr.jsonprevayler.exceptions.InternalPrevalenceException;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.IntegrityInspector;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.JsonSerializationInstructions;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.PrevalentAtributesValuesIdentificator;
import br.org.pr.jsonprevayler.pojojsonrepository.core.FileCore;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.org.pr.jsonprevayler.pojojsonrepository.core.OperationType;

public class DeleteOperation <T extends PrevalenceEntity> extends CommonsOperations<T> implements ComandOperationInterface {

	private T entity;
	private Class<T> classeInternal;
	private OperationState state = OperationState.INITIALIZED;
	
	public DeleteOperation(MemoryCore memoryCore, FileCore fileCore, SequenceProvider sequenceProvider) {
		setCore(memoryCore, fileCore, sequenceProvider);
	}

	public DeleteOperation<T> set(T entity) {
		this.entity = entity;
		return this;
	}
	
	@Override
	public void execute() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException {
		if (entity == null) {
			throw new ValidationPrevalenceException("Entity is null!");
		}
		Long id = entity.getId();
		if (id == null) {
			throw new ValidationPrevalenceException("Id is null!");
		}
		classeInternal = getClassRepository(entity.getClass());
		entity = memoryCore.getPojo(classeInternal, id);
		if (entity == null) {
			throw new ValidationPrevalenceException("Entity not found!");
		}
		new IntegrityInspector(fileCore.getPrevalencePath(), fileCore.getSystemName()).validateExcluision(entity);
		
		synchronized (classeInternal) {
			if (entity instanceof VersionedEntity) {
				VersionedEntity newVersionedEntity = (VersionedEntity) entity;
				VersionedEntity oldVersionedEntity = (VersionedEntity) memoryCore.getPojo(classeInternal, entity.getId());
				if (newVersionedEntity.getVersion() != oldVersionedEntity.getVersion()) {
					throw new DeprecatedPrevalenceEntityVersionException(classeInternal.getCanonicalName(), newVersionedEntity.getVersion(), oldVersionedEntity.getVersion());
				}				
			}
			state = OperationState.VALIDATED;
			try {
				fileCore.deleteRegister(classeInternal, id);
				state = OperationState.ENTITY_WRITED;
				sequenceProvider.get(TotalChangesPrevalenceSystem.class);
				state = OperationState.PREVALENCE_VERSION_UPDATED;
				memoryCore.updateMemory(classeInternal, OperationType.DELETE, entity, false);
				state = OperationState.MEMORY_UPDATED;
			} catch (Exception e) {
				undo();
				throw e;
			}
		}
		
	}

	@Override
	public void undo() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException {
		switch (state) {
			case MEMORY_UPDATED: {
				memoryCore.updateMemory(classeInternal, OperationType.SAVE, entity, false);
			}
			case ENTITY_WRITED: {
				JsonSerializationInstructions instructions = PrevalentAtributesValuesIdentificator.getJsonSerializationInstructions(entity);
				fileCore.writeRegister(classeInternal, entity, instructions);
			}
			default: { 
				break;
			}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T getEntity() {
		return entity;
	}

}