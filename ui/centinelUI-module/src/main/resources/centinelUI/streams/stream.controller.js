/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : this js is controller for stream functionality in CentinelUI
 */

define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.controller('centinelUIStreamCtrl',['$scope','$state','$filter','$timeout','$translate','filteredListService','streamServiceFactory','centinelUISvc',function($scope,$state,$filter,$timeout,$translate,filteredListService,streamServiceFactory,centinelUISvc){
		
		$scope.submitted = false;
		$scope.enableCreateStreamForm=false;
		$scope.streamList =[];
		$scope.pageSize = 5;
		$scope.reverse = false;
		$scope.streamSubscription = false;
		$scope.noStreamSubscription = true;
		$scope.subscribePressed = false;
		$scope.hoverstreamId = "";
		$scope.submitted = false;
		$scope.enableCreateButton = true; 
		$scope.successMsg='';
		$scope.NumberOfResults ='';
		$scope.subscriptionTypeSelected='';
		$scope.searchText ='';
		$scope.makeAllStreamCall = function() {
			centinelUISvc.getService("GET_ALL_STREAM_SERVICE").then(function(res) {
		    	if(res != '' && res!=undefined && res !=null )
		    		$scope.streamList = res.streamRecord.streamList;
		    	else
		    		$scope.streamList = [];
		    		
				$scope.search();
		    },function(response) {
	    	    console.log("Error with status code in controller", response.status);
				$translate('ERROR_RETRIEVE_STREAMS').then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =false;
			$scope.submitted = true;
	      });
		};
	   
	    
	    $scope.reset = function() {
			$scope.submitSuccess =false;
			$scope.submitted = false;
		};
		
		$scope.makeAllStreamCall();
	    
	    $scope.editRuleClicked = function(stream) {
	    	$scope.reset();
			$state.transitionTo('main.centinelUI.editRules', {streamID: stream.streamID});
		};
		
	    $scope.manageAlertClicked = function(stream) {
	    	$scope.reset();
	    	$state.transitionTo('main.centinelUI.alert', {streamID: stream.streamID});
		};
		
		$scope.addStreamConfigDiv= function() {
			$scope.reset();
			$scope.enableCreateButton = true;
			$scope.enableCreateStreamForm=true;
			$scope.submitted = false;
			$scope.resetStreamFormValues();
		};
		
		
		$scope.closeForm = function() {
			 $scope.enableCreateStreamForm=false;
		};
		
		$scope.resetStreamFormValues =function(streamObj){
			$scope.streamForm =  {
			        "configID": "",
			        "streamID": "",
			        "description": "",
			        "streamRules": [
			          {
			            "value": "",
			            "type": "",
			            "field": "",
			            "inverted": false
			          }
			        ],
			        "title": ""
			      };
				
		};
		
		$scope.moreActionValues = [{"actionId":"Start" , "actionName" : "Start Rule"},
		                           {"actionId":"Delete" , "actionName" : "Delete Rule"},
		                           {"actionId":"Edit" , "actionName" : "Edit Rule"},
		                           {"actionId":"Pause" , "actionName" : "Pause Rule"}
		                           ];
		
	     $scope.subscriptionTypes = [
			                         {'ID': 'http' ,'type':'Http Subscription'}
			                      ];
	     
	     $scope.streamSubscriptionForm = {
								    		"URL":"Enter a url like www.tcs.com/alerts",
								    		"userName":"Enter the User Name",
								     		"mode":"stream"
	     								};

		
		$scope.moreActionFunctions = function(action,stream) {
			$scope.reset();
			if(action == "Start")
				$scope.resumeStream(stream);
			if(action == "Delete"){
			    if (confirm("Are you sure, you want to delete this stream!") == true) {
			    	$scope.deleteStream(stream);
			    } else {
			        return;
			    }
			}
			if(action == "Edit")
				$scope.enableEditStream(stream);
			if(action == "Pause")
				$scope.pauseStream(stream);
		};
		
		$scope.enableEditStream = function(stream){
			var streamObj = filteredListService.searchItem($scope.streamList, stream.streamID,"streamID");
			$scope.streamForm = streamObj;
			$scope.enableCreateButton = false;
			$scope.submitted = false;
			$scope.enableCreateStreamForm=true;
		};

		 $scope.showDetails  = function(stream){
			 $scope.hoverstreamId = stream.streamID;
		 };
		 
		 $scope.hideDetails = function(stream) {
			 $scope.hoverstreamId = "";
		};
		
		
		$scope.createStream = function(streamForm,enableCreateButton) {
			$scope.submitting = true;
			if(enableCreateButton){
					var createStreamJson = "{\"input\": {\"description\":\""+streamForm.description+"\",\"title\":\""+streamForm.title+"\",\"streamRules\": []}}";
					centinelUISvc.postService("SET_STREAM_SERVICE",createStreamJson).then(function(createStreamRes) {
						var configIDForThis = createStreamRes.configID;
						$timeout(function(){
							 var newStreamList = [];
							 centinelUISvc.getService("GET_ALL_STREAM_SERVICE").then(function(res) {
							    	if(res.streamRecord.streamList != '' && res.streamRecord.streamList!=undefined && res.streamRecord.streamList !=null )
							    		newStreamList = res.streamRecord.streamList;
							    		
								 if(_.where(newStreamList, {"configID": configIDForThis}).length>0 ){
									 $scope.streamList =[];
									 $scope.streamList = newStreamList;
									 $scope.enableCreateStreamForm=false;
								       	 $translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'created' }).then(function (translations) {
								    		 $scope.successMsg =  translations;
								    	 });
											$scope.submitSuccess =true;
											

								 }else{
										$scope.enableCreateStreamForm=false;
								       	 $translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'not created' }).then(function (translations) {
								    		 $scope.successMsg =  translations;
								    	 });
								       	 	$scope.submitSuccess =false;
											
								 }
								 $scope.submitted = true;
								 $scope.submitting = false;
								 $scope.search();
								
							 });
							}, 5000);
					},function(response) {
			    	    console.log("Error with status code in controller", response.status);
						$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'not created' }).then(function (translations) {
				    		 $scope.successMsg =  translations;
				    	 });
						$scope.submitSuccess =false;
						$scope.submitting = false;
					$scope.submitted = true;
			      });
			}
			else{
				$scope.editStream(streamForm);
			}
				
		};
		
		$scope.editStream = function(streamForm) {
			$scope.submitting = true;
			var editStreamJson = "{\"input\": {\"description\":\""+streamForm.description+"\",\"streamID\":\""+streamForm.streamID+"\",\"configID\":\""+streamForm.configID+"\",\"title\":\""+streamForm.title+"\"}}";
			var streamIDForThis = streamForm.streamID;
			centinelUISvc.postService("UPDATE_STREAM_SERVICE",editStreamJson).then(function(updateStream) {
				$timeout(function(){
					centinelUISvc.getService("GET_ALL_STREAM_SERVICE").then(function(res) {
						 var newStreamList = [];
					    	if(res.streamRecord.streamList != '' && res.streamRecord.streamList!=undefined && res.streamRecord.streamList !=null )
					    		newStreamList = res.streamRecord.streamList;
					
					    if(_.where(newStreamList, {"streamID": streamIDForThis}).length == 1){
							$scope.streamList =[];
							$scope.streamList = newStreamList;
							$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'edited' }).then(function (translations) {
					    		 $scope.successMsg =  translations;
					    	 });
							$scope.enableCreateStreamForm=false;
					       	 	$scope.submitSuccess =true;
								$scope.submitted = true;
						}else{
							$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'not edited' }).then(function (translations) {
					    		 $scope.successMsg =  translations;
					    	 });
					       	 	$scope.submitSuccess =false;
								$scope.submitted = true;
						}
					    	$scope.submitting = false;
					    	 $scope.search();
								
						        if($scope.streamList.length<5)
							       	 $translate('NO_OF_RESULTS', { firstPageResults: $scope.streamList.length, total: $scope.streamList.length }).then(function (translations) {
							       		$scope.NumberOfResults =  translations;
							    	 });
						        else
						        	$translate('NO_OF_RESULTS', { firstPageResults: $scope.pageSize, total: $scope.streamList.length }).then(function (translations) {
							       		$scope.NumberOfResults =  translations;
							    	 });
					});
						
				}, 5000);

			},function(response) {
	    	    console.log("Error with status code in controller", response.status);
				$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'not edited' }).then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =false;
				$scope.submitting = false;
			$scope.submitted = true;
	      });
		};
		
		$scope.deleteStream = function(streamFormObj) {
			$scope.submitting = true;
			var streamForm = filteredListService.searchItem($scope.streamList, streamFormObj.streamID,"streamID");
			var deleteStreamJson = "{\"input\": {\"streamID\":\""+streamForm.streamID+"\"}}";
			var streamIDForThis = streamForm.streamID;
			centinelUISvc.postService("DELETE_STREAM_SERVICE",deleteStreamJson).then(function(deleteStreamRes) {
				$timeout(function(){
					centinelUISvc.getService("GET_ALL_STREAM_SERVICE").then(function(res) {
						 var newStreamList = [];
					    	if(res.streamRecord.streamList != '' && res.streamRecord.streamList!=undefined && res.streamRecord.streamList !=null )
					    		newStreamList = res.streamRecord.streamList;
					
					    if(_.where(newStreamList, {"streamID": streamIDForThis}).length == 0){
							$scope.streamList =[];
							$scope.streamList = newStreamList;
					    	$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'deleted' }).then(function (translations) {
					    		 $scope.successMsg =  translations;
					    	 });
					       	 	$scope.submitSuccess =true;
								$scope.submitted = true;
						}else{
					       	 $translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'not deleted' }).then(function (translations) {
					    		 $scope.successMsg =  translations;
					    	 });
					       	 	$scope.submitSuccess =false;
								$scope.submitted = true;
						}	
					    	$scope.submitting = false;
					    	 $scope.search();
								
						        if($scope.streamList.length<5)
							       	 $translate('NO_OF_RESULTS', { firstPageResults: $scope.streamList.length, total: $scope.streamList.length }).then(function (translations) {
							       		$scope.NumberOfResults =  translations;
							    	 });
						        else
						        	$translate('NO_OF_RESULTS', { firstPageResults: $scope.pageSize, total: $scope.streamList.length }).then(function (translations) {
							       		$scope.NumberOfResults =  translations;
							    	 });
					});
						
				}, 5000);
	     	},function(response) {
	    	    console.log("Error with status code in controller", response.status);
				$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'not deleted' }).then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =false;
				$scope.submitting = false;
			$scope.submitted = true;
	      });
		};
		
		$scope.resumeStream = function(streamFormObj) {
			$scope.submitting = true;
			var streamForm = filteredListService.searchItem($scope.streamList, streamFormObj.streamID,"streamID");
			var resumeStreamJson = "{\"input\": {\"streamID\":\""+streamForm.streamID+"\"}}";
			centinelUISvc.postService("START_STREAM_SERVICE",resumeStreamJson).then(function(res) {
				if(res!=null && res!=undefined && res!=""){
					var matchedStream = _.find($scope.streamList, function(stream) { return stream.streamID === streamForm.streamID })
					if (matchedStream) {
						matchedStream.disabled = res.disabled;
					}
					$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'resumed' }).then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
						$scope.resumed = true;
						$scope.submitSuccess =true;
				}else{
					$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'resumed' }).then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
						$scope.submitSuccess =false;
				}
					$scope.submitted = true;
					$scope.submitting = false;
			},function(response) {
	    	    console.log("Error with status code in controller", response.status);
				$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'not resumed' }).then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =false;
				$scope.submitting = false;
			$scope.submitted = true;
	      });

		};
		
		$scope.pauseStream = function(streamFormObj) {
			$scope.submitting = true;
			var streamForm = filteredListService.searchItem($scope.streamList, streamFormObj.streamID,"streamID");
			var pauseStreamJson = "{\"input\": {\"streamID\":\""+streamForm.streamID+"\"}}";
			centinelUISvc.postService("PAUSE_STREAM_SERVICE",pauseStreamJson).then(function(res) {
				if(res!=null && res!=undefined && res!=""){
					var matchedStream = _.find($scope.streamList, function(stream) { return stream.streamID === streamForm.streamID })
					if (matchedStream) {
						matchedStream.disabled = res.disabled;
					}
					$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'paused' }).then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
					$scope.submitSuccess =true;
				}else{
					$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'not paused' }).then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
					$scope.resumed = false
					$scope.submitSuccess =false;
				}
					$scope.submitting = false;
					$scope.submitted = true;
			},function(response) {
	    	    console.log("Error with status code in controller", response.status);
				$translate('STREAM_ADD_EDIT_DELETE_SUCCESS', { crud: 'not paused' }).then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =false;
				$scope.submitting = false;
			$scope.submitted = true;
	      });

		};
		
		$scope.getStream = function(streamForm) {  // to be called when edit rule is called
			var getStreamJson = "{\"input\": {\"streamID\":\""+streamForm.streamID+"\"}}";
			centinelUISvc.postService("GET_STREAM_SERVICE",getStreamJson).then(function(res) {
				$translate('GET_STREAM_SUCCESS', { crud: 'Got' }).then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =true;
				$scope.submitted = true;
			},function(response) {
	    	    console.log("Error with status code in controller", response.status);
				$translate('GET_STREAM_SUCCESS', { crud: 'Error in getting' }).then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =false;
			$scope.submitted = true;
	      });

		};
		
	    
	    $scope.openStreamSubscriptionDiv = function(stream) {
			$scope.subscribePressed = true;
			$scope.subscribedStreamID = stream.streamID;
		};
		
		 $scope.closeStreamSubscriptionForm = function() {
			 $scope.subscribePressed = false;
		};
		
	    $scope.addStreamSubscription = function(subscriptionType) {
	    	$scope.noStreamSubscription = false;
	    	$scope.streamSubscription = true;
			$scope.subscriptionTypeSelected = subscriptionType;
		};
		
		$scope.makeNewStreamSubscription = function(formObj) {
			$scope.submitting=true;
			var subscribeSvcJson = "{\"input\":{\"userName\":\""+formObj.userName+"\",\"mode\":\""+formObj.mode+"\",\"URL\":\""+formObj.URL+"\"}}";
			centinelUISvc.postService("SUBSCRIBE_SERVICE",subscribeSvcJson).then(function(res) {
				if(res!=null && res!=undefined && res!=""){
		       	 $translate('STREAM_SUBSCRIBE_ADD_SUCCESS', { crud: 'Subscribed' }).then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
		       	$scope.closeStreamSubscriptionForm();
				$scope.submitSuccess =true;
				}else{
			       	 $translate('STREAM_SUBSCRIBE_ADD_SUCCESS', { crud: 'Not Subscribed' }).then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
			       	$scope.closeStreamSubscriptionForm();
					$scope.submitSuccess =false;
				}
				$scope.submitting=false;
				$scope.submitted = true;
			},function(response) {
	    	    console.log("Error with status code in controller", response.status);
				$translate('STREAM_SUBSCRIBE_ADD_SUCCESS', { crud: 'Not Subscribed' }).then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =false;
				$scope.submitting=false;
			$scope.submitted = true;
	      });

		};

	     $scope.resetAll = function () {
		        $scope.filteredList = $scope.streamList;
		        $scope.searchText = '';
		        $scope.currentPage = 0;
		        $scope.Header = ['', '', ''];
		        $scope.pagination( $scope.filteredList);
		    };
		    
		    $scope.search = function () {
		        $scope.filteredList = filteredListService.searched($scope.streamList, $scope.searchText);
		        if ($scope.searchText == '') {
		            $scope.filteredList = $scope.streamList;
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
		    };
		
		    $scope.pagination = function (list) {
		    	if (!$scope.$$phase){
		    		$scope.$apply(function () {
				        $scope.streamsByPage = filteredListService.paged(list, $scope.pageSize);
				    	});
		    	}else{
		    		 $scope.streamsByPage = filteredListService.paged(list, $scope.pageSize);
		    	}
		    	
		    };
		
		    $scope.setPage = function () {
		        $scope.currentPage = this.n;
		    };
		
		    $scope.firstPage = function () {
		        $scope.currentPage = 0;
		    };
		
		    $scope.lastPage = function () {
		        $scope.currentPage = $scope.streamsByPage.length - 1;
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
		    
			  $scope.refresh = function(){
				  $state.transitionTo('main.centinelUI.stream');
			  };
		
		
	}]);
	
	
});
