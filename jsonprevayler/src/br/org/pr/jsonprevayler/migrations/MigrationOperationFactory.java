package br.org.pr.jsonprevayler.migrations;

import br.org.pr.jsonprevayler.PrevalentJsonRepository;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.migrations.operations.MigrationExecuter;
import br.org.pr.jsonprevayler.migrations.operations.RenameEntity;

public class MigrationOperationFactory {

	public static MigrationExecuter getOperationExecuter(MigrationInstruction migrationInstruction, PrevalentJsonRepository prevalence) throws ValidationPrevalenceException {
		if (migrationInstruction == null) {
			throw new ValidationPrevalenceException("MigrationInstruction is null!");
		}
		if ((migrationInstruction.getOperation() == null) || (migrationInstruction.getOperation().trim().isEmpty())) {
			throw new ValidationPrevalenceException("Operation is not seted in migrationInstruction!");
		}
		if (RenameEntity.class.getSimpleName().equals(migrationInstruction.getOperation())) {
			return new RenameEntity(migrationInstruction, prevalence);
		}
		
		throw new ValidationPrevalenceException(migrationInstruction.toString() + " Invalid Operation!");
	}
	
}