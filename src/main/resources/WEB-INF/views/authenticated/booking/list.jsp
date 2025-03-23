<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="authenticated.customer.list.label.locatorCode" path="locatorCode" width="10%"/>
	<acme:list-column code="authenticated.customer.list.label.purchaseMoment" path="purchaseMoment" width="20%"/>
	<acme:list-column code="authenticated.customer.list.label.travelClass" path="travelClass" width="20%"/>
	<acme:list-column code="authenticated.customer.list.label.lastnibble" path="lastNibble" width="20%"/>
	<acme:list-column code="authenticated.customer.list.label.price" path="price" width="10%"/>
	<acme:list-column code="authenticated.customer.list.label.passenger" path="passengers" width="20%"/>
</acme:list>

<acme:button code="authenticated.customer.create" action="/employer/job/create"/>