package pico;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App {
	
	private App() throws Exception {
    	Selector selector = Selector.open();
    	int[] ports = new int[]{80,9991};
    	ByteBuffer echoBuffer = ByteBuffer.allocate(1024);
    	ByteBuffer outBuffer = ByteBuffer.allocate(1024);
    	
    	for (int port: ports) {
    		ServerSocketChannel ssc = ServerSocketChannel.open();
    		ssc.configureBlocking(false);

    		ServerSocket ss = ssc.socket();
    		InetSocketAddress address = new InetSocketAddress(port);
    		ss.bind(address);
        	
        	SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);
    	}
    	
    	while (true) {
        	int num = selector.select();

        	Set<SelectionKey> selectedKeys = selector.selectedKeys();
        	Iterator<SelectionKey> it = selectedKeys.iterator();

        	while (it.hasNext()) {
        		SelectionKey key = it.next();
        		
        		if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
        			ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
        			SocketChannel sc = ssc.accept();
        			sc.configureBlocking(false);
        			SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
        		} else 
        			if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
        		     System.out.printf("%d\n", key.readyOps());
        				// Read the data
        		     SocketChannel sc = (SocketChannel)key.channel();
        		     
        		     int bytesEchoed = 0;
        	         for (int r; (r = sc.read(echoBuffer)) > 0; ) {
        	              //echoBuffer.flip();
        	              //sc.write(echoBuffer);
        	              bytesEchoed += r;
        	              //echoBuffer.clear();
        	              outBuffer.put("Hello".getBytes());
        	              sc.write(outBuffer);
        	              outBuffer.clear();
        	         }
        	         
        	         System.out.printf("Echoed %d bytes: %s\n", bytesEchoed, new String(echoBuffer.array()));
        		}
        			else
            			if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
               		     SocketChannel sc = (SocketChannel)key.channel();
          	              outBuffer.put("Hello".getBytes());
          	              sc.write(outBuffer);
            			}
        		
    			it.remove();
        	}
    	}		
	}
	
    public static void main( String[] args ) {
        System.out.println("Listening to multi ports");
        
        try {
        	new App();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}