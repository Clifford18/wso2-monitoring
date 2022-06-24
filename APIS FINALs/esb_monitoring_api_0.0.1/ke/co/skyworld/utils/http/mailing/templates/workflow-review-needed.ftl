<#import "fragments/layout.ftl" as l>
<@l.layout>
    <p style="font-family: 'Noto Sans', sans-serif; font-size:14px; line-height: 22px; font-weight:500; text-align: left; color:#464646;">
        <#--<b>Dear ${model.full_name},</b>-->
        Hello,
        <br /><br />
        <#--You are required to review Workflow <b>${model.workflow_title}</b>.-->
        Workflow <b>${model.workflow_title}</b> has been sent for <b>REVIEW</b> to approval level ${model.current_workflow_level} by <b>${model.actor_full_name}</b>.
        <br /><br />
        The current workflow status is <b>${model.workflow_status}</b> and is at <b>Approval Level ${model.current_workflow_level} of ${model.max_workflow_levels}</b>

        <br />
    </p>
    <p style="padding-top: 10px; font-family: 'Noto Sans', sans-serif; font-size:14px; line-height: 22px; font-weight:500; text-align: left; color:#464646;">
        Go to <a href="${model.portal_workflow_link}">${model.workflow_title} Workflow</a> to review
    </p>
    <p style="padding-top: 10px; font-family: 'Noto Sans', sans-serif; font-size:14px; line-height: 22px; font-weight:500; text-align: left; color:#464646;">
        <b>Workflow Details:</b>
        <br/>
        Initiator: <b>${model.initiator_full_name}</b><br/>
        Date Started: <b>${model.workflow_date_created}</b><br/>
        Date Last Acted upon: <b>${model.workflow_date_modified}</b><br/>
    </p>
    <br/>
    <p style="padding-top: 10px; font-family: 'Noto Sans', sans-serif; font-size:14px; line-height: 22px; font-weight:500; text-align: left; color:#464646;">
        <b>Workflow Logs:</b>
        <#list model.logs as log>
            <br/><br/>
            Approval Level: <b>${log["approval_level"]}</b><br/>
            Approval Status: <b>${log["approval_status"]}</b><br/>
            Approver: <b>${log["full_name"]}</b><br/>
            Comments: <b>${log["user_comments"]}</b><br/>
            Date: <b>${log["log_date"]}</b>
        </#list>
    </p>
</@l.layout>