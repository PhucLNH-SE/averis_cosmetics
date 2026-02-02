<%-- 
    Document   : newjsp
    Created on : Jan 28, 2026, 4:42:18?PM
    Author     : lengu
--%>
<%@ include file="/assets/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<a></a>
<c:forEach var="p" items="${products}">
  <div>
    <img src="${p.mainImage}" alt="${p.name}" width="120"/>
    <div>${p.name}</div>
    <div>${p.brand.name} - ${p.category.name}</div>
  </div>
</c:forEach>
<p>helolo </p>
<%@ include file="/assets/footer.jsp" %>