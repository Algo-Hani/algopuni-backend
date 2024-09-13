package algohani.moduleadminapi.domain.controller;

import algohani.common.component.HealthCheckComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/health-check")
@RequiredArgsConstructor
public class HealthCheckController {

    private final HealthCheckComponent healthCheckComponent;

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok(healthCheckComponent.healthCheck() + " from admin");
    }
}
