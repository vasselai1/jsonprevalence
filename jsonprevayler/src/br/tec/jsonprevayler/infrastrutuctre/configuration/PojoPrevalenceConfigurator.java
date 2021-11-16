package br.tec.jsonprevayler.infrastrutuctre.configuration;

import br.tec.jsonprevayler.pojojsonrepository.core.InitializationMemoryCoreType;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public class PojoPrevalenceConfigurator implements PrevalenceConfigurator {
	
	private SearchProcessorFactory searchProcessorFactory;
	private String prevalencePath;
	private String systemName;
	private InitializationMemoryCoreType initializationMemoryCoreType;

	public PojoPrevalenceConfigurator(SearchProcessorFactory searchProcessorFactory, String prevalencePath, String systemName, InitializationMemoryCoreType initializationMemoryCoreType) {
		this.searchProcessorFactory = searchProcessorFactory;
		this.prevalencePath = prevalencePath;
		this.systemName = systemName;
		this.initializationMemoryCoreType = initializationMemoryCoreType;
	}
	public SearchProcessorFactory getSearchProcessorFactory() {
		return searchProcessorFactory;
	}
	public void setSearchProcessorFactory(SearchProcessorFactory searchProcessorFactory) {
		this.searchProcessorFactory = searchProcessorFactory;
	}
	public String getPrevalencePath() {
		return prevalencePath;
	}
	public void setPrevalencePath(String prevalencePath) {
		this.prevalencePath = prevalencePath;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public InitializationMemoryCoreType getInitializationMemoryCoreType() {
		return initializationMemoryCoreType;
	}
	public void setInitializationMemoryCoreType(InitializationMemoryCoreType initializationMemoryCoreType) {
		this.initializationMemoryCoreType = initializationMemoryCoreType;
	}
	
}
