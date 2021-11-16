package br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory;

import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;
import br.tec.jsonprevayler.searchfilter.processing.SingleThreadSearchProcessor;

public class SingleThreadSearchProcessorFactory implements SearchProcessorFactory {

	@Override
	public SearchProcessor createNewSearchProcessor() {
		return new SingleThreadSearchProcessor();
	}

}
