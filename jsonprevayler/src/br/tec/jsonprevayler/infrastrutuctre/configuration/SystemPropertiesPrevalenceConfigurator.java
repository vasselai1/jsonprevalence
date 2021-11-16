package br.tec.jsonprevayler.infrastrutuctre.configuration;

import java.lang.reflect.InvocationTargetException;

import br.tec.jsonprevayler.pojojsonrepository.core.InitializationMemoryCoreType;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public class SystemPropertiesPrevalenceConfigurator implements PrevalenceConfigurator {

	@Override
	@SuppressWarnings("unchecked")
	public SearchProcessorFactory getSearchProcessorFactory() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class<? extends SearchProcessorFactory> classe = (Class<? extends SearchProcessorFactory>) Class.forName(System.getProperty("searchProcessorFactory"));
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