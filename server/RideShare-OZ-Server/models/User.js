/*
 * User Model
 */
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var UserSchema = new Schema({
  username: String,
  note: String,
  address: String,
  phone: { type: String, default: "" },
  DoB: Date,
  driver_license: { type: String, default: "" },
  groups: [{group:{type: Schema.Types.ObjectId, ref: 'Group' }, state:String}],
  updated_at: { type: Date, default: Date.now },
  driver_rate:{ type: Number, default:0},
  passenger_rate:{ type: Number, default:0},
});

UserSchema.statics.createUser = function(username, callback){
  var User = mongoose.model('User');
  var new_user = new User({'username': username});
  new_user.save(function(err){
    callback(new_user);
  });
};

UserSchema.statics.getAllUsers = function(callback){
  this.find({}).populate('groups.group', 'groupname')
               .exec({}, function(err, users){
    callback(users);
  });
};

UserSchema.methods.getRides = function(callback){
  var conditions = {
    $or:[
      {'driver':this},
      {'passengers.user':this},
      {'requests.user':this}
    ]
  };
  /* var conditions = {}; */
  this.model('Ride').find(conditions)
                    .populate('driver ',
                              'username phone driver_license driver_rate')
                    .populate('passengers.user  requests.user',
                              'username phone passenger_rate')
                    .populate('events','eventName')
                    .populate('group','groupname')
                    .exec({}, function(err, rides){
    callback(rides);
  });
};

UserSchema.statics.getGroups = function(req,callback){
  this.findById(req.userinfo._id).populate('groups.group',
                                           'groupname introduction')
                                 .exec({}, function(err,user){
    callback(user.groups);
  });
};

UserSchema.methods.getAllGroups = function(callback){
  var groups=[];
  this.model('Group').find({members:{$in:[this]}},function(err,group){
    group.forEach(function(g){
      var record={
        'group':{'_id':g._id,'groupname':g.groupname,'introduction':g.introduction},
        'state':'joined'
      };
      groups.push(record);
    });
  });
  this.model('Group').find({'requests.user':this},function(err,group){
    group.forEach(function(g){
      var record={
        'group':{
          '_id':g._id,
          'groupname':g.groupname,
          'introduction':g.introduction
        },
        'state':'request'
      };
      groups.push(record);
    });
  });
  this.model('Group').find({
    $and:[
      {'members':{$nin:[this]}},
      {'requests.user':{$nin:[this]}}
    ]
  },function(err,group){
    group.forEach(function(g){
      var record={
        'group':{
          '_id':g._id,
          'groupname':g.groupname,
          'introduction':g.introduction
        },
        'state':'unjoined'
      };
      groups.push(record);
    });
    callback(groups);
  });//.exec({},function(err,groups){ callback(groups);})
};

module.exports = mongoose.model('User', UserSchema);
