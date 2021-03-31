package jsonprevayler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

public class PersistenceBinaryRepository {
	
	private final String FS = File.separator;
	private final String systemPath;
	private final String FILE_NAME_PREFIX = "file_";
	private final String FILE_NAME_SUFIX = ".bin";
	
	private Logger log = Logger.getLogger(getClass().getName());
	
	public PersistenceBinaryRepository(String path, String systemName) {
		this.systemPath = path + FS + systemName;
	}	
	
	public Long save(byte[] data) throws IOException, Exception {
		return save(new ByteArrayInputStream(data));
	}
	
	public Long save(InputStream inputStream) throws IOException, Exception {
		Long id = SequenceProvider.get(systemPath);
		File file = getBinaryFile(id);
		Files.copy(inputStream, file.toPath());
		return id;
	}
	
	public void update(Long id, byte[] data) throws IOException, Exception {
		update(id, new ByteArrayInputStream(data));
	}
	
	public void update(Long id, InputStream inputStream) throws IOException, Exception {
		File file = getBinaryFile(id);
		synchronized (file.getName()) {
			Files.copy(inputStream, file.toPath());
		}
	}
	
	public void delete(Long id) throws IOException, Exception {
		File file = getBinaryFile(id);
		synchronized (file.getName()) {
			if (file.exists()) {
				file.delete();
				Files.delete(file.toPath());
			}
		}
	}
	
	public byte[] get(Long id) throws IOException {
		File file = getBinaryFile(id);
		return Files.readAllBytes(file.toPath());
	}
	
	public void get(Long id, OutputStream outputStream) throws IOException {
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
		File dirPathBinaries = new File(system, "BINARIES_PERSISTENCE");
		if (!dirPathBinaries.exists()) {
			log.info("Json Persistence for BINARIES initialized in " + dirPathBinaries.getAbsolutePath());
			dirPathBinaries.mkdir();
		}
		return dirPathBinaries;
	}
	
}