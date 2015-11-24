/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : This js provides services to streamdata functionality in CentinelUI 
*/

define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {
	centinelUIApp.register.factory('streamdataServiceFactory', ['$http','$q','Restangular','centinelUISvc',function($http,$q,Restangular,centinelUISvc) {
		
		return {

			getAllEvents: function(){
		        return;
			}
		}
	
	}]);
});