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
import br.tec.jsonprevayler.infrastrutuctre.normalization.JsonSerializationInstructions;
import br.tec.jsonprevayler.infrastrutuctre.normalization.PrevalentAtributesValuesIdentificator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.pojojsonrepository.core.OperationType;
import br.tec.jsonprevayler.util.ObjectCopyUtil;

public class SaveOperation <T extends PrevalenceEntity> extends CommonsOperations<T> implements ComandOperationInterface {

	private T entity;
	private Class<T> classeInternal;
	private boolean deepSave;
	private boolean isCascadeExecuted = false;
	private CascadeOperation<T> cascadeOperation;
	private OperationState state = OperationState.INITIALIZED;
	
	public SaveOperation() { }
	
	public SaveOperation(SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(sequenceUtil, memoryCore, fileCore);
	}

	public void setCore(SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(memoryCore, fileCore, sequenceUtil);
		cascadeOperation = new CascadeOperation<T>(sequenceUtil, memoryCore, fileCore);
	}

	public SaveOperation<T> set(T entity, boolean deepSave) {
		this.entity = entity;
		this.deepSave = deepSave;
		return this;
	}
	
	public void execute() throws ValidationPrevalenceException, IOException, InternalPrevalenceException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, NoSuchMethodException, InstantiationException, InterruptedException, Exception {		
		classeInternal = getClassRepository(entity.getClass());
		JsonSerializationInstructions instructions = PrevalentAtributesValuesIdentificator.getJsonSerializationInstructions(entity);				
		if (classeInternal == null) {
			throw new ValidationPrevalenceException("Classe is null!");
		}
		if (entity == null) {
			throw new ValidationPrevalenceException("Entity is null!");
		}
		if (entity.getId() != null) {
			throw new ValidationPrevalenceException("Id is seted!");
		}
		if (!deepSave) {
			validateAllRelationsPersisted(instructions);
		}
		Long id = sequenceProvider.get(classeInternal);
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
		entity.setId(id);
		state = OperationState.VALIDATED;
		try {
			if (deepSave) {
				cascadeOperation.set(instructions).execute();
				isCascadeExecuted = true;
				state = OperationState.RELATIONS_SAVED;
			}
			T entitySave = ObjectCopyUtil.copyEntity(entity);
			fileCore.writeRegister(classeInternal, entitySave, instructions);
			state = OperationState.ENTITY_WRITED;
			sequenceProvider.get(TotalChangesPrevalenceSystem.class);
			state = OperationState.PREVALENCE_VERSION_UPDATED;
			memoryCore.updateMemory(classeInternal, OperationType.SAVE, entitySave, deepSave);
			state = OperationState.MEMORY_UPDATED;
		} catch (Exception e) {
			undo();
			throw e;
		}
	}	
	
	public void undo() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchMethodException, InstantiationException, InterruptedException, Exception {
		switch (state) {
			case MEMORY_UPDATED: {
				memoryCore.updateMemory(classeInternal, OperationType.DELETE, entity, deepSave);
			}
			case ENTITY_WRITED: {
				fileCore.deleteRegister(classeInternal, entity.getId());
			}
			case RELATIONS_SAVED: {
				if (isCascadeExecuted) {
					cascadeOperation.undo();
				}
			}
			case VALIDATED: {
				entity.setId(null);
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