/*define(['angularAMD', 'app/core/core.services','app/centinelUI/centinelUI.module','app/centinelUI/centinelUI.services'], function(centinelUIApp) {

	  centinelUIApp.register.controller('centinelUIAlertCtrl1', ['$scope', '$rootScope', 'centinelUISvc', function($scope, $rootScope, centinelUISvc) {

	    $rootScope['section_logo'] = ''; // Add your topbar logo location here such as 'assets/images/logo_topology.gif'

	    $scope.centinelUIInfo = {};

	    $scope.data = "alert temporary ";

	  }]);


	});*/

define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.controller('centinelUIAlertCtrl', ['$scope','addAlertServiceFactory', function ($scope,addAlertServiceFactory) {
		$scope.data = "alert temporary ";
		$scope.selectedCondition ="";
		$scope.submitted = false;
		$scope.enableConfig = true;
		  
  		$scope.alertConfigForm = { more : 'false', less : 'false', threshold: 0,
				timestamp :0, gracePeriod : 0, messageCount : 0,
				field : '', meanValue : '', thresholdLower :'false', 
				thresholdHigher: 'false',fieldContentSetTo : ''};
		//$scope.alertConfigFormMessageCount = { more : 'true', less : 'false', messageCountThreshold: 9, timestamp :0, gracePeriod : 0, messageCount : 0 };
		//$scope.alertConfigFormFieldValue = { field : '', meanValue : '', messageCountThreshold: 9, thresholdLower :'true', thresholdHigher: 'false', gracePeriod : 0, messageCount : 0,  fieldValueThreshold:0, timestamp:0 };
		//$scope.alertConfigFormFieldContent = { field : 'true', fieldContentSetTo : 'false',  timestamp :0, gracePeriod : 0};
        console.log('executing controller cvTopologyCtrl');
        
        $scope.addAlertConfigDiv = function(condition) {
        	console.info(" Condition ::"+condition);
        	 $scope.alertConfig = condition;
          
         };
         
         $scope.conditionTypes = [
                             {'ID': 'MESSAGE_COUNT' ,'conditionValue':'Message count condition'},
                             {'ID': 'FIELD_VALUE' ,'conditionValue':'Field value condition'},
                             {'ID': 'FIELD_CONTENT' ,'conditionValue':'Field content condition'}
                          ];
         
         
         $scope.addAlertCondition = function(condition) {
         	console.info(" Condition ::"+condition);
         	console.info(" alertConfigForm ::"+this.alertConfigForm);
         	var alertConfigFormObject = new Object();
         	alertConfigFormObject.more = this.alertConfigForm.more;
         	alertConfigFormObject.less = this.alertConfigForm.less;
         	alertConfigFormObject.threshold = this.alertConfigForm.messageCountThreshold;
         	alertConfigFormObject.timestamp = this.alertConfigForm.timestamp;
         	alertConfigFormObject.gracePeriod =this.alertConfigForm.gracePeriod;
         	alertConfigFormObject.messageCount = this.alertConfigForm.messageCount;
         	alertConfigFormObject.field= this.alertConfigForm.field;
         	alertConfigFormObject.meanValue= this.alertConfigForm.meanValue;
         	alertConfigFormObject.thresholdLower= this.alertConfigForm.thresholdLower;
         	alertConfigFormObject.thresholdHigher= this.alertConfigForm.thresholdHigher;
         	alertConfigFormObject.fieldContentSetTo= this.alertConfigForm.fieldContentSetTo;
         	console.info(" alertConfigFormObject ::");
         	console.info(alertConfigFormObject);
         	//addAlertServiceFactory.addAlertConfig(alertConfigFormObject);
         	$scope.enableConfig = false;
         	$scope.submitted = true;
          };
         
         
         
         
    }]);
	
	
});