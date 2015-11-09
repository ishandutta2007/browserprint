<%@tag language="java" pageEncoding="UTF-8"%><%--
--%><%@tag body-content="empty"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><%@attribute name="chrsBean" required="true" type="beans.CharacteristicsBean" rtexprvalue="true"%><%--
--%><%@attribute name="uniquenessBean" required="true" type="beans.UniquenessBean" rtexprvalue="true"%><%--
--%><common:uniquenessText uniquenessBean="${ uniquenessBean }"/>
<table id="characteristics">
	<tr>
		<th>Browser Characteristic</th>
<%--		<th>bits of identifying information</th>
--%>	<th>Number of occurrences</th>
		<th>one in <i>x</i> browsers have this value</th>
		<th>value</th>
	</tr>
	<c:forEach var="chr" items="${ chrsBean.characteristics }"><tr>
		<td class="hovertext" title="${ chr.nameHoverText }">${ chr.name }</td>
<%--		<td><fmt:formatNumber value="${ chr.bits }" maxFractionDigits="2"/></td>
--%>		<td>${ chr.numOccurrences }</td>
		<td><fmt:formatNumber value="${ chr.inX }" maxFractionDigits="2"/></td>
		<td class="tableValue">${ chr.value }</td>
	</tr></c:forEach>
</table>