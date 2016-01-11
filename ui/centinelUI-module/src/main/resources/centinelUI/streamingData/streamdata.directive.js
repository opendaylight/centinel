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
	
	centinelUIApp.register.directive('lineChart',function () {
		 return {
			 restrict: 'E',
			 replace: true,
			 scope:{lines:'='},
			 template: '<div id="lineChart"></div>',
			 link: function(scope, element, attrs, ctrl) {
				
				var margin = {top: 20, right: 80, bottom: 30, left: 50},
				    width = 962 - margin.left - margin.right,
				    height = 162 - margin.top - margin.bottom;
				var parseDate = d3.time.format("%d%m %H:%M %p").parse;
				/*var parseDate = d3.time.format("%Y%m%d").parse;*/
				
				var x = d3.time.scale()
				    .range([0, width]);

				var y = d3.scale.linear()
				    .range([height, 0]);

				var color = d3.scale.category10();

				var xAxis = d3.svg.axis()
				    .scale(x)
				    .orient("bottom");

				var yAxis = d3.svg.axis()
				    .scale(y)
				    .orient("left");

				var line = d3.svg.line()
				    .interpolate("basis")
				    .x(function(d) { return x(d.date); })
				    .y(function(d) { return y(d.temperature); });

				var svg = d3.select("#lineChart").append("svg")
				    .attr("width", width + margin.left + margin.right)
				    .attr("height", height + margin.top + margin.bottom)
				  .append("g")
				    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
				
				function plotLineChart(lineChartData) {
				  color.domain(d3.keys(lineChartData[0]).filter(function(key) { return key !== "date"; }));

				  _.each(lineChartData, function(d) {
					  if(lineChartData != [])
						  d.date = parseDate(d.date);
				});


				  var cities = color.domain().map(function(name) {
				    return {
				      name: name,
				      values: _.map(lineChartData,function(d) {
				        return {date: d.date, temperature: +d[name]};
				      })
				    };
				  });

				  x.domain(d3.extent(lineChartData, function(d) { return d.date; }));

				  y.domain([
				    d3.min(cities, function(c) { return d3.min(c.values, function(v) { return v.temperature; }); }),
				    d3.max(cities, function(c) { return d3.max(c.values, function(v) { return v.temperature; }); })
				  ]);

				  svg.append("g")
				      .attr("class", "x axis")
				      .attr("transform", "translate(0," + height + ")")
				      .call(xAxis)
				      .append("text")
				        .text("time -->")
					  	.attr("x", 2)
					  	.attr("dx", ".71em")
					  	.style("text-anchor", "start");
				  svg.append("g")
				      .attr("class", "y axis")
				      .call(yAxis)
				    .append("text")
				      .text("Count")
				      .attr("transform", "rotate(-90)")
				      .attr("y", 6)
				      .attr("dy", ".71em")
				      .style("text-anchor", "end");

				  var city = svg.selectAll(".city")
				      .data(cities)
				    .enter().append("g")
				      .attr("class", "city");

				  city.append("path")
				      .attr("class", "line")
				      .attr("d", function(d) { return line(d.values); })
				      .style("stroke", function(d) { return color(d.name); });

				  city.append("text")
				      .datum(function(d) { return {name: d.name, value: d.values[d.values.length - 1]}; })
				      .attr("transform", function(d) { return "translate(" + x(d.value.date) + "," + y(d.value.temperature) + ")"; })
				      .attr("x", 3)
				      .attr("dy", ".35em")
				      .text(function(d) { return d.name; });
				};
	
				 scope.$watch('lines', function(newValue, oldValue) {
		                if (true){
		                    console.log("I see a data change! old");
		                console.log(oldValue);
		                plotLineChart(newValue);
		                console.log("I see a data change!  new");
		                console.log(newValue);
		                }
		            }, true);
				

			 }
			 	
		 }
	});
});