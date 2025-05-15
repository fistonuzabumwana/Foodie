package com.example.Foodie.service.storage; // Or your chosen package

import com.mongodb.client.gridfs.model.GridFSFile; // Import GridFSFile
import org.springframework.data.mongodb.gridfs.GridFsResource; // Import GridFsResource
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    /**
     * Stores the given file in MongoDB GridFS.
     *
     * @param file The MultipartFile to store.
     * @return The ID (as a String) of the stored file in GridFS.
     * @throws IOException If an I/O error occurs during file processing.
     */
    String store(MultipartFile file) throws IOException;

    /**
     * Retrieves a file as a GridFsResource from GridFS by its ID.
     *
     * @param fileId The String ID of the file in GridFS.
     * @return GridFsResource representing the file, or null if not found.
     */
    GridFsResource getResource(String fileId);

    /**
     * Retrieves GridFSFile metadata by its ID.
     *
     * @param fileId The String ID of the file in GridFS.
     * @return GridFSFile metadata object, or null if not found.
     */
    GridFSFile getGridFSFile(String fileId);


    /**
     * Deletes a file from GridFS by its ID.
     *
     * @param fileId The String ID of the file in GridFS.
     */
    void delete(String fileId);
}