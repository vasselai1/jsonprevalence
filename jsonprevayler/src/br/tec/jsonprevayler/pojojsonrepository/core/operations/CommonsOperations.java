package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.util.List;
import java.util.logging.Logger;

import br.tec.jsonprevayler.annotations.MappedSuperClassPrevalenceRepository;
import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;

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
