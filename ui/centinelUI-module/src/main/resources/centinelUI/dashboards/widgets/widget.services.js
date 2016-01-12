define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.factory('widgetServiceFactory', ['$http','$q','Restangular','centinelUISvc',function($http,$q,Restangular,centinelUISvc) {
		
		return {
			
			getWidgetMessageCount : function(widgetJson){			
				 var defer = $q.defer();
				 var messageCount ='';
				 centinelUISvc.getSeviceApi().then(function(data) {
						return Restangular.all(data.GET_WIDGET_MESSAGE_COUNT).post(('widget',widgetJson)).then(function(res) {
							messageCount = res.data.output;
				    		  defer.resolve(messageCount);
				      },function(response) {
				    	    defer.reject(response);
				      });
				});
				return defer.promise;
			},
			
			getWidgetHistogram : function(widgetJson){			
				 var defer = $q.defer();
				 var histogramValues =[];
				 centinelUISvc.getSeviceApi().then(function(data) {
						return Restangular.all(data.GET_WIDGET_HISTOGRAM).post(('widget',widgetJson)).then(function(res) {
							  histogramValues = res.data.output;
				    		  defer.resolve(histogramValues);
				      },function(response) {
				    	    defer.reject(response);
				      });
				});
				return defer.promise;
			},
			
			getAllStream : function(){

				 var defer = $q.defer();
				 var streamArr =[];
				 centinelUISvc.getSeviceApi().then(function(data) {
						
						return Restangular.all(data.GET_ALL_STREAM_SERVICE).getList().then(function(res) {
							streamArr = res.data.streamRecord.streamList;
				    		  defer.resolve(streamArr);

				      },function(response) {
				    	    defer.reject(response);
				      });
						
						 defer.resolve(streamArr);
				});
					return defer.promise;
			},
			
			deleteWidget : function(inputJson){
				var defer = $q.defer();
				centinelUISvc.getSeviceApi().then(function(data) {
					return Restangular.all(data.DELETE_WIDGET_SERVICE).post(('widget',inputJson)).then(function(res) {
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