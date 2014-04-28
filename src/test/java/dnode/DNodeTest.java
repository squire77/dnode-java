package dnode;

import dnode.netty.NettyServer;
import junit.framework.AssertionFailedError;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

public class DNodeTest {
	private static int basePort = 6000;
	
    public class Mooer {
        public int moo;

        public Mooer(int moo) {
        	this.moo = moo;
        }
        
        public void setMoo(int moo) {
        	this.moo = moo;
        }
        
        public void moo(Callback cb) throws IOException {
            cb.call(moo);
        }
        
        private void stuff(int x) {}

        public void boo(Callback cb) throws IOException {
            cb.call(moo * 10);
        }
        
        public void too(Callback cb) throws IOException {        	
        	cb.call(0);
        }      
    }

    @Test
    public void shouldTalk() throws IOException, InterruptedException {
    	int port = basePort;
    	String portStr = new Integer(port).toString();
    			
    	Server server = new NettyServer(port);
        DNode<Object> dnode = createDnode(100);        
        dnode.listen(server);
        assertEquals("100\n", runScript("client.js", portStr, "moo")); // invoke moo on the remote mooer
        dnode.closeAllConnections();
        server.shutdown();
    }

    @Test
    public void shouldUseDataInInstance() throws IOException, InterruptedException {
    	int port = basePort+1;
    	String portStr = new Integer(port).toString();
    			    	
    	Server server = new NettyServer(port);
    	DNode<Object> dnode = createDnode(200);
    	dnode.listen(server);
        assertEquals("200\n", runScript("client.js", portStr, "moo")); // invoke moo on the remote mooer
        dnode.closeAllConnections();
        server.shutdown();
    }

    @Test
    public void shouldCallRightMethod() throws IOException, InterruptedException {
    	int port = basePort+2;
    	String portStr = new Integer(port).toString();
    			    	
    	Server server = new NettyServer(port);
    	DNode<Object> dnode = createDnode(300);
    	dnode.listen(server);
        assertEquals("3000\n", runScript("client.js", portStr, "boo")); //invoke boo on the remote mooer
        dnode.closeAllConnections();
        server.shutdown();
    }

    public static interface SomeClient {
        void hello(int x);
    }    
    
    @Test
    public void shouldBeAbleToCallClient() throws IOException, InterruptedException {
    	int port = basePort+3;
    	String portStr = new Integer(port).toString();
    			    	
    	Server server = new NettyServer(port);
    	DNode<SomeClient> dnode = this.<SomeClient>createDnode(345, SomeClient.class, new ClientHandler<SomeClient>() {
            @Override
            public void onConnect(SomeClient client) {
                System.out.println("client = " + client);
                client.hello(5);
            }
        });
    	dnode.listen(server);
        assertEquals("hello5\n3450\n345\n", runScript("client2.js", portStr, "")); //connect to remote mooer and let it invoke the client upon connection
        dnode.closeAllConnections();
        server.shutdown();
    }

    private DNode<Object> createDnode(int moo) {
    	return this.<Object>createDnode(moo, null, null); // type T doesn't matter when there is no handler    
    }
    
    private <T> DNode<T> createDnode(int moo, Class<T> type, ClientHandler<T> handler) {
        return new DNode<T>(new Mooer(moo), type, handler);
    }

    private String runScript(String script, String port, String method) throws IOException, InterruptedException {
        String node = "C:\\Program Files\\nodejs\\node.exe"; //System.getProperty("node", "/usr/local/bin/node");
        String clientScript = "C:\\node-dnodetest\\" + script; //System.getProperty("client", "client.js");
        ProcessBuilder pb = new ProcessBuilder(node, clientScript, port, method);
        pb.directory(new File("C:\\node-dnodetest")); // working directory
        pb.redirectErrorStream(true);
        Process client = pb.start();

        Reader clientStdOut = new InputStreamReader(client.getInputStream(), "UTF-8");
        StringBuilder result = new StringBuilder();
        int c;
        while ((c = clientStdOut.read()) != -1) {
            result.append((char) c);
        }
        int exit = client.waitFor();
        if (exit != 0)
            throw new AssertionFailedError("Exit value from external process was " + exit +
                    " (with stdout/stderr: " + result + ")");
        return result.toString();
    }
}
