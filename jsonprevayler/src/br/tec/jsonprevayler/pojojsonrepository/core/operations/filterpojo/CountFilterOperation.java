package br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo;

import java.util.ArrayList;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;

public class CountFilterOperation <T extends PrevalenceEntity> extends FilterOperation<T> {
	
	public CountFilterOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore, SearchProcessor searchProcessor) {
		super(prevalenceConfigurator, sequenceUtil, memoryCore, fileCore, searchProcessor);
	}

	public CountFilterOperation<T> set(Class<T> classe, PrevalenceFilter<T> filter) {
		super.classe = classe;
		super.filter = filter;
		return this;
	}
	
	public Integer execute() throws InternalPrevalenceException, ValidationPrevalenceException {
		List<T> retorno = new ArrayList<T>();
		searchProcessor.setOnlyCount(true);
		searchProcessor.setMemorySearchEngine(memoryCore);
		searchProcessor.process(classe, filter, retorno);
		totalResults = searchProcessor.getTotalFounded();
		return totalResults;
	}

	@Override
	public String getOperationName() {
		return "CountFilterOperation_" + classe + "_" + filter.getClass().getSimpleName();
	}
	
}