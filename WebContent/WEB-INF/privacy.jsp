<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint - Privacy</title>
<link type="text/css" href="style.css" rel="stylesheet">
</head>
<body>
<common:header/>
<div id="content">
	<h2>What we will do with this data: ethical use and details of retention.</h2>
	<p>
		The fingerprint data collected will be mined for information such as:
		what browsers are most common,
		how many browsers are using Tor and
		what version, and what the most effective fingerprinting techniques are.
	</p>
	<p>
		We do this as part of ongoing research in the area of browser fingerprinting, as well as to provide statistics (such as those on the statistics page) to clients of the website.
		A paper(s) may be published in the future that discusses fingerprints collected by this website but such research will not provide user identification,
		 information that could be used to deanonymise Tor clients or tracking data that could be employed to track users based on data collected here.
	</p>
	<p>
		Data may be shared with other researchers upon their request but under the same conditions as for publication.
		If cookies are enabled we associate all the fingerprints for a user with those shared by the same cookie; the purpose of this is to prevent resubmission of a fingerprint from being counted and to provide the ability to look at and compare previous fingerprints easily. You may disable cookies if you do not wish this to occur.
		Fingerprints collected are publicly available on the website, but to view a fingerprint you need to know the designated UUID, which is <b>extremely</b> hard to guess but it is still feasible that anonymised data, as previously indicated, could be read from this site.
		However, if you don't share your fingerprint it is unlikely to be viewed by anybody except you through the website, although researchers can and will be accessing the fingerprint data as listed above.
	</p>
	<p>
		Currently the database has not been shared with anybody.
		We have received no requests from law enforcement for the database or any other data.
		We will do our best to prevent theft or misuse of collected data, however, such incidents are still possible.
	</p>
	<p>
		To reiterate: We will not attempt to deanonymise Tor clients nor will we attempt to track users across other websites using data gathered here.
	</p>
	<p>
		Thank you for participating in this project. We will place links to any published research here to allow users to confirm that we are following the usage policy.
	</p>

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
		<li>The screen size and colour depth of the monitor displaying the client's web browser, as detected using JavaScript, Flash, and CSS.</li>
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

	<h2>Disclaimer</h2>
	<p>
		This privacy policy may change at any time, for instance, when a new test is added we will change the policy to reflect collection of results of the new test.
		We do not collect contact details of clients so it is impossible for us to notify you of changes.
	</p>
</div>
<common:footer/>
</body>
</html>