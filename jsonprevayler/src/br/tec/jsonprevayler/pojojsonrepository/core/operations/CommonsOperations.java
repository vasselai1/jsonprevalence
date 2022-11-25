package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.tec.jsonprevayler.annotations.MappedSuperClassPrevalenceRepository;
import br.tec.jsonprevayler.entity.OperationLog;
import br.tec.jsonprevayler.entity.OperationStatus;
import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import flexjson.JSONSerializer;

public abstract class CommonsOperations <T extends PrevalenceEntity> {

	protected PrevalenceConfigurator prevalenceConfigurator;
	protected MemoryCore memoryCore;
	protected FileCore fileCore;
	protected SequenceProvider sequenceProvider;	
	protected Logger logger = Logger.getLogger(getClass().getName());
	private OperationState state;
	private OperationLog operationLog;
	private OperationStatus operationStatus;
	private JSONSerializer serializer = new JSONSerializer();

	void setCore(PrevalenceConfigurator prevalenceConfigurator, MemoryCore memoryCore, FileCore fileCore, SequenceProvider sequenceProvider) {
		this.prevalenceConfigurator = prevalenceConfigurator;
		this.memoryCore = memoryCore;
		this.fileCore = fileCore;
		this.sequenceProvider = sequenceProvider;
	}	
	
	@SuppressWarnings("unchecked")
	Class<T> getClassRepository(Class<? extends PrevalenceEntity> classe) throws ValidationPrevalenceException {
		if (classe.isAnnotationPresent(MappedSuperClassPrevalenceRepository.class)) {
			MappedSuperClassPrevalenceRepository mappingAnnotation = classe.getAnnotation(MappedSuperClassPrevalenceRepository.class);
			if (mappingAnnotation.mapping() == null) {
				throw new ValidationPrevalenceException("The annotation" + MappedSuperClassPrevalenceRepository.class.getName()  +  "in class " + classe.getCanonicalName() + " doesn't have mapping");
			}
			return (Class<T>) mappingAnnotation.mapping();
		}
		return (Class<T>) classe;
	}
	protected void initState(Class<T> classe, Object entity) throws IOException, NoSuchAlgorithmException {
		initState(classe, entity, null);
	}
	protected void initState(Class<T> classe, Object entity, Date moment) throws IOException, NoSuchAlgorithmException {		
		state = OperationState.INITIALIZED;
		if (!prevalenceConfigurator.isStoreOperationsDetails()) {
			return;
		}
		if (moment == null) {
			moment = new Date();
		}
		operationLog = new OperationLog(classe.getName(), getClass().getSimpleName(), serializer.deepSerialize(entity));
		operationLog.setInitMoment(moment);
		operationLog.setId(sequenceProvider.get(OperationLog.class));
		operationStatus = new OperationStatus(operationLog.getId());
		fileCore.writeRegister(OperationLog.class, operationLog, true);
		fileCore.writeRegister(OperationStatus.class, operationStatus, true);
	}
	
	protected void updateState(OperationState operationState) throws NoSuchAlgorithmException, IOException {
		updateState(operationState, null, null);
	}
	protected void updateState(OperationState operationState, String mensage) throws NoSuchAlgorithmException, IOException {
		updateState(operationState, mensage, null);
	}
	protected void updateState(OperationState operationState, String mensage, Date moment) throws NoSuchAlgorithmException, IOException {
		state = operationState;
		if (!prevalenceConfigurator.isStoreOperationsDetails()) {
			return;
		}
		if (moment == null) {
			moment = new Date();
		}
		operationStatus.getStatus().put(state, moment);
		if (mensage != null) {
			operationStatus.addMessage(mensage);
		}
		fileCore.writeRegister(OperationStatus.class, operationStatus, true);		
	}
	
	protected void initStateAssinc(Class<T> classe, Object entity, Date moment) throws IOException, NoSuchAlgorithmException {
		state = OperationState.INITIALIZED;
		if (!prevalenceConfigurator.isStoreOperationsDetails()) {
			return;
		}
		new Thread() {
			public void run() {
				try {
					initState(classe, entity, moment);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "initStateAssinc error", e);
				}
			}
		}.start();
	}
	protected void updateStateAssinc(OperationState operationState, Date moment) throws IOException, NoSuchAlgorithmException {
		updateStateAssinc(operationState, null, moment);
	}
	protected void updateStateAssinc(OperationState operationState, String mensage, Date moment) throws IOException, NoSuchAlgorithmException {
		state = operationState;
		if (!prevalenceConfigurator.isStoreOperationsDetails()) {
			return;
		}
		new Thread() {
			public void run() {
				try {
					updateState(operationState, mensage, moment);		
				} catch (Exception e) {
					logger.log(Level.SEVERE, "updateStateAssinc error", e);
				}
			}
		}.start();
	}
	
	protected OperationState getState() {
		return state;
	}
	
	
}