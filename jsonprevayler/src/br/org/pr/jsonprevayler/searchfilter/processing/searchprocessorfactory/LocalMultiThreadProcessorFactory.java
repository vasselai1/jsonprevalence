package br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory;

import br.org.pr.jsonprevayler.searchfilter.processing.MultiThreadSearchProcessor;
import br.org.pr.jsonprevayler.searchfilter.processing.SearchProcessor;

public class LocalMultiThreadProcessorFactory implements SearchProcessorFactory {

	@Override
	public SearchProcessor createNewSearchProcessor() {
		return new MultiThreadSearchProcessor();
	}

}
