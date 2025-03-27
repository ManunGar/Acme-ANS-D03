<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="assistanceAgent.claim.list.label.registrationMoment" path="registrationMoment" width="25%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.passengerEmail" path="passengerEmail" width="25%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.claimTypes" path="claimTypes" width="25%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.accepted" path="accepted" width="25%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:if test="${_command == 'list'}">
	<acme:button code="assistanceAgent.claim.list.button.list.resolved" action="/assistance-agent/claim/listResolved"/>
	<acme:button code="assistanceAgent.claim.list.button.list.notResolved" action="/assistance-agent/claim/listNotResolved"/>
</jstl:if>	
<jstl:if test="${_command == 'listResolved'}">
	<acme:button code="assistanceAgent.claim.list.button.list.listAll" action="/assistance-agent/claim/list"/>
	<acme:button code="assistanceAgent.claim.list.button.list.notResolved" action="/assistance-agent/claim/listNotResolved"/>
</jstl:if>	
<jstl:if test="${_command == 'listNotResolved'}">
	<acme:button code="assistanceAgent.claim.list.button.list.listAll" action="/assistance-agent/claim/list"/>
	<acme:button code="assistanceAgent.claim.list.button.list.resolved" action="/assistance-agent/claim/listResolved"/>
</jstl:if>	