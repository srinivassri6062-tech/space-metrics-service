# High-Throughput Request Ingestion & Metrics Service

This project implements a high-throughput Java + Spring Boot service that ingests request IDs, performs per-minute deduplication, aggregates metrics, persists results, and optionally forwards them to an external HTTP endpoint (Extension-1).

---

## 1. Tech Stack

- Java 17
- Spring Boot 3.2.2
- Spring Web + WebFlux (WebClient)
- H2 Database (in-memory)
- Spring Scheduler
- Lombok
- Swagger / OpenAPI

---

## 2. Core Functional Overview

The system exposes a single REST endpoint:

```
GET /api/space/accept?id={id}&endpoint={optional}
```

### Request Behavior:

| Behavior | Description |
|---|---|
| Fast response | Designed for theoretical 10k RPS ingestion pipeline |
| Dedup | Same ID counted only once per minute |
| Calendar minute bucketing | `HH:mm:00` → `HH:mm:59` |
| Non-blocking HTTP | External calls dispatched via `WebClient` |
| Logging | Outbound status / errors logged |
| Aggregation | Every minute scheduler computes unique count |
| Persistence | Per-minute aggregates stored in H2 DB |

Example timeline:

```
12:00:10 id=10
12:00:30 id=10
12:00:45 id=11
→ unique = 2 for 12:00
```

---

## 3. Swagger / API Docs

After startup:

```
http://localhost:8080/swagger-ui.html
```
---

## 4. Scheduler & Aggregation

A scheduler runs once per minute to compute:

- `minute_start` (e.g., `2026-01-10T17:15`)
- `unique_id_count`

Then:

✔ logs the result  
✔ persists into H2 table `MINUTE_STATS`  
✔ triggers Extension-1 POST logic (if applicable)

### Sample Logged Output

```
[AGGREGATION] 2026-01-10T17:15 -> 153 unique ids
```

---

## 5. Database Schema (H2)

H2 auto-creates:

```
MINUTE_STATS
-----------
MINUTES        VARCHAR
UNIQUE_COUNT   INT
```

Example data:

```
SELECT * FROM MINUTE_STATS;

MINUTES                UNIQUE_COUNT
2026-01-10T17:14       0
2026-01-10T17:15       1
2026-01-10T17:16       98
```

---

## 6. External HTTP Calls (Optional Per Request)

If user provides an `endpoint` parameter:

```
GET /api/space/accept?id=10&endpoint=http://example.com/ping
```

Then:

- Outbound GET request executed asynchronously (non-blocking)
- Logs status codes or errors
- Ingestion pipeline remains unblocked

This prevents external latency from impacting ingestion throughput.

---

## 7. Error Handling Strategy

| Type | Handling |
|---|---|
| Bad input | Returns `"failed"` |
| Outbound HTTP failure | Logged, pipeline continues |
| Scheduler failure | Logged, does not halt system |
| Duplicate IDs | Ignored via Set for same minute |
| Timeout | 2 seconds on outbound HTTP |

---

## 8. Example API Calls

### Valid Request

```
curl "http://localhost:8080/api/space/accept?id=101"
```

Response:

```
ok
```

### With Endpoint

```
curl "http://localhost:8080/api/space/accept?id=202&endpoint=https://postman-echo.com/get"
```

Response:

```
ok
```

### Invalid Request

```
curl "http://localhost:8080/api/space/accept"
```

Response:

```
failed
```

---

## 9. High-Throughput Design Rationale

To support theoretical 10k RPS:

✔ No database writes during request handling  
✔ Dedup stored purely in-memory per-minute  
✔ Outbound HTTP done via WebClient → async / non-blocking  
✔ Scheduler batches DB writes once per minute  
✔ External failures do not back-pressure ingestion  

This mirrors real ingestion pipelines.

---

## 10. How to Build & Run

Run via Maven:

```
mvn spring-boot:run
```

---

## 11. H2 Console

URL:

```
http://localhost:8080/h2-console
```

JDBC URL:

```
jdbc:h2:mem:metricsdb
```

---

## 12. Extensions Status

| Extension | Status |
|---|---|
| Extension-1 (POST minute metrics to endpoint) | ✔ Implemented |
| Extension-2 (Multi-instance global dedupe) | ❌ Not implemented (design documented) |
| Extension-3 (Kafka / Streaming output) | ❌ Not implemented (design documented) |

---

## 13. Design for Extension-2 (Multi-Instance / Load Balancer Scenario)

**Goal:** ensure global deduplication when multiple application instances receive the same ID within the same minute.

### Challenges

```
Client → LB → Instance A
Client → LB → Instance B
```

Both may receive `id=10` at `17:15`.

### Proposed Solution

Use distributed store such as:

✔ Redis Set  
✔ Redis Bloom Filter  
✔ Kafka compacted topic  
✔ DB unique constraint  

Example Redis schema:

```
SADD metrics:2026-01-10T17:15 10
SADD metrics:2026-01-10T17:15 10 (ignored)
SCARD metrics:2026-01-10T17:15 → 1
```

Alternate approaches:

| Approach | Benefit |
|---|---|
| Redis | Fast, O(1), natural for dedupe |
| Kafka | Replay + audit |
| DB unique index | Consistent but slower |

---

## 14. Design for Extension-3 (Kafka / Streaming Instead of Logs)

**Goal:** produce per-minute metrics events for analytics, billing, dashboards, machine learning, etc.

### Event Payload Example

```
{
  "minuteStart": "2026-01-10T17:15",
  "uniqueIdCount": 153
}
```

### Kafka Topic Definition

```
Topic: minute.metrics
Key: minuteStart
Value: JSON metrics
Retention: configurable
```

### Downstream Consumers

## 15. Assumptions

- Single instance deployment (no global dedup)
- In-memory minute state is sufficient for assignment scenarios
- Outbound HTTP failures do not fail ingestion
- H2 used for demo; production may use PostgreSQL/Redis/Kafka

---

## 16. Conclusion

This system fulfills all core assignment goals:

✔ high-throughput ingestion  
✔ per-minute ID deduplication  
✔ periodic aggregation & persistence  
✔ optional external dispatch (Extension-1)  
✔ Swagger API documentation  

Additionally, Extension-2 and Extension-3 design considerations were documented to demonstrate scalability and streaming readiness for production environments.

