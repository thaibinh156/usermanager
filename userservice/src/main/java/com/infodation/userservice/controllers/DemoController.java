package com.infodation.userservice.controllers;

import com.authzed.api.v1.*;
import com.authzed.grpcutil.BearerToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/demo")
public class DemoController {
    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/hello")
    public String hello(@RequestBody String schema) {
        // Create schema service client
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50051").usePlaintext().build();
        BearerToken bearerToken = new BearerToken("foobar");
        SchemaServiceGrpc.SchemaServiceBlockingStub
                schemaService = SchemaServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);

//        String schema =
//                "definition user {}\n" +
//                        "\n" +
//                        "definition task {\n" +
//                        "    relation create_by: user\n" +
//                        "}\n" +
//                        "\n" +
//                        "definition role {\n" +
//                        "    relation has_role: user\n" +
//                        "}\n" +
//                        "\n" +
//                        "definition admin_role {\n" +
//                        "    relation grants: role\n" +
//                        "}\n" +
//                        "\n" +
//                        "definition normal_role {\n" +
//                        "    relation grants: role\n" +
//                        "}";


        try {
            ReadSchemaRequest request = ReadSchemaRequest.newBuilder().build();
            ReadSchemaResponse response = schemaService.readSchema(request);

            // Show current schema
            return "Current schema:\n" + response.getSchemaText();
        } catch (Exception e) {
            return "Lỗi khi đọc schema: " + e.getMessage();
        } finally {
            channel.shutdown();
        }
    }

    @PostMapping("/schema")
    public String writeSchema(@RequestBody String schema) {
        // Create schema service client
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50051").usePlaintext().build();
        BearerToken bearerToken = new BearerToken("foobar");
        SchemaServiceGrpc.SchemaServiceBlockingStub
                schemaService = SchemaServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);

        try {
            WriteSchemaRequest request = WriteSchemaRequest.newBuilder()
                    .setSchema(schema)
                    .build();
            WriteSchemaResponse response = schemaService.writeSchema(request);

            // Show current schema
            return "Schema updated successfully";
        } catch (Exception e) {
            return "Has error during update schema " + e.getMessage();
        } finally {
            channel.shutdown();
        }
    }

    @GetMapping("/test")
    public String testUser(@RequestParam("task_id") String taskId, @RequestParam("user_id") String userId) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50051").usePlaintext().build();
        BearerToken bearerToken = new BearerToken("foobar");
        PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionService = PermissionsServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);
        Relationship relationship = Relationship.newBuilder()
                .setResource(ObjectReference.newBuilder()
                        .setObjectType("task")
                        .setObjectId(taskId) // ID của task
                        .build())
                .setRelation("create_by")
                .setSubject(SubjectReference.newBuilder()
                        .setObject(ObjectReference.newBuilder()
                                .setObjectType("user")
                                .setObjectId(userId) // ID của user
                                .build())
                        .build())
                .build();
        RelationshipUpdate update = RelationshipUpdate.newBuilder()
                .setOperation(RelationshipUpdate.Operation.OPERATION_CREATE)
                .setRelationship(relationship)
                .build();

        permissionService.writeRelationships(WriteRelationshipsRequest.newBuilder()
                .addUpdates(update)
                .build());

        channel.shutdown();

        return "Đã tạo quan hệ giữa user " + userId + " và task " + taskId;
    }

    @GetMapping("/check-permission")
    public String checkPermission(@RequestParam("task_id") String taskId, @RequestParam("user_id") String userId) {
        ManagedChannel channel = ManagedChannelBuilder
                .forTarget("localhost:50051").usePlaintext().build();
        BearerToken bearerToken = new BearerToken("foobar");

        PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionService =
                PermissionsServiceGrpc.newBlockingStub(channel).withCallCredentials(bearerToken);

        CheckPermissionRequest request = CheckPermissionRequest.newBuilder()
                .setResource(ObjectReference.newBuilder()
                        .setObjectType("task")
                        .setObjectId(taskId) // Task cần kiểm tra
                        .build())
                .setPermission("create_by") // Quyền cần kiểm tra
                .setSubject(SubjectReference.newBuilder()
                        .setObject(ObjectReference.newBuilder()
                                .setObjectType("user")
                                .setObjectId(userId) // User cần kiểm tra
                                .build())
                        .build())
                .build();

        var response = permissionService.checkPermission(request);

        channel.shutdown();
        return "User có quyền không? " + response.getPermissionship();

    }
}
