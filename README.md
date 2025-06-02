
# IoTBay

This is a Java web application for the **IoTBay project**, developed as part of the UTS subject **41025 - Introduction to Software Development**. It follows the **Model-View-Controller (MVC)** architecture using **JSP**, **Servlets**, and **JavaBeans**, and is deployed using the **GlassFish Server** via **NetBeans IDE**.

---

## ✨ Key Features Implemented

The application supports the following core functionalities:

1. **Online User Access Management**  
   - Registration, login, logout, and session control  
   - Admin and customer account roles  
   - Secure user authentication with validation

2. **IoT Device Catalogue Management**  
   - View and search IoT device listings  
   - Admins can add, update, or delete products  
   - Categorized browsing and filtering

3. **Order Management**  
   - Customers can add items to cart and place orders  
   - Order history and status tracking  
   - Admins can view and manage all orders

4. **Payment Management**  
   - Payment form with validation  
   - Simulated payment confirmation  
   - View payment history for orders

---

## 📽️ Demo Video

▶️ [Watch Demo on Google Drive] (https://drive.google.com/file/d/1oDDZo-kvfzsfUJmYvANj-1lC1tiWUvpo/view?usp=sharing)

---

## 👥 Team - Contributors

- **Yadu Pillai** - 24966285  
- **Allanah Wadey** - 24758688  
- **Kimleng Thai** - 24577494  
- **Faren Susanto** - 14621609

---

## ⚙️ Dependencies & Environment

- **Java JDK 17**  
- **NetBeans IDE 25**  
  [Download NetBeans](https://netbeans.apache.org/front/main/download/)  
- **Eclipse GlassFish 7.0.23**, Jakarta EE Web Profile 10  
  [Download GlassFish](https://glassfish.org/download)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/ISD_GRP.git
```

---

### 2. Open the Project in NetBeans

- Launch **NetBeans IDE**  
- Go to **File > Open Project**  
- Navigate to the cloned `ISD_GRP` folder and open it  
- NetBeans should detect and load the project automatically

---

### 3. Setup GlassFish Server

- Open the **Services** tab  
- Expand **Servers**  
- Right-click → **Add Server**  
- Select **GlassFish Server**  
- Browse to the GlassFish installation directory  
- Click **Next > Finish**

---

### 4. Setup Derby Database

The project uses **Apache Derby (Java DB)** bundled with NetBeans.

#### 4.1 Start Derby Server

- In NetBeans, open the **Services** tab → **Databases**  
- Right-click on **Java DB** → **Start Server**

#### 4.2 Create the Database

- Right-click on **Java DB** → **Create Database**  
- Enter the following details:

  ```
  Database Name: iotbay
  User Name: iotbay
  Password: iotbay
  Confirm Password: iotbay
  ```

- Click **OK** to create the database

#### 4.3 Initialize the Database Schema

- To populate tables and sample data:  
  - Locate `src/main/java/com/util/DatabaseInitializer.java`  
  - Right-click the file → **Run File**

#### 4.4 JDBC Connection Info (Reference)

- URL: `jdbc:derby://localhost:1527/iotbay`  
- Username: `iotbay`  
- Password: `iotbay`

---

### 5. Run the Application

- Right-click the project in NetBeans → **Run**  
- NetBeans deploys the app on GlassFish  
- It should open automatically in your browser at:

```
http://localhost:8080/<project_folder_name>
```

---

## 🛠️ Troubleshooting Database Issues

- Ensure Derby Server is running:  
  - In NetBeans, go to **Services > Databases**  
  - Right-click **Java DB** → **Start Server**

- Verify the database connection:  
  - In **Services > Databases**, find `jdbc:derby://localhost:1527/iotbay`  
  - Right-click → **Connect**  
  - Expand connection to check tables like Users, Device, Orders, etc.

- If tables are missing, rerun `DatabaseInitializer.java` as described above.

---
