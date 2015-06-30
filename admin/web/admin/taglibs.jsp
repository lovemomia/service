<%@page import="cn.momia.admin.web.common.ConfigUtil"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
    String filepath = ConfigUtil.loadProperties().getProperty("serverPath");
%>
<c:set var="ctx" value="${pageContext.request.contextPath}"></c:set>
<c:set var="basePath" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/${ctx}"></c:set>
<c:set var="filepath" value="<%=filepath%>"></c:set>


