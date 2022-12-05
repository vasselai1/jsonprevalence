package br.tec.jsonprevayler.pojojsonrepository.core;

import java.util.Collection;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;

public interface MemorySearchEngineInterface {

	public <T extends PrevalenceEntity> T getPojo(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException;
	
	public <T extends PrevalenceEntity> Integer count(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException;
	
	public <T extends PrevalenceEntity> Collection<Long> getKeys(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException;
	
}
