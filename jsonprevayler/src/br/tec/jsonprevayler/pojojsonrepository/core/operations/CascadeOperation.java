package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.normalization.JsonSerializationInstructions;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;

public class CascadeOperation <T extends PrevalenceEntity> implements ComandOperationInterface {

	private final SequenceProvider sequenceUtil;
	private final MemoryCore memoryCore;
	private final FileCore fileCore;
	private JsonSerializationInstructions instructions;
	private final List<ComandOperationInterface> executedsOperations = new ArrayList<ComandOperationInterface>();
	
	public CascadeOperation(SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		this.fileCore = fileCore;
		this.memoryCore = memoryCore;
		this.sequenceUtil = sequenceUtil;
	}

	public CascadeOperation<T> set(JsonSerializationInstructions instructions) {
		this.instructions = instructions;
		return this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void execute() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, Exception {
		List<T> values = (List<T>) instructions.getPrevalentObjects();
		try {
			for (T entityLoop : values) {
				if (entityLoop.getId() == null) {
					SaveOperation<T> saveOperation = new SaveOperation<T>(sequenceUtil, memoryCore, fileCore);
					saveOperation.set(entityLoop, true).execute();
					executedsOperations.add(saveOperation);
				} else {
					UpdateOperation<T> updateOperation = new UpdateOperation<T>(sequenceUtil, memoryCore, fileCore);
					updateOperation.set(entityLoop, true);
					executedsOperations.add(updateOperation);
				}
			}
		} catch (Exception e) {
			undo();
			throw e;
		}
		
	}

	@Override
	public void undo() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, Exception {
		if (executedsOperations.isEmpty()) {
			return;
		} 
		Collections.reverse(executedsOperations);
		for (ComandOperationInterface operationLoop : executedsOperations) {
			operationLoop.undo();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T getEntity() {
		return null;
	}

}
