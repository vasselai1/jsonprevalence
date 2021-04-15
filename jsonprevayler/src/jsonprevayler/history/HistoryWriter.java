package jsonprevayler.history;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jsonprevayler.PrevalenceRepository.OperationType;
import jsonprevayler.util.HashPasswdUtil;

public class HistoryWriter {

	private static SimpleDateFormat SDF_HISTORY = new SimpleDateFormat("ddMMyyyy_HH:mm:ss.S");
	
	private static File getHistoryDirEntity(File baseDir) {
		File historyDir = new File(baseDir, "history");
		if (!historyDir.exists()) {
			historyDir.mkdir();
		}
		return historyDir;
	}
	
	public static void writeHistory(File oldFile, String author) throws IOException {
		StringBuilder fileName = new StringBuilder();
		fileName.append(oldFile.getName().replace(".json", ""));
		fileName.append("_").append(SDF_HISTORY.format(new Date()));
		fileName.append("_").append(author);
		fileName.append(".json");
		File historyFile = new File(getHistoryDirEntity(oldFile.getParentFile()), fileName.toString());
		Files.copy(oldFile.toPath(), historyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void appendJournal(File baseDir, OperationType operationType, Long id, String json) throws IOException, NoSuchAlgorithmException {
		File journalFile = new File(getHistoryDirEntity(baseDir), "operations.jrn");
		if (!journalFile.exists()) {
			journalFile.createNewFile();
		}
		StringBuilder journalEntity = new StringBuilder();
		journalEntity.append(operationType.name().substring(0, 1));
		journalEntity.append(":");
		journalEntity.append(SDF_HISTORY.format(new Date()));
		journalEntity.append(":");
		journalEntity.append(id);
		journalEntity.append(":");
		journalEntity.append(HashPasswdUtil.getSha512(json));
		journalEntity.append(System.lineSeparator());
		Files.write(journalFile.toPath(), journalEntity.toString().getBytes(), StandardOpenOption.APPEND);
	}
}