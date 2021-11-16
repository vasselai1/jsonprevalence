package br.tec.jsonprevayler.exceptions;

public class DeprecatedPrevalenceEntityVersionException extends Exception {

	private static final long serialVersionUID = 1L;

	public DeprecatedPrevalenceEntityVersionException(String entityClassName, long informedVersion, long lastVersion) {
		super("The informed version " + informedVersion + " for entity " + entityClassName  + " is deprecated, the last version is " + lastVersion + ". Please reload entity before any prevalence operation.");
	}
	
}
