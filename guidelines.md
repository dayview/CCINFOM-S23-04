
---

# 📑 Project Guidelines

## 1. Folder Structure

```
src/
    main/
        java/
            businesspermitsystem/
                db/             # Database connection + DAO classes = Data Access Layer
                models/         # Business, Owner, Inspector, Permit, etc.
                services/       # Business logic (registration, renewal, reporting)
                controllers/    # JavaFX controllers (connect FXML + logic)
                views/          # FXML files
                utils/          # Helpers (formatting, validation, constants)
        resources/          # CSS, icons, sample data
    test/                   # Testing
```

---

## 2. Naming Conventions

* **Classes**: `PascalCase` → `BusinessDAO`, `OwnerService`
* **Variables/Methods**: `camelCase` → `businessName`, `getOwners()`
* **Constants**: `UPPER_CASE` → `DB_URL`
* **FXML IDs**: descriptive → `BusinessTable`, `OwnerFormSubmitButton`
* **CSS**: `kebab-case` → `main-style`

---

## 3. Branching & Git Workflow

* **Main branch** → always stable, working code
* **Feature branches** →

  * `feature/business-crud`
  * `feature/owner-transactions`
  * `report/payments`
* **Commit messages** (use short prefixes):

  * `feat: add business CRUD`
  * `fix: owner form validation`
  * `report: generate permits issued chart`

---

## 4. Code Organization
* **MVC Pattern** → model, view, controller pattern architecture 

* **Models** → only represent data (fields + getters/setters)
* **DAO classes** → handle SQL queries for one entity
* **Services** → contain business rules (e.g., fee calculation)
* **Controllers** → manage UI logic, call Service/DAO
* **Views** → UI/Screens

---

## 5. Documentation

* Each variable → short comment describing purpose
* Each class → short comment describing purpose
* Each method → detailed doc comment
* All Documentations will be done in Javadoc format

Example:

```java
/**
 * Calculates renewal fee with surcharge
 * 
 * @param permit *parameter description*
 * @return *returned object description*
*/
public double calculateRenewalFee(Permit permit) { ... }
```

---

## 6. Task Assignment

* Each group member owns their **core record + transaction + report** (based on proposal)
* Members must update the shared task board in GitHub Projects
* Diagrams:
    * ERD
    * Use Case
    * Class Diagram

---

## 7. Testing & Merging

* Test your feature before merging to `main`
---

