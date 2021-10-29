package br.org.pr.jsonprevayler.migrations;

import br.org.pr.jsonprevayler.PrevalentRepository;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.migrations.operations.MigrationExecuter;
import br.org.pr.jsonprevayler.migrations.operations.RenameClass;

public class MigrationOperationFactory {

	public static MigrationExecuter getOperationExecuter(MigrationInstruction migrationInstruction, PrevalentRepository prevalence) throws ValidationPrevalenceException {
		if (migrationInstruction == null) {
			throw new ValidationPrevalenceException("MigrationInstruction is null!");
		}
		if ((migrationInstruction.getOperation() == null) || (migrationInstruction.getOperation().trim().isEmpty())) {
			throw new ValidationPrevalenceException("Operation is not seted in migrationInstruction!");
		}
		if (RenameClass.class.getSimpleName().equals(migrationInstruction.getOperation())) {
			return new RenameClass(migrationInstruction, prevalence);
		}
		
		throw new ValidationPrevalenceException(migrationInstruction.toString() + " Invalid Operation!");
	}
	
}
