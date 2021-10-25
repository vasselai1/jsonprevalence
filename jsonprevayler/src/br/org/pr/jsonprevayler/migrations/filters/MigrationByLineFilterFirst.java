package br.org.pr.jsonprevayler.migrations.filters;

import br.org.pr.jsonprevayler.entity.MigrationExecution;
import br.org.pr.jsonprevayler.search.FilterFirst;

public class MigrationByLineFilterFirst implements FilterFirst<MigrationExecution> {

	private Integer lineNumber;
		
	public MigrationByLineFilterFirst(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public boolean isAcepted(MigrationExecution entity) {
		return entity.getLineNuber().equals(lineNumber);
	}
	
}
