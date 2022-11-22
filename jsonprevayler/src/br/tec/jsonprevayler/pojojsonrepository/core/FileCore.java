package br.tec.jsonprevayler.pojojsonrepository.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.infrastrutuctre.HistoryJornalWriter;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class FileCore {

	private final String systemPath;
	private final String prevalencePath; 
	private final String systemName;
	private final Integer maxFilesPerDiretory;
	private static final Map<Class<? extends PrevalenceEntity>, FileBalancer> entitiesBalancers = new HashMap<Class<? extends PrevalenceEntity>, FileBalancer>();
	public final String FS = File.separator;
	private Logger log = Logger.getLogger(getClass().getName());
	
	public FileCore(String prevalencePath, String systemName, Integer maxFilesPerDiretory) {
		this.prevalencePath = prevalencePath;
		this.systemPath = prevalencePath + FS + systemName;
		this.systemName = systemName;
		this.maxFilesPerDiretory = maxFilesPerDiretory;
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
	
	public <T extends PrevalenceEntity> T readRegistry(Class<T> classe, Long id) throws IOException {
		File dataFile = getFileBalancer(classe).getPath(id).toFile();
		return new JSONDeserializer<T>().deserialize(Files.readString(dataFile.toPath()));
	}
	
	public <T extends PrevalenceEntity> List<T> readRegistries(Class<T> classe) throws IOException {
		List<T> retorno = new ArrayList<T>();
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
				T entity = deserializer.deserialize(Files.readString(dataFile.toPath()));
				retorno.add(entity);
				fileBalancer.addPath(entity.getId(), dataFile.toPath());
			}
		}
		return retorno;
	}
	
	private <T extends PrevalenceEntity> File getFilePath(Class<T> classe) throws IOException {
		String canonicalClassEntityName = classe.getCanonicalName();
		return getFilePath(canonicalClassEntityName);
	}
	
	public File getFilePath(String canonicalClassEntityName) throws IOException {
		File dirPathEntities = getPrevalenceDir();
		File dirPathClassName = new File(dirPathEntities, canonicalClassEntityName);
		if (!dirPathClassName.exists()) {
			log.info("Json Prevacelence for " + canonicalClassEntityName + " initialized in " + dirPathClassName.getAbsolutePath());
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
	
	public <T extends PrevalenceEntity> void writeRegister(Class<T> classe, T entity) throws IOException, NoSuchAlgorithmException {
		FileBalancer fileBalancer = getFileBalancer(classe);
		Path fileRegisterPath = fileBalancer.getPath(entity.getId());		
		OperationType operationType = OperationType.UPDATE;
		File fileRegister = null;
		if (fileRegisterPath == null) {
			operationType = OperationType.SAVE;
			String fileName = getFileRegisterName(classe, entity.getId());
			fileRegister = fileBalancer.getNewFile(fileName);
			fileRegister.createNewFile();
		} else {
			fileRegister = fileRegisterPath.toFile();
		}
		String json = new JSONSerializer().deepSerialize(entity);		
		Files.write(fileRegister.toPath(), json.getBytes());
		//TODO balancear arquivos de historia tbm
		HistoryJornalWriter.writeHistory(fileRegister);
		HistoryJornalWriter.appendJournal(getFilePath(classe), operationType, entity.getId(), json);
	}
	
	public <T extends PrevalenceEntity> void deleteRegister(Class<T> classe, Long id) throws IOException, NoSuchAlgorithmException {
		Path fileRegisterPath = getFileBalancer(classe).getPath(id);
		if (fileRegisterPath != null) {
			Files.delete(fileRegisterPath);
		}
		HistoryJornalWriter.appendJournal(getFilePath(classe), OperationType.DELETE, id, "deleted");
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
				continue;
			}
		}		
		return prevalentClasses;
	}
	
	private <T extends PrevalenceEntity> FileBalancer getFileBalancer(Class<T> classe) throws IOException {
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