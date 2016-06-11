<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Browserprint - Blog</title>
<link type="text/css" href="../style.css" rel="stylesheet">
</head>
<body>
<common:header/>
<div id="content">
	<h2><a href="userFingerprinting">User fingerprinting via CAPTCHAs</a></h2>
	<h4>Posted: 2016-06-11<br/>
	By <a href="mailto:${initParam['devEmail']}?subject=User%20fingerprinting">Lachlan Kang</a></h4>
	<p>
		<b>Important note:</b> Almost all of this is currently theoretical and has not been implemented or trialled.
		We have <b>absolutely no</b> intention of adding these tests to the main fingerprinting suite of Browserprint.
		It's possible that in the future we may add some of these tests to the site as optional proof of concept tests for people to play around with completely separate from the main fingerprinting suite.
		In that case we probably won't be recording results and if we are we will make it very clear.
	</p>
	<p>
		The main reason for fingerprinting browsers is to track users.
		So why then do we focus on fingerprinting the computer and software of the user rather than the user themselves?
		If we can fingerprint the user instead of their browser we can track them even if they switch computers.
	</p>
	<p>
		One down side, and perhaps a good reason as to why user fingerprinting has been neglected is that it generally requires input from the user rather than just being able to call a JavaScript function to get the fingerprint data.
		For good reasons users may be reluctant to provide this input if they know they&#39;ll be fingerprinted with it; they may even provide false information if they want to throw off tracking.
		There&#39;s two simple ways of getting around this, collecting inputs covertly, and making input mandatory for use of the site.
	</p>
	<p><%-- COLLECTING COVERTLY --%>
		An example of collecting fingerprintable information covertly could be having a user scroll through a terms and conditions page to get a rough idea of how fast they scroll.
		Alternatively it could be observing them post a comment on the site and measuring how fast their typing speed is.
		The user would not be aware that you&#39;re collecting this information so they wouldn&#39;t be reluctant to provide the input.
		A downside to these two tests is that they&#39;re opportunistic and the inputs may not be provided every time a client visits the site.   
	</p>
	<p><%-- MANDATORY INPUT --%>
		An example of mandatory input could be to require users to input their name in order to use the site.
		This would of course result in many users providing fake names so you would need some sort of validation, for instance requiring their passport number as well.
		You&#39;re unlikely to know someone&#39;s passport number unless you&#39;re in possession of their passport, so it acts as a rough validation.
		A major problem with this is that many people wouldn&#39;t use a website that requires these inputs unless that website was very important to them.
	</p>
	<p><%-- FINGERPRINTING CAPTCHAS --%>
		One powerful way of getting a user to input information about themselves is with a CAPTCHA.
		A well designed fingerprinting CAPTCHA can be both covert and mandatory.
		Since CAPTCHAs are common and people are used to solving them they will not be reluctant to provide information through them, provided you&#39;re tactful.
		CAPTCHAs are very flexible and the user can be tricked into revealing all sorts of information through them: whether they have vision problems,
		 whether they have hearing problems, ability to solve puzzles, ability to play simple games, political or religious beliefs, or breadth of knowledge.
		Here we&#39;ll discuss a very simple CAPTCHA that can be used to detect colour vision problems, and then we'll discuss other potential tests.
	</p>
	<h3>Colour blindness test</h3>
	<p>
		To test the waters of user fingerprinting CAPTCHAs we created a simple proof of concept CAPTCHA that tests for colour blindness.
		For the prototype CAPTCHA we use plates from the Ishihara colour perception test.
		The user is shown 3 plates, circular arrangements of circles within which sets of coloured circles make up numbers (see Figure 1).
		The user is then expected to type the numbers that appear on the plates into the text box, leaving a blank space for plates that appear to have no numbers.
	</p>
	<p>
		The first plate shown is one of one of plates 2-7 from Ishihara&#39;s 1917 paper [2].
		These plates are used to test for red-green deficiencies, since someone with a red-green deficiency will see different numbers than someone with perfect colour vision.
		In left-most plate in Figure 1 is Ishihara plate number 4;
		 someone with normal colour vision will see 5, someone with a red-green deficiency will see 2. 
	</p>
	<p>
		The second plate shown is one of the plates 8-13.
		These are used to test for colour vision deficiencies in general, since the majority of people with colour deficiencies will see no number.
		Since they won&#39;t see a number a person with colour deficiencies won&#39;t type a number for this plate.
		The middle plate in Figure 1 is Ishihara plate number 13;
		 someone with normal vision will see 73, and someone with colour deficiencies won&#39;t see any number.
	</p>
	<p>
		The third plate shown is one of the plates 16-17;
		 people with protanopia will see different numbers on these plates to someone with deuteranomalia who will see different numbers to someone with perfect colour vision.
		The right-most plate in Figure 1 is Ishihara plate number 16;
		 someone with normal colour vision will see 26, someone with protanopia will see 6, and someone with deuteranomalia will see 2.
	</p>
	<img src="../images/blog/colour.vision.captcha.png" style="display: block; margin-left: auto; margin-right: auto"/>
	<p>
		<b>Fig. 1.</b> A screenshot of the first prototype.
		This implementation is not very covert, the plates are obviously from a colour vision test;
		 a more advanced implementation would attempt to hide this fact, perhaps by adding additional circles so that the overall circular shape of the plates is lost and it looks like one continuous bar. 
	</p>
	<p>
		<%-- NOT A STRONG CAPTCHA AS IS --%>
		Due to a low number of plates and the answers to the plates being freely available in Ishihara&#39;s paper [2] this CAPTCHA does not provide a strong guarantee that the user is human,
		 nor does it protect against a user who is determined to fool the system.
		A better implementation would involve generating new plates for each CAPTCHA presented to the user.
		Additionally a time limit should be imposed so that the client does not have time to save the images and adjust the colours to determine what the other answers are.
	</p>
	<p>
		<%-- TESTED ON REAL PERSON --%>
		Another concern is that in its current state colour blind people will recognise that plates as being from a colour blindness test and that may discourage from attempting to answer the CAPTCHA.
		This concern was raised when the colour blindness CAPTCHA was tested on a partially colour blind person.
		After they identified the test as a CAPTCHA and read the CAPTCHA instructions they stated &quot;we&#39;ll I suppose I&#39;ll just have to guess the answer&quot;.
		This was despite the fact that they said they were able to see numbers when probed further.
		To resolve this we need to make the fact that it&#39;s a colour blindness test less obvious;
		 one simple step towards this is to add additional coloured circles so that the plates are no longer circular and so that there is a smooth transition between the plates.
		With the iconic circular shape of the tests gone the fact that it&#39;s a colour blindness test should be far less obvious.
	</p>
	<p>
		<%-- DOESN'T WORK ON COLOUR BLIND PEOPLE --%>
		This test has one down side, it does not work on people who have full colour blindness.
		People with full colour blindness (achromatopsia) will see no numbers so they will be unable to input a correct answer for the CAPTCHA.
		Full colour blindness is said to affect 1 in 30,000 people [1].
		For these people you could provide an alternative, an audio CAPTCHA.
		Sadly we cannot use a user&#39;s choice of an audio CAPTCHA over a visual CAPTCHA as evidence that the user has visual problems,
		 since it could just be personal preference or them breaking from their routine of normally selecting a visual CAPTCHA.
		On the other hand you could potentially craft an audio CAPTCHA to fingerprint the user in a similar manner to the colour blindness CAPTCHA.
	</p>
	<h3>Other CAPTCHA ideas</h3>
	<p>
		As we said before there are many facets of a user that you can test with a CAPTCHA.
		Here we present ideas for other CAPTCHAs.
	</p>
	<p><%-- AUDIO CAPTCHA --%>
		Similarly to the colour blindness CAPTCHA you could test for hearing problems in users using an audio CAPTCHA that contains high frequency sounds.
		The user would listen to some audio and input what they hear.
		As people grow older they tend to lose their ability to hear higher frequencies, so older people wouldn&#39;t hear the higher frequency parts of the CAPTCHA and therefore wouldn&#39;t input them.
	</p>
	<p><%-- PUZZLE CAPTCHA --%>
		Another CAPTCHA could be you present the user with one or several short and simple puzzles, like the ones found on IQ tests, and measure how good they are at solving them.
		The easiest way of doing this would be to present a single puzzle and measure how long it takes the user to solve it.
		A difficulty with this is that you would either need a large set of similar puzzles of the same difficulty, or develop a way to generate them.
		Additionally if a user is exposed to many of the same type of puzzle their ability to solve them may increase, changing their fingerprint. 
	</p>
	<p><%-- BELIEF CAPTCHA --%>
		Another CAPTCHA could be designed to covertly probe a user for their beliefs on certain issues.
		For instance you could present a CAPTCHA where the user is tasked with selecting all items that they associate with safety;
		 you would have a set of obvious items that must always be selected in order to pass the CAPTCHA, such as a hard hat and knee pads,
		 but you would also include items such as guns or crucifixes that some people associate with safety and others don&#39;t.
		This test has a problem in that it can be difficult to design these tests in a way that is both effective and covert.
		Another difficulty is that it may not always produce the same results;
		 it&#39;s common that while solving an image selection CAPTCHA users overlook some images;
		 this could mean that they overlook the contentious items and as a result produce a different fingerprint.
	</p>
	<p><%-- KNOWLEDGE CAPTCHA --%>
		Similarly to the belief CAPTCHA you could design a CAPTCHA to test the knowledge of a user.
		For example you could present an image selection CAPTCHA that says &quot;select all items that were invented in China&quot;.
		This has a problem in that a user&#39;s level of knowledge may change, they may solve the CAPTCHA, get curious and look up the items, then next time answer differently because they now know more than they did before.
	</p>
	<ol>
		<li>Fran ̧cois, J.: Heredity in ophthalmology. Mosby (1961)</li>
		<li>Ishihara, S.: Test for colour-blindness. Tokyo: Hongo Harukicho (1917)</li>
	</ol>
</div>
<common:footer/>
</body>
</html>