package br.tec.jsonprevayler.infrastrutuctre.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public class SystemPropertiesForLinkPropertiesFilePrevalenceConfigurator implements PrevalenceConfigurator {

	private static PropertiesPrevalenceConfigurator propertiesConfigurator;
	
	public static void initialize() throws FileNotFoundException, IOException {
		if (propertiesConfigurator != null) {
			return;
		}
		File propertiesFile = new File(System.getProperty("br.tec.jsonprevayler.prevalencePropertiesFilePath")) ;
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
		propertiesConfigurator = new PropertiesPrevalenceConfigurator(properties);
	}	
	
	@Override
	public SearchProcessorFactory getSearchProcessorFactory() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
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