package br.tec.jsonprevayler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import br.tec.jsonprevayler.entity.TotalChangesBinarySystem;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileBalancer;
import br.tec.jsonprevayler.util.LoggerUtil;

/**
 * Prevalence for binaries
 * @author vasselai1
 */
public class PrevalentBinaryRepository {
	
	private static Map<Long, byte[]> binaryRepository = new HashMap<Long, byte[]>();
	private FileBalancer binaryFileBalancer = null;
	
	private final String FS = File.separator;
	private final String systemPath;
	private final String FILE_NAME_PREFIX = "file_";
	private final String FILE_NAME_SUFIX = ".bin";
	private final SequenceProvider sequenceUtil;
	private static boolean inMaintenance = false;
	
	private Logger logger = Logger.getLogger(getClass().getName());
	
	public PrevalentBinaryRepository(PrevalenceConfigurator prevalenceConfigurator) {		
		this.systemPath = prevalenceConfigurator.getPrevalencePath() + FS + prevalenceConfigurator.getSystemName();
		sequenceUtil = new SequenceProvider(systemPath);
		binaryFileBalancer = new FileBalancer("GROUP_", prevalenceConfigurator.getNumberOfFilesPerDiretory(), getFilePath().toPath());
	}	

	public Set<Long> list() throws InternalPrevalenceException, ValidationPrevalenceException {
		initialize();		
		return binaryRepository.keySet();		
	}
	
	public Long save(byte[] data) throws InternalPrevalenceException, ValidationPrevalenceException {		
		return save(new ByteArrayInputStream(data));
	}
	
	public Long save(InputStream inputStream) throws InternalPrevalenceException, ValidationPrevalenceException {
		initialize();
		Long id = SequenceProvider.get(systemPath);
		if (isIdUsed(id)) {
			throw new InternalPrevalenceException("Id " + id + " is repeated! Please see the max value in the binaries files and set +1 in sequence file!");
		}		
		File file = getBinaryFile(id);
		try {
			Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			throw LoggerUtil.error(logger, e, "Error coping stream to file %1$s", file.getName());
		}
		sequenceUtil.get(TotalChangesBinarySystem.class);
		try {
			binaryRepository.put(id, Files.readAllBytes(file.toPath()));
		} catch (Exception e) {
			throw LoggerUtil.error(logger, e, "Error reading file %1$s to memory", file.getName());
		}
		return id;
	}
	
	public void update(Long id, byte[] data) throws InternalPrevalenceException, ValidationPrevalenceException {				
		update(id, new ByteArrayInputStream(data));		
	}
	
	public void update(Long id, InputStream inputStream) throws InternalPrevalenceException, ValidationPrevalenceException {
		initialize();
		File file = getBinaryFile(id);
		synchronized (file.getName()) {
			try {
				Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				throw LoggerUtil.error(logger, e, "Error coping stream to file %1$s", file.getName());
			}
			sequenceUtil.get(TotalChangesBinarySystem.class);
			try {
				if (binaryRepository.containsKey(id)) {
					binaryRepository.replace(id, Files.readAllBytes(file.toPath()));
				} else {
					binaryRepository.put(id, Files.readAllBytes(file.toPath()));
				}
			} catch (Exception e) {
				throw LoggerUtil.error(logger, e, "Error reading binary file %1$s to memory", file.getName());
			}
		}		
	}
	
	public void delete(Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		initialize();
		File file = getBinaryFile(id);
		synchronized (file.getName()) {
			if (file.exists()) {
				try {
					Files.delete(file.toPath());
				} catch (Exception e) {
					throw LoggerUtil.error(logger, e, "Error deleting file %1$s to memory", file.getName());
				}
				sequenceUtil.get(TotalChangesBinarySystem.class);
			}
			if (binaryRepository.containsKey(id)) {
				binaryRepository.remove(id);
			}
		}
	}
		
	public void get(Long id, OutputStream outputStream) throws InternalPrevalenceException, ValidationPrevalenceException {		
		byte[] bytes = get(id);
		if (bytes != null) {
			try {
				outputStream.write(bytes);
				outputStream.flush();
			} catch (Exception e) {
				throw LoggerUtil.error(logger, e, "Error writing file id = %1$d in outputstream", id);
			} finally {
				try {
					outputStream.close();
				} catch (Exception e) {
					throw LoggerUtil.error(logger, e, "Error closing outputstream for copy file id = %1$d", id);
				}
			}
			
		}
	}

	public byte[] get(Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		initialize();
		if (binaryRepository.containsKey(id)) {
			return binaryRepository.get(id);
		}
		return null;
	}	
	
	private File getBinaryFile(Long id) throws InternalPrevalenceException {
		String binaryFileName = FILE_NAME_PREFIX + id + FILE_NAME_SUFIX;
		if (binaryFileBalancer.isActualPathNotInitialized()) {
			binaryFileBalancer.listBalancedDirectories();
		}
		Path fileBinaryPath = binaryFileBalancer.getPath(id);
		File binaryFile = null;
		if (fileBinaryPath == null) {		
			binaryFile = binaryFileBalancer.getNewFile(binaryFileName);
			try {
				binaryFile.createNewFile();
			} catch (Exception e) {
				throw LoggerUtil.error(logger, e, "Error creating a new file %1$d", binaryFile.getName());
			}
		} else {
			binaryFile = fileBinaryPath.toFile();
		}
		return binaryFile;
	}
	
	private File getFilePath() {
		File system = new File(systemPath);
		if (!system.exists()) {
			system.mkdir();
		}
		File dirPathBinaries = new File(system, "BINARIES");
		if (!dirPathBinaries.exists()) {
			logger.info("Prevacelence for BINARIES initialized in " + dirPathBinaries.getAbsolutePath());
			dirPathBinaries.mkdir();
		}
		return dirPathBinaries;
	}
	
	private void initialize() throws InternalPrevalenceException, ValidationPrevalenceException {
		if (inMaintenance) {
			throw new ValidationPrevalenceException("Prevalence don't be start when maintenance is running! Plese wait the maintenance end.");
		}
		synchronized (binaryRepository) {
			if (!binaryRepository.isEmpty()) {
				return;
			}
			
			List<File> balancedDirectories = binaryFileBalancer.listBalancedDirectories();
			for (File directoryLoop : balancedDirectories) {				
				File[] files = directoryLoop.listFiles();
				if (files == null) {
					return;
				}
				for (File binFile : files) {
					if (!binFile.getName().endsWith(".bin")) {
						continue;
					}
					String fileNameId = binFile.getName().replace(FILE_NAME_PREFIX, "").replace(FILE_NAME_SUFIX, "");
					Long id = Long.parseLong(fileNameId);
					try {
						binaryRepository.put(id, Files.readAllBytes(binFile.toPath()));
					} catch (Exception e) {
						throw LoggerUtil.error(logger, e, "Error reading binary file %1$s to memory", binFile.getName());
					}
				}				
			}
		}
	}
	
	private boolean isIdUsed(Long id) {
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