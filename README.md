# Project Orbit — Backend (Reference Implementation)

This is a **Maven multi-module Spring Boot** implementation of Project Orbit,
generated to accompany the **Day 24: System Design Fundamentals** training
session. Each module directly implements one of the four JD-named KARAT
case studies covered that day, wired together into one working e-commerce
system.

| Module | Port | Day 24 Case Study | Core Technique |
|---|---|---|---|
| `gateway-service` | 8080 | Case Study 4 — Rate Limiting | Token Bucket algorithm |
| `product-catalog-service` | 8081 | Case Study 1 — URL Shortener | Base62 encoding + cache |
| `order-service` | 8082 | Case Study 3 — E-Commerce Platform (orchestrator) | Circuit breaker (Resilience4j), event publishing |
| `payment-service` | 8083 | (supports Case Study 3 bottleneck demo) | Chaos-mode toggle to simulate a failing dependency |
| `inventory-service` | 8084 | (supports Case Study 3) | Simple stock reservation |
| `notification-service` | 8085 | Case Study 2 — Notification System | Min-heap (PriorityQueue) priority dispatch |

## Design → Code Map

This project is the direct implementation of the Day 24 session plan
(`Day24_System_Design_Session_Plan.md`). Every code comment referencing
"Case Study N" ties back to that document — read the relevant section
before presenting a module in class.

## Quick Start (zero external infrastructure required)

Every service defaults to an in-memory H2 database and an in-memory
event/rate-limit store, so the whole system runs locally with nothing
installed beyond a JDK 17 and Maven.

```bash
# From the orbit-backend root, build everything:
mvn clean install

# Then, in separate terminals, start each service:
cd payment-service      && mvn spring-boot:run     # port 8083
cd inventory-service     && mvn spring-boot:run     # port 8084
cd product-catalog-service && mvn spring-boot:run   # port 8081
cd notification-service && mvn spring-boot:run     # port 8085
cd order-service         && mvn spring-boot:run     # port 8082
cd gateway-service       && mvn spring-boot:run     # port 8080  (start last)
```

All client traffic should go through the Gateway on port 8080, e.g.:

```bash
# List products (through the gateway)
curl http://localhost:8080/api/products

# Generate a Base62 short link for product 1 (Case Study 1)
curl -X POST http://localhost:8080/api/products/1/share

# Follow the short link (redirects to the product)
curl -i http://localhost:8080/r/1

# Checkout / place an order (Case Study 3, exercises Order->Payment->Inventory)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2,"unitPrice":79.99}'

# Enqueue notifications with different priorities and watch them dispatch
# in priority order, not arrival order (Case Study 2)
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"priority":5,"channel":"EMAIL","recipient":"user@orbit.com","message":"Flash sale!"}'
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"priority":1,"channel":"PUSH","recipient":"user@orbit.com","message":"Payment failed"}'
curl http://localhost:8080/api/notifications/sent   # "Payment failed" should appear first

# Demo the circuit breaker (Case Study 3 bottleneck analysis):
curl -X POST http://localhost:8080/api/payments/chaos/enable
# now place several orders back-to-back and watch order-service's logs -
# after enough failures the breaker trips OPEN and fails fast instead of
# hanging for 6s per request
curl -X POST http://localhost:8080/api/payments/chaos/disable

# Demo the rate limiter (Case Study 4): fire >20 requests quickly and watch
# HTTP 429 responses start appearing once the bucket empties
for i in $(seq 1 30); do curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/products; done
```

## Optional: Full Infrastructure (Postgres / Redis / Kafka)

```bash
docker compose up -d
```

Then start `order-service` and `notification-service` with the `kafka`
profile to enable real event publishing/consuming:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=kafka
```

Swapping H2 → Postgres and the in-memory cache → Redis is left as a
trainee exercise (this is intentional — it mirrors the "stretch goal"
exercises in the Day 24 lab: moving the Token Bucket and cache state into
Redis so they work correctly across multiple replicas).

## What's intentionally left as an exercise

- Redis-backed Token Bucket (currently in-memory per Gateway instance)
- Redis-backed notification idempotency store (currently an in-memory `Set`)
- Postgres/MongoDB wiring (currently H2 for portability)
- Full Kafka DTO (de)serialization (currently a raw `toString()` payload)
- Circuit breakers on the Inventory client (currently only on Payment)

These gaps are deliberate — they give you built-in "stretch goal" labs for
Day 24 and Day 25 without needing to invent new ones.
