SERVER DNODE INSTANCE OBJECT
----------------------------
1. Remote callable server methods are limited to having a single 
   parameter which is a callback


CLIENT DNODE
-------------
1. Remote callable client methods may have any number or type of parameters, except callbacks
2. Remote callable client methods are passed in the DNode constructor
3. netty library does not use flush() or REUSE_ADDR so
   applications MUST close connections by calling dnode.end() when done
4. Java DNODE does not support client connection to Javascript server


SIMPLE EXAMPLE
--------------------------------
[Java Server]

Server server = new NettyServer(port);
// DNode type doesn't matter when there is no handler (2nd and 3rd parameters)
DNode<Object> dnode = new DNode<Object>(new Mooer(100), null, null); 
dnode.listen(server);
...
dnode.closeAllConnections();
server.shutdown();

[JavaScript Client]

var d = new dnode();
d.on('remote', function (remote) {
  remote.moo(function (x) {
    console.log(x);
    d.end();
  });
});
d.connect(port);


EXAMPLE WITH 'ON CONNECT' HANDLER
----------------------------------
[Java Server]

public static interface SomeClient {
    void hello(int x);
}       

Server server = new NettyServer(port);
DNode<SomeServer> dnode = new DNode<SomeServer>(new Mooer(100), SomeServer.class, new ClientHandler<SomeClient>() {
    @Override
    public void onConnect(SomeClient client) {
        System.out.println("client = " + client);
        client.hello(5);
    }
});
dnode.listen(server);
...
dnode.closeAllConnections();
server.shutdown();

[JavaScript Client]

var d = dnode({ hello : function(x) { console.log('hello' + x); } });

d.on('remote', function(remote) {
  remote.moo(function(x) { console.log(x); d.end() });
  //d.end(); // don't do this. async calls close connection too soon
});

var c = net.connect(port);
c.pipe(d).pipe(c);