package br.org.pr.jsonprevayler.migrations.operations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.migrations.MigrationInstruction;
import br.org.pr.jsonprevayler.pojojsonrepository.core.FileCore;

public class RenameAtribute extends MigrationExecuter {

	private String actualQualifiedClassNameOfPojoEntity = null;
	private String actualAtributeName = null;
	private String newAtributeName = null;
	
	public RenameAtribute(MigrationInstruction migrationInstruction, FileCore fileCore) {
		super(migrationInstruction, fileCore);
	}

	private void initParameters() {
		actualQualifiedClassNameOfPojoEntity = migrationInstruction.getParameters()[0];
		actualAtributeName = migrationInstruction.getParameters()[1];
		actualAtributeName = migrationInstruction.getParameters()[2];
	}
	
	private void validateNewAtributeName(String actualQualifiedClassNameOfPojoEntity, String newAtributeName) throws IOException, ValidationPrevalenceException {
		StringBuilder resultValidation = new StringBuilder();
		for(File jsonFile : listAllFiles(actualQualifiedClassNameOfPojoEntity)) {
			String textFile = Files.readString(jsonFile.toPath());
			if (textFile.contains(newAtributeName)) {
				resultValidation.append(FS);
				resultValidation.append("The file ").append(jsonFile.getName()).append(" already contain new atribute ").append(newAtributeName).append("! Please rename to old name manually after backup.");
			}
		}
		if (!resultValidation.toString().isEmpty()) {
			throw new ValidationPrevalenceException(resultValidation.toString());
		}
	}
	
	private void renameAtributeNameInJson(String actualAtributeName, String newAtributeName, File jsonFile) throws IOException {
		String textFile = Files.readString(jsonFile.toPath());
		String newTextFile = textFile.replace(actualAtributeName, newAtributeName);
		Files.write(jsonFile.toPath(), newTextFile.getBytes());
	}
	
	@Override
	protected void validateSpecificParameters() throws IOException, ValidationPrevalenceException {
		String actualQualifiedClassNameOfPojoEntity = migrationInstruction.getParameters()[0];
		if ((actualQualifiedClassNameOfPojoEntity == null) || actualQualifiedClassNameOfPojoEntity.trim().isEmpty()) {
			throw new ValidationPrevalenceException(migrationInstruction + " actual qualified name of class pojo entity is not seted!");
		}
		if (migrationInstruction.getParameters().length < 2) {
			throw new ValidationPrevalenceException(migrationInstruction + " actual atribute name is not seted!");
		}
		String actualAtributeName = migrationInstruction.getParameters()[1];
		if ((actualAtributeName == null) || actualAtributeName.trim().isEmpty()) {
			throw new ValidationPrevalenceException(migrationInstruction + " actual atribute name is not seted!");
		}
		if (migrationInstruction.getParameters().length < 3) {
			throw new ValidationPrevalenceException(migrationInstruction + " new atribute name is not seted!");
		}
		String newAtributeName = migrationInstruction.getParameters()[1];
		if ((newAtributeName == null) || newAtributeName.trim().isEmpty()) {
			throw new ValidationPrevalenceException(migrationInstruction + " new atribute name is not seted!");
		}
		validateActualDirName(actualQualifiedClassNameOfPojoEntity);		
		validateNewAtributeName(actualQualifiedClassNameOfPojoEntity, newAtributeName);
	}
		
	@Override
	public void execute() throws IOException, ValidationPrevalenceException {
		validateSpecificParameters();
		initParameters();
		for (File jsonFile : listAllFiles(actualQualifiedClassNameOfPojoEntity)) {
			renameAtributeNameInJson(actualAtributeName, newAtributeName, jsonFile);
		}		
	}

	@Override
	public String getDirToSecurityCopy() {
		return actualQualifiedClassNameOfPojoEntity;
	}
	
}