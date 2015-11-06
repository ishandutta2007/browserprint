<%@ tag language="java" pageEncoding="UTF-8"%><%--
--%><%@tag body-content="empty"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><%@attribute name="chrsBean1" required="true" type="beans.CharacteristicsBean" rtexprvalue="true"%><%--
--%><%@attribute name="uniquenessBean1" required="true" type="beans.UniquenessBean" rtexprvalue="true"%><%--
--%><%@attribute name="chrsBean2" required="true" type="beans.CharacteristicsBean" rtexprvalue="true"%><%--
--%><%@attribute name="uniquenessBean2" required="true" type="beans.UniquenessBean" rtexprvalue="true"%><%--
--%><div style="float:left">
<common:uniquenessText uniquenessBean="${ uniquenessBean1 }"/>
</div>
<div style="float:right">
<common:uniquenessText uniquenessBean="${ uniquenessBean2 }"/>
</div>
<table id="comparison">
	<tr>
		<th>Browser Characteristic</th>
		<th>one in <i>x</i> browsers have this value</th>
		<th>Fingerprint 1 value</th>
		<th>Fingerprint 2 value</th>
		<th>one in <i>x</i> browsers have this value</th>
	</tr><%
	java.util.Iterator<beans.CharacteristicBean> it1 = chrsBean1.getCharacteristics().iterator();
	java.util.Iterator<beans.CharacteristicBean> it2 = chrsBean2.getCharacteristics().iterator();
	while(it1.hasNext()){
		beans.CharacteristicBean chrBean1 = it1.next();
		beans.CharacteristicBean chrBean2 = it2.next();
	%>
	<tr>
		<td class="hovertext" title="<%= chrBean1.getNameHoverText() %>"><%= chrBean1.getName() %></td>
		<td><fmt:formatNumber value="<%= chrBean1.getInX() %>" maxFractionDigits="2"/></td>
		<td class="compareValue"><%= chrBean1.getValue() %></td>
		<td class="compareValue"><%= chrBean2.getValue() %></td>
		<td><fmt:formatNumber value="<%= chrBean2.getInX() %>" maxFractionDigits="2"/></td>
	</tr><% } %>
</table>