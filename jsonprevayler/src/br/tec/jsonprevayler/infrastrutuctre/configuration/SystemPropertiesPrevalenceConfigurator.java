package br.tec.jsonprevayler.infrastrutuctre.configuration;

import java.lang.reflect.InvocationTargetException;

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

}