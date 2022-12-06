package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;

public class DeleteHistoryAndDetailsOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {
	
	private Class<T> classe;
	private Class<T> classeInternal;
	private Long id;
	
	public DeleteHistoryAndDetailsOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);		
	}
	
	public DeleteHistoryAndDetailsOperation<T> set(Class<T> classe, Long id) {
		this.classe = classe;
		this.id = id;
		return this;
	}
	
	public void execute() throws ValidationPrevalenceException, InternalPrevalenceException {
		classeInternal = getClassRepository(classe);
		if (memoryCore.getPojo(classeInternal, id) != null) {
			throw new ValidationPrevalenceException("Please delete entity " + classe.getSimpleName() + " id = " + id + " before!");
		}
		
	}

	@Override
	public String getOperationName() {
		return "DeleteHistoryAndDetailsOperation_" + classe.getSimpleName() + "_" + id;
	}
	
}