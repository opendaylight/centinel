-/*
- * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
- *
- * This program and the accompanying materials are made available under the
- * terms of the Eclipse Public License v1.0 which accompanies this distribution,
- * and is available at http://www.eclipse.org/legal/epl-v10.html
- * @author Himanshu Yadav 
- * @description : This js provides services to streamdata functionality in CentinelUI 
-*/


define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {
	centinelUIApp.register.factory('streamdataServiceFactory', ['$http','$q','Restangular','centinelUISvc',function($http,$q,Restangular,centinelUISvc) {
		
		return {
			getAllEventsFromLocal: function(query){
		        var deferred = $q.defer();
		        var eventList = [];
		        var url="/src/app/centinelUI/assets/data/event.json";
		        $http.get(url).success(function(data){
		        	var eventList = data.output.records;
						          deferred.resolve(eventList);
						      }).error(function(){
						    	  console.info("ERROR !!");
						        deferred.reject("An error occured while fetching items");
						      });
		        return deferred.promise;
			},
			
			getChartDataFromLocal: function(){
		        var deferred = $q.defer();
		        var url="/src/app/centinelUI/assets/data/lineChartData.json";
		        $http.get(url).success(function(data){
		        	
						          deferred.resolve(data);
						      }).error(function(){
						    	  console.info("ERROR !!");
						        deferred.reject("An error occured while fetching items");
						      });
		        return deferred.promise;
			},
			
			getAllEvents: function(queryJson){
				var postJson = queryJson;
				console.info("query postJson");
				console.info(postJson);
				var defer = $q.defer();
				 var eventList =[];
				 centinelUISvc.getSeviceApi().then(function(data) {
						
						return Restangular.all(data.GET_EVENTS_SERVICE).post(('query',postJson)).then(function(res) {
							var eventList = res.data.output.records;
					          defer.resolve(eventList);    				  
			    			 },function(response) {
					    	    console.log("Error with status code", response.status);
					    	    defer.reject(response);
					      });
							 defer.resolve(eventList);
						});
					return defer.promise;
			}
		}
	
	}]);
});
