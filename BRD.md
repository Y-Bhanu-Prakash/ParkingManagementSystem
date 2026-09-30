## Parking Reservation & Management System

### 1. Basic idea

A parking facility has a fixed number of parking slots.

For example:

```text
Parking Facility
│
├── Slot 001 → AVAILABLE
├── Slot 002 → OCCUPIED
├── Slot 003 → AVAILABLE
├── Slot 004 → RESERVED
├── Slot 005 → OVERDUE
└── ...
```

The system is operated by the **Parking Owner** and **Parking Attendant/Manager**.

A vehicle owner/customer **does not need an account**.

---

# 2. Users

I would use only **two staff roles**.

### Parking Owner

The owner manages the entire parking facility.

They can:

* Configure total parking slots
* Add/remove parking slots
* Define slot types
* Set parking rates
* View all occupied/free slots
* View active parking
* View overdue vehicles
* View payment information
* View notifications
* View parking history

---

### Parking Attendant

This is the person at the entrance/exit gate.

This is probably the role you were trying to describe.

The attendant handles:

```text
Vehicle Entry
↓
Vehicle Details
↓
Parking Slot Assignment
↓
Parking Pass Generated
↓
Vehicle Parks
↓
Vehicle Exit
↓
Payment / Extra Charge
↓
Slot Released
```

They can:

* Register vehicle entry
* Assign parking slots
* Generate parking pass
* Record payment
* Check active vehicles
* Process vehicle exit
* Handle overdue parking
* View available slots

They **cannot** modify the parking facility configuration.

---

# 3. Customer / Vehicle Owner

There is **no customer login**.

This is important.

When someone arrives:

```text
Customer
↓
Parking Attendant
```

The attendant enters:

```text
Vehicle Number
Vehicle Type
Entry Time
Parking Duration
```

The system generates:

```text
Parking Pass

Pass ID: PK-10245
Slot: B-014
Password: 4728
Entry: 10:30 AM
Allowed Until: 1:30 PM
```

The customer receives the **slot/pass information and 4-digit password**.

---

# 4. Vehicle Types

Keep this simple.

```text
BIKE
CAR
SUV
TRUCK
OTHER
```

You can also make slots vehicle-specific.

For example:

```text
Bike Slots
B01
B02
B03

Car Slots
C01
C02
C03

Truck Slots
T01
T02
```

The system should not assign a bike to a car-only slot.

---

# 5. Parking Entry

Suppose a car arrives.

The attendant enters:

```text
Vehicle Number:
AP39AB1234

Vehicle Type:
CAR

Parking Duration:
2 hours
```

System finds an available slot:

```text
C-018
```

Then:

```text
Slot C-018 → OCCUPIED
```

The system generates:

```text
Parking Pass
────────────────────
Pass ID: PK-10041
Slot: C-018
Password: 5832

Entry:
10:15 AM

Valid Until:
12:15 PM

Amount:
₹60
```

---

# 6. Time limit

This is one of the main features you described.

Suppose:

```text
Entry:
10:15 AM

Allowed until:
12:15 PM
```

At:

```text
12:16 PM
```

the system detects:

```text
OVERDUE
```

The dashboard can show:

```text
⚠ OVERDUE PARKING

Slot: C-018
Vehicle: AP39AB1234
Exceeded: 16 minutes
Extra Charge: ₹20
```

---

# 7. Continuous checking

We can have a backend background process.

For example:

```text
Every 30 seconds
↓
Check active parking
↓
Compare current time
↓
Is allowed time exceeded?
↓
YES
↓
Mark OVERDUE
↓
Generate notification
```

So the system doesn't depend on someone manually checking every vehicle.

---

# 8. Notifications

The owner/attendant dashboard can receive notifications.

For example:

```text
Notifications
────────────────────────────

🔴 Vehicle AP39AB1234 exceeded
parking time by 25 minutes.

🔴 Vehicle TS09XY4521 exceeded
parking time by 10 minutes.

🟢 Vehicle AP40CD1020 exited.
Slot B-012 is now available.
```

We could use **WebSockets** so the notification appears immediately.

---

# 9. Extra charges

Don't make the pricing system complicated initially.

Example:

```text
Car

First 2 hours → ₹60

Every additional hour → ₹30
```

If the customer stays:

```text
2 hours  → ₹60
3 hours  → ₹90
4 hours  → ₹120
```

The backend calculates the additional amount when they exit.

---

# 10. Exit

At the exit gate, the attendant enters:

```text
Parking Pass:
PK-10041

Password:
5832
```

The system verifies:

```text
Pass exists?          ✓
Password correct?     ✓
Vehicle still active? ✓
```

Then calculates:

```text
Parking charge       ₹60
Extra charge         ₹30
────────────────────────
Total                ₹90
```

After payment:

```text
Pass → COMPLETED

Slot C-018 → AVAILABLE
```

---

# 11. Owner Dashboard

The owner gets a simple dashboard:

```text
PARKING MANAGEMENT
────────────────────────────────

Total Slots       100
Available          42
Occupied           51
Overdue             5
Maintenance         2

────────────────────────────────

ACTIVE PARKING

Slot     Vehicle       Type     Status
C-001    AP39AB1234    CAR      NORMAL
C-002    AP40CD5678    CAR      OVERDUE
B-012    TS09XY1234    BIKE     NORMAL
B-013    AP10AA1111    BIKE     OVERDUE

────────────────────────────────

NOTIFICATIONS

🔴 5 vehicles exceeded parking time
🟢 3 slots became available
```

---

# 12. Slot Management

Owner can configure slots.

For example:

```text
Slot Number     Type       Status
────────────────────────────────────
C-001           CAR        AVAILABLE
C-002           CAR        OCCUPIED
C-003           CAR        MAINTENANCE

B-001           BIKE       AVAILABLE
B-002           BIKE       AVAILABLE

T-001           TRUCK      AVAILABLE
```

Owner can:

```text
Add Slot
Edit Slot
Deactivate Slot
Change Slot Type
Mark Maintenance
```

---

# 13. What we should NOT build

This is important for keeping the project simple.

**No:**

* Customer accounts
* Customer registration
* Vehicle-owner profiles
* Insurance
* Online parking marketplace
* GPS
* Maps
* Payment gateway
* Multiple parking locations
* Monthly subscription
* Complex pricing rules
* Mobile application
* Database

We can add some of these later if the basic project becomes too easy.

---

# 14. Runtime memory

The backend can maintain something like:

```text
MemoryStore
│
├── users
├── slots
├── active_parking
├── parking_history
├── payments
├── notifications
└── passes
```

Everything disappears when the server restarts.

That's completely acceptable for this project.

---

# 15. Overall architecture

```text
                  PARKING SYSTEM
                        │
            ┌────────────┴────────────┐
            │                         │
      OWNER                  ATTENDANT
            │                         │
            │                    Vehicle Entry
            │                         │
            │                    Slot Assignment
            │                         │
            │                    Parking Pass
            │                         │
            └────────────┬────────────┘
                        │
                  BACKEND
                        │
      ┌───────────────┼────────────────┐
      │               │                │
Slot Manager    Parking Engine   Notification
                        │
                        │
                  Time Checker
                        │
                        ▼
                  OVERDUE
                        │
                        ▼
                  Extra Charge
```

### The final scope I'd recommend

**Roles:**

```text
OWNER
ATTENDANT
```

**Core modules:**

```text
Authentication
Slot Management
Vehicle Entry
Parking Pass
Parking Management
Time/Overdue Detection
Pricing
Payment Recording
Vehicle Exit
Notifications
Parking History
```

That's **simple enough to finish**, but it has enough backend logic to make it a meaningful full-stack project.

And I especially like the **entry → assigned slot → generated 4-digit password → time limit → automatic overdue detection → extra charge → exit → slot released** flow. That gives the project a clear business workflow rather than just a collection of CRUD screens.