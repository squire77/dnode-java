package dnode.socketio;

import org.webbitserver.HttpRequest;
import org.webbitserver.WebSocketConnection;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

class SocketIOConnection implements WebSocketConnection {
    private final WebSocketConnection connection;
    private final SocketIOCodec codec;

    public SocketIOConnection(WebSocketConnection connection, SocketIOCodec codec) {
        this.connection = connection;
        this.codec = codec;
        String id = String.valueOf(System.currentTimeMillis());
        connection.send(codec.encode(id));
    }

    @Override
    public HttpRequest httpRequest() {
        return connection.httpRequest();
    }

    @Override
    public WebSocketConnection send(String message) {
        String encodedMessage = codec.encode(message);
        connection.send(encodedMessage);
        return this;
    }

    @Override
    public WebSocketConnection close() {
        connection.close();
        return this;
    }

    @Override
    public Map<String, Object> data() {
        return connection.data();
    }

    @Override
    public Object data(String key) {
        return connection.data(key);
    }

    @Override
    public WebSocketConnection data(String key, Object value) {
        return connection.data(key, value);
    }

    @Override
    public Set<String> dataKeys() {
        return connection.dataKeys();
    }

    @Override
    public Executor handlerExecutor() {
        return connection.handlerExecutor();
    }

    @Override
    public void execute(Runnable runnable) {
        connection.execute(runnable);
    }

	@Override
	public WebSocketConnection ping(byte[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebSocketConnection pong(byte[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebSocketConnection send(byte[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebSocketConnection send(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String version() {
		// TODO Auto-generated method stub
		return null;
	}
}
