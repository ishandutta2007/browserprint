<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%-- These comments are to prevent excess whitespace in the output.
--%><%@page session="false"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="common" tagdir="/WEB-INF/tags"%><%--
--%><jsp:useBean id="historyListBean" class="beans.HistoryListBean" scope="request" /><%--
--%><jsp:useBean id="chrsBean1" class="beans.CharacteristicsBean" scope="request" /><%--
--%><jsp:useBean id="uniquenessBean1" class="beans.UniquenessBean" scope="request" /><%--
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
	<script type="text/javascript">
	$(function(){
		$("#sourceRadioHistory1").click(onClickHistory1);
		$("#sourceRadioUUID1").click(onClickUUID1);
		if($("#sourceRadioHistory1").is(":checked")){
			onClickHistory1();
		}
		else{
			onClickUUID1();
		}
		
		$("#sourceRadioHistory2").click(onClickHistory2);
		$("#sourceRadioUUID2").click(onClickUUID2);
		if($("#sourceRadioHistory2").is(":checked")){
			onClickHistory2();
		}
		else{
			onClickUUID2();
		}
	});
	</script>
</head>
<body>
<common:header/>
	<p>
		<a href="view">Back</a>
	</p>
	<div id="compareDiv">
		<form action="view" method="get">
			<table class="viewOptionsTable">
				<tr>
					<td>
						<c:choose><%--
							--%><c:when test="${ requestScope.source1 == "history" }"><%--
								--%><input type="radio" id="sourceRadioHistory1" name="source1" value="history" checked><%--
							--%></c:when><%--
							--%><c:otherwise><%--
								--%><input type="radio" id="sourceRadioHistory1" name="source1" value="history"><%--
							--%></c:otherwise><%--
						--%></c:choose>
					</td>
					<td class="viewOptionsTd">
						My history
						<br>
						<select name="UUID1history" id="historySelector1" class="historySelect">
							<c:choose><%--
								--%><c:when test="${ requestScope.source1 == "history" }"><%--
									--%><common:historyOptions historyListBean="${ historyListBean }" sampleUUID="${ requestScope.sampleUUID1 }" /><%--
								--%></c:when><%--
								--%><c:otherwise><%--
									--%><common:historyOptions historyListBean="${ historyListBean }" /><%--
								--%></c:otherwise><%--
							--%></c:choose>
						</select>
						<script type="text/javascript">
							fixDates("historySelector1");
						</script>
					</td>
					<td>
						<c:choose><%--
							--%><c:when test="${ !requestScope.compare || requestScope.source2 == "history" }"><%--
								--%><input type="radio" id="sourceRadioHistory2" name="source2" value="history" checked><%--
							--%></c:when><%--
							--%><c:otherwise><%--
								--%><input type="radio" id="sourceRadioHistory2" name="source2" value="history"><%--
							--%></c:otherwise><%--
						--%></c:choose>
					</td>
					<td class="viewOptionsTd">
						My history
						<br>
						<select name="UUID2history" id="historySelector2" class="historySelect">
							<c:choose><%--
								--%><c:when test="${ requestScope.compare && requestScope.source2 == "history" }"><%--
									--%><common:historyOptions historyListBean="${ historyListBean }" sampleUUID="${ requestScope.sampleUUID2 }" /><%--
								--%></c:when><%--
								--%><c:otherwise><%--
									--%><common:historyOptions historyListBean="${ historyListBean }" /><%--
								--%></c:otherwise><%--
							--%></c:choose>
						</select>
						<script type="text/javascript">
							fixDates("historySelector2");
						</script>
					</td>
				</tr>
				<tr>
					<td>
						<c:choose><%--
							--%><c:when test="${ requestScope.source1 == "UUID" }"><%--
								--%><input type="radio" id="sourceRadioUUID1" name="source1" value="UUID" checked><%--
							--%></c:when><%--
							--%><c:otherwise><%--
								--%><input type="radio" id="sourceRadioUUID1" name="source1" value="UUID"><%--
							--%></c:otherwise><%--
						--%></c:choose>
					</td>
					<td class="viewOptionsTd">
						UUID
						<br>
						<input type="text" id="UuidTextbox1" name="UUID1UUID" class="viewUuidTextbox" value="<c:out value="${ requestScope.sampleUUID1 }"/>">
					</td>
					<td>
						<c:choose><%--
							--%><c:when test="${ requestScope.compare && requestScope.source2 == "UUID" }"><%--
								--%><input type="radio" id="sourceRadioUUID2" name="source2" value="UUID" checked><%--
							--%></c:when><%--
							--%><c:otherwise><%--
								--%><input type="radio" id="sourceRadioUUID2" name="source2" value="UUID"><%--
							--%></c:otherwise><%--
						--%></c:choose>
					</td>
					<td class="viewOptionsTd">
						UUID
						<br>
						<c:choose><%--
							--%><c:when test="${ requestScope.compare }"><%--
								--%><input type="text" id="UuidTextbox2" name="UUID2UUID" class="viewUuidTextbox" value="<c:out value="${ requestScope.sampleUUID2 }"/>"><%--
							--%></c:when><%--
							--%><c:otherwise><%--
								--%><input type="text" id="UuidTextbox2" name="UUID2UUID" class="viewUuidTextbox"><%--
							--%></c:otherwise><%--
						--%></c:choose>
					</td>
				</tr>
				<tr>
					<td></td>
					<td class="viewOptionsButtonTd"><input type="submit" name="action" value="View" class="sampleViewButton"></td>
				</tr>
				<tr>
					<td></td>
					<td colspan="4" class="viewOptionsButtonTd">
						<input type="submit" name="action" value="Compare" class="sampleViewButton">
					</td>
				</tr>
			</table>
		</form>
	</div>
<c:choose>
	<c:when test="${ requestScope.compare }">
		<common:compareFingerprints uniquenessBean1="${ uniquenessBean1 }" chrsBean1="${ chrsBean1 }" uniquenessBean2="${ uniquenessBean2 }" chrsBean2="${ chrsBean2 }"/>
	</c:when>
	<c:otherwise>
		<common:uniquenessText uniquenessBean="${ uniquenessBean1 }"/>
		<common:displayFingerprint chrsBean="${ chrsBean1 }"/>
	</c:otherwise>
</c:choose>
<common:footer/>
</body>
</html>