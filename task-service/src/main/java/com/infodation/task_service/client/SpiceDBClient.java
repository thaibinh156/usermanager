package com.infodation.task_service.client;

import com.authzed.api.v1.*;
import com.authzed.grpcutil.BearerToken;
import com.infodation.task_service.models.AssignPermissionRequest;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SpiceDBClient {
    private static final Logger logger = LoggerFactory.getLogger(SpiceDBClient.class);

    @Value("${authzed.token}")
    private String token;

    @Value("${authzed.base.host}")
    private String zedHost;

    @Value("${authzed.base.port}")
    private String zedPort;

    public String readSchema(String schema) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(zedHost + ":" + zedPort).usePlaintext().build();
        BearerToken bearerToken = new BearerToken(token);
        SchemaServiceGrpc.SchemaServiceBlockingStub schemaService =
                SchemaServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);
        WriteSchemaRequest request = WriteSchemaRequest.newBuilder()
                .setSchema(schema)
                .build();
        try {
            WriteSchemaResponse response = schemaService.writeSchema(request);
            logger.info("Schema written successfully {}", response);
            return "Schema written successfully" + response;
        } catch (Exception e) {
            logger.error("Error writing schema: {}", e.getMessage());
            return "Error writing schema: " + e.getMessage();
        } finally {
            channel.shutdown();
        }
    }

    public String assignPermission(AssignPermissionRequest requestObj) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(zedHost + ":" + zedPort).usePlaintext().build();
        BearerToken bearerToken = new BearerToken(token);
        if (channel == null) {
            logger.error("Channel is null");
            return "Channel is null";
        }
        PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService =
                PermissionsServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);

        Relationship relationship = Relationship.newBuilder()
                .setResource(ObjectReference.newBuilder()
                        .setObjectType(requestObj.getResourceType())
                        .setObjectId(requestObj.getResourceId())
                        .build())
                .setRelation(requestObj.getRelation())
                .setSubject(SubjectReference.newBuilder()
                        .setObject(ObjectReference.newBuilder()
                                .setObjectType(requestObj.getSubjectType())
                                .setObjectId(requestObj.getSubjectId())
                                .build())
                        .build())
                .build();

        try {
            RelationshipUpdate update = RelationshipUpdate.newBuilder()
                    .setOperation(RelationshipUpdate.Operation.OPERATION_CREATE)
                    .setRelationship(relationship)
                    .build();

            WriteRelationshipsResponse response = permissionsService.writeRelationships(WriteRelationshipsRequest.newBuilder()
                    .addUpdates(update)
                    .build());
            logger.info("Permission assigned successfully {}", response);
            return "Permission assigned successfully" + response;
        } catch (Exception e) {
            logger.error("Error assigning permission", e);
            return "Error assigning permission: " + e.getMessage();
        } finally {
            if (channel != null && !channel.isShutdown()) {
                channel.shutdown();
            }
        }
    }

    public Boolean checkPermission(AssignPermissionRequest requestObj) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(zedHost + ":" + zedPort).usePlaintext().build();
        BearerToken bearerToken = new BearerToken(token);
        PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionService =
                PermissionsServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);

        CheckPermissionRequest request = CheckPermissionRequest.newBuilder()
                .setResource(ObjectReference.newBuilder()
                        .setObjectType(requestObj.getResourceType())
                        .setObjectId(requestObj.getResourceId())
                        .build())
                .setPermission(requestObj.getRelation())
                .setSubject(SubjectReference.newBuilder()
                        .setObject(ObjectReference.newBuilder()
                                .setObjectType(requestObj.getSubjectType())
                                .setObjectId(requestObj.getSubjectId())
                                .build())
                        .build())
                .build();

        var response = permissionService.checkPermission(request);
        logger.info("Permission checked successfully {}", response.getPermissionship());
        channel.shutdown();
        return response.getPermissionship() == CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION;
    }
}
