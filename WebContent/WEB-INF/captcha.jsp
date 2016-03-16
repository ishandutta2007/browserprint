<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint</title>
<meta name="robots" content="noindex" >
<script type="text/javascript" src="scripts/jquery-1.11.2.min.js"></script>
<script type="text/javascript">
$(window).load(function(){
	$('#captchaForm').attr('action', 'test?js_enabled=true');
});
</script>
</head>
<body>
	<p>
		Please type the numbers in to the box and click continue.
	</p>
	<p>
		The purpose of this is to confirm that you're human.
	</p>
	<form id="captchaForm" action="test" method="get">
		<input type="text" name="UUID1UUID" id="UuidTextbox1" class="viewUuidTextbox" maxlength="36" style="display: inline-block;"></br>
		<input type="submit" name="action" value="Continue" class="sampleViewButton">
	</form>
</body>
</html>