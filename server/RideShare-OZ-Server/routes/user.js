var express = require('express');
var router = express.Router();
var User = require('../models/User.js');

/*
 * User Login
 * @description: verify user, if the user is new, create a new user
 * @return: User's info
 */
router.post('/login', function(req, res, next) {
  User.findOne({'username': req.userinfo.email}, function(err, user){
    if (!user) {
      // if the user is new, then create a new user
      User.createUser(req.userinfo.email, function(new_user){
        res.json(new_user);
      });
    } else {
      res.json(user);
    }
  });
});

router.post('/info', function(req, res, next) {
  User.findOne({'username': req.userinfo.email}, function(err, user){
    res.json(user);
  });
});

/*
 * User's ride
 * @return: all the rides which the user is the driver, passenger or has made a request
 */
router.post('/getRides', function(req, res, next) {
  User.findOne({'username': req.userinfo.email}, function(err, user){
    /* res.json(user); */
    if (user) {
      user.getRides(function(rides){
        res.json(rides);
      });
    } else {
      res.end('Invalid username');
    }
  });
});

router.get('/getRides', function(req, res, next) {
  User.findOne({'username': req.userinfo.email}, function(err, user){
    if (user) {
      user.getRides(function(rides){
        res.json(rides);
      });
    } else {
      res.end('Invalid username');
    }
  });
});

/*
 * Update a User's info
 */
router.post('/update', function(req, res, next){
  User.findOne({'username': req.userinfo.email}, function(err, user){
    user.address = (req.body.address)? req.body.address : user.address;
    user.note = (req.body.note)? req.body.note : user.note;
    user.phone = (req.body.phone)? req.body.phone : user.phone;
    user.DoB = (req.body.DoB)? req.body.DoB : user.DoB;
    user.driver_license = (req.body.driver_license)? req.body.driver_license : user.driver_license;
    user.save(function(err){
      res.json(user);
    });
  });
});

/*
 * get all user's list
 */
router.post('/getall', function(req, res, next) {
  User.getAllUsers(function(users){
    res.json(users);
  });
});

router.get('/getall', function(req, res, next) {
  User.getAllUsers(function(users){
    res.json(users);
  });
});

/*
 * User's group
 * @return: all the gruops which the user is a member or has made a request
 */
router.post('/getGroups', function(req, res) {
  User.getGroups(req,function(users){
    res.json(users);
  });
});

/*
 * All group 
 * @return: all the gruops with user's relationship with each group
 *          joined, request or unjoined
 */
router.post('/getAllGroups', function(req, res) {
  User.findById(req.userinfo._id,function(err,user){
    user.getAllGroups(function(group){
      res.json(group);
    });
  });
});

module.exports = router;
