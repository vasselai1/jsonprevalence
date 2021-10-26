package br.org.pr.jsonprevayler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import br.org.pr.jsonprevayler.entity.TotalChangesBinarySystem;
import br.org.pr.jsonprevayler.exceptions.InternalPrevalenceException;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.HistoryJornalWriter;
import br.org.pr.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.org.pr.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.org.pr.jsonprevayler.pojojsonrepository.core.OperationType;

/**
 * Prevalence for binaries
 * @author vasselai1
 */
public class PrevalentBinaryRepository {
	
	private static Map<Long, byte[]> binaryRepository = new HashMap<Long, byte[]>();
	
	private final String FS = File.separator;
	private final String systemPath;
	private final String FILE_NAME_PREFIX = "file_";
	private final String FILE_NAME_SUFIX = ".bin";
	private final SequenceProvider sequenceUtil;
	private static boolean inMaintenance = false;
	
	private Logger log = Logger.getLogger(getClass().getName());
	
	public PrevalentBinaryRepository(PrevalenceConfigurator prevalenceConfigurator) {
		this.systemPath = prevalenceConfigurator.getPrevalencePath() + FS + prevalenceConfigurator.getSystemName();
		sequenceUtil = new SequenceProvider(systemPath);
	}	

	public Set<Long> list() throws IOException, ValidationPrevalenceException {
		initialize();
		return binaryRepository.keySet();
	}
	
	public Long save(byte[] data) throws IOException, Exception {		
		return save(new ByteArrayInputStream(data));
	}
	
	public Long save(InputStream inputStream) throws InternalPrevalenceException, IOException, ValidationPrevalenceException, NoSuchAlgorithmException {
		initialize();
		Long id = SequenceProvider.get(systemPath);
		if (isIdUsed(id)) {
			throw new InternalPrevalenceException("Id " + id + " is repeated! Please see the max value in the binaries files and set +1 in sequence file!");
		}		
		File file = getBinaryFile(id);		
		Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		sequenceUtil.get(TotalChangesBinarySystem.class);
		binaryRepository.put(id, Files.readAllBytes(file.toPath()));
		HistoryJornalWriter.writeHistory(file);
		HistoryJornalWriter.appendJournal(getFilePath(), OperationType.SAVE, id, binaryRepository.get(id));
		return id;
	}
	
	public void update(Long id, byte[] data) throws IOException, Exception {				
		update(id, new ByteArrayInputStream(data), null);		
	}
	
	public void update(Long id, byte[] data, String author) throws IOException, Exception {		
		update(id, new ByteArrayInputStream(data), author);		
	}
	
	public void update(Long id, InputStream inputStream) throws IOException, Exception {
		update(id, inputStream, null);
	}
	
	public void update(Long id, InputStream inputStream, String author) throws IOException, NoSuchAlgorithmException, ValidationPrevalenceException {
		initialize();
		File file = getBinaryFile(id);
		synchronized (file.getName()) {
			Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			sequenceUtil.get(TotalChangesBinarySystem.class);
			if (binaryRepository.containsKey(id)) {
				binaryRepository.replace(id, Files.readAllBytes(file.toPath()));
			} else {
				binaryRepository.put(id, Files.readAllBytes(file.toPath()));
			}			
		}		
		HistoryJornalWriter.writeHistory(file);	
		HistoryJornalWriter.appendJournal(getFilePath(), OperationType.UPDATE, id, binaryRepository.get(id));
	}
	
	public void delete(Long id) throws IOException, NoSuchAlgorithmException, ValidationPrevalenceException {
		initialize();
		File file = getBinaryFile(id);
		synchronized (file.getName()) {
			if (file.exists()) {
				Files.delete(file.toPath());
				sequenceUtil.get(TotalChangesBinarySystem.class);
			}
			if (binaryRepository.containsKey(id)) {
				binaryRepository.remove(id);
			}
		}
		HistoryJornalWriter.appendJournal(getFilePath(), OperationType.DELETE, id, "deleted");
	}
		
	public void get(Long id, OutputStream outputStream) throws IOException, ValidationPrevalenceException {		
		byte[] bytes = get(id);
		if (bytes != null) {
			outputStream.write(bytes);
			outputStream.flush();
			outputStream.close();
		}
	}

	public byte[] get(Long id) throws IOException, ValidationPrevalenceException {
		initialize();
		if (binaryRepository.containsKey(id)) {
			return binaryRepository.get(id);
		}
		return null;
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
		File dirPathBinaries = new File(system, "BINARIES");
		if (!dirPathBinaries.exists()) {
			log.info("Json Prevacelence for BINARIES initialized in " + dirPathBinaries.getAbsolutePath());
			dirPathBinaries.mkdir();
		}
		return dirPathBinaries;
	}
	
	private void initialize() throws IOException, ValidationPrevalenceException {
		if (inMaintenance) {
			throw new ValidationPrevalenceException("Prevalence don't be start when maintenance is running! Plese wait the maintenance end.");
		}
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
				if (!binFile.getName().endsWith(".bin")) {
					continue;
				}
				String fileNameId = binFile.getName().replace(FILE_NAME_PREFIX, "").replace(FILE_NAME_SUFIX, "");
				Long id = Long.parseLong(fileNameId);
				binaryRepository.put(id, Files.readAllBytes(binFile.toPath()));
			}
		}
	}
	
	private boolean isIdUsed(Long id) throws IOException, ValidationPrevalenceException {
		return binaryRepository.containsKey(id);
	}	
	
	public void stopForMaintenance() {
		synchronized (binaryRepository) {
			binaryRepository.clear();
			binaryRepository.clear();
			inMaintenance = true;
		}		
	}
	
	public void startPosMaintenance() {
		synchronized (binaryRepository) {
			inMaintenance = false;
		}
	}	
	
}