<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%--
--%><jsp:useBean id="chrsBean" class="beans.CharacteristicsBean" scope="request" /><%--
--%><jsp:useBean id="uniquenessBean" class="beans.UniquenessBean" scope="request" /><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Browserprint</title>
	<meta name="robots" content="noindex" />
	<link type="text/css" href="style.css" rel="stylesheet">
</head>
<body>
<%@include file="header.html" %>
	<p>
		<a href=".">Back</a>
	</p>
<common:displayFingerprint uniquenessBean="${ uniquenessBean }" chrsBean="${ chrsBean }"/>
<%@include file="footer.jsp" %>
</body>
</html>