<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><jsp:useBean id="historyListBean" class="beans.HistoryListBean" scope="request" /><%--
--%><jsp:useBean id="chrsBean" class="beans.CharacteristicsBean" scope="request" /><%--
--%><jsp:useBean id="uniquenessBean" class="beans.UniquenessBean" scope="request" /><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Browserprint</title>
	<link type="text/css" href="style.css" rel="stylesheet">
	<script type="text/javascript" src="scripts/jquery-1.11.2.min.js"></script>
	<script type="text/javascript" src="scripts/jquery-dateFormat.min.js"></script>
	<script type="text/javascript" src="scripts/view.js"></script>
</head>
<body>
<%@include file="header.html" %>
	<p>
		<a href="view">Back</a>
	</p>
	<div id="compareDiv">
		<form action="view" method="get">
			<div style="float:left;">
				<div class="sampleBox">
					History
					<br/>
					<select name="sampleUUID1" id="historySelector1" class="historySelect"><%--
						--%><c:forEach var="history" items="${ historyListBean.history }"><%--
							--%><c:choose><%--
								--%><c:when test="${ param.sampleUUID1 == history.sampleUUID }">
						<option value="${ history.sampleUUID }" selected>${ history.timestamp }</option></c:when><%--
								--%><c:otherwise>
						<option value="${ history.sampleUUID }">${ history.timestamp }</option></c:otherwise><%--
							--%></c:choose><%--
						--%></c:forEach>
					</select>
					<script type="text/javascript">
						fixDates("historySelector1");
					</script>
				</div>
				<div>
					<input type="submit" name="action" value="View" id="sampleViewButton2"/>
				</div>
			</div>
			<div style="float:right;">
				<div class="sampleBox">
					History
					<br/>
					<select name="sampleUUID2" id="historySelector2" class="historySelect">
						<option selected value="">-------Select sample-------</option><%--
						--%><c:forEach var="history" items="${ historyListBean.history }">
						<option value="${ history.sampleUUID }">${ history.timestamp }</option><%--
						--%></c:forEach>
					</select>
					<script type="text/javascript">
						fixDates("historySelector2");
					</script>
				</div>
			</div>
			<div style="clear:left;">
				<input type="submit" name="action" value="Compare" id="sampleCompareButton"/>
			</div>
		</form>
	</div>
<common:displayFingerprint uniquenessBean="${ uniquenessBean }" chrsBean="${ chrsBean }"/>
<%@include file="footer.jsp" %>
</body>
</html>