# ISE5786_1172_7191

# MP2 - Performance Acceleration & Multi-Threading

## 1. Project Goal
Implementation of a **Bounding Volume Hierarchy (BVH)** for ray-tracing optimization and a robust multi-threading rendering engine.

---

## 2. Implementation

- **Acceleration:** Implemented BVH (Bounding Volume Hierarchy) with both Manual and Auto-construction (recursive subdivision).
- **Multi-Threading:** Support for both Parallel Streams and Raw Threads (via custom `PixelManager`).
- **Integration:** All performance tests include active Depth of Field (DoF) to ensure robust results under heavy rendering load.

---

## 3. Results (Performance Comparison)

| Scene Mode       | No MT (s) | With MT (s) | Speedup  |
|------------------|-----------|-------------|----------|
| No Accel, Flat   | 239       | 144         | 1.66     |
| No Accel, Manual | 427       | 159         | 2.69     |
| No Accel, Auto   | 639       | 123         | 5.20     |
| CBR, Flat        | 78        | 27.8        | 2.80     |
| CBR, Manual      | 16.7      | 21.5        | 0.78 \*  |
| CBR, Auto        | 5.3       | 6.7         | 0.79 \*  |

> \* **Note:** Speedup < 1.0 in optimized modes is due to thread synchronization overhead.  
> **Overhead for Auto-Hierarchy construction:** 45ms.

---

## 4. How to Run

- **Execution:** Run tests in `PerformanceTests.java`.
- **Configuration:** Toggle `THREADS_COUNT` to `0` (Off) or `-1` (Parallel Streams).
