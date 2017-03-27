<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><jsp:useBean id="statisticsBean" class="beans.StatisticsBean" scope="request" /><%--
--%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Browserprint - Statistics</title>
	<link type="text/css" href="style.css" rel="stylesheet">
</head>
<body>
<common:header/>
	<script type="text/javascript" src="scripts/jquery-1.11.2.min.js"></script>
	<script type="text/javascript" src="scripts/Highcharts/highcharts.js"></script>
	<script type="text/javascript" src="scripts/Highcharts/modules/drilldown.js"></script>
	<script type="text/javascript" src="scripts/statistics.js"></script>
	<h1>
		Out of a total of ${ statisticsBean.numSamples } fingerprint samples:
	</h1>
	<div id="statisticsDiv">
		<div id="anonymitySetsCCDF" style="height: auto; width: auto;"></div>
		<div id="uniquenessGraph" style="height: auto; width: auto; float: left"></div>
		<div id="torUsersGraph" style="height: auto; width: auto; float: right"></div>
		<div id="osGraph" style="height: auto; width: auto; float: left"></div>
		<div id="cookiesGraph" style="height: auto; width: auto; float: right"></div>
		<div id="timezoneGraph" style="height: auto; width: auto; float: left"></div>
		<div id="browserGraph" style="height: auto; width: auto; float: right"></div>
		<div id="adsBlockedGoogleGraph" style="height: auto; width: auto; float: left"></div>
		<div id="languageGraph" style="height: auto; width: auto; float: right"></div>
		<div id="screenDetailsGraph" style="height: auto; width: auto; float: left"></div>
	</div>
<common:footer/>
</body>
</html>