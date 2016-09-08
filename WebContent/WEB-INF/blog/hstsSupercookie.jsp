<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="true"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint - Blog - Revisiting HSTS supercookies</title>
<link type="text/css" href="../style.css" rel="stylesheet">
</head>
<body style="width:1200px;margin:auto">
<common:header/>
<div id="content">
	<h2><a href="hstsSupercookie">Revisiting HSTS supercookies</a></h2>
	<h4>Posted: 2016-09-08<br/>
	By <a href="mailto:${initParam['devEmail']}?subject=Blog%20-%20HSTS%20supercookies">Lachlan Kang</a></h4>
	<p>
		Supercookies are a class of techniques for storing data in your browser that tends to be difficult to remove and can be used to track you.
		Recently we've been exploring HSTS based supercookies after an email tipped us off to their existence.
		Our interest in them stems from how they can be used to track and potentially deanonymise users of VPNs or Tor.
		HSTS is a browser feature that allows websites to specify that future visits to a domain should only ever be via HTTPS, never HTTP.
		This behaviour can be used to store a single bit of data per domain, whether HSTS was enabled for the domain or not.
		We are not the first to abuse HSTS to store supercookies,
		 this type of supercookie was first <a href="http://www.leviathansecurity.com/blog/the-double-edged-sword-of-hsts-persistence-and-privacy/">described in 2012</a>,
		 and was rediscovered and implemented <a href="https://www.radicalresearch.co.uk/lab/hstssupercookies">in 2015</a>.
		However our method is an improvement on other's techniques since it works without JavaScript or cookies.
	</p>
	<p>
		<a href="#fig1">Figure 1</a> is a HSTS supercookie demonstration.
		It assigns your browser a random ID (32 bit integer) and then stores it in a supercookie, extracts the ID from the supercookie, then outputs the ID as a set of images.
		This will stay the same between visits even if you close your browser or have cookies disabled.
	</p>
	<div id="hstsID">
		<a name="fig1"></a>
		<img src="http://hsts0.${ initParam['websiteBaseURL'] }/<c:url value="hstsSuperCookie/start/1"/>" alt="Supercookie digit 1"/>
		<img src="http://hsts0.${ initParam['websiteBaseURL'] }/<c:url value="hstsSuperCookie/start/2"/>" alt="Supercookie digit 2"/>
		<img src="http://hsts0.${ initParam['websiteBaseURL'] }/<c:url value="hstsSuperCookie/start/3"/>" alt="Supercookie digit 3"/>
		<img src="http://hsts0.${ initParam['websiteBaseURL'] }/<c:url value="hstsSuperCookie/start/4"/>" alt="Supercookie digit 4"/>
		<img src="http://hsts0.${ initParam['websiteBaseURL'] }/<c:url value="hstsSuperCookie/start/5"/>" alt="Supercookie digit 5"/>
		<img src="http://hsts0.${ initParam['websiteBaseURL'] }/<c:url value="hstsSuperCookie/start/6"/>" alt="Supercookie digit 6"/>
		<img src="http://hsts0.${ initParam['websiteBaseURL'] }/<c:url value="hstsSuperCookie/start/7"/>" alt="Supercookie digit 7"/>
		<img src="http://hsts0.${ initParam['websiteBaseURL'] }/<c:url value="hstsSuperCookie/start/8"/>" alt="Supercookie digit 8"/>
	</div>
	<p>
		<b>Figure 1.</b> The randomly assigned ID we stored in a HSTS supercookie on your browser.
		This should stay the same if you refresh the page or close your browser, even if cookies are disabled.
	</p>  
	<p>
		Note: if you've visited Browserprint before this test was implemented it probably won't work (the ID will be all f's) until you clear the supercookie.
		To do this in Firefox and IceCat clear your "Site Preferences".
		In Opera the supercookie is cleared when you check "Delete password protected pages and data" and clear history.
		In Chrome the supercookie is cleared when cookies are cleared, which effectively defeats the attack.
	</p>
	<p>
		The easiest way we know of to defend against this is to block mixed content.
		To do that in Firefox and IceCat go to <code>about:config</code> and set <code>security.mixed_content.block_display_content = true</code>.
	</p>
	<p>
		HSTS is fairly simple.
		When the client visits a HSTS using website via HTTPS the site sends them a HTTP header that tells them to only connect to the website's domain using HTTPS, never HTTP.
		An example of this header is shown in <a href="#fig2">Figure 2</a>.
		If the client then tries to visit the domain using HTTP the web browser will attempt to upgrade the connection to HTTPS,
		 and if it fails will refuse to connect and display an error message.
	</p>
	<p>
		<a name="fig2"></a>
		<div style="text-align: center; padding: 1px 4px; border-radius: 2px; border: 1px solid #DDD; color: #000;">
			<code>Strict-Transport-Security: max-age=31536000</code>
		</div>
	</p>
	<p>
		<b>Figure 2.</b> The HTTP header used to enable HSTS.
	</p>
	<p>
		We can take advantage of this behavior to store single bit of information.
		If we embed a HTTP link to a domain (perhaps in <code>&lt;img&gt;</code> tags) and the client contacts the domain using HTTP we know HSTS is not enabled, so that's a bit value of 0;
		 if the client contacts the domain using HTTPS we know HSTS was enabled, so that's a bit value of 1. 
		By using more domains we can store more bits, one bit per domain or subdomain.
	</p>
	<p>
		Using these bits it's possible to implement what's called zombie cookies, cookies that may be resurrected after being deleted.
		For instance, imagine that when the user visits a site they're assigned a session ID that is stored in a cookie,
		 and that data about the user and their browsing habits is stored on the web server using that session ID as an identifier.
		By storing that session ID on the client using a super cookie we can restore it if the client ever clears their cookies, ensuring that tracking is not disrupted.
		The benefit of using a zombie cookie session ID instead of an exclusively supercookie one is that it removes significant complexity and pages load faster.
	</p>
	<p>
		Our method of storing a HSTS supercookie does not require JavaScript nor does it require cookies (client session ID can be encoded in the URIs);
		 this makes it a little more complex but makes it useful against many more clients.
		We'll start by demonstrating a simplified version of the supercookie where session IDs are only a single bit long.
		This method requires at least 2 domains, one to store the ID bit
		 and another to act as a guard that decides whether this is a new client that needs to be assigned an ID or an existing client that we want to extract an ID from.
		For our explanation we'll use the domain names <code>hasIdBeenStored.example.com</code> and <code>idBit1.example.com</code>. 
	</p>
	<p>
		<a href="#fig3">Figure 3</a> shows the basic process behind storing a single bit long JSESSIONID in a supercookie and then retrieving it.
		If the client already has a supercookie the green steps are skipped as they are associated with setting the supercookie.
		If the client deletes the cookie containing their JSESSIONID and revisits the page their old JSESSIONID will be restored.
	</p>
	<div style="position:relative;left:-350px">
		<a name="fig3"></a>
		<img src="../images/blog/hsts.supercookie.single.png" alt="A diagram showing the process of storing and retrieving a HSTS supercookie."/>
	</div>
	<p>
		<b>Figure 3.</b> A diagram showing the storing and retrieving of a single bit supercookie.
		This can be extended to store and retrieve multi-bit supercookies by chaining together multiple bit storages and retrievals,
		 or by having multiple parallel instances of this pattern that share information. 
	</p>
	<p>
		In real life JSESSIONIDs are much longer than 1 bit so we would need the supercookie to consist of multiple bits.
		The process can be extended to store and retrieve multiple bit supercookies by chaining together multiple bit storages and retrievals,
		 or by having multiple parallel instances of this pattern that share information among themselves by way of the client's session.
		If the client has cookies disabled we can still easily give them a session, we simply encode their session ID in the &lt;img&gt; tag URIs instead of using cookies.
		To store N bits we need N + 1 domains, 1 domain to be the guard (hstIdBeenStored.example.com) and the others to store bits of the supercookie.
	</p>
	<p>
		If the method you choose to extend the process is chaining then you would simply have each store and extract redirect to the next until you got to the desired number of bits.
		For instance a chain of 2 bits (JSESSIONID = 10) would look like:
	</p>
	<div style="text-align: left; padding: 1px 4px; border-radius: 2px; border: 1px solid #DDD; color: #000;">
		<code>http://hasIdBeenStored.example.com</code><br>
			redirects to ➡ <code>https://idBit1.example.com/storeBit?bitsToStore=10</code><br>
			redirects to ➡ <code>https://idBit2.example.com/storeBit?bitsToStore=0</code><br>
			redirects to ➡ <code>https://hasIdBeenStored.example.com</code><br>
			redirects to ➡ <code>http://idBit1.example.com/extractBit</code><br>
			redirects to ➡ <code>http://idBit2.example.com/extractBit?prevBits=1</code><br>
	</div>
	<p>
		With the first storage page removing its bit from the <code>bitsToStore</code> parameter, leaving just the second ID bit.
		And with the first extract page giving the value of its bit to the second page by encoding it within the URI of the next second page,
		 or alternatively temporarily within the client's session.
	</p>
	<p>
		If you instead choose to extend it using multiple chains of redirects the guard page wouldn't enable HSTS until ever bit had been stored,
		 which it can learn by each <code>/storeBit</code> page saving a flag value in the client's session.
		Additionally each extract page could store the extracted bit in the client's session, with the last extract then combining them and setting the cookie. 
	</p>
	<p>
		It should be noted that if you attempt to chain together multiple storages and retrievals it will take longer and you may use so many redirects that the browser stops following them,
		 which is what happened with the first version of our supercookie example:
		 we wanted to assign each visitor a random 32 bit identifier and then store it and display it for them using a single chain of redirects;
		 that meant we needed 65 redirects for the full storage and retrieval;
		 the browser stopped following them at 20.
		For this reason in our implementation we spread the bits across 8 chains of 4 bits length;
		 this meant that each chain would consist of only 9 redirects;
		 it's also a convenient number since the bits of each chain can then be represented as a single hex digit. 
	</p>
</div>
<common:footer/>
</body>
</html>