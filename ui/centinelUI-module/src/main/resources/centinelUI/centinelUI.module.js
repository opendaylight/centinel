/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : This js registers and configure all modules in CentinelUI 
*/

define(['angularAMD', 'app/routingConfig', 'app/core/core.services', 'common/config/env.module','d3'], function(ng) {
  var centinelUIApp = angular.module('app.centinelUI', 
		  ['app.core', 'ui.router.state','config','pascalprecht.translate','restangular']);

  centinelUIApp.register = centinelUIApp; 

  
  centinelUIApp.factory("centinelLoaderSvc", function ($q) {

      var controllers = [
        'app/centinelUI/centinelUI.controller',
        'app/centinelUI/streamingData/streamdata.controller',
        'app/centinelUI/streams/stream.controller',
        'app/centinelUI/streams/alert/alertFunction.controller',
        'app/centinelUI/streams/editRules/editRulesFunction.controller',
	'app/centinelUI/dashboards/dashboard.controller',
        'app/centinelUI/dashboards/widgets/widget.controller',
        'app/centinelUI/dashboards/createWidgets/createWidget.controller'
        ];
      var services = [
		'app/centinelUI/centinelUI.services',
		'app/centinelUI/streams/stream.services',
		'app/centinelUI/streams/alert/alertFunction.services',
		'app/centinelUI/streams/editRules/editRulesFunction.services',
		'app/centinelUI/streamingData/streamdata.services',
		'src/app/centinelUI/utils/js/pagination.js',
		'src/app/centinelUI/utils/js/dashboard.pagination.js',
		'app/centinelUI/dashboards/dashboard.services',
		'app/centinelUI/dashboards/createWidgets/createWidget.services',
		'app/centinelUI/dashboards/widgets/widget.services'
        ];
      var directive = [
		'app/centinelUI/streams/alert/alertFunction.directive',
		'app/centinelUI/dashboards/widgets/widget.directive',
		'app/centinelUI/streamingData/streamdata.directive',
		'app/centinelUI/utils/js/autocomplete'
      ];
  
      var loaded = $q.defer();

       require([].concat(controllers).concat(services).concat(directive), function () {
     		console.info("centinelLoaderSvc:  completed");
     		loaded.resolve(true);
    	});
 
      return loaded.promise; // return promise to wait for in $state transition
    });
  
  
  centinelUIApp.config(function($stateProvider, $compileProvider, $controllerProvider, $provide, NavHelperProvider, $translateProvider,RestangularProvider) {
    centinelUIApp.register = {
      controller : $controllerProvider.register,
      directive : $compileProvider.directive,
      factory : $provide.factory,
      service : $provide.service

    };
    
    $translateProvider.useStaticFilesLoader({
    			prefix: 'src/app/centinelUI/assets/data/application_properties_',
			    suffix: '.json'
      });
   


    NavHelperProvider.addControllerUrl('app/centinelUI/centinelUI.controller');
    NavHelperProvider.addToMenu('CentinelUI', {
     "link" : "#/centinelUI",
     "active" : "main.centinelUI",
     "title" : "Centinel",
     "icon" : "",  // Add navigation icon css class here
     "page" : {
        "title" : "Centinel",
        "description" : "Centinel"
     }
    });

    var access = routingConfig.accessLevels;

    $stateProvider.state('main.centinelUI', {
        url: 'centinelUI',
        access: access.admin,
        views : {
            'content' : {
                templateUrl: 'src/app/centinelUI/centinelUI.tpl.html',
                controller: 'centinelUICtrl'     	
            }
        },
        resolve: {
            dummy: "centinelLoaderSvc"
          }
    });

    
    $stateProvider.state('main.centinelUI.streamdata', {
        url: '/streamdata',
        access: access.public,
        views : {
            'centinelContent' : {
                templateUrl: 'src/app/centinelUI/streamingData/streamdata.html',
                controller: 'centinelUIStreamdataCtrl'
            }
           
        }
    });
    
    $stateProvider.state('main.centinelUI.stream', {
        url: '/streams',
        access: access.public,
        views : {
            'centinelContent' : {
                templateUrl: 'src/app/centinelUI/streams/stream.html',
                controller: 'centinelUIStreamCtrl'
                
            }
           
        }
    });
    
   $stateProvider.state('main.centinelUI.alert', {
        url: '/alertFunction',
        access: access.public,
        views : {
            'centinelContent' : {
                templateUrl: 'src/app/centinelUI/streams/alert/manageAlert.html',
                controller: 'centinelUIAlertCtrl'
                
            }
        },
   		params: {streamID: null}
    });
   
    $stateProvider.state('main.centinelUI.editRules', {
        url: '/editRulesFunction',
        access: access.public,
        views : {
            'centinelContent' : {
                templateUrl: 'src/app/centinelUI/streams/editRules/editRules.html',
                controller: 'editRulesFunCtrl'
                
            }
        },
		params: {streamID: null}
    });
    $stateProvider.state('main.centinelUI.dashboards', {
        url: '/dashboards',
        access: access.public,
        views : {
            'centinelContent' : {
                templateUrl: 'src/app/centinelUI/dashboards/dashboard.html',
                controller: 'centinelUIDashboardCtrl'
                
            }
        }
    });
    
    $stateProvider.state('main.centinelUI.widgets', {
    	url : '/dashboards/widgets',
    	access : access.public,
    	views : {
    		'centinelContent' : {
    			templateUrl: 'src/app/centinelUI/dashboards/widgets/widget.html',
                controller: 'centinelUIWidgetsCtrl'
    		}
    	},
		params: {dashboard: null}
    });

    $stateProvider.state('main.centinelUI.createWidgets', {
    	url : '/dashboards/createWidgets',
    	access : access.public,
    	views : {
    		'centinelContent' : {
    			templateUrl: 'src/app/centinelUI/dashboards/createWidgets/createWidget.html',
                controller: 'centinelUICreateWidgetsCtrl'
    		}
    	},
		params: {dashboard: null,streams: null}
    });

    RestangularProvider.setBaseUrl('http://localhost:8181/restconf/');
    RestangularProvider.setDefaultHeaders({'Authorization': 'Basic YWRtaW46YWRtaW4='});
    RestangularProvider.setResponseExtractor(function (response, operation, what) {
        return response;
      });
    RestangularProvider.setFullResponse(true);
    
    RestangularProvider.setErrorInterceptor(function ( response ) {
    	        // DON'T stop promise chain since error is not handled
    	        return true;
    	    
    });


    
  });

  return centinelUIApp;
});
