package br.tec.jsonprevayler.infrastrutuctre;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.tec.jsonprevayler.entity.BinaryEntity;
import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.entity.Sequence;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.util.LoggerUtil;

public class SequenceProvider {

	private String systemPath;	
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	public SequenceProvider(String systemPath) {
		this.systemPath = systemPath;
	}

	public <T extends PrevalenceEntity> long get(Class<T> classEntity) throws InternalPrevalenceException {
		String entityName = classEntity.getCanonicalName();
		long retorno = 0;
		synchronized (classEntity) {			
			Sequence sequence = load(entityName);
			retorno = sequence.getLastValue() + 1;
			sequence.setLastValue(retorno);
			upadate(sequence);
		}
		return retorno;
	}
	
	public static synchronized long get(String dirName) throws InternalPrevalenceException {
		SequenceProvider sequenceProvider = new SequenceProvider(dirName);
		return sequenceProvider.get(BinaryEntity.class);
	}
	
	private Sequence load(String entityName) throws InternalPrevalenceException {
		Sequence sequence = new Sequence();
		sequence.setName(entityName);
		sequence.setLastValue(0);
		File sequenceFile = getFile(entityName);		
		BufferedReader reader = null;
		try {
			FileReader fileReader = new FileReader(sequenceFile);
			reader = new BufferedReader(fileReader);
		} catch (Exception e) {
			throw LoggerUtil.error(logger, e, "Error while create FileReader for sequence of entity = %1$s, file = %2$s", sequenceFile.getName());		
		}
		try {
			String line = reader.readLine();
			if ((line != null) && (!line.isEmpty()) && (!line.isBlank())) {
				Long value = Long.parseLong(line);
				sequence.setLastValue(value);
			}
		} catch (Exception e) {
			throw LoggerUtil.error(logger, e, "Error while read line for sequence of entity = %1$s, file = %2$s", sequenceFile.getName());
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				throw LoggerUtil.error(logger, e, "Error while close reader for sequence of entity = %1$s, file = %2$s", sequenceFile.getName());
			}
		} 
		return sequence;
	}
	
	private File getFile(String name) throws InternalPrevalenceException {
		File system = new File(systemPath);
		if (!system.exists()) {
			system.mkdir();
		}
		File dirPathSequences = new File(system, "SEQUENCES");
		if (!dirPathSequences.exists()) {
			dirPathSequences.mkdir();
		}
		File sequenceFile = new File(dirPathSequences, name.concat(".seq"));
		if (!sequenceFile.exists()) {
			try {
				sequenceFile.createNewFile();
			} catch (Exception e) {
				throw LoggerUtil.error(logger, e, "Error while create a new file for sequence of entity = %1$s, file = %2$s", sequenceFile.getName());
			}
			logger.log(Level.INFO, "Json Prevacelence sequence for " + name + " initialized in " + sequenceFile.getAbsolutePath());
		}
		return sequenceFile;
	}
	
	private void upadate(Sequence sequence) throws InternalPrevalenceException {
		File sequenceFile = getFile(sequence.getName());
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(sequenceFile);
			fileWriter.write("" + sequence.getLastValue());
			fileWriter.flush();
		} catch (Exception e) {
			throw LoggerUtil.error(logger, e, "Error while update file for sequence of entity = %1$s, file = %2$s", sequenceFile.getName());
		} finally {
			try { 
				fileWriter.close();
			} catch (Exception e) {
				throw LoggerUtil.error(logger, e, "Error while close writer for sequence of entity = %1$s, file = %2$s", sequenceFile.getName());
			}
		}
	}
	
}