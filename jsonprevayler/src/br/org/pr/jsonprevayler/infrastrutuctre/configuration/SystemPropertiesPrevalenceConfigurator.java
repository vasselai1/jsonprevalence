package br.org.pr.jsonprevayler.infrastrutuctre.configuration;

import java.lang.reflect.InvocationTargetException;

import br.org.pr.jsonprevayler.pojojsonrepository.core.InitializationMemoryCoreType;
import br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public class SystemPropertiesPrevalenceConfigurator implements PrevalenceConfigurator {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends SearchProcessorFactory> T getSearchProcessorFactory() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class<T> classe = (Class<T>) Class.forName(System.getProperty("searchProcessorFactory"));
		return classe.getDeclaredConstructor().newInstance();
	}

	@Override
	public String getPrevalencePath() {
		return System.getProperty("prevalencePath");
	}

	@Override
	public String getSystemName() {
		return System.getProperty("systemName");
	}

	@Override
	public InitializationMemoryCoreType getInitializationMemoryCoreType() {
		return InitializationMemoryCoreType.valueOf(System.getProperty("initializationMemoryCoreType"));
	}

}