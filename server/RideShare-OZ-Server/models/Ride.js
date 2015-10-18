/*
 * Ride Model
 */
var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var User = require('../models/User.js');
var Group = require('../models/Group.js');
var distance= require('google-distance-matrix');

var RideSchema = new Schema({
  arrival_time: Date,
  start_time: Date,
  seats: Number,
  start_point: {type:[Number],index:'2d'}, // Lon, Lat
  start_add: String,
  end_point: {type:[Number],index:'2d'},
  destination: String,
  driver: {type: Schema.Types.ObjectId, ref: 'User' },
  group: {type: Schema.Types.ObjectId, ref: 'Group'},
  events: {type: Schema.Types.ObjectId, ref: 'Event'},
  passengers: [{
    user:{ type: Schema.Types.ObjectId, ref: 'User' },
    pickup_point:{ type: [Number] },
    pickup_time:Date,
    pickup_add:String,
    rated_by_driver:Boolean,
    rated: Boolean
  }],
  requests: [{
    user:{type: Schema.Types.ObjectId, ref: 'User' },
    pickup_point:{ type: [Number] },
    state: String,
    note: String,
    pickup_time: Date,
    pickup_add: String
  }],
  updated_at: { type: Date, default: Date.now },
  note: String,
  finished: Boolean
});


RideSchema.statics.getAllRides = function(callback){
  this.find().populate('driver ','username phone driver_license driver_rate').
    populate('passengers.user  requests.user','username phone passenger_rate').
    populate('events','eventName').
    populate('group','groupname').
    exec({}, function(err, rides){
      callback(rides);
    });
};

RideSchema.statics.createRide = function(req,callback){
  var Ride = mongoose.model('Ride');
  var ride = new Ride();
  ride.finished=false;
  ride.arrival_time = req.body.arrival_time;
  ride.start_time=req.body.start_time;
  ride.seats = req.body.seat;
  var start_lon=req.body.s_lon;
  var start_lat=req.body.s_lat;
  ride.start_add=req.body.start_add;
  ride.destination=req.body.destination;
  ride.start_point=[Number(start_lon),Number(start_lat)];
  var end_lon=req.body.e_lon;
  var end_lat=req.body.e_lat;
  ride.end_point=[Number(end_lon),Number(end_lat)];
  ride.events=req.body.event_id;
  ride.group = req.body.group_id;
  User.findById(req.userinfo._id, function(err, user){
    ride.driver = user;
    ride.save(function(err, doc){
      if (err) {
        console.log(err);
      }
      callback(doc);
    });
  });
};

// lat lon  desitination  date 
RideSchema.statics.searchRide = function(req,callback){

  var start = [];
  start[0] = Number(req.body.s_lon);
  start[1] = Number(req.body.s_lat); 

  var e_lon = req.body.e_lon;
  var e_lat = req.body.e_lat; 
  var end=[];
  end[0]=e_lon;
  end[1]=e_lat;

  //need have arrival time
  var maxDistance = 0.0002;
  var limit = 10;
  var groupID = req.body.group_id;
  var arrival_time = req.body.arrival_time;
  var s_time = arrival_time.substring(0, arrival_time.length - 14) + 'T00:00:00.000Z';
  var e_time = arrival_time.substring(0, arrival_time.length - 14) + 'T23:59:59.000Z';

  var origins = [];
  var destinations = [];

  // fill your Google api key here
  distance.key('AIzaSyDRcEadcdHfKKNyeQSRDtsSVsGaKEM2r2M');
  distance.units('imperial');

  var rides = [];
  var count = 0;

  // find valid ride based on arrival_time and destination
  this.find({
    'arrival_time':{"$gte":new Date(s_time),"$lt":new Date(e_time)},
    'end_point': {
      $nearSphere: end,
      $maxDistance: maxDistance
    }
  }).populate('driver ','username phone driver_license driver_rate')
    .populate('passengers.user  requests.user','username phone passenger_rate')
    .populate('events','eventName')
    .populate('group','groupname')
    .exec({},function(err,ride){
    if(ride && ride.length!==0) { 
      var ride1=[];
      if(groupID){
        for (var i = 0; ride.length>i ; i++) {
          if (ride[i].group && String(ride[i].group._id)==groupID){
            ride1.push(ride[i]);
          }
        }
      } else if(req.body.event_id){
        for (var j = 0; ride.length>j ; j++) {
          if (ride[j].events && String(ride[j].events._id)==req.body.event_id){
            ride1.push(ride[j]);
          }
        } 
      }

      // ride=[];
      ride=ride1;
      var length=ride.length;

      ride.forEach(function(ride){
        origins[0]=ride.start_add;
        destinations[0]=ride.destination;
        destinations[1]=req.body.origins;
        origins[1]=req.body.origins;

        var h1=999999,
            h2=999999, 
            h3=999999;

        // compare the duration between three points
        distance.matrix(origins, destinations, function (err, distances){
          count++;
          if(distances) {
            if (distances.status == 'OK') {
              if (distances.rows[0].elements[0].status == 'OK'){
                h1=distances.rows[0].elements[0].duration.value;
                //console.log(h1);
              }
              if (distances.rows[0].elements[1].status == 'OK'){
                h2=distances.rows[0].elements[1].duration.value;
                //console.log(h2);
              }
              if (distances.rows[1].elements[1].status == 'OK'){
                h3=distances.rows[1].elements[0].duration.value;
                //console.log(h3);
              }
              if(Number(h3)+Number(h2)<Number(h1)+900){
                rides.push(ride);
              }
            }
            if(length==count) {
              callback(rides);
            }
            else{console.log(count);}
          }
        });
      });
    }else{
      console.log("==nothing============");
      callback([]);
    }
  });
};

RideSchema.methods.addRequest = function(user_id,req,callback){
  /* console.log(this); */ 
  var pickup_point = [];
  pickup_point[0] = req.body.p_lon;
  pickup_point[1] = req.body.p_lat;
  var note = req.body.note;
  var pickup_time = req.body.pickup_time;
  var count=0;
  this.requests.forEach(function(request){
    if (String(request.user)==String(user_id)) {
      count=1;
    }});
  if(count===0){
    this.requests.push({
      'user':user_id,
      'state':"unaccept",
      'pickup_point':pickup_point,
      'note':note,
      'pickup_time':pickup_time,
      'pickup_add':req.body.pickup_add
    });
    this.save(function(err, doc){
      callback(doc);
    });
  }else{callback("already exist");} 
};


RideSchema.statics.cancelRide = function(req,callback){
  var ride_id=req.body.ride_id;
  console.log(req.userinfo._id);
  this.find({_id:ride_id}).remove(function(err){
    callback("deleted");
  });
};

RideSchema.statics.rejectRequest= function(req,callback){
  var ride_id=req.body.ride_id;
  var user_id=req.userinfo._id;
  this.findByIdAndUpdate(ride_id,{$pull:{'requests':{'user':user_id}}},
      function(err,doc){callback(doc);});
};

RideSchema.statics.acceptRequest= function(req,callback){
  var ride_id=req.body.ride_id;
  var user_id=req.userinfo._id; 
  var pickup_point=[];
  var pickup_time; 
  var pickup_add;
  var user_in_passenger=0;
  //add use to passenger
  this.findById(ride_id,function(err,doc){

    doc.passengers.forEach(function(passenger){
      if (String(passenger.user)==String(user_id))
      {user_in_passenger=1;}

    });
    if(user_in_passenger===0){
      doc.requests.forEach(function(request){

        if (String(request.user)==String(user_id)) {
          pickup_point=request.pickup_point;
          pickup_time=request.pickup_time;
          pickup_add=request.pickup_add;
          // doc.seats=Number(doc.seats)-1;
          doc.passengers.push({
            'user':user_id,
            'pickup_point':pickup_point,
            'pickup_time':pickup_time,
            'pickup_add':pickup_add,
            'rated_by_driver':false,
            'rated':false
          });
          doc.save();
        }
      });}
  });
  //delete the user in requests
  this.findByIdAndUpdate(ride_id,{$pull:{'requests':{'user':user_id}}},function(err,doc){
    callback(doc);
  });
};

RideSchema.statics.kickPassenger= function(req,callback){
  var ride_id=req.body.ride_id;
  var user_id=req.userinfo._id; 
  this.findByIdAndUpdate(ride_id,
                         {$pull:{'passengers':{'user':user_id}}},
                         function(err,doc){
    callback(doc);
  });
};

RideSchema.statics.passengerLeave= function(req,callback){
  var ride_id=req.body.ride_id;
  var user_id=req.userinfo._id; 
  this.findByIdAndUpdate(ride_id,
                         {$pull:{'passengers':{'user':user_id}}},
                         function(err,doc){
    callback(doc);
  });
};

module.exports = mongoose.model('Ride', RideSchema);
