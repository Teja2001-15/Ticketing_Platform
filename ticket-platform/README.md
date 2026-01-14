# Anti-Scalping Ticket Platform - MVP Monolith

## Overview
A Spring Boot-based anti-scalping ticketing platform designed to prevent ticket fraud and control resale while maintaining user flexibility through trusted circles and community-driven pools.

## Tech Stack
- **Framework**: Spring Boot 3.2.1
- **Database**: PostgreSQL 17
- **Cache**: Redis 7
- **Security**: JWT Authentication with Spring Security
- **API Documentation**: OpenAPI 3 / Swagger
- **QR Code**: Google ZXing
- **Build**: Maven 3.9+
- **Monitoring**: Spring Boot Actuator + Prometheus

## Project Structure
```
ticket-platform/
├── src/main/java/com/antiscalping/tickets/
│   ├── config/          # Spring configuration
│   ├── controllers/      # REST endpoints
│   ├── services/        # Business logic
│   ├── repositories/    # Data access
│   ├── entities/        # JPA entities
│   ├── dto/            # Data transfer objects
│   ├── security/       # Security components
│   ├── exceptions/     # Custom exceptions
│   └── utils/          # Utility classes
├── src/main/resources/
│   ├── db/migration/   # Flyway SQL migrations
│   └── application.yml # Configuration
└── docker-compose.yml  # Docker services
```

## Core Features

### 1. User Management
- Registration and login with JWT authentication
- User profile management
- Account suspension for fraud detection
- Audit logging

### 2. Event Management
- Create and manage events
- Event listing and filtering
- Venue-based search
- Upcoming events view

### 3. Ticket System
- Purchase tickets with quantity limits
- QR code generation (dynamic, refreshes every 60s)
- Ticket validation at venue entry
- Ticket cancellation and refunds
- Transfer counting for fraud detection

### 4. Ticket Transfers
- **Trusted Circle Transfer**: Pre-approved users
- **Controlled Transfer**: With fee cap (30% max)
- Price anomaly detection
- Multi-transfer limits

### 5. Community Pool
- Add tickets to community pool
- 15-minute nomination window
- Democratic claiming process
- Prevents forced resale

### 6. Security & Fraud Detection
- Transfer velocity checks
- Price anomaly detection
- Audit logging for all actions
- Pattern-based fraud detection

### 7. Payment Integration
- Support for Stripe/Razorpay
- Test mode for development
- Payment status tracking
- Refund processing

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login

### Events
- `GET /api/v1/events` - List all events
- `GET /api/v1/events/upcoming` - Upcoming events
- `POST /api/v1/events` - Create event (admin)
- `GET /api/v1/events/{id}` - Event details
- `GET /api/v1/events/search/venue?venue=name` - Search by venue

### Tickets
- `POST /api/v1/tickets/purchase` - Purchase tickets
- `GET /api/v1/tickets/my-tickets` - User's tickets
- `GET /api/v1/tickets/{id}` - Ticket details
- `POST /api/v1/tickets/{id}/validate` - Validate at entry
- `POST /api/v1/tickets/{id}/cancel` - Cancel ticket

### Transfers
- `POST /api/v1/transfers/create` - Initiate transfer
- `POST /api/v1/transfers/{id}/approve` - Approve transfer
- `POST /api/v1/transfers/{id}/complete` - Complete transfer
- `POST /api/v1/transfers/{id}/reject` - Reject transfer

### Trusted Circle
- `POST /api/v1/trusted-circle/add/{userId}` - Add user to trusted circle
- `DELETE /api/v1/trusted-circle/remove/{userId}` - Remove user
- `GET /api/v1/trusted-circle` - View trusted circle

### Pool
- `POST /api/v1/pool/{ticketId}/add` - Add ticket to pool
- `POST /api/v1/pool/{poolTicketId}/nominate/{userId}` - Nominate user
- `POST /api/v1/pool/{poolTicketId}/claim` - Claim ticket
- `GET /api/v1/pool/{eventId}` - View pool tickets

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker & Docker Compose

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ticket-platform
   ```

2. **Start services with Docker**
   ```bash
   docker-compose up -d
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - API: `http://localhost:8080/api/v1`
   - Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
   - API Docs: `http://localhost:8080/api/v1/v3/api-docs`
   - Actuator: `http://localhost:8080/api/v1/actuator/health`
   - PgAdmin: `http://localhost:5050`

## Configuration

Edit `src/main/resources/application.yml` for:
- Database connection
- Redis configuration
- JWT secret and expiration
- Logging levels
- Server port

## Database Migrations

Flyway automatically runs migrations in `src/main/resources/db/migration/`

To create a new migration:
```bash
# Create V{N+1}__{description}.sql in db/migration/
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

## Development Notes

### Adding New Features
1. Create entity in `entities/`
2. Create repository in `repositories/`
3. Create service in `services/`
4. Create controller in `controllers/`
5. Create DTOs in `dto/`
6. Add tests in `src/test/`

### Security
- All endpoints except `/auth/**` require JWT token
- Pass token in header: `Authorization: Bearer <token>`
- Tokens expire after 24 hours (configurable)

### Logging
- Use `@Slf4j` annotation for logging
- All user actions are logged to `audit_logs` table
- Log level set to DEBUG for application code

## Performance Considerations

- Database indexes on frequently queried columns
- Redis caching for QR codes (60-second TTL)
- Connection pooling (HikariCP)
- Lazy loading for JPA relationships
- Batch inserts/updates configured

## Deployment

### Production Checklist
- [ ] Change JWT secret in application.yml
- [ ] Set appropriate database pool sizes
- [ ] Configure Redis cluster for high availability
- [ ] Enable HTTPS
- [ ] Configure CORS for your domain
- [ ] Set up monitoring and alerts
- [ ] Configure automatic backups
- [ ] Use environment variables for secrets

### Docker Build
```bash
mvn clean package
docker build -t ticket-platform:1.0.0 .
```

## Monitoring

- Health check: `/api/v1/actuator/health`
- Metrics: `/api/v1/actuator/metrics`
- Prometheus: `/api/v1/actuator/prometheus`

## Known Limitations & Phase 2 Enhancements

- SMS notifications (Phase 2)
- Advanced analytics dashboard
- Mobile app
- Dynamic pricing
- Season passes
- Group tickets
- VIP tiers

## Support & Contact

For issues and feature requests, please create a GitHub issue.

## License

Proprietary - All Rights Reserved
