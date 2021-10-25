package br.org.pr.jsonprevayler.searchfilter.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemorySearchEngineInterface;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceFilter;

public class MultiThreadSearchProcessor extends SearchProcessor {

	public final int PROCESSOR_CORES = Runtime.getRuntime().availableProcessors();
	
	private MemorySearchEngineInterface prevalence;
	private List<List<Long>> sectors = new ArrayList<List<Long>>();
	private List<Throwable> errors = new ArrayList<Throwable>();
		
	@Override
	public <T extends PrevalenceEntity> void process(Class<T> classe, PrevalenceFilter<T> filter, List<T> retorno) throws ClassNotFoundException, IOException, ValidationPrevalenceException {		
		setProgressObserver(filter.getProgressSearchObserver());
		int numberOfThreads = PROCESSOR_CORES;
		if (PROCESSOR_CORES > 1) {
			numberOfThreads = numberOfThreads - 1;
		}
		int total = prevalence.count(classe);
		setTotalCount(total);
		int registersPerThread = total / numberOfThreads;
		initSectors(numberOfThreads, registersPerThread, prevalence.getKeys(classe));
		List<Thread> threads = new ArrayList<Thread>();
		List<LocalThreadFilter<T>> processors = new ArrayList<LocalThreadFilter<T>>();
		for (int i = 0; i < numberOfThreads; i++) {
			LocalThreadFilter<T> localThreadFilter = new LocalThreadFilter<T>(classe, filter, sectors.get(i), retorno);
			processors.add(localThreadFilter);
			Thread thread = new Thread(localThreadFilter);
			thread.start();
			threads.add(thread);
		}
		for (Thread threadLoop : threads) {
			try {
				threadLoop.join();
			} catch (InterruptedException e) {
				//TODO lançar erro
				e.printStackTrace();
			}
		}
		for (LocalThreadFilter<T> localThreadFilterLoop : processors) {
			errors.addAll(localThreadFilterLoop.getErrors());
			if (onlyCount) {
				sumCountFounded(localThreadFilterLoop.getTotalFounded());
			}
		}
		if (!errors.isEmpty()) {
			//TODO lançar erros...
		}
	}

	private void initSectors(int numberOfThreads, int registersPerThread, Collection<Long> keys) {
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
