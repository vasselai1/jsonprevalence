package br.tec.jsonprevayler.pojojsonrepository.core;

import java.util.Date;
import java.util.List;
import java.util.Map;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.PrevalenceChangeObserver;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.CustomOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.DeleteHistoryAndDetailsOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.DeleteOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.ListVersionsOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.OverwriteOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.SaveOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.UpdateOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo.CountFilterOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo.CountTotalOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo.FilterFirstJsonOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo.FilterFirstPojoOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo.FilterJsonOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo.FilterPojoOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo.ListTotalJsonOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo.ListTotalPojoOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.josql.JoSqlGroupInMapOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.josql.JoSqlHavingOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.josql.JoSqlListOperation;
import br.tec.jsonprevayler.searchfilter.FilterFirst;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;
import br.tec.jsonprevayler.util.ObjectCopyUtil;

public class OperationsControler <T extends PrevalenceEntity> {
 	
	private final PrevalenceConfigurator prevalenceConfigurator;
	private final FileCore fileCore;
	private final MemoryCore memoryCore;	
	private final SequenceProvider sequenceProvider;

	private final SaveOperation<T> saveOperation;
	private final UpdateOperation<T> updateOperation;
	private final DeleteOperation<T> deleteOperation;
	private final CountFilterOperation<T> countFilterOperation;
	private final CountTotalOperation<T> countTotalOperation;
	private final FilterFirstPojoOperation<T> filterFirstPojoOperation;
	private final FilterFirstJsonOperation<T> filterFirstJsonOperation;
	private final FilterPojoOperation<T> filterPojoOperation;
	private final FilterJsonOperation<T> filterJsonOperation;
	private final ListTotalJsonOperation<T> listTotalJsonOperation;
	private final ListTotalPojoOperation<T> listTotalPojoOperation;
	private final JoSqlListOperation<T> joSqlListOperation;
	private final JoSqlHavingOperation<T> joSqlHavingOperation;
	private final JoSqlGroupInMapOperation<T> joSqlGroupInMapOperation;
	private final ListVersionsOperation<T> listVersionsOperation;
	private final OverwriteOperation<T> overwriteOperation;
	private final DeleteHistoryAndDetailsOperation<T> deleteHistoryAndDetailsOperation;

	public OperationsControler(PrevalenceConfigurator prevalenceConfigurator, SearchProcessorFactory searchProcessorFactory) {
		this.prevalenceConfigurator = prevalenceConfigurator;
		fileCore = new FileCore(prevalenceConfigurator.getPrevalencePath(), prevalenceConfigurator.getSystemName(), prevalenceConfigurator.getNumberOfFilesPerDiretory(), prevalenceConfigurator.getDateProvider());
		memoryCore = new MemoryCore(fileCore);
		sequenceProvider = new SequenceProvider(fileCore.getSystemPath());		
		saveOperation = new SaveOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
		updateOperation = new UpdateOperation<T>(prevalenceConfigurator,sequenceProvider, memoryCore, fileCore);
		deleteOperation = new DeleteOperation<T>(prevalenceConfigurator, memoryCore, fileCore, sequenceProvider);
		countFilterOperation = new CountFilterOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessorFactory.createNewSearchProcessor());
		countTotalOperation = new CountTotalOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessorFactory.createNewSearchProcessor());
		filterFirstPojoOperation = new FilterFirstPojoOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessorFactory.createNewSearchProcessor());
		filterFirstJsonOperation = new FilterFirstJsonOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessorFactory.createNewSearchProcessor());
		filterPojoOperation = new FilterPojoOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessorFactory.createNewSearchProcessor());
		filterJsonOperation = new FilterJsonOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessorFactory.createNewSearchProcessor());
		listTotalJsonOperation = new ListTotalJsonOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessorFactory.createNewSearchProcessor());
		listTotalPojoOperation = new ListTotalPojoOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessorFactory.createNewSearchProcessor());
		joSqlListOperation = new JoSqlListOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
		joSqlHavingOperation = new JoSqlHavingOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
		joSqlGroupInMapOperation = new JoSqlGroupInMapOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
		listVersionsOperation = new ListVersionsOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
		overwriteOperation = new OverwriteOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
		deleteHistoryAndDetailsOperation = new DeleteHistoryAndDetailsOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
	}

	public void save(T entity) throws InternalPrevalenceException, ValidationPrevalenceException {
		saveOperation.set(entity).execute();
	}
	
	public void update(T entity) throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		updateOperation.set(entity).execute();
	}
	
	public void delete(T entity) throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		deleteOperation.set(entity).execute();
	}
	
	public <Z extends CustomOperation> Z newCustomOperation(Class<Z> classeCustomOperation) throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		Z customOperation = null;
		try {
			customOperation = classeCustomOperation.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new InternalPrevalenceException("Error creating new instance for class " + classeCustomOperation.getSimpleName() + ". Please code a default contructor in all CustomOperations", e);
		}
		customOperation.initialize(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
		return customOperation;
	}
	
	public Integer count(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return countTotalOperation.set(classe).execute();
	}
	public Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		return countFilterOperation.set(classe, filter).execute();
	}
	
	public T getPojo(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyEntity(memoryCore.getPojo(classe, id));
	} 
	
	public String getJson(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		return memoryCore.getJson(classe, id);
	}
	
	public T getFirstPojo(Class<T> classe, FilterFirst<T> filterFirst) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyEntity(filterFirstPojoOperation.set(classe, filterFirst).execute());
	}
	
	public String getFirstJson(Class<T> classe, FilterFirst<T> filterFirst) throws InternalPrevalenceException, ValidationPrevalenceException {
		return filterFirstJsonOperation.set(classe, filterFirst).execute();
	}
	
	public List<T> listPojo(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyList(classe, listTotalPojoOperation.set(classe).execute());
	}
	public List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyList(classe,filterPojoOperation.set(classe, filter).execute());
	}

	public String listJson(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return listTotalJsonOperation.set(classe).execute();
	}
	
	public String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		return filterJsonOperation.set(classe, filter).execute();
	}	
	
	public List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyList(joSqlListOperation.set(classe, joSqlQuery, parametersBind).execute());		
	}

	public List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyList(joSqlHavingOperation.set(classe, joSqlQuery, parametersBind).execute());
	}

	@SuppressWarnings({ "rawtypes" })
	public Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyEntity(joSqlGroupInMapOperation.set(classe, joSqlQuery, parametersBind).execute());		
	}
	
	public Map<Date, String> listVersions(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		return listVersionsOperation.set(classe, id).list();
	}
	
	public void overwrite(Class<T> classe, Long id, Date versionDate) throws InternalPrevalenceException, ValidationPrevalenceException {
		overwriteOperation.set(classe, id, versionDate).execute();
	}

	public void deleteHistoryAndDetails(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		deleteHistoryAndDetailsOperation.set(classe, id).execute();
	}
	
	public static void registerObserver(PrevalenceChangeObserver observer) {
		MemoryCore.register(observer);
	}

	public static void deRegisterObserver(PrevalenceChangeObserver observer) {
		MemoryCore.deRegister(observer);
	}	
	
	public MemorySearchEngineInterface getSearchEngine() {
		return memoryCore;
	}
	
}