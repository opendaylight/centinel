define(['app/centinelUI/centinelUI.module','app/centinelUI/centinelUI.services'], function(centinelUIApp) {

  centinelUIApp.register.controller('centinelUICreateWidgetsCtrl', ['$scope', '$rootScope','$state','$stateParams','createWidgetServiceFactory','$timeout','$translate', function($scope,$rootScope,$state,$stateParams,createWidgetServiceFactory,$timeout,$translate) {

	  $scope.dashboardName = $stateParams.dashboard.title;
	  $scope.streamList = $stateParams.streams;
	  $scope.submitted = false;
	  $scope.submitSuccess =true;
	  $scope.submitting = false;
	  $scope.initWidgetFormValues= function() {
	 		$scope.createWidgetForm ={
	 			//"dashboardID":"",
	 				"streamID":"",
	 				"alertID":"",
	 		        "mode": "stream",
	 		        "type": "",
	 		        "time_range": ""
	 		      };
	  };
	  $scope.initWidgetFormValues();
	  
	  $scope.formValid = function(widgetForm){
		  if(widgetForm.mode=="stream" && (widgetForm.type=="" || widgetForm.streamID=="" || widgetForm.time_range=="")){
			  $scope.successMsg =  "All fields are required to be selected !!";
			  $scope.submitSuccess =false;
			  $scope.submitted = true;
			  return false;
		  }else if(widgetForm.mode=="alert" && (widgetForm.type=="" || widgetForm.streamID=="" || widgetForm.time_range=="" || widgetForm.alertID=="")){
			  $scope.successMsg =  "All fields are required to be selected !!";
			  $scope.submitSuccess =false;
			  $scope.submitted = true;
			  return false;
		  }
		  return true;
	  };

	  /*$scope.modeField = "Stream ID";
	  $scope.changeUIModeField = function(){
		if($scope.createWidgetForm.mode=="stream"){
			$scope.modeField = "Stream ID";
		}else if ($scope.createWidgetForm.mode=="alert"){
			$scope.modeField = "Alert ID";
		}  
	  };*/ 
	  $scope.alertList = [];
	  $scope.alertBoxShow = false;
	  
	  $scope.showAlertDropdown = function(){
		  if($scope.createWidgetForm.mode=="stream"){
			  	$scope.submitted = false;
				$scope.alertBoxShow = false;
			}else if ($scope.createWidgetForm.mode=="alert"){
				$scope.alertBoxShow = true;
			}
	  };
	  
	  $scope.populateAlertBox = function(){
		  $scope.submitted = false;
		  if($scope.createWidgetForm.mode=="alert" && !($scope.createWidgetForm.streamID=="")){
			  $scope.alertList = [];
			  $scope.getAllAlerts();
		  }
	  };
	  
	  $scope.getAllAlerts = function(){

		  createWidgetServiceFactory.getAllAlerts($scope.createWidgetForm.streamID).then(function(res){
			
			  $scope.alertList = res;

		  },function(response) {
				$translate('ERROR_RETRIEVE_ALERTS').then(function (translations) {
		    		 $scope.successMsg =  translations;
			});
				$scope.submitSuccess =false;
				$scope.submitted = true;
		  });
	  };
	  
	  $scope.addWidget = function(widgetForm){
		  if($scope.formValid(widgetForm)){
			  $scope.submitting = true;
			  var widgetInputJson = "";
			  if(widgetForm.mode=="stream"){
				  widgetInputJson = "{\"input\":{\"dashboardID\" :\""+$stateParams.dashboard.dashboardID+"\",\"streamID\" :\""+widgetForm.streamID+"\",\"mode\" : \""+widgetForm.mode+"\",\"type\":\""+widgetForm.type+"\",\"time_range\":\""+widgetForm.time_range+"\"}}"
			  }else if(widgetForm.mode=="alert"){
				  widgetInputJson = "{\"input\":{\"dashboardID\" :\""+$stateParams.dashboard.dashboardID+"\",\"streamID\" :\""+widgetForm.streamID+"\",\"alertID\" :\""+widgetForm.alertID+"\",\"mode\" : \""+widgetForm.mode+"\",\"type\":\""+widgetForm.type+"\",\"time_range\":\""+widgetForm.time_range+"\"}}"
			  }
			  createWidgetServiceFactory.setWidget(widgetInputJson).then(function(res) {
		     		$timeout(function(){
				       	 $translate('WIDGET_ADD_EDIT_DELETE_SUCCESS', { crud: 'added' }).then(function (translations) {
				    		 $scope.successMsg =  translations;
				    	 });
				       	//$scope.submitSuccess =true;
				       	//$scope.submitted = true;
				       	//$scope.initWidgetFormValues();
				       	$scope.getDashboardByID();
				       	$scope.submitting = false;
		     		}, 5000);
				},function(response) {
					$translate('WIDGET_ADD_EDIT_DELETE_SUCCESS', { crud: 'not added' }).then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
					$scope.submitting = false;
					$scope.submitSuccess =false;
					$scope.submitted = true;
					$scope.initWidgetFormValues();
		      });
		  }
	  };
	  
	  $scope.getDashboardByID = function(){
		  createWidgetServiceFactory.getUpdatedDashboard($stateParams.dashboard.dashboardID).then(function(res) {
			  //$timeout(function(){
				  $scope.updatedDashboard = res;
				  $scope.initWidgetFormValues();
				  $state.transitionTo('main.centinelUI.widgets',{dashboard: $scope.updatedDashboard});
			  //}, 5000);
		    },function(response) {
				$translate('ERROR_RETRIEVE_DASHBOARDS').then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =false;
				$scope.submitted = true;
	      });
	  };
	  
	  /*$scope.transit = function(){
		  $state.transitionTo('main.centinelUI.widgets',{dashboard: $scope.updatedDashboard});
	  };*/
	  
	  $scope.closeForm = function() {
		  $state.transitionTo('main.centinelUI.widgets',{dashboard: $stateParams.dashboard});
		  //$scope.getDashboardByID();
		  //$scope.transit();
	  };
	  $scope.refresh = function(){
		  $state.transitionTo('main.centinelUI.dashboards');
	  };
  }]);

});
