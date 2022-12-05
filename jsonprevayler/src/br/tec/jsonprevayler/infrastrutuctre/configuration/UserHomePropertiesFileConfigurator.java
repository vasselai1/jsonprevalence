package br.tec.jsonprevayler.infrastrutuctre.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;
import br.tec.jsonprevayler.util.LoggerUtil;
import br.tec.jsonprevayler.util.RecordPathUtil;

public class UserHomePropertiesFileConfigurator implements PrevalenceConfigurator {

	private static PropertiesPrevalenceConfigurator propertiesConfigurator;
	private static final String FILE_NAME_CONFIGURATION = "jsonPrevalence.properties";
	
	public static void initialize() throws InternalPrevalenceException {
		if (propertiesConfigurator != null) {
			return;
		}
		File propertiesFile = new File(RecordPathUtil.getUserHomePath(), FILE_NAME_CONFIGURATION) ;
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesFile));
		} catch (Exception ex) {
			Logger logger = Logger.getLogger(UserHomePropertiesFileConfigurator.class.getName());
			throw LoggerUtil.error(logger, ex, "Error while reading configuration file %1$s", FILE_NAME_CONFIGURATION);
		}
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

}