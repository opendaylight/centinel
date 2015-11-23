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

	centinelUIApp.register.controller('centinelUIStreamdataCtrl', ['$scope','$filter','$timeout','$translate','filteredListService','streamdataServiceFactory', function ($scope,$filter,$timeout,$translate,filteredListService,streamdataServiceFactory) {
		$scope.fieldList =[];
		$scope.eventList =[];
		$scope.submitSuccess =false;
		$scope.submitted = false;
		$scope.successMsg ="";
		$scope.selectedFields =[];
		$scope.tableColSpacing = '10px';
		$scope.messageTableDataList = [];
		$scope.pageSize = 10;
		$scope.NumberOfResults ="";
		$scope.eventStateToBeShownList = [];

		
		
		$scope.field = {"label":"",
						"value": false
		};
		
		
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
	    			 uniquefields.push(fieldsForEvent[j].fieldName);
	    		 uniquefields = _(uniquefields).unique(); 
	    	 }
	    	 for (var i = 0; i < uniquefields.length; i++) {
	    		 if(uniquefields[i]=='timestamp')
	    			 var fieldObj ="{\"label\":\""+uniquefields[i]+"\",\"value\":true}";
	    		 else
	    			 var fieldObj ="{\"label\":\""+uniquefields[i]+"\",\"value\":false}";
	    		 $scope.fieldList.push(angular.fromJson(fieldObj)); 
	    	 }
		};
		
		
		$scope.makeCallForGetAllEvents = function() {
			streamdataServiceFactory.getAllEvents().then(function(res) {
		    	if(res != '' && res!=undefined && res !=null )
		    		$scope.eventList = res;
		    	else
		    		$scope.eventList = [];
		    	$scope.getFields($scope.eventList);
				$scope.getSelectedFieldsToDisplay();
		    },function(response) {
	    	    console.log("Error with status code in controller", response.status);
				$translate('ERROR_RETRIEVE_LOG_MESSAGES').then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =false;
			$scope.submitted = true;
	      });
		};
		
		//$scope.makeCallForGetAllEvents();
		
		
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