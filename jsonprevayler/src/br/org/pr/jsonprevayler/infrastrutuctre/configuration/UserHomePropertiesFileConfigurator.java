package br.org.pr.jsonprevayler.infrastrutuctre.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import br.org.pr.jsonprevayler.pojojsonrepository.core.InitializationMemoryCoreType;
import br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;
import br.org.pr.jsonprevayler.util.RecordPathUtil;

public class UserHomePropertiesFileConfigurator implements PrevalenceConfigurator {

	private static PropertiesPrevalenceConfigurator propertiesConfigurator;
	
	public static void initialize() throws FileNotFoundException, IOException {
		if (propertiesConfigurator != null) {
			return;
		}
		File propertiesFile = new File(RecordPathUtil.getPath(), "jsonPrevalence.properties") ;
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
		propertiesConfigurator = new PropertiesPrevalenceConfigurator(properties);
	}
	
	@Override
	public <T extends SearchProcessorFactory> T getSearchProcessorFactory() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
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
	public InitializationMemoryCoreType getInitializationMemoryCoreType() {
		return propertiesConfigurator.getInitializationMemoryCoreType();
	}

}
