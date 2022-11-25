package br.tec.jsonprevayler.pojojsonrepository.core.operations;

public enum OperationState {
	INITIALIZED,
	VALIDATED,
	ID_CREATED,
	INIT_LOCK,
	LOCK_FINALIZED,
	BINARY_COPY_OK,
	ENTITY_WRITED,
	PREVALENCE_VERSION_UPDATED,
	MEMORY_UPDATED,
	INIT_ITERATION,
	END_ITERATION,
	INIT_SORT,
	END_SORT,
	INIT_PAGINATION,
	END_PAGINATION,
	PAGENING,
	FINALIZED,
	CANCELED,
	UNDO_DELETE_MEMORY,
	UNDO_DELETE_REGISTER,
	UNDO_SET_NULL_ID,
	UNDO_VERSION,
	UNDO_SAVE_MEMORY,
	UNDO_SAVE_REGISTER;
}
