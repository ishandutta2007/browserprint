<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint - Sources of tests</title>
<link type="text/css" href="style.css" rel="stylesheet">
</head>
<body>
<common:header/>
<div id="content">
	<h1>Sources of our tests</h1>
	<p>
		The ideas behind our tests come from a variety of sources, most notably <a href="https://panopticlick.eff.org/">Panopticlick</a>, <a href="https://amiunique.org/">Am I unique?</a>, <a href="https://github.com/Valve/fingerprintjs2">Fingerprintjs2</a>, and Tor Project Trac tickets.
		The following is a list of each test and where the inspiration for it came from.
	</p>
	<p>
		Note: We may not always cite the original source of a test. Please contact us if we misattribute a test.
	</p>
	<h3>Monitor Contrast Level</h3>
	<p>
		Original.
	</p>
	<h3>Colour Vision</h3>
	<p>
		Original. [Test removed for ethics reasons]
	</p>
	<h3>User Agent</h3>
	<p>
		Panopticlick.
	</p>
	<h3>HTTP_ACCEPT Headers</h3>
	<p>
		Panopticlick.
	</p>
	<h3>Platform (JavaScript)</h3>
	<p>
		Panopticlick.
	</p>
	<h3>Platform (Flash)</h3>
	<p>
		AmIUnique.
	</p>
	<h3>Browser Plugin Details</h3>
	<p>
		Panopticlick.
	</p>
	<h3>Time Zone</h3>
	<p>
		Panopticlick.
	</p>
	<h3>Screen Size and Color Depth</h3>
	<p>
		Panopticlick.
	</p>
	<h3>Screen Size (Flash)</h3>
	<p>
		AmIUnique.
	</p>
	<h3>Screen Size (CSS)</h3>
	<p>
		Based on a proof of concept from <a href="https://github.com/arthuredelstein">Arthur Edelstein</a>.
	</p>
	<h3>Language (Flash)</h3>
	<p>	
		AmIUnique.
	</p>
	<h3>System Fonts (Flash)</h3>
	<p>
		Panopticlick.
	</p>
	<h3>System Fonts (JS/CSS)</h3>
	<p>
		Fingerprintjs2.
	</p>
	<h3>System Fonts (CSS)</h3>
	<p>
		Based on test in the <a href="http://ip-check.info">JonDonym IP check</a> test page.
	</p>
	<h3>Character Sizes</h3>
	<p>
		Based on <a href="https://www.bamsoftware.com/talks/fc15-fontfp/fontfp.html">work</a> by David Fifield and Serge Egelmantalk.<br/>
		Link to paper: <a href="https://www.bamsoftware.com/papers/fontfp.pdf">Fingerprinting web users through font metrics</a>
	</p>
	<h3>Are Cookies Enabled?</h3>
	<p>
		Panopticlick.
	</p>
	<h3>Limited supercookie test</h3>
	<p>
		Panopticlick.
	</p>
	<h3>IndexedDB Enabled Test</h3>
	<p>
		Fingerprintjs2.
	</p>
	<h3>Do Not Track header</h3>
	<p>
		Original.
	</p>
	<h3>Client/server time difference (minutes)</h3>
	<p>
		<a href="https://trac.torproject.org/projects/tor/ticket/2940">Tor Trac ticket #2940</a>
	</p>
	<h3>Date/Time format test</h3>
	<p>
		<a href="https://trac.torproject.org/projects/tor/ticket/15473">Tor Trac ticket #15473</a>
	</p>
	<h3>Math / Tan function</h3>
	<p>
		<a href="https://trac.torproject.org/projects/tor/ticket/13018">Tor Trac ticket #13018</a>
	</p>	
	<h3>Using Tor?</h3>
	<p>
		Original.
	</p>
	<h3>Blocking Ads?</h3>
	<p>
		Original, but also found on AmIUnique.
	</p>
	<h3>Blocking like/share buttons?</h3>
	<p>
		Original.
	</p>
	<h3>Canvas</h3>
	<p>
		AmIUnique? and Fingerprintjs2.
	</p>
	<h3>WebGL Vendor</h3>
	<p>
		AmIUnique?
	</p>
	<h3>WebGL Renderer</h3>
	<p>
		AmIUnique?
	</p>
	<h3>Touch Support</h3>
	<p>
		Fingerprintjs2.
	</p>
	<h3>Audio Fingerprints</h3>
	<p>
		Code taken with permission from https://audiofingerprint.openwpm.com/
		Tests discovered in the wild by Steven Englehardt and Arvind Narayanan in the paper Online tracking: A 1-million-site measurement and analysis.
	</p>
</div>
<common:footer/>
</body>
</html>