package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.ExcelPracticeLink;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.repository.ExcelPracticeLinkRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import com.duyhung.lydinc_backend.utils.SecurityUtils;
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
    private final UserRepository userRepository;

    public String getPracticeLink(String moduleIndex, String lessonIndex) throws GeneralSecurityException, IOException {
        String studentId = SecurityUtils.getUserIdFromAuthentication();

        // Fetch existing practice link
        ExcelPracticeLink practiceLink = excelPracticeLinkRepository.findByStudentId(studentId);
        if (practiceLink != null) {
            return practiceLink.getLink();
        }

        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String rootFolderId;
        if (user.getUniversity() == null) {
            // If the user has no university, look inside "User" folder
            rootFolderId = googleDriveService.findSubfolderId(googleDriveService.parentFolderId, "User");
            if (rootFolderId == null) return null;

            rootFolderId = googleDriveService.findSubfolderId(rootFolderId, user.getUsername());
        } else {
            // If the user has a university, look inside "University" folder
            rootFolderId = googleDriveService.findSubfolderId(googleDriveService.parentFolderId, "University");
            if (rootFolderId == null) return null;

            rootFolderId = googleDriveService.findSubfolderId(rootFolderId, user.getUniversity().getShortName());
        }
        if (rootFolderId == null) return null;

        // Navigate to module and lesson folders
        String moduleFolderId = googleDriveService.findSubfolderId(rootFolderId, moduleIndex);
        if (moduleFolderId == null) return null;

        String lessonFolderId = googleDriveService.findSubfolderId(moduleFolderId, lessonIndex);
        if (lessonFolderId == null) return null;

        // Find the file in the lesson folder
        String fileName = user.getUsername() + "_" + moduleIndex + "." + lessonIndex + ".xlsx";
        File userFile = googleDriveService.findFileInFolder(lessonFolderId, fileName);
        if (userFile == null) return null;

        // Create a new practice link entry
        ExcelPracticeLink newPracticeLink = new ExcelPracticeLink();
        newPracticeLink.setStudentId(studentId);
        newPracticeLink.setLink("https://docs.google.com/spreadsheets/d/" + userFile.getId());
        excelPracticeLinkRepository.save(newPracticeLink);

        return newPracticeLink.getLink();
    }


}
