package br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;

public class CountTotalOperation <T extends PrevalenceEntity> extends FilterOperation<T> {
	
	public CountTotalOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore, SearchProcessor searchProcessor) {
		super(prevalenceConfigurator, sequenceUtil, memoryCore, fileCore, searchProcessor);
	}

	public CountTotalOperation<T> set(Class<T> classe) {
		super.classe = classe;
		return this;
	}
	
	public Integer execute() throws InternalPrevalenceException, ValidationPrevalenceException {
		totalResults = memoryCore.count(classe);
		return totalResults;
	}

	@Override
	public String getOperationName() {
		return "CountTotalOperation_" + classe;
	}
	
}