<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint</title>
<script type="text/javascript" src="scripts/jquery-1.11.2.min.js"></script>
<script type="text/javascript">
	/*
	 * Script that adds js_enabled=true to the end of the test_link.
	 * This is how we know that Javascript is enabled.
	 */
	$(function() {
		var test_link = $("#test_link");
		test_link.attr("href", test_link.attr("href") + "?js_enabled=true");
	});
</script>
<link type="text/css" href="style.css" rel="stylesheet">
</head>
<body>
<%@include file="/WEB-INF/header.html" %>
<div id="content">
	<p>
		Does your web browser have a unique fingerprint?
		If so your web browser could be tracked across websites without techniques such as tracking cookies.
		Additionally the anonymisation aspects of services such as Tor or VPNs could be negated if websites you visit track you using your browser fingerprint.
		This service is designed to test how unique your web browser's fingerprint is, and hence how identifiable your browser is.
	</p>
	<p>
		This is a free service provided for research purposes.
		If you are worried about privacy feel free to read our <a href="privacy">privacy policy</a>.
	</p>
	<div id="testLink">
		<p>
			<a id="test_link" href="test"><img src="images/fingerprint.click.png" alt="Fingerprint me button"></a>
		</p>
	</div>
	<p>
		Browserprint is a free open source project designed to provide the same and better functionality as the <a href="https://panopticlick.eff.org/">original Panopticlick</a>.
		Several of the tests are based on publicly available code from <a href="https://amiunique.org/">Am I unique?</a>.
	</p>
</div>
<%@include file="/WEB-INF/footer.jsp" %>
</body>
</html>