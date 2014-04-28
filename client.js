#!/usr/bin/env node
const dnode = require('dnode');

var port = process.argv[2];
var methodName = process.argv[3];

var d = new dnode();

d.on('remote', function (remote) {
  remote[methodName](function (x) {
    console.log(x);
    d.end();
  });
});

d.connect(parseInt(port));
