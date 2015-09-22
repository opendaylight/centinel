define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

	centinelUIApp.register.controller('editRulesFunCtrl', ['$scope', '$rootScope', 'centinelUISvc', function($scope, $rootScope, centinelUISvc) {

    $rootScope['section_logo'] = ''; // Add your topbar logo location here such as 'assets/images/logo_topology.gif'

    

    $scope.data = "edit  Rules functionality";
    
    console.log("inside alert function controller ");

  }]);
	
});