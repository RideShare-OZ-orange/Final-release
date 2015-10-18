var express = require('express');
var router = express.Router();
var Credit = require('../models/Credit.js');

router.get('/getall', function(req, res, next) {
  Credit.getAllCredit(function(credit){
    res.json(credit);
  });
});

/*
 * rate a user
 */
router.post('/rate', function(req, res) {
  Credit.addRate(req,function(rate){
    res.json(rate);
  });
});

/*
 * get the average rate for a user
 */
router.get('/avg', function(req, res) {
  Credit.avgRate(req,function(result){
    res.json(result);
  });
});

module.exports = router;
