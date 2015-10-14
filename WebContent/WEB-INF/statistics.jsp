<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><jsp:useBean id="statisticsBean" class="beans.StatisticsBean" scope="request" /><%--
--%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Browserprint - Statistics</title>
	<link type="text/css" href="style.css" rel="stylesheet">
</head>
<body>
<%@include file="header.html" %>
	<script type="text/javascript" src="scripts/jquery-1.11.2.min.js"></script>
	<script type="text/javascript" src="scripts/highcharts.js"></script>
	<script>
		/*
		 * Modified code from Am I Unique?
		 * https://amiunique.org
		 * 
		 * The MIT License (MIT)
		 *
		 * Copyright (c) 2014 Pierre Laperdrix
		 *
		 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
		 *  to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
		 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
		 *
		 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
		 *
		 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
		 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
		 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
		 */
		$(function() {
			/* Percentage of Tor Users */
			jQuery.getJSON("json?chart=usingTor", function(jsonData){
				var nbTotal = 0;
				$.each(jsonData, function(key, num) {
					nbTotal += num;
				});
				
				var dataArray = [];
				$.each(jsonData, function(key, val){
					var name = "ERROR";
					if(key == "1"){
						name = "Using Tor";
					}
					else if(key == "0"){
						name = "Not using Tor";
					}
					var per = val*100/nbTotal;
					dataArray.push({name:name, y:per});
				});
				$('#torUsersGraph').highcharts({
					chart: {
						type: 'pie'
					},
					title: {
						text: 'Percentage of Tor Users'
					},
					plotOptions: {
						series: {
							dataLabels: {
								enabled: true,
								format: '{point.name}: {point.y:.1f}%'
							}
						}
					},
					tooltip: {
						headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
						pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
					},
					series: [{
						name: 'Clients',
						colorByPoint: true,
						data: dataArray
					}]
				});
			});
			
			/* Cookies enabled */
			jQuery.getJSON("json?chart=cookiesEnabled", function(jsonData){
				var nbTotal = 0;
				$.each(jsonData, function(key, num) {
					nbTotal += num;
				});
				
				var dataArray = [];
				$.each(jsonData, function(key, val){
					var name = "ERROR";
					if(key == "1"){
						name = "Cookies enabled";
					}
					else if(key == "0"){
						name = "Cookies disabled";
					}
					var per = val*100/nbTotal;
					dataArray.push({name:name, y:per});
				});
				$('#cookiesGraph').highcharts({
					chart: {
						type: 'pie'
					},
					title: {
						text: 'Percentage of clients with cookies enabled'
					},
					plotOptions: {
						series: {
							dataLabels: {
								enabled: true,
								format: '{point.name}: {point.y:.1f}%'
							}
						}
					},
					tooltip: {
						headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
						pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
					},
					series: [{
						name: 'Clients',
						colorByPoint: true,
						data: dataArray
					}]
				});
			});
			
			/* OS */
			jQuery.getJSON("json?chart=OS", function(jsonData){
				var nbTotal = 0;
				$.each(jsonData, function(key, os) {
					$.each(os, function(ver, numb) {
						nbTotal += numb;
					});
				});

				var osArray = [];
				var verArray = [];
				var colors = Highcharts.getOptions().colors;
				var colorI = 0;
				$.each(jsonData, function(key, os) {
					var total = 0;
					$.each(os, function(ver, numb) {
						total += numb;
						verArray.push({
							name : ver,
							y : numb * 100 / nbTotal,
							color : Highcharts.Color(colors[colorI]).brighten(0.12)
									.get(),
							visible : true
						});
					});
					osArray.push({
						name : key,
						y : total * 100 / nbTotal,
						color : colors[colorI]
					});
					colorI += 1;
				});
	
				$('#osGraph').highcharts({
					chart : {
						type : 'pie'
					},
					title : {
						text : 'Operating systems'
					},
					subtitle: {
						text: 'Based on User-Agent string.'
					},
					yAxis : {
						title : {
							text : 'Total percent share'
						}
					},
					plotOptions : {
						pie : {
							shadow : false,
							center : [ '50%', '50%' ]
						}
					},
					tooltip : {
						valueSuffix : '%',
						formatter : function() {
							return '<b>' + this.point.name
									+ '</b><br>Percentage : '
									+ Highcharts.numberFormat(this.y, 2)
									+ '%';
						}
					},
					series : [
							{
								name : 'Percentage',
								data : osArray,
								size : '60%',
								dataLabels : {
									formatter : function() {
										return this.y > 5 ? this.point.name
												: null;
									},
									color : 'white',
									distance : -30
								}
							},
							{
								name : 'Percentage',
								data : verArray,
								size : '80%',
								innerSize : '60%',
								dataLabels : {
									formatter : function() {
										// display only if larger than 1
										return this.y > 1 ? '<b>'
												+ this.point.name
												+ ':</b> '
												+ Highcharts.numberFormat(
														this.y, 2) + '%'
												: null;
									}
								}
							} ]
					})
			});	

			/* Browser */
			jQuery.getJSON("json?chart=browser", function(jsonData){
				var nbTotal = 0;
				$.each(jsonData, function(key, browser) {
					$.each(browser, function(ver, numb) {
						nbTotal += numb;
					});
				});

				var browserArray = [];
				var verArray = [];
				var colors = Highcharts.getOptions().colors;
				var colorI = 0;
				$.each(jsonData, function(key, browser) {
					var total = 0;
					$.each(browser, function(ver, numb) {
						total += numb;
						verArray.push({
							name : ver,
							y : numb * 100 / nbTotal,
							color : Highcharts.Color(colors[colorI]).brighten(0.12)
									.get(),
							visible : true
						});
					});
					browserArray.push({
						name : key,
						y : total * 100 / nbTotal,
						color : colors[colorI]
					});
					colorI += 1;
				});
	
				$('#browserGraph').highcharts({
					chart : {
						type : 'pie'
					},
					title : {
						text : 'Browsers'
					},
					subtitle: {
						text: 'Based on User-Agent string.'
					},
					yAxis : {
						title : {
							text : 'Total percent share'
						}
					},
					plotOptions : {
						pie : {
							shadow : false,
							center : [ '50%', '50%' ]
						}
					},
					tooltip : {
						valueSuffix : '%',
						formatter : function() {
							return '<b>' + this.point.name
									+ '</b><br>Percentage : '
									+ Highcharts.numberFormat(this.y, 2)
									+ '%';
						}
					},
					series : [
							{
								name : 'Percentage',
								data : browserArray,
								size : '60%',
								dataLabels : {
									formatter : function() {
										return this.y > 5 ? this.point.name
												: null;
									},
									color : 'white',
									distance : -30
								}
							},
							{
								name : 'Percentage',
								data : verArray,
								size : '80%',
								innerSize : '60%',
								dataLabels : {
									formatter : function() {
										// display only if larger than 1
										return this.y > 1 ? '<b>'
												+ this.point.name
												+ ':</b> '
												+ Highcharts.numberFormat(
														this.y, 2) + '%'
												: null;
									}
								}
							} ]
					})
			});
			
			/* TimeZone */
			jQuery.getJSON("json?chart=timezone", function(timeData){
				var nbTotal = 0;
				$.each(timeData, function(key, num) {
					nbTotal += num;
				});
				
				var timeArray = [];
				var timeDrillArray = [];
				var nbTimeOthers = 0;
				$.each(timeData, function(key, tab){
					if(key != "No JS") {
						var name = parseInt(key,10)/-60;
						if(name<0){
							name = "UTC" + name ;
						} else {
							name = "UTC+" + name ;
						}
					} else {
						var name = "Not specified";
					}
					var per = tab*100/nbTotal;
					if(per>3){
						timeArray.push({name:name, y:per});
					} else {
						nbTimeOthers += per;
						timeDrillArray.push({name:name,y:per});
					}
				});
				timeArray.push({name:"Others", drilldown:"Others", y:nbTimeOthers});
				var timeDrillSeries = [{id:"Others", name:"Others", data: timeDrillArray}];
				$('#timezoneGraph').highcharts({
					chart: {
						type: 'pie'
					},
					title: {
						text: 'Timezone'
					},
					subtitle: {
						text: 'Click on the \"Others\" slice to view more details.'
					},
					plotOptions: {
						series: {
							dataLabels: {
								enabled: true,
								format: '{point.name}: {point.y:.1f}%'
							}
						}
					},
					tooltip: {
						headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
						pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
					},
					series: [{
						name: 'Timezones',
						colorByPoint: true,
						data: timeArray
					}],
					drilldown: {
						series: timeDrillSeries
					}
				});
			});
		});
	</script>
	<h1>
		Out of a total of ${ statisticsBean.numSamples } fingerprint samples:
	</h1>
	<div id="torUsersGraph" style="height: auto; width: auto" data-highcharts-chart="1"></div>
	<div id="cookiesGraph" style="height: auto; width: auto" data-highcharts-chart="2"></div>
	<div id="osGraph" style="height: auto; width: auto" data-highcharts-chart="3"></div>
	<div id="browserGraph" style="height: auto; width: auto" data-highcharts-chart="4"></div>
	<div id="timezoneGraph" style="height: auto; width: auto" data-highcharts-chart="5"></div>
<%@include file="footer.jsp" %>
</body>
</html>