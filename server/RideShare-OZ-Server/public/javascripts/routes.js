'use strict';

/**
 * Route configuration for the RDash module.
 */
angular.module('RDash').config(['$stateProvider', '$urlRouterProvider',
  function($stateProvider, $urlRouterProvider) {

    // For unmatched routes
    $urlRouterProvider.otherwise('/');

    // Application routes
    $stateProvider
      .state('index', {
        url: '/',
        templateUrl: 'templates/dashboard'
      })
      .state('event', {
        url: '/event',
        templateUrl: 'templates/event'
      })
      /*
       * .state('user', {
       *   url: '/user',
       *   templateUrl: 'templates/user'
       * })
       */
      .state('group', {
        url: '/group',
        templateUrl: 'templates/group'
      });
  }
]);
