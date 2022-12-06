package br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;

public class ListTotalJsonOperation <T extends PrevalenceEntity> extends FilterOperation<T> {
	
	public ListTotalJsonOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore, SearchProcessor searchProcessor) {
		super(prevalenceConfigurator, sequenceUtil, memoryCore, fileCore, searchProcessor);
	}

	public ListTotalJsonOperation<T> set(Class<T> classe) {
		super.classe = classe;
		return this;
	}
	
	public String execute() throws InternalPrevalenceException, ValidationPrevalenceException {		
		totalResults = memoryCore.count(classe);
		return memoryCore.listJson(classe);
	}

	@Override
	public String getOperationName() {
		return "ListTotalJsonOperation_" + classe;
	}
	
}