# Customer Service Workflow Backend (Camunda + Spring Boot)

This project is a proof-of-concept backend that demonstrates how to orchestrate
a customer service ticket workflow using:

- **Camunda 7 (embedded engine)**
- **Spring Boot (Java 21)**
- **PostgreSQL** for persistence
- **Kafka** for ticket-created events
- REST APIs to be consumed by a separate React UI

The main use case:

> A customer service agent looks up an order, selects an issue type
> (lost item / damaged item / address change), enters details, and the system
> creates a ticket and notifies the customer — all orchestrated via a BPMN process.

---

## 1. High-level architecture

- **Camunda 7** is embedded into the Spring Boot app via `camunda-bpm-spring-boot-starter`.
- **BPMN process** (`customer_issue_flow.bpmn`) coordinates:
    - Order lookup (service task)
    - Issue type selection (user task)
    - Issue details entry (user task)
    - Parallel ticket persistence + customer notification (service tasks)
- **Spring Boot**:
    - Exposes REST endpoints under `/api/issues` (see [docs/api-contracts.md](docs/api-contracts.md))
    - Uses JPA + Postgres for `Ticket` persistence
    - Publishes `TicketCreatedEvent` to Kafka
- **React UI** (separate project) will:
    - Call `/api/issues/start` to start the process
    - Poll `/api/issues/tasks` to drive the agent UI
    - Complete user tasks via REST

---

## 2. Key modules & structure

```text
src/main/java/com/example/csworkflow/
├─ CsWorkflowApplication.java         # Spring Boot entrypoint
├─ config/                            # Camunda, Kafka, web config
├─ workflow/                          # Camunda-facing layer
│  ├─ controller/                     # REST endpoints for the workflow
│  ├─ delegate/                       # JavaDelegates for service tasks
│  └─ dto/                            # REST DTOs
├─ domain/                            # Business logic, Camunda-agnostic
│  ├─ order/                          # Order lookup stub/service
│  └─ ticket/                         # Ticket entity, repo, service
├─ messaging/                         # Kafka events & producers
└─ util/                              # Logging / MDC helpers (optional)
