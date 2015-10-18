var request = require('request');
var auth = {};

/*
 * verify the token with google server
 */
auth.auth_token = function (token, callback) {
  var url = 'https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=' + token;
  console.log(url);
  request(url, function (error, response, body) {
    if (!error && response.statusCode == 200) {
      callback(JSON.parse(body));
    } else {
      callback({"error_description": "Sorry, there is a problem on the server."});
    }
  });
};

module.exports = auth;
