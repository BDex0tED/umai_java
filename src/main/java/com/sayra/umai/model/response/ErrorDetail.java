package com.sayra.umai.model.response;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ErrorDetail(
        String code,
        String message,
        Map<String, List<String>> fields,
        String traceId
) {
}
