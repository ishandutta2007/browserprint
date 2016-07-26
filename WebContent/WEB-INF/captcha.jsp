<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="true"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><jsp:useBean id="captchaBean" class="beans.CaptchaBean" scope="request" /><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Browserprint</title>
	<meta name="robots" content="noindex" >
	<link rel="stylesheet" type="text/css" href="style.css">
	<link rel="stylesheet" type="text/css" href="<c:url value="screenSizeCSS.css"/>">
	<common:fontDetectCSSTestHead/>
</head>
<body>
	<common:fontDetectCSSTestBody/>
	<%--This script tag needs to be after the fontDetectCSSTest tag otherwise the CSS font test breaks in Opera. No idea why.
	--%><script type="text/javascript" src="scripts/jquery-1.11.2.min.js"></script>
	<p>
		Please type the letters from the image into the box below.
	</p><%--
	--%><c:if test="${ requestScope.error != null }">
	<p class="error">
		<c:out value="${ requestScope.error }"></c:out>
	</p>
	</c:if>
	<div>
		<img src="${ captchaBean.captchaSrc }" alt="A text CAPTCHA"/>
		<form action="<c:url value="test"/>" method="POST">
			<div id="captchaDiv">
				<script type="text/javascript">
				$('<input>').attr({
					type: 'hidden',
					name: 'js_enabled',
					value: 'true'
				}).appendTo('#captchaDiv');
				</script>
				<input type="text" name="captchaAnswer"><br/>
				<input type="submit" value="Continue">
			</div>
		</form>
	</div>
	<div id="device-width"></div>
	<div id="device-height"></div>
</body>
</html>