# CampusMarket Android App — Project Milestones

This document outlines the structured milestones for the **CampusMarket** mobile application, detailing the core objectives, implemented features, and technical architecture for each stage of development.

---

## Milestone 1: Project Foundation, Architecture & User Authentication

### **Objective**
Establish the foundational project structure, define the UI/UX design system inspired by professional e-commerce dashboards (Jumia style with green campus branding), and implement secure student authentication.

### **Core Deliverables & Features**
- **Brand & Theme System:** Created a professional e-commerce visual identity featuring brand green (`#2E7D32`), dark header contrast, custom price badges, chip styles, and customized app launcher icons.
- **Local SQLite Database (`DatabaseHelper.java`):** Set up a robust local SQLite relational database (`campus_market.db`) with tables for student users and marketplace items, pre-seeded with sample student listings.
- **Student Authentication (`LoginActivity.java`):**
  - Student Email & Password login and registration screens.
  - Client-side input validation (email format, minimum password length).
  - Session persistence using `SharedPreferences`.
  - Guest browsing mode for instant preview.

---

## Milestone 2: Marketplace Dashboard, Category Filtering & Live Search

### **Objective**
Develop the main e-commerce dashboard allowing students to seamlessly browse, search, and filter campus products and services.

### **Core Deliverables & Features**
- **Jumia-Style Dashboard (`MainActivity.java`):**
  - Dark header bar with campus market branding, user status display, and logout controls.
  - Embedded search bar for real-time query filtering across product titles and categories.
- **Horizontal Category Filter Chips:**
  - Dynamic filter chips for *All Items, Textbooks, Electronics, Fashion, Hostel, and Services*.
- **2-Column Product Grid (`ItemAdapter.java` & `item_row_card.xml`):**
  - Optimized `GridLayoutManager` displaying items in clean product cards.
  - Cards feature condition badges (*NEW, LIKE NEW, USED*), category tags, prices in UGX currency, seller contact details, and product image rendering via Glide.

---

## Milestone 3: Item Publishing, Direct WhatsApp/Call Communication & Inventory Management

### **Objective**
Empower all campus students to list their items for sale with photo uploads, connect directly with buyers via WhatsApp or phone calls, and manage their active listings.

### **Core Deliverables & Features**
- **Item Posting Form (`PostItemActivity.java`):**
  - Complete form allowing students to input item title, price in UGX, category, condition, description, and contact info.
  - **Image Picker Integration:** Select product photos directly from the device gallery with live preview.
  - **Success Confirmation Dialog:** Confirmation alert modal confirming that the listing is live on Campus Market.
- **Product Details & Direct Communication (`ItemDetailsActivity.java`):**
  - Full-screen product view displaying images, pricing, and seller details.
  - **Direct WhatsApp Chat Integration:** One-tap **`WHATSAPP CHAT`** button opening a direct WhatsApp chat with the seller (`api.whatsapp.com`) with a pre-filled purchase inquiry message.
  - **Direct Call Action:** One-tap **`CALL SELLER`** button (`Intent.ACTION_DIAL`) launching the phone dialer.
- **Inventory Management (Mark as Sold):**
  - Dedicated **`MARK AS SOLD / REMOVE`** button allowing users to remove sold items from the active market view in real time.

---

## Technical Stack Summary
- **Language:** Java
- **UI Components:** Material Design 3, CoordinatorLayout, RecyclerView, ConstraintLayout, Glide (Image Loading)
- **Database:** Android SQLite (`SQLiteOpenHelper`)
- **Communication:** Android Intents (`Intent.ACTION_VIEW` for WhatsApp, `Intent.ACTION_DIAL` for phone calls)
