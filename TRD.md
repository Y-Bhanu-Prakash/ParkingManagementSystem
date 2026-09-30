# Technical Requirements Document (TRD)

## Parking Reservation & Management System

**Technology:** React.js \| Spring Boot \| MySQL\
**Version:** 1.0

## 1. Document Purpose

This Technical Requirements Document translates the project requirements
into a practical technical design for development using React.js, Spring
Boot, and MySQL. It defines the application structure, modules, data
model, API contract, security approach, and implementation assumptions.

## 2. Project Overview

The system is a staff-operated parking facility application. An Owner
configures the facility and monitors operations. An Attendant records
vehicle entry and exit, assigns parking slots, issues passes, and
records payments. Customers do not log in or manage bookings directly.

## 3. Objectives

-   Maintain a current view of parking slot availability and occupancy.
-   Record vehicle entry, parking duration, assigned slot, and pass
    details.
-   Detect overdue parking and calculate additional charges according to
    configured rates.
-   Verify parking passes during exit, record payment, and release the
    slot.
-   Provide role-based access, operational notifications, and parking
    history.

## 4. Scope

### 4.1 Included

-   Owner and Attendant authentication and role-based authorization.
-   Staff account management.
-   Dashboard metrics and active parking list.
-   Slot creation, editing, status changes, and vehicle-type
    compatibility.
-   Vehicle entry, slot assignment, parking pass generation, and session
    tracking.
-   Overdue monitoring, pricing calculation, payment recording, vehicle
    exit, notifications, and reports/history.

### 4.2 Excluded

-   Customer accounts or self-service booking.
-   Payment gateway integration; payments are recorded by staff.
-   Multiple facilities, subscriptions, insurance, marketplace,
    GPS/maps, and mobile application.
-   Complex dynamic pricing beyond configured parking-rate rules.

## 5. Users and Permissions

  -----------------------------------------------------------------------
  Role                                Allowed operations
  ----------------------------------- -----------------------------------
  OWNER                               Manage staff; configure slots and
                                      rates; view dashboard, active
                                      sessions, overdue vehicles,
                                      payments, notifications, and
                                      history.

  ATTENDANT                           Register vehicle entry; view
                                      available slots; assign compatible
                                      slot; issue/reprint pass; process
                                      exit; record payment; view active
                                      parking and overdue information.
  -----------------------------------------------------------------------

Authorization must be enforced by the backend. Hiding a button in React
is not a security control. Attendants must not be able to change
facility configuration or pricing.

## 6. Technology Architecture

React.js provides the browser interface. It communicates with Spring
Boot through REST APIs using JSON over HTTP. Spring Boot contains
controllers, services, repositories, security, scheduled overdue checks,
and business rules. MySQL stores persistent application data. Spring
Data JPA/Hibernate is the proposed persistence layer.

  -----------------------------------------------------------------------
  Layer                   Technology              Responsibility
  ----------------------- ----------------------- -----------------------
  Presentation            React.js                Screens, forms,
                                                  validation feedback,
                                                  tables, dashboards, API
                                                  calls, and role-aware
                                                  navigation.

  Application/API         Spring Boot             REST endpoints,
                                                  authentication,
                                                  authorization, business
                                                  rules, validation,
                                                  transactions,
                                                  scheduling, and error
                                                  handling.

  Persistence             MySQL + Spring Data JPA Persistent storage for
                                                  staff, slots, rates,
                                                  sessions, passes,
                                                  payments, and
                                                  notifications.
  -----------------------------------------------------------------------

## 7. Functional Modules

### 7.1 Authentication & Authorization

Staff login/logout, password hashing, token/session validation, and
role-based endpoint protection. No customer login.

### 7.2 User Management

Owner can create, update, deactivate, and assign roles to staff. Prevent
accidental removal of the final active Owner.

### 7.3 Dashboard

Show total, available, occupied, overdue, and maintenance slots; active
parking list; recent payments and notifications.

### 7.4 Slot Management

Create and edit slot code/type; activate or deactivate slots; set
AVAILABLE, OCCUPIED, RESERVED, OVERDUE, or MAINTENANCE state. Do not
delete a slot with an active session.

### 7.5 Vehicle Entry

Capture vehicle number, vehicle type, expected duration, entry time, and
attendant. Validate required fields and available compatible slots.

### 7.6 Slot Assignment

Select a compatible available slot. Assignment and session creation must
occur in one database transaction to prevent double allocation.

### 7.7 Parking Pass

Generate a unique pass ID, slot, vehicle, entry time, allowed-until
time, and pass password. Store only a secure hash of the password.

### 7.8 Parking Session Management

Track ACTIVE and COMPLETED sessions, entry/exit timestamps, expected
duration, assigned slot, and final charges.

### 7.9 Time Monitoring & Overdue

A scheduled job checks active sessions against allowed-until time
(proposed interval: 30 seconds). Mark overdue and create a notification
once per overdue transition.

### 7.10 Pricing & Charges

Calculate initial and additional charges from Owner-configured rates.
Store the rate snapshot or calculated charge details with the
session/payment so later rate changes do not rewrite completed
transactions.

### 7.11 Payment Management

Record payment amount, method, status, timestamp, and staff member. No
payment gateway is included.

### 7.12 Vehicle Exit

Verify pass ID and pass password; ensure the session is active;
calculate payable amount; record payment; complete session; release slot
atomically.

### 7.13 Notifications

Create notifications for overdue events and operational updates.
Dashboard can refresh by polling initially; WebSocket push is optional.

### 7.14 Parking History & Reports

Search completed sessions by date, vehicle number, pass ID, and
attendant; show duration and payment details.

## 8. Frontend Requirements

### 8.1 Suggested React structure

Use feature-based folders so each business area keeps its pages,
reusable components, and API functions together. React Router can manage
navigation. A shared API client should attach the authentication token
and handle common errors.

Suggested pages: - Login - Dashboard - Slots - Vehicle Entry - Active
Parking - Exit/Payment - History - Staff - Rates - Notifications

Shared components: - AppLayout, Sidebar, Header, ProtectedRoute,
RoleGuard - DataTable, StatusBadge, ConfirmDialog, FormField -
LoadingState and ErrorMessage

Feature API files should call backend endpoints; components should not
contain database or business-rule logic. Use controlled forms and show
server validation messages.

## 9. Backend Requirements

### 9.1 Suggested package structure

``` text
com.parking
├── config/
├── controller/
├── dto/
├── entity/
├── repository/
├── service/
│   └── impl/
├── security/
├── scheduler/
├── exception/
└── websocket/   (optional)
```

### 9.2 Backend design rules

-   Controllers handle HTTP input/output; services enforce business
    rules; repositories handle database access.
-   Use DTO validation annotations for required fields, formats, and
    positive durations.
-   Use `@Transactional` for slot assignment/session creation and
    exit/payment/session completion.
-   Use a global exception handler to return consistent error responses.
-   Use database constraints and unique indexes in addition to
    application validation.

## 10. Database Design

The following is the proposed relational schema at logical level. Exact
column types and constraints should be finalized during implementation.

  --------------------------------------------------------------------------
  Table                   Important columns          Purpose
  ----------------------- -------------------------- -----------------------
  `users`                 `id` PK, `name`,           Owner and Attendant
                          `username` UNIQUE,         accounts.
                          `password_hash`, `role`,   
                          `active`, `created_at`     

  `parking_slots`         `id` PK, `slot_code`       Facility slot inventory
                          UNIQUE, `slot_type`,       and current state.
                          `status`, `active`,        
                          `created_at`               

  `parking_rates`         `id` PK, `vehicle_type`,   Owner-configured rate
                          `base_duration_minutes`,   rules.
                          `base_amount`,             
                          `extra_unit_minutes`,      
                          `extra_unit_amount`,       
                          `active`, `effective_from` 

  `parking_sessions`      `id` PK, `vehicle_number`, Current and historical
                          `vehicle_type`, `slot_id`  parking records.
                          FK, `attendant_id` FK,     
                          `entry_time`,              
                          `allowed_until`,           
                          `exit_time`, `status`,     
                          `base_charge`,             
                          `extra_charge`,            
                          `total_charge`             

  `parking_passes`        `id` PK, `pass_code`       Pass credentials
                          UNIQUE, `session_id` FK    associated with a
                          UNIQUE, `password_hash`,   session.
                          `issued_at`, `active`      

  `payments`              `id` PK, `session_id` FK,  Staff-recorded
                          `amount`, `method`,        payments.
                          `status`, `paid_at`,       
                          `recorded_by` FK,          
                          `reference_note`           

  `notifications`         `id` PK, `type`,           Overdue and operational
                          `message`, `session_id` FK notifications.
                          nullable, `created_at`,    
                          `read_at`, `created_by` FK 
                          nullable                   
  --------------------------------------------------------------------------

### 10.1 Relationships

-   One parking slot can have many sessions over time; only one active
    session may use a slot at a time.
-   One session has one parking pass; a session may have multiple
    payment records if partial/retry handling is later enabled. Initial
    implementation may enforce one successful payment per exit.
-   A user can record many sessions and payments as the
    attendant/recorder.
-   Notifications may reference a session; the session reference can be
    nullable for general alerts.

### 10.2 Data integrity

-   Use foreign keys for related records and unique constraints for
    username, slot code, and pass code.
-   Use an enum or constrained string for role, slot status, session
    status, payment status, and vehicle type.
-   Persist all operational data in MySQL so it survives application
    restarts.
-   Use database transactions for multi-step operations.

## 11. REST API Requirements

  --------------------------------------------------------------------------
  Method & path                          Purpose / access
  -------------------------------------- -----------------------------------
  `POST /api/auth/login`                 Authenticate staff; public.

  `POST /api/auth/logout`                Logout/invalidate token if token
                                         strategy supports it;
                                         authenticated.

  `GET /api/users`                       List staff; OWNER.

  `POST /api/users`                      Create staff; OWNER.

  `PUT /api/users/{id}`                  Update staff; OWNER.

  `PATCH /api/users/{id}/status`         Activate/deactivate staff; OWNER.

  `GET /api/dashboard/summary`           Dashboard counts; authenticated,
                                         with role-appropriate fields.

  `GET /api/slots`                       List slots; authenticated.

  `POST /api/slots`                      Create slot; OWNER.

  `PUT /api/slots/{id}`                  Edit slot; OWNER.

  `PATCH /api/slots/{id}/status`         Change slot state; OWNER, with
                                         business-rule checks.

  `GET /api/rates`                       List rates; authenticated.

  `PUT /api/rates/{id}`                  Update rate; OWNER.

  `POST /api/sessions/entry`             Create entry/session and assign
                                         slot; ATTENDANT.

  `GET /api/sessions/active`             List active sessions;
                                         authenticated.

  `GET /api/sessions/{id}`               Session details; authenticated.

  `GET /api/passes/{passCode}`           Find pass/session for exit;
                                         authenticated; never return
                                         password hash.

  `POST /api/sessions/{id}/exit`         Verify pass, record payment,
                                         complete session, release slot;
                                         ATTENDANT.

  `GET /api/notifications`               List notifications; authenticated.

  `PATCH /api/notifications/{id}/read`   Mark notification read;
                                         authenticated.

  `GET /api/reports/history`             Search completed parking history;
                                         authenticated, as permitted.
  --------------------------------------------------------------------------

API responses should use a consistent shape, for example
`{ data, message, timestamp }`. Errors should include a stable error
code and field-level validation details where applicable. Do not return
stack traces or sensitive credential data.

## 12. Core Business Rules

1.  A vehicle can be assigned only to an active AVAILABLE slot
    compatible with its vehicle type.
2.  Entry must fail cleanly when no compatible slot is available.
3.  Pass ID must be unique; the pass password must be stored as a hash
    and verified securely.
4.  Only an ACTIVE session can be checked out. A completed session
    cannot be exited again.
5.  Overdue status is based on current time exceeding allowed-until
    time; avoid duplicate overdue notifications on every scheduler run.
6.  Successful exit must record payment, complete the session, and
    release the slot in one transaction.
7.  Slots with active sessions cannot be deactivated, removed, or marked
    MAINTENANCE.
8.  Extra-charge rounding and any grace period must be configured
    explicitly before implementation.

## 13. Security Requirements

-   Hash staff passwords using a password-hashing algorithm such as
    BCrypt; never store plaintext passwords.
-   Use HTTPS in deployment. Document the HTTP exception for local
    development.
-   Protect APIs with Spring Security and role-based authorization.
-   Validate and normalize user input; use parameterized database access
    through JPA.
-   Do not expose password hashes, tokens, or unnecessary personal data
    in API responses or logs.
-   Apply CORS restrictions to the frontend origin and configure token
    expiry/logout behavior.

## 14. Non-Functional Requirements

  -----------------------------------------------------------------------
  Area                                Requirement
  ----------------------------------- -----------------------------------
  Reliability                         Database-backed records remain
                                      available after application
                                      restart.

  Consistency                         Concurrent entry/exit operations
                                      must not create duplicate slot
                                      assignments or inconsistent session
                                      states.

  Usability                           Staff workflows should minimize
                                      steps and provide clear validation
                                      and status feedback.

  Maintainability                     Separate UI, controller, service,
                                      and persistence responsibilities;
                                      use feature-based organization.

  Performance                         Paginate history and staff/slot
                                      lists; index active sessions, pass
                                      code, and vehicle number lookups.

  Auditability                        Record who performed entry, exit,
                                      rate changes, and payment
                                      recording.
  -----------------------------------------------------------------------

## 15. Assumptions & Decisions to Confirm

-   Single parking facility and one currency (INR) for the initial
    release.
-   No customer-facing reservation flow despite the project name;
    operations are staff-managed.
-   Confirm whether `RESERVED` is needed in the first version, since
    customer booking is excluded.
-   Confirm extra-charge rounding (per started hour or completed hour),
    grace period, and maximum charge rules.
-   Confirm accepted payment methods and whether failed/partial payments
    must be supported.
-   Confirm whether attendants can view payment totals and historical
    reports.
-   Confirm whether the initial Owner account is seeded or created
    through a setup process.

## 16. Acceptance Criteria

-   Owner can log in and manage staff, slots, and rates; Attendant
    cannot access Owner-only operations.
-   Attendant can register a vehicle and the system assigns only a
    compatible available slot.
-   A unique pass is issued and its password is verified without
    exposing the stored hash.
-   The scheduler marks eligible active sessions overdue and does not
    duplicate alerts each run.
-   Exit records payment, completes the session, and makes the slot
    available; invalid pass or inactive session is rejected.
-   Dashboard and history reflect persisted data after refresh and
    application restart.
