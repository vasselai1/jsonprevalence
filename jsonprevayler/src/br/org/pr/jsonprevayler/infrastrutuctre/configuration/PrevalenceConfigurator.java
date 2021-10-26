package br.org.pr.jsonprevayler.infrastrutuctre.configuration;

import java.lang.reflect.InvocationTargetException;

import br.org.pr.jsonprevayler.pojojsonrepository.core.InitializationMemoryCoreType;
import br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

public interface PrevalenceConfigurator {

	public <T extends SearchProcessorFactory> T getSearchProcessorFactory() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException;
	public String getPrevalencePath();
	public String getSystemName();
	public InitializationMemoryCoreType getInitializationMemoryCoreType();	
	
}
