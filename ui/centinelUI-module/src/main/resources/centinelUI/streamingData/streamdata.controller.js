/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : this js is controller for streamingData functionality in CentinelUI
 */

define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.controller('centinelUIStreamdataCtrl', ['$scope','$timeout','$filter','$timeout','$translate','filteredListService','streamdataServiceFactory', function ($scope,$timeout,$filter,$timeout,$translate,filteredListService,streamdataServiceFactory) {
		$scope.fieldList =[];
		$scope.eventList =[];
		$scope.submitSuccess =false;
		$scope.submitted = false;
		$scope.successMsg ="";
		$scope.selectedFields =[];
		$scope.tableColSpacing = '10px';
		$scope.pageSize = 10;
		$scope.NumberOfResults ="";
		$scope.eventStateToBeShownList = [];
		$scope.queryString = '';
		$scope.queryList = [];
		$scope.chartData =[];
		$scope.streamingDataType = "stream";
		$scope.tableRowHeight = '28px';
		$scope.eventType = 'Streams';
		
		$scope.reset = function(){
			$scope.queryList = [];
			$scope.eventStateToBeShownList = [];
			$scope.selectedFields =[];
			$scope.fieldList =[];
			$scope.eventList =[];
			$scope.queryString = '';
			$scope.submitted = false;
		}; 
		
		$scope.field = {"label":"",
						"value": false
		};
		$scope.searchRange = [{'rangeId':'5 Minutes', 'rangeMsg':'Search In last 5 Minutes'},
		                       {'rangeId':'15 Minutes', 'rangeMsg':'Search In last 15 Minutes'},
		                       {'rangeId':'30 Minutes', 'rangeMsg':'Search In last 30 Minutes'},
		                       {'rangeId':'1 Hours', 'rangeMsg':'Search In last 1 Hours'},
		                       {'rangeId':'2 Hours', 'rangeMsg':'Search In last 2 Hours'},
		                       {'rangeId':'8 Hours', 'rangeMsg':'Search In last 8 Hours'},
		                       {'rangeId':'1 Day', 'rangeMsg':'Search In last 1 Day'},
		                       {'rangeId':'2 Day', 'rangeMsg':'Search In last 2 Day'},
		                       {'rangeId':'5 Day', 'rangeMsg':'Search In last 5 Day'},
		                       {'rangeId':'7 Day', 'rangeMsg':'Search In last 7 Day'},
		                       {'rangeId':'14 Day', 'rangeMsg':'Search In last 14 Day'},
		                       {'rangeId':'30 Day', 'rangeMsg':'Search In last 30 Day'},
		                       {'rangeId':'all', 'rangeMsg':'Search in all Message'}];
		                       
		                       
		
		$scope.selectStreaminDataType = function(dataType) {
			if(dataType =='alert')
				$scope.eventType = 'Alarms';
			else
				$scope.eventType = 'Streams';
			$scope.tableRowHeight = '28px';
			$scope.streamingDataType = dataType;
			console.info("streamingDataType :: ");
			console.info($scope.streamingDataType);
			$scope.makeCallForGetAllEvents('');
		}
		
		$scope.makeLogQuery = function() {
			var queryJson ="";
			console.info('query string ::');
			console.info($scope.queryString );
			if($scope.queryString == '' || $scope.queryString == undefined || $scope.queryString == null)
				queryJson = "{\"input\": {\"queryString\": \"select "+ $scope.streamingDataType +" from centinel\",\"limit\": 50}}";
			else{
				var whereClause = $scope.getFormattedDbQuery($scope.queryString);
				queryJson = "{\"input\": {\"queryString\": \"select "+ $scope.streamingDataType +" from centinel where "+whereClause+ "\" ,\"limit\": 50}}";
			}
			console.info('queryJson ::');
			console.info(queryJson);
			$scope.makeCallForGetAllEvents(queryJson);
		}

		$scope.getFormattedDbQuery = function(query) {
			var res = query.split(" and ");
			    for (i = 0; i < res.length; i++) { 
			     if(res[i].indexOf('=')>0){
			      var temp = res[i];
			      temp = $scope.streamingDataType+"."+temp;
			      temp = temp.replace("=", "='");
			      res[i]= temp+"'";
			      }
			    }
			var whereClause= '';
			    for (i = 0; i < res.length; i++) 
			    	whereClause= whereClause+" "+res[i]+' and';
			    whereClause = whereClause.substring(0,whereClause.lastIndexOf('and'));
			    return whereClause;
		}
		
		$scope.getSelectedFieldsToDisplay = function() {
			var eventArr = $scope.eventList;
			$scope.eventStateToBeShownList = [];
			
			$scope.selectedFields = _.where($scope.fieldList, {"value": true}) ;
			for (var i = 0; i < eventArr.length; i++) {
				var fieldInThisEvent = eventArr[i].fields;
				var selectedFieldInThisEvent =[];
				for (var j = 0; j < $scope.selectedFields.length; j++) {
					var fieldFound = (_.where(fieldInThisEvent, {"fieldName": $scope.selectedFields[j].label }))[0] ;
					selectedFieldInThisEvent.push(fieldFound);
				}
				$scope.eventStateToBeShownList.push(selectedFieldInThisEvent);
			}
			$scope.tableColSpacing = (940/$scope.selectedFields.length) + "px";
			if(($scope.selectedFields.length>4 && $scope.streamingDataType=='stream')||($scope.selectedFields.length>3 && $scope.streamingDataType=='alert'))
				$scope.tableRowHeight = 18*($scope.selectedFields.length-2) + "px";
			else
				$scope.tableRowHeight = '28px';
			$scope.search();
		}; 

		
		$scope.newFieldSelected = function(field) {
			$scope.getSelectedFieldsToDisplay();
		};
		


		$scope.getFields = function(events){
			var uniquefields = [];
	    	 for (var i = 0; i < events.length; i++) {
	    		 var fieldsForEvent = events[i].fields;
	    		 for (var j = 0; j < fieldsForEvent.length; j++) 
	    			 if(fieldsForEvent[j].fieldName.length) 
	    				 uniquefields.push(fieldsForEvent[j].fieldName);
	    		 uniquefields = _(uniquefields).unique(); 
	    	 }
	    	 for (var i = 0; i < uniquefields.length; i++) {
	    		 if(uniquefields[i]=='stream:title' ||uniquefields[i]=='check_result:triggeredCondition:type' ||uniquefields[i]=='check_result:triggeredCondition:type' ||uniquefields[i]=='event_timestamp' || uniquefields[i]=='check_result:triggeredAt'||uniquefields[i]=='fields:severity'||uniquefields[i]=='fields:message'||uniquefields[i]=='source')
	    			 var fieldObj ="{\"label\":\""+uniquefields[i]+"\",\"value\":true}";
	    		 else
	    			 var fieldObj ="{\"label\":\""+uniquefields[i]+"\",\"value\":false}";
	    		 $scope.queryList.push(angular.fromJson("{\"fieldName\":\""+uniquefields[i]+"=\"}"));
	    		 $scope.fieldList.push(angular.fromJson(fieldObj)); 
	    	 }
		};
		
		
	    $scope.getChartData = function() {
	    	 streamdataServiceFactory.getChartDataFromLocal().then(function(res) {
	    		 $scope.chartData = res.chartData; 
	    	 });
		};
		
		//$scope.getChartData();
		
		$scope.makeCallForGetAllEvents = function(queryString) {
			var query ='';
			if($scope.queryString =='' || $scope.queryString == undefined || $scope.queryString == null)
				query = "{\"input\": {\"queryString\": \"select "+ $scope.streamingDataType +" from centinel\",\"limit\": 50}}";
			else
				query = queryString;
				$scope.reset();
			
			 //streamdataServiceFactory.getAllEventsFromLocal(query).then(function(res) {
			streamdataServiceFactory.getAllEvents(query).then(function(res) {
		    	if(res != '' && res!=undefined && res !=null )
		    		$scope.eventList = res;
		    	else
		    		$scope.eventList = [];
		    	$scope.getFields($scope.eventList);
				$scope.getSelectedFieldsToDisplay();
				$scope.queryString='';
		    },function(response) {
	    	    console.log("Error with status code in controller", response.status);
				$translate('ERROR_RETRIEVE_LOG_MESSAGES').then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.search();
				$scope.submitSuccess =false;
			$scope.submitted = true;
	      });
		};
		
		$scope.makeCallForGetAllEvents('');
/*		$interval(function() {
	        console.debug("REFRESHING....");
	        $scope.makeCallForGetAllEvents();
	      }, 5000);*/
		
		
		 $scope.search = function () {
			 	$scope.filteredList = $scope.eventStateToBeShownList;
			 	$scope.pagination($scope.filteredList);
		        if($scope.filteredList.length<5)
			       	 $translate('NO_OF_RESULTS', { firstPageResults: $scope.filteredList.length, total: $scope.filteredList.length }).then(function (translations) {
			       		$scope.NumberOfResults =  translations;
			    	 });
		        else
		        	$translate('NO_OF_RESULTS', { firstPageResults: $scope.pageSize, total: $scope.filteredList.length }).then(function (translations) {
			       		$scope.NumberOfResults =  translations;
			    	 });
		        $scope.firstPage();
		    }
		
		    $scope.pagination = function (list) {
		    	if (!$scope.$$phase){
		    		$scope.$apply(function () {
				        $scope.eventsByPage = filteredListService.paged(list, $scope.pageSize);
				    	});
		    	}else{
		    		 $scope.eventsByPage = filteredListService.paged(list, $scope.pageSize);
		    	}
		    	
		    };
		
		    $scope.setPage = function () {
		        $scope.currentPage = this.n;
		    };
		
		    $scope.firstPage = function () {
		        $scope.currentPage = 0;
		    };
		
		    $scope.lastPage = function () {
		        $scope.currentPage = $scope.eventsByPage.length - 1;
		    };
		
		    $scope.range = function (input, total) {
		        var ret = [];
		        if (!total) {
		            total = input;
		            input = 0;
		        }
		        for (var i = input; i < total; i++) {
		            if (i != 0 && i != total - 1) {
		                ret.push(i);
		            }
		        }
		        return ret;
		    };
		

		
		
		
	}]);
});
