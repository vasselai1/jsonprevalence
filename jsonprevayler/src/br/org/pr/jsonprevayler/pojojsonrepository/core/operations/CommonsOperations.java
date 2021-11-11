package br.org.pr.jsonprevayler.pojojsonrepository.core.operations;

import java.util.List;
import java.util.logging.Logger;

import br.org.pr.jsonprevayler.annotations.MappedSuperClassPrevalenceRepository;
import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.org.pr.jsonprevayler.infrastrutuctre.normalization.JsonSerializationInstructions;
import br.org.pr.jsonprevayler.pojojsonrepository.core.FileCore;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemoryCore;

public class CommonsOperations <T extends PrevalenceEntity> {

	protected MemoryCore memoryCore;
	protected FileCore fileCore;
	protected SequenceProvider sequenceProvider;	
	protected Logger log = Logger.getLogger(getClass().getName());

	void setCore(MemoryCore memoryCore, FileCore fileCore, SequenceProvider sequenceProvider) {
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
	
	@SuppressWarnings("unchecked")
	void validateAllRelationsPersisted(JsonSerializationInstructions instructions) throws ValidationPrevalenceException {
		List<T> values = (List<T>) instructions.getPrevalentObjects();
		for (T entityLoop : values) {
			if (entityLoop.getId() == null) {
				throw new ValidationPrevalenceException("Prevance entity " + entityLoop + " is not persisted");
			}
		}
	}	
	
	boolean isPrevalentInstances(List<?> result) {
		if (result == null) {
			return false;
		}
		for (Object object : result) {
			if (object instanceof PrevalenceEntity) {
				return true;
			}
		}
		return false;
	}
	
}