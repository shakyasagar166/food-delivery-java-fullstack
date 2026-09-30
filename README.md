# 🍔 QuickBite / Zosh Food - Full-Stack Food Delivery Web Application

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.x-61dafb.svg)](https://reactjs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Tailwind CSS](https://img.shields.io/badge/TailwindCSS-3.x-38b2ac.svg)](https://tailwindcss.com/)
[![Razorpay](https://img.shields.io/badge/Razorpay-Payment%20Gateway-02042B.svg)](https://razorpay.com/)

A modern, production-grade **Full-Stack Food Delivery Platform** built with **Spring Boot 3, Spring Security (JWT), MySQL, React.js, Tailwind CSS**, and integrated with **Razorpay & Stripe Payment Gateways**.

---

## 🌟 Key Features

### 👤 Customer Features
- **User Authentication**: Secure JWT-based registration and login with encrypted passwords.
- **Restaurant Discovery**: Browse top-rated restaurants, cuisines, and search favorite food items.
- **Dynamic Menu & Filtering**: Filter by vegetarian, non-vegetarian, seasonal specials, and food categories.
- **Cart & Order Management**: Real-time cart calculations, order placement, and order tracking.
- **Integrated Payments**: Checkout seamlessly via Razorpay and Stripe.
- **Customer Favorites**: Save favorite restaurants and dishes.

### 🏢 Restaurant Owner / Admin Features
- **Restaurant Dashboard**: Manage restaurant profile, opening/closing hours, cuisine types, and photos.
- **Menu Management**: Add, update, or remove food dishes, categories, and ingredients.
- **Order Management**: View incoming live orders, update order status (`PENDING`, `PREPARING`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`).
- **Revenue Analytics**: Track daily and monthly restaurant earnings.

---

## 🏗️ System Architecture

```text
+-----------------------+              +---------------------------+
|     React.js SPA      |  HTTP / REST |   Spring Boot Backend     |
| (Tailwind CSS, Redux) | <----------> | (Spring Security + JWT)   |
|   Port: 3000 / Cloud  |     JSON     |      Port: 5454 / Cloud   |
+-----------------------+              +-------------+-------------+
                                                     |
                                                     | Spring Data JPA / Hibernate
                                                     v
                                       +-------------+-------------+
                                       |      MySQL Database       |
                                       |  (Tables: Users, Orders,  |
                                       |   Restaurants, Items)     |
                                       +---------------------------+
```

---

## 🗂️ Project Structure

```text
food-delivery-java-fullstack/
├── backend-spring boot/          # Spring Boot 3 Backend
│   ├── src/main/java/com/zosh/
│   │   ├── config/               # SecurityFilterChain, JWT & CORS Config
│   │   ├── controller/           # REST API Controllers
│   │   ├── model/                # JPA Entities (User, Order, Restaurant, etc.)
│   │   ├── repository/           # Spring Data JPA Repositories
│   │   ├── service/              # Core Business Logic Services
│   │   └── ZoshFoodApplication.java
│   ├── src/main/resources/
│   │   └── application.properties# Config with Environment Variable support
│   ├── Dockerfile                # Multi-stage Cloud Dockerfile
│   └── pom.xml                   # Maven dependencies
│
├── frontend-react/               # React Frontend SPA
│   ├── src/
│   │   ├── config/api.js         # Dynamic API URL configuration
│   │   ├── State/                # Redux State Management
│   │   ├── customers/            # Customer UI components & pages
│   │   └── Admin/                # Restaurant Owner & Admin Dashboard
│   ├── vercel.json               # SPA routing configuration for Vercel
│   └── package.json
│
├── Zosh Food.postman_collection.json # Complete API Test Collection
├── push_to_github.bat            # One-click GitHub push script
└── README.md
```

---

## 🚀 Quick Start (Local Setup)

### 1. Database Setup
Create a database in your local MySQL instance:
```sql
CREATE DATABASE zosh_food;
```

### 2. Run Backend (Spring Boot)
1. Navigate to backend directory:
   ```bash
   cd "backend-spring boot"
   ```
2. Start the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
   *The backend will start at:* `http://localhost:5454`

### 3. Run Frontend (React)
1. Navigate to frontend directory:
   ```bash
   cd "frontend-react"
   ```
2. Install dependencies & start dev server:
   ```bash
   npm install
   npm start
   ```
   *The frontend will start at:* `http://localhost:3000`

---

## ☁️ Cloud Deployment Guide

### 1. Free Cloud MySQL Setup (Aiven or Clever Cloud)
1. Sign up on [Aiven.io](https://aiven.io/) or [Clever Cloud](https://www.clever-cloud.com/).
2. Create a free **MySQL** database.
3. Note your Database Host, Port, Username, Password, and Database Name.

### 2. Deploy Backend (Render)
1. Create a new **Web Service** on [Render.com](https://render.com/).
2. Connect your GitHub repository: `https://github.com/shakyasagar166/food-delivery-java-fullstack`.
3. Root Directory: `backend-spring boot`
4. Runtime: **Docker** (it automatically uses our multi-stage `Dockerfile`).
5. Add Environment Variables:
   - `DB_HOST`: Your cloud MySQL host
   - `DB_PORT`: Your cloud MySQL port (default: `3306`)
   - `DB_NAME`: Your cloud database name
   - `DB_USERNAME`: Your cloud database username
   - `DB_PASSWORD`: Your cloud database password
   - `PORT`: `5454`
6. Click **Deploy**. Note your live backend URL (e.g. `https://zosh-food-backend.onrender.com`).

### 3. Deploy Frontend (Vercel)
1. Import your repo into [Vercel](https://vercel.com/).
2. Root Directory: `frontend-react`
3. Framework Preset: **Create React App**
4. Environment Variables:
   - `REACT_APP_API_URL`: `https://your-backend-app.onrender.com`
5. Click **Deploy**!

---

## 📜 License
This project is open-source and available under the MIT License.
