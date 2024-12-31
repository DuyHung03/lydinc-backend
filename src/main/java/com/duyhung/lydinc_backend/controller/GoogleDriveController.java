package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.exception.FileInDriveNotFoundException;
import com.duyhung.lydinc_backend.service.GoogleDriveService;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drive")
public class GoogleDriveController {


    private final GoogleDriveService googleDriveService;

    @GetMapping("/files")
    public ResponseEntity<?> getFilesInFolder(@RequestParam String folderId, @RequestParam String username) {
        try {
            List<File> files = googleDriveService.getFilesInFolder(folderId, username);
            return ResponseEntity.ok(files);
        } catch (IOException | GeneralSecurityException e) {
            throw new FileInDriveNotFoundException(e.getMessage(), e.getCause());

        }
    }

    @GetMapping("/subfolders")
    public ResponseEntity<?> getSubfolders() {
        try {
            List<File> subfolders = googleDriveService.getUserFolders();
            return ResponseEntity.ok(subfolders);
        } catch (IOException | GeneralSecurityException e) {
            throw new FileInDriveNotFoundException(e.getMessage(), e.getCause());
        }
    }

    @GetMapping("/files-from-subfolders")
    public ResponseEntity<?> getFilesFromUsernameFolders(@RequestParam String username) {
        try {
            Map<String, List<File>> files = googleDriveService.getFilesFromUsernameFolders(username);
            return ResponseEntity.ok(files);
        } catch (IOException | GeneralSecurityException e) {
            throw new FileInDriveNotFoundException(e.getMessage(), e.getCause());

        }
    }
}
