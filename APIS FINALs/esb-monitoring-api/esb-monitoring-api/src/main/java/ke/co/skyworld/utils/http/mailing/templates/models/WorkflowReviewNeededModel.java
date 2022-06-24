package ke.co.skyworld.utils.http.mailing.templates.models;

import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.utils.Constants;

public class WorkflowReviewNeededModel {

    private String workflow_id;
    private String title;
    private String full_name;
    private String initiator_full_name;
    private String workflow_title;
    private String workflow_status;
    private String approval_status;
    private String app_type;
    private String workflow_date_created;
    private String workflow_date_modified;
    private int current_workflow_level;
    private int next_workflow_level;
    private int max_workflow_levels;
    private String portal_workflow_link;
    private FlexicoreArrayList logs;

    public WorkflowReviewNeededModel(PortalTypes portal) {
        portal_workflow_link = Constants.getPortalWorkflowLink(portal);
    }

    public String getWorkflow_id() {
        return workflow_id;
    }

    public void setWorkflow_id(String workflow_id) {
        this.workflow_id = workflow_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getInitiator_full_name() {
        return initiator_full_name;
    }

    public void setInitiator_full_name(String initiator_full_name) {
        this.initiator_full_name = initiator_full_name;
    }

    public String getWorkflow_title() {
        return workflow_title;
    }

    public void setWorkflow_title(String workflow_title) {
        this.workflow_title = workflow_title;
    }

    public String getWorkflow_status() {
        return workflow_status;
    }

    public void setWorkflow_status(String workflow_status) {
        this.workflow_status = workflow_status;
    }

    public String getApproval_status() {
        return approval_status;
    }

    public void setApproval_status(String approval_status) {
        this.approval_status = approval_status;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public String getWorkflow_date_created() {
        return workflow_date_created;
    }

    public void setWorkflow_date_created(String workflow_date_created) {
        this.workflow_date_created = workflow_date_created;
    }

    public String getWorkflow_date_modified() {
        return workflow_date_modified;
    }

    public void setWorkflow_date_modified(String workflow_date_modified) {
        this.workflow_date_modified = workflow_date_modified;
    }

    public int getCurrent_workflow_level() {
        return current_workflow_level;
    }

    public void setCurrent_workflow_level(int current_workflow_level) {
        this.current_workflow_level = current_workflow_level;
    }

    public int getNext_workflow_level() {
        return next_workflow_level;
    }

    public void setNext_workflow_level(int next_workflow_level) {
        this.next_workflow_level = next_workflow_level;
    }

    public int getMax_workflow_levels() {
        return max_workflow_levels;
    }

    public void setMax_workflow_levels(int max_workflow_levels) {
        this.max_workflow_levels = max_workflow_levels;
    }

    public String getPortal_workflow_link() {
        return portal_workflow_link;
    }

    public void setPortal_workflow_link(String portal_workflow_link) {
        this.portal_workflow_link = portal_workflow_link;
    }

    public FlexicoreArrayList getLogs() {
        return logs;
    }

    public void setLogs(FlexicoreArrayList logs) {
        this.logs = logs;
    }
}
