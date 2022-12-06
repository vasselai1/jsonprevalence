package br.tec.jsonprevayler.infrastrutuctre.configuration;

import java.util.Properties;
import java.util.logging.Logger;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.pojojsonrepository.core.util.CurrentSytemDateProvider;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;
import br.tec.jsonprevayler.util.LoggerUtil;

public class PropertiesPrevalenceConfigurator implements PrevalenceConfigurator {

	public static final String PROPERTY_CLASS_NAME_SEARCH_PROCESSOR_FACTORY = "searchProcessorFactory"; 
	private static final DateProvider DATE_PROVIDER = new CurrentSytemDateProvider();
	
	private Properties properties;
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	public PropertiesPrevalenceConfigurator(Properties properties) {	
		this.properties = properties;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public SearchProcessorFactory getSearchProcessorFactory() throws InternalPrevalenceException {
		try {
			Class<? extends SearchProcessorFactory> classe = (Class<? extends SearchProcessorFactory>) Class.forName(properties.getProperty(PROPERTY_CLASS_NAME_SEARCH_PROCESSOR_FACTORY));
			return classe.getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			throw LoggerUtil.error(logger, ex, "Erro creating a new SearchProcessorFactory class %1$s", PROPERTY_CLASS_NAME_SEARCH_PROCESSOR_FACTORY);
		}
	}

	@Override
	public String getPrevalencePath() {
		return properties.getProperty("prevalencePath");
	}

	@Override
	public String getSystemName() {
		return properties.getProperty("systemName");
	}

	@Override
	public Integer getNumberOfFilesPerDiretory() {
		return Integer.parseInt(properties.getProperty("numberOfFilesPerDiretory"));
	}

	@Override
	public boolean isStoreOperationsDetails() {
		return Boolean.parseBoolean(properties.getProperty("storeOperationsDetails"));
	}

	@Override
	public DateProvider getDateProvider() {
		return DATE_PROVIDER;
	}
}
