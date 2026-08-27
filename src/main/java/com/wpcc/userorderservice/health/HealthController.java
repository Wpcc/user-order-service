package com.wpcc.userorderservice.health;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
public class HealthController {

  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "UP");
  }

}
