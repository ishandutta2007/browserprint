<%@page language="java" contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><?xml version="1.0" encoding="utf-8"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
	<channel>
		<atom:link href="https://browserprint.info/blog/rss.xml" rel="self" type="application/rss+xml" />
		<title>Browserprint Blog</title>
		<link>https://browserprint.info/</link>
		<description>Blog posts from Browserprint.</description>
		<item>
			<title>Defences against fingerprinting</title>
			<link>${ initParam['websiteBaseURL'] }/blog/fingerprintingDefence</link>
			<guid>${ initParam['websiteBaseURL'] }/blog/fingerprintingDefence</guid>
			<pubDate>Mon, 4 Aug 2016 00:00:00 GMT</pubDate>
			<description>A blog post discussing fingerprinting defences.</description>
		</item>
		<item>
			<title>User fingerprinting via CAPTCHAs</title>
			<link>${ initParam['websiteBaseURL'] }/blog/userFingerprinting</link>
			<guid>${ initParam['websiteBaseURL'] }/blog/userFingerprinting</guid>
			<pubDate>Mon, 27 Jun 2016 00:00:00 GMT</pubDate>
			<description>A blog post discussing user fingerprinting via CAPTCHAs.</description>
		</item>
	</channel>
</rss>