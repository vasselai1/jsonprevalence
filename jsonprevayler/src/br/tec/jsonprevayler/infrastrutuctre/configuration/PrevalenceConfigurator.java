package br.tec.jsonprevayler.infrastrutuctre.configuration;

import java.lang.reflect.InvocationTargetException;

import br.tec.jsonprevayler.pojojsonrepository.core.InitializationMemoryCoreType;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public interface PrevalenceConfigurator {

	public SearchProcessorFactory getSearchProcessorFactory() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException;
	public String getPrevalencePath();
	public String getSystemName();
	public InitializationMemoryCoreType getInitializationMemoryCoreType();	
	
}
