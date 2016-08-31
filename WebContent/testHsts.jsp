<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="true"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint</title>
<link type="text/css" href="style.css" rel="stylesheet">
</head>
<body>
<common:header/>
<div id="content">
	I see you found my secret page.<br>
	I'm testing a feature that will be released later.<br>
	<img src="<c:url value="http://hsts0.browserprint.info/hstsSuperCookie/start/1"/>"></img>
	<img src="<c:url value="http://hsts0.browserprint.info/hstsSuperCookie/start/2"/>"></img>
	<img src="<c:url value="http://hsts0.browserprint.info/hstsSuperCookie/start/3"/>"></img>
	<img src="<c:url value="http://hsts0.browserprint.info/hstsSuperCookie/start/4"/>"></img>
	<img src="<c:url value="http://hsts0.browserprint.info/hstsSuperCookie/start/5"/>"></img>
	<img src="<c:url value="http://hsts0.browserprint.info/hstsSuperCookie/start/6"/>"></img>
	<img src="<c:url value="http://hsts0.browserprint.info/hstsSuperCookie/start/7"/>"></img>
	<img src="<c:url value="http://hsts0.browserprint.info/hstsSuperCookie/start/8"/>"></img>
</div>
<common:footer/>
</body>
</html>