package br.tec.jsonprevayler.infrastrutuctre.configuration;

import java.util.logging.Logger;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.pojojsonrepository.core.util.CurrentSytemDateProvider;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;
import br.tec.jsonprevayler.util.LoggerUtil;

public class SystemPropertiesPrevalenceConfigurator implements PrevalenceConfigurator {

	private static final DateProvider DATE_PROVIDER = new CurrentSytemDateProvider();
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	@Override
	@SuppressWarnings("unchecked")
	public SearchProcessorFactory getSearchProcessorFactory() throws InternalPrevalenceException {
		String className = PropertiesPrevalenceConfigurator.PROPERTY_CLASS_NAME_SEARCH_PROCESSOR_FACTORY;
		try {
			Class<? extends SearchProcessorFactory> classe = (Class<? extends SearchProcessorFactory>) Class.forName(System.getProperty(className));
			return classe.getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			throw LoggerUtil.error(logger, ex, "Error creating a new SearchProcessorFactory class %1$s", className);
		}
	}

	@Override
	public String getPrevalencePath() {
		return System.getProperty("br.tec.jsonprevayler.prevalencePath");
	}

	@Override
	public String getSystemName() {
		return System.getProperty("br.tec.jsonprevayler.systemName");
	}

	@Override
	public Integer getNumberOfFilesPerDiretory() {
		return Integer.parseInt(System.getProperty("br.tec.jsonprevayler.numberOfFilesPerDiretory"));
	}

	@Override
	public boolean isStoreOperationsDetails() {
		return Boolean.parseBoolean(System.getProperty("br.tec.jsonprevayler.storeOperationsDetails"));
	}

	@Override
	public DateProvider getDateProvider() {
		return DATE_PROVIDER;
	}

}