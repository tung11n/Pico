package pico;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sf.cglib.proxy.Enhancer;

public final class PicoActionManager {
	private static final int THREADPOOL_SIZE = 8;

	private static ExecutorService executorService;

	public static void initiatePicoActionManager(int threadPoolSize) {
		if (executorService == null) {
			if (threadPoolSize <= 0)
				threadPoolSize = THREADPOOL_SIZE;
			executorService = Executors.newFixedThreadPool(threadPoolSize);
		}
	}
	
	public static Object createProxy(Class targetClass) {
		initiatePicoActionManager(THREADPOOL_SIZE);
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(targetClass);
		enhancer.setCallback(new ExecutionWrapper(executorService));
		return enhancer.create();
	}
	
	public static void shutdown() {
		try {
			executorService.shutdown();
			executorService.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			//
		}
	}
}