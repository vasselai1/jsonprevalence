package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.entity.TotalChangesPrevalenceSystem;
import br.tec.jsonprevayler.entity.VersionedEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryOperationType;
import br.tec.jsonprevayler.util.LoggerUtil;
import br.tec.jsonprevayler.util.ObjectCopyUtil;

public class SaveOperation <T extends PrevalenceEntity> extends CommonsOperations<T> implements ComandOperationInterface {

	private T entity;
	private Class<T> classeInternal;
	
	public SaveOperation() { }
	
	public SaveOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);
	}

	public SaveOperation<T> set(T entity) {
		this.entity = entity;
		return this;
	}
	
	public void execute() throws InternalPrevalenceException, ValidationPrevalenceException {		
		classeInternal = getClassRepository(entity.getClass());
		initState(classeInternal, entity);
		if (classeInternal == null) {
			throw new ValidationPrevalenceException("Classe is null!");
		}
		if (entity == null) {
			throw new ValidationPrevalenceException("Entity is null!");
		}
		if (entity.getId() != null) {
			throw new ValidationPrevalenceException("Id is seted!");
		}		
		Long id = sequenceProvider.get(classeInternal);
		updateState(OperationState.ID_CREATED);
		if (memoryCore.isIdUsed(classeInternal, id)) {
			throw new InternalPrevalenceException("Id " + id + " is repeated! Please see the max value id in entities " + entity.getClass().getCanonicalName() + " and set +1 in sequence file!");
		}
		if (entity instanceof VersionedEntity) {				
			VersionedEntity newVersionedEntity = (VersionedEntity) entity;
			if (newVersionedEntity.getVersion() != 0L) {
				throw new ValidationPrevalenceException("Version is not zero!!!");
			}
			newVersionedEntity.setVersion(1L);
		}
		updateState(OperationState.VALIDATED);
		entity.setId(id);
		try {
			T entitySave = ObjectCopyUtil.copyEntity(entity);
			updateState(OperationState.BINARY_COPY_OK);
			fileCore.writeRegister(classeInternal, entitySave);
			updateState(OperationState.ENTITY_WRITED);
			sequenceProvider.get(TotalChangesPrevalenceSystem.class);
			updateState(OperationState.PREVALENCE_VERSION_UPDATED);
			memoryCore.updateMemory(classeInternal, MemoryOperationType.SAVE, entitySave);
			updateState(OperationState.MEMORY_UPDATED);
			updateState(OperationState.FINALIZED);
		} catch (Exception e) {
			undo();
			updateState(OperationState.CANCELED, e.getMessage());
			throw LoggerUtil.error(logger, e, "Error save entity = %1$s, id = %2$d", classeInternal, id);
		}
	}	
	
	public void undo() throws InternalPrevalenceException, ValidationPrevalenceException {
		switch (getState()) {
			case MEMORY_UPDATED: {
				memoryCore.updateMemory(classeInternal, MemoryOperationType.DELETE, entity);
				updateState(OperationState.UNDO_DELETE_MEMORY);
			}
			case ENTITY_WRITED: {
				fileCore.deleteRegister(classeInternal, entity.getId());
				updateState(OperationState.UNDO_DELETE_REGISTER);
			}
			case VALIDATED: {
				entity.setId(null);
				updateState(OperationState.UNDO_SET_NULL_ID);
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
		return "SaveOperation" + entity.getClass() + "_" + entity.getId();
	}
	
}