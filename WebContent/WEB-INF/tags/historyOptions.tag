<%@tag language="java" pageEncoding="UTF-8"%><%--
--%><%@tag body-content="empty"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@attribute name="historyListBean" required="true" type="beans.HistoryListBean" rtexprvalue="true"%><%--
--%><%@attribute name="sampleUUID" required="false" type="String" rtexprvalue="true"%><%--
	--%><c:choose><%--
		--%><c:when test="${ sampleUUID != null }"><%--
			--%><c:forEach var="history" items="${ historyListBean.history }"><%--
				--%><c:choose><%--
						--%><c:when test="${ sampleUUID == history.sampleUUID }">
						<option value="${ history.sampleUUID }" selected>${ history.timestamp }</option></c:when><%--
						--%><c:otherwise>
						<option value="${ history.sampleUUID }">${ history.timestamp }</option></c:otherwise><%--
				--%></c:choose><%--
			--%></c:forEach><%--
		--%></c:when><%--
		--%><c:otherwise>
						<option selected value="">-------Select sample-------</option><%--
			--%><c:forEach var="history" items="${ historyListBean.history }">
						<option value="${ history.sampleUUID }">${ history.timestamp }</option><%--
			--%></c:forEach><%--
		--%></c:otherwise><%--
	--%></c:choose>