<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><jsp:useBean id="platesBean" class="beans.PlatesBean" scope="request" /><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint</title>
<meta name="robots" content="noindex" >
<script type="text/javascript" src="scripts/jquery-1.11.2.min.js"></script>
<script type="text/javascript">
$(window).load(function(){
	$('<input>').attr({
		type: 'hidden',
		name: 'js_enabled',
		value: 'true'
	}).appendTo('#captchaForm');
});
</script>
</head>
<body>
	<p>
		Please type the numbers in to the box and click continue.
		Some circles may not contain numbers.
	</p>
	<p>
		The purpose of this is to confirm that you're human.
	</p>
	<div><%--
		--%><c:forEach var="plate" items="${ platesBean.plates }">
		<img src="images/captcha/${ plate }.gif" height="150" width="150"/><%--
		--%></c:forEach>
		<form id="captchaForm" action="test" method="POST">
			<input type="hidden" id="encryptedCaptcha" value="${ platesBean.platesEncrypted }">
			<input type="text" name="UUID1UUID" id="UuidTextbox1" class="viewUuidTextbox" maxlength="36" style="display: inline-block;"><br/>
			<input type="submit" value="Continue">
		</form>
	</div>
</body>
</html>