var express = require('express');
var router = express.Router();
var Ride = require('../models/Ride.js');

/*
 * Create a ride
 */
router.post('/create', function(req, res, next) {
  Ride.createRide(req, function(doc){
    res.json(doc);
  });
});

/*
 * @return: all the rides in database
 */
router.get('/getall', function(req, res, next) {
  Ride.getAllRides(function(rides){
    res.json(rides);
  });
});

/*
 * Search for rides
 * @params: filters
 * @return: all valid rides
 */
router.get('/search',function(req, res){
  req.body = req.query;
  Ride.searchRide(req, function(locations){
    res.json(locations);
  });
});

router.post('/search',function(req, res){
  Ride.searchRide(req, function(locations){
    res.json(locations);
  });
});

/*
 * add a user to a ride
 * @params: a user and a ride
 */
router.post('/request',function(req,res){
  Ride.findById(req.body.ride_id, function(err, ride){
    if(ride){
      ride.addRequest(req.userinfo._id,req, function(updated_ride){
        res.json(updated_ride);
      });
    }else{ res.json("no ride");}
  });

});

/*
 * delete the ride
 * @params: a ride
 */
router.post('/cancel',function(req,res){
  Ride.cancelRide(req, function(flag){
    res.json(flag);
  });
});


/*
 * reject a request
 * @params: a user and a ride
 */
router.post('/reject',function(req,res){
  Ride.rejectRequest(req,function(ride){
    res.json(ride);
  });

});

/*
 * accept a request
 * @params: a user and a ride
 */
router.post('/accept',function(req,res){
  Ride.acceptRequest(req,function(ride){
    res.json(ride);
  });
});


/*
 * kick a user out of a ride
 * @params: a user and a ride
 */
router.post('/kick',function(req,res){
  Ride.kickPassenger(req,function(ride){
    res.json(ride);
  });
});

/*
 * kick a user out of a ride
 * @params: a user and a ride
 */
router.post('/leave',function(req,res){
  Ride.passengerLeave(req,function(ride){
    res.json(ride);
  });
});

module.exports = router;
