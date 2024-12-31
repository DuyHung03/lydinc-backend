package com.duyhung.lydinc_backend.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
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

import java.io.FileInputStream;
import java.io.IOException;
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
    private String parentFolderId;

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
    public List<File> getFilesInFolder(String folderId, String username) throws IOException, GeneralSecurityException {
        Drive driveService = createDriveService();
        String query = String.format("'%s' in parents and name contains '%s'", folderId, username);

        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name, mimeType)")
                .execute();

        return result.getFiles();
    }

    // Get all subfolders in the parent folder
    public List<File> getUserFolders() throws IOException, GeneralSecurityException {
        Drive driveService = createDriveService();
        String query = String.format("'%s' in parents and mimeType = 'application/vnd.google-apps.folder'", parentFolderId);

        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

        return result.getFiles();
    }

    // Map files for each username folder
    public Map<String, List<File>> getFilesFromUsernameFolders(String username) throws IOException, GeneralSecurityException {
        List<File> subfolders = getUserFolders();
        Map<String, List<File>> filesBySubfolder = new HashMap<>();

        for (File subfolder : subfolders) {
            List<File> filesInSubfolder = getFilesInFolder(subfolder.getId(), username);
            if (!filesInSubfolder.isEmpty()) {
                filesBySubfolder.put(subfolder.getName(), filesInSubfolder);
            }
        }

        return filesBySubfolder;
    }
}
