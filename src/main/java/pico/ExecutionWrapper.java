package pico;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ExecutionWrapper implements MethodInterceptor {
	
	private ExecutorService executorService;

	public ExecutionWrapper(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	@Override
	@SuppressWarnings({"rawtypes","unchecked"})
	public Object intercept(final Object object, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {

		Pico picoAnnotation = isPicoMethod(method);
		if (picoAnnotation == null) {
			// proceed to the method if it isn't annotated with a @Pico
			return methodProxy.invokeSuper(object, args);
		}
		
		final Future<?> futureResult = executorService.submit(new Callable() {
			
			public Object call() throws Exception {
				try {
					return methodProxy.invokeSuper(object, args);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});
		
		final Class returnType = method.getReturnType();
		final int timeout = picoAnnotation.timeout();

		if (returnType != void.class) {
			ProxyService.CallbackAssigner callbackAssigner = new ProxyService.CallbackAssigner() {
				
				@Override
				public MethodInterceptor setMethodInterceptor() {
					return new Result(method, futureResult, timeout);
				}
				
				@Override
				public InvocationHandler setInvocationHandler() {
					return new Result(method, futureResult, timeout);
				}
			};
			
			return ProxyService.createProxy(returnType, callbackAssigner);
		}
		else {
			return (null);
    	}
	}
	
	private Pico isPicoMethod(Method method) {
		Annotation[] all = method.getAnnotations();
		for (Annotation a: all) {
			if (a instanceof Pico) {
				return (Pico)a;
			}
		}
		return null;
	}

	class Result implements MethodInterceptor, InvocationHandler {
		private Method nanoMethod;
		private Future<?> futureResult;
		private int timeout;
		
		public Result(Method nanoMethod, Future<?> futureResut, int timeout) {
			this.nanoMethod = nanoMethod;
			this.futureResult = futureResut;
			this.timeout = timeout;
		}
	
		@Override
		public Object invoke(Object object, Method method, Object[] args) throws Throwable {
			try {
				Object result = 
					(timeout == 0) ? futureResult.get() : futureResult.get(timeout, TimeUnit.MILLISECONDS);
				return method.invoke(result, args);
			} catch (Throwable e) {
				if (e instanceof TimeoutException) {
					futureResult.cancel(true);
					throw new RuntimeException("Pico timeout " + nanoMethod.getName() + ", ms=" + timeout);
				}
				else
					throw e;
			}
		}

		@Override
		public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			try {
				Object result = 
					(timeout == 0) ? futureResult.get() : futureResult.get(timeout, TimeUnit.MILLISECONDS);
				Object r = method.invoke(result, args);
				return r;
			} catch (Throwable e) {
				if (e instanceof TimeoutException) {
					futureResult.cancel(true);
					throw new RuntimeException("Pico timeout " + nanoMethod.getName() + ", ms=" + timeout);
				}
				else
					throw e;
			}
		}
	}
}