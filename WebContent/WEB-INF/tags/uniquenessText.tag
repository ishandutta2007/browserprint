<%@tag language="java" pageEncoding="UTF-8"%><%--
--%><%@tag body-content="empty"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%--
--%><%@attribute name="uniquenessBean" required="true" type="beans.UniquenessBean" rtexprvalue="true"%><%---
--%><%@attribute name="predictionBean" required="true" type="beans.PredictionBean" rtexprvalue="true"%><%--
--%><p><c:choose><c:when test='${ uniquenessBean.isUnique }'>
	Your browser fingerprint <strong>appears to be unique</strong> among the <fmt:formatNumber value="${ uniquenessBean.inX }" maxFractionDigits="0"/> tested so far.
</c:when><c:otherwise>
	Within our dataset of ${ uniquenessBean.num_samples } visitors, your fingerprint has <strong>appeared ${ uniquenessBean.num_occurrences } times.</strong>
</p>
<p>
	<strong>One in <fmt:formatNumber value="${ uniquenessBean.inX }" maxFractionDigits="0"/> browsers have the same fingerprint as yours.</strong>
</c:otherwise></c:choose></p>
<p>
	Currently, we estimate that your browser has a fingerprint that conveys <strong><fmt:formatNumber value="${ uniquenessBean.bits }" maxFractionDigits="2"/> bits of identifying information.</strong>
</p>
<p>
	Your user-agent string specifies your browser as being a variant of <b>${ predictionBean.useragentSpecifiedBrowser }</b>.<br>
	Judging by your fingerprint we believe your browser is a variant of <b>${ predictionBean.browserPrediction }</b>.<br>
	Your user-agent string specifies your operating system as being a variant of <b>${ predictionBean.useragentSpecifiedOs }</b>.<br>
	Judging by your fingerprint we believe your operating system is a variant of <b>${ predictionBean.osPrediction }</b>.<br>
	Note: This is experimental so correct results are not guaranteed, especially when JavaScript is disabled.
</p>
<p>
	The measurements we used to obtain this result are listed below.
</p>