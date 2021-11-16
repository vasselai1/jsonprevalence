package br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory;

import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;

public interface SearchProcessorFactory {

	public SearchProcessor createNewSearchProcessor();
	
}