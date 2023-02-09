package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.HistoryWriter;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.util.LoggerUtil;

public class ListVersionsOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {

	private static final SimpleDateFormat SDF_HISTORY = HistoryWriter.SDF_HISTORY;
	
	private Class<T> classe;
	private Class<T> classeInternal;
	private Long id;
	
	public ListVersionsOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);
	}
	
	public ListVersionsOperation<T> set(Class<T> classe, Long id) {
		this.classe = classe;
		this.id = id;
		return this;
	}
	
	public Date getLast() throws InternalPrevalenceException, ValidationPrevalenceException {		
		return new TreeSet<Date>(list().keySet()).last();
	}
	
	public Map<Date, String> list() throws InternalPrevalenceException, ValidationPrevalenceException {
		classeInternal = getClassRepository(classe);
		initState();		
		writeOperationDetail(INTERNAL_CLASS, classeInternal.getCanonicalName());
		Map<Date, String> versionsMap = new HashMap<Date, String>();
		try {
			List<File> entityFiles = fileCore.listVersions(classeInternal, id);
			if (entityFiles.isEmpty()) {
				throw new ValidationPrevalenceException("File sytem do not contain versions from class " + classeInternal + " and id = " + id);
			}
			for (File fileVersion: entityFiles) {
				Date dateVersion = getDateVersionName(fileVersion.getName());
				try {
					String json = Files.readString(fileVersion.toPath());
					versionsMap.put(dateVersion, json);
				} catch (Exception ex) {
					throw LoggerUtil.error(logger, ex, "Error reading file %1$s from class %2$s and id = %3$d", fileVersion.getName(), classeInternal.getSimpleName(), id);
				}
			}
		} catch (Exception e) {
			writeOperationDetail(ERROR, e.getMessage());
			throw LoggerUtil.error(logger, e, "Error in List versions for entity = %1$s, id = %2$d", classeInternal.getCanonicalName(), id);
		} finally {
			updateState(OperationState.FINALIZED);
		}
		return versionsMap;
	}
	
	private Date getDateVersionName(String fileName) throws InternalPrevalenceException {
		String prefix = classeInternal.getSimpleName() + "_" + id + "_";
		String sufix = ".json";
		String dateName = fileName.replace(prefix, "").replace(sufix, "");
		try {
			return SDF_HISTORY.parse(dateName);
		} catch (Exception ex) {
			throw LoggerUtil.error(logger, ex, "Error converting date version from file %1$s from class %2$s and id = %3$d", fileName, classeInternal.getSimpleName(), id);
		}
	}

	@Override
	public String getOperationName() {
		return "ListVersionsOperation_" + classe + "_" + id;
	}
	
}