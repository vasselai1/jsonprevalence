package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.entity.TotalChangesPrevalenceSystem;
import br.tec.jsonprevayler.entity.VersionedEntity;
import br.tec.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.EntityTokenKey;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.LockPrevalenceEntityTokenFactory;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryOperationType;
import br.tec.jsonprevayler.util.LoggerUtil;

public class DeleteOperation <T extends PrevalenceEntity> extends CommonsOperations<T> implements ComandOperationInterface {

	private T entity;
	private Class<T> classeInternal;
	private OperationState state = OperationState.INITIALIZED;
	
	public DeleteOperation(PrevalenceConfigurator prevalenceConfigurator, MemoryCore memoryCore, FileCore fileCore, SequenceProvider sequenceProvider) {
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceProvider);
	}

	public DeleteOperation<T> set(T entity) {
		this.entity = entity;
		return this;
	}
	
	@Override
	public void execute() throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		if (entity == null) {
			throw new ValidationPrevalenceException("Entity is null!");
		}
		Long id = entity.getId();
		if (id == null) {
			throw new ValidationPrevalenceException("Id is null!");
		}
		classeInternal = getClassRepository(entity.getClass());		
		entity = memoryCore.getPojo(classeInternal, id);
		initState(classeInternal, entity);
		if (entity == null) {
			throw new ValidationPrevalenceException("Entity not found!");
		}		
		EntityTokenKey entityToken = LockPrevalenceEntityTokenFactory.get(entity, dateProvider);
		synchronized (entityToken) {
			entityToken.setUse("Delete");
			if (entity instanceof VersionedEntity) {
				VersionedEntity newVersionedEntity = (VersionedEntity) entity;
				VersionedEntity oldVersionedEntity = (VersionedEntity) memoryCore.getPojo(classeInternal, entity.getId());
				if (newVersionedEntity.getVersion() != oldVersionedEntity.getVersion()) {
					throw new DeprecatedPrevalenceEntityVersionException(classeInternal.getCanonicalName(), newVersionedEntity.getVersion(), oldVersionedEntity.getVersion());
				}				
			}
			updateState(OperationState.VALIDATED);
			try {
				fileCore.deleteRegister(classeInternal, id);
				updateState(OperationState.ENTITY_WRITED);
				sequenceProvider.get(TotalChangesPrevalenceSystem.class);
				updateState(OperationState.PREVALENCE_VERSION_UPDATED);
				memoryCore.updateMemory(classeInternal, MemoryOperationType.DELETE, entity);
				updateState(OperationState.MEMORY_UPDATED);
				updateState(OperationState.FINALIZED);
			} catch (Exception e) {
				undo();
				updateState(OperationState.CANCELED, e.getMessage());
				throw LoggerUtil.error(logger, e, "Error in delete entity = %1$s, id = %2$d", classeInternal, id);
			} finally {
				entityToken.setEnd();
			}
		}
		
	}

	@Override
	public void undo() throws InternalPrevalenceException, ValidationPrevalenceException {
		switch (state) {
			case MEMORY_UPDATED: {
				memoryCore.updateMemory(classeInternal, MemoryOperationType.SAVE, entity);
				updateState(OperationState.UNDO_SAVE_MEMORY);
			}
			case ENTITY_WRITED: {
				fileCore.writeRegister(classeInternal, entity);
				updateState(OperationState.UNDO_SAVE_REGISTER);
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

	@Override
	public String getOperationName() {		 
		return "DeleteOperation_" + entity.getClass() + "_" + entity.getId();
	}

}