package jsonprevayler.search.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jsonprevayler.PrevalenceRepository;
import jsonprevayler.entity.PrevalenceEntity;
import jsonprevayler.search.PrevalenceFilter;

public class MultiThreadSearchProcessor implements  SearchProcessor {

	public final int PROCESSOR_CORES = Runtime.getRuntime().availableProcessors();
	
	private PrevalenceRepository prevalence;
	private List<List<Long>> sectors = new ArrayList<List<Long>>();
		
	@Override
	public <T extends PrevalenceEntity> void process(Class<T> classe, 
										   PrevalenceFilter<T> filter, 
										   List<T> retorno,
										   Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository) throws InterruptedException {
		filter.setPrevalenceInstance(prevalence);
		int numberOfThreads = PROCESSOR_CORES;
		if (PROCESSOR_CORES > 1) {
			numberOfThreads = numberOfThreads - 1;
		}
		int registersPerThread = pojoRepository.size() / numberOfThreads;
		initSectors(numberOfThreads, registersPerThread, pojoRepository.get(classe).keySet());
		List<LocalThreadFilter<T>> threads = new ArrayList<LocalThreadFilter<T>>();
		for (int i = 0; i < numberOfThreads; i++) {
			LocalThreadFilter<T> thread = new LocalThreadFilter<T>(classe, filter, pojoRepository, sectors.get(i), retorno);
			thread.start();
			threads.add(thread);
		}
		for (Thread threadLoop : threads) {
			threadLoop.join();
		}
	}

	@Override
	public void setPrevalence(PrevalenceRepository prevalenceJsonRepository) {
		this.prevalence = prevalenceJsonRepository;
	}

	private void initSectors(int numberOfThreads, int registersPerThread, Set<Long> keys) {
		ArrayList<Long> allKeysList = new ArrayList<Long>(keys);
		int inicialIndex = 0;
		int finalIndex = 0;
		int endIndex = keys.size();
		for (int i = 0; i < numberOfThreads; i++) {
			finalIndex = inicialIndex + registersPerThread;
			if (finalIndex >= endIndex) {
				finalIndex = endIndex + 1;
			}
			sectors.add(new ArrayList<Long>(allKeysList.subList(inicialIndex, finalIndex)));
			inicialIndex = finalIndex;
		}
	}
	
}
