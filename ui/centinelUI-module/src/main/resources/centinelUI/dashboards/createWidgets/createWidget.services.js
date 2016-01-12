define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.factory('createWidgetServiceFactory', ['$http','$q','Restangular','centinelUISvc',function($http,$q,Restangular,centinelUISvc) {
		
		return {
			
			setWidget : function(widgetJson){			
				 var defer = $q.defer();
				 var output ='';
				 centinelUISvc.getSeviceApi().then(function(data) {
						return Restangular.all(data.SET_WIDGET_SERVICE).post(('widget',widgetJson)).then(function(res) {
				    		  output=res.data.output;
				    		  defer.resolve(output);
					
				      },function(response) {
				    	    defer.reject(response);
				      });
						 defer.resolve(output);
					});
					return defer.promise;
			},
			getUpdatedDashboard: function(id) {
				 var defer = $q.defer();
				 var dashboard;
				 centinelUISvc.getSeviceApi().then(function(data) {
						return Restangular.all(data.GET_ALL_DASHBOARD_SERVICE).getList().then(function(res) {
							dashboard = _.where(res.data.dashboardRecord.dashboardList, {dashboardID: id});
			    		    defer.resolve(dashboard[0]);
				        	
				      },function(response) {
				    	    defer.reject(response);
				      });
						 defer.resolve(dashboard);
					});
					return defer.promise;
				 
			},
			getAllAlerts: function(streamId){
				var postJson = "{\"input\": {\"streamID\":\""+streamId+"\" } }";
				 var defer = $q.defer();
				 var alertArr =[];
				 centinelUISvc.getSeviceApi().then(function(data) {
						
						return Restangular.all(data.GET_ALL_ALERTS_SERVICE).post(('streams',postJson)).then(function(res) {
						_.each(res.data.output, function(listOfParticularType){
			    				  _.each(listOfParticularType, function(alert) {
			    					  			alertArr.push(alert);
								})		    				  
			    			 });
						defer.resolve(alertArr);
					
				        	
				      },function(response) {
				    	    console.log("Error with status code", response.status);
				    	    defer.reject(response);
				      });
						 defer.resolve(alertArr);
					});
					return defer.promise;
			}
		
		}
		
	}]);
	
});