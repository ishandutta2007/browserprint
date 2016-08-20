<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="true"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
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
	<%-- This image is part of the HSTS test. --%>
	<p>
		Please type the letters from the image into the box below.
	</p><%--
	--%><c:if test="${ requestScope.error != null }">
	<p class="error">
		<c:out value="${ requestScope.error }"></c:out>
	</p>
	</c:if>
	<div>
		<img id="captchaImg" src="https://${ initParam['websiteBaseURL'] }/<c:url value="captcha.png"/>" alt="A text CAPTCHA"/>
		<br>
		<script type="text/javascript">
		function refresh(){
			$("#captchaImg").attr("src", "https://${ initParam['websiteBaseURL'] }/<c:url value="captcha.png"/>?z=" + new Date().getTime());
		}
		</script>
		<a href="#" onclick="refresh()">refresh</a>
		<form action="<c:url value="test"/>" method="POST">
			<div id="captchaDiv">
				<script type="text/javascript">
				$('<input>').attr({
					type: 'hidden',
					name: 'js_enabled',
					value: 'true'
				}).appendTo('#captchaDiv');
				</script>
				<input type="text" name="captchaAnswer">
				<br>
				<br>
				<input type="submit" value="Continue">
			</div>
			<p>
				<b>Optional info that can help us with our research</b>
			</p>
			<div id="questionnaire">
				<table>
					<tr>
						<td>I'm using a VPN/Proxy/Tor:</td>
						<td>
							<select name="usingProxy"><%--
								--%><c:choose><%--
										--%><c:when test="${ sessionScope.usingProxy == null }">
										<option value="" selected>Prefer not to say</option></c:when><%--
										--%><c:otherwise>
										<option value="">Prefer not to say</option></c:otherwise><%--
								--%></c:choose><%--
								--%><c:choose><%--
										--%><c:when test="${ sessionScope.usingProxy == "1" }">
										<option value="1" selected>Yes</option></c:when><%--
										--%><c:otherwise>
										<option value="1">Yes</option></c:otherwise><%--
								--%></c:choose><%--
								--%><c:choose><%--
										--%><c:when test="${ sessionScope.usingProxy == "0" }">
										<option value="0" selected>No</option></c:when><%--
										--%><c:otherwise>
										<option value="0">No</option></c:otherwise><%--
								--%></c:choose>
							</select>
						</td>
					</tr>
					<tr>
						<td>My browser is spoofing part of its fingerprint (e.g. user-agent string):</td>
						<td>
							<select name="isSpoofing"><%--
								--%><c:choose><%--
										--%><c:when test="${ sessionScope.isSpoofing == null }">
										<option value="" selected>Prefer not to say</option></c:when><%--
										--%><c:otherwise>
										<option value="">Prefer not to say</option></c:otherwise><%--
								--%></c:choose><%--
								--%><c:choose><%--
										--%><c:when test="${ sessionScope.isSpoofing == "1" }">
										<option value="1" selected>Yes</option></c:when><%--
										--%><c:otherwise>
										<option value="1">Yes</option></c:otherwise><%--
								--%></c:choose><%--
								--%><c:choose><%--
										--%><c:when test="${ sessionScope.isSpoofing == "0" }">
										<option value="0" selected>No</option></c:when><%--
										--%><c:otherwise>
										<option value="0">No</option></c:otherwise><%--
								--%></c:choose>
							</select>
						</td>
					</tr>
					<tr>
						<td>My web browser is:</td>
						<td>
							<select name="whatBrowser"><%--
							--%><%{	String browsers[][] = {
										{"", "Prefer not to say"},
										{"Firefox", "Firefox"},
										{"Chrome", "Chrome"},
										{"Opera", "Opera"},
										{"Safari", "Safari"},
										{"Edge", "Edge"},
										{"IE", "IE"},
										{"Other", "Other"}
									};
									String storedVal = (String)session.getAttribute("whatBrowser");
									for(String browser[]: browsers){
										if(browser[0].equals(storedVal)){%>
								<option value="<%= browser[0] %>" selected><%= browser[1] %></option><%
										}
										else{%>
								<option value="<%= browser[0] %>"><%= browser[1] %></option><%
										}
									}
							}%>
							</select>
						</td>
					</tr>
					<tr>
						<td>My operating system is:</td>
						<td>
							<select name="whatOS"><%--
							--%><%{	String oses[][] = {
										{"", "Prefer not to say"},
										{"Windows", "Windows"},
										{"Linux", "Linux"},
										{"Mac OS", "Mac OS"},
										{"Android", "Android"},
										{"IOS", "IOS"},
										{"BSD", "BSD"},
										{"TempleOS", "TempleOS"},//You never know, Terry might evade the CIA long enough to visit browserprint.
										{"Other", "Other"}
									};
									String storedVal = (String)session.getAttribute("whatOS");
									for(String os[]: oses){
										if(os[0].equals(storedVal)){%>
								<option value="<%= os[0] %>" selected><%= os[1] %></option><%
										}
										else{%>
								<option value="<%= os[0] %>"><%= os[1] %></option><%
										}
									}
							}%>
							</select>
						</td>
					</tr>
				</table>
			</div>
		</form>
	</div>
	<div id="device-width"></div>
	<div id="device-height"></div>
	<img src="http://${ initParam['websiteBaseURL'] }/<c:url value="hstsTest.png"/>" alt="A single pixel. Part of a test."/>
</body>
</html>