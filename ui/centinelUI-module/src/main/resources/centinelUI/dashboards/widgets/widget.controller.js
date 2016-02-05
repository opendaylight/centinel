define(['app/centinelUI/centinelUI.module','app/centinelUI/centinelUI.services'], function(centinelUIApp) {

  centinelUIApp.register.controller('centinelUIWidgetsCtrl', ['$filter','$scope', '$rootScope','$state','$stateParams','widgetServiceFactory','$timeout','$translate','createWidgetServiceFactory', function($filter,$scope,$rootScope,$state,$stateParams,widgetServiceFactory,$timeout,$translate,createWidgetServiceFactory) {

	  $scope.submitted = false;
	  //$rootScope.dashboard = $stateParams.dashboard;
	  $scope.dashboardName = $stateParams.dashboard.title;
	  $scope.widgetCounterValue = [];
	  $scope.widgetValuesForMsgCount = [];
	  $scope.widgetValuesForHistogram = [];
	  $scope.msgWidgetCount = 0;
	  $scope.histogramWidgetCount = 0;
	  /*$scope.dummyHistogramValues = [{"value":5,"timestamp":"2016-01-07 17:50:00"},{"value":10,"timestamp":"2016-01-07 17:45:00"},{"value":15,"timestamp":"2016-01-07 17:40:00"},{"value":10,"timestamp":"2016-01-07 17:35:00"},{"value":25,"timestamp":"2016-01-07 17:30:00"},{"value":40,"timestamp":"2016-01-07 17:25:00"},{"value":50,"timestamp":"2016-01-07 17:20:00"},{"value":30,"timestamp":"2016-01-07 17:15:00"},{"value":20,"timestamp":"2016-01-07 17:10:00"},{"value":5,"timestamp":"2016-01-07 17:05:00"}];*/
	  $scope.dummyHistogramValues = [];
	  $scope.streamList = '';
	  $scope.getAllStream = function(){
		  widgetServiceFactory.getAllStream().then(function(res){
			  $scope.streamList = res;
		  },function(response) {
				$translate('ERROR_RETRIEVE_STREAMS').then(function (translations) {
		    		 $scope.errorMsg =  translations;
			});
				$scope.submitSuccess =false;
				$scope.submitted = true;
		  });
	  };
	  if($scope.streamList == ''){
		  $scope.getAllStream();  
	  }
	  
	  $scope.widgetCount = function(){
		  var dashboardData = [];
		  dashboardData =  $stateParams.dashboard;
		  _.each(dashboardData.widgets, function(widget) {
			 if(widget.type == "message_count"){
				 $scope.msgWidgetCount = $scope.msgWidgetCount + 1;
			 }else if(widget.type == "histogram"){
				 $scope.histogramWidgetCount = $scope.histogramWidgetCount + 1;
			 } 
		  });
	  };
	  
	  $scope.widgetCount();
	  
	  /*$scope.displayMsgWidgetCount = function(){
		  if($scope.msgWidgetCount==$scope.widgetValuesForMsgCount.length){
			  return true;
		  }
		  return false;
	  };*/
	  
	  $scope.showWidgets = function(){
		  var dashboardData = [];
		  dashboardData =  $stateParams.dashboard;
		  var msgCountWidgetInputJson = "";
		  var histogramWidgetInputJson = "";
		  var alertList = '';
		  if(dashboardData.hasOwnProperty("widgets")){
			  var widgetCounter = 0;
			  _.each(dashboardData.widgets, function(widget) {
				  var valueArr = [];
				  if(widget.type == "message_count"){
						  msgCountWidgetInputJson = "{\"input\":{\"widgetID\":\""+widget.widgetID+"\"}}";
						  widgetServiceFactory.getWidgetMessageCount(msgCountWidgetInputJson).then(function(res) {
							  valueArr.push(widgetCounter);
							  widgetCounter = widgetCounter+1;
							  $scope.widgetCounterValue.push(res.value);
							  if(widget.mode=="stream"){
								  var stream = _.where($scope.streamList, {streamID: widget.streamID})[0];
								  valueArr.push((stream==undefined)?"Stream Deleted !!":stream.title);
								  valueArr.push((stream==undefined)?"":stream.description);
								  //valueArr.push(stream.title);
								  //valueArr.push(stream.description);
								  valueArr.push(widget.widgetID);
								  valueArr.push(widget.time_range/60);
								  valueArr.push(widget.mode);
								  $scope.widgetValuesForMsgCount.push(valueArr);
							  }else if(widget.mode=="alert"){
								  if(!(_.contains(alertList,{ruleID: widget.alertID}))){
									  createWidgetServiceFactory.getAllAlerts(widget.streamID).then(function(res1){
										  alertList = res1;
										  var alert = _.where(alertList, {ruleID: widget.alertID})[0];
										  valueArr.push((alert==undefined)?"Alert Deleted !!":alert.alertName);
										  valueArr.push('');
										  valueArr.push(widget.widgetID);
										  valueArr.push(widget.time_range/60);
										  valueArr.push(widget.mode);
										  valueArr.push(_.where($scope.streamList, {streamID: widget.streamID})[0].title);
										  $scope.widgetValuesForMsgCount.push(valueArr);
									  },function(response) {
										  valueArr.push("Alert Deleted !!");
										  valueArr.push('');
										  valueArr.push(widget.widgetID);
										  valueArr.push(widget.time_range/60);
										  valueArr.push(widget.mode);
										  valueArr.push('');
										  $scope.widgetValuesForMsgCount.push(valueArr);
									  });  
								  }else{
									  var alert = _.where(alertList, {ruleID: widget.alertID})[0];
									  valueArr.push((alert==undefined)?"Alert Deleted":alert.alertName);
									  //valueArr.push(alert.alertName);
									  valueArr.push('');
									  valueArr.push(widget.widgetID);
									  valueArr.push(widget.time_range/60);
									  valueArr.push(widget.mode);
									  valueArr.push(_.where($scope.streamList, {streamID: widget.streamID})[0].title);
									  $scope.widgetValuesForMsgCount.push(valueArr);
								  }
							  }
						},function(response) {
							$translate('ERROR_RETRIEVE_WIDGET_VALUE').then(function (translations) {
					    		 $scope.errorMsg =  translations;
					    	});
							$scope.submitSuccess =false;
							$scope.submitted = true;
						});  
				  }else if(widget.type == "histogram"){
					 /* $scope.date = new Date();
					  $scope.toTimestamp = $filter('date')($scope.date, 'yyyy-MM-dd HH:mm:ss');
					  $scope.fromTimestamp = $filter('date')(new Date($scope.date-(widget.time_range*10*1000)), 'yyyy-MM-dd HH:mm:ss');
					  console.info("toTimestamp : "+$scope.toTimestamp+"fromTimestamp : "+$scope.fromTimestamp);
					  	  histogramWidgetInputJson = "{\"input\":{\"widgetID\":\""+widget.widgetID+"\",\"fromTimestamp\":\""+$scope.fromTimestamp+"\",\"toTimestamp\":\""+$scope.toTimestamp+"\"}}";
						  widgetServiceFactory.getWidgetHistogram(histogramWidgetInputJson).then(function(res) {
							  valueArr.push(res.values);
							  valueArr.push(_.where($scope.streamList, {streamID: widget.streamID})[0].title);
							  valueArr.push(_.where($scope.streamList, {streamID: widget.streamID})[0].description);
							  valueArr.push("");
							  valueArr.push(widget.widgetID);
							  $scope.widgetValuesForHistogram.push(valueArr);
						},function(response) {
							/*$translate('ERROR_RETRIEVE_WIDGET_VALUE').then(function (translations) {
					    		 $scope.errorMsg =  translations;
					    	});
							$scope.submitSuccess =false;
							$scope.submitted = true;*/
							/*console.info('in showWidget of widget.controller.js');
							var emptyArray = [];
							valueArr.push(emptyArray);
							  valueArr.push("");
							  valueArr.push("");
							  valueArr.push("No Events Recieved!!");
							  valueArr.push(widget.widgetID);
							  $scope.widgetValuesForHistogram.push(valueArr);
					    });*/
							
					  $timeout(function(){
						  if(widget.mode=="stream"){
							  valueArr.push($scope.dummyHistogramValues);
							  valueArr.push(_.where($scope.streamList, {streamID: widget.streamID})[0].title);
							  valueArr.push(_.where($scope.streamList, {streamID: widget.streamID})[0].description);
							  valueArr.push("");
							  valueArr.push(widget.widgetID);
							  $scope.widgetValuesForHistogram.push(valueArr);
						  }  
					  },800);
					  
				  }
			  });
		  }
	  };
	  
	  $timeout(function(){
		  $scope.showWidgets();
	  },1000);
	  
	  $scope.addWidget = function() {
		  $scope.submitted = false;
		  $state.transitionTo('main.centinelUI.createWidgets', {dashboard: $stateParams.dashboard,streams: $scope.streamList});
	  };
		
	  $scope.refresh = function(){
		  $state.transitionTo('main.centinelUI.dashboards');
	  };
  }]);

});
