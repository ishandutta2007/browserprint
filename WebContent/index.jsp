<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
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
		Browserprint is a free <a href="https://github.com/qqTYXn7/browserprint">open source</a> project designed to provide the same and better functionality as the <a href="https://panopticlick.eff.org/">original Panopticlick</a>.
		Several of the tests are based on publicly available code from <a href="https://amiunique.org/">Am I unique?</a> and <a href="https://github.com/Valve/fingerprintjs2">Fingerprintjs2</a>.
		The inspiration for each test can be found on the <a href="sources">sources page</a>. 
	</p>
	<p>
		Browserprint is developed by a student of and generously hosted by the <a href="https://www.adelaide.edu.au/">University of Adelaide</a>.
		Browerprint's development was partially funded by <a href="http://acems.org.au/">ACEMS</a>.
		This project would not be possible without these institutions' help.
	</p>
	<p>
		<b>News 2016-09-08</b> New <a href="blog/hstsSupercookie">blog post</a> discussing HSTS supercookies.
	</p>
	<p>
		<b>News 2016-08-16</b> We've added a new test. This one detects whether HSTS is supported by the browser.
		HSTS is a web security policy that prevents browsers from accessing HTTPS sites via HTTP.
		It can be abused to store supercookies in your browser potentially without the need for JavaScript.
	</p>
	<p>
		<b>News 2016-08-05</b> We have written a <a href="blog/fingerprintingDefence">blog post</a> discussing what you can do against fingerprinting.
	</p>
	<p>
		<b>News 2016-08-02</b> We've been featured on the front page of <a href="https://yro.slashdot.org/story/16/07/31/1357234/new-site-checks-your-browsers-fingerprint">slashdot</a>!
	</p>
	<p>
		<b>News 2016-07-05:</b> We have implemented a test that detects what fonts your browser has using CSS without JavaScript.
	</p>
	<p>
		<b>News 2016-06-28:</b> We have added tests.
		These tests work using the AudioContext API and are based on real world fingerprinting code discovered here <a href="https://audiofingerprint.openwpm.com/">https://audiofingerprint.openwpm.com/</a>.
	</p>
	<p>
		<b>News 2016-06-15:</b> We have added a new test.
		This test checks to see whether like/share/social buttons are blocked or modified by the browser.
	</p>
	<p>
		<b>News 2016-06-11:</b> We have written a <a href="blog/userFingerprinting">blog post</a> discussing a kind of fingerprinting that may have never have been explored until now.
	</p>
	<p>
		<b>News 2016-06-04:</b> We have now implemented our first CSS fingerprinting test that doesn't involve JavaScript;
		 a test for the size of the monitor the browser is being displayed on.
		It's our hope that this will be just the first of many innovative CSS-only tests.
		Thanks to <a href="https://github.com/arthuredelstein">Arthur Edelstein</a> for the concept.
	</p>
</div>
<common:footer/>
</body>
</html>