define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {	
	
	
	

	centinelUIApp.register.directive('dynamic',function () {
		 return {
		       restrict: 'A',
		     
		       link: function(scope, element, attrs, ctrl) {
		    	  /* console.info("alertConfigForm alertConfigForm :: "+scope.alertConfigForm)*/
		    	scope.getContentUrl = function() {
		        	   if(scope.alertConfig=='MESSAGE_COUNT')
		        		   return 'messageCount.html';
		        	   if(scope.alertConfig=='FIELD_VALUE')
			                return 'fieldValue.html';
		        	   if(scope.alertConfig=='FIELD_CONTENT')
			                return 'fieldContent.html';
		           }
		          
		 
		       },
		       template: '<div ng-include="getContentUrl()"></div>'
		    
		   };
		});
	
	
});
