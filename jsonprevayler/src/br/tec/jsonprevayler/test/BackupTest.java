package br.tec.jsonprevayler.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.BackupProvider;
import br.tec.jsonprevayler.util.RecordPathUtil;

class BackupTest {

	private static final String DIR_DADOS_TESTES = RecordPathUtil.getUserHomePath();
	private static final String SYSTEM_TEST_NAME = "PREVALENCE_TEST";
	
	@Test
	void test() throws IOException, InternalPrevalenceException, ValidationPrevalenceException {
		File backupDir = new File(DIR_DADOS_TESTES, "auto_backup_tests");
		if (!backupDir.exists()) {
			backupDir.mkdir();
		}
		File backupFile = new File(backupDir, SYSTEM_TEST_NAME + "_backup.zip");
		if (!backupFile.exists()) {
			backupFile.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(backupFile);
		BackupProvider backupProvider = new BackupProvider(TestPrevalenceConfigurator.getConfigurator());
		backupProvider.getZipBackup(fos);		
	}

}
