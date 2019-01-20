var express = require('express')
var app = express()
var bodyParser = require('body-parser')

var jsonParser = bodyParser.json()

app.listen(80, function(){
  console.log('Server ready on port 80!');
})

app.post('/integers', bodyParser.urlencoded({extended: true}), function (req, res) {
  var integerArr = req.body.integers;
  // do something with array
  // 1 = snare
  // 2 = crash
  // 3 = hihat
  // 4 = tom
  console.log(integerArr);
  // function(integerArr) {
  //   if (integerArr === 1) {
  //
  //   } else if (integerArr === 2) {
  //
  //   } else if (integerArr === 3) {
  //
  //   } else if (integerArr === 4) {
  //
  //   }
  // }
  return res.send('got numbers');
})

app.use(express.static('../circular-audio-wave-master'))
