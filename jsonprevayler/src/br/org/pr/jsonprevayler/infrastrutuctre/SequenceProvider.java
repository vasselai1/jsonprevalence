package br.org.pr.jsonprevayler.infrastrutuctre;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.org.pr.jsonprevayler.entity.BinaryEntity;
import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.entity.Sequence;

public class SequenceProvider {

	private String systemPath;	
	private final Logger log = Logger.getLogger(getClass().getName());
	
	public SequenceProvider(String systemPath) {
		this.systemPath = systemPath;
	}

	public <T extends PrevalenceEntity> long get(Class<T> classEntity) throws IOException {
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
	
	public static synchronized long get(String dirName) throws IOException {
		SequenceProvider sequenceProvider = new SequenceProvider(dirName);
		return sequenceProvider.get(BinaryEntity.class);
	}
	
	private Sequence load(String entityName) throws IOException {
		Sequence sequence = new Sequence();
		sequence.setName(entityName);
		sequence.setLastValue(0);
		File sequenceFile = getFile(entityName);
		FileReader fileReader = new FileReader(sequenceFile);
		BufferedReader reader = new BufferedReader(fileReader);
		String line = reader.readLine();
		if ((line != null) && (!line.isEmpty()) && (!line.isBlank())) {
			Long value = Long.parseLong(line);
			sequence.setLastValue(value);
		}
		reader.close();
		return sequence;
	}
	
	private File getFile(String name) throws IOException {
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
			sequenceFile.createNewFile();
			log.log(Level.INFO, "Json Prevacelence sequence for " + name + " initialized in " + sequenceFile.getAbsolutePath());
		}
		return sequenceFile;
	}
	
	private void upadate(Sequence sequencia) throws IOException {
		File arquivoSequencia = getFile(sequencia.getName());
		FileWriter fileWriter = new FileWriter(arquivoSequencia);
		fileWriter.write("" + sequencia.getLastValue());
		fileWriter.flush();
		fileWriter.close();
	}
	
}