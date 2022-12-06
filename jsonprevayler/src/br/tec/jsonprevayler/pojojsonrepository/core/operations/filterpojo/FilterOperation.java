package br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.CommonsOperations;
import br.tec.jsonprevayler.searchfilter.FilterFirst;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;

public abstract class FilterOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {

	protected final MemoryCore memoryCore;
	protected final SearchProcessor searchProcessor;//Precisa ser um por pesquisa!!!
	protected Class<T> classe;
	protected PrevalenceFilter<T> filter;
	protected FilterFirst<T> filterFirst;
	protected Integer totalResults = 0;
	
	public FilterOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore, SearchProcessor searchProcessor) {
		this.memoryCore = memoryCore;
		this.searchProcessor = searchProcessor;
		this.prevalenceConfigurator = prevalenceConfigurator;
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);
	}

	public Integer getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}
	
}