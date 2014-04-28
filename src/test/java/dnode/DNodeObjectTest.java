package dnode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DNodeObjectTest {
    public static class Cat {
        public void say(Callback callback) {

        }

        public void meow(Callback callback) {

        }
    }

    @Test
    public void shouldReportSignature() {
        DNodeObject dcat = new DNodeObject(new Cat());
        
        String signatures = dcat.getSignatures().toString();
        
        // order can come back different on subsequent platform lookups, but we cache to ensure it's always consistent
        if (signatures.equals("{\"say\":\"[Function]\",\"meow\":\"[Function]\"}")) {
        	for (int i=0; i<50; i++) {
        		//make sure it stays consistent
        		assertEquals("{\"say\":\"[Function]\",\"meow\":\"[Function]\"}", dcat.getSignatures().toString());
        	}
        } else {
        	for (int i=0; i<50; i++) {
        		//make sure it stays consistent
        		assertEquals("{\"meow\":\"[Function]\",\"say\":\"[Function]\"}", dcat.getSignatures().toString());
        	}
        }
        
    }

    @Test
    public void shouldReportCallbacks() {
        DNodeObject dcat = new DNodeObject(new Cat());
        
        String callbacks = dcat.getCallbacks().toString();
        
        // order can come back different on subsequent platform lookups, but we cache to ensure it's always consistent
        if (callbacks.equals("{\"0\":[\"0\",\"say\"],\"1\":[\"0\",\"meow\"]}")) {
        	for (int i=0; i<50; i++) {
        		//make sure it stays consistent
        		assertEquals("{\"0\":[\"0\",\"say\"],\"1\":[\"0\",\"meow\"]}", dcat.getCallbacks().toString());
        	}
        } else {
        	for (int i=0; i<50; i++) {
        		//make sure it stays consistent
        		assertEquals("{\"0\":[\"0\",\"meow\"],\"1\":[\"0\",\"say\"]}", dcat.getCallbacks().toString());
        	}
        }
    }
}    