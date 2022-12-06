package br.tec.jsonprevayler.infrastrutuctre;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import br.tec.jsonprevayler.PrevalentBinaryRepository;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;

public class BackupProvider {
	
	private final FileCore fileCore;
	private final MemoryCore memoryCore;
	private final PrevalentBinaryRepository prevalenceBinary;
	
	public BackupProvider(PrevalenceConfigurator prevalenceConfigurator) {
		fileCore = new FileCore(prevalenceConfigurator.getPrevalencePath(), prevalenceConfigurator.getSystemName(), prevalenceConfigurator.getNumberOfFilesPerDiretory(), prevalenceConfigurator.getDateProvider());
		memoryCore = new MemoryCore(fileCore);
		prevalenceBinary = new PrevalentBinaryRepository(prevalenceConfigurator);
	}
	
	public void getZipBackup(OutputStream outputStream) throws InternalPrevalenceException, ValidationPrevalenceException, IOException {
		File systemDir = fileCore.getSystemFileDir();
		ZipOutputStream zipOut = new ZipOutputStream(outputStream);
		memoryCore.startPosMaintenance();
		prevalenceBinary.stopForMaintenance();
		try {
			zipFile(systemDir, systemDir.getName(), zipOut);
			zipOut.flush();
			zipOut.close();
		} catch (Exception ex) {
			throw new InternalPrevalenceException("Error while backup execution!", ex);
		} finally {
			memoryCore.startPosMaintenance();
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
