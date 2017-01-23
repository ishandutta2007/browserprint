<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="true"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint - Blog - Finding independent clusters of fingerprint features</title>
<link type="text/css" href="../style.css" rel="stylesheet">
<script type="text/x-mathjax-config">
  MathJax.Hub.Config({tex2jax: {inlineMath: [['$','$'], ['\\(','\\)']]}});
</script>
<script type="text/javascript" async
  src="https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS_CHTML">
</script>
</head>
<body style="width:1167px;margin:auto">
<common:header/>
<div id="content">
	<h2><a href="mutualInfoClustering">Finding independent clusters of fingerprint features</a></h2>
	<h4>Posted: 2017-01-24<br/>
	By <a href="mailto:${initParam['devEmail']}?subject=Blog%20-%20Mutual%20info%20clustering">Lachlan Kang</a></h4>
	<p>
	Today we'll examine how features of the fingerprint taken by Browserprint are related.
	Our goal is to see if there are any hidden relationships between features that we weren't aware of,
	 to see if we can find clusters of features that are more or less independent of each other, and perhaps see if there are any redundancies that we could remove.
	To this end we're going to do clustering on fingerprint features using the amount of information they share as the distance metric.
	We'll start by presenting the results, then we'll discuss how we got them.
	</p>
	<p>
	You can see the results of our clustering in <a href="#fig1">Figure 1</a>.
	</p>
	<div style="position:relative;left:-333.5px">
		<a name="fig1"></a>
		<img src="../images/blog/mutualInfoClustering.png" alt="A dendrogram showing the clustering of fingerprint features."/>
	</div>
	<p>
	<b>Figure 1.</b> A dendrogram showing the clustering of fingerprint features.
	The higher two clusters (or features) are connected, the higher the distance between them.
	The distance metric (discussed later) is essentially the reverse of how much you can learn about one cluster from the other: 1 - the clusters' normalised mutual information.
	The red line represents an arbitrary cutoff point for clusters we attempt to explain.
	Note: We only consider fingerprints with JavaScript enabled during clustering due to reasons discussed later.
	</p>
	<p>
	Below a distance of 0.5 (the red line) we see almost exclusively clusters of 2 features, with one cluster of 4 features.
	Most of these clusters obvious: touch screen details clustered together, four audio fingerprinting tests clustered together, two ad blocking tests clustered together,
	 whether the client is using Tor clustered with the version of the Tor Browser Bundle they're using,
	 and whether local storage (that can be used for supercookies) is enabled with whether session storage (that can also be used for supercookies) is enabled.
	</p>
	<p>
	Less obvious clusters are: the list of installed plugins with fonts detected using JavaScript;
	 this could be explained by plugins modifying the fonts that are in the browser;
	 client platform (operating system) detected using flash, and screen details collected using flash;
	 this could be explained by the fact that if flash is disabled or not installed they will have the same value always;
	 and finally timezone, and the format of the UNIX epoch formatted and localised in the browser's default date/time format using JavaScript;
	 this can be explained by the fact that the format of the epoch date/time string contains the timezone
	 (e.g. timezone = -570 minutes (-9.5 hours) and formatted epoch &quot;01/01/1970, 09:30:00&quot; (9.5 hours ahead of UTC)).
	</p>
	<p>
	The one cluster that defies explanation is user-agent string with HTML5 canvas.
	This could indicate some hidden relationship between the two properties.
	</p>
	<h3>Clustering methodology</h3>
	<p>
	To get the dendrogram shown in <a href="#fig1">Figure 1</a> we do hierarchical clustering of fingerprint features,
	 where the distance between two clusters of features is one minus their normalised mutual information.
	What this means is we start out with a set of all the fingerprint features (user-agent string, time-zone, system fonts, etc), each of which we treat as cluster containing one fingerprint feature;
	 we then calculate the distance between these single feature clusters (we will explain our distance metric shortly).
	We find the two clusters with the lowest distance and merge them.
	We repeat, calculating the distance between each cluster, finding the two clusters with the lowest distance, then merging them, and we keep repeating until there is only one cluster.
	This method of clustering is called hierarchical clustering, or more specifically agglomerative hierarchical clustering.
	</p>
	<p>
	During the first iteration we need to calculate the distance between each cluster (which are each just one fingerprint feature at this point) and every other cluster;
	 we save these values, and then in subsequent iterations we need only to calculate the distance from the cluster created in the previous iteration to every other cluster.
	This is because only that cluster changes, and the distance between unchanged clusters stays the same.
	</p>
	<p>
	For the distance between two clusters, \( X \) and \( Y \), we use 1 minus their normalised mutual information \( [ 1 - NMI(X;Y) ] \).
	The mutual information of two clusters is the amount of information they share, written as \( I(X;Y) \).
	</p>
	<p>
	For example, say \( X = \{ \texttt{Are-Facebook-share-buttons-blocked?} \} \) and \( Y = \{ \texttt{Are-Twitter-share-buttons-blocked?} \} \).
	</p>
	<p>
	Say there are 5 possible values for \( \texttt{Are-Facebook-share-buttons-blocked?} \);
	 \( \texttt{null} \), meaning scripts are disabled,
	 0 when Facebook share buttons are not blocked,
	 1 when they're blocked in the way that the Privacy Badger [<a href="#ref1">1</a>] browser extension blocks share buttons,
	 2 when they're blocked in the way that a script blocker blocks them<sup><a href="#footnote1">1</a></sup>,
	 and 3 when it's blocked in the way that Adblock Plus's [<a href="#ref2">2</a>] Anti-Social list blocks them.
	</p>
	<p>
	Say that there are 4 possible values for \( \texttt{Are-Twitter-share-buttons-blocked?} \);
	 \( \texttt{null} \), meaning scripts are disabled,
	 0 when Twitter share buttons aren't blocked,
	 1 when they're blocked in the way that Privacy Badger blocks them,
	 and 2 when they're blocked in the way that a script blocker blocks them.
	</p>
	<p>
	There's a high probability that if Facebook share buttons are being blocked Twitter share buttons are also being blocked, and probably in the same way;
	 this is because social media blocking extensions often don't give you the option of just blocking one or the other, and even if they do the default will usually be to block both.
	This means that the two clusters share information, knowing the value of one makes it easier to guess correctly what the value of the other will be;
	 therefore, they have mutual information.
	</p>
	<p>
	In the previous example the two clusters had a lot of mutual information relative to how much identifying information they contain, which isn't much.
	If we were to use just mutual information in our distance formula the clusters in the previous example probably wouldn't be merged for many iterations, despite being closely related.
	The reason for this is that the mutual information of two clusters cannot be more than their joint entropy (clusters can't share more information than they have).
	To remedy this we need to use normalised mutual information instead.
	</p>
	<p>
	The amount of identifying information that a cluster, \( X \), has is its Shannon entropy<sup><a href="#footnote2">2</a></sup>, written \( H(X) \).
	This value tells us how useful a fingerprint feature, or cluster of features, is for distinguishing one browser from another; the more bits of entropy the more useful it is for fingerprinting.
	If a cluster of fingerprint features can have a large number of different values, and it's difficult to guess what those values will be, then the cluster has a lot of entropy.
	<a href="#fig2">Figure 2</a> gives the formula for \( H(X) \).
	</p>
	<div style="text-align: left; padding: 1px 4px; border-radius: 2px; border: 1px solid #DDD; color: #000;">
	<p>
		<a name="fig2"></a>
		\begin{aligned}
			H(X) =& H(X_{1}, X_{2}, \dots, X_{n})	\\
			 =& -\displaystyle\sum_{x_1} \displaystyle\sum_{x_2} \dots \displaystyle\sum_{x_n} P(x_1, x_2, \dots, x_n) log_2[P(x_1, x_2, \dots, x_n)]	\\
		\end{aligned}
	</p>
	<p>
		Where \( X = \{ X_{1}, X_{2}, \dots, X_{n} \} \) is a cluster of fingerprint features \( X_{i} \),<br>
		where \( x_i\) is a possible value for fingerprint feature \( X_i \),<br>
		where \( P(x_1, x_2, \dots, x_n) \) is the probability of the values \( x_1, x_2, \dots, x_n \) occurring together,
		and where we define \( log_2[P(x_1, x_2, \dots, x_n)] = 0 \), when \( P(x_1, x_2, \dots, x_n) = 0 \).<br>
		Also note that \( H(X, Y) = H( X_{1}, X_{2}, \dots, X_{n},  Y_{1}, Y_{2}, \dots, Y_{m}) = H(X \cup Y) \).
	</p>
	</div>
	<p>
		<b>Figure 2.</b> Formula for entropy, \( H(X) \), of a cluster, \( X \), of fingerprint features, and joint entropy \( H(X,Y) \) of two clusters \( X \) and \( Y \).
	</p>
	<p>
	We do not know the exact values of \( P(x_1, x_2, \dots, x_n) \), that would require us to collect the fingerprint of every web browser on every computer in existence;
	 instead we estimate it using the samples that we collected during the study.
	To estimate \( P(x_1, x_2, \dots, x_n) \) we simply take the number of submitted fingerprints with values \( x_1, x_2, \dots, x_n \) and divide it by the total number of fingerprints that have been submitted.
	</p>
	<p>
	An additional consideration is that submitted fingerprints have a Browserprint version number.
	When we add a new test to Browserprint we increment the version number given to new fingerprints by 1.
	The bulk of our collected fingerprints have a version number of 19, so for most of our analysis we only consider fingerprints with a version number of 19 or higher,
	 and we only consider tests that were present in version 19 of Browserprint.
	</p>
	<p>
	To simplify things and speed up calculations we calculate mutual information in terms of cluster entropies, using the formula \( I(X,Y) = H(X) + H(Y) - H(X,Y) \);
	 in other words the mutual information of two clusters \( X \) and \( Y \) is equal to the entropy of cluster \( X \)
	 plus the entropy of cluster \( Y \) minus the joint entropy of the two clusters, \( H(X, Y) \).
	</p>
	<p>
	To normalise \( I(X;Y) \) we do as suggested by Vinh, Epps, and Bailey [<a href="#ref3">3</a>], and divide by \( H(X) \) or \( H(Y) \), whichever is larger (see <a href="#fig3">Figure 3</a>).
	Benefits of this are \( 0 \le NMI(X;Y) \le 1 \) and that it satisfies all the requirements to be considered a metric.
	</p>
	<div style="text-align: left; padding: 1px 4px; border-radius: 2px; border: 1px solid #DDD; color: #000;">
	<p>
		<a name="fig3"></a>
		\begin{aligned}
			NMI(X;Y) =& I(X;Y) \over max(H(X), H(Y)) \\
				 =& (H(X) + H(Y) - H(X,Y)) \over max(H(X), H(Y))
		\end{aligned}
	</p>
	</div>
	<p>
		<b>Figure 3.</b> Formula for normalised mutual information of two clusters, \( X \) and \( Y \).
	</p>
	<p>
	If we were to use normalised mutual information for our cluster distance measure it would mean that clusters with low normalised mutual information would be merged first;
	 we want the opposite, so for our distance measure we use 1 minus normalised mutual information, or \( 1 - NMI(X;Y) \).
	</p>
	<p>
	When we performed the clustering to improve results we added some constraints.
	Firstly we only considered fingerprints with a Browserprint version number of 19 or above; these fingerprints consist of the bulk of our collected fingerprints, and have all tests except is-HSTS-enabled?.
	This is just a boolean value so not much is lost by excluding it.
	Additionally we only considered fingerprints which had JavaScript enabled.
	The reason for this is that when JavaScript is disabled all JavaScript tests should have the value \( \texttt{null} \);
	 this will cause JavaScript tests to have an inflated amount of mutual information and skew the results.
	</p>
	<p>
	Additionally we excluded several fingerprint features from consideration when clustering due to their instability:
	 We didn't do clustering on fonts detected using CSS, this is because the test was unstable (a more stable, streamlined version is in the works), and, if successful should produce the exact same list as fonts detected using JavaScript.
	We also didn't do clustering on screen size detected using JavaScript and CSS, and the character sizes test, since the value of these varies with the browser zoom level, which may vary often,
	 and (in the Tor Browser Bundle) there is no easy way to get around this and get a more correct value<sup><a href="#footnote3">3</a></sup>.
	</p>
	<h3>Footnotes</h3>
		<ol>
			<li>
				<p>
					<a name="footnote1"></a>
					No, this doesn't mean the same as \( \texttt{null} \) would.
					In the case of \( \texttt{null} \) scripts are blocked for the website's domain.
					When it's 2 scripts are allowed on the website's domain but the third-party scripts embedded in the page to build the share button are blocked.
				</p>
			</li>
			<li>
				<p>
					<a name="footnote2"></a>
					From now on when we refer to entropy in this blog post we mean Shannon entropy.
				</p>
			</li>
			<li>
				<p>
					<a name="footnote3"></a>
					However, recent work by Cao, Li, and Wijmans [<a href="#ref4">4</a>] describes, among other things,
					 how we can turn screen dimensions into a useful feature that doesn't differ when zooming by instead taking the ratio between screen height and width.
					I have not confirmed this defeats zooming, but I intend to, and then add the test (and others) to Browserprint.
				</p>
			</li>
		</ol>
		<h3>References</h3>
		<ol>
			<li><a name="ref1"></a>EFF: Privacy Badger - Electronic Frontier Foundation. <a href="https://www.eff.org/privacybadger">https://www.eff.org/privacybadger</a> (2017). Accessed 2017-01-10</li>
			<li><a name="ref2"></a>Eyeo GmbH: Adblock Plus - Surf the web without annoying ads! <a href="https://adblockplus.org/">https://adblockplus.org/</a> (2017). Accessed 2017-01-10</li>
			<li><a name="ref3"></a>Vinh, N.X., Epps, J., Bailey, J.: Information Theoretic Measures for Clusterings Comparison: Variants, Properties, Normalization and Correction for Chance. Journal of Machine Learning Research 11(Oct), 2837-2854 (2010)</li>
			<li><a name="ref4"></a>Cao, Y., Li, S., Wijmans, E.: (Cross-)Browser Fingerprinting via OS and Hardware Level Features (2017)</a></li>
		</ol>
</div>
<common:footer/>
</body>
</html>