package br.tec.jsonprevayler.infrastrutuctre.configuration;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public interface PrevalenceConfigurator {

	public SearchProcessorFactory getSearchProcessorFactory() throws InternalPrevalenceException;
	public String getPrevalencePath();
	public String getSystemName();
	public Integer getNumberOfFilesPerDiretory();
	public boolean isStoreOperationsDetails();
	
}
