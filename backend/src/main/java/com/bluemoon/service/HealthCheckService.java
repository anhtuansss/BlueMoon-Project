package com.bluemoon.service;

import java.util.Map;

public interface HealthCheckService {
    Map<String, Object> getSystemStatus();
}
