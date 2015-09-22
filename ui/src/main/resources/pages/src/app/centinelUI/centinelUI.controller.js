/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

define(['app/centinelUI/centinelUI.module','app/centinelUI/centinelUI.services'], function(centinelUIApp) {

  centinelUIApp.register.controller('centinelUICtrl', ['$scope', '$rootScope', 'centinelUISvc', function($scope, $rootScope, centinelUISvc) {

    $rootScope['section_logo'] = ''; // Add your topbar logo location here such as 'assets/images/logo_topology.gif'

    $scope.centinelUIInfo = {};

    $scope.data = "centinelUI";

  }]);


});
