<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint - Blog - Defences against fingerprinting</title>
<link type="text/css" href="../style.css" rel="stylesheet">
</head>
<body>
<common:header/>
<div id="content">
	<h2><a href="fingerprintingDefence">Defences against fingerprinting</a></h2>
	<h4>Posted: 2016-08-05<br/>
	By <a href="mailto:${initParam['devEmail']}?subject=Blog%20-%20Fingerprinting%20defence">Lachlan Kang</a></h4>
	<p>
		If you&#39;re on this website you probably have at least some idea of what browser fingerprinting is.
		Fingerprinting is a method of tracking you across the web that is much harder to defend against than previous techniques.
		Inter-domain tracking, the kind of tracking that involves following you between websites, is an invasion of your privacy;
		 typically tracking is done to build a profile of your browsing habits that can then be sold and used to serve you ads.
		If you would have a problem with showing a stranger your Internet history you should have a problem with tracking.
		In particular you should have a problem with browser-fingerprint based tracking, which we refer to more succinctly as fingerprinting, since it&#39;s so difficult to disable.
	</p>
	<p>
		There are several different ways to defend against fingerprinting, and each has their own positives and negatives.
		In this blog post we&#39;re going to discuss and compare each method.
	</p>
	<h3>Method 1: Share a fingerprint with others</h3>
	<p>
		The first method is probably the easiest, to give your browser a fingerprint that is not unique.
		The more people you share your fingerprint with the harder you are to track and the more you get lost within the crowd.
		Currently the most effective way of doing this is by using the Tor Browser Bundle (the TBB).
		The TBB is a fork of Firefox developed by the Tor Project and is their recommended way of browsing the Internet using Tor.
		The purpose of the Tor network is to anonymise your Internet traffic, and tracking technologies could potentially be used to deanonymise Tor users,
		 so it stands to reason that they try to defend against it<sup><a href="#footnote1">1</a></sup>.
		Tracking is an invasion of your privacy, and the Tor Project was created to help defend your privacy.
	</p>
	<p>
		Installing Tor is easy, it doesn&#39;t require any special configuration or setup.
		It does have its downsides however.
		For instance if you use the TBB you&#39;re forced to use Tor.
		While it has many benefits, it also has some properties that some people may potentially find frustrating.
		Most notably when using Tor pages load noticeably slower,
		 and Tor users are treated like second class citizens in the Internet, often being denied service by websites or shown very difficult CAPTCHAs to test if they&#39;re human before they can view a site [<a href="#ref1">1</a>].
	</p>
	<p>
		Other browsers exist that have fingerprinting defences, but they tend to only defend against the simplest of attacks.
		For instance IceCat, another Firefox fork, has several fingerprinting defences, such as spoofing HTTP headers to make it look like you&#39;re running a common version of Firefox on the most common operating system, Windows.
		These defences don&#39;t stack up to the TBB however, and aren&#39;t really good enough to defend against any but the simplest of fingerprinting techniques.
	</p>
	<p>
		Lastly fingerprinting attacks and defences are an arms race.
		For defences to be developed attacks first need to be discovered.
		An attack could be used in the wild for months or even years until a researcher discovers it and defences are created.
		For instance AudioContext fingerprinting was discovered in the wild [<a href="#ref2">2</a>] after having been active for who knows how long and defences are not yet widely available months later.
	</p>
	<h3>Method 2: Disable JavaScript universally</h3>
	<p>
		The second method of defending against fingerprinting is to disable JavaScript completely for all sites that you don&#39;t want to be tracked to.
		All scripts on sites you don&#39;t want to be tracked to must be disabled, any scripts that you allow could contain fingerprinting code.
		The majority of fingerprinting tests, and certainly the most powerful ones, require the use of JavaScript, hence disabling JavaScript will defeat most tests.
		The problem with this is that a large amount of sites on the Internet require JavaScript to function properly and simply won&#39;t work with it disabled.
		This makes the technique too restrictive for most users.
	</p>
	<p>
		Additionally it doesn&#39;t defeat all tests, and those few tests <i>may</i> be enough to uniquely identify you, we&#39;d like to find out whether that&#39;s the case as part of our research.
		In 2011 a study of 989 fingerprint samples found that a combination of fonts, the first two octets of IP address, timezone, and screen resolution, were enough to uniquely identify most users [<a href="#ref3">3</a>];
		 all of those, except perhaps timezone, can be obtained without JavaScript.
		In 2012 a study by Microsoft using datasets from Bing and Hotmail found that 60% - 70% of clients were accurately identifiable based on their user-agent string,
		 and that number went up to 80% when IP prefix information was also used [<a href="#ref4">4</a>].
		And in 2016 we have a study that found that only 29% of fingerprints were unique when JavaScript was disabled [<a href="#ref5">5</a>], but they didn&#39;t have the CSS screen size and font detection tests Browserprint has.
	</p>
	<p>
		A quick query of our database found that out of 2104 submitted fingerprints where JavaScript was disabled 1372 were unique, that means 65.2% of fingerprints with JavaScript disabled were still unique,
		 and that&#39;s without the scriptless tests that we&#39;re planning but haven&#39;t implemented yet such as scriptless ad-blocking checking and a scriptless version of the char sizes test.
		This just goes to show that disabling JavaScript is not a silver bullet, it should probably be combined with using a browser such as IceCat that has some fingerprinting defences.
		In summary we don&#39;t recommend this technique.
	</p>
	<h3>Method 3: Use multiple browsers</h3>
	<p>
		The third method is to use a different browser for different activities on the web.
		In its simplest form you partition your browsing habits between two browsers,
		 for instance one browser for things tied to your identity (LinkedIn, Facebook, Google+, banking, online shopping) and one another for general browsing.
		This means that while it&#39;s still possible for companies to track you and build a profile based on your browsing habits, the profile won&#39;t get tied to your identity, and the profile that is tied to your identity is minimised.
		This technique could be augmented using more browsers, using virtual machines, and by assigning each virtual machine a different VPN server.
	</p>
	<p>
		This approach does require a bit of discipline.
		When virtual machines are added to the mix this would likely become too troublesome for most people.
	</p>
	<p>
		What&#39;s more, cross-browser fingerprinting may be possible.
		That is, if two browsers are running on the same computer and operating system, it may be possible to tie sessions of one browser to another using only fingerprint data associated with operating system,
		 underlying hardware, and other browser independent data.
		At least one study has attempted to answer this question before.
		They found that a combination of fonts, the first two octets of your IP address, timezone,
		 and screen resolution were enough to uniquely identify most users [<a href="#ref3">3</a>];
		 these are features that tend to be consistent between multiple different browsers on the same machine.
		We plan to examine the question further.
		For instance in a previous blog post we explored the possibility of device independent fingerprinting using CAPTCHAs, <a href="userFingerprinting">user fingerprinting</a>.
	</p>
	<p>
		In summary I can neither argue for nor recommend against this technique, in its simplest form it may just act as a placebo and provide little benefit;
		 in its more advanced form it&#39;s likely very powerful, but still suffers from the fact it&#39;s unwieldy, and that you&#39;re only partitioning your profile, not completely defeating fingerprint-based tracking.
		If only it was possible to use a different combination of browser and operating system for every site you visited.
	</p>
	<h3>Method 4: Different fingerprints for different sites</h3>
	<p>
		The fourth method of defending against fingerprinting is where you spoof your fingerprint, providing a different one to each site you visit.
		The spoofing must be per site; there&#39;s no point for instance switching your user-agent string every 5 minutes<sup><a href="#footnote2">2</a></sup>.
		Per request is also acceptable, but it&#39;s trivial to detect, which makes it less desirable.
	</p>
	<p>
		There doesn&#39;t seem to exist a significant amount of software that provides the kind of per-domain or per-request spoofing we&#39;re looking for, but the few extensions we&#39;ve found we list here.
	</p>
	<p>
		The best software for fingerprint spoofing so far appears to be the Firefox extension <a href="https://github.com/christoftorres/FP-Block">FP-Block</a> [<a href="#ref6">6</a>].
		This randomises many fingerprint features per-site, including adding randomness to HTML canvases to foil canvas fingerprinting.
		Sadly it&#39;s not under active development and is a little bit buggy, but it&#39;s well worth playing around with.
	</p>
	<p>
		Another study developed software called PriVaricator that randomised the fingerprint features offsetHeight, plugins, and fonts [<a href="#ref7">7</a>].
		They found that this strategy worked against all tested fingerprinters, but sadly they do not seem to have released the extension publicly.
	</p>
	<p>
		Apart from that we have closed-source extensions <a href="https://addons.mozilla.org/en-us/firefox/addon/uacontrol/">UAControl</a> and <a href="https://www.dephormation.org.uk/?page=81">Secret Agent</a>.
		UAControl does user-agent string spoofing per domain, but you need to set the user-agent string manually rather than automatically generating mappings, meaning it&#39;s not particularly useful.
		Secret Agent randomises user-agent string and request headers per request.
	</p>
	<h3>Footnotes</h3>
	<ol>
		<li>
			<p>
				<a name="footnote1"></a>
				How could tracking deanonymise someone using Tor or a VPN?
				There&#39;s two scenarios;
			 	in the first scenario someone visits a site such as Gmail or Facebook that is tied to their identity, then they visit a second site that doesn&#39;t know their identity.
				If both websites have fingerprinting code and the websites collude, the first website could tell the second who you are, thus deanonymising you on the second site.
			</p>
			<p>
				In the second scenario you could visit a website with your IP address hidden by a proxy, then visit the site a second time without your IP address hidden.
				If the site uses fingerprinting they would be able to link the first visit of the site to the second, thus deanonymising your first visit.
			</p>
		</li>
		<li>
			<p>
				<a name="footnote2"></a>
				Imagine you switch your fingerprint at a regular (or irregular) interval.
				You visit one site where you have cookies enabled (or they&#39;re keeping track of your session with something else like a JSESSIONID),
	 			you switch your fingerprint, and then you visit the site again.
				The site can detect you&#39;ve changed your fingerprint trivially.
				All that needs to be done is for them to store your original fingerprint, notice the change, then overwrite the stored fingerprint with your new one, and tell all collaborating sites your fingerprint has changed.
			</p>
		</li>
	</ol>
	<h3>References</h3>
	<ol>
		<li><a name="ref1"></a>Khattak, S., Fifield, D., Afroz, S., Javed, M., Sundaresan, S., Paxson, V., Murdoch, S.J., McCoy, D.: Do You See What I See? Differential Treatment of Anonymous Users. In: Network and Distributed System Security Symposium (2016)</li>
		<li><a name="ref2"></a>Englehardt, S., Narayanan, A.: Online tracking: A 1-million-site measurement and analysis Draft: May 18, 2016</li>
		<li><a name="ref3"></a>Boda, K., Foldes, A.M., Gulyas, G.G., Imre, S.: User Tracking on the Web via Cross-Browser Fingerprinting. In: Nordic Conference on Secure IT Systems, pp. 31–46. Springer (2011)</li>
		<li><a name="ref4"></a>Yen, T.F., Xie, Y., Yu, F., Yu, R.P., Abadi, M.: Host Fingerprinting and Tracking on the Web: Privacy and Security Implications. In:NDSS (2012)</li>
		<li><a name="ref5"></a>Laperdrix, P., Rudametkin, W., Baudry, B.: Beauty and the Beast: Diverting modern web browsers to build unique browser fingerprints. In: 37th IEEE Symposium on Security and Privacy (S&P 2016) (2016)</li>
		<li><a name="ref6"></a>Torres, C.F., Jonker, H., Mauw, S.: FP-Block: usable web privacy by controlling browser fingerprinting. In: European Symposium on Research in Computer Security, pp. 3–19. Springer (2015)</li>
		<li><a name="ref7"></a>Nikiforakis, N., Joosen, W., Livshits, B.: PriVaricator: Deceiving Fingerprinters With Little White Lies. In: Proceedings of the 24th International Conference on World Wide Web, pp. 820–830. ACM (2015)</li>
	</ol>
</div>
<common:footer/>
</body>
</html>