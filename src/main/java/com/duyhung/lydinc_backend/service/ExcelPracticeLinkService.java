package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.ExcelPracticeLink;
import com.duyhung.lydinc_backend.repository.ExcelPracticeLinkRepository;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class ExcelPracticeLinkService {

    private final ExcelPracticeLinkRepository excelPracticeLinkRepository;
    private final GoogleDriveService googleDriveService;

    public ExcelPracticeLink getPracticeLink(
            String studentId,
            String username,
            String schoolName,
            String moduleName,
            String lessonName
    ) throws GeneralSecurityException, IOException {
        // Fetch existing practice link
        ExcelPracticeLink practiceLink = excelPracticeLinkRepository.findByStudentId(studentId);

        if (practiceLink == null) {
            // Navigate through the folder hierarchy
            String schoolFolderId = googleDriveService.findSubfolderId(googleDriveService.parentFolderId, schoolName);
            if (schoolFolderId == null) {
                return null; // School folder not found
            }

            String moduleFolderId = googleDriveService.findSubfolderId(schoolFolderId, moduleName);
            if (moduleFolderId == null) {
                return null; // Module folder not found
            }

            String lessonFolderId = googleDriveService.findSubfolderId(moduleFolderId, lessonName);
            if (lessonFolderId == null) {
                return null; // Lesson folder not found
            }

            // Find the file by username in the lesson folder
            String fileName = username + "_" + lessonName + ".xlsx";
            File userFile = googleDriveService.findFileInFolder(lessonFolderId, fileName);
            if (userFile == null) {
                return null; // File not found
            }

            // Create a new practice link
            ExcelPracticeLink newPracticeLink = new ExcelPracticeLink();
            newPracticeLink.setStudentId(studentId);
            newPracticeLink.setLink("https://docs.google.com/spreadsheets/d/" + userFile.getId());
            excelPracticeLinkRepository.save(newPracticeLink);

            return newPracticeLink;
        }

        return practiceLink;
    }


}
