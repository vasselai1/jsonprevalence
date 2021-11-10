package br.org.pr.jsonprevayler.pojojsonrepository.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.org.pr.jsonprevayler.exceptions.InternalPrevalenceException;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.PrevalenceChangeObserver;
import br.org.pr.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.org.pr.jsonprevayler.pojojsonrepository.core.FileCore;
import br.org.pr.jsonprevayler.pojojsonrepository.core.InitializationMemoryCoreType;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemorySearchEngineInterface;
import br.org.pr.jsonprevayler.searchfilter.FilterFirst;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceFilter;
import br.org.pr.jsonprevayler.searchfilter.processing.SearchProcessor;

public class OperationsControler <T extends PrevalenceEntity> {
 	
	private final FileCore fileCore;
	private final MemoryCore memoryCore;	
	private final SequenceProvider sequenceProvider;	
	private final SaveOperation<T> saveOperation;
	private final UpdateOperation<T> updateOperation;
	private final DeleteOperation<T> deleteOperation;
	private final FilterOperation<T> filterOperation;
	private final JoSqlOperation<T> joSqlOperation;

	public OperationsControler(String prevalencePath, String systemName, SearchProcessor searchProcessor, InitializationMemoryCoreType initializationMemoryCoreType) {
		MemoryCore.setInitializationType(initializationMemoryCoreType);
		fileCore = new FileCore(prevalencePath, systemName);
		memoryCore = new MemoryCore(fileCore);
		sequenceProvider = new SequenceProvider(fileCore.getSystemPath());		
		saveOperation = new SaveOperation<T>(sequenceProvider, memoryCore, fileCore);
		updateOperation = new UpdateOperation<T>(sequenceProvider, memoryCore, fileCore);
		deleteOperation = new DeleteOperation<T>(memoryCore, fileCore, sequenceProvider);
		filterOperation = new FilterOperation<T>(memoryCore, searchProcessor);
		joSqlOperation = new JoSqlOperation<T>(memoryCore);
	}

	public void save(T entity) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, InternalPrevalenceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, DeprecatedPrevalenceEntityVersionException, NoSuchMethodException, InstantiationException, InterruptedException, Exception {
		saveOperation.set(entity, false).execute();
	}
	public void saveDeep(T entity) throws ValidationPrevalenceException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAlgorithmException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, NoSuchMethodException, InstantiationException, InterruptedException, Exception {
		saveOperation.set(entity, true).execute();
	}
	
	public void update(T entity) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException, NoSuchMethodException, InstantiationException, InterruptedException, Exception {
		updateOperation.set(entity, false).execute();
	}
	public  void updateDeep(T entity) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException, NoSuchMethodException, InstantiationException, InterruptedException, Exception {
		updateOperation.set(entity, true).execute();
	}	
	
	public void delete(T entity) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InternalPrevalenceException, Exception {
		deleteOperation.set(entity).execute();
	}
	
	public <Z extends CustomOperation> Z newAtomicOperation(Class<Z> classeCustomOperation)  throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException, InstantiationException, NoSuchMethodException {
		Z customOperation = classeCustomOperation.getDeclaredConstructor().newInstance();
		customOperation.initialize(sequenceProvider, memoryCore, fileCore);
		return customOperation;
	}
	
	public Integer count(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		return filterOperation.count(classe);
	}
	public Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		return filterOperation.count(classe, filter);
	}
	
	public T getPojo(Class<T> classe, Long id) throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, ValidationPrevalenceException, InterruptedException {
		return memoryCore.getPojo(classe, id);
	} 
	
	public String getJson(Class<T> classe, Long id) throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ValidationPrevalenceException, IOException, InterruptedException {
		return memoryCore.getJson(classe, id);
	}
	
	public T getFirstPojo(Class<T> classe, FilterFirst<T> filterFirst) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException {
		return filterOperation.getFirstPojo(classe, filterFirst);
	}
	
	public String getFirstJson(Class<T> classe, FilterFirst<T> filterFirst) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException {
		return filterOperation.getFirstJson(classe, filterFirst);
	}
	
	public List<T> listPojo(Class<T> classe) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		return filterOperation.listPojo(classe);
	}
	public List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		return filterOperation.listPojo(classe, filter);
	}

	public String listJson(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		return filterOperation.listJson(classe);
	}
	public String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		return filterOperation.listJson(classe, filter);
	}	
	
	public List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		return joSqlOperation.joSqlQueryList(classe, joSqlQuery, parametersBind);
	}

	public List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		return joSqlOperation.joSqlQueryHavingList(classe, joSqlQuery, parametersBind);
	}

	@SuppressWarnings({ "rawtypes" })
	public Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		return joSqlOperation.joSqlQueryGroupMap(classe, joSqlQuery, parametersBind);
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