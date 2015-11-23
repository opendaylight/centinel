/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * @author Himanshu Yadav 
 * description : This js provides control functions common to all modules in centinelUI
*/

define(['app/centinelUI/centinelUI.module','app/centinelUI/centinelUI.services'], function(centinelUIApp) {

  centinelUIApp.register.controller('centinelUICtrl', ['$scope', '$rootScope', 'centinelUISvc', function($scope, $rootScope, centinelUISvc) {

    $rootScope['section_logo'] = '/src/app/centinelUI/assets/images/centinel.png'; 
    $scope.centinelUIInfo = {};
    $scope.data = "centinelUI";

  }]);


});
