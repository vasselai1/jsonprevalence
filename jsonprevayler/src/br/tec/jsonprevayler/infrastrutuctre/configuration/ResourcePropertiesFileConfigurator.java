package br.tec.jsonprevayler.infrastrutuctre.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.pojojsonrepository.core.util.CurrentSytemDateProvider;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public class ResourcePropertiesFileConfigurator implements PrevalenceConfigurator {

	private static final DateProvider DATE_PROVIDER = new CurrentSytemDateProvider();
	private static PropertiesPrevalenceConfigurator propertiesConfigurator;
	
	public static void initialize() throws FileNotFoundException, IOException {
		if (propertiesConfigurator != null) {
			return;
		}
		Properties properties = new Properties();
		properties.load(ResourcePropertiesFileConfigurator.class.getClassLoader().getResourceAsStream("jsonPrevalence.properties"));
		propertiesConfigurator = new PropertiesPrevalenceConfigurator(properties);
	}
	
	@Override
	public SearchProcessorFactory getSearchProcessorFactory() throws InternalPrevalenceException {
		return propertiesConfigurator.getSearchProcessorFactory();
	}

	@Override
	public String getPrevalencePath() {
		return propertiesConfigurator.getPrevalencePath();
	}

	@Override
	public String getSystemName() {
		return propertiesConfigurator.getSystemName();
	}

	@Override
	public Integer getNumberOfFilesPerDiretory() {
		return propertiesConfigurator.getNumberOfFilesPerDiretory();
	}

	@Override
	public boolean isStoreOperationsDetails() {
		return propertiesConfigurator.isStoreOperationsDetails();
	}

	@Override
	public DateProvider getDateProvider() {
		return DATE_PROVIDER;
	}

}