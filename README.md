# Final Rental – E2E Test Automation Framework

A production-grade **Selenium + TestNG + Cucumber BDD** automation framework for the **Final Rental** website ([testing.final.sa](https://testing.final.sa)), built with the **Page Object Model (POM) + Page Factory** pattern.

🔗 **GitHub:** [OmarEl-Antary/final-rental-framework-updated](https://github.com/OmarEl-Antary/final-rental-framework-updated)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 LTS |
| Browser Automation | Selenium WebDriver 4.18.1 |
| Test Runner | TestNG 7.9 |
| BDD Framework | Cucumber 7.15.0 |
| Build Tool | Maven 3.8+ |
| Design Pattern | Page Object Model + Page Factory |
| Reporting | Extent Reports (Spark) |
| Logging | Log4j2 + SLF4J |
| Assertions | AssertJ (fluent) |
| Pricing Validation | BigDecimal (exact arithmetic) |

> ⚠️ **Java 21 is required.** Java 24+ causes `ExceptionInInitializerError` with the Selenium/TestNG versions used here.

---

## Project Structure

```
final-rental-framework-updated/
│
├── pom.xml
│
├── src/
│   ├── main/java/com/finalrental/
│   │   ├── config/
│   │   │   ├── ConfigReader.java           # Singleton config loader
│   │   │   └── DriverFactory.java          # WebDriver factory
│   │   │
│   │   ├── pages/
│   │   │   ├── BasePage.java               # Parent page: Selenium utilities + explicit waits
│   │   │   ├── HomePage.java               # Landing page + city search
│   │   │   ├── LoginOtpPage.java           # OTP login flow
│   │   │   ├── RegisterPage.java           # New user registration
│   │   │   ├── ProductsPage.java           # Product listing + add to cart
│   │   │   ├── CartPage.java               # Cart: dates, times, submit
│   │   │   ├── CompleteOrderPage.java      # Identity number + end order
│   │   │   ├── OrderConfirmationPage.java  # Terms modal + order complete
│   │   │   ├── OrdersPage.java             # Orders list + menu options
│   │   │   ├── OrderSummaryPage.java       # Tax & total price validation
│   │   │   └── EditOrderPage.java          # Edit & cancel order flows
│   │   │
│   │   └── utils/
│   │       ├── ScreenshotUtil.java         # PNG screenshots (PASSED/FAILED prefix)
│   │       └── WaitUtil.java               # Explicit wait helpers
│   │
│   └── test/
│       ├── java/com/finalrental/
│       │   ├── tests/
│       │   │   ├── BaseTest.java           # @BeforeMethod / @AfterMethod
│       │   │   ├── RegisterTest.java       # Register new user
│       │   │   ├── LoginOtpTest.java       # OTP login
│       │   │   ├── OrderFlowTest.java      # Full order flow + price validation
│       │   │   ├── EditOrderTest.java      # Edit order
│       │   │   └── CancelOrderTest.java    # Cancel order
│       │   │
│       │   ├── bdd/
│       │   │   ├── runner/
│       │   │   │   └── CucumberRunner.java
│       │   │   └── stepdefs/
│       │   │       ├── Hooks.java
│       │   │       ├── RegisterSteps.java
│       │   │       ├── LoginSteps.java
│       │   │       ├── OrderSteps.java
│       │   │       ├── EditOrderSteps.java
│       │   │       └── CancelOrderSteps.java
│       │   │
│       │   ├── data/
│       │   │   └── TestContext.java        # Shared state between tests (registered phone)
│       │   │
│       │   └── listeners/
│       │       └── ExtentReportListener.java
│       │
│       └── resources/
│           ├── config.properties
│           ├── log4j2.xml
│           ├── testng.xml
│           └── features/
│               ├── 01_register.feature
│               ├── 02_login.feature
│               ├── 03_order.feature
│               ├── 04_edit_order.feature
│               └── 05_cancel_order.feature
```

---

## Test Scenarios

### TestNG Suite (testng.xml)
| Test Class | Description |
|---|---|
| `RegisterTest` | Register new user with random Egyptian phone number |
| `LoginOtpTest` | OTP login with registered phone |
| `OrderFlowTest` | Full order flow + tax/total price validation |
| `EditOrderTest` | Edit existing order by adding a product |
| `CancelOrderTest` | Cancel order with reason |

### Cucumber BDD (CucumberRunner)
| Feature File | Description |
|---|---|
| `01_register.feature` | Register new user |
| `02_login.feature` | OTP login with registered phone |
| `03_order.feature` | Full order flow + price validation |
| `04_edit_order.feature` | Edit order |
| `05_cancel_order.feature` | Cancel order |

---

## Key Technical Solutions

### Bootstrap Selectpicker
Standard Selenium `select` fails because selectpicker hides the native `<select>`. Solution: JavaScript to set value and dispatch change event.
```java
executeScript("var el = document.querySelector('#city_select'); el.value = '6'; el.dispatchEvent(new Event('change', {bubbles: true}));");
```

### Flatpickr Date Inputs
```java
executeScript("document.querySelector('#from_date')._flatpickr.setDate(arguments[0], true);", date);
```

### Discount Modal Blocking Clicks
```java
executeScript("document.querySelectorAll('.modal').forEach(m => { m.classList.remove('show'); m.style.display = 'none'; }); document.querySelectorAll('.modal-backdrop').forEach(b => b.remove()); document.body.classList.remove('modal-open');");
```

### Shared Test State (TestContext)
Registered phone number is saved in `TestContext` and reused across Login, Order, Edit, and Cancel tests:
```java
TestContext.setRegisteredPhone(phone); // in Register
TestContext.getRegisteredPhone();       // in Login, Order, etc.
```

### Price Validation (BigDecimal)
```java
BigDecimal expectedTax = rental.add(delivery).subtract(discount)
    .multiply(new BigDecimal("0.15"))
    .setScale(2, RoundingMode.HALF_UP);
assertThat(actualTax).isEqualByComparingTo(expectedTax);
```

---

## Quick Start

### Prerequisites
- Java 21 LTS (Adoptium recommended)
- Maven 3.8+
- Google Chrome (latest)

### 1. Clone
```bash
git clone https://github.com/OmarEl-Antary/final-rental-framework-updated.git
cd final-rental-framework-updated
```

### 2. Run TestNG Suite
```bash
mvn test
```

### 3. Run Cucumber BDD
```bash
mvn test -Dtest=CucumberRunner
```

### 4. Run Single Test Class
```bash
mvn test -Dtest=OrderFlowTest
```

### 5. Headless Mode
```bash
mvn test -Dheadless=true
```

---

## Configuration (`config.properties`)

| Key | Default | Description |
|---|---|---|
| `base.url` | `https://testing.final.sa` | Website URL |
| `browser` | `chrome` | Browser type |
| `headless` | `false` | Headless mode |
| `explicit.wait` | `15` | Explicit wait (seconds) |
| `page.load.timeout` | `60` | Page load timeout |
| `screenshot.on.failure` | `true` | Screenshot on failure |

---

## Reports & Artifacts

| Artifact | Location |
|---|---|
| Extent HTML Report | `test-output/ExtentReport.html` |
| Screenshots (PASSED) | `src/test/resources/screenshots/PASSED_*.png` |
| Screenshots (FAILED) | `src/test/resources/screenshots/FAILED_*.png` |
| Cucumber HTML Report | `target/cucumber-reports/cucumber.html` |
| Log file | `logs/automation.log` |

---

## Adding New Tests

### TestNG
1. Create Page Object in `src/main/java/com/finalrental/pages/` extending `BasePage`
2. Create test class in `src/test/java/com/finalrental/tests/` extending `BaseTest`
3. Add class to `testng.xml`

### Cucumber BDD
1. Create Page Object (same as above)
2. Create `.feature` file in `src/test/resources/features/`
3. Create Step Definitions in `src/test/java/com/finalrental/bdd/stepdefs/`

---

## Author

**Omar El-Antary**
GitHub: [@OmarEl-Antary](https://github.com/OmarEl-Antary)
