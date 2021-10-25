package br.org.pr.jsonprevayler.infrastrutuctre;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.org.pr.jsonprevayler.PrevalentJsonRepository;
import br.org.pr.jsonprevayler.entity.MigrationExecution;
import br.org.pr.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.org.pr.jsonprevayler.exceptions.InternalPrevalenceException;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.migrations.MigrationInstruction;
import br.org.pr.jsonprevayler.migrations.MigrationOperationFactory;
import br.org.pr.jsonprevayler.migrations.SecurityMigrationCopy;
import br.org.pr.jsonprevayler.migrations.filters.MigrationByLineFilterFirst;
import br.org.pr.jsonprevayler.migrations.filters.MigrationExecutedFilter;
import br.org.pr.jsonprevayler.migrations.operations.MigrationExecuter;

public class MigrationProvider {

	private enum ReadType {
		ALL,
		EXECUTED,
		PENDING;
	}

	private final PrevalentJsonRepository<MigrationExecution> prevalence;
	private final String FS = File.separator;
	private Set<SecurityMigrationCopy> securityCopies = new TreeSet<SecurityMigrationCopy>();
	
	public MigrationProvider(String systemPath, String systemName) {
		this.prevalence = new PrevalentJsonRepository<MigrationExecution>(systemPath, systemName);
	}

	public List<MigrationInstruction> readMigrationsFile(InputStream inputStreamMigrationsFile, ReadType readType) throws IOException, ClassNotFoundException, InterruptedException, ValidationPrevalenceException {
		List<MigrationInstruction> migrationsInstrutctions = new ArrayList<MigrationInstruction>();		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamMigrationsFile));
		MigrationExecution lastMigration = null;		
		if (ReadType.PENDING.equals(readType)) {
			lastMigration = getLastExecutedMigration();
		}
		String line;
		int lineCounter = 0; 
		while((line = reader.readLine()) != null) {
			lineCounter++;
			if (line.trim().isEmpty()) {
				continue;
			}
			if (ReadType.ALL.equals(readType)) {
				migrationsInstrutctions.add(MigrationInstruction.convert(lineCounter, line));				
			}
			if (ReadType.EXECUTED.equals(readType) && IsExecuted(lineCounter)) {				
				migrationsInstrutctions.add(MigrationInstruction.convert(lineCounter, line));
			}
			if (ReadType.PENDING.equals(readType) && isPendingLine(lastMigration, lineCounter)) {
				migrationsInstrutctions.add(MigrationInstruction.convert(lineCounter, line));
			}
		}
		return migrationsInstrutctions;
	} 
	
	public boolean isPendingLine(MigrationExecution lastMigration, int lineCounter) {
		int lastLine =  (lastMigration != null) ? lastMigration.getLineNuber() : 0;
		return lineCounter > lastLine;
	}
	
	public boolean IsExecuted(Integer lineNumber) throws ClassNotFoundException, IOException, ValidationPrevalenceException {		
		return (getLine(lineNumber) != null);
	}
	
	private MigrationExecution getLine(Integer lineNumber) throws ClassNotFoundException, IOException, ValidationPrevalenceException {
		if (lineNumber == null) {
			return null;
		}
		return prevalence.getPojo(MigrationExecution.class, new MigrationByLineFilterFirst(lineNumber));
	}
	
	public void validate(InputStream inputStreamMigrationsFile) throws ValidationPrevalenceException, IOException, ClassNotFoundException, InterruptedException {		
		List<MigrationExecution> migrationsExecuteds = prevalence.listPojo(MigrationExecution.class, new MigrationExecutedFilter());
		List<MigrationInstruction> migrationsIntructions = readMigrationsFile(inputStreamMigrationsFile, ReadType.ALL);
		for (MigrationExecution migrationExecuted : migrationsExecuteds) {
			MigrationInstruction migrationInstruction = getMigrationInstruction(migrationExecuted.getLineNuber(), migrationsIntructions);
			if (migrationInstruction == null) {
				throw new ValidationPrevalenceException("Migration Executed " + migrationExecuted.getId() + " : " + migrationExecuted.getOriginalLine() + " not present in migrations file!");
			}
			if (migrationExecuted.getOriginalLine().equals(migrationInstruction.getLineText())) {
				throw new ValidationPrevalenceException("Migration Executed " + migrationExecuted.getId() + " : " + migrationExecuted.getOriginalLine() + " is different of line " + migrationInstruction.getLineNumber() + ":" + migrationInstruction.getLineText() + "!");
			}
		}
		validateOrder(migrationsExecuteds, migrationsIntructions);
		for (MigrationInstruction migrationInstruction : migrationsIntructions) {
			if (isExecuted(migrationInstruction.getLineNumber(), migrationsExecuteds)) {
				continue;
			}
			MigrationOperationFactory.getOperationExecuter(migrationInstruction, prevalence).validate();
		}
	}
	
	private void validateOrder(List<MigrationExecution> migrationsExecuteds, List<MigrationInstruction> migrationsIntructions) throws ValidationPrevalenceException {
		if (migrationsIntructions == null) {
			return;
		}
		if (migrationsExecuteds == null) {
			return;
		}
		Integer lastLineExecuted = null;
		for (MigrationInstruction migrationInstruction : migrationsIntructions) {
			if (isExecuted(migrationInstruction.getLineNumber(), migrationsExecuteds)) {				 
				if (lastLineExecuted != null) {
					int diff = migrationInstruction.getLineNumber() - lastLineExecuted;
					if (diff > 1) {
						throw new ValidationPrevalenceException(diff + " instructions not executed before " + migrationInstruction + "! Please renegerate migrations file."); 
					} 
				}
				lastLineExecuted = migrationInstruction.getLineNumber();
			}
		}
	}
	
	private boolean isExecuted(Integer line, List<MigrationExecution> migrationsExecuteds) {
		if (migrationsExecuteds == null) {
			return false;
		}
		for (MigrationExecution migrationExecuted : migrationsExecuteds) {
			if (migrationExecuted.getLineNuber().equals(line)) {
				return true;
			}
		}
		return false;
	}
	
	public String regenerateFile() throws ClassNotFoundException, IOException, InterruptedException, ValidationPrevalenceException {
		StringBuilder textInstructionForFile = new StringBuilder();
		List<MigrationExecution> migrationsExecuteds = prevalence.listPojo(MigrationExecution.class, new MigrationExecutedFilter());
		for (MigrationExecution migrationExecuted : migrationsExecuteds) {
			textInstructionForFile.append(migrationExecuted.getOriginalLine()).append(FS);
		}
		return textInstructionForFile.toString();
	}
	
	private MigrationInstruction getMigrationInstruction(Integer lineNumber, List<MigrationInstruction> migrationsIntructions) {
		if (migrationsIntructions == null) {
			return null;
		}
		for (MigrationInstruction migratioInstruction : migrationsIntructions) {
			if (migratioInstruction.getLineNumber().equals(lineNumber)) {
				return migratioInstruction;
			}
		}
		return null;
	}
	
	public void migrate(InputStream inputStreamMigrationsFile) throws ValidationPrevalenceException, IOException, ClassNotFoundException, InternalPrevalenceException, InterruptedException {
		if (PrevalentJsonRepository.isMainInitialized()) {
			throw new ValidationPrevalenceException("When PrevalenceJsonRepository is initialized migrations don't be started! Please stop aplication before run migrations.");
		}
		validate(inputStreamMigrationsFile);
		List<MigrationInstruction> pendingMigrationsInstrucions = listPending(inputStreamMigrationsFile);		
		List<MigrationExecuter> newMigrationsExecuteds = new ArrayList<MigrationExecuter>();
		prevalence.stopForMaintenance();
		for (MigrationInstruction migrationInstruction : pendingMigrationsInstrucions) {
			MigrationExecuter migrationExecuter = MigrationOperationFactory.getOperationExecuter(migrationInstruction, prevalence);
			try {
				addSecurityCopy(migrationExecuter);
				migrationExecuter.execute();
				newMigrationsExecuteds.add(migrationExecuter);
			} catch (Exception e) {
				restoreSecurityCopies();
				deleteSecurityCopies();
				prevalence.startPosMaintenance();
				throw new InternalPrevalenceException("Error in migration execution " + migrationExecuter.getMigrationInstruction() + " undo is executed!", e);
			}
		}
		prevalence.startPosMaintenance();
		try {
			saveExecutedMigrations(newMigrationsExecuteds);
		} catch (Exception e) {
			try {
				prevalence.stopForMaintenance();
				restoreSecurityCopies();
				deleteSecurityCopies();	
			} catch (Exception e2) {
				throw new InternalPrevalenceException("Error in restore and delete securities copies!", e);
			} finally {
				prevalence.startPosMaintenance();
			}
			throw new InternalPrevalenceException("Error in save executed migrations undo is executed!", e);
		}
		deleteSecurityCopies();
	}

	private void saveExecutedMigrations(List<MigrationExecuter> newMigrationsExecuteds) throws IOException, ValidationPrevalenceException, NoSuchAlgorithmException, ClassNotFoundException, InternalPrevalenceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, DeprecatedPrevalenceEntityVersionException {
		for (MigrationExecuter executed : newMigrationsExecuteds) {
			saveExecutedMigration(executed);
		}
	}
	
	private void saveExecutedMigration(MigrationExecuter executed) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, InternalPrevalenceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, DeprecatedPrevalenceEntityVersionException {
		MigrationExecution migrationExecution = new MigrationExecution();
		migrationExecution.setDate(executed.getInstanciationMoment());
		migrationExecution.setLineNuber(executed.getMigrationInstruction().getLineNumber());
		migrationExecution.setOriginalLine(executed.getMigrationInstruction().getLineText());
		prevalence.save(migrationExecution);
	}
	
	private void addSecurityCopy(MigrationExecuter migrationExecuter) throws IOException {
		if (migrationExecuter == null) {
			return;
		}
		String dirName = migrationExecuter.getDirToSecurityCopy();
		Integer line = migrationExecuter.getMigrationInstruction().getLineNumber();
		String temporaryName = dirName + "_tempcopy";
		File directory = prevalence.getFilePath(dirName);
		File newDiretory = prevalence.getFilePath(temporaryName);
		SecurityMigrationCopy securityMigrationCopy = new SecurityMigrationCopy(dirName, line, directory, false, temporaryName, newDiretory);
		if (!securityCopies.contains(securityMigrationCopy)) {
			securityCopies.add(securityMigrationCopy);
			Files.copy(directory.toPath(), newDiretory.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	private void deleteSecurityCopies() {
		for (SecurityMigrationCopy securityMigrationCopy : securityCopies) {
			securityMigrationCopy.getNewDiretory().delete();
		}
	}
	
	private void restoreSecurityCopies() throws IOException {
		for (SecurityMigrationCopy securityMigrationCopy : securityCopies) {
			File directory = securityMigrationCopy.getDirectory();
			directory.delete();
			File newDiretory = securityMigrationCopy.getNewDiretory();
			Files.copy(newDiretory.toPath(), directory.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	public List<MigrationInstruction> listPending(InputStream inputStreamMigrationsFile) throws ValidationPrevalenceException, IOException, ClassNotFoundException, InterruptedException {
		return readMigrationsFile(inputStreamMigrationsFile, ReadType.PENDING);
	}

	public List<MigrationInstruction> listExecuted(InputStream inputStreamMigrationsFile) throws ValidationPrevalenceException, IOException, ClassNotFoundException, InterruptedException {
		return readMigrationsFile(inputStreamMigrationsFile, ReadType.EXECUTED);
	}
	
	public String getStatus() throws ClassNotFoundException, IOException, InterruptedException, ValidationPrevalenceException {
		MigrationExecution lastMigration = getLastExecutedMigration();
		return (lastMigration != null) ? lastMigration.toString() : "Never executed";
	}
	
	public MigrationExecution getLastExecutedMigration() throws ClassNotFoundException, IOException, InterruptedException, ValidationPrevalenceException {
		MigrationExecution migration = null;
		List<MigrationExecution> migrations = prevalence.listPojo(MigrationExecution.class, new MigrationExecutedFilter());
		for (MigrationExecution migrationLoop : migrations) {
			migration = migrationLoop;
		}
		return migration;
	}
	
}