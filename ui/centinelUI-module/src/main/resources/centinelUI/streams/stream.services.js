/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : This js provides services to stream functionality in CentinelUI 
*/

define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.factory('streamServiceFactory', ['$http','$q','Restangular','centinelUISvc',function($http,$q,Restangular,centinelUISvc) {
		
			return {

				getAllStreams: function() {
					 var defer = $q.defer();
					 var streamArr =[];
					 centinelUISvc.getSeviceApi().then(function(data) {
							
							return Restangular.all(data.GET_ALL_STREAM_SERVICE).getList().then(function(res) {
								streamArr = res.data.streamRecord.streamList;
					    		  defer.resolve(streamArr);

					      },function(response) {
					    	    console.log("Error with status code", response.status);
					    	    defer.reject(response);
					      });
							
							 defer.resolve(streamArr);
					});
						return defer.promise;
				},
				
				getStream: function(getStreamJson) {
					var dummy = { "input": { streamID:"5631ed20e4b0b56473896679" } };
					 var defer = $q.defer();
					 var stream ='';
					 centinelUISvc.getSeviceApi().then(function(data) {
							
							return Restangular.all(data.GET_STREAM_SERVICE).post(('stream',getStreamJson)).then(function(res) {
								stream = res.data.output;
					    		  defer.resolve(stream);
						
					      },function(response) {
					    	    console.log("Error with status code", response.status);
					    	    defer.reject(response);
					      });
							 defer.resolve(streamArr);
					});
					return defer.promise;
				},
				
				createStream: function(setStreamJson) {
					var dummy = '';
					 var defer = $q.defer();
					 var stream ='';
					 centinelUISvc.getSeviceApi().then(function(data) {
							
							return Restangular.all(data.SET_STREAM_SERVICE).post(('stream',setStreamJson)).then(function(res) {
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
				
				updateStream: function(updateStreamJson) {
					var dummy = '';
					 var defer = $q.defer();
					 var stream ='';
					 centinelUISvc.getSeviceApi().then(function(data) {
							
							return Restangular.all(data.UPDATE_STREAM_SERVICE).post(('stream',updateStreamJson)).then(function(res) {
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
				
				resumeStream: function(resumeStreamJson) {
					var dummy = '';
					 var defer = $q.defer();
					 var stream ='';
					 centinelUISvc.getSeviceApi().then(function(data) {
							
							return Restangular.all(data.START_STREAM_SERVICE).post(('stream',resumeStreamJson)).then(function(res) {
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
				
				pauseStream: function(pauseStreamJson) {
					var dummy = '';
					 var defer = $q.defer();
					 var stream ='';
					 centinelUISvc.getSeviceApi().then(function(data) {
							
							return Restangular.all(data.PAUSE_STREAM_SERVICE).post(('stream',pauseStreamJson)).then(function(res) {
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
				
				deleteStream: function(deleteStreamJson) {
					var dummy = '';
					 var defer = $q.defer();
					 var stream ='';
					 centinelUISvc.getSeviceApi().then(function(data) {
							
							return Restangular.all(data.DELETE_STREAM_SERVICE).post(('stream',deleteStreamJson)).then(function(res) {
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
				
				makeStreamSubscription: function(subscribeStreamJson) {
					var dummy = '';
					 var defer = $q.defer();
					 var stream ='';
					 centinelUISvc.getSeviceApi().then(function(data) {
							
							return Restangular.all(data.SUBSCRIBE_SERVICE).post(('stream',subscribeStreamJson)).then(function(res) {
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
