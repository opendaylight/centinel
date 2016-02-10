define(['app/centinelUI/centinelUI.module','app/centinelUI/centinelUI.services'], function(centinelUIApp) {

  centinelUIApp.register.controller('centinelUIDashboardCtrl', ['$scope', '$rootScope','$filter','$timeout','$translate','filteredDashboardListService','dashboardServiceFactory','$state','$window', function($scope,$rootScope,$filter,$timeout,$translate,filteredDashboardListService,dashboardServiceFactory,$state,$window) {

	  $scope.enableCreateDashboardForm=false;
	  $scope.successMsg = "";
	  $scope.submitted = false;
	  $scope.submitting = false;
	  $scope.dashboardList = [];
	  $scope.pageSize = 5;
	  $scope.reverse = false;
	  $scope.createDashboardDiv= function() {
			$scope.enableCreateDashboardForm=true;
			$scope.submitted = false;
		};
		
		$scope.dashboardForm = {
			"title":"",
			"description":""
		};
		
		$scope.closeForm = function() {
			 $scope.enableCreateDashboardForm = false;
			 $scope.submitted = false;
			 $scope.resetFormValues();
		};
		
		$scope.resetFormValues = function () {
			$scope.dashboardForm = {
					"title":"",
					"description":""
				};
	    };
		
		$scope.addDashboard = function(dashboardForm) {
			var dashboardFormObject = new Object();
			dashboardFormObject = dashboardForm;
	     	$scope.submitted = false;
	     	var dashboardInputJson = "";
	     	$scope.submitting = true;
	     	dashboardInputJson = "{\"input\":{\"title\" :\""+dashboardForm.title+"\",\"description\" :\""+dashboardForm.description+"\"}}";
	     	dashboardServiceFactory.setDashboard(dashboardFormObject,dashboardInputJson).then(function(res) {
	     		$timeout(function(){
		     		$scope.getAllDashboardCall();
			       	 $translate('DASHBOARD_ADD_EDIT_DELETE_SUCCESS', { crud: 'added' }).then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
			       	if($scope.filteredList.length<5)
				       	 $translate('NO_OF_RESULTS', { firstPageResults: $scope.filteredList.length, total: $scope.filteredList.length }).then(function (translations) {
				       		$scope.NumberOfResults =  translations;
				    	 });
			        else
			        	$translate('NO_OF_RESULTS', { firstPageResults: $scope.pageSize, total: $scope.filteredList.length }).then(function (translations) {
				       		$scope.NumberOfResults =  translations;
				    	 });
			       	$scope.submitted = true;
			       	$scope.submitSuccess =true;
			       	$scope.submitting = false;
			       	$scope.enableCreateDashboardForm = false;
			       	$scope.resetFormValues();
	     		}, 5000);
			},function(response) {
				$translate('DASHBOARD_ADD_EDIT_DELETE_SUCCESS', { crud: 'not added' }).then(function (translations) {
		    		 $scope.successMsg =  translations;
		    	 });
				$scope.submitting = false;
				$scope.submitSuccess =false;
			$scope.submitted = true;
	      });
	     		
		};
		
		$scope.addDashboardToTable = function (dashboardFormObject) {
	        $scope.dashboardList.push(dashboardFormObject);
	        $scope.resetAll();
	    };
		
		/*dashboardServiceFactory.getAllDashboardsFromLocal().then(function(res) {
			//$scope.dashboardList.push(res);
	    	$scope.dashboardList = res;
			$scope.search();
	    });*/
		// code for getting remote dashboard
		 $scope.getAllDashboardCall = function() {
			 dashboardServiceFactory.getAllDashboards().then(function(res) {
		    	if(res != '' && res!=undefined && res !=null )
		    		$scope.dashboardList = res;
		    	else
		    		$scope.dashboardList = [];
				$scope.search();
		    },function(response) {
		    	if(response.status!=404){
		    		$translate('ERROR_RETRIEVE_DASHBOARDS').then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
					$scope.submitSuccess =false;
					$scope.submitted = true;
		    	}else{
		    		$translate('NO_STREAMS_FOUND').then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
		    		$scope.submitSuccess =true;
					$scope.submitted = true;
		    	}
				
	      });
		};
		$scope.getAllDashboardCall();
		$scope.pagination = function (list) {
	    	if (!$scope.$$phase){
	    		$scope.$apply(function () {
			        $scope.dashboardsByPage = filteredDashboardListService.paged(list, $scope.pageSize);
			    	});
	    	}else{
	    		 $scope.dashboardsByPage = filteredDashboardListService.paged(list, $scope.pageSize);
	    	}
	    	
	    };
	    
	    $scope.resetAll = function () {
	        $scope.filteredList = $scope.dashboardList;
	        $scope.searchText = '';
	        $scope.currentPage = 0;
	        $scope.Header = ['', '', ''];
	        $scope.pagination( $scope.filteredList);
	    };
	    
	    $scope.search = function () {
	        $scope.filteredList = filteredDashboardListService.searched($scope.dashboardList, $scope.searchText);
	        if ($scope.searchText == '') {
	            $scope.filteredList = $scope.dashboardList;
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
	    };
	    
	    $scope.setPage = function () {
	        $scope.currentPage = this.n;
	    };
	
	    $scope.firstPage = function () {
	        $scope.currentPage = 0;
	    };
	
	    $scope.lastPage = function () {
	        $scope.currentPage = $scope.dashboardsByPage.length - 1;
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
	    
	    $scope.sort = function (sortBy) {
	        $scope.resetAll();
	
	        $scope.columnToOrder = sortBy;
	
	        $scope.filteredList = $filter('orderBy')($scope.filteredList, $scope.columnToOrder, $scope.reverse);
	
	        if ($scope.reverse) iconName = 'glyphicon glyphicon-chevron-up';
	        else iconName = 'glyphicon glyphicon-chevron-down';
	
	        if (sortBy === 'EmpId') {
	            $scope.Header[0] = iconName;
	        } else if (sortBy === 'name') {
	            $scope.Header[1] = iconName;
	        } else {
	            $scope.Header[2] = iconName;
	        }
	
	        $scope.reverse = !$scope.reverse;
	
	        $scope.pagination($scope.filteredList );
	    };
	
	    $scope.sort('name');
	    
	    $scope.deleteDashboard = function(dashboard) {
	    	var deleteDashboard = $window.confirm('Are you sure you want to delete dashboard?');
	    	if(deleteDashboard){
	    		var deleteDashboardInputJson = "{\"input\":{\"dashboardID\" :\""+dashboard.dashboardID+"\"}}";
		    	dashboardServiceFactory.deleteDashboard(deleteDashboardInputJson).then(function(res) {
		    		$scope.dashboardList = filteredDashboardListService.deleteDashboard($scope.dashboardList, dashboard.title);
		            	$scope.pagination($scope.dashboardList);
		           	 $translate('DASHBOARD_ADD_EDIT_DELETE_SUCCESS', { crud: 'deleted' }).then(function (translations) {
		        		 $scope.successMsg =  translations;
		        	 });
		    	        if($scope.dashboardList.length<5)
		    		       	 $translate('NO_OF_RESULTS', { firstPageResults: $scope.dashboardList.length, total: $scope.dashboardList.length }).then(function (translations) {
		    		       		$scope.NumberOfResults =  translations;
		    		    	 });
		    	        else
		    	        	$translate('NO_OF_RESULTS', { firstPageResults: $scope.pageSize, total: $scope.dashboardList.length }).then(function (translations) {
		    		       		$scope.NumberOfResults =  translations;
		    		    	 });
		    	},function(response) {
					$translate('ERROR_RETRIEVE_DASHBOARDS').then(function (translations) {
			    		 $scope.successMsg =  translations;
			    	 });
					$scope.submitSuccess =false;
				$scope.submitted = true;
		      });
	    	}
	    };
	
	$scope.openWidgetPage = function(dashb) {
		$state.transitionTo('main.centinelUI.widgets', {dashboard: dashb});
	};
	
  }]);


});
