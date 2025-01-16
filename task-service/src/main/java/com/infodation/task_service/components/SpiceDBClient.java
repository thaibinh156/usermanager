package com.infodation.task_service.components;

import com.authzed.api.v1.*;
import org.springframework.beans.factory.annotation.Autowired;

public class SpiceDBClient {
    @Autowired
    private PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;
    public void assignManager(String taskId, String userId) {
        Relationship relationship = Relationship.newBuilder()
                .setResource(ObjectReference.newBuilder()
                        .setObjectType("task")
                        .setObjectId(taskId)
                        .build())
                .setRelation("manager")
                .setSubject(SubjectReference.newBuilder()
                        .setObject(ObjectReference.newBuilder()
                                .setObjectType("user")
                                .setObjectId(userId)
                                .build())
                        .build())
                .build();

        WriteRelationshipsRequest request = WriteRelationshipsRequest.newBuilder()
                .addUpdates(RelationshipUpdate.newBuilder()
                        .setOperation(RelationshipUpdate.Operation.OPERATION_CREATE)
                        .setRelationship(relationship)
                        .build())
                .build();

        permissionsService.writeRelationships(request);
    }
}

