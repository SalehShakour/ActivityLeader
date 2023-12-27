# ActivityLeader

## Project Description

The **ActivityLeader** is a comprehensive leaderboard application designed to track user activities, enforce rate limiting on user requests from any IP, and dynamically display a scalable leaderboard based on user scores. This Spring Boot project leverages the power of the Redis and Bucket algorithm (Bucket4j) for efficient rate limiting, JWT for secure authentication, and PostgreSQL for robust user information storage.

## Features

### 1. User Activities Generation

The application intelligently generates diverse user activities, allowing users to accumulate scores based on their interactions with the system. This feature promotes engagement and ensures that the leaderboard reflects dynamic and real-time user contributions.

### 2. Rate Limiting with Redis and Bucket4j

To maintain fair usage and prevent abuse, the application incorporates the Redis and Bucket4j algorithm for rate limiting. This ensures that users are allowed a controlled number of requests, preventing performance degradation and ensuring a smooth experience for all users.

### 3. Scalable Leaderboard Panel

The leaderboard panel is designed to scale seamlessly as the user base grows. It dynamically updates based on user scores, providing an instant snapshot of the top performers. This scalability ensures that the application can handle a large number of users and activities without compromising performance.

### 4. JWT Authentication

Security is paramount, and the application employs JSON Web Tokens (JWT) for authentication. Users are required to authenticate securely, ensuring that only authorized users can access the system and contribute to the leaderboard.

### 5. PostgreSQL for User Information Storage

User information, including scores and other relevant details, is securely stored in a PostgreSQL database. This relational database provides a robust and structured storage solution, enabling efficient retrieval of user data for leaderboard generation.

## Implementation

The project is implemented using Spring Boot and integrates the following technologies:

- **Spring Boot:** The fundamental framework for building the application.
- **Redis and Bucket4j:** Implementing an effective rate-limiting mechanism.
- **JWT (JSON Web Token):** Ensuring secure authentication for users.
- **PostgreSQL:** A reliable relational database used to store and manage user information.


