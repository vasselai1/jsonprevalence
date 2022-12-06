package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.tec.jsonprevayler.entity.VersionedEntity;
import br.tec.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;

public abstract class CustomOperation {

	private PrevalenceConfigurator prevalenceConfigurator;
	private SequenceProvider sequenceProvider;
	private MemoryCore memoryCore;
	private FileCore fileCore;
	private List<ComandOperationInterface> executedOperations = new ArrayList<ComandOperationInterface>();
	
	public void initialize(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		this.prevalenceConfigurator = prevalenceConfigurator;
		this.sequenceProvider = sequenceUtil;
		this.memoryCore = memoryCore;
		this.fileCore = fileCore;
	}
	
	@SuppressWarnings("rawtypes")
	public void execute(ComandOperationInterface operation) throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		if (!(operation instanceof CommonsOperations)) {
			throw new ValidationPrevalenceException("Invalid Operation!");
		}

		if (!(operation.getEntity() instanceof VersionedEntity)) {
			throw new ValidationPrevalenceException("Entity isn't Versioned!");
		}
		((CommonsOperations) operation).setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceProvider);		
		operation.execute();
		executedOperations.add(operation);
	}
	
	public void undo() throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		if (executedOperations.isEmpty()) {
			return;
		} 
		Collections.reverse(executedOperations);
		for (ComandOperationInterface operationLoop : executedOperations) {
			operationLoop.undo();
		}		
	}
	
}