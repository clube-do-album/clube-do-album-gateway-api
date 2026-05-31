package br.com.clubedoalbum.gateway.controller;

import br.com.clubedoalbum.gateway.dto.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/health")
  public HealthResponse health() {
    return new HealthResponse("clube-do-album-gateway-api", "UP");
  }
}
