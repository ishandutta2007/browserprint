<%@tag language="java" pageEncoding="UTF-8"%><%--
--%><%@tag body-content="empty"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%--
--%><%@attribute name="uniquenessBean" required="true" type="beans.UniquenessBean" rtexprvalue="true"%><%--
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
	The measurements we used to obtain this result are listed below.
</p>