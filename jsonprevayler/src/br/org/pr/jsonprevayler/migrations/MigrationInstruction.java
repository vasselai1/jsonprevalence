package br.org.pr.jsonprevayler.migrations;

import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;

public class MigrationInstruction {
	
	private static final String FORMAT_EXAMPLE = "! Line example=operacao:parameter1,parameter2,parameterx..."; 
	
	private Integer lineNumber;
	private String operation;
	private String[] parameters;
	private String lineText;
	
	public static MigrationInstruction convert(Integer lineCounter, String migrationLine) throws ValidationPrevalenceException {
		if ((migrationLine == null) || migrationLine.trim().isEmpty()) {
			throw new ValidationPrevalenceException("Migration line is empty" + FORMAT_EXAMPLE);
		}
		if ((lineCounter == null) || (lineCounter < 0)) {
			throw new ValidationPrevalenceException("Migration line whitout lineCounter!");
		}
		String[] lineOperationArray = migrationLine.split(":");
		String operation = lineOperationArray[0].trim().toLowerCase();
		if (operation.isEmpty()) {
			throw new ValidationPrevalenceException("Operation not seted in line " + lineCounter + FORMAT_EXAMPLE);
		}
		String[] parameters = migrationLine.split(",");
		if (parameters.length < 1) {
			throw new ValidationPrevalenceException("No parameters seted in line " + lineCounter + FORMAT_EXAMPLE);
		}
		int parameterCount = 0;
		for (String parameter : parameters) {
			if (parameter.trim().isEmpty()) {
				throw new ValidationPrevalenceException("Parameter "  + parameterCount + "not seted in line " + lineCounter + FORMAT_EXAMPLE);
			}
			parameters[0] = parameter.trim();
			parameterCount++;
		}
		return new MigrationInstruction(lineCounter, operation, parameters, migrationLine);
	}
	
	private MigrationInstruction(Integer lineNumber, String operation, String[] parameters, String lineText) {
		this.lineNumber = lineNumber;
		this.operation = operation;
		this.parameters = parameters;
		this.lineText = lineText;
	}
	
	public Integer getLineNumber() {
		return lineNumber;
	}
	public String getOperation() {
		return operation;
	}
	public String[] getParameters() {
		return parameters;
	}
	public String getLineText() {
		return lineText;
	}

	@Override
	public String toString() {
		return "MigrationInstruction [lineNumber=" + lineNumber + ", lineText=" + lineText + "]";
	}
	
}