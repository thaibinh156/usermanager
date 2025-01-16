package com.infodation.userservice.components;

import com.authzed.api.v1.SchemaServiceGrpc;
import com.authzed.api.v1.WriteSchemaRequest;
import com.authzed.api.v1.WriteSchemaResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SchemaLoader {

    private final SchemaServiceGrpc.SchemaServiceBlockingStub schemaService;

    public SchemaLoader(SchemaServiceGrpc.SchemaServiceBlockingStub schemaService) {
        this.schemaService = schemaService;
    }

    public void loadSchemaFromFile(String filePath) {
        try {
            String schema = new String(Files.readAllBytes(Paths.get(filePath)));
            writeSchema(schema);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read schema file: " + e.getMessage(), e);
        }
    }

    private void writeSchema(String schema) {
        WriteSchemaRequest request = WriteSchemaRequest.newBuilder()
                .setSchema(schema)
                .build();

        try {
            WriteSchemaResponse response = schemaService.writeSchema(request);
            System.out.println("Schema applied successfully!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply schema: " + e.getMessage(), e);
        }
    }
}

