var express = require('express');
var router = express.Router();

/*
 * for testing the connection to the server
 * @return: the parameters received
 */
router.get('/', function(req, res){
  var str = "";
  for(var attr in req.query){
    str += attr + ": " + req.query[attr] + '\n';
  }
  res.end("GET request received\n --RideShare-OZ-Server\n" + str);
});

router.post('/', function(req, res){
  var str = "";
  for(var attr in req.body){
    str += attr + ": " + req.body[attr] + '\n';
  }
  res.end("POST request received\n --RideShare-OZ-Server\n" + str);
});

router.get('/json', function(req, res){
  res.end(JSON.stringify(req.query));
});

router.post('/json', function(req, res){
  res.end(JSON.stringify(req.body));
});

module.exports = router;
