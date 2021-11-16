package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.tec.jsonprevayler.entity.VersionedEntity;
import br.tec.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;

public abstract class CustomOperation {

	private SequenceProvider sequenceProvider;
	private MemoryCore memoryCore;
	private FileCore fileCore;
	private List<ComandOperationInterface> executedOperations = new ArrayList<ComandOperationInterface>();
	
	void initialize(SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		this.sequenceProvider = sequenceUtil;
		this.memoryCore = memoryCore;
		this.fileCore = fileCore;
	}
	
	@SuppressWarnings("rawtypes")
	public void execute(ComandOperationInterface operation) throws ValidationPrevalenceException, NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, Exception {
		if (operation instanceof CascadeOperation) {
			throw new ValidationPrevalenceException("Invalid Operation!");
		}
		if (!(operation instanceof CommonsOperations)) {
			throw new ValidationPrevalenceException("Invalid Operation!");
		}

		if (!(operation.getEntity() instanceof VersionedEntity)) {
			throw new ValidationPrevalenceException("Entity isn't Versioned!");
		}
		((CommonsOperations) operation).setCore(memoryCore, fileCore, sequenceProvider);		
		operation.execute();
		executedOperations.add(operation);
	}
	
	public void undo() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, Exception {
		if (executedOperations.isEmpty()) {
			return;
		} 
		Collections.reverse(executedOperations);
		for (ComandOperationInterface operationLoop : executedOperations) {
			operationLoop.undo();
		}		
	}
	
}