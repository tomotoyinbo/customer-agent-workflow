package io.bly.customer.agent.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Camunda engine customization for the embedded process engine.
 * <p>
 * NOTE:
 *  - The Spring Boot starter already wires datasource, transactions,
 *    deployment scanning, webapps, etc.
 */
@Configuration
public class CamundaConfig {

    @Bean
    public ProcessEnginePlugin csWorkflowEnginePlugin() {

        return new AbstractProcessEnginePlugin() {
            @Override
            public void preInit(ProcessEngineConfigurationImpl config) {
                // History: FULL gives rich audit trails for debugging.
                config.setHistoryLevel(HistoryLevel.HISTORY_LEVEL_FULL);

                // Ensures the job executor is deployment-aware
                // (useful if you ever run multiple deployments).
                config.setJobExecutorDeploymentAware(true);

                // enable JDBC batch processing for better throughput
                config.setJdbcBatchProcessing(true);
            }

            @Override
            public void postInit(ProcessEngineConfigurationImpl config) {
                // Hook for adding custom parse listeners, incident handlers, etc.
            }

            @Override
            public void postProcessEngineBuild(ProcessEngine processEngine) {
                // Hook after the engine is fully built.
                // startup checks or logging if needed.
            }
        };
    }
}
