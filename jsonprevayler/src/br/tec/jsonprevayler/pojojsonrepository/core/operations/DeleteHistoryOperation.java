package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.File;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.util.LoggerUtil;

public class DeleteHistoryOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {
	
	private Class<T> classe;
	private Class<T> classeInternal;
	private Long id;
	
	public DeleteHistoryOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);		
	}
	
	public DeleteHistoryOperation<T> set(Class<T> classe, Long id) {
		this.classe = classe;
		this.id = id;
		return this;
	}
	
	public void execute() throws ValidationPrevalenceException, InternalPrevalenceException {
		classeInternal = getClassRepository(classe);
		if (memoryCore.getPojo(classeInternal, id) != null) {
			throw new ValidationPrevalenceException("Please delete entity " + classe.getSimpleName() + " id = " + id + " before!");
		}
		List<File> historyFiles = fileCore.listVersions(classeInternal, id);
		if (historyFiles.isEmpty()) {
			throw new ValidationPrevalenceException("History for entity " + classe.getSimpleName() + " id = " + id + " not found!");
		} 
		for (File fileLoop : historyFiles) {
			if (!fileLoop.delete()) {
				LoggerUtil.error(logger, new InternalPrevalenceException("Erro deleting file " + fileLoop.getName()), "Error while deleting history from entity class %1$s, id = %2$d", classe, id);
			}
		}
	}

	@Override
	public String getOperationName() {
		return "DeleteHistoryAndDetailsOperation_" + classe.getSimpleName() + "_" + id;
	}
	
}