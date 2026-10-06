package com.shreyash.payledger.dto;

import java.time.Instant;
import java.util.Map;

public record ApiError(int status, String message, Map<String, String> fieldErrors, Instant timestamp) {}