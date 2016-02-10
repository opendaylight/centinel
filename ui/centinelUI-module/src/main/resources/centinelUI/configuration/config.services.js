define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.factory('configServiceFactory', ['$http','$q','Restangular','centinelUISvc',function($http,$q,Restangular,centinelUISvc) {
		
		return {
			
			setConfigIpAddr : function(ipJson){			
				 var defer = $q.defer();
				 var output ='';
				 centinelUISvc.getSeviceApi().then(function(data) {
						return Restangular.all(data.SET_CONFIG_SERVICE).post(('ip',ipJson)).then(function(res) {
				    		  output=res.data.output;
				    		  defer.resolve(output);
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