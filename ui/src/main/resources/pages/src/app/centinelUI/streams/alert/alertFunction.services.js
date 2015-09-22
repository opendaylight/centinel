define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {



centinelUIApp.register.factory('addAlertServiceFactory', ["Restangular", function(Restangular) {
	var service ="";
/*	var service = Restangular.service("addAlertConfig");  
	                        service.validateData = function(addAlertConfig) {
	                  
	                        }
	                       
	                        service.getAlertConfigResponse().then(function(data){
	                          
	                        });
	                        
	                        addAlertServiceFactory.addAlertConfig().then(function(data){
		                          
	                        });
	                        
	                        
	                        addAlertServiceFactory.post(student).then(function(data) {
	                        	   //interprete save result
	                        	});
	                     */   
	return service;
	}]);

	
	
	centinelUIApp.config(function(RestangularProvider) {
		var newBaseUrl = "";
		if (window.location.hostname == "localhost") {
			newBaseUrl = "http://localhost:8080/api/rest/register";
		} else {
				var deployedAt = window.location.href.substring(0, window.location.href);
				newBaseUrl = deployedAt + "/api/rest/addAlertConfig";
			}
		RestangularProvider.setBaseUrl(newBaseUrl);
	});
	
	
	/*centinelUIApp.register.factory('$templateCache',['$cacheFactory','$scope','$http','$injector', function($cacheFactory,$scope,$http, $injector) {
	var cache = $cacheFactory('templates');
	console.info(" inside $templateCache constructor")
	var allTplPromise;
	return{
		 get : function(url) {
			var fromCache = cache.get(url);
			console.info(" after fromCache")
			// already have required template in the cache
				if (fromCache) {
					console.info(" ckecking fromCache")
				return fromCache;
				}
			// first template request ever - get the all tpl file
				if (!allTplPromise) {
				allTplPromise = $http.get('src/app/centinelUI/streams/alert/alertConditionConfig.template.html').then(function(response) {
				// compile the response, which will put stuff into the cache
					console.info(" after http ajax call")
				$injector.get('$compile')(response.data);
				return response;
				});
				}
			// return the all-tpl promise to all template requests
			return allTplPromise.then(function(response) {
				console.info(" inside allTplPromise")
				return {
				status: response.status,
				data: cache.get(url)
				};
			});
		},
			
		 put : function(key, value) {
			 console.info(" inside put")
			cache.put(key, value);
			}
	
	};
	
}]);
*/

	
});










