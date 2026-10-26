package com.usach.mytingeso.dto;

import java.util.List;

public record AvailableSubjectResponse(
        Long subject, String subjectCode, String subjectName, int semester,
        Long sectionId, String professorName, List<String> schedules, long seatsLeft, boolean prerequisitesMet
) {
}
