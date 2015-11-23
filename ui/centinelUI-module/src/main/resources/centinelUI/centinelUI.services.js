/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : This js provides services common all modules in CentinelUI 
*/


define(['app/centinelUI/centinelUI.module'],function(centinelUIApp) {


  centinelUIApp.register.factory('centinelUISvc', function($http, ENV, $q,Restangular) {
    return {

		      getSeviceApi: function() {
							    	  var defer = $q.defer();
							    	  $http.get("src/app/centinelUI/assets/data/service_properties.json").then(function(properties) {
							    		  defer.resolve(properties.data);
										});
										return defer.promise;
		      						}
		    }

  });



});
