/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : this js is controller for alert functionality in CentinelUI
 */
define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.controller('centinelUIAlertCtrl', ['$scope','$filter','$stateParams','$timeout','$translate','filteredListService','addAlertServiceFactory','centinelUISvc', function ($scope,$filter,$stateParams,$timeout,$translate,filteredListService,addAlertServiceFactory,centinelUISvc) {
		
			$scope.selectedCondition ="";
			$scope.submitted = false;
			$scope.enableConfig = false;
			$scope.enableEditButton =false;
			$scope.enableAddButton =false;
		    $scope.pageSize = 5;
		    $scope.searchText ='';
		    $scope.alertList = [];
		    $scope.reverse = false;
		    $scope.successMsg = "";
		    $scope.noAlertSubscription = true;
		    $scope.alertSubscription = false;
		    $scope.streamIDForTheseAlerts = $stateParams.streamID;
		    $scope.validationMsg  ="";
 
		    $scope.makeAllAlertCall = function() {
			    addAlertServiceFactory.getAllAlerts($scope.streamIDForTheseAlerts).then(function(res) {
			    	if(res != '' && res!=undefined && res !=null )
			    		$scope.alertList = res;
			    	else
			    		$scope.alertList = [];
					$scope.search();
			    },function(response) {
			    	if(response.status!=404){
			    		console.log("Error with status code in controller", response.status);
						$translate('ERROR_RETRIEVE_ALERTS').then(function (translations) {
				    		 $scope.successMsg =  translations;
				    	 });
						$scope.submitSuccess =false;
					$scope.submitted = true;
			    	}else{
			    		$translate('NO_ALERTS_FOUND').then(function (translations) {
				    		 $scope.successMsg =  translations;
				    	 });
			    		$scope.submitSuccess =true;
						$scope.submitted = true;
			    	}
		    	    
		      });
			};

			  $scope.makeAllAlertCall();
		    
		    $scope.addAlertSubscription = function() {
		    	 $scope.reset();
		    	$scope.noAlertSubscription = false;
		    	$scope.alertSubscription = true;
				
			};
			
			$scope.makeNewAlertSubscription = function(formObj) {
		    	$scope.submitting=true;
				var subscribeSvcJson = "{\"input\":{\"userName\":\""+formObj.userName+"\",\"mode\":\""+formObj.mode+"\",\"URL\":\""+formObj.URL+"\"}}";
				centinelUISvc.postService('SUBSCRIBE_SERVICE',subscribeSvcJson).then(function(res) {
			       	 $translate('ALERT_SUBSCRIBE_ADD_SUCCESS', { crud: 'Subscribed' }).then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
				    	$scope.noAlertSubscription = true;
				    	$scope.alertSubscription = false;
				    	$scope.submitting=false;
						$scope.submitSuccess =true;
						$scope.submitted = true;
				},function(response) {
		    	    console.log("Error with status code in controller", response.status);
					$translate('ALERT_SUBSCRIBE_ADD_SUCCESS', { crud: 'not Subscribed' }).then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
					$scope.submitSuccess =false;
			    	$scope.submitting=false;
				$scope.submitted = true;
		      });
			};
			
		    $scope.addAlertConfigDiv = function(condition) {
		    	 $scope.reset();
		    	$scope.resetConfigFormValues(condition);
		    	 $scope.alertConfig = condition;
		    	 $scope.enableConfig = true;
		    	 $scope.submitted = false;
		    	 $scope.enableAddButton =true;
		    	 $scope.enableEditButton =false;
		      
		     };
		     
		     $scope.resetConfigFormValues= function() {
			    	$scope.enableConfig = false;
			 		$scope.alertMessageCountConfigForm ={
			 				"alertName":"",
			 				"configID": "",
			                "streamID": "",
			                "alertTypeClassifier": "alert-message-count",
			                "messageCountBacklog":1 ,
			                "timeStamp":1,
			                "ruleID": "string",
			                "ruleTypeClassifier": "base-alert",
			                "messageCountGrace":1 ,
			                "messageCountCount":1 ,
			                "messageCountOperator": "more",
			                "nodeType": "str3"
			              
			            };
				  		$scope.alertFieldValueConfigForm = {
				  				"alertName":"",
				  				"configID": "",
				                "alertTypeClassifier": "alert-field-value",
				                "fieldValueThreshholdType": "higher",
				                "timeStamp":1 ,
				                "fieldValueField": "",
				                "fieldValueGrace":1 ,
				                "fieldValueType": "mean value",
				                "streamID": "",
				                "ruleID": "str12",
				                "ruleTypeClassifier": "base-alert",
				                "fieldValueThreshhold":1,
				                "nodeType": "string",
				                "fieldValueBacklog":1
				            };
				  			
				  			$scope.alertFieldContentConfigForm =     {
				  					"alertName":"",
				  					"configID": "",
				  	                "streamID": "",
				  	                "alertTypeClassifier": "alert-field-content",
				  	                "timeStamp":1 ,
				  	                "ruleID": "str54",
				  	                "fieldContentBacklog":1 ,
				  	                "fieldContentField": "",
				  	                "ruleTypeClassifier": "base-alert",
				  	                "fieldContentGrace":1 ,
				  	                "nodeType": "str22",
				  	                "fieldContentCompareToValue": ""
				  	            };
			
				};
		     
		     $scope.conditionTypes = [
		                         {'ID': 'alert-message-count' ,'conditionValue':'Message Count Condition'},
		                         {'ID': 'alert-field-value' ,'conditionValue':'Field Value Condition'},
		                         {'ID': 'alert-field-content' ,'conditionValue':'Field Content Condition'}
		                      ];
		     
		     $scope.subscriptionTypes = [
				                         {'ID': 'http' ,'type':'Http Subscription'}
				                      ];
		     
		     $scope.alertSubscriptionForm = {
			    		"URL":"Enter a url like www.tcs.com/alerts",
			    		"userName":"Enter the User Name",
			     		"mode":"alert"
					};
		     
			    $scope.reset = function() {
					$scope.submitSuccess =false;
					$scope.submitted = false;
				};
		     
		   $scope.resetAll = function () {
		        $scope.filteredList = $scope.alertList;
		        $scope.searchText = '';
		        $scope.currentPage = 0;
		        $scope.Header = ['', '', ''];
		        $scope.pagination( $scope.filteredList);
		    }

		    $scope.search = function () {
		        $scope.filteredList = filteredListService.searched($scope.alertList, $scope.searchText);
		        if ($scope.searchText == '') {
		            $scope.filteredList = $scope.alertList;
		        }
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
				        $scope.alertsByPage = filteredListService.paged(list, $scope.pageSize);
				    	});
		    	}else{
		    		 $scope.alertsByPage = filteredListService.paged(list, $scope.pageSize);
		    	}
		    	
		    };
		
		    $scope.setPage = function () {
		        $scope.currentPage = this.n;
		    };
		
		    $scope.firstPage = function () {
		        $scope.currentPage = 0;
		    };
		
		    $scope.lastPage = function () {
		        $scope.currentPage = $scope.alertsByPage.length - 1;
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

		    
		$scope.addAlertCondition = function(condition,formObject) {
			$scope.submitting=true;
			if($scope.enableAddButton){
		     	var setAlertSvcJson = "";
		     	
		     	if(formObject.alertTypeClassifier=='alert-message-count'){
		     		 setAlertSvcJson = "{\"input\": {\"streamID\":\"" + $scope.streamIDForTheseAlerts+"\",\"alertName\":\"" + formObject.alertName +"\",\"alertTypeClassifier\":\"" + formObject.alertTypeClassifier + "\",\"messageCountBacklog\":"+ formObject.messageCountBacklog+",\"timeStamp\":" + formObject.timeStamp + ",\"ruleID\":\"" + formObject.ruleID +"\",\"ruleTypeClassifier\":\"" + formObject.ruleTypeClassifier +"\",\"messageCountGrace\":" + formObject.messageCountGrace + ",\"messageCountCount\":" + formObject.messageCountCount	+ ",\"messageCountOperator\":\"" + formObject.messageCountOperator	+"\",\"nodeType\":\"" + formObject.nodeType	+ "\"}}" ;	
		     	}
		     	if(formObject.alertTypeClassifier=='alert-field-value'){
		     		 setAlertSvcJson = "{\"input\": {\"streamID\":\"" + $scope.streamIDForTheseAlerts+"\",\"alertName\":\"" + formObject.alertName +"\",\"alertTypeClassifier\":\"" + formObject.alertTypeClassifier + "\",\"fieldValueThreshholdType\":\""+ formObject.fieldValueThreshholdType+"\",\"timeStamp\":" + formObject.timeStamp + ",\"ruleID\":\"" + formObject.ruleID +"\",\"ruleTypeClassifier\":\"" + formObject.ruleTypeClassifier +"\",\"fieldValueGrace\":" + formObject.fieldValueGrace + ",\"fieldValueField\":\"" + formObject.fieldValueField	+ "\",\"fieldValueType\":\"" + formObject.fieldValueType+"\",\"nodeType\":\"" + formObject.nodeType	+"\",\"fieldValueThreshhold\":" + formObject.fieldValueThreshhold+",\"fieldValueBacklog\":" + formObject.fieldValueBacklog+ "}}" ;																																																																																																																																																																							     																																																																																																																																										     	
		     	}
		     	if(formObject.alertTypeClassifier=='alert-field-content'){
		     		 setAlertSvcJson = "{\"input\": {\"streamID\":\"" + $scope.streamIDForTheseAlerts+"\",\"alertName\":\"" + formObject.alertName +"\",\"alertTypeClassifier\":\"" + formObject.alertTypeClassifier + "\",\"fieldContentBacklog\":"+ formObject.fieldContentBacklog+",\"timeStamp\":" + formObject.timeStamp + ",\"ruleID\":\"" + formObject.ruleID +"\",\"ruleTypeClassifier\":\"" + formObject.ruleTypeClassifier +"\",\"fieldContentGrace\":" + formObject.fieldContentGrace + ",\"fieldContentField\":\"" + formObject.fieldContentField	+ "\",\"fieldContentCompareToValue\":\"" + formObject.fieldContentCompareToValue+"\",\"nodeType\":\"" + formObject.nodeType+ "\"}}" ;																																																																																																																																																																							     																																																																																																																																										     	
		     	}
		
		     	addAlertServiceFactory.setAlertConfiguration(formObject,setAlertSvcJson).then(function(setAlertRes) {
		     		var configIDForThis = setAlertRes.configID;
		     		$timeout(function(){
					    addAlertServiceFactory.getAllAlerts($scope.streamIDForTheseAlerts).then(function(res) {
					    	var newAlertList =[];
					    	if(res != '' && res!=undefined && res !=null )
					    		newAlertList = res;
					    	
					    	if(_.where(newAlertList, {"configID": configIDForThis}).length > 0 ){
					     		$scope.alertList = [];
					     		$scope.alertList = newAlertList;

								$scope.enableConfig = false;
						     	if($scope.enableAddButton){
						     		//$scope.addAlertToTable(formObject);
							       	 $translate('ALERT_ADD_EDIT_DELETE_SUCCESS', { crud: 'added' }).then(function (translations) {
							    		 $scope.successMsg =  translations;
							    	 });
						     	}
								$scope.submitSuccess =true;
								$scope.submitted = true;

				     		}else{
								$scope.enableConfig = false;
						     	if($scope.enableAddButton){
						     		//$scope.addAlertToTable(formObject);
							       	 $translate('ALERT_ADD_EDIT_DELETE_SUCCESS', { crud: 'not added' }).then(function (translations) {
							    		 $scope.successMsg =  translations;
							    	 });
						     	}
								$scope.submitSuccess =false;
								$scope.submitted = true;
				     		}
					    	$scope.submitting=false;
					    	$scope.search();
					    	
					    });
		     		}, 5000);
				},function(response) {
		    	    console.log("Error with status code in controller", response.status);
					$translate('ALERT_ADD_EDIT_DELETE_SUCCESS', { crud: 'not added' }).then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
					$scope.submitSuccess =false;
			    	$scope.submitting=false;
				$scope.submitted = true;
		      });
			}else{
				$scope.editAlert(formObject);
		}

	};
		      
		     $scope.deleteAlert = function(alert) {
		    	if (confirm("Are you sure, you want to delete this Alert!") == true) {
				    $scope.submitting=true;
			    	$scope.reset();
					var formObject = filteredListService.searchItem($scope.alertList, alert.ruleID,"ruleID");
			     	var deleteAlertSvcJson = "{\"input\": {\"streamID\":\"" + formObject.streamID+"\",\"configID\":\"" + formObject.configID + "\",\"ruleID\":\""+ formObject.ruleID+ "\"}}" ;																																																																																																																																																																							     																																																																																																																																										     	
			     	var ruleIDForThis = formObject.ruleID;
			     	addAlertServiceFactory.deleteAlertConfiguration(alert,deleteAlertSvcJson).then(function(deleteAlertRes) {
			     		$timeout(function(){
						    addAlertServiceFactory.getAllAlerts($scope.streamIDForTheseAlerts).then(function(res) {
						    	var newAlertList =[];
						    	if(res != '' && res!=undefined && res !=null )
						    		newAlertList = res;
						    	
							    if(_.where(newAlertList, {"ruleID": ruleIDForThis}).length == 0 || _.where(newAlertList, {"ruleID": ruleIDForThis}) == []){
						     		$scope.alertList = [];
						     		$scope.alertList = newAlertList;
					     			$translate('ALERT_ADD_EDIT_DELETE_SUCCESS', { crud: 'deleted' }).then(function (translations) {
							    		 $scope.successMsg =  translations;
							    	 });
									$scope.submitSuccess =true;
									$scope.submitted = true;
					     		}else{
					     			$translate('ALERT_ADD_EDIT_DELETE_SUCCESS', { crud: 'not deleted' }).then(function (translations) {
							    		 $scope.successMsg =  translations;
							    	 });
									$scope.submitSuccess =false;
									$scope.submitted = true;
					     		}
				     			
							        if($scope.alertList.length<5)
								       	 $translate('NO_OF_RESULTS', { firstPageResults: $scope.alertList.length, total: $scope.alertList.length }).then(function (translations) {
								       		$scope.NumberOfResults =  translations;
								    	 });
							        else
							        	$translate('NO_OF_RESULTS', { firstPageResults: $scope.pageSize, total: $scope.alertList.length }).then(function (translations) {
								       		$scope.NumberOfResults =  translations;
								    	 });
							        $scope.search();
							    	$scope.submitting=false;
	
						    });
			     		},5000);
	
			     	},function(response) {
			    	    console.log("Error with status code in controller", response.status);
						$translate('ALERT_ADD_EDIT_DELETE_SUCCESS', { crud: 'not deleted' }).then(function (translations) {
				    		 $scope.successMsg =  translations;
				    	 });
						$scope.submitSuccess =false;
				    	$scope.submitting=false;
					$scope.submitted = true;
			      });
		    	}else
		    		return;

			};
			
			
			$scope.editAlertClicked = function(alert) {
				var formObject = filteredListService.searchItem($scope.alertList, alert.ruleID,"ruleID");
				if(formObject.alertTypeClassifier=='alert-message-count'){
					$scope.alertMessageCountConfigForm = formObject;

				}
				if(formObject.alertTypeClassifier=='alert-field-value'){
					$scope.alertFieldValueConfigForm = formObject;

				}
				if(formObject.alertTypeClassifier=='alert-field-content'){
					$scope.alertFieldContentConfigForm = formObject;
				}

			 	$scope.submitted = false;
	        	$scope.alertConfig = formObject.alertTypeClassifier;
	        	$scope.enableConfig = true;
	        	$scope.enableEditButton =true;
	        	$scope.enableAddButton =false;
			};
		     
			 $scope.editAlert = function(alert){
			    	$scope.submitting=true;
					var formObject = filteredListService.searchItem($scope.alertList, alert.ruleID,"ruleID");
		        	var editAlertSvcJson ='';
		        	if(formObject.alertTypeClassifier=='alert-message-count'){
			     		 editAlertSvcJson = "{\"input\": {\"streamID\":\"" + formObject.streamID+"\",\"alertName\":\"" + formObject.alertName +"\",\"configID\":\"" + formObject.configID +"\",\"alertTypeClassifier\":\"" + formObject.alertTypeClassifier + "\",\"messageCountBacklog\":"+ formObject.messageCountBacklog+",\"timeStamp\":" + formObject.timeStamp + ",\"ruleID\":\"" + formObject.ruleID +"\",\"ruleTypeClassifier\":\"" + formObject.ruleTypeClassifier +"\",\"messageCountGrace\":" + formObject.messageCountGrace + ",\"messageCountCount\":" + formObject.messageCountCount	+ ",\"messageCountOperator\":\"" + formObject.messageCountOperator	+"\",\"nodeType\":\"" + formObject.nodeType	+ "\"}}" ;	
		        	}
		        	if(formObject.alertTypeClassifier=='alert-field-value'){
		        		editAlertSvcJson = "{\"input\": {\"streamID\":\"" + formObject.streamID+"\",\"alertName\":\"" + formObject.alertName +"\",\"configID\":\"" + formObject.configID +"\",\"alertTypeClassifier\":\"" + formObject.alertTypeClassifier + "\",\"fieldValueThreshholdType\":\""+ formObject.fieldValueThreshholdType+"\",\"timeStamp\":" + formObject.timeStamp + ",\"ruleID\":\"" + formObject.ruleID +"\",\"ruleTypeClassifier\":\"" + formObject.ruleTypeClassifier +"\",\"fieldValueGrace\":" + formObject.fieldValueGrace + ",\"fieldValueField\":\"" + formObject.fieldValueField	+ "\",\"fieldValueType\":\"" + formObject.fieldValueType+"\",\"nodeType\":\"" + formObject.nodeType	+"\",\"fieldValueThreshhold\":" + formObject.fieldValueThreshhold+",\"fieldValueBacklog\":" + formObject.fieldValueBacklog+ "}}" ;																																																																																																																																																																							     																																																																																																																																										     	
		        	}
		        	if(formObject.alertTypeClassifier=='alert-field-content'){
			     		 editAlertSvcJson = "{\"input\": {\"streamID\":\"" + formObject.streamID+"\",\"alertName\":\"" + formObject.alertName +"\",\"configID\":\"" + formObject.configID +"\",\"alertTypeClassifier\":\"" + formObject.alertTypeClassifier + "\",\"fieldContentBacklog\":\""+ formObject.fieldContentBacklog+"\",\"timeStamp\":" + formObject.timeStamp + ",\"ruleID\":\"" + formObject.ruleID +"\",\"ruleTypeClassifier\":\"" + formObject.ruleTypeClassifier +"\",\"fieldContentGrace\":" + formObject.fieldContentGrace + ",\"fieldContentField\":\"" + formObject.fieldContentField	+ "\",\"fieldContentCompareToValue\":" + formObject.fieldContentCompareToValue+",\"nodeType\":\"" + formObject.nodeType+ "\"}}" ;																																																																																																																																																																							     																																																																																																																																										     	
		        	}
		        	addAlertServiceFactory.editAlertConfiguration(formObject,editAlertSvcJson).then(function(updateAlertRes) {
						var ruleIDForThis = updateAlertRes.ruleID;
						$timeout(function(){
						    addAlertServiceFactory.getAllAlerts($scope.streamIDForTheseAlerts).then(function(res) {
						    	var newAlertList =[];
						    	if(res != '' && res!=undefined && res !=null )
						    		newAlertList = res;
						    	
							    if(_.where(newAlertList, {"ruleID": ruleIDForThis}).length == 1){
						     		$scope.alertList = [];
						     		$scope.alertList = newAlertList;
					        		$translate('ALERT_ADD_EDIT_DELETE_SUCCESS', { crud: 'edited' }).then(function (translations) {
							    		 $scope.successMsg =  translations;
							    	 });
					        		$scope.enableConfig = false;
									$scope.submitSuccess =true;
					     		}else{
					        		$translate('ALERT_ADD_EDIT_DELETE_SUCCESS', { crud: 'not edited' }).then(function (translations) {
							    		 $scope.successMsg =  translations;
							    	 });
									$scope.submitSuccess =false;
					     		}
				     			
							        if($scope.alertList.length<5)
								       	 $translate('NO_OF_RESULTS', { firstPageResults: $scope.alertList.length, total: $scope.alertList.length }).then(function (translations) {
								       		$scope.NumberOfResults =  translations;
								    	 });
							        else
							        	$translate('NO_OF_RESULTS', { firstPageResults: $scope.pageSize, total: $scope.alertList.length }).then(function (translations) {
								       		$scope.NumberOfResults =  translations;
								    	 });
							        $scope.search();
							    	$scope.submitting=false;
						    });
			     		},5000); 
		        		$scope.submitted = true;
		        	},function(response) {
			    	    console.log("Error with status code in controller", response.status);
						$translate('ALERT_ADD_EDIT_DELETE_SUCCESS', { crud: 'not edited' }).then(function (translations) {
				    		 $scope.successMsg =  translations;
				    	 });
						$scope.submitSuccess =false;
				    	$scope.submitting=false;
					$scope.submitted = true;
			      });

		        	
			 };
			 
			 $scope.showDetails  = function(alert){
		        	$scope.hoverAlertConfigID=alert.configID;
		        	if(alert.alertTypeClassifier=='alert-message-count')
		        		$translate('MSG_COUNT_ALERT_DETAILS', { messageCountOperator: alert.messageCountOperator, messageCountCount: alert.messageCountCount,timeStamp:alert.timeStamp, messageCountGrace : alert.messageCountGrace, messageCountBacklog : alert.messageCountBacklog}).then(function (translations) {
		        			$scope.alertDetails =  translations;
		   	    	 });
		        	if(alert.alertTypeClassifier=='alert-field-value')
		        		$scope.alertDetails=$translate('FIELD_VALUE_ALERT_DETAILS', { fieldValueField: alert.fieldValueField, fieldValueType: alert.fieldValueType, fieldValueThreshholdType : alert.fieldValueThreshholdType, fieldValueThreshhold : alert.fieldValueThreshhold, timeStamp : alert.timeStamp, fieldValueGrace : alert.fieldValueGrace, fieldValueBacklog : alert.fieldValueBacklog}).then(function (translations) {
		        			$scope.alertDetails =  translations;
			   	    	 });
		        	if(alert.alertTypeClassifier=='alert-field-content')
		        		$scope.alertDetails=$translate('FIELD_CONTENT_ALERT_DETAILS', { fieldContentField: alert.fieldContentField, fieldContentCompareToValue: alert.fieldContentCompareToValue, fieldContentGrace : alert.fieldContentGrace, fieldContentBacklog : alert.fieldContentBacklog}).then(function (translations) {
		        			$scope.alertDetails =  translations;
			   	    	 });;
			 };
			 
			 $scope.hideDetails  = function(alert){
		        	$scope.hoverAlertConfigID="";
			 };
			 
			 $scope.closeForm = function() {
				 $scope.enableConfig = false;
				 $scope.resetConfigFormValues();
			};
			
			$scope.refresh = function(){
				  $state.transitionTo('main.centinelUI.alert', {streamID: stream.streamID});
			};
			
		}]);
	
});
