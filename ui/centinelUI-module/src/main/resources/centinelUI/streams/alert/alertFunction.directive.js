/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * @description : this js is directive for alert functionality to provide the dynamic alert configuration forms in CentinelUI
 */
define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {	
	
	centinelUIApp.register.directive('dynamic',function () {
		 return {
		       restrict: 'A',
		     
		       link: function(scope, element, attrs, ctrl) {
		    	scope.getContentUrl = function() {
		        	   if(scope.alertConfig=='alert-message-count')
		        		   return 'messageCount.html';
		        	   if(scope.alertConfig=='alert-field-value')
			                return 'fieldValue.html';
		        	   if(scope.alertConfig=='alert-field-content')
			                return 'fieldContent.html';
		           }
		       },
		       template: '<div ng-include="getContentUrl()"></div>'
		   };
		});
	
	
});