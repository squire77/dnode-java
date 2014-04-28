package dnode.webbit;

import dnode.DNode;

import org.webbitserver.WebSocketConnection;
import org.webbitserver.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

public class DNodeWebSocketHandler implements WebSocketHandler {
    private final DNode<?> dnode;

    private final Map<WebSocketConnection, WebbitConnection> connections = new HashMap<WebSocketConnection, WebbitConnection>();

    public DNodeWebSocketHandler(DNode<?> dnode) {
        this.dnode = dnode;
    }

    public void onOpen(WebSocketConnection connection) throws Exception {
        WebbitConnection c = getFor(connection);
        dnode.onOpen(c);
    }

    public void onMessage(WebSocketConnection connection, String msg) throws Exception {
        WebbitConnection c = getFor(connection);
        dnode.onMessage(c, msg);
    }

    public void onClose(WebSocketConnection connection) throws Exception {
        connections.remove(connection);
    }

    private WebbitConnection getFor(WebSocketConnection connection) {
        WebbitConnection wc = connections.get(connection);
        if (wc == null) {
            wc = new WebbitConnection(connection);
            connections.put(connection, wc);
        }
        return wc;
    }

	@Override
	public void onMessage(WebSocketConnection arg0, byte[] arg1)
			throws Throwable {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPing(WebSocketConnection arg0, byte[] arg1) throws Throwable {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPong(WebSocketConnection arg0, byte[] arg1) throws Throwable {
		// TODO Auto-generated method stub
		
	}
}


