# 🏡 AirBnB Clone – Spring Boot REST API

A RESTful backend service for an **AirBnB-like accommodation booking platform**, built using **Spring Boot**.  
This API handles users, hotels, bookings, inventories, and availability with secure authentication and role-based access.

---

## 📌 Features

- User authentication (JWT Based)
- Role-based authorization (GUEST, HOTEL_MANAGER)
- Hotel and Rooms listing management
- Booking & inventory management
- Dynamic pricings (Strategy Design Pattern)
- Payment gateway integration with webhook
- Schedular to automate inventory add to system for 1 year
- RESTful APIs with Swagger documentation

---

## 🛠 Tech Stack

- Java 17  
- Spring Boot 3  
- Spring Web  
- Spring Data JPA (Hibernate)  
- Spring Security + JWT  
- PostgreSQL
- Stripe Payment Gateway
- Maven  
- Swagger (OpenAPI)  
- Lombok
- ModelMapper

---

## 📐 System Requirements 
<img width="1611" height="3939" alt="requirements" src="https://github.com/user-attachments/assets/0c627286-3bfc-4a8d-bad2-7fed03b5b101" />

---

## 🗄 Database Design / ERD
![ERD](https://github.com/user-attachments/assets/4d0a70c5-7598-4ca1-9e0b-6a287fcf7f5d)
<img width="894" height="828" alt="airbnb_clone - db digram" src="https://github.com/user-attachments/assets/1dcde017-b165-4fc8-9cb0-10173469b735" />

---

## 📡 API Documentation (Swagger)

Swagger UI is available at: http://localhost:8080/api/v1/swagger.html#/

<img width="1366" height="5111" alt="screencapture-localhost-8080-api-v1-swagger-ui-index-html-2026-01-17-19_54_59" src="https://github.com/user-attachments/assets/13da1010-5994-4409-877b-182358ab156d" />

---
## 🔄 Sequence Diagram

### Authentication flow

<img width="1500" height="516" alt="image" src="https://github.com/user-attachments/assets/9b06e824-ef16-4d83-91eb-f9c921447eaa" />

### Initialize room for year

<img width="622" height="305" alt="image" src="https://github.com/user-attachments/assets/fb42495f-83be-445d-9adb-7bb30e3ca572" />

### Get all inventories by room

<img width="833" height="504" alt="image" src="https://github.com/user-attachments/assets/51b7d256-f06a-4117-92a8-de754587adb7" />

### Initiate Booking

<img width="976" height="380" alt="image" src="https://github.com/user-attachments/assets/45cb4e76-dbfc-46d4-aeee-5ca1b3fa54be" />

### Initiate payment

<img width="755" height="333" alt="image" src="https://github.com/user-attachments/assets/d0eda668-9c33-45b0-b892-7bc4ef0f377d" />

### Capture payment

<img width="727" height="310" alt="image" src="https://github.com/user-attachments/assets/b7bb0f68-6fe4-4db3-ba55-e0845b26c45e" />

### Cancel payment

<img width="691" height="327" alt="image" src="https://github.com/user-attachments/assets/5ead838b-7152-4db6-88c8-475a36138a84" />

### Payment Webhook

<img width="830" height="426" alt="image" src="https://github.com/user-attachments/assets/8a60747e-b747-4510-b594-01990815c53d" />

### Schedular for inventories

<img width="561" height="382" alt="image" src="https://github.com/user-attachments/assets/1b2d401a-c94c-49d9-ba23-aac132728aac" />

### Dynamic Pricing

<img width="1111" height="512" alt="image" src="https://github.com/user-attachments/assets/74469ac5-6403-49f2-a5dc-b8d86f82e534" />


