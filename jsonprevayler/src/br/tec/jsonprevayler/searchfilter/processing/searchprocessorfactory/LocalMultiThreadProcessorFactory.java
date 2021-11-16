package br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory;

import br.tec.jsonprevayler.searchfilter.processing.MultiThreadSearchProcessor;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;

public class LocalMultiThreadProcessorFactory implements SearchProcessorFactory {

	@Override
	public SearchProcessor createNewSearchProcessor() {
		return new MultiThreadSearchProcessor();
	}

}
