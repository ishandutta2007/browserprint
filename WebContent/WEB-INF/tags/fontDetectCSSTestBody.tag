<%@tag language="java" pageEncoding="UTF-8"%><%--
--%><%@tag body-content="empty"%><%--
 hidden visibility and 0px font size because display: none prevents the fonts from being requested
--%><div style="visibility: hidden; font-size: 0px">
<%for(String font: util.Fonts.fonts){%>	<span style='font-family: "<%= font %>"'>a</span>
<% } %></div>