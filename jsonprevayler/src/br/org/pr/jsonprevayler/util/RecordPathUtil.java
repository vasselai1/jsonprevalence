package br.org.pr.jsonprevayler.util;

public class RecordPathUtil {

	public static String getPath() {
		return System.getProperty("systemRecordPath", System.getProperty("user.home"));
	}
	
}