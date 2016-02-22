<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint</title>
<meta name="robots" content="noindex" >
<meta http-equiv="refresh" content="4; url=test" />
<script type="text/javascript" src="scripts/jquery-1.11.2.min.js"></script>
<script type="text/javascript">
$(window).load(function(){
	$(location).attr('href', 'test?js_enabled=true');
});
</script>
</head>
<body>
	<p>
		Redirecting...
	</p>
</body>
</html>