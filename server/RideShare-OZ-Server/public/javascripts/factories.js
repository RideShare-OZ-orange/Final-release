'use strict';

/**
 * get group data
 */
angular.module('RDash').factory('groupDataFactory', ['$http', '$q', '$timeout', function($http, $q, $timeout) {
  return {
    getGroupData: function() {
      var MAX_REQUESTS = 5,
          counter = 1,
          results = $q.defer();

      var request = function() {
        $http({method: 'GET', url: 'group/getall'})
          .success(function(response) {
            results.resolve(response.reverse());
          })
        .error(function() {
          // retry getting data
          if (counter < MAX_REQUESTS) {
            // request();
            $timeout(request, 800);
            counter++;
          } else {
            results.reject("Could not load after multiple tries");
          }
        });
      };
      request();
      return results.promise;
      // return "Hello, World!"
    }
  };
}]);

/**
 * get event data
 */
angular.module('RDash').factory('eventDataFactory', ['$http', '$q', '$timeout',
    function($http, $q, $timeout) {
  return {
    getEventData: function() {
      var MAX_REQUESTS = 5,
          counter = 1,
          results = $q.defer();

      var request = function() {
        $http({method: 'GET', url: 'event/getall'})
          .success(function(response) {
            for (var idx in response) {
              // console.log(response[idx].startTime);
              if(response[idx].startTime){
                response[idx].startTime = response[idx].startTime.slice(0,10);
              }
              if(response[idx].endTime){
                response[idx].endTime = response[idx].endTime.slice(0,10);
              }
            }
            results.resolve(response.reverse());
          })
        .error(function() {
          if (counter < MAX_REQUESTS) {
            // request();
            $timeout(request, 800);
            counter++;
          } else {
            results.reject("Could not load after multiple tries");
          }
        });
      };
      request();
      return results.promise;
      // return "Hello, World!"
    }
  };
}]);
