# ENSE 375 - Software Testing and Validation: Project Requirements

> **Purpose of this file:** This document is a consolidated reference for Claude Code (or any AI assistant) to understand the full scope, constraints, deliverables, testing requirements, and grading expectations for the ENSE 375 course project. Use this as ground truth when assisting with development, testing, documentation, or architecture decisions.

---

## 1. Project Overview

- **Course:** ENSE 375 - Software Testing and Validation
- **Team Size:** 3 members
- **Timeline:** ~2 months (design/development time-box)
- **Scope:** Small-to-medium software application
- **Final Submission Deadline:** April 10, 2026, 23:59:59
- **Primary Objective:** Design optimal test suites for the application. The testing is the core deliverable, not just the application itself.

### Application Details

> **TODO:** Replace this section with your approved application description, including its purpose, target users, and key features.

- **Application Name:** [TBD]
- **Description:** [TBD]
- **Key Features/Modules:** [TBD]
- **Tech Stack:** [TBD]

---

## 2. Architecture and Methodology Requirements

### 2.1 Mandatory Architecture: Model-View-Controller (MVC)

All code must follow MVC separation:

- **Model:** Business logic, data handling, state management. This is where most unit-testable logic lives.
- **View:** UI/display layer. Keep thin and logic-free where possible.
- **Controller:** Mediates between Model and View. Handles input, invokes model operations, updates view.

When writing or reviewing code, enforce clean MVC boundaries. If logic is in the View or Controller that belongs in the Model, flag it.

### 2.2 Mandatory Methodology: Test-Driven Development (TDD)

Follow the Red-Green-Refactor cycle:

1. **Red:** Write a failing test first.
2. **Green:** Write the minimum code to pass the test.
3. **Refactor:** Clean up while keeping tests green.

Commit history should reflect TDD practice (test commits preceding or accompanying implementation commits).

### 2.3 Test Framework: JUnit

Use JUnit for test cases where applicable. If the application uses a language other than Java, use the equivalent xUnit framework (e.g., pytest for Python, Jest for JavaScript) but confirm with the instructor.

### 2.4 Development Lifecycle: V-Model (Encouraged)

The V-Model pairs each development phase with a corresponding testing phase:

- Requirements <-> Acceptance Testing
- High-Level Design <-> Integration Testing
- Detailed Design <-> Unit Testing
- Implementation <-> Code/Unit Tests

### 2.5 Engineering Design Process (Iterative)

The project must follow this structured process and document it:

1. Define the Problem
2. Do Background Research
3. Specify Requirements
4. Brainstorm, Evaluate, and Choose Solution
5. Develop and Prototype Solution
6. Test Solution
7. If solution meets requirements -> Communicate Results
8. If solution partially/does not meet requirements -> Iterate (make design changes, prototype, test again, review new data)

**Critical for grading:** The rubric explicitly rewards showing multiple design iterations (Solutions 1, 2, and 3 in REPORT.md). Do not skip straight to a final design. Document at least two earlier design concepts and why they were rejected or evolved.

---

## 3. Design Constraints

The application must address **at least four** of the following six constraints. Document how each chosen constraint is addressed in the design.

| Constraint | Description | How to Address (Example) |
|---|---|---|
| **Economic Factors** | Cost of development, deployment, maintenance | Free/open-source tools, lightweight hosting, minimal dependencies |
| **Regulatory Compliance (Security & Access)** | Data protection, access control, authentication | Input validation, role-based access, secure storage of sensitive data |
| **Reliability** | Fault tolerance, error handling, uptime | Exception handling, graceful degradation, input sanitization |
| **Sustainability & Environmental Factors** | Resource efficiency, energy consumption | Efficient algorithms, minimal resource usage, no unnecessary computation |
| **Ethics** | Fair use, bias, transparency, user consent | No hidden data collection, transparent logic, accessible design |
| **Societal Impacts** | Accessibility, inclusivity, community benefit | Usable by diverse users, positive social value, no harm |

> **TODO:** Select your four (or more) constraints and document concrete decisions made for each.

---

## 4. Testing Requirements (This Is the Core of the Project)

### 4.1 Structural Testing (White-Box)

Choose **one function** from the application and apply both of the following:

#### 4.1.1 Path Testing

- Draw the control flow graph (CFG) for the chosen function.
- Identify all independent paths.
- Design test cases to cover all paths.
- Document: CFG diagram, identified paths, test cases, results.

#### 4.1.2 Data Flow Testing

- Identify all definition-use (def-use) pairs for variables in the chosen function.
- Design test cases to cover all-defs, all-uses, or all-du-paths (specify which coverage criterion).
- Document: Variable def-use table, coverage criterion chosen, test cases, results.

### 4.2 Integration Testing

- Choose a **subset of units/modules** to perform integration testing.
- Specify the integration strategy (top-down, bottom-up, sandwich, or big-bang) and justify the choice.
- Design test cases that verify correct interaction between the integrated units.
- Document: Which units are integrated, strategy used, test cases, results.

### 4.3 Validation Testing (Black-Box)

Apply **all five** of the following techniques to appropriate parts of the application:

#### 4.3.1 Boundary Value Testing

- Identify input variables with defined ranges/boundaries.
- Design test cases at boundaries: min, min+1, nominal, max-1, max.
- Consider both normal and robust boundary value analysis.
- Document: Input variables, boundary values, test cases, results.

#### 4.3.2 Equivalence Class Testing

- Partition input domains into valid and invalid equivalence classes.
- Design test cases covering each class (at least one per class).
- Document: Equivalence classes (clearly defined), test cases, results.

#### 4.3.3 Decision Table Testing

- Identify a feature/function with multiple conditions and actions.
- Build the decision table (conditions, actions, rules).
- Design test cases for each rule.
- Document: Decision table, test cases, results.

#### 4.3.4 State Transition Testing

- Identify a stateful component in the application.
- Draw the state transition diagram.
- Build the state transition table.
- Design test cases to cover all transitions (and optionally invalid transitions).
- Document: State diagram, transition table, test cases, results.

#### 4.3.5 Use Case Testing

- Define use cases for core functionality.
- Design test cases based on main success scenarios and alternate/exception flows.
- Document: Use case descriptions, test cases, results.

### 4.4 Testing Documentation Summary

For **every** testing method above, the following must exist:

1. Test requirements (what is being tested and why)
2. Test case design (inputs, expected outputs, preconditions)
3. Test execution results (actual outputs, pass/fail status)
4. Comparison of expected vs. actual outputs

---

## 5. Deliverables and Repository Structure

### 5.1 Required Files

```
project-root/
|-- REPORT.md          # Technical report (see Section 5.2)
|-- TESTING.md         # Test plan and documentation (see Section 5.3)
|-- src/               # Application source code (MVC structure)
|   |-- model/
|   |-- view/
|   |-- controller/
|-- test/              # All test suites (JUnit or equivalent)
|-- README.md          # (Optional, or merge with REPORT.md)
```

> Adjust folder structure to match your tech stack, but maintain clear MVC separation.

### 5.2 REPORT.md Structure

Follow the Project File Template from UR Courses. Key sections:

| Section | Content | Deliverable # | Weight |
|---|---|---|---|
| 2.1 - Problem Definition | Problem statement, motivation, scope | 1 (Jan 23) | 10% |
| 2.2 - Design Constraints & Requirements | Chosen constraints, functional/non-functional requirements | 2 (Jan 30) | 10% |
| 3.1-3.2 - Iterative Design (Solutions 1 & 2) | At least two design alternatives, evaluation criteria, decision-making rationale | 3 (Feb 13) | 10% |
| 3.3 - Final Design, Implementation & Testing | Final architecture, implementation details, full testing documentation | 4 (Mar 27) | 60% |
| 4-5 - Teamwork & Communication | Team roles, meeting logs, conflict resolution, contribution tracking | Ongoing | 10% |

### 5.3 TESTING.md Structure

This file should be concise but must contain minimum necessary information for all testing techniques:

- For each testing method: explain the approach and provide any supporting artifacts (e.g., equivalence classes for EC testing, use cases for use case testing, state diagrams for state transition testing).
- Not a long document, but technically complete.

### 5.4 Code Quality Requirements

- All code must be **well-commented**.
- All test suites must be **well-commented**.
- The GitHub repo must be **public** (at minimum for the duration of ENSE 375).
- Commit frequently. The instructor reviews GitHub commit history.

---

## 6. Grading Rubric Summary

### 6.1 Problem Definition and Design Requirements (10%)

| Level | Criteria |
|---|---|
| **Exceed** | Clear problem/requirements. Constraints (regulatory, environmental, social, ethical, safety) explicitly identified and considered. |
| **Meet** | Clear problem/requirements. Constraints identified and considered. |
| **Marginal** | Vague requirements. Constraints superficially considered. |
| **Below** | Unclear requirements. Constraints not identified. |

### 6.2 Iterative Engineering Design Process and Design Selection (20%)

| Level | Criteria |
|---|---|
| **Exceed** | Systematic design process. Effective iterations addressing testing requirements AND design constraints. Multiple design concepts evaluated with formal decision-making methods producing a novel solution. Metrics for design selection and testing are clear, aligned, and justified. |
| **Meet** | Design process followed with iterations. Multiple concepts evaluated with formal methods. Clear, aligned metrics. |
| **Marginal** | Superficial process/iterations. Multiple concepts but no formal decision-making. Vague metrics. |
| **Below** | No design process. Single concept. No formal methods. Unclear metrics. |

### 6.3 Final Design Implementation and Testing (60%)

| Level | Criteria |
|---|---|
| **Exceed** | Prototype satisfies all constraints with exceptional functionality. Test suite is systematically designed and comprehensive, including ALL specified testing methods. Every method has systematically designed test requirements and cases. All test cases executed with expected vs. actual output comparison. |
| **Meet** | Prototype satisfies constraints with basic functionality. Test suite covers almost all methods. Test requirements and cases designed for all considered methods. Almost all test cases executed with comparison. |
| **Marginal** | Prototype satisfies most constraints marginally. Test suite covers some methods. Incomplete test requirements/cases. Marginal execution and comparison. |
| **Below** | Prototype satisfies few constraints. No basic functionality demonstrated. Incomplete test suite. Unable to test properly. |

### 6.4 Collaborative Teamwork and Communication Skills (10%)

| Level | Criteria |
|---|---|
| **Exceed** | Skillful collaboration. Effective oral, written, and graphical communication. Well-organized, well-written documentation. All necessary information provided. |
| **Meet** | Acceptable collaboration. Effective communication. Well-organized documentation with no errors. All necessary information provided. |
| **Marginal** | Some collaboration ability. Readable documentation with some errors. Most important information provided. |
| **Below** | No demonstrated collaboration. Documentation needs significant editing. Crucial information missing. |

---

## 7. Key Reminders for Claude Code

1. **Testing is the priority.** The application exists to be tested. When making architecture or implementation decisions, optimize for testability.
2. **MVC is mandatory.** Keep business logic in the Model layer where it can be unit-tested independently.
3. **TDD workflow.** Write tests before implementation. Commit history should reflect this.
4. **Document everything in TESTING.md.** Every testing technique needs its supporting artifacts (CFGs, def-use tables, equivalence classes, decision tables, state diagrams, use cases).
5. **Show iteration in REPORT.md.** At least three design solutions with clear evaluation and selection rationale.
6. **Address at least four design constraints** with concrete, documented decisions.
7. **JUnit (or equivalent).** All tests should be runnable and automated where possible.
8. **Late penalty:** 10% per day. Hard deadlines.
9. **The rubric rewards systematically designed test suites.** Haphazard or incomplete testing is the fastest way to lose marks on the 60% component.

---

## 8. Submission Checklist

- [ ] Application is functional and demonstrates core features
- [ ] MVC architecture is clearly implemented and separated
- [ ] REPORT.md is complete with all sections per template
- [ ] TESTING.md is complete with documentation for all testing techniques
- [ ] Path testing: CFG, paths, test cases, results
- [ ] Data flow testing: def-use pairs, coverage, test cases, results
- [ ] Integration testing: strategy, units, test cases, results
- [ ] Boundary value testing: boundaries, test cases, results
- [ ] Equivalence class testing: classes, test cases, results
- [ ] Decision table testing: table, test cases, results
- [ ] State transition testing: diagram, table, test cases, results
- [ ] Use case testing: use cases, test cases, results
- [ ] All code and tests are well-commented
- [ ] GitHub repo is public
- [ ] Commit history reflects TDD and iterative development
- [ ] Email sent to instructor with GitHub project name and commit hash
- [ ] At least four design constraints are addressed and documented
- [ ] Team management sections are updated throughout

---

*Last updated: March 25, 2026*
