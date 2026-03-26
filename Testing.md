# Software Test Plan and Documentation (ENSE 375)

## Introduction
This document outlines the testing strategy, test requirements, and test cases for our software application. [cite_start]The project follows a Test-Driven Development (TDD) methodology to ensure all functional requirements are met and uses JUnit for test case management. [cite_start]The application is built using the Model-View-Controller (MVC) architecture to ensure proper separation of concerns, which is reflected in our integration testing strategy.

## Testing Metrics and Justification
[cite_start]*To ensure a robust and systematically evaluated design, the following metrics are used for design selection and testing:*
* **Metrics Used:** [e.g., Statement coverage > 85%, 100% pass rate on boundary values, specific execution time limits].
* **Justification:** [Explain *why* these metrics were chosen and how they align with your requirements. e.g., "Achieving 100% boundary value pass rate is critical to satisfy our Reliability design constraint, ensuring the application handles edge-case user inputs without crashing."]

## Design Constraints Verification
[cite_start]*Briefly explain how your test suite helps validate the four design constraints you chose for the project (Economic, Regulatory, Reliability, Sustainability, Ethics, or Societal) .*
* **Constraint 1 ([e.g., Reliability]):** Validated through comprehensive Boundary Value and Equivalence Class testing to prevent unexpected crashes.
* **Constraint 2 ([e.g., Regulatory/Security]):** Validated by testing specific Data Flow paths related to user authentication or data access.
* **Constraint 3 ([Name]):** [How testing ensures this]
* **Constraint 4 ([Name]):** [How testing ensures this]

## Test Execution Instructions
* **How to Run the Tests:** [Provide brief, clear instructions on how the grader can execute your test suite, e.g., "Run `mvn test` in the terminal from the root directory" or "Right-click the `src/test/java` folder and select 'Run All Tests' in IntelliJ/Eclipse."]

---

## 1. Function Testing
[cite_start]*The following function was chosen from the application to demonstrate path and data flow testing[cite: 9].*

### 1.1 Path Testing
* **Target Function:** [Name of the function]
* **Test Requirement:** [Explain the paths being tested to ensure full coverage]
* **Test Cases:**
  * **Test Case 1:** [Input parameters] -> Expected Output: [Expected Output] | Actual Output: [Match/Fail] (Tests Path A)
  * **Test Case 2:** [Input parameters] -> Expected Output: [Expected Output] | Actual Output: [Match/Fail] (Tests Path B)

### 1.2 Data Flow Testing
* **Target Function:** [Name of the function]
* **Data Variables Tracked:** [List the variables being tracked through their lifecycle]
* **Test Requirement:** [Explain what definition-use (def-use) pairs must be covered]
* **Test Cases:**
  * **Test Case 1:** [Describe the test case covering specific def-use pairs] -> Expected Output: [Expected Output] | Actual Output: [Match/Fail]

---

## 2. Integration Testing
[cite_start]*Testing interactions within the MVC architecture.*

* [cite_start]**Subset of Units Tested:** [List the specific Model, View, or Controller units chosen for integration testing [cite: 13]]
* **Integration Strategy:** [e.g., Top-down, Bottom-up]
* **Test Requirement:** [Define what constitutes a successful integration between these modules]
* **Test Cases:**
  * **Test Case 1:** [Describe how Unit A and Unit B interact] -> Expected Output: [Expected Outcome] | Actual Output: [Match/Fail]

---

## 3. Validation Testing
[cite_start]*The following techniques are used to validate the application's functionality[cite: 14].*

### 3.1 Boundary Value Testing
* **Target Component/Input:** [Specify what is being tested]
* **Test Requirement:** Application must correctly handle inputs exactly at, just below, and just above defined limits.
* **Boundaries Identified:** [List the upper, lower, and edge cases]
* **Test Cases:**
  * **Test Case 1 (Lower Bound - 1):** [Input] -> Expected Output: [Expected Output] | Actual Output: [Match/Fail]
  * **Test Case 2 (Lower Bound):** [Input] -> Expected Output: [Expected Output] | Actual Output: [Match/Fail]
  * **Test Case 3 (Upper Bound):** [Input] -> Expected Output: [Expected Output] | Actual Output: [Match/Fail]

### 3.2 Equivalence Class Testing
* **Target Component/Input:** [Specify what is being tested]
* **Test Requirement:** Application must process valid classes correctly and gracefully reject invalid classes.
* [cite_start]**Equivalence Classes:** * *Valid Classes:* [Explicitly state valid ranges/types [cite: 41]]
  * [cite_start]*Invalid Classes:* [Explicitly state invalid ranges/types [cite: 41]]
* **Test Cases:**
  * **Test Case 1 (Valid Class):** [Input from valid class] -> Expected Output: [Expected Output] | Actual Output: [Match/Fail]
  * **Test Case 2 (Invalid Class):** [Input from invalid class] -> Expected Output: [Expected Output] | Actual Output: [Match/Fail]

### 3.3 Decision Tables Testing
* **Target Business Logic:** [Describe the rule or logic being tested]
* **Test Requirement:** Application logic must map accurately to the truth table outcomes.
* **Decision Table:**
  * *(Insert a simple markdown table here showing Conditions/Inputs and Actions/Outputs)*
* **Test Cases:**
  * **Test Case 1 (Rule 1):** [Condition state] -> Expected Action: [Expected Action] | Actual Action: [Match/Fail]

### 3.4 State Transition Testing
* **Target Object/System:** [Describe the entity that changes states]
* **Test Requirement:** Object must only transition to allowed states based on specific trigger events.
* **States and Transitions:** [Briefly list the valid states and the events that trigger transitions]
* **Test Cases:**
  * **Test Case 1:** [Initial State] + [Trigger Event] -> Expected New State: [Expected New State] | Actual State: [Match/Fail]

### 3.5 Use Case Testing
* [cite_start]**Target Use Case:** [Explicitly describe the use cases being tested [cite: 41]]
* **Actor(s):** [Who is interacting with the system]
* **Test Requirement:** The system must allow the actor to successfully complete the main scenario and properly handle alternate paths.
* **Main Success Scenario:** [Briefly list the expected happy path]
* **Test Cases:**
  * **Test Case 1 (Happy Path):** [Steps taken] -> Expected Outcome: [Expected Outcome] | Actual Outcome: [Match/Fail]
  * **Test Case 2 (Alternate/Exception Path):** [Steps taken] -> Expected Outcome: [Expected Outcome] | Actual Outcome: [Match/Fail]

---

## 4. Test Suite Execution Summary
* [cite_start]**Execution Status:** All test cases in the test suite have been executed[cite: 4].
* [cite_start]**Output Verification:** All actual outputs were compared with the expected outputs, successfully demonstrating the basic functionality of the detailed final design[cite: 4].