<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta charset="UTF-8">
<title>Browserprint - Error</title>
</head>
<body>
	<c:choose><%--
		--%><c:when test="${ requestScope.errorMessage != null}">Error: <c:out value="${ requestScope.errorMessage }"></c:out></c:when><%--
		--%><c:otherwise>An error has occurred.</c:otherwise><%--
	--%></c:choose>
</body>
</html>