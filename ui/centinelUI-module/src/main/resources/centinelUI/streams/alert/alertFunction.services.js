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
				 centinelUISvc.postService('GET_ALL_ALERTS_SERVICE',postJson).then(function(res) {
						_.each(res, function(listOfParticularType){
			    				  _.each(listOfParticularType, function(alert) {
			    					  			alertArr.push(alert);
								})		    				  
			    			 });
						defer.resolve(alertArr);	
				      },function(response) {
				    	    console.log("Error with status code", response.status);
				    	    defer.reject(response);
				      });				
					return defer.promise;
			},
			
			setAlertConfiguration : function(alertConfigFormObject,setAlertSvcJson){			
				 var defer = $q.defer();
				 var serviceName = '';
					 if(alertConfigFormObject.alertTypeClassifier== 'alert-message-count')
						 serviceName = 'SET_MESSAGE_COUNT_ALERT_SERVICE';
						 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-value')
							 serviceName = 'SET_FIELD_VALUE_ALERT_SERVICE';
							 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-content')
								 serviceName = 'SET_FIELD_CONTENT_ALERT_SERVICE';
					centinelUISvc.postService(serviceName,setAlertSvcJson).then(function(res) {
				    		  defer.resolve(res);
				      },function(response) {
				    	    console.log("Error with status code", response.status);
				    	    defer.reject(response);
				      });
					return defer.promise;
			},
			
			editAlertConfiguration : function(alertConfigFormObject,editAlertSvcJson){
				 var defer = $q.defer();
				 var serviceName='';
					 if(alertConfigFormObject.alertTypeClassifier== 'alert-message-count')
						 serviceName = 'UPDATE_MESSAGE_COUNT_ALERT_SERVICE';
						 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-value')
							 serviceName = 'UPDATE_FIELD_VALUE_ALERT_SERVICE';
							 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-content')
								 serviceName = 'UPDATE_FIELD_CONTENT_ALERT_SERVICE';
							 centinelUISvc.postService(serviceName,editAlertSvcJson).then(function(res) {
				    		  defer.resolve(res);
				      },function(response) {
				    	    console.log("Error with status code", response.status);
				    	    defer.reject(response);
				      });
					return defer.promise;
			},
			
			deleteAlertConfiguration : function(alertConfigFormObject,deleteAlertSvcJson){
				 var defer = $q.defer();
				 var serviceName = '';
					 if(alertConfigFormObject.alertTypeClassifier== 'alert-message-count')
						 serviceName = 'DELETE_MESSAGE_COUNT_ALERT_SERVICE';
						 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-value')
							 serviceName = 'DELETE_FIELD_VALUE_ALERT_SERVICE';
							 if(alertConfigFormObject.alertTypeClassifier== 'alert-field-content')
								 serviceName = 'DELETE_FIELD_CONTENT_ALERT_SERVICE';
					centinelUISvc.postService(serviceName,deleteAlertSvcJson).then(function(res) {
				    		  defer.resolve(res);
				      },function(response) {
				    	    console.log("Error with status code", response.status);
				    	    defer.reject(response);
				      });
					return defer.promise;
			}
		
		
		}

	}]);
	
});










