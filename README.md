# Sports Betting Settlement

A sports betting platform that demonstrates a hybrid messaging architecture using both **Apache Kafka** and **Apache RocketMQ** within a single Spring Boot application.

## Overview

When an event outcome is published, the system fans out bet settlement messages across two different brokers:

1. The event outcome is published to **Kafka**.
2. Each worker resolves which bets are affected and publishes individual settlement messages to **RocketMQ**.
3. A RocketMQ consumer receives each settlement and atomically updates the bet status in the database.

## Project Structure

```
src/main/java/com/betting/
├── BettingApplication.java
├── api/
│   ├── controller/
│   │   ├── BetController.java              # GET /api/v1/bet
│   │   └── EventOutcomeController.java     # POST /api/v1/eventOutcomes
│   └── model/
│       ├── BetResponse.java                # Response DTO returned by BetController
│       └── EventOutcomeRequest.java        # Request DTO with validation
├── config/
│   ├── GlobalExceptionHandler.java         # Maps exceptions to HTTP status codes
│   └── KafkaConfig.java                    # Topic definitions
├── data/
│   ├── model/
│   │   ├── Bet.java                        # Bet entity (PENDING → WON/LOST)
│   │   ├── BetStatus.java                  # Enum: PENDING, WON, LOST
│   │   └── Event.java                      # Event entity
│   └── repository/
│       ├── BetRepository.java
│       └── EventRepository.java
├── exception/
│   └── BrokerException.java
├── mapper/
│   └── BetMapper.java                      # MapStruct: Bet → BetDto → BetResponse
├── messaging/
│   ├── consumer/
│   │   ├── BetSettlementConsumer.java      # RocketMQ listener
│   │   └── EventOutcomeConsumer.java       # Kafka listener (6 concurrent threads)
│   ├── model/
│   │   ├── BetSettlementMessage.java
│   │   ├── EventOutcomeMessage.java
│   │   └── MessageTopics.java
│   └── producer/
│       ├── BetSettlementProducer.java
│       ├── EventOutcomeProducer.java
│       ├── KafkaEventOutcomeProducer.java
│       └── RocketMqBetSettlementProducer.java
└── service/
    ├── model/
    │   └── BetDto.java                     # Service layer DTO returned by BetService
    ├── BetService.java                     # Resolves affected bets and sends settlements
    ├── BetSettlementService.java           # Atomically settles a single bet
    └── EventOutcomeService.java            # Idempotency check + publishes event outcome
```

## Running the Project

### Prerequisites

- Docker and Docker Compose

### Start

```bash
docker compose up --build
```

This starts four containers:

| Container           | Image                   | Port  |
|---------------------|-------------------------|-------|
| Kafka               | apache/kafka            | 9092  |
| RocketMQ NameServer | apache/rocketmq:5.3.2   | 9876  |
| RocketMQ Broker     | apache/rocketmq:5.3.2   | 10911 |
| App                 | (built from Dockerfile) | 8080  |

#### Start without App

If you want to run the application outside Docker (to IDE Debugging, for instance)

```bash
docker compose up kafka rocketmq-namesrv rocketmq-broker
```

### Usage

**Publish an event outcome:**

```bash
curl -X POST http://localhost:8080/api/v1/eventOutcomes \
  -H "Content-Type: application/json" \
  -d '{"eventId": 1, "eventName": "Match A vs B", "eventWinnerId": 2}'
```

**List all bets:**

```bash
curl http://localhost:8080/api/v1/bet
```

The database is pre-seeded with 5 bets on startup (`data.sql`):

| ID | User | Event | Market | Picked Winner | Amount | Status  |
|----|------|-------|--------|---------------|--------|---------|
| 1  | 1    | 1     | 1      | 1             | 100    | PENDING |
| 2  | 2    | 2     | 1      | 1             | 200    | PENDING |
| 3  | 2    | 2     | 1      | 2             | 300    | PENDING |
| 4  | 3    | 2     | 1      | 2             | 400    | PENDING |
| 5  | 4    | 3     | 1      | 1             | 500    | PENDING |

For example, posting `eventId=2` with `eventWinnerId=2` will settle bets 3 and 4 as **WON** and bet 2 as **LOST**.

**H2 Console** (development): http://localhost:8080/h2-console
JDBC URL: `jdbc:h2:mem:bettingdb`

### Stop

```bash
docker compose down
```

## Running Tests

```bash
mvn test
```

## Technical Decisions

### Atomic settlement query

Bet status updates use a targeted `UPDATE ... WHERE status = PENDING` query rather than a read-modify-write cycle. This prevents double-settlement if a message is delivered more
than once (e.g., due to a broker retry), making the consumer effectively idempotent.

### Idempotent event outcome processing

`EventOutcomeService` checks whether an event already has a winner recorded before publishing to Kafka. This prevents duplicate fan-outs if the same outcome is posted more than
once.

### Prioritizing reliability

The Kafka and RocketMQ producers block until the broker acknowledges the message. This lets errors propagate back through the service layer so the API can return the appropriate
HTTP status code instead of silently losing messages.

### Isolating messaging technologies

Services that contain business logic are decoupled from Kafka and RocketMQ SDK classes via producer/consumer interfaces. This allows the messaging implementation to change without
touching business logic.

### Isolating system layers

Each layer has its own models. Schema changes in one layer only propagate to adjacent layers where explicitly mapped, preventing accidental coupling.

### Evolutions

- Non-blocking producers error handling
- Retry tuning
- Send messages in batches to improve performance
- Instrumentalization
