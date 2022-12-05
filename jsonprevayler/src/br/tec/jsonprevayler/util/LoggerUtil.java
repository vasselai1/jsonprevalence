package br.tec.jsonprevayler.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;

public class LoggerUtil {
	
	public static InternalPrevalenceException error(Logger logger, Throwable ex, String errorDescription, Object... parameters) {
		String message = String.format(errorDescription, parameters);
		InternalPrevalenceException ipex = new InternalPrevalenceException(message, ex);
		logger.log(Level.SEVERE, message, ipex);
		return ipex;
	}
}
