define(['app/centinelUI/centinelUI.module'], function(centinelUIApp) {	
	
	centinelUIApp.register.directive('barChart',['$stateParams', function ($stateParams) {
		return {
	         restrict: 'E',
	         replace: true,
	         scope:{val1:'=', index:'@',dashboardID:'='},
	         template: '<div style="float:left; margin-right:5px; clear:both;"></div>',
	         //template: '<svg id="chart" width="280" height="180" style="margin-right: 50px;"></svg>',
	         controller: function($scope,$stateParams,$window){
		        	$scope.dashboardID = $stateParams.dashboard.dashboardID;
		        	$scope.confirmDelete = function(){
		        		var deleteWidget = $window.confirm('Are you sure you want to delete widget?');
		        		return deleteWidget;
		        	};
		         },
	         link: function (scope, element, attrs) {
	        	 var barData = scope.val1[0],
	        	 	name = scope.val1[1],
	        	 	description = scope.val1[2],
	        	 	widgetID = scope.val1[4],
	        	 	grepId = '#widgetBar'+scope.index,	
	        	 	//chart = d3.select('#chart'),
	        	 	WIDTH = 580,
	        	    HEIGHT = 320,
	        	    MARGINS = {
	        	      top: 100,
	        	      right: 30,
	        	      bottom: 30,
	        	      left: 40
	        	    };
	        	 var d3DateFormat = d3.time.format("%H:%M");
	        	 //var dateFormat = d3.time.format("%a %b %d %H:%M:%S IST %Y");
	        	 var dateFormat = d3.time.format("%Y-%m-%d %H:%M:%S");
	        	 var dates = barData.map(function(d){
	        		 return dateFormat.parse(d.timestamp);
	        	 }),
	        	 	mindate = d3.min(dates),
	        	 	maxdate = d3.max(dates);
       	     var xRange = d3.time.scale().range([MARGINS.left, WIDTH - MARGINS.right-MARGINS.left]).domain([mindate, maxdate]),
	        	 	yRange = d3.scale.linear().range([HEIGHT - MARGINS.bottom, MARGINS.top]).domain([0,d3.max(barData, function (d) {
       	            return d.value;
       	           })
       	          ]),
	        	    xAxis = d3.svg.axis()
	        	      .scale(xRange)
	        	      .orient("bottom")
	        	      .tickSize(5)
	        	      .tickFormat(d3DateFormat)
	        	      .tickSubdivide(true),
	        	      
	        	    yAxis = d3.svg.axis()
	        	      .scale(yRange)
	        	      .orient("left")
	        	      .tickSize(10)
	        	      .tickSubdivide(true);
       	     /*var inputDateBox = angular.element('<input type="datetime-local" id="exampleInput" name="input" ng-model="example.value" placeholder="yyyy-MM-ddTHH:mm:ss" min="2001-01-01T00:00:00" max="2016-12-31T00:00:00" required />');
	        	 element.append(inputDateBox);*/
       	  function deleteWidget(){
     		 var widgetInputJson = "{\"input\":{\"dashboardID\" :\""+scope.dashboardID+"\",\"widgetID\" :\""+widgetID+"\"}}";
     		 widgetServiceFactory.deleteWidget(widgetInputJson).then(function(res) {
     			 d3.select(grepId).remove();
     			 stopInterval();
     			 console.info("Widget deletion successful");
  	    	},function(response) {
  	    		console.info('Error in deleting widget'+response);
  	      }); 
     	 };
       	     var svg = angular.element('<svg id="widgetBar'+scope.index+'" width="580" height="320"></svg>');
	        	 element.append(svg);
	        	 var tooltip = d3.select('body').append('div')
			     		.style('position','absolute') //To allow d3 to follow the position absolute to the relationship to the page
			     		.style('padding','0 10px') //To do padding on the toop tip. 0 on the top and bottom and 10px on each side
			     		.style('background','black')
			     		.style('color','white')
			     		.style('opacity',0);
	        	 var chart = d3.select(grepId);
	        	 chart.style('background','#5f5f5f');
	        	 chart.style('fill','white');
	        	 chart.on('mouseover', function() {
             		d3.select(this)
             		.style('background','rgba(95, 95, 95, 0.7)');
             		})
		        	  .on('mouseout',function(d){
		        		  d3.select(this)
		        		  .style('background','#5f5f5f');
		        	  	});
	        	 chart.append('svg:text')
	        	 	.text(name+" histogram")
	        	 	.style('font-size','16px')
	        	 	.attr('x',20)
	        	 	.attr('y',20);
	          chart.append('svg:foreignObject')
       	 		.attr('x',40)
       	 		.attr('y',30)
       	 	  .append('xhtml:label')
       	 	  .text("From")
       	 	  .append('xhtml:input')
       	 		.attr('type','datetime-local')
       	 		.attr('placeholder','yyyy-MM-ddTHH:mm:ss')
       	 		.attr('min','2001-01-01T00:00:00')
       	 		.attr('max','2016-12-31T00:00:00')
       	 		.attr('class','form-control ng-pristine ng-untouched ng-valid')
       	 		.style('width','220px')
       	 		.style('height','30px');
	        	 chart.append('svg:foreignObject')
	        	 		.attr('x',270)
	        	 		.attr('y',30)
  	        	 	  .append('xhtml:label')
  	        	 	  .text("To")
	        	 	  .append('xhtml:input')
	        	 		.attr('type','datetime-local')
	        	 		.attr('placeholder','yyyy-MM-ddTHH:mm:ss')
	        	 		.attr('min','2001-01-01T00:00:00')
	        	 		.attr('max','2016-12-31T00:00:00')
	        	 		.attr('class','form-control ng-pristine ng-untouched ng-valid')
	        	 		.style('width','220px')
	        	 		.style('height','30px');
	        	 chart.append('svg:foreignObject')
       	 		.attr('x',500)
       	 		.attr('y',30)
       	 	  .append('xhtml:label')
       	 	  .text("Now")
       	 	  .append('xhtml:input')
       	 		.attr('type','checkbox');
	        	 chart.append('svg:g')
	        	    .attr('class', 'x axis')
	        	    .attr('transform', 'translate(0,' + (HEIGHT - MARGINS.bottom) + ')')
	        	    .call(xAxis);
	        	    /*.append("text")
			             .attr("x", 10)
			             .attr("dx", ".41em")
			             .style("text-anchor", "start");*/
	        	 ///Delete icon added
	        	 chart.append("svg:image")
			    			.attr('x',558)
				    	.attr('y',10)
				    	.attr('width',14)
				    	.attr('height',18)
				    	.attr('xlink:href','/src/app/centinelUI/assets/images/delete-icon.png')
				    	.style('cursor','hand')
				    	.on('click', function() {
				    		console.info("Delete icon clicked");
				    		if(scope.confirmDelete()){
				    			deleteWidget();					    			
				    		}
		         		})
		         	.on('mouseover', function() {
		         		d3.select(this)
		         		 .attr('width',15)
		         		 .attr('height',19);
		     			tooltip.transition()
		     			.style('opacity',1);
		     			tooltip.html("Delete Widget")
		     			.style('left',(d3.event.pageX + 10)+ 'px') //position of the tooltip
		     			.style('top',(d3.event.pageY + 15) + 'px'); 
		         		})
			        	  .on('mouseout',function(d){
			        		  d3.select(this)
		             		 .attr('width',14)
		             		 .attr('height',18);
			        		  tooltip.transition()
			        			.style('opacity',0);
			        	  	});
	        	 //Display only when No data found or Error in retrieving widget values
	        	 chart.append("svg:text")
	        	 		.text(scope.val1[3])
		        	 	.attr('x',120)
		        	 	.attr('text-anchor','middle')
		        	 	.style('opacity',0.7)
		        	 	.style('font-size','32px');
	        	 chart.append('svg:g')
	        	    .attr('class', 'y axis')
	        	    .attr('transform', 'translate(' + (MARGINS.left) + ',0)')
	        	    .call(yAxis)
		        	 .append("text")
			             //.attr("transform", "rotate(-90)")
			             .attr("y", 90)
			             .attr("x", 30)
			             .attr("dy", ".41em")
			             .style("text-anchor", "middle")
			             .text("Events");
	        	 chart.selectAll(".x.axis text")  // select all the text elements for the x axis
	        	 .attr("x", 10);
	        	 chart.selectAll('rect')
	        	  .data(barData)
	        	  .enter()
	        	  .append('rect').attr("class", "chart")
	        	  .attr('x', function(d) { // sets the x position of the bar
	        	    //return xRange(d.timestamp);
	        		  return xRange(dateFormat.parse(d.timestamp));
	        	  })
	        	  .attr('y', function(d) { // sets the y position of the bar
	        	    //return yRange(d.value);
	        		  return yRange(d.value);
	        	  })
	        	  .attr('width', ((WIDTH - MARGINS.right-MARGINS.left)/barData.length) - 4) // sets the width of bar
	        	  .attr('height', function(d){
	        		  return HEIGHT - MARGINS.bottom - yRange(d.value);
	        		  //return yRange(d.value);
	        	  })
	        	  .on('mouseover', function(d) {
	        			tooltip.transition()
	        			.style('opacity',1);
	        	 
	        			tooltip.html(d.value+" events<p style=\"font-size:8pt;color:gray\">"+dateFormat.parse(d.timestamp)+"</p>")
	        			.style('left',(d3.event.pageX - 20)+ 'px') //position of the tooltip
	        			.style('top',(d3.event.pageY + 15) + 'px'); 

	        		  /*d3.select(this)
	        		  .attr('fill', 'orange');*/
	        		  d3.select(this)
			        		  .style('opacity',0.7);
	        	  })
	        	  .on('mouseout',function(d){
	        		  tooltip.transition()
	        			.style('opacity',0);
	        		  /*d3.select(this)
	        		  .attr('fill','white');*/
	        		  d3.select(this)
			        		  .style('opacity',1);
	        	  })
	        	  .transition().delay(0)            
	              .duration(1000)
	        	  .attr('fill', 'white');
	         } 
	      }
	}]);
	centinelUIApp.register.directive('msgCount', ['$interval','widgetServiceFactory','$stateParams', function($interval,widgetServiceFactory,$stateParams) {
		      return {
		         restrict: 'E',
		         replace: true,
		         scope:{val:'=', index:'@',widgetvalue:'=',dashboardID:'='},
		         template: '<div style="float:left; margin-right:5px;"></div>',
		         //template: '<svg id="msgcount" width="280" height="180"></svg>',
		         controller: function($scope,$stateParams,$window){
		        	$scope.dashboardID = $stateParams.dashboard.dashboardID;
		        	$scope.confirmDelete = function(){
		        		var deleteWidget = $window.confirm('Are you sure you want to delete widget?');
		        		return deleteWidget;
		        	};
		         },
		         link: function (scope, element, attrs,controller) {
		        	 //var count = scope.val[0],
		        	 	var name = '',
		        	 	description = '',
		        	 	widgetID = scope.val[3],
		        	 	resetTime = scope.val[4],
		        	 	widgetType = scope.val[5],
		        	 	grepId = '#widget'+scope.index,
		        	 	tspanId = '#tspanValue'+scope.index;
		        	 	
		        	 var timeoutId;
		        	 //var c = 0;
		        	 function updateCount() {
		        		 msgCountWidgetInputJson = "{\"input\":{\"widgetID\":\""+widgetID+"\"}}";
		        		 console.info('in Update count: '+msgCountWidgetInputJson);
		        		 widgetServiceFactory.getWidgetMessageCount(msgCountWidgetInputJson).then(function(res) {
		        			 //scope.widgetvalue[scope.val[0]] = res.value+c;
		        			 scope.widgetvalue[scope.val[0]] = res.value;
		        			 console.info('in getWidgetMessageCount '+scope.widgetvalue[scope.val[0]]);
		        		 },
		        		 function(response) {
									console.info('Error in getting widget value while updating widget'+response);
						});
		        	 };
		        	 function deleteWidget(){
		        		 var widgetInputJson = "{\"input\":{\"dashboardID\" :\""+scope.dashboardID+"\",\"widgetID\" :\""+widgetID+"\"}}";
		        		 widgetServiceFactory.deleteWidget(widgetInputJson).then(function(res) {
		        			 d3.select(grepId).remove();
		        			 stopInterval();
		        			 console.info("Widget deletion successful");
		     	    	},function(response) {
		     	    		console.info('Error in deleting widget'+response);
		     	      }); 
		        	 };
		        	 function stopInterval(){
		        		 if (angular.isDefined(timeoutId)) {
		        	            $interval.cancel(timeoutId);
		        	            timeoutId = undefined;
		        	      	} 
		        	 };
		        	 var tooltip = d3.select('body').append('div')
			     		.style('position','absolute') 
			     		.style('padding','0 10px') 
			     		.style('background','black')
			     		.style('color','white')
			     		.style('opacity',0);
		        	 var svg = angular.element('<svg id="widget'+scope.index+'" width="240" height="120"></svg>');
		        	 element.append(svg);
		        	 var msgcount = d3.select(grepId);
		        	 msgcount.style('background','#5f5f5f')
		        	 		.style('border','1px solid #2F2C2C')
		        	 		.style('fill','white');
		        	 msgcount.on('mouseover', function() {
	                		d3.select(this)
	                		.style('background','rgba(95, 95, 95, 0.7)');
	                		})
			        	  .on('mouseout',function(d){
			        		  d3.select(this)
			        		  .style('background','#5f5f5f');
			        	  	});
		        	 if(widgetType=="stream"){
		        		 name = scope.val[1];
		        		 description = scope.val[2];
		        		 msgcount.append("svg:text")																																																																																																																																
			        	 	.text(name)
			        	 	.attr('x',10)
			        	 	.attr('y',20)
			        	 	.style('font-size','16px')
			        	 	.append("tspan")
				        	 	.text(description)
				        	 	.attr('x',10)
				        	 	.attr('dy',15)
				        	 	.style('font-size','12px');
		        	 }else if(widgetType=="alert"){
		        		 var streamForAlert = scope.val[6]+" stream";
		        		 name = scope.val[1]+" alert of";
		        		 if(scope.val[1]=="Alert Deleted !!"){
		        			 name = scope.val[1];
		        		 	 streamForAlert = '';
		        		 }
		        		 description = scope.val[2];
		        		 msgcount.append("svg:text")																																																																																																																																
			        	 	.text(name)
			        	 	.attr('x',10)
			        	 	.attr('y',20)
			        	 	.style('font-size','16px')
			        	 	.append("tspan")
				        	 	.text(streamForAlert)
				        	 	.attr('x',10)
				        	 	.attr('dy',15)
				        	 	.style('font-size','12px');
		        	 		}
			        	 	msgcount.append("svg:text")
			        	 	.attr('y',80)
			        	 	.append("tspan")
			        	 		.attr('id','tspanValue'+scope.index)
				        	 	//.text(scope.widgetvalue[scope.val[0]])
				        	 	.attr('x',120)
				        	 	.attr('text-anchor','middle')
				        	 	.style('font-size','42px');
			        	    msgcount.append("svg:text")
				        	    .attr('x',10)
				        	 	.attr('y',110)
				        	 	.style('font-size','12px')
				        	    .text("Reset time: "+resetTime+" min");
			        	    msgcount.append("svg:image")
	        	    			.attr('x',218)
					    	.attr('y',95)
					    	.attr('width',14)
					    	.attr('height',18)
					    	.attr('xlink:href','/src/app/centinelUI/assets/images/delete-icon.png')
					    	.style('cursor','hand')
					    	.on('click', function() {
					    		console.info("Delete icon clicked");
					    		if(scope.confirmDelete()){
					    			deleteWidget();					    			
					    		}
		                		})
		                	.on('mouseover', function() {
		                		d3.select(this)
		                		 .attr('width',15)
		                		 .attr('height',19);
			        			tooltip.transition()
			        			.style('opacity',1);
			        			tooltip.html("Delete Widget")
			        			.style('left',(d3.event.pageX + 10)+ 'px') //position of the tooltip
			        			.style('top',(d3.event.pageY + 15) + 'px'); 
		                		})
				        	  .on('mouseout',function(d){
				        		  d3.select(this)
			                		 .attr('width',14)
			                		 .attr('height',18);
				        		  tooltip.transition()
				        			.style('opacity',0);
				        	  	});
		        	    var tspanValue = d3.select(tspanId);
		        	    tspanValue.text(scope.widgetvalue[scope.val[0]]);
		        	    scope.$watch('widgetvalue['+scope.val[0]+']', function(newValue,oldValue) {
			        	 		console.info('in watch');
			        	 		tspanValue.text(scope.widgetvalue[scope.val[0]]);
			        	    },true);
		        	    timeoutId = $interval(function() {
		        	    	console.info('in interval ');
		        	    	//c = c+1
		        	        updateCount(); // update DOM
		        	      }, 10000);
		        	    element.on('$destroy', function() {
		        	    	stopInterval();
		                  });
		         } 
		      }
		   }]);
	
});
