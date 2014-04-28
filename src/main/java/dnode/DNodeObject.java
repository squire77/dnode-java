package dnode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DNodeObject {
    private final Object instance;    
    private JsonObject signatures = null;    
    private JsonObject callbacks = null;
    private Map<Integer, Method> methods = null;

    public DNodeObject(Object instance) {
        this.instance = instance;
    }    

    public JsonElement getSignatures() {
    	if (signatures == null) {
    		doGetSignaturesAndCallbacks();
    	}
    	
    	return signatures;
    }
    
    public JsonObject getCallbacks() {
    	if (callbacks == null) {
    		doGetSignaturesAndCallbacks();
    	}
    	
    	return callbacks;
    }
    
    private Map<Integer, Method> getMethods() {
    	if (methods == null) {
    		doGetSignaturesAndCallbacks();
    	}
    	
    	return methods;
    }
    
    private void doGetSignaturesAndCallbacks() {
        Class<?> klass = this.instance.getClass();
        
        signatures = new JsonObject();    
        callbacks = new JsonObject();
        methods = new HashMap<Integer,Method>();
        
        int idx = 0;
        for (Method m : klass.getDeclaredMethods()) {
        	//System.out.println(idx + ": " + m.getName());
        	
            signatures.addProperty(m.getName(), "[Function]");  
                        
            JsonArray path = new JsonArray();
            path.add(new JsonPrimitive("0"));
            path.add(new JsonPrimitive(m.getName()));
            callbacks.add(String.valueOf(idx), path);
            
            methods.put(idx, m);
            
            idx++;
        }
    }
/*
    public JsonObject getCallbacks() {
    	if (callbacks == null) {
	        Class<?> klass = this.instance.getClass();
	        callbacks = new JsonObject();
	        //int index = 0;
	        //for (Method m : klass.getDeclaredMethods()) {
	        Set<Integer> keys = getMethods().keySet();
	        for (Integer key: keys) {
	        	Method m = getMethods().get(key);
	            Class<?>[] parameterTypes = m.getParameterTypes();
	            
	            for (Class<?> parameterType : parameterTypes) {
	                if (Callback.class.isAssignableFrom(parameterType)) {
	                    JsonArray path = new JsonArray();
	                    path.add(new JsonPrimitive("0"));
	                    path.add(new JsonPrimitive(m.getName()));
	                    callbacks.add(key.toString(), path);
	                    //callbacks.add(String.valueOf(index++), path);
	                }
	            }
	        }
    	}
        return callbacks;
    }
    */
        
    public void invoke(final DNode<?> dNode, JsonObject invocation, final Connection connection) {
        try {
            int methodIndex = invocation.get("method").getAsInt();
            Set<Map.Entry<String, JsonElement>> callbacks = invocation.get("callbacks").getAsJsonObject().entrySet();
            Callback cb = null; 
            		
            for (Map.Entry<String, JsonElement> callback : callbacks) {
                final int callbackId = Integer.parseInt(callback.getKey());
                
                cb = new Callback() {
                    @Override
                    public void call(Object... args) throws RuntimeException {
                        JsonArray jsonArgs = dNode.transform(args);
                        
                        //currently limited to invoking client methods that do not have any callback parameters
                        JsonElement invoker = dNode.response(callbackId, jsonArgs, new JsonObject(), new JsonArray());
                        connection.write(invoker);
                        System.out.println("server invokes client callback parameter: " + invoker.toString());
                    }
                };
                
                break;
            }
            
            //currently limited to invoking server methods with a single parameter which is a callback
            getMethods().get(methodIndex).invoke(instance, cb);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
