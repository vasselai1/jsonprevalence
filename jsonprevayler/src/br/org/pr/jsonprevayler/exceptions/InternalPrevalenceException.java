package br.org.pr.jsonprevayler.exceptions;

public class InternalPrevalenceException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public InternalPrevalenceException(String message) {
		super(message);
	}
	
	public InternalPrevalenceException(String message, Throwable ex) {
		super(message, ex);
	}
	
}