# 💰 ExpenseTrackerFX – JavaFX + PostgreSQL

A modern, feature-rich **Personal Expense Management Application** built with **JavaFX 21** and **PostgreSQL**.  
This project helps users track, analyze, and export their expenses in a clean and responsive desktop application.

---

## 🚀 Features

### **Modern & Responsive UI**
- Sleek, professional design with **collapsible sidebar** and **card-based layouts**.
- Smooth animations for transitions and interactions.
- 100% **theme-responsive** with Light ☀️ & Dark 🌙 modes.

### **Live Data Dashboard**
- Monthly overview card with:
    - ✅ Total spending
    - ✅ Top spending category
    - ✅ Daily average spend
- Quick Actions card for fast navigation.

### **Advanced Analytics Suite**
- Interactive charts with professional segmented controls.
- Visualizations include:
    - 🥧 **Pie Chart** → Category share
    - 📊 **Bar Chart** → Category totals
    - 📈 **Line Chart** → Monthly spending trends
- Filters to compare spending across the **last 12 months**.

### **Intuitive Expense Logging**
- User-friendly form with fields: category, amount, date, merchant & notes.
- Quick and accurate entry system.

### **Professional Data Export**
- Export monthly expenses to **CSV**.
- File chooser defaults to user’s **Downloads** folder.
- Custom file naming support.

---

## 🛠️ Technologies Used
- **Java 21**
- **JavaFX 21** (FXML for layout, Java for controllers)
- **PostgreSQL** (Relational Database)
- **HikariCP** (High-performance connection pooling)
- **Flyway** (Database schema migration)
- **Maven** (Build & Dependency Management)
- **CSS** (Theme-responsive styling)
- **Ikonli** (Modern Material Design icons)
- **Layered Architecture** (UI, Service, DAO/Repository, Model)

---

## 📂 Project Structure
```plaintext
ExpenseTracker/
│── .idea/
│── .mvn/
│── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/isa/expensetracker/
│   │   │       ├── dao/
│   │   │       ├── db/
│   │   │       ├── model/
│   │   │       ├── repo/
│   │   │       ├── service/
│   │   │       ├── ui/
│   │   │       ├── util/ # each file is linked to a resource fxml file
│   │   │       └── MainApp.java
│   │   ├── resources/
│   │   │   └── com/isa/expensetracker/
│   │   │       ├── analytics.fxml
│   │   │       ├── dashboard.fxml
│   │   │       ├── expense_form.fxml
│   │   │       ├── main.fxml
│   │   │       └── settings.fxml
│   │   │
│   │   ├── css/
│   │   │   └── app.css
│   │   │
│   │   ├── db.migration/
│   │   │   └── V1__init.sql
│   │   │
│   │   ├── icons/
│   │   │   └── app.png
│   │   │
│   │   └── application.properties
│   │
│   └── module-info.java
│
└── pom.xml
```
## 🗄 Database Setup

Run the following SQL before starting the application:

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS expense_tracker;

-- Create table
CREATE TABLE expenses (
    id SERIAL PRIMARY KEY,
    category VARCHAR(100) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    merchant VARCHAR(100),
    notes TEXT
);
```
Flyway will manage schema migrations automatically on startup.

## ▶️ How to Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/expense-tracker-fx.git
   cd expense-tracker-fx
2. **Configure Database Connection**

   Update your PostgreSQL username & password in:
```aiignore
src/main/java/com/expensetrackerfx/config/DatabaseConfig.java
```
3. Build & Run
- Using Maven:
```bash
mvn clean install
mvn javafx:run
```
- Or open in IntelliJ/Eclipse and run Main.java.

---
## 📸 Sample Video


https://github.com/user-attachments/assets/5a8f0970-d08d-494f-8f03-54b86fa20b67


---
## 🔮 Future Enhancements
I am open to suggestions and plan to continue developing this Application with new Ideas :).



---

## 🤝 Contact
🧑‍💻 Your Name – [isashaikh2005@gmail.com](mailto:isashaikh2005@gmail.com)  
🔗 Project Link: [ExpenseTrackerFX](https://github.com/IsaShaikh/Expense-tracker-fx)
