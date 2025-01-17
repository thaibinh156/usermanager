package com.infodation.task_service.models;

public class AssignPermissionRequest {
    String resourceId;
    String subjectId;
    String resourceType;
    String subjectType;
    String relation;

    public AssignPermissionRequest() {
    }

    public AssignPermissionRequest(String resourceId, String subjectId, String resourceType, String subjectType, String relation) {
        this.resourceId = resourceId;
        this.subjectId = subjectId;
        this.resourceType = resourceType;
        this.subjectType = subjectType;
        this.relation = relation;
    }

    public String getRelation() {
        return relation;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
