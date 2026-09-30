# Development Week Plan

## Parking Reservation & Management System

**Technology:** React.js \| Spring Boot \| MySQL\
**Duration:** 12 weeks \| **Team:** 4 members

## 1. Planning Basis

-   Team size: 4 members (roles can be shared or rotated).
-   One facility; staff-operated workflow; no customer login or payment
    gateway.
-   Each week ends with a demonstrable deliverable and a short review.

## 2. Team Responsibilities

  -------------------------------------------------------------------------
  Member                  Primary responsibility    Shared work
  ----------------------- ------------------------- -----------------------
  Member 1                React layout, routing,    Integration and UI
                          authentication UI,        testing
                          dashboard                 

  Member 2                React feature screens:    Form validation and
                          slots, entry, active      usability
                          parking, exit/history     

  Member 3                Spring Boot APIs,         API testing and code
                          security, users, slots,   review
                          DTOs                      

  Member 4                Database schema,          Integration, test data,
                          session/pricing/payment   documentation
                          services, scheduler       
  -------------------------------------------------------------------------

These are suggested ownership areas, not isolated work. All members
should review API contracts and test the complete workflow together.

## 3. Week-by-Week Plan

  -------------------------------------------------------------------------
  Week              Focus             Activities          Deliverable
  ----------------- ----------------- ------------------- -----------------
  1                 Requirement       Review BRD; confirm Approved TRD,
                    analysis and      roles, workflow,    task board,
                    design            slot states, charge architecture and
                                      rules, and          ER draft.
                                      assumptions.        
                                      Finalize TRD,       
                                      folder hierarchy,   
                                      use cases, and API  
                                      draft.              

  2                 Repository and    Create Git          Frontend and
                    environment setup repository, React   backend run
                                      app, Spring Boot    locally; backend
                                      project, MySQL      connects to
                                      database,           MySQL.
                                      environment         
                                      configuration, and  
                                      basic health        
                                      endpoint.           

  3                 Database and      Create users and    Owner and
                    authentication    slot tables;        Attendant can log
                                      implement staff     in;
                                      login, password     role-protected
                                      hashing,            API works.
                                      token/session       
                                      strategy, role      
                                      authorization, and  
                                      protected routes.   

  4                 User and slot     Implement staff     Owner can manage
                    management        management and slot staff and slots;
                                      CRUD/status APIs;   Attendant is
                                      build React screens restricted.
                                      for staff and       
                                      slots; validate     
                                      slot state          
                                      transitions.        

  5                 Dashboard and     Implement dashboard Dashboard counts
                    available-slot    summary and slot    and slot list
                    view              filtering; connect  load from MySQL.
                                      frontend to APIs;   
                                      add loading and     
                                      error states.       

  6                 Vehicle entry and Build entry form    Vehicle entry
                    slot assignment   and service logic;  creates one
                                      check vehicle type  active session
                                      compatibility;      and occupies one
                                      assign a slot and   compatible slot.
                                      create session in   
                                      one transaction.    

  7                 Parking pass      Generate unique     Pass is generated
                                      pass code and       and can be
                                      password; store     retrieved without
                                      password hash;      exposing its
                                      display/print pass  secret.
                                      details; implement  
                                      pass lookup.        

  8                 Pricing and       Implement           Overdue sessions
                    overdue           configured rates    are identified;
                    monitoring        and charge          charges follow
                                      calculation; create agreed rules.
                                      scheduled overdue   
                                      checker; prevent    
                                      duplicate overdue   
                                      notifications.      

  9                 Exit and payment  Implement pass      Valid exit
                    recording         verification,       completes session
                                      payment recording,  and frees slot;
                                      session completion, invalid pass is
                                      and slot release in rejected.
                                      one transaction;    
                                      build exit UI.      

  10                Notifications and Implement           Staff can view
                    history           notification        alerts and
                                      list/read state and retrieve parking
                                      searchable          history.
                                      completed-session   
                                      history; add        
                                      pagination and      
                                      filters.            

  11                Integration and   Run end-to-end      Test report,
                    testing           scenarios; test     defect list, and
                                      invalid inputs,     stable integrated
                                      role restrictions,  build.
                                      concurrent slot     
                                      assignment, overdue 
                                      behavior, and       
                                      payment/exit        
                                      failures; fix       
                                      defects.            

  12                Finalization and  Polish UI, verify   Release
                    demonstration     setup instructions, candidate, final
                                      prepare sample      documents, and
                                      data, finalize      demo-ready
                                      documentation, and  workflow.
                                      rehearse project    
                                      demo.               
  -------------------------------------------------------------------------

## 4. Testing Checklist

-   **Authentication:** valid/invalid login, inactive user, role
    restrictions, token expiry/logout behavior.
-   **Slots:** create/edit, incompatible vehicle type, maintenance
    transition, prevent deactivation while occupied.
-   **Entry:** no available slot, duplicate/concurrent entry, invalid
    vehicle number, database rollback.
-   **Pass:** unique code, wrong password, unknown pass, completed pass,
    secret not exposed.
-   **Overdue/pricing:** boundary at allowed-until time, repeated
    scheduler runs, rate changes, charge rounding.
-   **Exit/payment:** successful exit, failed payment record, repeated
    exit, slot released only after successful transaction.
-   **History/dashboard:** counts match persisted records; filters and
    pagination work.

## 5. Weekly Team Routine

1.  Start of week: agree on small tasks and API/data dependencies.
2.  During the week: commit code regularly and review each other's
    changes before merging.
3.  End of week: demonstrate the deliverable, run tests, record
    unresolved issues, and update the README/project diary.
4.  Keep a shared definition of done: code merged, tested, connected to
    the database, and documented.

## 6. Milestones

  -----------------------------------------------------------------------
  Milestone                           Target
  ----------------------------------- -----------------------------------
  M1 --- Foundation                   End of Week 3: application
                                      skeleton, database connection, and
                                      secure login.

  M2 --- Core operations              End of Week 7: slot management,
                                      dashboard, entry, assignment, and
                                      pass.

  M3 --- Complete workflow            End of Week 9: overdue pricing,
                                      payment recording, and exit.

  M4 --- Release candidate            End of Week 12: notifications,
                                      history, testing, documentation,
                                      and demo.
  -----------------------------------------------------------------------
