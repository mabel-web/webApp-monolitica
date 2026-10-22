package com.usach.mytingeso.dto;

import java.util.List;

public record SubjectResponse(
        Long id, String code, String name, int semester, String tel, int sct,
        List<String> prerequisites
) {
}
