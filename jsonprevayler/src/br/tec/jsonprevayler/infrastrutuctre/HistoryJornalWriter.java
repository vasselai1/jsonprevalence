package br.tec.jsonprevayler.infrastrutuctre;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.tec.jsonprevayler.pojojsonrepository.core.OperationType;
import br.tec.jsonprevayler.util.HashUtil;

public class HistoryJornalWriter {

	private static SimpleDateFormat SDF_HISTORY = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ssS");
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
	
	public static void writeHistory(File oldFile) throws IOException {
		String fileExtension = getExtension(oldFile);
		StringBuilder fileName = new StringBuilder();
		fileName.append(oldFile.getName().replace(fileExtension, ""));
		fileName.append("_").append(SDF_HISTORY.format(new Date()));		
		fileName.append(fileExtension);
		File historyFile = new File(getHistoryDirEntity(oldFile.getParentFile()), fileName.toString());
		Files.copy(oldFile.toPath(), historyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void appendJournal(File baseDir, OperationType operationType, Long id, String json) throws IOException, NoSuchAlgorithmException {		
		File journalFile = getJournalFile(baseDir);
		rotateFile(baseDir);
		if (!journalFile.exists()) {
			journalFile.createNewFile();
		}
		StringBuilder journalEntity = new StringBuilder();
		journalEntity.append(operationType.name().substring(0, 1));
		journalEntity.append(";");
		journalEntity.append(SDF_HISTORY.format(new Date()));
		journalEntity.append(";");
		journalEntity.append(id);
		journalEntity.append(";");
		journalEntity.append(HashUtil.getMd5(json));
		journalEntity.append(System.lineSeparator());
		Files.write(journalFile.toPath(), journalEntity.toString().getBytes(), StandardOpenOption.APPEND);
	}

	private static File getJournalFile(File baseDir) {
		return new File(getHistoryDirEntity(baseDir), JOURNAL_FILE_NAME + JOURNAL_FILE_EXT);
	}
	
	public static synchronized void appendJournal(File baseDir, OperationType operationType, Long id, byte[] data) throws IOException, NoSuchAlgorithmException {
		File journalFile = getJournalFile(baseDir);
		rotateFile(baseDir);
		if (!journalFile.exists()) {
			journalFile.createNewFile();
		}
		StringBuilder journalEntity = new StringBuilder();
		journalEntity.append(operationType.name().substring(0, 1));
		journalEntity.append(";");
		journalEntity.append(SDF_HISTORY.format(new Date()));
		journalEntity.append(";");
		journalEntity.append(id);
		journalEntity.append(";");
		journalEntity.append(HashUtil.getMd5(data));
		journalEntity.append(System.lineSeparator());
		Files.write(journalFile.toPath(), journalEntity.toString().getBytes(), StandardOpenOption.APPEND);
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
	
	private static void rotateFile(File baseDir) {
		File journalFile = getJournalFile(baseDir);
		if (!journalFile.exists() || (journalFile.length() < SIZE_IN_BYTES_TO_ROTATE)) {
			return;
		}
		File journalFileRotaded = new File(getHistoryDirEntity(baseDir), JOURNAL_FILE_NAME + SDF_HISTORY.format(new Date()) + JOURNAL_FILE_EXT);
		journalFile.renameTo(journalFileRotaded);
	}
	
}