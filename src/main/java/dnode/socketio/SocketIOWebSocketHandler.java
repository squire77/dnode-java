package dnode.socketio;

import org.webbitserver.WebSocketConnection;
import org.webbitserver.WebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler that wraps/unwraps messages sent with a SocketIO client.
 */
public class SocketIOWebSocketHandler implements WebSocketHandler {
    private static final SocketIOCodec codec = new SocketIOCodec();
    private final WebSocketHandler handler;
    private Map<WebSocketConnection, SocketIOConnection> socketIOConnections = new HashMap<WebSocketConnection, SocketIOConnection>();

    public SocketIOWebSocketHandler(WebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onOpen(WebSocketConnection connection) throws Exception {
        SocketIOConnection socketIOConnection = new SocketIOConnection(connection, codec);
        socketIOConnections.put(connection, socketIOConnection);
        try {
			handler.onOpen(socketIOConnection);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void onMessage(WebSocketConnection connection, String msg) throws Exception {
        List<String> messages = codec.decode(msg);
        for (String message : messages) {
            String frame = message.substring(0, 3);
            if (frame.equals("~j~")) {
                // Heartbeat
                return;
            } else if (frame.equals("~j~")) {
                // TODO: Should we parse into JSON here? We also seem to get JSON that is *not* prefixed with ~j~ (??)
                message = message.substring(3);
            }
            try {
				handler.onMessage(socketIOConnections.get(connection), message);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Exception {
        try {
			handler.onClose(socketIOConnections.remove(connection));
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
