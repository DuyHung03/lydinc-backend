package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Lesson;
import com.duyhung.lydinc_backend.model.Module;
import com.duyhung.lydinc_backend.model.dto.LessonDto;
import com.duyhung.lydinc_backend.model.dto.ModuleDto;
import com.duyhung.lydinc_backend.repository.LessonRepository;
import com.duyhung.lydinc_backend.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public String updateLessonData(String data, String moduleId) {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Lesson lesson = new Lesson();
        lesson.setModule(module);
        lesson.setLessonTitle(data);
        lesson.setLessonContent(data);

        lessonRepository.save(lesson);

        return "Update successfully";
    }

    public LessonDto getLessonData(String moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Lesson lesson = lessonRepository.findByModule_ModuleId(moduleId)
                .orElseGet(() -> createNewLesson(module));

        return mapToLessonDto(lesson, module);
    }

    private Lesson createNewLesson(Module module) {
        Lesson newLesson = Lesson.builder()
                .module(module)
                .build();
        return lessonRepository.save(newLesson);
    }

    private LessonDto mapToLessonDto(Lesson lesson, Module module) {
        return LessonDto.builder()
                .lessonId(lesson.getLessonId())
                .lessonTitle(lesson.getLessonTitle())
                .lessonContent(lesson.getLessonContent())
                .module(ModuleDto.builder()
                        .moduleId(module.getModuleId())
                        .level(module.getLevel())
                        .index(module.getIndex())
                        .parentModuleId(module.getParentModuleId())
                        .status(module.getStatus())
                        .build())
                .build();
    }


}
