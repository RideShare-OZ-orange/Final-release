/*
 * Administrator's account model
 */
var mongoose = require('mongoose'),
    Schema = mongoose.Schema,
    passportLocalMongoose = require('passport-local-mongoose');

var AccountSchema = new Schema({
  username: String,
  updated_at: { type: Date, default: Date.now },
});

AccountSchema.plugin(passportLocalMongoose);

module.exports = mongoose.model('Account', AccountSchema);
