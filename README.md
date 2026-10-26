# Thrift Store Marketplace

A small second-hand clothing shop where signed-in shoppers browse a catalogue, keep a basket, and place an order, and admins manage the products and those orders.

The app is a Spring Boot web application. Pages are server-rendered with Thymeleaf. Accounts, products, and orders live in a local H2 file database. The basket lives in the HTTP session, not in the database.

## Core functionality

**Shoppers**

- Register and log in with an email and a BCrypt-hashed password.
- Browse the home page and the catalogue. Home shows the three best-selling visible products, or the first three visible products when nothing has sold yet.
- Open a product page with title, material, size, condition, price in euro, description, photo, and three other visible recommendations.
- Add a visible product to the basket, then change quantities or remove lines. Shipping is a flat €2 whenever the basket has items.
- Check out. The app saves an order numbered `0001`, `0002`, and so on, marks it **Paid**, and clears the basket. There is no card payment step.
- Review past orders on `/orders`.

**Admins**

- Register and log in on the same forms, choosing the admin role. An email cannot belong to both a shopper and an admin.
- Open a dashboard, create products, edit them, show or hide them, and delete them. Deleting a product keeps past order lines and drops the product link.
- List every order and move it between **Paid**, **Shipped**, and **Delivered**.

Browsing, the basket, checkout, orders, and the admin area all require a session. `/login` and `/register` are public. A shopper who opens an admin URL is sent home, and an admin cannot use the basket or checkout.

On startup, each file in `src/main/resources/static/images/` becomes a visible product if that filename is not already stored: title from the filename, size M, condition Good, material "Mixed Fabric", price €25.00.

## How to run

You need JDK 24. The repo includes the Maven Wrapper, so a separate Maven install is optional.

```bash
git clone https://github.com/kenechukz/Thrift-Store-Marketplace.git
cd Thrift-Store-Marketplace
./mvnw spring-boot:run
```

On Windows:

```bash
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). Register a shopper or an admin, then sign in. The server listens on port 8080.

The database file is created at `~/marketplace/data/23318136DB`. Hibernate updates the schema on startup (`ddl-auto=update`), and `schema.sql` ensures the Spring Session tables exist. Sessions last 30 minutes and are stored in that same H2 database.

The H2 console is on at [http://localhost:8080/h2-console](http://localhost:8080/h2-console).

| Field | Value |
| --- | --- |
| JDBC URL | `jdbc:h2:file:~/marketplace/data/23318136DB` |
| User | `sa` |
| Password | empty |

Run the context-load test with:

```bash
./mvnw test
```

## Core components

| Piece | Role |
| --- | --- |
| `MarketplaceApplication` | Starts Spring Boot and turns on JDBC-backed HTTP sessions. |
| `controller/AuthController` | Register, login, and logout for shoppers and admins. |
| `controller/BrowseController` | Home, catalogue, product page, and add-to-basket. |
| `controller/CheckoutController` | Basket page, quantity changes, and checkout. |
| `controller/OrdersController` | A shopper's own orders. |
| `controller/AdminController` | Product and order management pages. |
| `service/AuthService`, `AdminAuthService` | Look up a shopper or admin by email and check the password with BCrypt (`BCryptPasswordEncoder`). Registration stores the BCrypt hash, not the raw password. |
| `service/AdminProductService` | Create, update, show or hide, and delete products. |
| `service/AdminOrderService` | List orders and change status. |
| `config/AuthInterceptor` | If you are not logged in yet, redirects you to `/login` before home, the catalogue, the basket, checkout, orders, or admin pages. A logged-in shopper who opens an admin URL is sent home, and a logged-in admin is kept off the basket, checkout, and orders. |
| `config/GlobalModelAttributes` | Puts the signed-in role and basket count on every page. |
| `config/ProductImageSeedInitializer` | Creates the starter products from the image files. |
| `model` | `User`, `Admin`, `Product`, `Order`, `OrderItem`, and the session `Basket`. |
| `repository` | Spring Data JPA access to H2. |
| `templates/` | Thymeleaf pages: home, catalogue, product, basket, orders, login, register, and the admin screens. |
| `static/` | `style.css`, basket and auth scripts, and product photos. |

Products record a title, material, size (`XS`–`XL`), condition (from "New with tags" through "Poor"), price, description, image filename, and visibility. Orders are stored as `app_order` with line items in `order_item`.

## Screenshots

The current shopper and admin screens are in [docs/screenshots.md](docs/screenshots.md).
