# Anti-Scalping Ticket Platform - MVP Implementation

## Project Overview

This is a complete implementation of the Anti-Scalping Ticket Platform MVP as specified in the design document. The project is built as a monolithic Spring Boot application with PostgreSQL database and Redis caching.

## Directory Structure

```
Ticketing_Platform/
├── ticket-platform/          # Main Spring Boot application
│   ├── src/main/java/       # Source code
│   ├── src/main/resources/  # Configuration and migrations
│   ├── src/test/            # Unit tests
│   ├── pom.xml              # Maven configuration
│   ├── docker-compose.yml   # Docker services
│   └── README.md            # Application documentation
└── Anti-Scalping Ticket Platform - MVP Monolith Guide 2.pdf  # Design document
```

## Quick Start

### 1. Prerequisites
- Java 17 or higher
- Maven 3.9+
- Docker & Docker Compose
- Git

### 2. Setup & Run

```bash
# Navigate to project directory
cd ticket-platform

# Start PostgreSQL and Redis
docker-compose up -d

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### 3. Access Points

| Component | URL | Credentials |
|-----------|-----|-------------|
| API | http://localhost:8080/api/v1 | - |
| Swagger UI | http://localhost:8080/api/v1/swagger-ui.html | - |
| Health Check | http://localhost:8080/api/v1/actuator/health | - |
| PgAdmin | http://localhost:5050 | admin@example.com / admin |

## Key Features Implemented

✅ **User Management**
- User registration and authentication
- JWT-based authorization
- Audit logging

✅ **Event Management**
- Create and manage events
- List and search events
- Event capacity management

✅ **Ticket System**
- Purchase tickets with limits
- QR code generation (dynamic)
- Ticket validation and cancellation
- Transfer tracking and limits

✅ **Ticket Transfers**
- Trusted circle transfers
- Controlled transfers with fee caps
- Transfer approval workflow
- Fraud detection

✅ **Community Pool**
- Add tickets to pool
- Nominate users (15-min window)
- Claim unclaimed tickets
- Prevention of forced resale

✅ **Security**
- Transfer velocity checks
- Price anomaly detection
- Comprehensive audit logging
- Password hashing

✅ **Infrastructure**
- PostgreSQL with Flyway migrations
- Redis caching
- Docker containerization
- Swagger API documentation
- Spring Boot Actuator monitoring

## API Endpoints Overview

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login and get JWT token

### Events
- `GET /events` - List all events
- `POST /events` - Create event
- `GET /events/{id}` - Get event details
- `GET /events/upcoming` - List upcoming events

### Tickets
- `POST /tickets/purchase` - Buy tickets
- `GET /tickets/my-tickets` - View owned tickets
- `POST /tickets/{id}/validate` - Validate ticket
- `POST /tickets/{id}/cancel` - Cancel ticket

### Transfers
- `POST /transfers/create` - Initiate transfer
- `POST /transfers/{id}/approve` - Approve transfer
- `POST /transfers/{id}/complete` - Complete transfer
- `POST /transfers/{id}/reject` - Reject transfer

### Pool
- `POST /pool/{ticketId}/add` - Add to pool
- `POST /pool/{poolTicketId}/nominate/{userId}` - Nominate user
- `POST /pool/{poolTicketId}/claim` - Claim ticket
- `GET /pool/{eventId}` - View pool tickets

### Trusted Circle
- `POST /trusted-circle/add/{userId}` - Add trusted user
- `DELETE /trusted-circle/remove/{userId}` - Remove trusted user
- `GET /trusted-circle` - View trusted circle

## Project Statistics

| Component | Count |
|-----------|-------|
| Entities | 8 |
| Services | 8 |
| Controllers | 6 |
| DTOs | 11 |
| Repositories | 8 |
| Migrations | 1 |
| Total Classes | 70+ |

## Database Schema

8 main tables:
- `users` - User accounts and profiles
- `events` - Event information
- `tickets` - Individual tickets
- `ticket_transfers` - Transfer records
- `trusted_circles` - User relationships
- `pool_tickets` - Community pool
- `payments` - Payment records
- `audit_logs` - Activity logs

## Security Features

- JWT authentication (24-hour expiry)
- Password hashing (SHA-256)
- CORS configuration
- Input validation
- Exception handling
- Audit logging
- Fraud detection

## Configuration

### Environment Variables
Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ticket_platform
    username: postgres
    password: postgres
  data:
    redis:
      host: localhost
      port: 6379
```

### Default Credentials
- Database: `postgres` / `postgres`
- PgAdmin: `admin@example.com` / `admin`

## Development & Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=UserServiceTest

# Build with tests
mvn clean install

# Skip tests
mvn clean install -DskipTests
```

## Docker Commands

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# Restart specific service
docker-compose restart postgres
```

## Next Steps / Phase 2 Enhancements

- SMS notifications
- Mobile application (iOS/Android)
- Advanced analytics dashboard
- Dynamic pricing
- Season passes
- Group ticket discounts
- VIP membership tiers
- Real-time websocket updates
- Microservices migration

## Troubleshooting

### Port Already in Use
```bash
# Change port in application.yml
server:
  port: 8081
```

### Database Connection Issues
```bash
# Check Docker status
docker-compose ps

# View container logs
docker-compose logs postgres
```

### Maven Build Issues
```bash
# Clean cache
mvn clean

# Update dependencies
mvn dependency:resolve

# Skip tests
mvn clean install -DskipTests
```

## Code Quality & Standards

- Spring Boot best practices
- SOLID principles
- Clean code architecture
- Comprehensive exception handling
- Logging with SLF4J
- Lombok for boilerplate reduction
- JPA best practices
- RESTful API design

## Documentation

Full API documentation available at:
- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/api/v1/v3/api-docs`

## Performance Metrics

- Database connection pooling (HikariCP)
- Redis caching layer
- JPA query optimization
- Index optimization
- Lazy loading for associations
- Batch operations support

## Deployment

### Production Build
```bash
mvn clean package -DskipTests

# Run JAR
java -jar target/ticket-platform-1.0.0-MVP.jar
```

### Docker Image
```bash
docker build -t ticket-platform:latest .
docker run -p 8080:8080 ticket-platform:latest
```

## Support

For questions or issues, refer to:
- Project README: `ticket-platform/README.md`
- API Documentation: Swagger UI
- Design Document: `Anti-Scalping Ticket Platform - MVP Monolith Guide 2.pdf`

---

**Project Status**: MVP Complete ✅
**Last Updated**: January 2026
**Version**: 1.0.0-MVP