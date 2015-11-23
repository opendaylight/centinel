/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : This js provides services to editrules functionality in CentinelUI 
*/

define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.factory('streamRuleSvcFactory', ['$http','$q','Restangular','centinelUISvc',function($http,$q,Restangular,centinelUISvc) {
		return {	
			
			getAllRules: function(postJson) {
					 var defer = $q.defer();
					 var ruleArr =[];
					 centinelUISvc.getSeviceApi().then(function(data) {
							
							return Restangular.all(data.GET_ALL_STREAM_RULE_SERVICE).post(('streamsRules',postJson)).then(function(res) {
				    			 _.each(res.data.output.streamRules, function(rule) {
				    					  ruleArr.push(rule);
								});
					    		  defer.resolve(ruleArr);
							},function(response) {
					    	    console.log("Error with status code", response.status);
					    	    defer.reject(response);
					      });
							 defer.resolve(ruleArr);
						});
						return defer.promise;
				},
					
					createStreamRule: function(setStreamRuleJson) {
						requestStatus = '';
						var dummy = '';
						 var defer = $q.defer();
						 var stream ='';
						 centinelUISvc.getSeviceApi().then(function(data) {

								return Restangular.all(data.SET_STREAM_RULE_SERVICE).post(('streamRule',setStreamRuleJson)).then(function(res) {
									stream = res.data.output;
						    		  defer.resolve(stream);
						        	
						      },function(response) {
						    	    console.log("Error with status code", response.status);
						    	    defer.reject(response);
						      });

						});
						return defer.promise;
					}
		}
		
	}]);
});