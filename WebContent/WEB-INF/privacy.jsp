<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint - Privacy</title>
<link type="text/css" href="style.css" rel="stylesheet">
</head>
<body>
<%@include file="header.html" %>
<div id="content">
	<h2>Information we collect</h2>
	<p>
		When a client submits themselves to fingerprinting we collect several pieces of data about them.
	</p>
	
	<h3>Fingerprint data</h3>
	<p>
		We collect the results of all the fingerprint tests.
		This may include:
	</p>
	<ul>
		<li>Whether JavaScript is enabled or not.</li>
		<li>Whether your monitor has high contrast or not.</li>
		<li>The User-Agent header of the HTTP request for the page.</li>
		<li>The Accept header, the Accept-Encoding header, and the Accept-Language header from the HTTP request for the page.</li>
		<li>The name of the platform the browser is running on, detected using JavaScript and flash.</li>
		<li>A list of your installed browser plugins, as detected using JavaScript.</li>
		<li>The time-zone configured on your machine, as detected using JavaScript.</li>
		<li>The screen size and colour depth of the monitor displaying the client's web browser, as detected using JavaScript and flash.</li>
		<li>The language of the client's browser, as detected using Flash.</li>
		<li>The fonts installed on the client's machine, detected using Flash and JavaScript.</li>
		<li>The heights and widths of a set of Unicode characters rendered in various styles (e.g. sans-serif).</li>
		<li>Whether cookies are enabled, detected by creating a cookie and then retrieving it.</li>
		<li>Whether localStorage, sessionStorage, and Internet Explorer's userData are available in the browser, detected using JavaScript.</li>
		<li>Whether the browser supports IndexedDB, a database embedded within the browser.</li>
		<li>The value of the DNT (Do Not Track) header from the HTTP request for the page.</li>
		<li>The difference between the client and the server's clocks, in minutes, detected using JavaScript.</li>
		<li>The value returned by the JavaScript function toLocaleString() called on the UNIX epoch.</li>
		<li>The output of the JavaScript Math.tan(-1e300).</li>
		<li>Whether the client is thought to be using Tor or not. Detected by performing a TorDNSEL request.</li>
		<li>Whether ads are blocked by the browser.</li>
		<li>An image created by the client's browser using a HTML5 canvas.</li>
		<li>The name of the WebGL vendor of the client's browser; this may be the name of the client's graphics card.</li>
		<li>The name of the WebGL renderer of the client's browser; this may be the name of the client's underlying graphics driver.</li>
		<li>Information about touch screen support by the system.</li>
	</ul>
	
	<h3>Cookies</h3>
	<p>
		This site makes use of cookies.
		One cookie is set to expire when the browser closes, this is used only to check whether cookies are enabled.
		Another cookie set to expire after 30 days, for the main purpose of preventing double counting of fingerprints.
		Additionally a session cookie may be set when submitting a fingerprint, this is used to keep track of which CAPTCHA you were shown on the CAPTCHA page.
	</p>
	
	<h3>IP addresses</h3>
	<p>
		The salted hash of IP addresses is collected for most clients.
		For clients who are using Tor (and hence whose IP address is hidden) we instead store the entire IP address of the exit-node they used. 
	</p>
	
	<h3>Timestamps</h3>
	<p>
		The date and time that a fingerprint was taken is stored along with the fingerprint. 
	</p>
	
	<h2>Use of data</h2>
	<p>
		The purpose of this website is to compile a database of browser fingerprints from which statistics and other interesting data can be extracted.
		We will not track or attempt to track clients outside of this website.
	</p>
	
	<h3>Sharing of data</h3>
	<p>
		Fingerprints, aggregated data about fingerprints, and other statistics may be published and shared publicly (See: <a href="statistics">Statistics</a>).
		For non-Tor clients IP addresses will not be collected or published, however statistics that can be derived from IP address, such as the country of clients may be published.
		For Tor clients the IP address or identity of the Tor exit node being used may be published. 
		An effort will be made to protect the privacy of clients in situations where their privacy may be threatened.
	</p>
	<p>
		The database of collected data, may be shared with a third party upon request,
		 provided they have a valid reason (e.g. research) and they agree to abide by our privacy policy.
		Currently the database has not been shared with anybody.
		We have received no requests from law enforcement for the database or any other data.
		We will do our best to prevent theft or misuse of collected data, however, such incidents are still possible.
	</p>
	<p>
		Each fingerprint is assigned a unique UUID; this UUID, or a URL containing this UUID, may be shared by the client to allow others to see and compare their fingerprint.
		The UUIDs used are so long (122 random bits) that it is very unlikely they can be guessed.
		Hence it is very unlikely that a fingerprint will be viewed by others on the website without the client disclosing its UUID or URL.
	</p>
	
	<h2>Disclaimer</h2>
	<p>
		This privacy policy may change at any time, for instance, when a new test is added we will change the policy to reflect collection of results of the new test.
		We do not collect contact details of clients so it is impossible for us to notify you of changes.
	</p>
</div>
<%@include file="footer.jsp" %>
</body>
</html>