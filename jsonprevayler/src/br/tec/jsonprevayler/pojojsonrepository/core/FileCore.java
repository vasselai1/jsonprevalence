package br.tec.jsonprevayler.pojojsonrepository.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.HistoryWriter;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;
import br.tec.jsonprevayler.pojojsonrepository.core.util.FileNameFilterClassId;
import br.tec.jsonprevayler.util.LoggerUtil;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class FileCore {

	private static final Map<Class<? extends PrevalenceEntity>, FileBalancer> entitiesBalancers = new HashMap<Class<? extends PrevalenceEntity>, FileBalancer>();
	private static final String FS = File.separator;
		
	private final String systemPath;
	private final String prevalencePath; 
	private final String systemName;
	private final Integer maxFilesPerDiretory;
	private final DateProvider dateProvider;
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	public FileCore(String prevalencePath, String systemName, Integer maxFilesPerDiretory, DateProvider dateProvider) {
		this.prevalencePath = prevalencePath;
		this.systemPath = prevalencePath + FS + systemName;
		this.systemName = systemName;
		this.maxFilesPerDiretory = maxFilesPerDiretory;
		this.dateProvider = dateProvider;
	}

	public String getPrevalencePath() {
		return prevalencePath;
	}
	
	public String getSystemName() {
		return systemName;
	}
	
	public String getSystemPath() {
		return systemPath;
	}
	
	public <T extends PrevalenceEntity> T readRegistry(Class<T> classe, Long id) throws InternalPrevalenceException {
		File dataFile = getFileBalancer(classe).getPath(id).toFile();
		try {
			return new JSONDeserializer<T>().deserialize(Files.readString(dataFile.toPath()));
		} catch (IOException e) {			
			throw LoggerUtil.error(logger, e, "Error while deserialize entity = %1$s, id = %2$d, file = %3$s", classe, id, dataFile.getName());
		}
	}
	
	public <T extends PrevalenceEntity> Map<String,T>  readRegistries(Class<T> classe) throws InternalPrevalenceException {
		Map<String, T> retorno = new HashMap<String, T>();
		JSONDeserializer<T> deserializer = new JSONDeserializer<T>();
		FileBalancer fileBalancer =  getFileBalancer(classe);
		List<File> balancedDirectories = fileBalancer.listBalancedDirectories();
		for (File directoryLoop : balancedDirectories) {
			fileBalancer.resetFilesCounter();
			File[] dataFiles = directoryLoop.listFiles();
			if (dataFiles == null) {
				return retorno;
			}
			for (File dataFile : dataFiles) {
				if (!dataFile.getName().endsWith(".json")) {
					continue;
				}
				try {
					String json = Files.readString(dataFile.toPath());
					T entity = deserializer.deserialize(json);
					retorno.put(json, entity);
					fileBalancer.addPath(entity.getId(), dataFile.toPath());
				} catch (IOException e) {
					throw LoggerUtil.error(logger, e, "Error while deserialize entities = %1$s, file = %2$s", classe, dataFile.getName());
				}
			}
		}
		return retorno;
	}
	
	private <T extends PrevalenceEntity> File getFilePath(Class<T> classe) {
		String canonicalClassEntityName = classe.getCanonicalName();
		return getFilePath(canonicalClassEntityName);
	}
	
	public File getFilePath(String canonicalClassEntityName) {
		File dirPathEntities = getPrevalenceDir();
		File dirPathClassName = new File(dirPathEntities, canonicalClassEntityName);
		if (!dirPathClassName.exists()) {
			logger.info("Json Prevacelence for " + canonicalClassEntityName + " initialized in " + dirPathClassName.getAbsolutePath());
			dirPathClassName.mkdir();
		}
		return dirPathClassName;
	}

	public File getSystemFileDir() {
		File systemDir = new File(systemPath);
		if (!systemDir.exists()) {
			systemDir.mkdir();
		}
		return systemDir;
	}

	public File getPrevalenceDir() {
		File dirPathEntities = new File(getSystemFileDir(), "PREVALENCE");
		if (!dirPathEntities.exists()) {
			dirPathEntities.mkdir();
		}
		return dirPathEntities;
	}
	
	public <T extends PrevalenceEntity> void writeRegister(Class<T> classe, T entity) throws InternalPrevalenceException {
		writeRegister(classe, entity, false);
	}
	
	public <T extends PrevalenceEntity> void writeRegister(Class<T> classe, T entity, boolean isControlFile) throws InternalPrevalenceException {
		FileBalancer fileBalancer = getFileBalancer(classe);
		if (fileBalancer.isActualPathNotInitialized()) {
			fileBalancer.listBalancedDirectories();
		}
		Path fileRegisterPath = fileBalancer.getPath(entity.getId());				
		File fileRegister = null;
		if (fileRegisterPath == null) {		
			String fileName = getFileRegisterName(classe, entity.getId());
			fileRegister = fileBalancer.getNewFile(fileName);
			try {
				fileRegister.createNewFile();
			} catch (IOException e) {
				throw LoggerUtil.error(logger, e, "Error creating a new file for entity class = %1$s, id = %2$d", classe, entity.getId());
			}
		} else {
			fileRegister = fileRegisterPath.toFile();
		}
		String json = new JSONSerializer().deepSerialize(entity);
		try {
			Files.write(fileRegister.toPath(), json.getBytes());
		} catch (IOException e) {
			throw LoggerUtil.error(logger, e, "Error writing file for entity class = %1$s, id = %2$d, json = %3$s", classe, entity.getId(), json);
		}
		if (!isControlFile) {
			new HistoryWriter(getFilePath(classe), maxFilesPerDiretory).writeHistory(fileRegister, dateProvider);
		}
	}
	
	public <T extends PrevalenceEntity> void deleteRegister(Class<T> classe, Long id) throws InternalPrevalenceException {
		Path fileRegisterPath = getFileBalancer(classe).getPath(id);
		try {
			if (fileRegisterPath != null) {
				Files.delete(fileRegisterPath);
			}
		} catch (IOException e) {
			throw LoggerUtil.error(logger, e, "Error deleting entity class = %1$s, id = %2$d, file = %3$s", classe, id, fileRegisterPath.getFileName());
		}
	}
	
	public <T extends PrevalenceEntity> String getFileRegisterName(Class<T> classe, Long id) {
		StringBuilder retorno = new StringBuilder();
		retorno.append(classe.getSimpleName()).append("_").append(id).append(".json");
		return retorno.toString();
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Class<? extends PrevalenceEntity>> listAllPrevalentClasses() {		
		File prevalenceDir = getPrevalenceDir();		
		List<Class<? extends PrevalenceEntity>> prevalentClasses = new ArrayList<>();
		if (prevalenceDir == null) {
			return prevalentClasses;
		}
		for (File directoryLoop : prevalenceDir.listFiles()) {
			if (!directoryLoop.isDirectory()) {
				continue;
			}
			try {
				Class classe = Class.forName(directoryLoop.getName());
				if (PrevalenceEntity.class.isAssignableFrom(classe)) {
					prevalentClasses.add((Class<? extends PrevalenceEntity>) classe);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Trash in directory " + directoryLoop.getName(), e);				
			}
		}		
		return prevalentClasses;
	}
	
	public <T extends PrevalenceEntity> List<File> listVersions(Class<T> classe, Long id) throws InternalPrevalenceException {
		FileNameFilterClassId fileFilter = new FileNameFilterClassId(classe.getSimpleName(), id);
		List<File> files = new ArrayList<File>();
		FileBalancer fileBalancer = getFileBalancer(classe);
		List<File> directories = fileBalancer.listBalancedDirectories();		
		for (File directory : directories) {
			files.addAll(Arrays.asList(directory.listFiles(fileFilter)));
		}
		return files;
	}  
	
	private <T extends PrevalenceEntity> FileBalancer getFileBalancer(Class<T> classe) throws InternalPrevalenceException {
		if (entitiesBalancers.containsKey(classe)) {
			return entitiesBalancers.get(classe);
		}
		synchronized (classe) {
			if (entitiesBalancers.containsKey(classe)) {
				return entitiesBalancers.get(classe);
			}
			entitiesBalancers.put(classe, new FileBalancer("GROUP_", maxFilesPerDiretory, getFilePath(classe).toPath()));
		}
		return entitiesBalancers.get(classe);
	}
	
}