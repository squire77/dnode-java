package dnode;

import com.google.gson.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class DNode<T> {
    private final List<Connection> connections = new ArrayList<Connection>();
    private final DNodeObject instance;
    private final ClientHandler<T> clientHandler;
    private final Class<T> type;

    public DNode(Object instance) {
        this(instance, null, null);
    }

    public DNode(Object instance, Class<T> type, ClientHandler<T> clientHandler) {
        this.instance = new DNodeObject(instance);
        this.clientHandler = clientHandler;
        this.type = type;
    }

    public void listen(Server server) throws IOException {
        server.listen(this);
    }

    private JsonElement methods() {
        JsonArray arguments = new JsonArray();
        arguments.add(instance.getSignatures());
        JsonElement serverMethods = response("methods", arguments, instance.getCallbacks(), new JsonArray());
        //System.out.println("server methods: " + serverMethods.toString());
        return serverMethods;
    }

    private JsonElement response(String method, JsonArray arguments, JsonObject callbacks, JsonArray links) {
        return response(new JsonPrimitive(method), arguments, callbacks, links);
    }

    public JsonElement response(int method, JsonArray arguments, JsonObject callbacks, JsonArray links) {
        return response(new JsonPrimitive(method), arguments, callbacks, links);
    }

    private JsonElement response(JsonElement method, JsonArray arguments, JsonObject callbacks, JsonArray links) {
        JsonObject response = new JsonObject();
        response.add("method", method);
        response.add("arguments", arguments);
        response.add("callbacks", callbacks);
        response.add("links", links);
        return response;
    }

    public void onOpen(Connection connection) {
        connections.add(connection);
        connection.write(methods());
        if(clientHandler != null) {
            T clientProxy = createClientProxy(connection);
            clientHandler.onConnect(clientProxy);
        }
    }

    private T createClientProxy(final Connection connection) {
    	// You cannot get a generic base type at run-time due to type erasure. So, the type is now passed
    	// in directly on object construction.
        //ParameterizedType type = (ParameterizedType) clientHandler.getClass().getGenericInterfaces()[0];
        //Class<?> clientType = (Class<?>) type.getActualTypeArguments()[0];
            	
        Object proxy = Proxy.newProxyInstance(
        		type.getClassLoader(), //clientType.getClassLoader(),        		 
        		new Class<?>[]{type}, //clientType}, 
        		new InvocationHandler() {
            @Override
            public Object invoke(Object target, Method method, Object[] args) throws Throwable {
            	if (method.getName().equals("toString") && target != null) {
            		return "object-ref:####";
            	}
            	
                JsonArray arguments = transform(args);
                JsonObject callbacks = new JsonObject();
                JsonArray links = new JsonArray();
                
                //currently limited to invoking client methods that do not have any callback parameters
        		JsonElement invoker = response(method.getName(), arguments, callbacks, links);
        		System.out.println("server invokes client function: " + invoker.toString());
                connection.write(invoker);
                return null;
            }
        });
        
        return (T) proxy; // this cast into type T on return is unchecked due to type erasure}
    }

    public void onMessage(Connection connection, String msg) {
    	//System.out.println(msg);
        JsonObject json = new JsonParser().parse(msg).getAsJsonObject();
        JsonPrimitive method = json.getAsJsonPrimitive("method");
        if (method.isString() && method.getAsString().equals("methods")) {
            defineClientMethods(json.getAsJsonArray("arguments").get(0).getAsJsonObject());
        } else {
            instance.invoke(this, json, connection);
        }
    }

    public JsonArray transform(Object[] args) {
        JsonArray result = new JsonArray();
        
        if (args != null) {
	        for (Object arg : args) {
	            result.add(toJson(arg));
	        }
        }
        
        return result;
    }

    private JsonElement toJson(Object o) {
        JsonElement e;
        if (o instanceof String) {
            e = new JsonPrimitive((String) o);
        } else if (o instanceof Number) {
            e = new JsonPrimitive((Number) o);
        } else {
            throw new RuntimeException("Unsupported type: " + o.getClass());
        }
        return e;
    }
    
    private void defineClientMethods(JsonObject methods) {
        // TODO: Verify that the client's reported methods have the same
        // signatures as our client proxy, and fail fast if it doesn't.
    	//System.out.println(methods.toString());
    }

    public void closeAllConnections() {
        for (Connection connection : connections) {
            connection.close();
        }
    }
}
