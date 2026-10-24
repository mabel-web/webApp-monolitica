package com.usach.mytingeso.dto;

import java.util.List;

public record SectionResponse(
        Long id, String period, String subjectCode, String subjectName, Long professorId,
        String professorName, int capacity, int enrolled, List<String> schedules
) {
}
