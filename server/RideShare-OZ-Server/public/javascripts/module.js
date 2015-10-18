var app = angular.module('RDash',
  [
    'ui.bootstrap',
    'ui.router',
    'ngCookies',
    'angularUtils.directives.dirPagination',
    'xeditable'
  ]
);

app.run(function(editableOptions) {
  // bootstrap3 theme. Can be also 'bs2', 'default'
  editableOptions.theme = 'bs3';
});
