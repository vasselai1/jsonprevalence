package br.org.pr.jsonprevayler.migrations.operations;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;

import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.migrations.MigrationInstruction;
import br.org.pr.jsonprevayler.pojojsonrepository.core.FileCore;

public abstract class MigrationExecuter {
	
	protected final String FS = File.separator;
	protected final FileCore fileCore;
	protected final MigrationInstruction migrationInstruction;
	protected final Date instanciationMoment = new Date();
	
	protected final FilenameFilter jsonFilterFileName = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".json");
		}
	};
	
	protected MigrationExecuter(MigrationInstruction migrationInstruction, FileCore fileCore) {
		this.migrationInstruction = migrationInstruction;
		this.fileCore = fileCore;
	}
	
	public void validate() throws IOException, ValidationPrevalenceException {
		if (migrationInstruction == null) {
			throw new ValidationPrevalenceException("Migrations instruction is null!");
		}
		if (migrationInstruction.getParameters() == null) {
			throw new ValidationPrevalenceException(migrationInstruction + " not contain parameters!");
		}
		if (migrationInstruction.getParameters().length == 0) {
			throw new ValidationPrevalenceException(migrationInstruction + " not contain parameters!");
		}
		validateSpecificParameters();
	}

	protected void validateActualDirName(String actualQualifiedClassNameOfPojoEntity) {
		
	}	
	
	protected abstract void validateSpecificParameters() throws IOException, ValidationPrevalenceException;
	
	public abstract void execute() throws IOException, ValidationPrevalenceException;
	
	public  abstract String getDirToSecurityCopy();
	
	protected File[] listAllFiles(String canonicalClassEntityName) throws IOException {
		return fileCore.getFilePath(canonicalClassEntityName).listFiles(jsonFilterFileName);
	}  

	protected File getDir(String canonicalClassEntityName) throws IOException {
		return fileCore.getFilePath(canonicalClassEntityName);
	}
	
	public MigrationInstruction getMigrationInstruction() {
		return migrationInstruction;
	}
	
	public Date getInstanciationMoment() {
		return instanciationMoment;
	} 
}
