#!/usr/bin/env node
const dnode = require('dnode');
const net = require('net');

var port = process.argv[2];
var d = dnode({ hello : function(x) { console.log('hello' + x); } });

d.on('remote', function(remote) {
  remote.boo(function(x) { console.log(x); });
  remote.moo(function(x) { console.log(x); });
  remote.too(function(x) { d.end(); });
  //d.end(); // don't do this. async calls close connection too soon
});

var c = net.connect(parseInt(port));
c.pipe(d).pipe(c);
