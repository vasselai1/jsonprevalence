package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.util.List;
import java.util.Map;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.PrevalenceChangeObserver;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemorySearchEngineInterface;
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
	private final FilterOperation<T> filterOperation;
	private final JoSqlOperation<T> joSqlOperation;

	public OperationsControler(PrevalenceConfigurator prevalenceConfigurator, SearchProcessorFactory searchProcessorFactory) {
		this.prevalenceConfigurator = prevalenceConfigurator;
		fileCore = new FileCore(prevalenceConfigurator.getPrevalencePath(), prevalenceConfigurator.getSystemName(), prevalenceConfigurator.getNumberOfFilesPerDiretory());
		memoryCore = new MemoryCore(fileCore);
		sequenceProvider = new SequenceProvider(fileCore.getSystemPath());		
		saveOperation = new SaveOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
		updateOperation = new UpdateOperation<T>(prevalenceConfigurator,sequenceProvider, memoryCore, fileCore);
		deleteOperation = new DeleteOperation<T>(prevalenceConfigurator, memoryCore, fileCore, sequenceProvider);
		filterOperation = new FilterOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessorFactory.createNewSearchProcessor());
		joSqlOperation = new JoSqlOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
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
			throw new InternalPrevalenceException("Error creatign new instance for class " + classeCustomOperation.getSimpleName() + ". Please code a default contructor in all CustomOperations", e);
		}
		customOperation.initialize(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore);
		return customOperation;
	}
	
	public Integer count(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return filterOperation.count(classe);
	}
	public Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		return filterOperation.count(classe, filter);
	}
	
	public T getPojo(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyEntity(memoryCore.getPojo(classe, id));
	} 
	
	public String getJson(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		return memoryCore.getJson(classe, id);
	}
	
	public T getFirstPojo(Class<T> classe, FilterFirst<T> filterFirst) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyEntity(filterOperation.getFirstPojo(classe, filterFirst));
	}
	
	public String getFirstJson(Class<T> classe, FilterFirst<T> filterFirst) throws InternalPrevalenceException, ValidationPrevalenceException {
		return filterOperation.getFirstJson(classe, filterFirst);
	}
	
	public List<T> listPojo(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyList(classe,filterOperation.listPojo(classe));
	}
	public List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyList(classe, filterOperation.listPojo(classe, filter));
	}

	public String listJson(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return filterOperation.listJson(classe);
	}
	
	public String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		return filterOperation.listJson(classe, filter);
	}	
	
	public List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		return joSqlOperation.joSqlQueryList(classe, joSqlQuery, parametersBind);
	}

	public List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		return joSqlOperation.joSqlQueryHavingList(classe, joSqlQuery, parametersBind);
	}

	@SuppressWarnings({ "rawtypes" })
	public Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		return ObjectCopyUtil.copyEntity(joSqlOperation.joSqlQueryGroupMap(classe, joSqlQuery, parametersBind));
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