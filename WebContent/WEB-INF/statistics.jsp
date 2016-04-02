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
	<script type="text/javascript" src="scripts/Highcharts/highcharts.js"></script>
	<script type="text/javascript" src="scripts/Highcharts/modules/drilldown.js"></script>
	<script type="text/javascript" src="scripts/statistics.js"></script>
	<h1>
		Out of a total of ${ statisticsBean.numSamples } fingerprint samples:
	</h1>
	<div id="torUsersGraph" style="height: auto; width: auto"></div>
	<div id="cookiesGraph" style="height: auto; width: auto"></div>
	<div id="osGraph" style="height: auto; width: auto"></div>
	<div id="browserGraph" style="height: auto; width: auto"></div>
	<div id="timezoneGraph" style="height: auto; width: auto"></div>
	<div id="languageGraph" style="height: auto; width: auto"></div>
	<div id="adsBlockedGraph" style="height: auto; width: auto"></div>
<%@include file="footer.jsp" %>
</body>
</html>