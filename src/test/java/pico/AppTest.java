package pico;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest 
{
	@Test
    public void testApp() {
    	TextService service = (TextService)PicoActionManager.createProxy(TextService.class);
    	service.service1(9);
    	service.service2();
    	List<Integer> l = service.service3();
        service.service4(l);
    	System.out.println("Waiting for services");
    	System.out.println(l.get(0));
    	PicoActionManager.shutdown();
    }
    
    //@Test
    public void testNIOServer() throws Exception {
    	//URL url = new URL
    }
    
    //@Test
    public void testNIORead() throws Exception {
    	FileInputStream fin = new FileInputStream("/tmp/niotest.txt");
    	FileChannel fc = fin.getChannel();
    	ByteBuffer buffer = ByteBuffer.allocate(1024);
    	fc.read(buffer);
    	String content = new String(buffer.array());
    	System.out.printf("%s\n", content);
    }
	
	//@Test
	public void testProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ArrayList.class);
        enhancer.setInterfaces(ArrayList.class.getInterfaces());
		enhancer.setCallback(NoOp.INSTANCE);
		Object obj = enhancer.create();
		Class proxyCls = obj.getClass();
		
		System.out.println("SUPER " + proxyCls.getSuperclass() + " " + proxyCls.getMethods().length);
		//assertTrue(proxyCls.getSuperclass() == List.class);
		
		for (Method i: proxyCls.getMethods()) {
			System.out.println("Method: " + i.getName());
		}
		for (Class i: proxyCls.getInterfaces()){
			System.out.println("Interface: " + i);
		}
	}
}
