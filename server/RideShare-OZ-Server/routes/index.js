var express = require('express');
var router = express.Router();
var passport = require('passport');
var Account = require('../models/Account');

router.get('/', function(req, res, next) {
  res.redirect('/admin');
});

router.get('/login', function(req, res) {
  res.render('login', { user : req.user });
});

router.post('/login', passport.authenticate('local'), function(req, res) {
  res.redirect('/admin');
});

router.get('/logout', function(req, res) {
  req.logout();
  res.redirect('/login');
});

/*
 * Admin Register
 * comment the following block of code if you want to close the register of Administrator
 */

//////////////////////////////////////////////////////////////////////////////////////
router.get('/register', function(req, res) {
  res.render('register', { });
});
router.post('/register', function(req, res) {
  Account.register(new Account({ username : req.body.username }), req.body.password, function(err, account) {
    if (err) {
      return res.render('register', { account : account });
    }
    passport.authenticate('local')(req, res, function () {
      res.redirect('/login');
    });
  });
});
////////////////////////////////////////////////////////////////////////////////////

module.exports = router;
