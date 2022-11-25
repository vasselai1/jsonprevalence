package br.tec.jsonprevayler.infrastrutuctre.configuration;

import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public class PojoPrevalenceConfigurator implements PrevalenceConfigurator {
	
	private SearchProcessorFactory searchProcessorFactory;
	private String prevalencePath;
	private String systemName;
	private Integer numberOfFilesPerDiretory;
	private boolean storeOperationsDetails = true;

	public PojoPrevalenceConfigurator(SearchProcessorFactory searchProcessorFactory, 
									  String prevalencePath, 
									  String systemName,
									  Integer numberOfFilesPerDiretory,
									  boolean storeOperationsDetails) {
		this.searchProcessorFactory = searchProcessorFactory;
		this.prevalencePath = prevalencePath;
		this.systemName = systemName;
		this.numberOfFilesPerDiretory = numberOfFilesPerDiretory;
		this.storeOperationsDetails = storeOperationsDetails;
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
	@Override
	public Integer getNumberOfFilesPerDiretory() {
		return numberOfFilesPerDiretory;
	}
	@Override
	public boolean isStoreOperationsDetails() {
		return storeOperationsDetails;
	}
	
}