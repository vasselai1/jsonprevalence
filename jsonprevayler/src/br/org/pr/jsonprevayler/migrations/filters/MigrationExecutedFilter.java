package br.org.pr.jsonprevayler.migrations.filters;

import java.util.Comparator;

import br.org.pr.jsonprevayler.PrevalentRepository;
import br.org.pr.jsonprevayler.entity.MigrationExecution;
import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemorySearchEngineInterface;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceFilter;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceSearchFilter;
import br.org.pr.jsonprevayler.searchfilter.ProgressSearchObserver;

public class MigrationExecutedFilter extends PrevalenceSearchFilter<MigrationExecution> {

	private static Comparator<MigrationExecution> comparator = new Comparator<MigrationExecution>() {
		@Override
		public int compare(MigrationExecution m1, MigrationExecution m2) {
			return m1.getLineNuber().compareTo(m2.getLineNuber());
		}
	};	
	
	public MigrationExecutedFilter() {
		super(comparator);
	}

	@Override
	public boolean isAcepted(MigrationExecution entity) {
		return true;
	}

	@Override
	public Class<? extends PrevalenceEntity> getClasse() {
		return MigrationExecution.class;
	}

	@Override
	public ProgressSearchObserver<MigrationExecution> getProgressSearchObserver() {
		return null;
	}

	@Override
	public void setMemorySearchEngine(MemorySearchEngineInterface pojoJsonRepository) {
		// TODO Auto-generated method stub
		
	}

}
