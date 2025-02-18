package com.duyhung.lydinc_backend.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleDriveService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static String getPathToGoogleClient() {
        String currentDir = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDir, "cred.json");
        return filePath.toString();
    }

    @Value("${google-drive.parent-folder-id}")
    public String parentFolderId;

    private Drive createDriveService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(getPathToGoogleClient()))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        HttpRequestInitializer initializer = new HttpCredentialsAdapter(credentials);

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                initializer
        ).build();
    }

    // Get files in a specific subfolder filtered by username
    // Get files in a specific folder and resolve shortcuts
    public String findSubfolderId(String parentFolderId, String subfolderName) throws IOException, GeneralSecurityException {
        Drive driveService = createDriveService();
        String query = String.format("'%s' in parents and name = '%s' and mimeType = 'application/vnd.google-apps.folder'", parentFolderId, subfolderName);

        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

        if (result.getFiles().isEmpty()) {
            return null; // Subfolder not found
        }

        return result.getFiles().get(0).getId(); // Return the ID of the subfolder
    }

    public File findFileInFolder(String folderId, String fileName) throws IOException, GeneralSecurityException {
        Drive driveService = createDriveService();
        String query = String.format("'%s' in parents and name = '%s'", folderId, fileName);

        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

        if (result.getFiles().isEmpty()) {
            return null; // File not found
        }

        return result.getFiles().get(0); // Return the file
    }

    public String uploadFileAndReturnUrl(MultipartFile multipartFile) throws IOException, GeneralSecurityException {
        Drive driveService = createDriveService();
        String folderId = "10PaudcvfvSCvNHziQSFXCb1_hz1yervz"; // Your Google Drive folder ID

        // Prepare file metadata
        File fileMetadata = new File();
        fileMetadata.setName(multipartFile.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(folderId));

        // Use InputStreamContent for uploading directly from MultipartFile
        try (InputStream inputStream = multipartFile.getInputStream()) {
            InputStreamContent mediaContent = new InputStreamContent(multipartFile.getContentType(), inputStream);

            // Upload to Google Drive
            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            // Construct and return direct image view URL
            String fileId = uploadedFile.getId();
            return "https://lh3.googleusercontent.com/d/" + fileId;
        }
    }


}
