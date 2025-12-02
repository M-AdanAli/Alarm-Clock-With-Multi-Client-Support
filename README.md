# ⏰ Alarm Clock With Concurrency Support

![Java](https://img.shields.io/badge/Java-17+-blue?logo=java&logoColor=white)
![Threads](https://img.shields.io/badge/Concurrency-Multithreading-orange)
![License](https://img.shields.io/badge/license-MIT-blue)

A basic Java project that serves as a robust example of **Java concurrency primitives** (`Semaphore` and `ArrayBlockingQueue`) applied to a simple alarm clock system. This project simulates multiple threads (clients) concurrently setting and waiting for alarms to ring.

---

## 📖 Table of Contents

- [Core Concept](#-core-concept)
- [Project Structure](#-project-structure)
- [Key Components](#-key-components)
- [How It Works](#-how-it-works)
- [Concurrency Implementation](#-concurrency-implementation)
- [How to Run](#-how-to-run)
- [License](#-license)

---

## 🧠 Core Concept

The project simulates a centralized **Alarm Service** that manages a limited number of alarm slots. It uses Java's built-in concurrency mechanisms to enforce:

1.  **Bounded Buffer Problem**: Limiting the number of alarms that can be active simultaneously (`MAX_ALARMS`).
2.  **Thread Safety**: Ensuring multiple concurrent threads (simulated clients) can safely add alarms and wait for them to trigger.

This is an **in-memory, multi-threaded application**—it does not use network sockets but demonstrates the fundamental logic required for managing concurrent requests in a backend system.

---

## 📦 Project Structure

The structure follows standard Java package naming conventions:

```
.
└── com.adanali.java
     ├── model 
     │   └── Alarm.java (Data model for an alarm) 
     ├── service 
     │   └── AlarmService.java (Centralized alarm management logic)
     └── model 
         └── App.java (Main class for execution and testing)
```

## 🛠️ Key Components

### `Alarm.java` (Model)

A simple Plain Old Java Object (POJO) that holds the alarm data:
* `dateTime`: A `java.time.LocalDateTime` object specifying when the alarm should trigger.
* `title`: A `String` to identify the alarm.

### `AlarmService.java` (Service/Manager)

This is the core of the application, implemented as a **Singleton** using a Java `enum`.

* **`alarms`**: An `ArrayBlockingQueue<Alarm>` of size `5`. This thread-safe queue acts as the **bounded buffer** for pending alarms.
* **`spots` (Semaphore)**: Initialized to `MAX_ALARMS` (5). Controls the **producer** (thread adding an alarm) access to the buffer.
    * A thread must `acquire()` a `spot` before adding an alarm, ensuring the buffer capacity is never exceeded.
* **`clientSpots` (Semaphore)**: Initialized to `MAX_ALARMS` (5). Controls the **consumer** (thread waiting for an alarm) access.

### `App.java` (Main Execution)

This class sets up the simulation:
1.  **Producer Threads (Alarm Setters)**: Creates 10 threads, each setting a new alarm staggered by 100ms. The alarms are scheduled 10 to 19 seconds in the future.
2.  **Consumer Threads (Alarm Waiters)**: After a brief delay (1 second), 10 threads are created to call `AlarmService.INSTANCE::startAlarming`.

---

## 🔄 How It Works

### Setting an Alarm (`addAlarm`)

1.  A producer thread calls `AlarmService.INSTANCE.addAlarm(alarm)`.
2.  It attempts to acquire a permit from the **`spots` Semaphore**. If the buffer is full (5 alarms already waiting), the thread **blocks** until a permit is released.
3.  The alarm is added to the `ArrayBlockingQueue`.
4.  No permit is released until the alarm has been triggered/consumed.

### Triggering an Alarm (`startAlarming`)

1.  A consumer thread calls `AlarmService.INSTANCE.startAlarming()`.
2.  It attempts to acquire a permit from the **`clientSpots` Semaphore**.
3.  It then calls `alarms.take()`, which **blocks** if the queue is empty, waiting for a new alarm to be added.
4.  Once an alarm is taken, the thread calculates the duration until the alarm time (`Duration.between(...)`).
5.  The thread calls `Thread.sleep()` to pause for the exact duration until the alarm time.
6.  The alarm rings, and the thread releases permits on **both `spots` and `clientSpots`**, allowing a new producer thread to set an alarm and a new consumer thread to wait for it.

---

## Concurrency Implementation

The ingenious use of two separate Semaphores, though complex, helps control the flow:

| Component | Synchronization Primitive | Role |
| :--- | :--- | :--- |
| **Alarm Buffer** | `ArrayBlockingQueue<Alarm>` | **Thread-safe Bounded Buffer (Size 5)**. Handles the actual storage and blocking when empty (`take()`) or full (implicitly handled by `spots`). |
| **Producer Flow** | `Semaphore spots` | Limits the number of **Producers** that can *add* an alarm to the queue to 5. |
| **Consumer Flow** | `Semaphore clientSpots` | Limits the number of **Consumers** that can be *actively waiting* or sleeping for an alarm to trigger to 5. |

This implementation ensures that at any given moment, only up to 5 alarms can be pending or actively ringing.

---

## 🏃 How to Run

You will need the Java Development Kit (JDK) installed (version **8 or higher** is compatible, but **Java 17+** is recommended for modern development).

### 1. File Setup

Ensure your files are structured with the correct packages:

```
.
└── com.adanali.java
     ├── model 
     │   └── Alarm.java (Data model for an alarm) 
     ├── service 
     │   └── AlarmService.java (Centralized alarm management logic)
     └── model 
         └── App.java (Main class for execution and testing)
```

### 2. Compile and Execute

Open your terminal in the `src` directory (or use an IDE like IntelliJ or Eclipse):

```bash
# 1. Compile the code (from the src directory)
javac com/adanali/java/model/Alarm.java com/adanali/java/service/AlarmService.java com/adanali/java/App.java

# 2. Run the main class
java com.adanali.java.App
Expected Output
The output will show the concurrent nature of the process, with alarms being added and then ringing after their scheduled delay:

Added Alarm 0 for : 2025-12-03T09:46:17.123
Added Alarm 1 for : 2025-12-03T09:46:17.223
....
Added Alarm 4 for : 2025-12-03T09:46:17.523
.... (Pause while the consumer threads wait for the time to pass)
Alarm Ringing : Alarm 0
Alarm Ringing : Alarm 1
```
📜 License :
This project is licensed under the MIT License.