package jsonprevayler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jsonprevayler.PrevalenceRepository.OperationType;
import jsonprevayler.exceptions.ValidationException;
import jsonprevayler.history.HistoryWriter;

/**
 * Prevalence for binaries
 * @author vasselai1
 */
public class PrevalenceBinaryRepository {
	
	private static Map<Long, byte[]> binaryRepository = new HashMap<Long, byte[]>();
	
	private final String FS = File.separator;
	private final String systemPath;
	private final String FILE_NAME_PREFIX = "file_";
	private final String FILE_NAME_SUFIX = ".bin";
	
	private Logger log = Logger.getLogger(getClass().getName());
	
	public PrevalenceBinaryRepository(String path, String systemName) {
		this.systemPath = path + FS + systemName;
	}	
	
	/**
	 * @param path dir name for persisted files.
	 * @param systemName name of your system. 
	 */
	public Long save(byte[] data) throws IOException, Exception {
		initialize();
		return save(new ByteArrayInputStream(data));
	}
	
	public Long save(InputStream inputStream) throws IOException, Exception {
		initialize();
		Long id = SequenceProvider.get(systemPath);
		File file = getBinaryFile(id);
		Files.copy(inputStream, file.toPath());
		binaryRepository.put(id, Files.readAllBytes(file.toPath()));
		HistoryWriter.appendJournal(getFilePath(), OperationType.SAVE, id, binaryRepository.get(id));
		return id;
	}
	
	public void update(Long id, byte[] data) throws IOException, Exception {
		initialize();
		update(id, new ByteArrayInputStream(data));
	}
	
	public void update(Long id, InputStream inputStream) throws IOException, Exception {
		initialize();
		File file = getBinaryFile(id);
		synchronized (file.getName()) {
			Files.copy(inputStream, file.toPath());
			if (binaryRepository.containsKey(id)) {
				binaryRepository.replace(id, Files.readAllBytes(file.toPath()));
			} else {
				binaryRepository.put(id, Files.readAllBytes(file.toPath()));
			}			
		}
		HistoryWriter.appendJournal(getFilePath(), OperationType.UPDATE, id, binaryRepository.get(id));
	}
	
	public void delete(Long id) throws IOException, Exception {
		initialize();
		File file = getBinaryFile(id);
		synchronized (file.getName()) {
			if (file.exists()) {
				file.delete();
				Files.delete(file.toPath());
			}
			if (binaryRepository.containsKey(id)) {
				binaryRepository.remove(id);
			}
		}
		HistoryWriter.appendJournal(getFilePath(), OperationType.SAVE, id, "deleted");
	}
	
	public byte[] get(Long id) throws IOException {
		initialize();
		if (binaryRepository.containsKey(id)) {
			return binaryRepository.get(id);
		}
		return null;
	}
	
	public void get(Long id, OutputStream outputStream) throws IOException {
		initialize();
		byte[] bytes = get(id);
		if (bytes != null) {
			outputStream.write(bytes);
			outputStream.flush();
			outputStream.close();
		}
	}
	
	private File getBinaryFile(Long id) throws IOException {
		String name = FILE_NAME_PREFIX + id + FILE_NAME_SUFIX;
		File binaryFile = new File(getFilePath(), name);
		if (!binaryFile.exists()) {
			binaryFile.createNewFile();
		}
		return binaryFile;
	}
	
	private File getFilePath() throws IOException {
		File system = new File(systemPath);
		if (!system.exists()) {
			system.mkdir();
		}
		File dirPathBinaries = new File(system, "BINARIES_PREVALENCE");
		if (!dirPathBinaries.exists()) {
			log.info("Json Prevacelence for BINARIES initialized in " + dirPathBinaries.getAbsolutePath());
			dirPathBinaries.mkdir();
		}
		return dirPathBinaries;
	}
	
	private void initialize() throws IOException {
		synchronized (binaryRepository) {
			if (!binaryRepository.isEmpty()) {
				return;
			}
			File dirFiles = getFilePath();
			File[] files = dirFiles.listFiles();
			if (files == null) {
				return;
			}
			for (File binFile : files) {
				String fileNameId = binFile.getName().replace(FILE_NAME_PREFIX, "").replace(FILE_NAME_SUFIX, "");
				Long id = Long.parseLong(fileNameId);
				binaryRepository.put(id, Files.readAllBytes(binFile.toPath()));
			}
		}
	}
	
}