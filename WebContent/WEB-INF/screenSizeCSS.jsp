<%@page language="java" contentType="text/css; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%>@CHARSET "UTF-8";
<% for(int i = 0; i < 5000; ++i){
%>@media (device-width: <%= i %>px) { #device-width { background-image: url("<%= response.encodeURL("screenSize/device-width." + i + "px")%>"); } }
@media (device-height: <%= i %>px) { #device-height { background-image: url("<%= response.encodeURL("screenSize/device-height." + i + "px")%>"); } }
<% } %>