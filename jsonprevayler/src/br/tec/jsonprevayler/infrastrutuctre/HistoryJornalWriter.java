package br.tec.jsonprevayler.infrastrutuctre;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryOperationType;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;
import br.tec.jsonprevayler.util.HashUtil;
import br.tec.jsonprevayler.util.LoggerUtil;

public class HistoryJornalWriter {

	public  static final SimpleDateFormat SDF_HISTORY = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ssS");
	private static final String JOURNAL_FILE_NAME = "operations";
	private static final String JOURNAL_FILE_EXT = ".jrn";
	private static final int SIZE_IN_BYTES_TO_ROTATE = 10485760;//10MB 
	
	private static File getHistoryDirEntity(File baseDir) {
		File historyDir = new File(baseDir, "history");
		if (!historyDir.exists()) {
			historyDir.mkdir();
		}
		return historyDir;
	}
	
	public static void writeHistory(File oldFile, DateProvider dateProvider) throws InternalPrevalenceException {
		String fileExtension = getExtension(oldFile);
		StringBuilder fileName = new StringBuilder();
		fileName.append(oldFile.getName().replace(fileExtension, ""));
		fileName.append("_").append(SDF_HISTORY.format(dateProvider.get()));		
		fileName.append(fileExtension);
		File historyFile = new File(getHistoryDirEntity(oldFile.getParentFile()), fileName.toString());
		try {
			Files.copy(oldFile.toPath(), historyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			throw LoggerUtil.error(Logger.getLogger(HistoryJornalWriter.class.getName()), e, "Error while copy history old file = %1$s, history file = %2$s", oldFile.getName(), historyFile.getName());
		}
	}
	
	public static void appendJournal(File baseDir, MemoryOperationType operationType, Long id, String json, DateProvider dateProvider) throws InternalPrevalenceException {		
		File journalFile = getJournalFile(baseDir);
		rotateFile(baseDir, dateProvider);
		if (!journalFile.exists()) {
			try {
				journalFile.createNewFile();
			} catch (Exception e) {
				throw LoggerUtil.error(Logger.getLogger(HistoryJornalWriter.class.getName()), e, "Error while create a new file for journal file = %1$s", journalFile.getName());
			}
		}
		StringBuilder journalEntity = new StringBuilder();
		journalEntity.append(operationType.name().substring(0, 1));
		journalEntity.append(";");
		journalEntity.append(SDF_HISTORY.format(dateProvider.get()));
		journalEntity.append(";");
		journalEntity.append(id);
		journalEntity.append(";");
		try {
			journalEntity.append(HashUtil.getMd5(json));
		} catch (Exception e) {
			throw LoggerUtil.error(Logger.getLogger(HistoryJornalWriter.class.getName()), e, "Error while create hash MD5 for journal file = %1$s, json = %2$s", journalFile.getName(), json);
		}
		journalEntity.append(System.lineSeparator());
		try {
			Files.write(journalFile.toPath(), journalEntity.toString().getBytes(), StandardOpenOption.APPEND);
		} catch (Exception e) {
			throw LoggerUtil.error(Logger.getLogger(HistoryJornalWriter.class.getName()), e, "Error while copy write journal file = %1$s, json = %2$s", journalFile.getName(), json);
		}		
	}

	private static File getJournalFile(File baseDir) {
		return new File(getHistoryDirEntity(baseDir), JOURNAL_FILE_NAME + JOURNAL_FILE_EXT);
	}
	
	public static synchronized void appendJournal(File baseDir, MemoryOperationType operationType, Long id, byte[] data, DateProvider dateProvider) throws InternalPrevalenceException {
		File journalFile = getJournalFile(baseDir);
		rotateFile(baseDir, dateProvider);
		if (!journalFile.exists()) {
			try {
				journalFile.createNewFile();
			} catch (Exception e) {
				throw LoggerUtil.error(Logger.getLogger(HistoryJornalWriter.class.getName()), e, "Error while create a new file for journal file = %1$s", journalFile.getName());
			}
		}
		StringBuilder journalEntity = new StringBuilder();
		journalEntity.append(operationType.name().substring(0, 1));
		journalEntity.append(";");
		journalEntity.append(SDF_HISTORY.format(dateProvider.get()));
		journalEntity.append(";");
		journalEntity.append(id);
		journalEntity.append(";");
		try {
			journalEntity.append(HashUtil.getMd5(data));
		} catch (Exception e) {
			throw LoggerUtil.error(Logger.getLogger(HistoryJornalWriter.class.getName()), e, "Error while create hash MD5 for journal file = %1$s, binary", journalFile.getName());
		}
		journalEntity.append(System.lineSeparator());
		try {
			Files.write(journalFile.toPath(), journalEntity.toString().getBytes(), StandardOpenOption.APPEND);
		} catch (Exception e) {
			throw LoggerUtil.error(Logger.getLogger(HistoryJornalWriter.class.getName()), e, "Error while copy write journal file = %1$s, binary", journalFile.getName());
		}
	}	
	
	public static String getExtension(File file) {
		if (file == null) {
			return null;
		}
		String fileName = file.getName();
		if (!fileName.contains(".")) {
			return "";
		}
		int index = fileName.lastIndexOf(".");
		return fileName.substring(index).toLowerCase();
	}
	
	private static void rotateFile(File baseDir, DateProvider dateProvider) {
		File journalFile = getJournalFile(baseDir);
		if (!journalFile.exists() || (journalFile.length() < SIZE_IN_BYTES_TO_ROTATE)) {
			return;
		}
		File journalFileRotaded = new File(getHistoryDirEntity(baseDir), JOURNAL_FILE_NAME + SDF_HISTORY.format(dateProvider.get()) + JOURNAL_FILE_EXT);
		journalFile.renameTo(journalFileRotaded);
	}
	
}