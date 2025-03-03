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

    public String getPracticeLink(
            Integer courseId,
            String moduleId,
            String lessonId,
            Integer moduleIndex,
            Integer lessonIndex
    ) throws GeneralSecurityException, IOException {
        String studentId = SecurityUtils.getUserIdFromAuthentication();

        // Fetch existing practice link
        ExcelPracticeLink practiceLink = excelPracticeLinkRepository.findByStudentId(
                studentId, courseId, moduleId, lessonId
        );
        if (practiceLink != null) {
            return practiceLink.getLink();
        }

        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String rootFolderId;
        if (user.getUniversity() == null) {
            // If the user has no university, look inside "User" folder
            rootFolderId = googleDriveService.findSubfolderId(
                    googleDriveService.parentFolderId,
                    "User"
            ); // type folder
            if (rootFolderId == null) {
                return null;
            }
            // Find User folder
            rootFolderId = googleDriveService.findSubfolderId(rootFolderId, user.getUsername()); //username folder

            if (rootFolderId == null) {
                return null;
            }
        } else {
            // If the user has a university, look inside "University" folder
            rootFolderId = googleDriveService.findSubfolderId(
                    googleDriveService.parentFolderId, "University"
            ); // type folder
            if (rootFolderId == null) {
                return null;
            }

            // Find University folder
            rootFolderId = googleDriveService.findSubfolderId(
                    rootFolderId, user.getUniversity().getShortName()
            ); //university name folder
        }

        // Find Course Folder (courseId)
        String courseFolderId = googleDriveService.findSubfolderId(rootFolderId, courseId.toString());
        if (courseFolderId == null) {
            return null;
        }

        // Find Module Folder (moduleIndex)
        String moduleFolderId = googleDriveService.findSubfolderId(courseFolderId, moduleIndex.toString());
        if (moduleFolderId == null) {
            return null;
        }

        // Find Lesson Folder (lessonIndex)
        String lessonFolderId = googleDriveService.findSubfolderId(moduleFolderId, lessonIndex.toString());
        if (lessonFolderId == null) {
            return null;
        }

        // Find the File in the Lesson Folder
        String filePath = user.getUsername() + "_" + moduleIndex + "." + lessonIndex;
        File userFile = googleDriveService.findFileInFolder(lessonFolderId, filePath);
        if (userFile == null) {
            return null;
        }

        // Save the practice link
        ExcelPracticeLink newPracticeLink = new ExcelPracticeLink();
        newPracticeLink.setStudentId(studentId);
        newPracticeLink.setCourseId(courseId);
        newPracticeLink.setModuleId(moduleId);
        newPracticeLink.setLessonId(lessonId);
        newPracticeLink.setModuleIndex(moduleIndex);
        newPracticeLink.setLessonIndex(lessonIndex);
        newPracticeLink.setLink("https://docs.google.com/spreadsheets/d/" + userFile.getId());
        excelPracticeLinkRepository.save(newPracticeLink);

        return newPracticeLink.getLink();
    }
}
