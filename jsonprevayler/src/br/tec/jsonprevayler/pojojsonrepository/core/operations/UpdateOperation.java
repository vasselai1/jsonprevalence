package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

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
import br.tec.jsonprevayler.pojojsonrepository.core.OperationType;

public class UpdateOperation <T extends PrevalenceEntity> extends CommonsOperations<T> implements ComandOperationInterface {

	private Class<T> classeInternal;
	private T entity;
	private T oldEntity;	
	private OperationState state = OperationState.INITIALIZED;
	
	public UpdateOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);
	}
	
	public UpdateOperation<T> set(T entity) {
		this.entity = entity;
		return this;
	}

	@Override
	public void execute() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, NoSuchMethodException, InstantiationException, InterruptedException, Exception {
		classeInternal = getClassRepository(entity.getClass());
		initState(classeInternal, entity);
		if (entity == null) {
			throw new ValidationPrevalenceException("Entity is null!");
		}
		if (entity.getId() == null) {
			throw new ValidationPrevalenceException("Id is not seted!");
		}
		EntityTokenKey entityToken = LockPrevalenceEntityTokenFactory.get(entity);
		updateState(OperationState.INIT_LOCK);
		synchronized (entityToken) {
			entityToken.setUse("Update");
			if (entity instanceof VersionedEntity) {
				VersionedEntity newVersionedEntity = (VersionedEntity) entity;
				VersionedEntity oldVersionedEntity = (VersionedEntity) memoryCore.getPojo(classeInternal, entity.getId());
				if (newVersionedEntity.getVersion() != oldVersionedEntity.getVersion()) {
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
				memoryCore.updateMemory(classeInternal, OperationType.UPDATE, entity);
				updateState(OperationState.MEMORY_UPDATED);
				updateState(OperationState.FINALIZED);
			} catch (Exception e) {
				undo();
				updateState(OperationState.CANCELED, e.getMessage());
				throw e;
			} finally {
				entityToken.setEnd();
				updateState(OperationState.LOCK_FINALIZED);
			}
		}		
	}

	@Override
	public void undo() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, NoSuchMethodException, InstantiationException, InterruptedException, Exception {
		switch (state) {
			case MEMORY_UPDATED: {
				memoryCore.updateMemory(classeInternal, OperationType.UPDATE, oldEntity);
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
	
}