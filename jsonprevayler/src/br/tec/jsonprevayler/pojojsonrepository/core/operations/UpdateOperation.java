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

public class UpdateOperation <T extends PrevalenceEntity> extends CommonsOperations<T> implements ComandOperationInterface {

	private Class<T> classeInternal;
	private T entity;
	private T oldEntity;	
	private OperationState state = OperationState.INITIALIZED;
	private boolean isOverwrite = false;
	
	public UpdateOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);
	}
	
	public UpdateOperation<T> set(T entity) {
		this.entity = entity;
		return this;
	}

	@Override
	public void execute() throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		classeInternal = getClassRepository(entity.getClass());
		initState();
		writeOperationDetail(ENTITY, getJson(entity));
		writeOperationDetail(INTERNAL_CLASS, classeInternal.getCanonicalName());
		if (entity == null) {
			throw new ValidationPrevalenceException("Entity is null!");
		}
		if (entity.getId() == null) {
			throw new ValidationPrevalenceException("Id is not seted!");
		}
		EntityTokenKey entityToken = LockPrevalenceEntityTokenFactory.get(entity, dateProvider);
		updateState(OperationState.INIT_LOCK);
		synchronized (entityToken) {
			entityToken.setUse("Update");
			if (entity instanceof VersionedEntity) {
				VersionedEntity newVersionedEntity = (VersionedEntity) entity;
				VersionedEntity oldVersionedEntity = (VersionedEntity) memoryCore.getPojo(classeInternal, entity.getId());
				if ((newVersionedEntity.getVersion() != oldVersionedEntity.getVersion()) && (!isOverwrite)) {
					throw new DeprecatedPrevalenceEntityVersionException(classeInternal.getCanonicalName(), newVersionedEntity.getVersion(), oldVersionedEntity.getVersion());
				}
				newVersionedEntity.setVersion(oldVersionedEntity.getVersion() + 1);
			}
			oldEntity = memoryCore.getPojo(classeInternal, entity.getId());
			updateState(OperationState.VALIDATED);
			try {
				fileCore.writeRegister(classeInternal, entity);
				updateState(OperationState.ENTITY_WRITED);
				sequenceProvider.get(TotalChangesPrevalenceSystem.class);
				updateState(OperationState.PREVALENCE_VERSION_UPDATED);
				memoryCore.updateMemory(classeInternal, MemoryOperationType.UPDATE, entity);
				updateState(OperationState.MEMORY_UPDATED);
				if (isOverwrite) {
					updateState(OperationState.OVERWRITED);
				}
				updateState(OperationState.FINALIZED);
			} catch (Exception e) {
				undo();
				updateState(OperationState.CANCELED);
				writeOperationDetail(ERROR, e.getMessage());
				throw LoggerUtil.error(logger, e, "Error in update entity = %1$s, id = %2$d", classeInternal, entity.getId());
			} finally {
				entityToken.setEnd();
				updateState(OperationState.LOCK_FINALIZED);
			}
		}		
	}

	@Override
	public void undo() throws InternalPrevalenceException, ValidationPrevalenceException {
		switch (state) {
			case MEMORY_UPDATED: {
				memoryCore.updateMemory(classeInternal, MemoryOperationType.UPDATE, oldEntity);
				updateState(OperationState.UNDO_DELETE_MEMORY);
			}
			case ENTITY_WRITED: {
				fileCore.writeRegister(classeInternal, oldEntity);
				updateState(OperationState.UNDO_DELETE_REGISTER);
			}
			case VALIDATED: {
				if (entity instanceof VersionedEntity) {
					VersionedEntity versionedEntity = (VersionedEntity) entity;
					versionedEntity.setVersion(versionedEntity.getVersion() - 1);
					updateState(OperationState.UNDO_VERSION);
				}
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
	
	public void setIsOverwrite(boolean isOverwrite) {
		this.isOverwrite = isOverwrite;
	}

	@Override
	public String getOperationName() {
		return "UpdateOperation_" + classeInternal.getCanonicalName() + "_" + entity.getId();
	}
	
}