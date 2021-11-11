package br.org.pr.jsonprevayler.test;

import br.org.pr.jsonprevayler.infrastrutuctre.configuration.PojoPrevalenceConfigurator;
import br.org.pr.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.org.pr.jsonprevayler.pojojsonrepository.core.InitializationMemoryCoreType;
import br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;
import br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory.SingleThreadSearchProcessorFactory;
import br.org.pr.jsonprevayler.util.RecordPathUtil;

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