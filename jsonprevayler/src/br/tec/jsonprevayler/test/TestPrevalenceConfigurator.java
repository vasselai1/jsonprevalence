package br.tec.jsonprevayler.test;

import br.tec.jsonprevayler.infrastrutuctre.configuration.PojoPrevalenceConfigurator;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.InitializationMemoryCoreType;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SingleThreadSearchProcessorFactory;
import br.tec.jsonprevayler.util.RecordPathUtil;

public class TestPrevalenceConfigurator {

	private static final String prevalencePath = RecordPathUtil.getUserHomePath();
	private static final String systemName = "PREVALENCE_TEST";
	
	public static PrevalenceConfigurator getConfigurator() {
		return getConfigurator(new SingleThreadSearchProcessorFactory());
	}
	
	public static PrevalenceConfigurator getConfigurator(SearchProcessorFactory searchProcessorFactory) {
		return new PojoPrevalenceConfigurator(searchProcessorFactory, prevalencePath, systemName, InitializationMemoryCoreType.POJO_AND_ENUM);
	}
	
}