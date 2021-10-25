package br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory;

import br.org.pr.jsonprevayler.searchfilter.processing.SearchProcessor;

public interface SearchProcessorFactory {

	public SearchProcessor createNewSearchProcessor();
	
}