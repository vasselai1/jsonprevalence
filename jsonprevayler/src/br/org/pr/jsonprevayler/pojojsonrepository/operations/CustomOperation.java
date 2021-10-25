package br.org.pr.jsonprevayler.pojojsonrepository.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import br.org.pr.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.org.pr.jsonprevayler.exceptions.InternalPrevalenceException;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.org.pr.jsonprevayler.pojojsonrepository.core.FileCore;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemoryCore;

public abstract class CustomOperation {

	private SequenceProvider sequenceUtil;
	private MemoryCore memoryCore;
	private FileCore fileCore;
	private List<ComandOperationInterface> executedOperations = new ArrayList<ComandOperationInterface>();
	
	void initialize(SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		this.sequenceUtil = sequenceUtil;
		this.memoryCore = memoryCore;
		this.fileCore = fileCore;
	}
	
	public void execute(ComandOperationInterface operation) throws ValidationPrevalenceException, NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		if (operation instanceof CascadeOperation) {
			throw new ValidationPrevalenceException("Invalid Operation!");
		}
		operation.execute();
	}
	
}
