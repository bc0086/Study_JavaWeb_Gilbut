<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" 
    import="java.util.*"
    isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	request.setCharacterEncoding("UTF-8");
%>
<c:set var="contextPath" value="${pageContext.request.contextPath }" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
    p {
     font-size:20px;
      text-align:center;
    }
</style>
</head>
<body>
	<p> e-mail:admin@test.com</p> 
	<p> 회사주소:서울시 강동구</p>
	<p>찾아오는 길:<a href="#">약도</a></p>
</body>
</html>