package com.sayra.umai.model.response;

import lombok.Builder;

@Builder
public record ErrorResponse(ErrorDetail error) {}
