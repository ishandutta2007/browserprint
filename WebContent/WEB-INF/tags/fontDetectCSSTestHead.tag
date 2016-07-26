<%@tag language="java" pageEncoding="UTF-8"%><%--
--%><%@tag body-content="empty"%><%--
--%><style type="text/css"><%
for(String font: util.Fonts.fonts){%>
@font-face{
	font-family: "<%= font %>";
	src: local("<%= font %>"), url(<%= response.encodeURL("CSSFont/" + font.replace(' ', '_')) %>);
}<% } %>
</style>