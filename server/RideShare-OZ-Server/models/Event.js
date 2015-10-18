/*
 * Event Model
 */
var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var User = require('../models/User.js');

var EventSchema = new Schema({
  eventName: String,
  eventInfo: { type: String, default: "" },
  eventLocation: [Number],
  location: { type: String, default: "" },
  startTime:Date,
  endTime:Date,
  updated_at: { type: Date, default: Date.now },
});

EventSchema.statics.createEvent= function(req,callback) {
  var Event = mongoose.model('Event');
  var events = new Event();
  events.eventName = req.body.event_name;
  var lon=req.body.e_lon;
  var lat=req.body.e_lat;

  events.eventLocation=[lon,lat];
  events.eventInfo=req.body.eventInfo;
  events.startTime=req.body.start_time;
  events.endTime=req.body.end_time;
  events.location=req.body.location;

  events.save(function(err, doc){
    if (err) {
      console.log(err);
    }
    callback(doc);
  });
};

module.exports = mongoose.model('Event', EventSchema);
