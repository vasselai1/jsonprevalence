package br.tec.jsonprevayler.infrastrutuctre;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.pojojsonrepository.core.FileBalancer;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;
import br.tec.jsonprevayler.util.LoggerUtil;

public class HistoryWriter {

	public static final SimpleDateFormat SDF_HISTORY = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ssS");
	private static final Map<String, FileBalancer> BALANCERS = new HashMap<String, FileBalancer>();
	
	private Integer maxFilesPerDiretory;
	private final File entityDirectory;
	
	public HistoryWriter(File entityDirectory, Integer maxFilesPerDiretory) {
		this.entityDirectory = entityDirectory;
		this.maxFilesPerDiretory = maxFilesPerDiretory;
	}

	private File newHistoryFile(String fileName) throws InternalPrevalenceException {
		File historyDir = new File(entityDirectory, "HISTORY");
		if (!historyDir.exists()) {
			historyDir.mkdir();
		}
		return getFileBalancer(historyDir).getNewFile(fileName);
	}
	
	public void writeHistory(File oldFile, DateProvider dateProvider) throws InternalPrevalenceException {
		String fileExtension = getExtension(oldFile);
		StringBuilder fileName = new StringBuilder();
		fileName.append(oldFile.getName().replace(fileExtension, ""));
		fileName.append("_").append(SDF_HISTORY.format(dateProvider.get()));		
		fileName.append(fileExtension);
		File historyFile = newHistoryFile(fileName.toString());
		try {
			Files.copy(oldFile.toPath(), historyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			throw LoggerUtil.error(Logger.getLogger(HistoryWriter.class.getName()), e, "Error while copy history old file = %1$s, history file = %2$s", oldFile.getName(), historyFile.getName());
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

	private FileBalancer getFileBalancer(File historyEntityDir) throws InternalPrevalenceException {
		String directoryPath = historyEntityDir.toPath().toString();
		if (BALANCERS.containsKey(directoryPath)) {
			return BALANCERS.get(directoryPath);
		}
		synchronized (directoryPath) {
			if (BALANCERS.containsKey(directoryPath)) {
				return BALANCERS.get(directoryPath);
			}
			BALANCERS.put(directoryPath, new FileBalancer("GROUP_", maxFilesPerDiretory, historyEntityDir.toPath()));
			BALANCERS.get(directoryPath).listBalancedDirectories();
		}
		return BALANCERS.get(directoryPath);
	}	
	
}