<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-select code="technician.maintenance-record.form.label.aircraft" path="aircraft" choices= "${aircrafts}" readonly="${_command != 'create'}"/>
	<jstl:choose>
		<jstl:when test="${_command != 'create'}">
			<acme:input-textbox code="technician.maintenance-record.form.label.technician" path="technician.identity.name" readonly="true"/>
		</jstl:when>		
	</jstl:choose>	
	<acme:input-moment code="technician.maintenance-record.form.label.maintenanceDate" path="maintenanceMoment" readonly="true"/>
	<acme:input-select code="technician.maintenance-record.form.label.status" path="status" choices= "${status}"/>
	<acme:input-money code="technician.maintenance-record.form.label.estimatedCost" path="estimatedCost" readonly="true"/>
	<acme:input-moment code="technician.maintenance-record.form.label.nextInspectionDueDate" path="nextInspection"/>
	<acme:input-textarea code="technician.maintenance-record.form.label.notes" path="notes" readonly="true"/>
		
	<jstl:choose>	
		<jstl:when test="${_command == 'show' && draftMode == false}">
			<acme:button code="technician.maintenance-record.form.button.tasks" action="/technician/maintenance-record/list?maintenanceRecordId=${id}"/>			
		</jstl:when> 
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode == true}">	
			<acme:submit code="technician.maintenance-record.form.button.update" action="/technician/maintenance-record/update"/>
			<acme:submit code="technician.maintenance-record.form.button.publish" action="/technician/maintenance-record/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="technician.maintenance-record.form.button.create" action="/technician/maintenance-record/create"/>
		</jstl:when>		
	</jstl:choose>	
</acme:form>