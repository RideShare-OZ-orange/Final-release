var express = require('express');
var router = express.Router();
var passport = require('passport');
var Group = require('../models/Group.js');

/*
 * Admin page,
 * if the user hasn't login, redrect to the login page
 */
router.get('/', function(req, res, next) {
  if (req.user) {
    res.render('admin', {
      title: 'Administrator',
      user: req.user.username
    });
  } else {
    res.redirect('/login');
  }
});

module.exports = router;
