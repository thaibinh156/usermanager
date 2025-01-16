package com.infodation.userservice.components;

import com.authzed.api.v1.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SpiceDBClient {


    private PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;

    @PostConstruct
    public void init() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        permissionsService = PermissionsServiceGrpc.newBlockingStub(channel);
    }

    public void createUserInSpiceDB(String userId) {
        Relationship relationship = Relationship.newBuilder()
                .setResource(ObjectReference.newBuilder()
                        .setObjectType("user")
                        .setObjectId(userId)
                        .build())
                .setRelation("")
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

