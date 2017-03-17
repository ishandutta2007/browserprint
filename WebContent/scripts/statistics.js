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
	/* Anonymity sets line graph */
	jQuery.getJSON("json?chart=anonymitySets", function(jsonData){
		var largestSetSize = jsonData["Largest anonymity set"];
		
		var jsDisabledArray = new Array(largestSetSize);
		var jsEnabledArray = new Array(largestSetSize);
		var bothArray = new Array(largestSetSize);
		//Initialise the arrays to zero instead of null
		for(var i = 0; i < largestSetSize; ++i){
			bothArray[i] = jsEnabledArray[i] = jsDisabledArray[i] = 0.0001;
		}
		
		$.each(jsonData["JavaScript disabled"], function(setSize, num) {
			jsDisabledArray[setSize - 1] = setSize * num;
		});
		
		$.each(jsonData["JavaScript enabled"], function(setSize, num) {
			jsEnabledArray[setSize - 1] = setSize * num;
		});
		
		var totalCount = 0;
		$.each(jsonData["Both"], function(setSize, num) {
			totalCount += num;
			bothArray[setSize - 1] = setSize * num;
		});

		$('#anonymitySets').highcharts({
			chart : {
				type : 'line'
			},
		    title: {
		        text: 'Anonymity set sizes'
		    },
		    subtitle: {
		        text: 'Or in other words: How many fingerprints match x other fingerprints'
		    },
		    yAxis: {
		    	type: 'logarithmic',
		        title: {
		            text: 'Number of fingerprints who are the same as x other fingerprints'
		        },
		        min: 0.1
		    },
		    xAxis: {
		    	type: 'logarithmic',
		        title: {
		            text: 'Anonymity set size'
		        }
		    },
		    legend: {
		        layout: 'vertical',
		        align: 'right',
		        verticalAlign: 'top',
		        floating: true
		    },
		    plotOptions: {
		        series: {
		            pointStart: 1
		        }
		    },
		    series: [
		    	{
		    		name: 'Both fingerprints with JavaScript enabled and disabled',
		    		data: bothArray
		    	},
		    	{
		    		name: 'Fingerprints with JavaScript enabled',
		    		data: jsEnabledArray
		    	},
		    	{
		    		name: 'Fingerprints with JavaScript disabled',
		    		data: jsDisabledArray
		    	},
		    ]
		})
	});
	
	/* Uniqueness */
	jQuery.getJSON("json?chart=uniqueness", function(jsonData){
		var nbTotal = 0;
		$.each(jsonData, function(key, browser) {
			$.each(browser, function(ver, numb) {
				nbTotal += numb;
			});
		});

		var uniquenessArray = [];
		var verArray = [];
		var colors = Highcharts.getOptions().colors;
		var colorI = 0;
		$.each(jsonData, function(key, uniqueness) {
			var total = 0;
			$.each(uniqueness, function(ver, numb) {
				total += numb;
				verArray.push({
					name : ver,
					y : numb * 100 / nbTotal,
					color : Highcharts.Color(colors[colorI]).brighten(0.12)
							.get(),
					visible : true
				});
			});
			uniquenessArray.push({
				name : key,
				y : total * 100 / nbTotal,
				color : colors[colorI]
			});
			colorI += 1;
		});

		$('#uniquenessGraph').highcharts({
			chart : {
				type : 'pie'
			},
			title : {
				text : 'Fingerprint uniqueness'
			},
			subtitle: {
				text: 'With less stable tests not included. Among samples taken from 2016-07-06 onwards.'
			},
			yAxis : {
				title : {
					text : 'Total percent'
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
						data : uniquenessArray,
						size : '60%',
						dataLabels : {
							formatter : function() {
								return this.y > 5 ? this.point.name
										: null;
							},
							color : 'black',
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
							color : 'black',
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
							color : 'black',
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
	jQuery.getJSON("json?chart=timezone", function(jsonData){
		var nbTotal = 0;
		$.each(jsonData, function(key, num) {
			nbTotal += num;
		});
		
		var dataArray = [];
		var otherDrillArray = [];
		var nbOthers = 0;
		$.each(jsonData, function(key, tab){
			var name = key;
			if(isNaN(name)){
				if(name != "No JavaScript") {
					name = "Non-numeric timezone";
				}
			}
			else{						
				name = parseInt(name,10)/-60;
				if(name < 0){
					name = "UTC" + name;
				} else {
					name = "UTC+" + name;
				}
			}
			var per = tab*100/nbTotal;
			if(per>3){
				dataArray.push({name:name, y:per});
			} else {
				nbOthers += per;
				otherDrillArray.push({name:name,y:per});
			}
		});
		dataArray.push({name:"Others", drilldown:"Others", y:nbOthers});
		var dataDrillSeries = [{id:"Others", name:"Other timezones", data:otherDrillArray}];
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
				data: dataArray
			}],
			drilldown: {
				series: dataDrillSeries
			}
		});
	});
	
	/* Languages */
	jQuery.getJSON("json?chart=language", function(jsonData){
		var nbTotal = 0;
		$.each(jsonData, function(key, num) {
			nbTotal += num;
		});
		
		var dataArray = [];
		var otherDrillArray = [];
		var nbOthers = 0;
		$.each(jsonData, function(key, tab){
			var per = tab*100/nbTotal;
			if(per>3){
				dataArray.push({name:key, y:per});
			} else {
				nbOthers += per;
				otherDrillArray.push({name:key,y:per});
			}
		});
		dataArray.push({name:"Others", drilldown:"Others", y:nbOthers});
		var dataDrillSeries = [{id:"Others", name:"Other client languages", data:otherDrillArray}];
		$('#languageGraph').highcharts({
			chart: {
				type: 'pie'
			},
			title: {
				text: 'Breakdown of languages'
			},
			subtitle: {
				text: 'As detected using Flash.'
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
				name: 'Client languages',
				colorByPoint: true,
				data: dataArray
			}],
			drilldown: {
				series: dataDrillSeries
			}
		});

	});
	
	/* Ads blocked */
	jQuery.getJSON("json?chart=adsBlockedGoogle", function(jsonData){
		var nbTotal = 0;
		$.each(jsonData, function(key, num) {
			nbTotal += num;
		});
		
		var dataArray = [];
		$.each(jsonData, function(key, val){
			if(key == "1"){
				name = "Ads blocked";
			}
			else if(key == "0"){
				name = "Ads allowed";
			}
			else{
				name = key;
			}
			var per = val*100/nbTotal;
			dataArray.push({name:name, y:per});
		});
		$('#adsBlockedGoogleGraph').highcharts({
			chart: {
				type: 'pie'
			},
			title: {
				text: 'Percentage of clients with Google ads blocked'
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
	
	/* ScreenDetails */
	jQuery.getJSON("json?chart=screenDetails", function(jsonData){
		var nbTotal = 0;
		$.each(jsonData, function(key, num) {
			nbTotal += num;
		});
		
		var dataArray = [];
		var otherDrillArray = [];
		var nbOthers = 0;
		$.each(jsonData, function(key, tab){
			var per = tab*100/nbTotal;
			if(per>3){
				dataArray.push({name:key, y:per});
			} else {
				nbOthers += per;
				otherDrillArray.push({name:key,y:per});
			}
		});
		dataArray.push({name:"Others", drilldown:"Others", y:nbOthers});
		var dataDrillSeries = [{id:"Others", name:"Other screen sizes and colour depths", data:otherDrillArray}];
		$('#screenDetailsGraph').highcharts({
			chart: {
				type: 'pie'
			},
			title: {
				text: 'Screen sizes and colour depth.'
			},
			subtitle: {
				text: 'As detected using JavaScript.'
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
				name: 'Client languages',
				colorByPoint: true,
				data: dataArray
			}],
			drilldown: {
				series: dataDrillSeries
			}
		});

	});
});