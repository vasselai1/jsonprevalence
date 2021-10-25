package br.org.pr.jsonprevayler.infrastrutuctre;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import br.org.pr.jsonprevayler.PrevalentBinaryRepository;
import br.org.pr.jsonprevayler.PrevalentJsonRepository;
import br.org.pr.jsonprevayler.exceptions.InternalPrevalenceException;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;

public class BackupProvider {
	
	private final PrevalentJsonRepository prevalenceJson;
	private final PrevalentBinaryRepository prevalenceBinary;
	
	public BackupProvider(String systemPath, String systemName) {
		prevalenceJson = new PrevalentJsonRepository(systemPath, systemName);
		prevalenceBinary = new PrevalentBinaryRepository(systemPath, systemName);
	}
	
	public void getZipBackup(OutputStream outputStream) throws InternalPrevalenceException, ValidationPrevalenceException, IOException {
		File systemDir = prevalenceJson.getSystemFileDir();
		ZipOutputStream zipOut = new ZipOutputStream(outputStream);
		prevalenceJson.stopForMaintenance();
		prevalenceBinary.stopForMaintenance();
		try {
			zipFile(systemDir, systemDir.getName(), zipOut);
			zipOut.flush();
			zipOut.close();
		} catch (Exception ex) {
			throw new InternalPrevalenceException("Error while backup execution!", ex);
		} finally {
			prevalenceJson.startPosMaintenance();
			prevalenceBinary.startPosMaintenance();
		}
	}
	
    private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));                
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        Files.copy(fileToZip.toPath(), zipOut);
    }
	
	
}
