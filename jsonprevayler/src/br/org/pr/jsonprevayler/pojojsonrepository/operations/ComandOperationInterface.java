package br.org.pr.jsonprevayler.pojojsonrepository.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.org.pr.jsonprevayler.exceptions.InternalPrevalenceException;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;

public interface ComandOperationInterface {

	public void execute() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, NoSuchMethodException, InstantiationException, InterruptedException;
	public void undo() throws NoSuchAlgorithmException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationPrevalenceException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, NoSuchMethodException, InstantiationException, InterruptedException;
	public <T extends PrevalenceEntity> T getEntity();
	
}
