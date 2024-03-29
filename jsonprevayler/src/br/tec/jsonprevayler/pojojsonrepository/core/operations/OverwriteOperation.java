package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.HistoryWriter;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.util.LoggerUtil;
import flexjson.JSONDeserializer;

public class OverwriteOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {

	private static final SimpleDateFormat SDF_HISTORY = HistoryWriter.SDF_HISTORY;
	
	private UpdateOperation<T> updateOperation;
	private Class<T> classe;
	private Class<T> classeInternal;
	private Long id;
	private Date dateVersion;	
	private ListVersionsOperation<T> listVersionsOperation;
	
	public OverwriteOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);
		updateOperation = new UpdateOperation<T>(prevalenceConfigurator,sequenceUtil, memoryCore, fileCore);
		updateOperation.setIsOverwrite(true);
		listVersionsOperation = new ListVersionsOperation<T>(prevalenceConfigurator, sequenceUtil, memoryCore, fileCore);
	}
	
	public OverwriteOperation<T> set(Class<T> classe, Long id, Date dateVersion) {
		this.classe = classe;
		this.id = id;
		this.dateVersion = dateVersion;
		return this;
	}
	
	public void execute() throws ValidationPrevalenceException, InternalPrevalenceException {
		classeInternal = getClassRepository(classe);
		initState();
		writeOperationDetail(INTERNAL_CLASS, classeInternal.getCanonicalName());
		File selectedFile = null;
		try {
			if (dateVersion == null) {
				dateVersion = listVersionsOperation.getLast();
			}
			List<File> entityFiles = fileCore.listVersions(classeInternal, id);
			selectedFile = getFile(entityFiles);
			T entity = null;
			try {
				entity = new JSONDeserializer<T>().deserialize(Files.readString(selectedFile.toPath()));
			} catch (Exception e) {
				throw LoggerUtil.error(logger, e, "Error while deserialize entity = %1$s, id = %2$d, file = %3$s! Verify compatibilty whitch actual POJO.", classeInternal, id, selectedFile.getName());
			}
			updateOperation.set(entity).execute();
		} catch (Exception e) {
			writeOperationDetail(ERROR, e.getMessage());
			throw LoggerUtil.error(logger, e, "Error while overwrite entity entity = %1$s, id = %2$d, file = %3$s", classeInternal, id, selectedFile.getName());
		} finally {
			updateState(OperationState.FINALIZED);
		}
	}
	
	private File getFile(List<File> entityFiles) throws ValidationPrevalenceException {
		String dateVersionText = SDF_HISTORY.format(dateVersion);
		String fileName = classeInternal.getSimpleName() + "_" + id + "_" + dateVersionText + ".json";
		for (File fileVersionLoop : entityFiles) {
			if (fileVersionLoop.getName().equals(fileName)) {
				return fileVersionLoop;
			}
		}
		throw new ValidationPrevalenceException(fileName + " not found!");
	}
	
	@Override
	public String getOperationName() {		
		return "OverwriteOperation_" + classe + "_" + id + "_" + SDF_HISTORY.format(dateVersion);
	}
	
}