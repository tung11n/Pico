package pico;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

class ProxyService {

	@SuppressWarnings({"rawtypes"})
	public static Object createProxy(Class tobeProxied, CallbackAssigner callbackAssigner) {
		if (!tobeProxied.isInterface()) {
	        Enhancer enhancer = new Enhancer();
	        enhancer.setSuperclass(tobeProxied);
			enhancer.setCallback(callbackAssigner.setMethodInterceptor());
			
			return enhancer.create();
		}
		else {
			Class[] interfaces = tobeProxied.getInterfaces();
			Class[] allInterfaces = new Class[interfaces.length + 1];
			allInterfaces[0] = tobeProxied;
			System.arraycopy(interfaces, 0, allInterfaces, 1, interfaces.length);
			
			return
				Proxy.newProxyInstance(ProxyService.class.getClassLoader(), allInterfaces, callbackAssigner.setInvocationHandler());
		}
	}
	
	public static interface CallbackAssigner {
		public MethodInterceptor setMethodInterceptor();
		public InvocationHandler setInvocationHandler();
	}
}