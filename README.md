# E-Commerce REST API

A Spring Boot REST API for an e-commerce store, with JWT authentication, role-based access (USER / ADMIN), a database-backed shopping cart, and order management.

**Frontend for this API:** [ecommerce-frontend](https://github.com/Mohamed-Mahroof/ecommerce-frontend) (React + Vite)

<!-- Add after deployment -->
<!-- **Live API:** https://your-railway-url -->
<!-- **Live app:** https://your-vercel-url -->

---

## Features

- **Authentication:** register and login with JWT (stateless, no sessions)
- **Password security:** BCrypt hashing; the password hash is never returned in any response (`@JsonIgnore`)
- **Role-based access:** `USER` and `ADMIN`, enforced with Spring Security and `@PreAuthorize`
- **Products:** CRUD (admin only for writes), search by name, filter by category, product images via `imageUrl`
- **Cart:** persisted in the database per user (add, update quantity, remove, clear)
- **Orders:** place an order, view personal order history, cancel a placed order
- **Request DTOs:** separate `LoginRequest` and `RegisterRequest` classes instead of exposing the `User` entity
- **CORS** configured for the frontend dev server
- Transactional service methods for cart and order operations

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.x |
| Security | Spring Security, JWT, BCrypt |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL |
| Build tool | Maven |
| Testing | Postman |

## Architecture

```
src/main/java/com/example/ecommerce_api/
├── controller/    AuthController, ProductController, CartController, OrderController
├── service/       AuthService, ProductService, CartService, OrderService
├── repository/    UserRepository, ProductRepository, CartRepository, CartItemRepository,
│                  OrderRepository, OrderItemRepository
├── model/         User, Product, Cart, CartItem, Order, OrderItem
├── dto/           LoginRequest, RegisterRequest
└── security/      JwtService, JwtAuthFilter, SecurityConfig, UserDetailsServiceImpl
```

**Request flow:** `Controller → Service → Repository → MySQL`. Every protected request passes through `JwtAuthFilter`, which validates the token and loads the user before the controller runs.

### Database tables

`users`, `products`, `orders`, `order_items`, `carts`, `cart_items`

Key relationships: `User 1—1 Cart`, `User 1—* Order`, `Cart 1—* CartItem`, `Order 1—* OrderItem`, `CartItem/OrderItem *—1 Product`.

---

## API Endpoints

### Auth (public)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user (always created as `USER`) |
| POST | `/api/auth/login` | Log in and receive a JWT |

**Register request**
```json
{ "name": "Jane", "email": "jane@example.com", "password": "secret123" }
```

**Login request / response**
```json
{ "email": "jane@example.com", "password": "secret123" }
```
```json
{ "token": "eyJhbGciOi..." }
```

Send the token on protected requests: `Authorization: Bearer <token>`

### Products

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/products` | Authenticated | List products |
| GET | `/api/products/{id}` | Authenticated | Get one product |
| GET | `/api/products/search?name=` | Authenticated | Search by name (case-insensitive) |
| GET | `/api/products/category/{category}` | Authenticated | Filter by category |
| POST | `/api/products` | ADMIN | Add a product |
| PUT | `/api/products/{id}` | ADMIN | Update a product |
| DELETE | `/api/products/{id}` | ADMIN | Remove a product |

### Cart (Authenticated)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/cart` | Get the current user's cart |
| POST | `/api/cart/add` | Add an item: `{ "productId": 1, "quantity": 2 }` |
| PUT | `/api/cart/{cartItemId}` | Update quantity: `{ "quantity": 3 }` |
| DELETE | `/api/cart/{cartItemId}` | Remove one item |
| DELETE | `/api/cart` | Clear the cart |

The user is always taken from the JWT, so one user can never read or change another user's cart.

### Orders (Authenticated)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Place an order from the cart items |
| GET | `/api/orders/my` | Current user's order history |
| PUT | `/api/orders/{id}/cancel` | Cancel an order (status `Placed` → `Cancelled`) |

Only the owner of an order can cancel it, and only orders with status `Placed` can be cancelled.

---

## Getting Started

### Prerequisites

- Java 21
- Maven
- MySQL 8+

### 1. Clone the repository

```bash
git clone https://github.com/Mohamed-Mahroof/ecommerce-api.git
cd ecommerce-api
```

### 2. Create the database

```sql
CREATE DATABASE ecommercedb;
```

### 3. Configure `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommercedb
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

Set `DB_USERNAME` and `DB_PASSWORD` as environment variables instead of committing credentials. Use `ddl-auto=update` so your data is kept between restarts.

### 4. Run the application

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

### 5. Create an admin user

Registration always creates a `USER`. Register normally, then promote the account in MySQL:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

Log in again afterwards so the new role is included in your token.

### 6. Run the frontend

Start the API first, then follow the setup steps in the [frontend repository](https://github.com/Mohamed-Mahroof/ecommerce-frontend). The frontend is allowed through CORS at `http://localhost:5173`.

---

## Screenshots

<!-- Add screenshots to a /screenshots folder and reference them here -->

| Postman: login | Postman: add to cart |
|---|---|
| _screenshot_ | _screenshot_ |

---

## Roadmap

- [ ] Soft delete for products, so products with existing orders can be removed from the storefront without breaking order history
- [ ] Global exception handling with proper HTTP status codes
- [ ] Request validation (`@Valid`)
- [ ] Deploy to Railway

## Author

**Mohamed Mahroof**
GitHub: [@Mohamed-Mahroof](https://github.com/Mohamed-Mahroof)
