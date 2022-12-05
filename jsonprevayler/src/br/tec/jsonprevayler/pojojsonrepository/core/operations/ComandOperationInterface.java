package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;

public interface ComandOperationInterface {

	public void execute() throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException;
	public void undo() throws InternalPrevalenceException, ValidationPrevalenceException;
	public <T extends PrevalenceEntity> T getEntity();
	
}
