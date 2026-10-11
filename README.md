# Farm Agri API

Spring Boot backend for a farmer-to-buyer marketplace. It includes product listings, cart and wishlist operations, buyer/seller order workflows, Razorpay payments, image uploads, weather, mandi prices, and email notifications.

## Tech stack

- Java 17 and Spring Boot 3
- MySQL 8
- Razorpay and Cloudinary
- OpenAPI/Swagger and Spring Boot Actuator
- Docker and Docker Compose

## Local setup

1. Install Java 17 and Docker Desktop.
2. Copy `.env.example` to `.env` and set your own credentials.
3. Create or import the existing `PRODUCTMANAGEMENT` and `USER` schemas.
4. Run `docker compose up --build`.
5. Open Swagger UI at `http://localhost:8085/docs`.

Health is available at `GET /actuator/health`. The API defaults to port `8085`. Set `CORS_ALLOWED_ORIGINS` to the deployed frontend URL in production.

## Main API areas

| Area | Base path |
| --- | --- |
| Products | `/product` |
| Cart | `/api/cart` |
| Wishlist | `/api/wishlist` |
| Orders | `/api/orders` |
| Payments | `/api/payment` |
| Farmers/users | `/farmers`, `/user` |
| Prices/weather | `/price`, `/weather` |

## Deployment

The `Dockerfile` can be deployed to Render, Railway, Fly.io, or another container host. Provision managed MySQL, configure every value from `.env.example` in the host's secret manager, set `CORS_ALLOWED_ORIGINS` to the frontend domain, and deploy the container. Never commit `.env` or real credentials.


