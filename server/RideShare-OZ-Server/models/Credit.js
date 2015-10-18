/*
 * Credit Model
 */
var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var Ride = require('../models/Ride.js');
var User = require('../models/User.js');

var CreditSchema = new Schema({
  type : String,
  receiver: String,//{type: Schema.Types.ObjectId, ref: 'User' },
  rater : {type: Schema.Types.ObjectId, ref: 'User' },
  rate : Number,
  ride_id: {type: Schema.Types.ObjectId, ref: 'Ride' },
  updateDate:{ type: Date, default: Date.now },
  comment: String,
});

CreditSchema.statics.getAllCredit = function(callback){
  this.find({}, function(err, credit){
    callback(credit);
  });
};

CreditSchema.statics.addRate = function(req,callback){
  var Credit = mongoose.model('Credit');
  var credit = new Credit();
  var ratee_id;
  if(req.body.type==="driver"||req.body.type==="passenger"){
    User.find({'username':req.body.rateeName},function(err,user){
      // save the rating 
      ratee_id=user[0]._id;
      credit.ride_id=req.body.ride_id;
      credit.rater=req.userinfo._id;
      credit.receiver=ratee_id;
      credit.comment=req.body.comment;
      credit.rate=req.body.rate;
      credit.type=req.body.type;
      credit.save();
      if(req.body.type==="passenger"){
        Credit.aggregate([
            {	
              $match:{$and:[{type:"passenger"},{receiver:String(ratee_id)}]}
            },
            {
              $group:{
                _id:"$receiver",
                avgRate:{$sum:"$rate"},
                count:{$sum:1}
              }
            }
        ],function(err,result){
          // update user's average rate
          User.findById(ratee_id,function(err,user){
            user.passenger_rate=((Number(result[0].avgRate)+Number(req.body.rate))/(Number(result[0].count)+1)).toFixed(2);
            user.save();
          });
          // mark the user as rated
          Ride.update({'_id':req.body.ride_id,'passengers.user':ratee_id},
                      {'$set':{'passengers.$.rated_by_driver': true }},
                      function(err,user){
            console.log("RATE save");
          });
        });
      }else if (req.body.type === "driver"){
        Credit.aggregate([
            {	
              $match:{$and:[{type:"driver"},{receiver:String(ratee_id)}]}
            },
            {
              $group:{
                _id:"$receiver",
                avgRate:{$sum:"$rate"},
                count:{$sum:1}
              }
            }
        ],function(err,result){
          // update user's average rate
          User.findById(ratee_id,function(err,user){
            user.driver_rate=((Number(result[0].avgRate)+Number(req.body.rate))/(Number(result[0].count)+1)).toFixed(2);
            user.save();
          });
          // mark the user as rated
          Ride.update({'_id':req.body.ride_id,'passengers.user':req.userinfo._id},
                      {'$set':{'passengers.$.rated': true }},
                      function(err,user){
            console.log("RATE save");
          });
        });	
      } 
      callback("save success!");
    });
  } else {
    callback("type is wrong!");
  }
};

// get the average rate
CreditSchema.statics.avgRate = function(req,callback){
  this.aggregate([
      {
        $match:{$and:[{type:"driver"},{receiver:req.query.receiver_id}]}
      },
      {
        $group:{
          _id:"$receiver",
          avgRate:{$avg:"$rate"},
          count:{$sum:1}
        }
      }
  ],function(err,result){
    console.log(result[0].avgRate);
    callback(result);
  });
};

module.exports = mongoose.model('Credit', CreditSchema);
