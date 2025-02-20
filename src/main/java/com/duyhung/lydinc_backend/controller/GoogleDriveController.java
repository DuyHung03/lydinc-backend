package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.GoogleDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drive")
public class GoogleDriveController {


    private final GoogleDriveService googleDriveService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(googleDriveService.uploadFileAndReturnUrl(file));
        } catch (IOException | GeneralSecurityException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }


//    @GetMapping("/files")
//    public ResponseEntity<?> getFilesInFolder(@RequestParam String folderId, @RequestParam String username) {
//        try {
//            List<File> files = googleDriveService.getFilesInFolder(folderId, username);
//            return ResponseEntity.ok(files);
//        } catch (IOException | GeneralSecurityException e) {
//            throw new FileInDriveNotFoundException(e.getMessage(), e.getCause());
//
//        }
//    }
//
//    @GetMapping("/subfolders")
//    public ResponseEntity<?> getSubfolders() {
//        try {
//            List<File> subfolders = googleDriveService.getUserFolders();
//            return ResponseEntity.ok(subfolders);
//        } catch (IOException | GeneralSecurityException e) {
//            throw new FileInDriveNotFoundException(e.getMessage(), e.getCause());
//        }
//    }

//    @GetMapping("/files-from-subfolders")
//    public ResponseEntity<?> getFilesFromUsernameFolders(@RequestParam String username) {
//        try {
//            Map<String, List<File>> files = googleDriveService.getFilesFromUsernameFolders(username);
//            return ResponseEntity.ok(files);
//        } catch (IOException | GeneralSecurityException e) {
//            throw new FileInDriveNotFoundException(e.getMessage(), e.getCause());
//
//        }
//    }
}
