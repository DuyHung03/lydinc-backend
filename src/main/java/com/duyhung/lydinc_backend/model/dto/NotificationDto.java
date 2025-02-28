package com.duyhung.lydinc_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String title;
    private String message;
    private boolean isSeen = false;
    private Integer type;
    private LocalDateTime createAt;
}
