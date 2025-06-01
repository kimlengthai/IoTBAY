-- 01 User table
CREATE TABLE Users (
    UserID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- DE-1001
    Name VARCHAR(50) NOT NULL, -- DE-1002
    Email VARCHAR(100) UNIQUE NOT NULL, -- DE-1003
    Password VARCHAR(255) NOT NULL, -- DE-1004
    DateOfBirth DATE, -- DE-1005
    Address VARCHAR(255), -- DE-1006
    Phone VARCHAR(20), -- DE-1007
    UserType VARCHAR(20) NOT NULL CHECK (UserType IN ('Customer', 'Staff')),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 02 Category table
CREATE TABLE Category (
    CategoryID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- DE-2001
    CategoryName VARCHAR(50) NOT NULL, -- DE-2002
    CategoryDescription VARCHAR(255) -- DE-2003
);

-- 03 Device table (IoT devices)
CREATE TABLE Device (
    DeviceID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- DE-3001
    CategoryID INT, -- DE-3002
    DeviceName VARCHAR(100) NOT NULL, -- DE-3003
    Description VARCHAR(500), -- DE-3004
    Price DECIMAL(10, 2) NOT NULL, -- DE-3005
    StockQuantity INT NOT NULL, -- DE-3006
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IsActive BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID)
);

-- 04 Cart table
CREATE TABLE Cart (
    CartID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- DE-4001
    UserID INT NOT NULL, -- DE-4002
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- 05 CartItem table
CREATE TABLE CartItem (
    CartItemID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- DE-5001
    CartID INT NOT NULL, -- DE-5002
    DeviceID INT NOT NULL, -- DE-5003
    Quantity INT NOT NULL, -- DE-5004
    DateAdded TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- DE-5005
    FOREIGN KEY (CartID) REFERENCES Cart(CartID),
    FOREIGN KEY (DeviceID) REFERENCES Device(DeviceID)
);

-- 06 Order table
CREATE TABLE Orders (
    OrderID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- DE-6001
    UserID INT NOT NULL, -- DE-6002
    OrderDate DATE DEFAULT CURRENT_DATE, -- DE-6003
    TotalAmount DECIMAL(10, 2) NOT NULL, -- DE-6004
    ShippingAddress VARCHAR(255), -- DE-6005
    OrderStatus VARCHAR(20) NOT NULL, -- DE-6006
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- 07 OrderItem table
CREATE TABLE OrderItem (
    OrderItemID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- DE-7001
    OrderID INT NOT NULL, -- DE-7002
    DeviceID INT NOT NULL, -- DE-7003
    Quantity INT NOT NULL, -- DE-7004
    Price DECIMAL(10, 2) NOT NULL, -- DE-7005
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (DeviceID) REFERENCES Device(DeviceID)
);

-- 08 Payment table
CREATE TABLE Payment (
    PaymentID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- DE-8001
    OrderID INT NOT NULL, -- DE-8002
    PaymentMethod VARCHAR(50) NOT NULL, -- DE-8003
    PaymentStatus VARCHAR(20) NOT NULL, -- DE-8004
    PaymentDate TIMESTAMP, -- DE-8005
    CreditCardNumber VARCHAR(16),
    CardHolderName VARCHAR(100),
    ExpiryDate VARCHAR(7),
    CVC VARCHAR(4),
    Amount DECIMAL(10, 2),
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
);

-- 09 OrderManagement table
CREATE TABLE OrderManagement (
    OrderManagementID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- DE-9001
    OrderID INT NOT NULL, -- DE-9002
    StaffID INT, -- DE-9003
    UpdateTimestamp TIMESTAMP, -- DE-9004
    TrackingNumber VARCHAR(50), -- DE-9005
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (StaffID) REFERENCES Users(UserID)
);

-- 10 Delivery table
CREATE TABLE Delivery (
    DeliveryID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- DE-11001
    OrderID INT NOT NULL, -- DE-11002
    DeliveryDate DATE, -- DE-11003
    EstimatedDeliveryDate DATE, -- DE-11004
    DeliverStatus VARCHAR(20), -- DE-11005
    DeliveryMethod VARCHAR(50), -- DE-11006
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
);

-- User access logs table for tracking login/logout activities
CREATE TABLE AccessLogs (
    LogID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    UserID INT NOT NULL,
    LoginTime TIMESTAMP,
    LogoutTime TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- SavedCartItems table for storing saved cart items
CREATE TABLE SavedCartItems (
    SavedCartItemID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    UserID INT NOT NULL,
    DeviceID INT NOT NULL,
    Quantity INT NOT NULL,
    Price DECIMAL(10, 2) NOT NULL,
    DateAdded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (DeviceID) REFERENCES Device(DeviceID) ON DELETE CASCADE
);

INSERT INTO Users (Name, Email, Password, DateOfBirth, Address, Phone, UserType) VALUES
('John Smith', 'john.smith@iotstore.com', 'hashed_password_1', '1985-06-15', '123 Staff St, Tech City', '555-0101', 'Staff'),
('Sarah Johnson', 'sarah.j@iotstore.com', 'hashed_password_2', '1990-03-22', '456 Admin Ave, Tech City', '555-0102', 'Staff'),
('Michael Brown', 'michael.b@iotstore.com', 'hashed_password_3', '1988-11-30', '789 Staff Rd, Tech City', '555-0103', 'Staff'),
('Emily Davis', 'emily.d@iotstore.com', 'hashed_password_4', '1992-07-18', '321 Support St, Tech City', '555-0104', 'Staff'),
('David Wilson', 'david.w@iotstore.com', 'hashed_password_5', '1987-04-25', '654 Staff Ave, Tech City', '555-0105', 'Staff'),
('Lisa Anderson', 'lisa.a@iotstore.com', 'hashed_password_6', '1991-09-12', '987 Support Rd, Tech City', '555-0106', 'Staff'),
('Robert Taylor', 'robert.t@iotstore.com', 'hashed_password_7', '1986-12-05', '147 Staff St, Tech City', '555-0107', 'Staff'),
('Jennifer Martinez', 'jennifer.m@iotstore.com', 'hashed_password_8', '1993-02-28', '258 Admin Ave, Tech City', '555-0108', 'Staff'),
('William Thomas', 'william.t@iotstore.com', 'hashed_password_9', '1989-08-14', '369 Support Rd, Tech City', '555-0109', 'Staff'),
('Patricia Garcia', 'patricia.g@iotstore.com', 'hashed_password_10', '1994-05-20', '741 Staff Ave, Tech City', '555-0110', 'Staff'),
('Alice Cooper', 'alice.c@email.com', 'hashed_password_11', '1995-01-15', '123 Main St, City', '555-0201', 'Customer'),
('Bob Wilson', 'bob.w@email.com', 'hashed_password_12', '1980-03-20', '456 Oak Ave, Town', '555-0202', 'Customer'),
('Carol White', 'carol.w@email.com', 'hashed_password_13', '1992-07-10', '789 Pine Rd, Village', '555-0203', 'Customer'),
('Daniel Lee', 'daniel.l@email.com', 'hashed_password_14', '1988-11-25', '321 Elm St, City', '555-0204', 'Customer'),
('Emma Davis', 'emma.d@email.com', 'hashed_password_15', '1996-04-05', '654 Maple Ave, Town', '555-0205', 'Customer'),
('Frank Miller', 'frank.m@email.com', 'hashed_password_16', '1985-09-18', '987 Cedar Rd, Village', '555-0206', 'Customer'),
('Grace Taylor', 'grace.t@email.com', 'hashed_password_17', '1993-12-30', '147 Birch St, City', '555-0207', 'Customer'),
('Henry Brown', 'henry.b@email.com', 'hashed_password_18', '1990-06-22', '258 Spruce Ave, Town', '555-0208', 'Customer'),
('Ivy Martinez', 'ivy.m@email.com', 'hashed_password_19', '1987-02-14', '369 Willow Rd, Village', '555-0209', 'Customer'),
('Jack Anderson', 'jack.a@email.com', 'hashed_password_20', '1994-08-08', '741 Ash St, City', '555-0210', 'Customer');

INSERT INTO Category (CategoryName, CategoryDescription) VALUES
('Smart Home', 'Devices for home automation and control'),
('Security', 'Security cameras and monitoring systems'),
('Climate Control', 'Smart thermostats and climate monitoring'),
('Lighting', 'Smart lighting solutions and controls'),
('Entertainment', 'Smart entertainment systems and devices'),
('Health & Wellness', 'Health monitoring and wellness devices'),
('Kitchen Appliances', 'Smart kitchen devices and appliances'),
('Garden & Outdoor', 'Smart garden and outdoor monitoring systems'),
('Energy Management', 'Energy monitoring and management devices'),
('Wearables', 'Smart wearable devices and accessories'),
('Voice Assistants', 'Smart speakers and voice control devices'),
('Smart Locks', 'Electronic and smart lock systems'),
('Air Quality', 'Air quality monitoring and control devices'),
('Water Management', 'Smart water monitoring and control systems'),
('Pet Care', 'Smart pet care and monitoring devices'),
('Baby Care', 'Smart baby monitoring and care devices'),
('Elderly Care', 'Smart elderly care and monitoring systems'),
('Fitness', 'Smart fitness and exercise devices'),
('Sleep Technology', 'Smart sleep monitoring and improvement devices'),
('Smart Storage', 'Smart storage and organization solutions');

INSERT INTO Device (CategoryID, DeviceName, Description, Price, StockQuantity, isActive) VALUES
(1, 'Smart Home Hub Pro', 'Central control unit for smart home devices', 199.99, 50, TRUE),
(2, 'Security Camera 4K', '4K resolution security camera with night vision', 149.99, 75, TRUE),
(3, 'Smart Thermostat Elite', 'AI-powered climate control system', 249.99, 40, TRUE),
(4, 'Smart LED Bulb Pack', 'Set of 4 color-changing smart bulbs', 79.99, 100, TRUE),
(5, 'Smart TV 55"', '4K Smart TV with voice control', 699.99, 30, TRUE),
(6, 'Health Monitor Pro', 'Advanced health monitoring device', 299.99, 45, TRUE),
(7, 'Smart Refrigerator', 'WiFi-enabled smart refrigerator', 1299.99, 20, TRUE),
(8, 'Garden Monitor System', 'Smart garden monitoring and irrigation system', 199.99, 35, TRUE),
(9, 'Energy Monitor', 'Real-time energy usage monitoring device', 149.99, 60, TRUE),
(10, 'Smart Watch Pro', 'Advanced fitness and health tracking watch', 299.99, 80, TRUE),
(11, 'Voice Assistant Hub', 'Smart speaker with voice control', 99.99, 120, TRUE),
(12, 'Smart Lock System', 'Fingerprint and app-controlled door lock', 249.99, 55, TRUE),
(13, 'Air Quality Monitor', 'Real-time air quality monitoring device', 179.99, 65, TRUE),
(14, 'Smart Water Controller', 'Water usage monitoring and control system', 159.99, 40, TRUE),
(15, 'Pet Tracker Pro', 'GPS pet tracking and monitoring device', 89.99, 90, TRUE),
(16, 'Baby Monitor Elite', 'Advanced baby monitoring system', 199.99, 45, TRUE),
(17, 'Elderly Care Monitor', 'Health and safety monitoring system', 349.99, 30, TRUE),
(18, 'Smart Fitness Band', 'Advanced fitness tracking device', 129.99, 100, TRUE),
(19, 'Sleep Monitor Pro', 'Sleep quality monitoring and improvement device', 249.99, 50, TRUE),
(20, 'Smart Storage System', 'Automated storage and organization system', 399.99, 25, TRUE);

INSERT INTO Cart (UserID) VALUES
(11), (12), (13), (14), (15), (16), (17), (18), (19), (20),
(11), (12), (13), (14), (15), (16), (17), (18), (19), (20);

INSERT INTO CartItem (CartID, DeviceID, Quantity) VALUES
(1, 1, 1), (1, 3, 2), (2, 5, 1), (2, 7, 1), (3, 9, 1),
(4, 11, 2), (4, 13, 1), (5, 15, 1), (5, 17, 1), (6, 19, 1),
(7, 2, 1), (7, 4, 3), (8, 6, 1), (8, 8, 1), (9, 10, 1),
(10, 12, 1), (10, 14, 1), (11, 16, 1), (11, 18, 1), (12, 20, 1);

INSERT INTO Orders (UserID, OrderDate, TotalAmount, ShippingAddress, OrderStatus) VALUES
(11, '2024-01-15', 449.98, '123 Main St, City', 'Delivered'),
(12, '2024-01-16', 699.99, '456 Oak Ave, Town', 'Processing'),
(13, '2024-01-17', 299.99, '789 Pine Rd, Village', 'Shipped'),
(14, '2024-01-18', 159.98, '321 Elm St, City', 'Delivered'),
(15, '2024-01-19', 1299.99, '654 Maple Ave, Town', 'Processing'),
(16, '2024-01-20', 199.99, '987 Cedar Rd, Village', 'Shipped'),
(17, '2024-01-21', 299.99, '147 Birch St, City', 'Delivered'),
(18, '2024-01-22', 249.99, '258 Spruce Ave, Town', 'Processing'),
(19, '2024-01-23', 349.99, '369 Willow Rd, Village', 'Shipped'),
(20, '2024-01-24', 399.99, '741 Ash St, City', 'Delivered'),
(11, '2024-01-25', 149.99, '123 Main St, City', 'Processing'),
(12, '2024-01-26', 199.99, '456 Oak Ave, Town', 'Shipped'),
(13, '2024-01-27', 299.99, '789 Pine Rd, Village', 'Delivered'),
(14, '2024-01-28', 179.99, '321 Elm St, City', 'Processing'),
(15, '2024-01-29', 89.99, '654 Maple Ave, Town', 'Shipped'),
(16, '2024-01-30', 199.99, '987 Cedar Rd, Village', 'Delivered'),
(17, '2024-01-31', 349.99, '147 Birch St, City', 'Processing'),
(18, '2024-02-01', 129.99, '258 Spruce Ave, Town', 'Shipped'),
(19, '2024-02-02', 249.99, '369 Willow Rd, Village', 'Delivered'),
(20, '2024-02-03', 399.99, '741 Ash St, City', 'Processing'),
-- Additional orders with varied dates for each user
(11, '2023-10-15', 249.99, '123 Main St, City', 'Delivered'),
(11, '2023-12-05', 199.99, '123 Main St, City', 'Delivered'),
(11, '2024-02-20', 399.99, '123 Main St, City', 'Processing'),
(11, '2024-03-15', 129.99, '123 Main St, City', 'Pending'),
(12, '2023-09-20', 349.99, '456 Oak Ave, Town', 'Delivered'),
(12, '2023-11-15', 229.99, '456 Oak Ave, Town', 'Delivered'),
(12, '2024-02-25', 179.99, '456 Oak Ave, Town', 'Shipped'),
(12, '2024-03-10', 299.99, '456 Oak Ave, Town', 'Processing'),
(13, '2023-08-10', 499.99, '789 Pine Rd, Village', 'Delivered'),
(13, '2023-11-25', 179.99, '789 Pine Rd, Village', 'Delivered'),
(13, '2024-02-15', 249.99, '789 Pine Rd, Village', 'Shipped'),
(13, '2024-03-20', 399.99, '789 Pine Rd, Village', 'Pending'),
(14, '2023-07-22', 599.99, '321 Elm St, City', 'Delivered'),
(14, '2023-12-22', 159.99, '321 Elm St, City', 'Delivered'),
(14, '2024-02-05', 279.99, '321 Elm St, City', 'Shipped'),
(14, '2024-03-25', 349.99, '321 Elm St, City', 'Pending'),
(15, '2023-09-05', 449.99, '654 Maple Ave, Town', 'Delivered'),
(15, '2023-12-15', 199.99, '654 Maple Ave, Town', 'Delivered'),
(15, '2024-02-10', 329.99, '654 Maple Ave, Town', 'Shipped'),
(15, '2024-03-05', 279.99, '654 Maple Ave, Town', 'Processing'),
(16, '2023-10-28', 349.99, '987 Cedar Rd, Village', 'Delivered'),
(16, '2023-12-27', 279.99, '987 Cedar Rd, Village', 'Delivered'),
(16, '2024-02-02', 459.99, '987 Cedar Rd, Village', 'Shipped'),
(16, '2024-03-22', 199.99, '987 Cedar Rd, Village', 'Pending'),
(17, '2023-11-10', 499.99, '147 Birch St, City', 'Delivered'),
(17, '2024-01-05', 229.99, '147 Birch St, City', 'Delivered'),
(17, '2024-02-22', 179.99, '147 Birch St, City', 'Shipped'),
(17, '2024-03-17', 299.99, '147 Birch St, City', 'Pending'),
(18, '2023-08-28', 399.99, '258 Spruce Ave, Town', 'Delivered'),
(18, '2023-12-12', 259.99, '258 Spruce Ave, Town', 'Delivered'),
(18, '2024-02-12', 329.99, '258 Spruce Ave, Town', 'Shipped'),
(18, '2024-03-14', 279.99, '258 Spruce Ave, Town', 'Processing'),
(19, '2023-09-15', 349.99, '369 Willow Rd, Village', 'Delivered'),
(19, '2023-11-20', 229.99, '369 Willow Rd, Village', 'Delivered'),
(19, '2024-02-27', 399.99, '369 Willow Rd, Village', 'Shipped'),
(19, '2024-03-12', 249.99, '369 Willow Rd, Village', 'Processing'),
(20, '2023-10-10', 499.99, '741 Ash St, City', 'Delivered'),
(20, '2023-12-19', 299.99, '741 Ash St, City', 'Delivered'),
(20, '2024-02-19', 249.99, '741 Ash St, City', 'Shipped'),
(20, '2024-03-08', 399.99, '741 Ash St, City', 'Processing');

INSERT INTO OrderItem (OrderID, DeviceID, Quantity, Price) VALUES
(1, 1, 1, 199.99), (1, 3, 1, 249.99),
(2, 5, 1, 699.99), (3, 6, 1, 299.99),
(4, 11, 2, 99.99), (5, 7, 1, 1299.99),
(6, 8, 1, 199.99), (7, 10, 1, 299.99),
(8, 12, 1, 249.99), (9, 17, 1, 349.99),
(10, 20, 1, 399.99), (11, 9, 1, 149.99),
(12, 13, 1, 179.99), (13, 10, 1, 299.99),
(14, 13, 1, 179.99), (15, 15, 1, 89.99),
(16, 16, 1, 199.99), (17, 17, 1, 349.99),
(18, 18, 1, 129.99), (19, 19, 1, 249.99);

INSERT INTO Payment (OrderID, PaymentMethod, PaymentStatus, PaymentDate) VALUES
(1, 'Credit Card', 'Completed', '2024-01-15 10:30:00'),
(2, 'PayPal', 'Completed', '2024-01-16 11:45:00'),
(3, 'Credit Card', 'Completed', '2024-01-17 09:15:00'),
(4, 'Debit Card', 'Completed', '2024-01-18 14:20:00'),
(5, 'Credit Card', 'Completed', '2024-01-19 16:30:00'),
(6, 'PayPal', 'Completed', '2024-01-20 13:45:00'),
(7, 'Credit Card', 'Completed', '2024-01-21 10:15:00'),
(8, 'Debit Card', 'Completed', '2024-01-22 11:30:00'),
(9, 'Credit Card', 'Completed', '2024-01-23 15:45:00'),
(10, 'PayPal', 'Completed', '2024-01-24 09:30:00'),
(11, 'Credit Card', 'Completed', '2024-01-25 14:15:00'),
(12, 'Debit Card', 'Completed', '2024-01-26 10:45:00'),
(13, 'Credit Card', 'Completed', '2024-01-27 16:30:00'),
(14, 'PayPal', 'Completed', '2024-01-28 11:15:00'),
(15, 'Credit Card', 'Completed', '2024-01-29 13:45:00'),
(16, 'Debit Card', 'Completed', '2024-01-30 15:30:00'),
(17, 'Credit Card', 'Completed', '2024-01-31 10:15:00'),
(18, 'PayPal', 'Completed', '2024-02-01 14:45:00'),
(19, 'Credit Card', 'Completed', '2024-02-02 16:30:00'),
(20, 'Debit Card', 'Completed', '2024-02-03 11:15:00');

INSERT INTO OrderManagement (OrderID, StaffID, UpdateTimestamp, TrackingNumber) VALUES
(1, 1, '2024-01-15 11:30:00', 'TRK001'),
(2, 2, '2024-01-16 12:45:00', 'TRK002'),
(3, 3, '2024-01-17 10:15:00', 'TRK003'),
(4, 4, '2024-01-18 15:20:00', 'TRK004'),
(5, 5, '2024-01-19 17:30:00', 'TRK005'),
(6, 6, '2024-01-20 14:45:00', 'TRK006'),
(7, 7, '2024-01-21 11:15:00', 'TRK007'),
(8, 8, '2024-01-22 12:30:00', 'TRK008'),
(9, 9, '2024-01-23 16:45:00', 'TRK009'),
(10, 10, '2024-01-24 10:30:00', 'TRK010'),
(11, 1, '2024-01-25 15:15:00', 'TRK011'),
(12, 2, '2024-01-26 11:45:00', 'TRK012'),
(13, 3, '2024-01-27 17:30:00', 'TRK013'),
(14, 4, '2024-01-28 12:15:00', 'TRK014'),
(15, 5, '2024-01-29 14:45:00', 'TRK015'),
(16, 6, '2024-01-30 16:30:00', 'TRK016'),
(17, 7, '2024-01-31 11:15:00', 'TRK017'),
(18, 8, '2024-02-01 15:45:00', 'TRK018'),
(19, 9, '2024-02-02 17:30:00', 'TRK019'),
(20, 10, '2024-02-03 12:15:00', 'TRK020');

INSERT INTO Delivery (OrderID, DeliveryDate, EstimatedDeliveryDate, DeliverStatus, DeliveryMethod) VALUES
(1, '2024-01-17', '2024-01-18', 'Delivered', 'Standard Shipping'),
(2, '2024-01-19', '2024-01-20', 'In Transit', 'Express Shipping'),
(3, '2024-01-20', '2024-01-21', 'Delivered', 'Standard Shipping'),
(4, '2024-01-21', '2024-01-22', 'Delivered', 'Express Shipping'),
(5, '2024-01-23', '2024-01-24', 'In Transit', 'Standard Shipping'),
(6, '2024-01-24', '2024-01-25', 'Delivered', 'Express Shipping'),
(7, '2024-01-25', '2024-01-26', 'Delivered', 'Standard Shipping'),
(8, '2024-01-26', '2024-01-27', 'In Transit', 'Express Shipping'),
(9, '2024-01-28', '2024-01-29', 'Delivered', 'Standard Shipping'),
(10, '2024-01-29', '2024-01-30', 'Delivered', 'Express Shipping'),
(11, '2024-01-30', '2024-01-31', 'In Transit', 'Standard Shipping'),
(12, '2024-01-31', '2024-02-01', 'Delivered', 'Express Shipping'),
(13, '2024-02-01', '2024-02-02', 'Delivered', 'Standard Shipping'),
(14, '2024-02-02', '2024-02-03', 'In Transit', 'Express Shipping'),
(15, '2024-02-03', '2024-02-04', 'Delivered', 'Standard Shipping'),
(16, '2024-02-04', '2024-02-05', 'Delivered', 'Express Shipping'),
(17, '2024-02-05', '2024-02-06', 'In Transit', 'Standard Shipping'),
(18, '2024-02-06', '2024-02-07', 'Delivered', 'Express Shipping'),
(19, '2024-02-07', '2024-02-08', 'Delivered', 'Standard Shipping'),
(20, '2024-02-08', '2024-02-09', 'In Transit', 'Express Shipping');

-- Insert access logs for each user (5 logs per user)
-- Staff users (UserID 1-10)
INSERT INTO AccessLogs (UserID, LoginTime, LogoutTime) VALUES
-- User 1 (John Smith)
(1, '2024-01-01 08:00:00', '2024-01-01 17:30:00'),
(1, '2024-01-03 08:15:00', '2024-01-03 17:45:00'),
(1, '2024-01-08 08:30:00', '2024-01-08 18:00:00'),
(1, '2024-01-15 08:45:00', '2024-01-15 17:15:00'),
(1, '2024-01-22 09:00:00', '2024-01-22 17:30:00'),

-- User 2 (Sarah Johnson)
(2, '2024-01-01 08:30:00', '2024-01-01 17:45:00'),
(2, '2024-01-04 08:45:00', '2024-01-04 18:00:00'),
(2, '2024-01-09 09:00:00', '2024-01-09 17:15:00'),
(2, '2024-01-16 08:15:00', '2024-01-16 17:30:00'),
(2, '2024-01-23 08:30:00', '2024-01-23 17:45:00'),

-- User 3 (Michael Brown)
(3, '2024-01-02 09:00:00', '2024-01-02 18:15:00'),
(3, '2024-01-05 08:15:00', '2024-01-05 17:30:00'),
(3, '2024-01-10 08:30:00', '2024-01-10 17:45:00'),
(3, '2024-01-17 08:45:00', '2024-01-17 18:00:00'),
(3, '2024-01-24 09:00:00', '2024-01-24 17:15:00'),

-- User 4 (Emily Davis)
(4, '2024-01-02 08:15:00', '2024-01-02 17:30:00'),
(4, '2024-01-05 08:30:00', '2024-01-05 17:45:00'),
(4, '2024-01-11 08:45:00', '2024-01-11 18:00:00'),
(4, '2024-01-18 09:00:00', '2024-01-18 17:15:00'),
(4, '2024-01-25 08:15:00', '2024-01-25 17:30:00'),

-- User 5 (David Wilson)
(5, '2024-01-03 08:45:00', '2024-01-03 18:00:00'),
(5, '2024-01-08 09:00:00', '2024-01-08 17:15:00'),
(5, '2024-01-12 08:15:00', '2024-01-12 17:30:00'),
(5, '2024-01-19 08:30:00', '2024-01-19 17:45:00'),
(5, '2024-01-26 08:45:00', '2024-01-26 18:00:00'),

-- User 6 (Lisa Anderson)
(6, '2024-01-01 08:00:00', '2024-01-01 17:30:00'),
(6, '2024-01-04 08:15:00', '2024-01-04 17:45:00'),
(6, '2024-01-09 08:30:00', '2024-01-09 18:00:00'),
(6, '2024-01-16 08:45:00', '2024-01-16 17:15:00'),
(6, '2024-01-23 09:00:00', '2024-01-23 17:30:00'),

-- User 7 (Robert Taylor)
(7, '2024-01-02 08:30:00', '2024-01-02 17:45:00'),
(7, '2024-01-05 08:45:00', '2024-01-05 18:00:00'),
(7, '2024-01-10 09:00:00', '2024-01-10 17:15:00'),
(7, '2024-01-17 08:15:00', '2024-01-17 17:30:00'),
(7, '2024-01-24 08:30:00', '2024-01-24 17:45:00'),

-- User 8 (Jennifer Martinez)
(8, '2024-01-03 09:00:00', '2024-01-03 18:15:00'),
(8, '2024-01-08 08:15:00', '2024-01-08 17:30:00'),
(8, '2024-01-11 08:30:00', '2024-01-11 17:45:00'),
(8, '2024-01-18 8:45:00', '2024-01-18 18:00:00'),
(8, '2024-01-25 9:00:00', '2024-01-25 17:15:00'),

-- User 9 (William Thomas)
(9, '2024-01-01 08:15:00', '2024-01-01 17:30:00'),
(9, '2024-01-04 08:30:00', '2024-01-04 17:45:00'),
(9, '2024-01-09 08:45:00', '2024-01-09 18:00:00'),
(9, '2024-01-16 9:00:00', '2024-01-16 17:15:00'),
(9, '2024-01-23 8:15:00', '2024-01-23 17:30:00'),

-- User 10 (Patricia Garcia)
(10, '2024-01-02 08:45:00', '2024-01-02 18:00:00'),
(10, '2024-01-05 9:00:00', '2024-01-05 17:15:00'),
(10, '2024-01-10 8:15:00', '2024-01-10 17:30:00'),
(10, '2024-01-17 8:30:00', '2024-01-17 17:45:00'),
(10, '2024-01-24 8:45:00', '2024-01-24 18:00:00'),

-- Customer users (UserID 11-20)
-- User 11 (Alice Cooper)
(11, '2024-01-01 10:00:00', '2024-01-01 11:30:00'),
(11, '2024-01-08 14:15:00', '2024-01-08 15:45:00'),
(11, '2024-01-15 09:30:00', '2024-01-15 10:45:00'),
(11, '2024-01-22 16:00:00', '2024-01-22 17:15:00'),
(11, '2024-01-26 11:30:00', '2024-01-26 12:45:00'),

-- User 12 (Bob Wilson)
(12, '2024-01-02 11:00:00', '2024-01-02 12:30:00'),
(12, '2024-01-09 15:15:00', '2024-01-09 16:45:00'),
(12, '2024-01-16 10:30:00', '2024-01-16 11:45:00'),
(12, '2024-01-23 17:00:00', '2024-01-23 18:15:00'),
(12, '2024-01-26 12:30:00', '2024-01-26 13:45:00'),

-- User 13 (Carol White)
(13, '2024-01-03 12:00:00', '2024-01-03 13:30:00'),
(13, '2024-01-10 16:15:00', '2024-01-10 17:45:00'),
(13, '2024-01-17 11:30:00', '2024-01-17 12:45:00'),
(13, '2024-01-24 18:00:00', '2024-01-24 19:15:00'),
(13, '2024-01-26 13:30:00', '2024-01-26 14:45:00'),

-- User 14 (Daniel Lee)
(14, '2024-01-01 13:00:00', '2024-01-01 14:30:00'),
(14, '2024-01-08 17:15:00', '2024-01-08 18:45:00'),
(14, '2024-01-15 12:30:00', '2024-01-15 13:45:00'),
(14, '2024-01-22 19:00:00', '2024-01-22 20:15:00'),
(14, '2024-01-26 14:30:00', '2024-01-26 15:45:00'),

-- User 15 (Emma Davis)
(15, '2024-01-02 14:00:00', '2024-01-02 15:30:00'),
(15, '2024-01-09 18:15:00', '2024-01-09 19:45:00'),
(15, '2024-01-16 13:30:00', '2024-01-16 14:45:00'),
(15, '2024-01-23 20:00:00', '2024-01-23 21:15:00'),
(15, '2024-01-26 15:30:00', '2024-01-26 16:45:00'),

-- User 16 (Frank Miller)
(16, '2024-01-03 15:00:00', '2024-01-03 16:30:00'),
(16, '2024-01-10 19:15:00', '2024-01-10 20:45:00'),
(16, '2024-01-17 14:30:00', '2024-01-17 15:45:00'),
(16, '2024-01-24 21:00:00', '2024-01-24 22:15:00'),
(16, '2024-01-26 16:30:00', '2024-01-26 17:45:00'),

-- User 17 (Grace Taylor)
(17, '2024-01-01 16:00:00', '2024-01-01 17:30:00'),
(17, '2024-01-08 20:15:00', '2024-01-08 21:45:00'),
(17, '2024-01-15 15:30:00', '2024-01-15 16:45:00'),
(17, '2024-01-22 22:00:00', '2024-01-22 23:15:00'),
(17, '2024-01-26 17:30:00', '2024-01-26 18:45:00'),

-- User 18 (Henry Brown)
(18, '2024-01-02 17:00:00', '2024-01-02 18:30:00'),
(18, '2024-01-09 21:15:00', '2024-01-09 22:45:00'),
(18, '2024-01-16 16:30:00', '2024-01-16 17:45:00'),
(18, '2024-01-23 23:00:00', '2024-01-24 00:15:00'),
(18, '2024-01-26 18:30:00', '2024-01-26 19:45:00'),

-- User 19 (Ivy Martinez)
(19, '2024-01-03 18:00:00', '2024-01-03 19:30:00'),
(19, '2024-01-10 22:15:00', '2024-01-10 23:45:00'),
(19, '2024-01-17 17:30:00', '2024-01-17 18:45:00'),
(19, '2024-01-24 00:00:00', '2024-01-24 01:15:00'),
(19, '2024-01-26 19:30:00', '2024-01-26 20:45:00'),

-- User 20 (Jack Anderson)
(20, '2024-01-01 19:00:00', '2024-01-01 20:30:00'),
(20, '2024-01-08 23:15:00', '2024-01-09 00:45:00'),
(20, '2024-01-15 18:30:00', '2024-01-15 19:45:00'),
(20, '2024-01-22 01:00:00', '2024-01-22 02:15:00'),
(20, '2024-01-26 20:30:00', '2024-01-26 21:45:00');
