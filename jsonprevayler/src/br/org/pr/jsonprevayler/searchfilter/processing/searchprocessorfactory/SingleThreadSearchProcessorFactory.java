package br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory;

import br.org.pr.jsonprevayler.searchfilter.processing.SearchProcessor;
import br.org.pr.jsonprevayler.searchfilter.processing.SingleThreadSearchProcessor;

public class SingleThreadSearchProcessorFactory implements SearchProcessorFactory {

	@Override
	public SearchProcessor createNewSearchProcessor() {
		return new SingleThreadSearchProcessor();
	}

}
