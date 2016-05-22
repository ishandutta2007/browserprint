<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint</title>
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
			<a id="test_link" href="captcha"><img src="images/fingerprint.click.png" alt="Fingerprint me button"></a>
		</p>
	</div>
	<p>
		Browserprint is a free open source project designed to provide the same and better functionality as the <a href="https://panopticlick.eff.org/">original Panopticlick</a>.
		Several of the tests are based on publicly available code from <a href="https://amiunique.org/">Am I unique?</a> and <a href="https://github.com/Valve/fingerprintjs2">Github user Valve's Fingerprintjs2</a>.
	</p>
	<p>
		Browserprint is developed by a student of, and generously hosted by the <a href="https://www.adelaide.edu.au/">University of Adelaide</a>.
		This project would not be possible without their help.
	</p>
</div>
<%@include file="/WEB-INF/footer.jsp" %>
</body>
</html>