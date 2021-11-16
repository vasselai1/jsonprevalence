package br.tec.jsonprevayler.infrastrutuctre;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class SynonymProvider {
	
	private final String systemPath;	
	public static List<String[]> sinonyms = new ArrayList<String[]>();
	private Logger log = Logger.getLogger(getClass().getName());
	
	public SynonymProvider(String systemPath) {
		this.systemPath = systemPath;
	}

	public void save(List<String[]> sinonymsList) throws IOException {
		if (sinonymsList == null) {
			return;
		}
		synchronized (sinonyms) {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(getFile())));
			for (String[] lineRegisry : sinonymsList) {
				StringBuilder sb = new StringBuilder();
				for (String word : lineRegisry) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append(word);
				}
				pw.println(sb.toString());
			}
			pw.flush();
			pw.close();
			sinonyms = sinonymsList;
		}
	}
	
	public void delete() throws IOException {
		synchronized (sinonyms) {
			getFile().delete();
			sinonyms.clear();
		}
	}
	
	public List<String[]> get() {
		return Collections.unmodifiableList(sinonyms);
	}
	
	public File getFile() throws IOException {
		File textFile = new File(getFilePath(), "sinonyms.prc");
		if (!textFile.exists()) {
			textFile.createNewFile();
		}
		return textFile;
	}
	
	private File getFilePath() throws IOException {
		File system = new File(systemPath);
		if (!system.exists()) {
			system.mkdir();
		}
		File dirPathBinaries = new File(system, "SINONYM");
		if (!dirPathBinaries.exists()) {
			log.info("Json Persistence for SINONYM initialized in " + dirPathBinaries.getAbsolutePath());
			dirPathBinaries.mkdir();
		}
		return dirPathBinaries;
	}
	
}