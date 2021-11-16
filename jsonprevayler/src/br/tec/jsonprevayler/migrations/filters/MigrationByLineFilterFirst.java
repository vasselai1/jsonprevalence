package br.tec.jsonprevayler.migrations.filters;

import br.tec.jsonprevayler.entity.MigrationExecution;
import br.tec.jsonprevayler.searchfilter.FilterFirst;

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
