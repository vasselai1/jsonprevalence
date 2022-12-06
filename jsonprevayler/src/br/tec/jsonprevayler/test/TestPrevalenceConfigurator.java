package br.tec.jsonprevayler.test;

import br.tec.jsonprevayler.infrastrutuctre.configuration.PojoPrevalenceConfigurator;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.util.CurrentSytemDateProvider;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SingleThreadSearchProcessorFactory;
import br.tec.jsonprevayler.util.RecordPathUtil;

public class TestPrevalenceConfigurator {

	private static final String prevalencePath = RecordPathUtil.getUserHomePath();
	private static final String systemName = "PREVALENCE_TEST";
	private static final DateProvider dateProvider = new CurrentSytemDateProvider();
	
	public static PrevalenceConfigurator getConfigurator() {
		return getConfigurator(new SingleThreadSearchProcessorFactory());
	}
	
	public static PrevalenceConfigurator getConfigurator(SearchProcessorFactory searchProcessorFactory) {
		return new PojoPrevalenceConfigurator(searchProcessorFactory, prevalencePath, systemName, 10, true, dateProvider);
	}
	
	public static PrevalenceConfigurator getConfigurator(SearchProcessorFactory searchProcessorFactory, DateProvider dateProvider) {
		return new PojoPrevalenceConfigurator(searchProcessorFactory, prevalencePath, systemName, 10, true, dateProvider);
	}	
	
}