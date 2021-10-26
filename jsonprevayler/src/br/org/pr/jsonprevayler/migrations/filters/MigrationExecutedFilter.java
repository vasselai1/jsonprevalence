package br.org.pr.jsonprevayler.migrations.filters;

import java.util.Comparator;

import br.org.pr.jsonprevayler.PrevalentRepository;
import br.org.pr.jsonprevayler.entity.MigrationExecution;
import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.search.PrevalenceFilter;
import br.org.pr.jsonprevayler.search.ProgressSearchObserver;

public class MigrationExecutedFilter implements PrevalenceFilter<MigrationExecution> {

	private Comparator<MigrationExecution> comparator = new Comparator<MigrationExecution>() {
		@Override
		public int compare(MigrationExecution m1, MigrationExecution m2) {
			return m1.getLineNuber().compareTo(m2.getLineNuber());
		}
	};

	@Override
	public boolean isAcepted(MigrationExecution entity) {
		return true;
	}

	@Override
	public Class<? extends PrevalenceEntity> getClasse() {
		return MigrationExecution.class;
	}

	@Override
	public Comparator<MigrationExecution> getComparator() {
		return comparator;
	}

	@Override
	public ProgressSearchObserver<MigrationExecution> getProgressSearchObserver() {
		return null;
	}

	@Override
	public int getFirstResult() {
		return 0;
	}

	@Override
	public int getPageSize() {
		return 0;
	}

	@Override
	public void setTotal(int total) {
		
	}

	@Override
	public void setPrevalenceInstance(PrevalentRepository pojoJsonRepository) {
		
	}

}
