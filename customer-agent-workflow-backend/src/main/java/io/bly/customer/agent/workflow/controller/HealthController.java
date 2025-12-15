package io.bly.customer.agent.workflow.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ProcessEngine processEngine;

    private final String apiBasePath;

    private final ObjectMapper objectMapper;

    public HealthController(DataSource dataSource,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            ProcessEngine processEngine,
                            @Value("${app.api.base-path:/api/v1}") String apiBasePath,
                            ObjectMapper objectMapper) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.kafkaTemplate = kafkaTemplate;
        this.processEngine = processEngine;
        this.apiBasePath = apiBasePath;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "${app.api.base-path:/api/v1}/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public String health() throws JsonProcessingException {

        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", Instant.now().toString());

        // DB check: trivial "SELECT 1"
        try {
            Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            healthInfo.put("database", one != null && one == 1 ? "UP" : "DEGRADED");
        } catch (Exception e) {
            healthInfo.put("database", "DOWN");
            healthInfo.put("databaseError", e.getMessage());
        }

        // Kafka "soft" check: verify template is present; deeper checks can be added later
        try {
            kafkaTemplate.toString(); // just ensure bean is initialized
            healthInfo.put("kafka", "UP");
        } catch (Exception e) {
            healthInfo.put("kafka", "DOWN");
            healthInfo.put("kafkaError", e.getMessage());
        }

        // Camunda engine check
        try {
            String processEngineName = processEngine.getName();
            healthInfo.put("camunda", "UP");
            healthInfo.put("camundaEngineName", processEngineName);
        } catch (Exception e) {
            healthInfo.put("camunda", "DOWN");
            healthInfo.put("camundaError", e.getMessage());
        }

        // Base path (helps UI / devs verify they hit the right version)
        healthInfo.put("apiBasePath", apiBasePath);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(healthInfo);
    }
}
