# PayLedger: Digital Wallet & Ledger API

Spring Boot REST API for a digital wallet with double-entry ledger, idempotent transfers
and concurrency-safe balance updates.

**Live demo:** <link baad mein> | First load may take ~30s (free hosting)

## Features
- JWT authentication (register, login, BCrypt password hashing)
- Wallet auto-created on signup
- Deposit and wallet-to-wallet transfer with `@Transactional`
- Idempotency-Key header: duplicate requests never move money twice
- Double-entry ledger (every transfer = one DEBIT + one CREDIT entry)
- Paginated transaction history
- Global exception handler with consistent JSON errors

## Tech stack
Java 17, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, Maven, Docker, JUnit

## Architecture
Security filter -> Controller -> Service -> Repository -> PostgreSQL
(diagram in docs/)

## Concurrency design
50 concurrent transfers from one wallet:
- Optimistic locking (@Version) with retries: 21/50 succeeded
- Pessimistic locking (SELECT FOR UPDATE), wallets locked in a fixed order to avoid deadlocks: 50/50 succeeded
- Balance stayed consistent in both runs

(screenshots)

## API
| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Register, returns JWT |
| POST | /api/auth/login | Login, returns JWT |
| GET | /api/wallet | My wallet balance |
| POST | /api/wallet/deposit | Deposit (Idempotency-Key header) |
| POST | /api/transfers | Transfer (Idempotency-Key header) |
| GET | /api/transactions?page=0&size=10 | Transaction history |

## Run locally
1. `docker run --name payledger-db -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=payledger -p 5433:5432 -d postgres:16`
2. `./mvnw spring-boot:run`
3. Production: set DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET environment variables.

## Author
Shreyash Gawande | GitHub | LinkedIn
