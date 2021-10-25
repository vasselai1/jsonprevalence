package br.org.pr.jsonprevayler.pojojsonrepository.core;

import java.io.IOException;
import java.util.Collection;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;

public interface MemorySearchEngineInterface {

	public <T extends PrevalenceEntity> T getPojo(Class<T> classe, Long id) throws IOException, ClassNotFoundException, ValidationPrevalenceException;
	
	public <T extends PrevalenceEntity> Integer count(Class<T> classe) throws IOException, ClassNotFoundException, ValidationPrevalenceException;
	
	public <T extends PrevalenceEntity> Collection<Long> getKeys(Class<T> classe) throws IOException, ValidationPrevalenceException;
	
}
