define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {


	centinelUIApp.register.factory('dashboardServiceFactory', ['$http','$q','Restangular','centinelUISvc',function($http,$q,Restangular,centinelUISvc) {
		return {
			getAllDashboardsFromLocal: function() {
				 var defer = $q.defer();
				 var dashboardArr =[];
		    	 $http.get("src/app/centinelUI/assets/data/dashboard.json").then(function(dashboardObjects) {
		    		  _.each(dashboardObjects.data, function(dashboard){
			    			  _.each(dashboard, function(db) {
			    				 _.each(db, function(db1) {
				    				  dashboardArr.push(db1);
				    			  })
			    			  })		
		    			 });
		    		 
				    	defer.resolve(dashboardArr);
					});
					return defer.promise;
				 
			},
			getAllDashboards: function() {
				 var defer = $q.defer();
				 var dashboardArr =[];
				 centinelUISvc.getSeviceApi().then(function(data) {
						return Restangular.all(data.GET_ALL_DASHBOARD_SERVICE).getList().then(function(res) {
				    			  _.each(res.data.dashboardRecord.dashboardList, function(db) {
				    				  dashboardArr.push(db);
			    			 });
				    		  defer.resolve(dashboardArr);
				        	
				      },function(response) {
				    	    defer.reject(response);
				      });
						 defer.resolve(dashboardArr);
					});
					return defer.promise;
				 
			},
			setDashboard : function(dashboardFormObject,dashboardJson){			
				 var defer = $q.defer();
				 var output ='';
				 centinelUISvc.getSeviceApi().then(function(data) {
						return Restangular.all(data.SET_DASHBOARD_SERVICE).post(('dashboard',dashboardJson)).then(function(res) {
				    		  output=res.data.output;
				    		  defer.resolve(output);
					
				      },function(response) {
				    	    defer.reject(response);
				      });
						 defer.resolve(output);
					});
					return defer.promise;
			},
			deleteDashboard : function(inputJson){
				var defer = $q.defer();
				centinelUISvc.getSeviceApi().then(function(data) {
					return Restangular.all(data.DELETE_DASHBOARD_SERVICE).post(('dashboard',inputJson)).then(function(res) {
						message = res.data.output;
			    		  defer.resolve(message);
			      },function(response) {
			    	    defer.reject(response);
			      });
					 defer.resolve(output);
				});
				return defer.promise;
			}
		
		}

	}]);
	
});
