/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : This js provides services to alert functionality in CentinelUI 
*/

define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {


	centinelUIApp.register.factory('addAlertServiceFactory', ['$http','$q','Restangular','centinelUISvc',function($http,$q,Restangular,centinelUISvc) {
		
		return {
			
			getAllAlerts: function(streamId) {
				var postJson = "{\"input\": {\"streamID\":\""+streamId+"\" } }";
				 var defer = $q.defer();
				 var alertArr =[];
				 centinelUISvc.getSeviceApi().then(function(data) {
						
						return Restangular.all(data.GET_ALL_ALERTS_SERVICE).post(('streams',postJson)).then(function(res) {
						_.each(res.data.output, function(listOfParticularType){
			    				  _.each(listOfParticularType, function(alert) {
			    					  			alertArr.push(alert);
								})		    				  
			    			 });
						defer.resolve(alertArr);
					
				        	
				      },function(response) {
				    	    console.log("Error with status code", response.status);
				    	    defer.reject(response);
				      });
						 defer.resolve(alertArr);
					});
					return defer.promise;
			},
			
			setAlertConfiguration : function(alertConfigFormObject,setAlertSvcJson){			
				 var defer = $q.defer();
				 var serviceName = '';
				 var output ='';
				 centinelUISvc.getSeviceApi().then(function(data) {
					 if(alertConfigFormObject.alertTypeClassifier== 'alert-message-count')
						 serviceName = data.SET_MESSAGE_COUNT_ALERT_SERVICE;
						 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-value')
							 serviceName = data.SET_FIELD_VALUE_ALERT_SERVICE;
							 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-content')
								 serviceName = data.SET_FIELD_CONTENT_ALERT_SERVICE;
						return Restangular.all(serviceName).post(('alert',setAlertSvcJson)).then(function(res) {
				    		  output=res.data.output;
				    		  defer.resolve(output);
					
				      },function(response) {
				    	    console.log("Error with status code", response.status);
				    	    defer.reject(response);
				      });
						 defer.resolve(output);
					});
					return defer.promise;
			},
			
			editAlertConfiguration : function(alertConfigFormObject,editAlertSvcJson){
				 var defer = $q.defer();
				 var output ='';
				 var serviceName = '';
				 centinelUISvc.getSeviceApi().then(function(data) {
					 if(alertConfigFormObject.alertTypeClassifier== 'alert-message-count')
						 serviceName = data.UPDATE_MESSAGE_COUNT_ALERT_SERVICE;
						 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-value')
							 serviceName = data.UPDATE_FIELD_VALUE_ALERT_SERVICE;
							 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-content')
								 serviceName = data.UPDATE_FIELD_CONTENT_ALERT_SERVICE;
						return Restangular.all(serviceName).post(('alert',editAlertSvcJson)).then(function(res) {
				    		  output=res.data.output;
				    		  defer.resolve(output);
				      },function(response) {
				    	    console.log("Error with status code", response.status);
				    	    defer.reject(response);
				      });
						 defer.resolve(output);
					});
					return defer.promise;
			},
			
			deleteAlertConfiguration : function(alertConfigFormObject,deleteAlertSvcJson){
				 var defer = $q.defer();
				 var output ='';				 
				 var serviceName = '';
				 centinelUISvc.getSeviceApi().then(function(data) {
					 if(alertConfigFormObject.alertTypeClassifier== 'alert-message-count')
						 serviceName = data.DELETE_MESSAGE_COUNT_ALERT_SERVICE;
						 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-value')
							 serviceName = data.DELETE_FIELD_VALUE_ALERT_SERVICE;
							 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-content')
								 serviceName = data.DELETE_FIELD_CONTENT_ALERT_SERVICE;
						return Restangular.all(serviceName).post(('alert',deleteAlertSvcJson)).then(function(res) {
				    		  output = res.data.output;
				    		  defer.resolve(output);
					
				      },function(response) {
				    	    console.log("Error with status code", response.status);
				    	    defer.reject(response);
				      });
						 defer.resolve(output);
					});
					return defer.promise;
			},
			
			makeAlertSubscription: function(subscribeAlertJson) {
				var dummy = '';
				 var defer = $q.defer();
				 var stream ='';
				 centinelUISvc.getSeviceApi().then(function(data) {
						
						return Restangular.all(data.SUBSCRIBE_SERVICE).post(('stream',subscribeAlertJson)).then(function(res) {
							stream = res.data.output;
				    		  defer.resolve(stream);
					
				        	
				      },function(response) {
				    	    console.log("Error with status code", response.status);
				    	    defer.reject(response);
				      });
						 defer.resolve(stream);
				});
				return defer.promise;
			},
			
		
		}

	}]);
	
});










