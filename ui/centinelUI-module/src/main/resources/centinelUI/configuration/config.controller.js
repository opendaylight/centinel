define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {

  centinelUIApp.register.controller('centinelUIConfigCtrl', ['$scope','$http','$window','configServiceFactory','$translate','$timeout', function($scope,$http,$window,configServiceFactory,$translate,$timeout) {

	  $scope.submitting = false;
	  $scope.submitted = false;
	  $scope.submitSuccess = true;
	  $scope.configForm = {
			  "graylogIP": [127,0,0,1],
			  "flumeIP":[127,0,0,1],
		      "drillIP":[127,0,0,1],
		      "graylogPort":12900,
		      "flumePort":41414,
		      "drillPort":8047,
		      "syslogPort":1514		      
	  };
	 
	  $scope.validated = true;
	  $scope.validate = function (configForm){
		  _.each(configForm,function(ip){
			 _.each(ip,function(block){
				 if(!(block >= 0 && block <256)) {
					 $scope.validated = false;
				  }
			 }) 
		  });
	  };
	  
	  $scope.save = function(configForm){
		  $scope.validate(configForm);
		  if (!$scope.validated){
			  $window.alert("Invalid IP address !! Range: [0-255]");
		  }else{
			  $scope.submitting = true;
			  var graylogIP= configForm.graylogIP[0]+"."+configForm.graylogIP[1]+"."+configForm.graylogIP[2]+"."+configForm.graylogIP[3];
			  var flumeIP= configForm.flumeIP[0]+"."+configForm.flumeIP[1]+"."+configForm.flumeIP[2]+"."+configForm.flumeIP[3];
			  var drillIP= configForm.drillIP[0]+"."+configForm.drillIP[1]+"."+configForm.drillIP[2]+"."+configForm.drillIP[3];
			  var ipInputJson = "{\"input\":{\"graylogIp\":\""+graylogIP+"\",\"flumeIp\":\""+flumeIP+"\",\"drillIp\":\""+drillIP+"\",\"graylogPort\":\""+configForm.graylogPort+"\",\"flumePort\":\""+configForm.flumePort+"\",\"drillPort\":\""+configForm.drillPort+"\",\"syslogPort\":\""+configForm.syslogPort+"\"}}";
			  configServiceFactory.setConfigIpAddr(ipInputJson).then(function(res) {
				  $timeout(function(){
					  $translate('CONFIG_ADD_SUCCESS', { crud: 'added' }).then(function (translations) {
				    		 $scope.serviceResponseMsg =  translations;
				    	 });
				       	$scope.submitting = false;
				       	$scope.submitSuccess =true;
						$scope.submitted = true;
						console.info("submitted !!");
				  },2000);
				},function(response) {
					$translate('CONFIG_ADD_SUCCESS', { crud: 'not added' }).then(function (translations) {
			    		 $scope.serviceResponseMsg =  translations;
			    	 });
					$scope.submitting = false;
					$scope.submitSuccess =false;
					$scope.submitted = true;
					console.info("not submitted !!");
				});
		  }
	  };
	  
	  $scope.validatePass = function(id,cl){
		  var ele = $(cl)[id];
		  if(ele.value.length>2){
			  if(!(ele.value >= 0 && ele.value <256)) {
					  $window.alert("Invalid IP address !! Range: [0-255]");
					  ele.focus();
					  ele.value = '';
				  }else if(id<3){
					  $(cl)[id+1].focus();
					  $(cl)[id+1].value = '';  
			  }
			  
		  }
	  };
	  
	  $scope.validatePort = function(id){
		  var ele = $(id)[0];
		  if(!(ele.value >= 0 && ele.value <65536)) {
			  $window.alert("Invalid Port number !! Range: [0-65535]");
			  ele.focus();
			  ele.value = '';
		  }
	  }
	  /*$scope.ping = function (ip, callback) {
		  console.info("pinging: "+ ip);
		    if (!this.inUse) {
		        this.status = 'unchecked';
		        this.inUse = true;
		        this.callback = callback;
		        this.ip = ip;
		        var _that = this;
		        this.img = new Image();
		        this.img.onload = function () {
		            _that.inUse = false;
		            console.info("connection success");
		            _that.callback('responded');
		            
		        };
		        this.img.onerror = function (e) {
		            if (_that.inUse) {
		                _that.inUse = false;
		                console.info("error connecting !!");
		                _that.callback('responded', e);
		            }

		        };
		        this.start = new Date().getTime();
		        this.img.src = "http://"+ ip;
		        this.timer = setTimeout(function () {
		            if (_that.inUse) {
		                _that.inUse = false;
		                console.info("connection timeout !!");
		                _that.callback('timeout');
		            }
		        }, 1500);
		    }
		}
	  */
	 
	 /* $scope.testConnection = function (configForm){
		  console.info("testing connection: .....");
		  $scope.ping(configForm.graylogIP, function (status, e) {
			  console.info("connection: "+status);
	        });
		  $http.get("http://"+configForm.graylogIP).success( function(response) {
		      $scope.students = response; 
		   });
		  $http({
			  method: 'GET',
			  url: 'http://'+configForm.graylogIP,
			  headers:{'Access-Control-Allow-Origin' : '*',
			  		'Access-Control-Allow-Methods' : 'POST, GET, OPTIONS, PUT'}
			}).then(function successCallback(response) {
				console.info("connection success");
			  }, function errorCallback(response) {
				  console.info("error connecting !!");
			  });
	  }*/
	  
	  
  }]);
});
