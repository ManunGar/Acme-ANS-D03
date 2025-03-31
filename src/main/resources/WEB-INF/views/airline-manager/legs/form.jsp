<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		<acme:input-textarea code="manager.legs.list.label.departure" path="departure" />
		<acme:input-textarea code="manager.legs.list.label.arrival" path="arrival"/>
		<acme:input-select code="manager.legs.list.label.status" path="status" choices="${status}"/>
		<acme:input-select code="manager.legs.list.label.departure-airport" path="departureAirport" choices="${departureAirports}"/>
		<acme:input-select code="manager.legs.list.label.arrival-airport" path="arrivalAirport" choices="${arrivalAirports}"/>
		<acme:input-select code="manager.legs.list.label.aircraft" path="aircraft" choices="${aircrafts}"/>
		<acme:input-select code="manager.legs.list.label.flight" path="flight" choices="${flights}"/>
		<jstl:choose>
			<jstl:when test="${acme:anyOf(_command, 'show|update')}">
				<acme:input-checkbox code="manager.legs.form.label.confirmation.update" path="confirmation"/>
				<acme:submit code="manager.legs.form.button.update" action="/airline-manager/legs/update"/>
			</jstl:when>
			<jstl:when test="${_command == 'create'}">
				<acme:input-checkbox code="manager.legs.form.label.confirmation.create" path="confirmation"/>	
				<acme:submit code="manager.legs.form.button.create" action="/airline-manager/legs/create"/>
			</jstl:when>		
	</jstl:choose>
</acme:form>