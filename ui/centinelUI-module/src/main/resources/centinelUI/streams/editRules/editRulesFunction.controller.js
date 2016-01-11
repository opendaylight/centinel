/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : this js is controller for editrules functionality in CentinelUI
 */

define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.controller('editRulesFunCtrl', ['$scope','$stateParams','$filter','$timeout','$translate','filteredListService','streamRuleSvcFactory','centinelUISvc', function ($scope,$stateParams,$filter,$timeout,$translate,filteredListService,streamRuleSvcFactory,centinelUISvc) {
		$scope.successMsg ='';
		$scope.submitSuccess = false;
		$scope.submitted = false;
		$scope.pageSize = 5;
		$scope.streamForThisRule = $stateParams.streamID;
		$scope.enableCreateStreamRuleForm = false;
		$scope.streamRuleList =[];
		$scope.searchText ="";
		$scope.NumberOfResults ="";
		$scope.matchingType = [{'key':'Match Exactly','value':'match-exactly'},{'key':'Match Regular Expression','value':'match-reg-expression'},{'key':'Greater Than','value':'greator-than'},{'key':'Smaller Than','value':'smaller-than'},{'key':'Field Presence','value':'field-presence'}];
		
			$scope.ruleForm = {
		        	"streamID": "",
		            "field": "",
		            "type": "Match Exactly",
		            "inverted": false,
		            "value": ""
		        
		};
		
		$scope.reset = function() {
			$scope.ruleForm = {
		        	"streamID": "",
		            "field": "",
		            "type": "Match Exactly",
		            "inverted": false,
		            "value": ""
		        
		};
		}
		
		$scope.addStreamRuleDiv = function() {
			$scope.reset();
			$scope.enableCreateStreamRuleForm = true;
			$scope.submitted = false;
		};
		
		$scope.closeForm = function() {
			$scope.enableCreateStreamRuleForm = false;
		};
		
		$scope.addStreamRule = function(ruleForm , type){
	    	$scope.submitting=true;
			var setStreamRuleJson = "{\"input\": {\"streamID\":\""+$scope.streamForThisRule+"\",\"field\":\""+ruleForm.field+"\",\"type\":\""+ type.value+"\",\"inverted\":\""+ ruleForm.inverted+"\",\"value\":\""+ ruleForm.value+"\"}}";
			centinelUISvc.postService('SET_STREAM_RULE_SERVICE',setStreamRuleJson).then(function(createStreamRuleRes) {
	     		var streamRuleIdForThis = createStreamRuleRes.streamRuleID;
	     		$timeout(function(){
		    			var getAllRuleSvcJson = "{\"input\": {\"streamID\":\""+$scope.streamForThisRule+"\" } }";
		    				 streamRuleSvcFactory.getAllRules(getAllRuleSvcJson).then(function(res) {
		    					 var newStreamRuleList =[]
			    			    	if(res != '' && res!=undefined && res !=null )
			    			    		newStreamRuleList = res;
		    					 
			    			    	if(_.where(newStreamRuleList, {"streamRuleID": streamRuleIdForThis}).length > 0 ){
			    			    		$scope.streamRuleList = [];
			    			    		$scope.streamRuleList = newStreamRuleList;
			    			    		$translate('STREAMRULE_ADD_EDIT_DELETE_SUCCESS', { crud: 'been added' }).then(function (translations) {
								    		 $scope.successMsg =  translations;
								    	 });
										$scope.submitSuccess =true;
										$scope.enableCreateStreamRuleForm = false;
			    			    	}
			    			    	else{
										$translate('STREAMRULE_ADD_EDIT_DELETE_SUCCESS', { crud: 'not added' }).then(function (translations) {
								    		 $scope.successMsg =  translations;
								    	 });
										$scope.submitSuccess =false;
										$scope.enableCreateStreamRuleForm = false;
			    			    	}

		    			    		$scope.search();
							    	$scope.submitting=false;
			    			    	$scope.submitted = true;
			    			    	
		    				 });
	     		},5000);
			},function(response) {
	    	    console.log("Error with status code in controller", response.status);
				$translate('STREAMRULE_ADD_EDIT_DELETE_SUCCESS', { crud: 'not been added' }).then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitSuccess =false;
		    	$scope.submitting=false;
			$scope.submitted = true;
	      });	
		};
		
		$scope.getAllRulesForThisStream = function() {
			var getAllRuleSvcJson = "{\"input\": {\"streamID\":\""+$scope.streamForThisRule+"\" } }";
			streamRuleSvcFactory.getAllRules(getAllRuleSvcJson).then(function(res) {
			    	if(res != '' && res!=undefined && res !=null )
			    		$scope.streamRuleList = res;
			    	else
			    		$scope.streamRuleList = [];
			    		
					$scope.search();
			    },function(response) {
		    	    console.log("Error with status code in controller", response.status);
					$translate('ERROR_RETRIEVE_STREAM_RULES').then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
					$scope.submitSuccess =false;
				$scope.submitted = true;
		      });
			
		};
		
	    $scope.search = function () {
	        $scope.filteredList = filteredListService.searched($scope.streamRuleList, $scope.searchText);
	        if ($scope.searchText == '') {
	            $scope.filteredList = $scope.streamRuleList;
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
			        $scope.rulesByPage = filteredListService.paged(list, $scope.pageSize);
			    	});
	    	}else{
	    		 $scope.rulesByPage = filteredListService.paged(list, $scope.pageSize);
	    	}
	    	
	    };
	
	    $scope.setPage = function () {
	        $scope.currentPage = this.n;
	    };
	
	    $scope.firstPage = function () {
	        $scope.currentPage = 0;
	    };
	
	    $scope.lastPage = function () {
	        $scope.currentPage = $scope.rulesByPage.length - 1;
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
			$state.transitionTo('main.centinelUI.editRules', {streamID: stream.streamID});
		};

	    $scope.getAllRulesForThisStream();
	    
  }]);
	
});
