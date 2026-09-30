# Project Folder Hierarchy

## Parking Reservation & Management System

**Technology:** React.js \| Spring Boot \| MySQL

Recommended monorepo layout for the frontend, backend, and database
scripts.

## 1. Root Structure

``` text
parking-management-system/
├── frontend/                         # React.js application
│   ├── public/
│   ├── src/
│   │   ├── app/
│   │   │   ├── App.jsx
│   │   │   ├── routes.jsx
│   │   │   └── providers.jsx
│   │   ├── assets/
│   │   ├── components/
│   │   │   ├── common/
│   │   │   ├── layout/
│   │   │   └── feedback/
│   │   ├── features/
│   │   │   ├── auth/
│   │   │   ├── dashboard/
│   │   │   ├── users/
│   │   │   ├── slots/
│   │   │   ├── entry/
│   │   │   ├── active-parking/
│   │   │   ├── passes/
│   │   │   ├── exit-payment/
│   │   │   ├── rates/
│   │   │   ├── notifications/
│   │   │   └── history/
│   │     ├── services/
│   │   │   ├── apiClient.js
│   │   │   └── authService.js
│   │   ├── hooks/
│   │   ├── utils/
│   │   ├── styles/
│   │   └── main.jsx
│   ├── .env.example
│   ├── package.json
│   └── vite.config.js                # if Vite is selected
├── backend/                          # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/parking/
│   │   │   │   ├── ParkingApplication.java
│   │   │   │   ├── config/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── service/impl/
│   │   │   │   ├── security/
│   │   │   │   ├── scheduler/
│   │   │   │   ├── exception/
│   │   │   │   └── websocket/        # optional
│   │     │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── db/migration/     # if Flyway is selected
│   │   │   └── test/
│   │   │       └── java/com/parking/
│   ├── pom.xml
│   └── .env.example
├── database/
│   ├── schema.sql
│   ├── seed.sql
│   └── README.md
├── docs/
│   ├── TRD.md
│   ├── Folder_Hierarchy.md
│   └── Week_Plan.md
├── .gitignore
└── README.md
```

## 2. Frontend Folder Responsibilities

  -----------------------------------------------------------------------
  Folder                              Contents / responsibility
  ----------------------------------- -----------------------------------
  `app/`                              Application entry, route
                                      definitions, and providers.

  `components/common/`                Reusable buttons, inputs, tables,
                                      badges, dialogs, and loading/error
                                      states.

  `components/layout/`                Sidebar, header, page shell, and
                                      navigation.

  `features/<feature>/`               Feature-specific pages, components,
                                      hooks, and API functions; keep
                                      related UI together.

  `services/`                         Shared HTTP client, token handling,
                                      and cross-feature API utilities.

  `hooks/`                            Reusable React hooks such as data
                                      loading or confirmation behavior.

  `utils/`                            Formatting, date/time, validation
                                      helpers, and constants.

  `styles/`                           Global styles and design tokens.
  -----------------------------------------------------------------------

## 3. Backend Package Responsibilities

  -----------------------------------------------------------------------
  Package                             Contents / responsibility
  ----------------------------------- -----------------------------------
  `controller/`                       REST endpoint classes; parse
                                      requests and return DTOs.

  `dto/`                              Request/response classes, including
                                      validation rules.

  `entity/`                           JPA entities and enums mapped to
                                      MySQL tables.

  `repository/`                       Spring Data JPA repositories and
                                      database queries.

  `service/` and `service/impl/`      Business service interfaces and
                                      implementations.

  `security/`                         Spring Security configuration,
                                      authentication, token handling, and
                                      role checks.

  `scheduler/`                        Scheduled overdue-session checks.

  `exception/`                        Custom exceptions and centralized
                                      API error responses.

  `config/`                           CORS, application, and API
                                      documentation configuration.

  `websocket/`                        Optional push notifications if
                                      implemented.
  -----------------------------------------------------------------------

## 4. Feature File Naming Example

``` text
features/slots/
├── pages/
│   └── SlotsPage.jsx
├── components/
│   ├── SlotTable.jsx
│   ├── SlotForm.jsx
│   └── SlotStatusBadge.jsx
├── slotsApi.js
└── slotsValidation.js
```

## 5. Naming and Organization Rules

-   Use PascalCase for React component files (for example,
    `SlotTable.jsx`).
-   Use camelCase for JavaScript utilities and API functions.
-   Use PascalCase for Java classes and camelCase for methods/variables.
-   Keep database schema changes in versioned migration files if Flyway
    is adopted; otherwise maintain `schema.sql` carefully.
-   Do not place passwords, JWT secrets, or production database
    credentials in Git. Commit only `.env.example` with placeholder
    values.
-   Keep business rules in Spring Boot services, not in React
    components.

## 6. Initial Setup Order

1.  Create the root repository and README.
2.  Create the MySQL database and initial schema.
3.  Create the Spring Boot backend and verify database connectivity.
4.  Implement authentication and a protected test endpoint.
5.  Create the React application and connect its shared API client to
    the backend.
6.  Build features in the sequence defined in the Week Plan.
