/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

define(['angularAMD', 'app/routingConfig', 'app/core/core.services', 'common/config/env.module'], function(ng) {
  var centinelUIApp = angular.module('app.centinelUI', 
		  ['app.core', 'ui.router.state','config','pascalprecht.translate']);

  centinelUIApp.register = centinelUIApp; 

  
  centinelUIApp.factory("centinelLoaderSvc", function ($q) {

      var controllers = [
        'app/centinelUI/centinelUI.controller',
        'app/centinelUI/streams/alert/alertFunction.controller',
        'app/centinelUI/streams/editRules/editRulesFunction.controller'
        ];
      var services = [
		'app/centinelUI/centinelUI.services',
		'app/centinelUI/streams/alert/alertFunction.services'
        ];
      var directive = [
		'app/centinelUI/streams/alert/alertFunction.directive'
      ];
  
      var loaded = $q.defer();

       require([].concat(controllers).concat(services).concat(directive), function () {
     		console.info("centinelLoaderSvc:  completed");
     		loaded.resolve(true);
    	});
 
      return loaded.promise; // return promise to wait for in $state transition
    });
  
  
  centinelUIApp.config(function($stateProvider, $compileProvider, $controllerProvider, $provide, NavHelperProvider, $translateProvider) {
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
     "title" : "centinelUI",
     "icon" : "",  // Add navigation icon css class here
     "page" : {
        "title" : "centinelUI",
        "description" : "centinelUI"
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
    
/*    
    $stateProvider.state('main.centinelUI.streams', {
        url: '/streams',
        access: access.public,
        views : {
            'mainTabSubmenu' : {
                templateUrl: 'src/app/centinelUI/streams/streamsSubMenu.html'
               
            }
        }
    });*/
    
    $stateProvider.state('main.centinelUI.alert', {
        url: '/alertFunction',
        access: access.public,
        views : {
            'centinelContent' : {
                templateUrl: 'src/app/centinelUI/streams/alert/manageAlert.html',
                controller: 'centinelUIAlertCtrl'
                
            }
           
        }
    });
    
    $stateProvider.state('main.centinelUI.editRules', {
        url: '/editRulesFunction',
        access: access.public,
        views : {
            'centinelContent' : {
                templateUrl: 'src/app/centinelUI/streams/editRules/editRules.html',
                controller: 'editRulesFunCtrl'
                
            }
        }
    });

  });

  return centinelUIApp;
});
