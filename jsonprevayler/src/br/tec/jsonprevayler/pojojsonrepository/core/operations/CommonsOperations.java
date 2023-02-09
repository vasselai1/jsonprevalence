package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.tec.jsonprevayler.annotations.MappedSuperClassPrevalenceRepository;
import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.OperationWriter;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;
import flexjson.JSONSerializer;

public abstract class CommonsOperations <T extends PrevalenceEntity> {

	public static final SimpleDateFormat SDF_OPERATION_TIME = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ssS");
	public static final SimpleDateFormat SDF_STATE_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
	
	protected static final String ERROR = "Error";
	protected static final String ENTITY = "Entity";
	protected static final String ID = "Id";
	protected static final String CLASS = "Class";
	protected static final String INTERNAL_CLASS = "InternalClass";
	
	protected PrevalenceConfigurator prevalenceConfigurator;
	protected MemoryCore memoryCore;
	protected FileCore fileCore;
	protected SequenceProvider sequenceProvider;
	protected DateProvider dateProvider;
	protected Logger logger = Logger.getLogger(getClass().getName());
	protected JSONSerializer jsonSerializer = new JSONSerializer();
	private OperationWriter operationWriter;
	private File operationFile;
	
	private Date initialMoment;
	private Date finalMoment;
	private OperationState state;
	
	public void setCore(PrevalenceConfigurator prevalenceConfigurator, MemoryCore memoryCore, FileCore fileCore, SequenceProvider sequenceProvider) {
		this.prevalenceConfigurator = prevalenceConfigurator;
		this.memoryCore = memoryCore;
		this.fileCore = fileCore;
		this.sequenceProvider = sequenceProvider;
		this.dateProvider = prevalenceConfigurator.getDateProvider();
		initialMoment = dateProvider.get();
		operationWriter = new OperationWriter(fileCore.getSystemFileDir(), prevalenceConfigurator.getNumberOfFilesPerDiretory());
	}	
	
	public OperationState getState() {
		return state;
	}
	public void setState(OperationState state) {
		this.state = state;
	}	
	public Date getInitialMoment() {
		return initialMoment;
	}
	public void setInitialMoment(Date initialMoment) {
		this.initialMoment = initialMoment;
	}
	public Date getFinalMoment() {
		return finalMoment;
	}
	public void setFinalMoment(Date finalMoment) {
		this.finalMoment = finalMoment;
	}	
	
	@SuppressWarnings("unchecked")
	Class<T> getClassRepository(Class<? extends PrevalenceEntity> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		if (classe.isAnnotationPresent(MappedSuperClassPrevalenceRepository.class)) {
			MappedSuperClassPrevalenceRepository mappingAnnotation = classe.getAnnotation(MappedSuperClassPrevalenceRepository.class);
			if (mappingAnnotation.mapping() == null) {
				throw new ValidationPrevalenceException("The annotation" + MappedSuperClassPrevalenceRepository.class.getName()  +  " in class " + classe.getCanonicalName() + " doesn't have mapping");
			}
			return (Class<T>) mappingAnnotation.mapping();
		}
		return (Class<T>) classe;
	}
	protected void initState() throws InternalPrevalenceException, ValidationPrevalenceException {
		initState(null);
	}
	protected void initState(Date moment) throws InternalPrevalenceException, ValidationPrevalenceException {		
		state = OperationState.INITIALIZED;
		if (!prevalenceConfigurator.isStoreOperationsDetails()) {
			return;
		}
		if (moment == null) {
			moment = dateProvider.get();
		}
		operationWriter.writeLn(getOperationFile(), state.toString(), SDF_STATE_TIME.format(moment));
	}
	
	protected void writeOperationDetail(String key, String value) throws InternalPrevalenceException {
		operationWriter.writeLn(getOperationFile(), key, value);
	}

	protected void updateState(OperationState operationState) throws InternalPrevalenceException {
		updateState(operationState, null);
	}
	protected void updateState(OperationState operationState, Date moment) throws InternalPrevalenceException {
		state = operationState;
		if (moment == null) {
			moment = new Date();
		}
		if (OperationState.FINALIZED.equals(operationState)) {
			finalMoment = dateProvider.get();
		}
		if (!prevalenceConfigurator.isStoreOperationsDetails()) {
			return;
		}
		operationWriter.writeLn(getOperationFile(), state.toString(), SDF_STATE_TIME.format(moment));
	}
	
	protected void initStateAssinc(Class<T> classe, Object entity, Date moment) throws InternalPrevalenceException, ValidationPrevalenceException {
		state = OperationState.INITIALIZED;
		if (!prevalenceConfigurator.isStoreOperationsDetails()) {
			return;
		}
		new Thread() {
			public void run() {
				try {
					initState(moment);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "initStateAssinc error", e);
				}
			}
		}.start();
	}
	protected void updateStateAssinc(OperationState operationState, Date moment) throws InternalPrevalenceException, ValidationPrevalenceException {
		updateStateAssinc(operationState, null, moment);
	}
	protected void updateStateAssinc(OperationState operationState, String mensage, Date moment) throws InternalPrevalenceException, ValidationPrevalenceException {
		state = operationState;
		if (!prevalenceConfigurator.isStoreOperationsDetails()) {
			return;
		}
		new Thread() {
			public void run() {
				try {
					updateState(operationState, moment);		
				} catch (Exception e) {
					logger.log(Level.SEVERE, "updateStateAssinc error", e);
				}
			}
		}.start();
	}
	
	private File getOperationFile() throws InternalPrevalenceException {
		if (operationFile == null) {
			operationFile = operationWriter.newOperationFile(getOperationFileName());
		}
		return operationFile;
	}
	
	public abstract String getOperationName();
	
	private String getOperationFileName() {
		StringBuilder fileNameBuilder = new StringBuilder();
		fileNameBuilder.append(getOperationName());
		fileNameBuilder.append("_");
		fileNameBuilder.append(SDF_OPERATION_TIME.format(dateProvider.get()));
		fileNameBuilder.append(".opr");
		return fileNameBuilder.toString();
	}
	
	protected String getJson(T entity) {
		return jsonSerializer.deepSerialize(entity);
	}
}