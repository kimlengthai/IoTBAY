# IoTBay Web Application Prototype

This is a Java web application prototype for the IoTBay project, built as part of the UTS 41025 Introduction to Software Development subject. It uses **JSP**, **Servlets**, and **JavaBeans**, following the **MVC architecture**, and is deployed using **GlassFish Server** via **NetBeans IDE**.


Dependencies & Environments Required

## Java JDK 17
## Netbeans IDE 25 (https://netbeans.apache.org/front/main/download/)
## Eclipse GlassFish 7.0.23, Jakarta EE Web Profile, 10 (https://glassfish.org/download)


1. Clone the Repository
	- git clone https://github.com/your-username/ISD_GRP.git
	- Note this will clone in the current directory 

2. Open the Project in Netbeans	
	- Launch NetBeans IDE
	- Go to File > Open Project
	- Find the cloned project and open it
	- Netbeans should detect it automatically and load it

3. Setup GlassFish Server
	- Go to Services Tab on the left hand side -> Servers -> Right click -> Add Server -> Choose GlassFish Server
	- Browse to the downloaded GlassFish Server
	- Next -> Finish

4. Setup Derby Database
	- The application uses Apache Derby (Java DB) which comes bundled with NetBeans
	- Start the Derby Database Server from NetBeans:
	  - Go to the Services tab > Databases
	  - Right-click on "Java DB" and select "Start Server"
	- Create the Database:
	  - In the Services tab, right-click on "Java DB" and select "Create Database"
	  - Set the following parameters:
	    - Database Name: iotbay
	    - User Name: iotbay
	    - Password: iotbay
	    - Confirm Password: iotbay
	  - Click OK to create the database
	- Initialize the Database Schema:
	  - The application will automatically connect to this database
	  - To manually initialize the database with the required schema, you can run the DatabaseInitializer:
	    - Right-click on the project > Run File > src/main/java/com/util/DatabaseInitializer.java
	  - This will create all necessary tables and sample data
	- Connection Parameters (for reference):
	  - JDBC URL: jdbc:derby://localhost:1527/iotbay
	  - Username: iotbay
	  - Password: iotbay

5. Run the App
	- Right-click the project > Run
	- Netbeans will deploy it
	- It should open in your browser at: http://localhost:8080/<folder_name>

## Troubleshooting Database Issues

- If you encounter database connection issues, ensure the Derby server is running.
- To verify the database has been set up correctly, in NetBeans:
  - Services tab > Databases > jdbc:derby://localhost:1527/iotbay [iotbay on IOTBAY]
  - Right-click and select "Connect"
  - Expand the connection to see Tables, Views, etc.
  - You should see tables like Users, Device, Orders, etc.
- If tables are missing, manually run the DatabaseInitializer as described above.
