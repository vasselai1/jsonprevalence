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
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.JsonSerializationInstructions;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.PrevalentAtributesValuesIdentificator;
import br.org.pr.jsonprevayler.pojojsonrepository.core.EntityTokenKey;
import br.org.pr.jsonprevayler.pojojsonrepository.core.FileCore;
import br.org.pr.jsonprevayler.pojojsonrepository.core.LockPrevalenceEntityTokenFactory;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.org.pr.jsonprevayler.pojojsonrepository.core.OperationType;
import br.org.pr.jsonprevayler.util.ObjectCopyUtil;

public class UpdateOperation <T extends PrevalenceEntity> extends CommonsOperations<T> implements ComandOperationInterface {

	private Class<T> classeInternal;
	private T entity;
	private T oldEntity; 
	private boolean updateDeep;
	private boolean isCascadeExecuted = false;	
	private final CascadeOperation<T> cascadeOperation;
	private JsonSerializationInstructions instructions;
	private OperationState state = OperationState.INITIALIZED;
	
	public UpdateOperation(SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(memoryCore, fileCore, sequenceUtil);
		cascadeOperation = new CascadeOperation<T>(sequenceUtil, memoryCore, fileCore);
	}
	
	public UpdateOperation<T> set(T entity, boolean updateDeep) {
		this.entity = entity;
		this.updateDeep = updateDeep;
		return this;
	}

	@Override
	public void execute() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, NoSuchMethodException, InstantiationException, InterruptedException, Exception {
		classeInternal = getClassRepository(entity.getClass());
		instructions = PrevalentAtributesValuesIdentificator.getJsonSerializationInstructions(entity);
		if (entity == null) {
			throw new ValidationPrevalenceException("Entity is null!");
		}
		if (entity.getId() == null) {
			throw new ValidationPrevalenceException("Id is not seted!");
		}
		if (!updateDeep) {
			validateAllRelationsPersisted(instructions);
		}
		EntityTokenKey entityToken = LockPrevalenceEntityTokenFactory.get(entity);
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
			state = OperationState.VALIDATED;
			try {
				if (updateDeep) {
					cascadeOperation.set(instructions).execute();				
					isCascadeExecuted = true;
					state = OperationState.RELATIONS_SAVED;
				}
				T entityUpdate = ObjectCopyUtil.copyEntity(entity);
				fileCore.writeRegister(classeInternal, entityUpdate, instructions);
				state = OperationState.ENTITY_WRITED;
				sequenceProvider.get(TotalChangesPrevalenceSystem.class);
				state = OperationState.PREVALENCE_VERSION_UPDATED;
				memoryCore.updateMemory(classeInternal, OperationType.UPDATE, entityUpdate, updateDeep);
				state = OperationState.MEMORY_UPDATED;
			} catch (Exception e) {
				undo();
				throw e;
			} finally {
				entityToken.setEnd();
			}
		}		
	}

	@Override
	public void undo() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, NoSuchMethodException, InstantiationException, InterruptedException, Exception {
		switch (state) {
			case MEMORY_UPDATED: {
				memoryCore.updateMemory(classeInternal, OperationType.UPDATE, oldEntity, updateDeep);
			}
			case ENTITY_WRITED: {
				fileCore.writeRegister(classeInternal, oldEntity, instructions);
			}
			case RELATIONS_SAVED: {
				if (isCascadeExecuted) {
					cascadeOperation.undo();
				}
			}
			case VALIDATED: {
				if (entity instanceof VersionedEntity) {
					VersionedEntity versionedEntity = (VersionedEntity) entity;
					versionedEntity.setVersion(versionedEntity.getVersion() - 1);
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