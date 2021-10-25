package br.org.pr.jsonprevayler.pojojsonrepository.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.infrastrutuctre.HistoryJornalWriter;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.JsonSerializationInstructions;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.MapPrevalenceTransformer;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class FileCore {

	private final String systemPath;
	private final String prevalencePath; 
	private final String systemName;
	public final String FS = File.separator;
	private Logger log = Logger.getLogger(getClass().getName());
	
	public FileCore(String prevalencePath, String systemName) {
		this.prevalencePath = prevalencePath;
		this.systemPath = prevalencePath + FS + systemName;
		this.systemName = systemName;
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
	
	public <T extends PrevalenceEntity> List<T> readRegistries(Class<T> classe) throws IOException {
		List<T> retorno = new ArrayList<T>();
		JSONDeserializer<T> deserializer = new JSONDeserializer<T>();
		File file = getFilePath(classe);
		File[] dataFiles = file.listFiles();
		if (dataFiles == null) {
			return retorno;
		}
		log.info(classe.getSimpleName() + " " + dataFiles.length + " registries");
		int count = 0;
		final int sizePrint = 100000;
		int printInterval = sizePrint;
		boolean printStatus = (dataFiles.length > printInterval);
		for (File dataFile : dataFiles) {
			if (!dataFile.getName().endsWith(".json")) {
				continue;
			}
			retorno.add(deserializer.deserialize(Files.readString(dataFile.toPath())));
			count++;
			if (printStatus && (count == printInterval)) {
				log.info(count + " of " + dataFiles.length);
				printInterval = printInterval + sizePrint;
			}
		}
		log.info("End of read " + retorno.size() + " registries");
		return retorno;
	}
	
	private <T extends PrevalenceEntity> File getFilePath(Class<T> classe) throws IOException {
		String canonicalClassEntityName = classe.getCanonicalName();
		return getFilePath(canonicalClassEntityName);
	}
	
	public File getFilePath(String canonicalClassEntityName) throws IOException {
		File systemDir = getSystemFileDir();
		File dirPathEntities = getPrevalenceDir(systemDir);
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

	public File getPrevalenceDir(File systemDir) {
		File dirPathEntities = new File(systemDir, "PREVALENCE");
		if (!dirPathEntities.exists()) {
			dirPathEntities.mkdir();
		}
		return dirPathEntities;
	}
	
	public <T extends PrevalenceEntity> void writeRegister(Class<T> classe, T entity, String author, JsonSerializationInstructions instructions) throws IOException, NoSuchAlgorithmException {
		OperationType operationType = OperationType.UPDATE;
		File dirPathClassName = getFilePath(classe);
		File fileRegister = new File(dirPathClassName, getFileRegisterName(classe, entity.getId()));
		if (!fileRegister.exists()) {
			operationType = OperationType.SAVE;
			fileRegister.createNewFile();
		}
		JSONSerializer serializer = new JSONSerializer();
		serializer.transform(new MapPrevalenceTransformer(), Map.class);
		for (String excludeLoop : instructions.getIgnores()) {
			serializer.exclude(excludeLoop);	
		}
		for (String includeLoop : instructions.getAdds()) {
			serializer.include(includeLoop);
		}
		String json = serializer.deepSerialize(entity);		
		Files.write(fileRegister.toPath(), json.getBytes());
		HistoryJornalWriter.writeHistory(fileRegister, author);
		HistoryJornalWriter.appendJournal(getFilePath(classe), operationType, entity.getId(), json);
	}
	
	public <T extends PrevalenceEntity> void deleteRegister(Class<T> classe, Long id) throws IOException, NoSuchAlgorithmException {
		File dirPathClassName = getFilePath(classe);
		File fileRegister = new File(dirPathClassName, getFileRegisterName(classe, id));
		if (fileRegister.exists()) {
			Files.delete(fileRegister.toPath());
		}
		HistoryJornalWriter.appendJournal(getFilePath(classe), OperationType.DELETE, id, "deleted");
	}
	
	public <T extends PrevalenceEntity> String getFileRegisterName(Class<T> classe, Long id) {
		StringBuilder retorno = new StringBuilder();
		retorno.append(classe.getSimpleName()).append("_").append(id).append(".json");
		return retorno.toString();
	}	
	
}