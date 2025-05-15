package com.example.Foodie.service.storage; // Or your chosen package

import com.mongodb.BasicDBObject; // Import BasicDBObject
import com.mongodb.DBObject; // Import DBObject
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId; // Import ObjectId
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria; // Import Criteria
import org.springframework.data.mongodb.core.query.Query; // Import Query
import org.springframework.data.mongodb.gridfs.GridFsOperations; // For getResource
import org.springframework.data.mongodb.gridfs.GridFsTemplate;   // For store and find
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations; // Provides getResource

    @Autowired
    public FileStorageServiceImpl(GridFsTemplate gridFsTemplate, GridFsOperations gridFsOperations) {
        this.gridFsTemplate = gridFsTemplate;
        this.gridFsOperations = gridFsOperations;
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // Optional: Add metadata (e.g., original filename, content type)
        DBObject metadata = new BasicDBObject();
        metadata.put("originalFilename", file.getOriginalFilename());
        metadata.put("contentType", file.getContentType());
        // You could add more metadata like fileSize, uploaderId, etc.
        // metadata.put("fileSize", file.getSize());


        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(), // Filename stored in GridFS
                file.getContentType(),      // Content type stored in GridFS
                metadata                    // Additional metadata
        );
        return fileId.toHexString(); // Return the ID as a String
    }

    @Override
    public GridFsResource getResource(String fileId) {
        GridFSFile gridFsFile = getGridFSFile(fileId);
        if (gridFsFile != null) {
            return gridFsOperations.getResource(gridFsFile);
        }
        return null;
    }

    @Override
    public GridFSFile getGridFSFile(String fileId) {
        if (fileId == null || fileId.isEmpty()) {
            return null;
        }
        try {
            // Ensure the fileId is a valid ObjectId string if that's what you're using
            // Spring Data MongoDB's GridFsTemplate.findOne handles ObjectId conversion if ID field is ObjectId
            return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(fileId))));
        } catch (IllegalArgumentException e) {
            // Handle cases where fileId might not be a valid ObjectId hex string
            System.err.println("Invalid fileId format: " + fileId + " - " + e.getMessage());
            return null;
        }
    }


    @Override
    public void delete(String fileId) {
        if (fileId == null || fileId.isEmpty()) {
            return;
        }
        try {
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(fileId))));
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid fileId format for delete: " + fileId + " - " + e.getMessage());
        }
    }
}