package jsonprevayler.search.processing;

import java.util.List;
import java.util.Map;

import jsonprevayler.PrevalenceRepository;
import jsonprevayler.entity.PrevalenceEntity;
import jsonprevayler.search.PrevalenceFilter;

public class RemoteMultiThreadSearchPocessor implements SearchProcessor {

	@Override
	public <T extends PrevalenceEntity> void process(Class<T> classe, PrevalenceFilter<T> filter, List<T> retorno,
			Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPrevalence(PrevalenceRepository prevalenceJsonRepository) {
		// TODO Auto-generated method stub
		
	}

}
