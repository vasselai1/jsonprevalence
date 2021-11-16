package br.tec.jsonprevayler.infrastrutuctre.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import br.tec.jsonprevayler.pojojsonrepository.core.InitializationMemoryCoreType;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public class PropertiesPrevalenceConfigurator implements PrevalenceConfigurator {

	private Properties properties;
	
	public PropertiesPrevalenceConfigurator(Properties properties) {	
		this.properties = properties;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public SearchProcessorFactory getSearchProcessorFactory() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class<? extends SearchProcessorFactory> classe = (Class<? extends SearchProcessorFactory>) Class.forName(properties.getProperty("searchProcessorFactory"));
		return classe.getDeclaredConstructor().newInstance();
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
	public InitializationMemoryCoreType getInitializationMemoryCoreType() {
		return InitializationMemoryCoreType.valueOf(properties.getProperty("initializationMemoryCoreType"));
	}
}
