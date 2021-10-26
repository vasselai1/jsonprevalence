package br.org.pr.jsonprevayler.migrations.operations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import br.org.pr.jsonprevayler.PrevalentRepository;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.migrations.MigrationInstruction;

public class RenameEntity extends MigrationExecuter {

	private String actualQualifiedClassNameOfPojoEntity = null;
	private String newQualifiedClassNameOfPojoEntity = null;
	private String actualFilesPrefix = null;
	private String newFilesPrexis = null; 
	
	public RenameEntity(MigrationInstruction migrationInstruction, PrevalentRepository prevalence) {
		super(migrationInstruction, prevalence);
	}

	private void initParameters() {
		actualQualifiedClassNameOfPojoEntity = migrationInstruction.getParameters()[0];
		newQualifiedClassNameOfPojoEntity = migrationInstruction.getParameters()[1];
		actualFilesPrefix = getPrefix(actualQualifiedClassNameOfPojoEntity); 
		newFilesPrexis = getPrefix(newQualifiedClassNameOfPojoEntity);	
	}
	
	private void validateFutureDirName(String newQualifiedClassNameOfPojoEntity) throws IOException, ValidationPrevalenceException {
		File futureDir = getDir(newQualifiedClassNameOfPojoEntity);
		if (futureDir.exists()) {
			throw new ValidationPrevalenceException("The directory "  + newQualifiedClassNameOfPojoEntity + " already exists! Please delete manually after backup.");
		}
	}
	
	private void validateAllFilesNamesInDir(String actualQualifiedClassNameOfPojoEntity, String actualFilesPrefix, String newFilesPrexis) throws IOException, ValidationPrevalenceException {
		StringBuilder resultValidation = new StringBuilder();
		String prefix = getPrefix(actualQualifiedClassNameOfPojoEntity);
		for(File jsonFile : listAllFiles(actualQualifiedClassNameOfPojoEntity)) {
			if (!jsonFile.getName().startsWith(prefix)) {
				resultValidation.append("The file ").append(jsonFile.getName()).append(" don't respect name pattern! Please rename manually after backup.");
			}
		}
		if (!resultValidation.toString().isEmpty()) {
			throw new ValidationPrevalenceException(resultValidation.toString());
		}
	}
	
	private void renameClassNameInJson(String actualName, String newName, File jsonFile) throws IOException {
		String textFile = Files.readString(jsonFile.toPath());
		String newTextFile = textFile.replace(actualName, newName);
		Files.write(jsonFile.toPath(), newTextFile.getBytes());
	}
	
	private void renameDir(String actualName, String newName) throws IOException {
		File dir = getDir(actualName);
		File newDir = new File(dir.getParent(), newName);
		dir.renameTo(newDir);
	}
	
	private void renameFileName(String actualFilesPrefix, String newFilesPrefix, File jsonFile) {
		String actualFileName = jsonFile.getName();
		String newFileName = actualFileName.replace(actualFilesPrefix, newFilesPrefix);
		File newFile = new File(jsonFile.getParent(), newFileName);
		jsonFile.renameTo(newFile);
	}	
	
	private String getPrefix(String canonicalEntityName) {
		return canonicalEntityName.substring(canonicalEntityName.lastIndexOf("."));
	}
	
	@Override
	protected void validateSpecificParameters() throws IOException, ValidationPrevalenceException {
		String actualQualifiedClassNameOfPojoEntity = migrationInstruction.getParameters()[0];
		if ((actualQualifiedClassNameOfPojoEntity == null) || actualQualifiedClassNameOfPojoEntity.trim().isEmpty()) {
			throw new ValidationPrevalenceException(migrationInstruction + " actual qualified name of class pojo entity is not seted!");
		}
		if (migrationInstruction.getParameters().length < 2) {
			throw new ValidationPrevalenceException(migrationInstruction + " new qualified name of class pojo entity is not seted!");
		}
		String newQualifiedClassNameOfPojoEntity = migrationInstruction.getParameters()[1];
		if ((newQualifiedClassNameOfPojoEntity == null) || newQualifiedClassNameOfPojoEntity.trim().isEmpty()) {
			throw new ValidationPrevalenceException(migrationInstruction + " actual qualified name of class pojo Entity is not seted!");
		}
		String actualFilesPrefix = getPrefix(actualQualifiedClassNameOfPojoEntity); 
		String newFilesPrexis = getPrefix(newQualifiedClassNameOfPojoEntity);	
		validateActualDirName(actualQualifiedClassNameOfPojoEntity);
		validateAllFilesNamesInDir(actualQualifiedClassNameOfPojoEntity, actualFilesPrefix, newFilesPrexis);
		validateFutureDirName(newQualifiedClassNameOfPojoEntity);
	}
		
	@Override
	public void execute() throws IOException, ValidationPrevalenceException {
		validateSpecificParameters();
		initParameters();
		for (File jsonFile : listAllFiles(actualQualifiedClassNameOfPojoEntity)) {
			renameClassNameInJson(actualQualifiedClassNameOfPojoEntity, newQualifiedClassNameOfPojoEntity, jsonFile);
			renameFileName(actualFilesPrefix, newFilesPrexis, jsonFile);
		}
		renameDir(actualQualifiedClassNameOfPojoEntity, newQualifiedClassNameOfPojoEntity);		
	}

	@Override
	public String getDirToSecurityCopy() {
		return actualQualifiedClassNameOfPojoEntity;
	}

}