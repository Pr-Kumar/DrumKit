var express = require('express')
var app = express()
var bodyParser = require('body-parser')

// var http = require('http').Server(app);
// var io = require('socket.io')(http);
// var ReadableStream = Object.getPrototypeOf(process.stdin);

var jsonParser = bodyParser.json()

app.listen(8080, function(){
  console.log('Server ready on port 8080!');
})

app.post('/integers', bodyParser.urlencoded({extended: true}), function (req, res) {
  var integerArr = req.body.integers;
  // do something with array
  // 1 = snare
  // 2 = crash
  // 3 = hihat
  // 4 = tom
  console.log(integerArr);
  return res.send('got numbers');
})



// ReadableStream.read = function(cb) {
//     this.on('data', function(buf) {
//         cb(null, buf);
//     });
//
//     this.on('error', function(err) {
//         cb(err, null);
//     });
//
//     this.on('end', function() {
//         cb(null, null);
//     });
//
//     this.on('close', function() {
//         cb(new Error("Stream closed"), null);
//     });
// };
//
// io.on('connection', function(socket){
//   socket.on('chat message', function(msg){
//     console.log('message: ' + msg);
//   });
//   socket.on("input data", function(msg) {
//     console.log('data:' + msg)
//   });
//   console.log('a user connected');
//   socket.on('disconnect', function(){
//     console.log('user disconnected');
//   });
// });
