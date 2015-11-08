<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><jsp:useBean id="historyListBean" class="beans.HistoryListBean" scope="request" /><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Browserprint</title>
	<meta name="robots" content="noindex, nofollow" >
	<link type="text/css" href="style.css" rel="stylesheet">
	<script type="text/javascript" src="scripts/jquery-1.11.2.min.js"></script>
	<script type="text/javascript" src="scripts/jquery-dateFormat.min.js"></script>
	<script type="text/javascript" src="scripts/view.js"></script>
</head>
<body>
<%@include file="header.html" %>
	<p>
		View past browser fingerprints.
	</p>
	<c:if test="${ !requestScope.cookiesEnabled }"><p class="error">
		To keep track of your history you need cookies enabled.
	</p></c:if>
	<div id="viewDiv">
		<form action="view" method="get">
			<table class="viewOptionsTable">
				<tr>
					<td><input type="radio" name="source1" value="history" checked></td>
					<td class="viewOptionsTd">
						History
						<br/>
						<select name="UUID1history" id="historySelector1" class="historySelect"><%--
							--%><common:historyOptions historyListBean="${ historyListBean }" />
						</select>
						<script type="text/javascript">
							fixDates("historySelector1");
						</script>
					</td>
				</tr>
				<tr>
					<td><input type="radio" name="source1" value="UUID"></td>
					<td class="viewOptionsTd">
						UUID
						<br/>
						<input type="text" name="UUID1UUID" maxlength="36" style="display: inline-block;"></input>
					</td>
				</tr>
				<tr>
					<td></td>
					<td class="viewOptionsButtonTd"><input type="submit" name="action" value="View" class="sampleViewButton"/></td>
				</tr>
			</table>
		</form>
	</div>
<%@include file="footer.jsp" %>
</body>
</html>