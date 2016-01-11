/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : This js provides services to stream functionality in CentinelUI 
*/

define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.factory('streamServiceFactory', ['$http','$q','Restangular','centinelUISvc',function($http,$q,Restangular,centinelUISvc) {
		
			return {
				getAllStreamsfromLocal: function(){
			        var deferred = $q.defer();
			        var url="/src/app/centinelUI/assets/data/testStreamTable.json";
			        $http.get(url).success(function(data){
							          deferred.resolve(data);
							      }).error(function(){
							    	  console.info("ERROR !!");
							        deferred.reject("An error occured while fetching items");
							      });
			        return deferred.promise;
				}
			
			}
	}]);

});
