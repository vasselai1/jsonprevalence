package br.tec.jsonprevayler.infrastrutuctre.configuration;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public interface PrevalenceConfigurator {

	SearchProcessorFactory getSearchProcessorFactory() throws InternalPrevalenceException;
	String getPrevalencePath();
	String getSystemName();
	Integer getNumberOfFilesPerDiretory();
	boolean isStoreOperationsDetails();
	DateProvider getDateProvider();
	
}
