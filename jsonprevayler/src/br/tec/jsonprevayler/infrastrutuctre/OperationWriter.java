package br.tec.jsonprevayler.infrastrutuctre;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.pojojsonrepository.core.FileBalancer;
import br.tec.jsonprevayler.util.LoggerUtil;

public class OperationWriter {

	public static final SimpleDateFormat SDF_HISTORY = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ssS");
	private static FileBalancer BALANCER;
	private final File aplicationDirectory;
	private Integer maxFilesPerDiretory;
	private File operationsDir;
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	
	public OperationWriter(File aplicationDirectory, Integer maxFilesPerDiretory) {
		this.aplicationDirectory = aplicationDirectory;
		this.maxFilesPerDiretory = maxFilesPerDiretory;
	}

	public void writeLn(File operationFile, String key, String value) throws InternalPrevalenceException {
		StringBuilder line = new StringBuilder();
		line.append(key).append("=").append(value).append(System.lineSeparator());
		try {
			Files.write(operationFile.toPath(), line.toString().getBytes(), StandardOpenOption.APPEND);
		} catch (Exception e) {
			throw LoggerUtil.error(logger, e, "Error while append line '%1$s' in file = %2$s", line.toString(), operationFile.toPath().toString());
		}
	}

	public File newOperationFile(String fileName) throws InternalPrevalenceException {
		operationsDir = new File(aplicationDirectory, "OPERATIONS");
		if (!operationsDir.exists()) {
			operationsDir.mkdir();
		}
		File file = getFileBalancer().getNewFile(fileName);
		try {
			file.createNewFile();
		} catch (Exception e) {
			throw LoggerUtil.error(logger, e, "Error creating a new operation = %1$s", fileName);
		}
		return file;
	}	
	
	private FileBalancer getFileBalancer() throws InternalPrevalenceException {
		if (BALANCER != null) {
			return BALANCER;
		}
		synchronized (aplicationDirectory) {
			if (BALANCER != null) {
				return BALANCER;
			}
			BALANCER = new FileBalancer("GROUP_", maxFilesPerDiretory, operationsDir.toPath());
			BALANCER.listBalancedDirectories();
		}
		return BALANCER;
	}	
}