package br.tec.jsonprevayler.migrations;

import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.migrations.operations.MigrationExecuter;
import br.tec.jsonprevayler.migrations.operations.RenameClass;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;

public class MigrationOperationFactory {

	public static MigrationExecuter getOperationExecuter(MigrationInstruction migrationInstruction, FileCore fileCore) throws ValidationPrevalenceException {
		if (migrationInstruction == null) {
			throw new ValidationPrevalenceException("MigrationInstruction is null!");
		}
		if ((migrationInstruction.getOperation() == null) || (migrationInstruction.getOperation().trim().isEmpty())) {
			throw new ValidationPrevalenceException("Operation is not seted in migrationInstruction!");
		}
		if (RenameClass.class.getSimpleName().equals(migrationInstruction.getOperation())) {
			return new RenameClass(migrationInstruction, fileCore);
		}
		
		throw new ValidationPrevalenceException(migrationInstruction.toString() + " Invalid Operation!");
	}
	
}
