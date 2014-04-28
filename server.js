#!/usr/bin/env node
var dnode = require('dnode');
var sys = require('sys');

var port = process.argv[2];

var server = DNode({
  moo : function (reply) { 
    reply(100); 
    server.close();
  },
  boo : function (n, reply) { 
    reply(n+1); 
    server.close();
  }
}).listen(parseInt(port));
