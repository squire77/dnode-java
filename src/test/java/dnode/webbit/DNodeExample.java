package dnode.webbit;

import dnode.Callback;
import dnode.ClientHandler;
import dnode.DNode;
import org.webbitserver.WebServer;
import org.webbitserver.handler.EmbeddedResourceHandler;
import org.webbitserver.netty.NettyWebServer;

import java.io.IOException;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class DNodeExample {
    public static class Cat {
        public void howAreYou(Callback cb) {
            cb.call("I am fine");
        }
    }

    public interface MyClient {
        void greet(String what);
    }
    
    public static void main(String[] args) throws IOException {
        WebServer server = new NettyWebServer(6061);
        new DNode<MyClient>(new Cat(), MyClient.class, new ClientHandler<MyClient>() {
            @Override
            public void onConnect(MyClient client) {
                client.greet("How are you?");
            }
        }).listen(new WebbitServer(server));
        server.add("/.*", new EmbeddedResourceHandler("dnode/js", newFixedThreadPool(4)));
        server.start();
    }
}
