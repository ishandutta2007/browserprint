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
	<meta name="robots" content="noindex" >
	<link type="text/css" href="style.css" rel="stylesheet">
	<script type="text/javascript" src="scripts/jquery-1.11.2.min.js"></script>
</head>
<body>
<%@include file="header.html" %>
	<p>
		<a href=".">Back</a>
	</p>
	<common:uniquenessText uniquenessBean="${ uniquenessBean }"/>
	<p>
		Your fingerprint&#39;s UUID is <c:out value="${ requestScope.sampleUUID }"></c:out>;<br>
		you can share this to show others your fingerprint and let them compare it against their own.<br>
		Alternatively you can share your fingerprint using this URL:<br>
		<input type="text" id="UrlTextbox" size="80" value="<c:out value="http://${ initParam['websiteBaseURL'] }/view?source1=UUID&UUID1UUID=${ requestScope.sampleUUID }"></c:out>">
		<script type="text/javascript">
		$("#UrlTextbox").click(function(){
		    $(this).focus();
		    $(this).select();
		});
		</script>
	</p>
<common:displayFingerprint chrsBean="${ chrsBean }"/>
<%@include file="footer.jsp" %>
</body>
</html>