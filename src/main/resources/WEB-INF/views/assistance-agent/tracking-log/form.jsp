<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<jstl:if test="${_command != 'create' }">
		<acme:input-moment code="assistanceAgent.trackingLog.form.label.lastUpdateMoment" path="lastUpdateMoment" readonly="true"/>
	</jstl:if> 
	<acme:input-textarea code="assistanceAgent.trackingLog.form.label.step" path="step"/>
	<acme:input-double code="assistanceAgent.trackingLog.form.label.resolutionPercentage" path="resolutionPercentage"/>
	<acme:input-select code="assistanceAgent.trackingLog.form.label.accepted" path="accepted" choices="${status}"/>
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.resolution" path="resolution"/>
	<acme:input-select code="assistanceAgent.trackingLog.form.label.claim" path="claim" choices="${claims}" readonly="${readOnlyClaim}"/>
	
	<jstl:choose>	 
		<jstl:when test="${_command != 'create' && draftMode == true}">
			<acme:submit code="assistanceAgent.trackingLog.form.button.update" action="/assistance-agent/tracking-log/update"/>
			<jstl:if test="${claimDraftMode == false}">
				<acme:submit code="assistanceAgent.trackingLog.form.button.publish" action="/assistance-agent/tracking-log/publish"/>
			</jstl:if>
			<acme:submit code="assistanceAgent.trackingLog.form.button.delete" action="/assistance-agent/tracking-log/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="assistanceAgent.trackingLog.form.button.create" action="/assistance-agent/tracking-log/create"/>
		</jstl:when>
	</jstl:choose>
</acme:form>