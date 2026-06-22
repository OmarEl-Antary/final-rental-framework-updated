# Final Rental – E2E Test Automation Framework

A production-grade Selenium + TestNG automation framework for the **Final Rental** website, built with the **Page Object Model (POM) + Page Factory** pattern.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Browser Automation | Selenium WebDriver 4.x |
| Test Runner | TestNG 7.x |
| Build Tool | Maven 3.8+ |
| Design Pattern | Page Object Model + Page Factory |
| Driver Management | WebDriverManager (auto) |
| Reporting | Extent Reports (Spark) |
| Logging | Log4j2 |
| Assertions | AssertJ (fluent) |
| Test Data | JavaFaker + JSON files |

---

## Project Structure

```
final-rental-framework/
│
├── pom.xml                                    # Maven dependencies & profiles
│
├── src/
│   ├── main/java/com/finalrental/
│   │   ├── config/
│   │   │   ├── ConfigReader.java              # Singleton config loader (config.properties)
│   │   │   └── DriverFactory.java             # Thread-safe WebDriver factory (ThreadLocal)
│   │   │
│   │   ├── models/
│   │   │   ├── RentalCalculator.java          # Business logic: pricing & tax calculations
│   │   │   └── RentalSearchRequest.java       # Immutable search request POJO (Builder pattern)
│   │   │
│   │   ├── pages/
│   │   │   ├── BasePage.java                  # Parent page: all Selenium utilities + explicit waits
│   │   │   ├── HomePage.java                  # Landing page + search form
│   │   │   ├── SearchResultsPage.java         # Car listing + filters + sorting
│   │   │   ├── CarDetailPage.java             # Car detail + price breakdown
│   │   │   ├── BookingPage.java               # Checkout form + order summary
│   │   │   ├── ConfirmationPage.java          # Booking success page
│   │   │   ├── LoginPage.java                 # Authentication
│   │   │   └── RegisterPage.java              # New account registration
│   │   │
│   │   └── utils/
│   │       ├── DatePickerUtil.java            # Calendar widget: month navigation + day click
│   │       ├── PriceParser.java               # Parses "EGP 1,250.50" → BigDecimal
│   │       ├── ScreenshotUtil.java            # Captures PNG/Base64 screenshots
│   │       └── WaitUtil.java                  # Static explicit-wait factory methods
│   │
│   └── test/
│       ├── java/com/finalrental/
│       │   ├── tests/
│       │   │   ├── BaseTest.java              # @BeforeMethod / @AfterMethod (driver + screenshot)
│       │   │   ├── HomePageTest.java          # UI load + search form tests
│       │   │   ├── SearchResultsTest.java     # Filters, sorting, card validation
│       │   │   ├── DatePickerTest.java        # Date picker interactions + cross-month
│       │   │   ├── PricingCalculationTest.java# ★ Subtotal / Tax / Total accuracy (critical)
│       │   │   ├── BookingFlowTest.java       # Full E2E booking journey
│       │   │   └── LoginTest.java             # Auth: valid + invalid credentials
│       │   │
│       │   ├── listeners/
│       │   │   └── ExtentReportListener.java  # TestNG listener → HTML Extent Report
│       │   │
│       │   └── data/
│       │       └── TestDataProvider.java      # Centralised Faker + JSON data loader
│       │
│       └── resources/
│           ├── config.properties              # All framework settings
│           ├── log4j2.xml                     # Logging configuration
│           ├── testng.xml                     # Full suite
│           ├── testng-smoke.xml               # Smoke suite
│           ├── testng-regression.xml          # Regression suite (parallel)
│           └── testdata/
│               └── users.json                 # Static user credentials
```

---

## Quick Start

### Prerequisites
- **Java 21 LTS** (Java 24+ causes `ExceptionInInitializerError` with the Selenium/TestNG versions pinned here — use 21)
- Maven 3.8+
- Chrome / Firefox / Edge installed (drivers are managed automatically)

> ⚠️ **Before running any tests:** open `src/test/resources/config.properties` and replace
> `base.url` with the real Final Rental website URL. The shipped placeholder
> (`https://www.finalrental.com`) does not resolve, and tests will fail with
> `ERR_NAME_NOT_RESOLVED` until it's updated.

### 1. Clone & Build
```bash
git clone <repo-url>
cd final-rental-framework
mvn clean compile
```

### 2. Run All Tests
```bash
mvn test
```

### 3. Run Smoke Suite Only
```bash
mvn test -Psmoke
```

### 4. Run Regression Suite (parallel)
```bash
mvn test -Pregression
```

### 5. Override Browser / Environment
```bash
# Firefox + staging environment
mvn test -Dbrowser=firefox -Denv=staging -Dbase.url=https://staging.finalrental.com

# Chrome headless CI mode
mvn test -Dheadless=true

# Edge browser
mvn test -Dbrowser=edge
```

---

## Key Design Decisions

### 1. No Thread.sleep – Ever
All synchronisation is done with `WebDriverWait` + `ExpectedConditions`. The `BasePage` class provides typed wait helpers (`waitForVisible`, `waitForClickable`, `waitForInvisible`, etc.) that every page object reuses.

### 2. Thread-Safe Parallel Execution
`DriverFactory` uses a `ThreadLocal<WebDriver>` so each TestNG thread gets its own browser instance. The `testng-regression.xml` suite runs at `parallel="classes"` with 4 threads.

### 3. Rental Business Logic in a Dedicated Model
`RentalCalculator` encapsulates the pricing domain:
```java
subtotal = dailyRate × rentalDays
tax      = subtotal × (taxRate / 100)   // default 14% VAT
total    = subtotal + tax
```
`PricingCalculationTest` uses this model to assert UI-displayed prices are mathematically correct, using `BigDecimal` throughout to avoid floating-point drift.

### 4. DatePickerUtil – Dynamic Calendar Navigation
Handles any month-navigation picker:
- Navigates forward/backward until the target month/year is in the header
- Falls back to typing dates directly into text inputs for non-calendar pickers
- Supports range pickers (check-in → check-out without re-opening)

### 5. PriceParser – Locale-Aware Currency Parsing
Strips `EGP`, `LE`, `$`, whitespace, and thousands separators, returning a `BigDecimal` for exact numeric comparison.

---

## Configuration Reference (`config.properties`)

| Key | Default | Description |
|---|---|---|
| `base.url` | `https://www.finalrental.com` | Website URL |
| `browser` | `chrome` | `chrome` / `firefox` / `edge` |
| `headless` | `false` | Run browser in headless mode |
| `explicit.wait` | `15` | Seconds for explicit waits |
| `page.load.timeout` | `30` | Max page load time (seconds) |
| `screenshot.on.failure` | `true` | Auto-capture on test failure |
| `rental.tax.rate` | `14.0` | VAT % for pricing assertions |
| `rental.date.format` | `MM/dd/yyyy` | Date picker input format |
| `rental.min.days` | `1` | Minimum allowed rental period |

---

## Reports & Logs

| Artifact | Location |
|---|---|
| Extent HTML Report | `test-output/ExtentReport.html` |
| Screenshots | `src/test/resources/screenshots/` |
| Log file | `logs/automation.log` |

---

## Adding New Tests

1. Create your Page Object in `src/main/java/com/finalrental/pages/` extending `BasePage`.
2. Add `@FindBy` fields and interaction methods (no raw `driver.findElement` in tests).
3. Create your test class in `src/test/java/com/finalrental/tests/` extending `BaseTest`.
4. Annotate tests with `@Test(groups={"smoke"})` or `groups={"regression"}`.
5. Add the class to the relevant `testng-*.xml` suite file.

---

## CI/CD Integration (GitHub Actions example)

```yaml
- name: Run Smoke Tests
  run: mvn test -Psmoke -Dheadless=true -Dbrowser=chrome

- name: Upload Report
  uses: actions/upload-artifact@v3
  with:
    name: extent-report
    path: test-output/ExtentReport.html
```
