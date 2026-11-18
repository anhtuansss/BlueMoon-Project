# BlueMoon – Hướng Dẫn Phát Triển Dự Án (Project Guide)

> **Tài liệu chuẩn hóa kiến trúc, quy tắc phát triển, phân công trách nhiệm và quy ước phối hợp.**  
> Áp dụng cho: Cả 4 thành viên nhóm phát triển, AI Coding Agent, Reviewer và Người tích hợp hệ thống.  
> **Nguyên tắc cốt lõi:** Không tự ý sửa scope, schema, contract hoặc code thuộc module của người khác khi chưa có sự thống nhất.

---

## Mục Lục

1. [A. Project Overview (Tổng quan dự án)](#a-project-overview)
2. [B. Technology Stack & Architecture (Công nghệ & Kiến trúc)](#b-technology-stack--architecture)
3. [C. Module Ownership & Phân công trách nhiệm](#c-module-ownership)
4. [D. Database Rules (Quy tắc Cơ sở dữ liệu)](#d-database-rules)
5. [E. API Contract & Integration Rules (Hợp đồng API & Tích hợp)](#e-api-contract--integration-rules)
6. [F. Business Rules (Quy tắc Nghiệp vụ cốt lõi)](#f-business-rules)
7. [G. Coding Conventions (Quy ước Lập trình)](#g-coding-conventions)
8. [H. Git Workflow & Quản lý Mã nguồn](#h-git-workflow)
9. [I. AI Coding Rules (Quy tắc bắt buộc cho AI Agent)](#i-ai-coding-rules)
10. [J. Definition of Done (Định nghĩa Hoàn thành - DoD)](#j-definition-of-done)
11. [K. Open Issues / Decisions Needed (Vấn đề cần thống nhất)](#k-open-issues--decisions-needed)

---

<a name="a-project-overview"></a>
## A. Project Overview (Tổng quan dự án)

### 1. Mục tiêu
**BlueMoon** là Hệ thống Quản lý Chung cư (Apartment Management System – AMS), phiên bản 1.0, tập trung số hóa quy trình quản lý hành chính, cư dân, căn hộ, công tác thu phí dịch vụ/quản lý và tiếp nhận đóng góp tự nguyện. Hệ thống hướng tới việc giảm thiểu thao tác thủ công, đảm bảo tính minh bạch tài chính và hỗ trợ đối soát thanh toán tự động qua mã QR ngân hàng (SePay).

### 2. Đối tượng sử dụng & Các vai trò (RBAC)

Hệ thống phân quyền dựa trên 3 vai trò chính (`role`):

| Vai trò | Ký hiệu | Mô tả quyền hạn |
|---|---|---|
| **Ban quản trị** | `BQT` | Quyền cao nhất trong hệ thống. Quản lý tài khoản NV/BQT; quản lý đơn giá phí; tạo và **hủy** khoản thu; **hủy/đảo** bản ghi nộp tiền; xóa thông báo; xử lý các ngoại lệ đối soát giao dịch ngân hàng. |
| **Nhân viên quản lý** | `NV` | Thực hiện nghiệp vụ thường nhật: quản lý căn hộ, hồ sơ cư dân, biến động cư trú; tạo khoản thu; ghi nhận nộp tiền mặt/chuyển khoản; tiếp nhận và phản hồi khiếu nại. **Không có quyền hủy khoản thu, không được hủy bản ghi nộp tiền, không được xóa thông báo, không quản lý tài khoản NV/BQT.** |
| **Cư dân** | `RESIDENT` | Người sinh sống tại chung cư, sử dụng tài khoản do BQT/NV cấp. Tra cứu thông tin căn hộ của mình, xem nghĩa vụ nộp phí, tạo mã QR chuyển khoản, thực hiện đóng góp tự nguyện, gửi khiếu nại và xem thông báo nội bộ. **Chỉ được truy cập dữ liệu thuộc căn hộ của mình; không tự tạo/hủy thanh toán, không đăng ký tài khoản tự do.** |

### 3. Phạm vi chức năng (In-Scope: F01 – F09)

- **F01 – Quản lý tài khoản và phân quyền:** Đăng nhập, đăng xuất, đổi mật khẩu, quản lý hồ sơ, cấp phát tài khoản BQT/NV/Cư dân, đặt lại mật khẩu qua OTP email.
- **F02 – Quản lý căn hộ:** Quản lý danh mục căn hộ (diện tích, mã căn hộ, trạng thái sử dụng). Ngừng sử dụng căn hộ (không xóa vật lý).
- **F03 – Quản lý cư dân & hộ gia đình:** Hồ sơ nhân khẩu, nhóm hộ gia đình, liên kết hộ gia đình với căn hộ theo thời gian.
- **F04 – Quản lý biến động cư trú:** Ghi nhận chuyển đến, chuyển đi, tạm trú, tạm vắng và lưu vết toàn bộ lịch sử cư trú.
- **F05 – Quản lý khoản thu và đóng góp:** Quản lý loại phí, lập đợt thu phí bắt buộc (tính theo diện tích và đơn giá snapshot), quản lý các đợt vận động đóng góp tự nguyện.
- **F06 – Quản lý thu tiền & thanh toán QR:** Ghi nhận nộp tiền mặt/chuyển khoản thường; sinh mã QR động kèm mã tham chiếu (`payment_reference`); tiếp nhận webhook SePay đối soát tự động; xử lý giao dịch ngoại lệ; đảo bản ghi thanh toán (BQT).
- **F07 – Tra cứu và thống kê:** Báo cáo dashboard, thống kê tổng phải thu, đã thu, còn phải thu đối với phí bắt buộc; thống kê riêng tổng tiền đóng góp tự nguyện; tra cứu công nợ theo căn hộ.
- **F08 – Quản lý thông báo:** Soạn và gửi thông báo nội bộ (toàn bộ hoặc theo căn hộ); cấu hình lịch tự động nhắc đóng phí; xóa thông báo (BQT).
- **F09 – Quản lý khiếu nại:** Cư dân gửi khiếu nại; BQT/NV cập nhật tiến độ (Mới, Đang xử lý, Đã giải quyết) và nhập nội dung phản hồi.

### 4. Các chức năng NGOÀI phạm vi phiên bản hiện tại (Out-of-Scope)

Các tính năng sau **tuyệt đối không triển khai** trong V1:
- ❌ **Tự đăng ký tài khoản cư dân:** Hệ thống không có màn hình đăng ký public; tài khoản do BQT/NV khởi tạo.
- ❌ **Thanh toán một phần (Partial payment):** Nghĩa vụ phí bắt buộc phải được thanh toán đủ 100% trong 1 lần.
- ❌ **Cổng thanh toán trừ tiền trực tiếp:** Hệ thống không can thiệp trừ tiền tài khoản ngân hàng; cư dân tự thao tác trên app ngân hàng qua QR, backend nhận webhook biến động số dư từ SePay để đối soát.
- ❌ **Tự động sinh đợt thu phí bằng Cron ngầm:** Phí bắt buộc và đợt đóng góp do BQT/NV chủ động lập đợt thu (campaign) trên giao diện.
- ❌ **Xuất báo cáo ra file Excel / PDF:** Toàn bộ báo cáo, biểu đồ chỉ hiển thị trực quan trên ứng dụng web.
- ❌ **Gửi thông báo qua SMS / Push Notification / Zalo:** Thông báo chỉ hiển thị in-app trên giao diện BlueMoon.
- ❌ **Chat thời gian thực / Trao đổi nhiều lượt / Đính kèm file khiếu nại:** Khiếu nại chỉ gồm tiêu đề, nội dung và 1 phản hồi chính thức từ BQT/NV.

---

<a name="b-technology-stack--architecture"></a>
## B. Technology Stack & Architecture (Công nghệ & Kiến trúc)

### 1. Chi tiết Tech Stack

| Tầng | Công nghệ / Thư viện chính | Ghi chú |
|---|---|---|
| **Frontend** | React 19, React Router DOM v7, Vite 8, Tailwind CSS v4 | Khởi chạy SPA, styling tiện ích qua Tailwind, linting bằng Oxlint. |
| **Backend** | Java Spring Boot 4.x (JDK 21 LTS), Spring Data JPA, Hibernate ORM, Bean Validation, Lombok | Kiến trúc Modular Monolith phân tầng Controller – Service – Repository. |
| **Database** | MySQL 8.x (MySQL 8.0.16+) | Charset: `utf8mb4`, Collate: `utf8mb4_0900_ai_ci`. Driver: `mysql-connector-j`. |
| **Integration**| SePay Webhook IPN | Nhận biến động số dư ngân hàng phục vụ đối soát thanh toán QR. |
| **Giao tiếp FE-BE**| RESTful API qua HTTP/JSON | Chuẩn endpoint prefix `/api/v1`, định dạng JSON camelCase. |

### 2. Kiến trúc hệ thống (Modular Monolith)

- **Frontend & Backend phân tách hoàn toàn:** Frontend giao tiếp với Backend duy nhất qua REST API (`/api/v1`).
- **Giao tiếp nội bộ Backend:** Các module giao tiếp với nhau thông qua **Java Service Interface trong cùng bộ nhớ (In-Memory Service Contract)**. Tuyệt đối **không** tạo cuộc gọi HTTP ngược lại chính backend (`localhost:8080`).
- **Ranh giới dữ liệu (Data Boundaries):** Module A không được query hoặc sửa trực tiếp Entity/Table do Module B sở hữu; mọi truy vấn chéo phải qua Service công khai do Module B cung cấp.

```
[ Frontend (React SPA) ]
           │ (HTTP / JSON /api/v1)
           ▼
┌─────────────────────────────────────────────────────────────┐
│ Backend (Spring Boot Modular Monolith)                      │
│ ┌──────────────┐ ┌──────────────┐ ┌───────────────────────┐ │
│ │  F01 & F08   │ │ F02, F03, F04│ │     F05 & F07         │ │
│ │  (Nguyên)    │ │   (Thành)    │ │      (Long)           │ │
│ └──────────────┘ └──────┬───────┘ └───────────▲───────────┘ │
│                         │ In-memory Service   │ In-memory   │
│                         ▼ Contract            │ Service     │
│                  ┌──────────────┐             │             │
│                  │  F06 (Tuấn)  ├─────────────┘             │
│                  └──────────────┘                           │
└──────────────────────────┬──────────────────────────────────┘
                           │ (JDBC / Hibernate)
                           ▼
               [ MySQL 8.x (BlueMoonV1) ]
```

### 3. Cấu trúc thư mục mã nguồn

```text
BlueMoon-Project/
├── .gitignore                          # Cấu hình Git ignore chung
├── README.md                           # Tài liệu giới thiệu & khởi động nhanh
├── PROJECT_GUIDE.md                    # Tài liệu này (Single Source of Truth)
├── BlueMoonV1_schema.sql               # Script DDL MySQL chuẩn đã chốt
├── docs/
│   ├── SRS.md                         # Đặc tả yêu cầu phần mềm chi tiết v1.0
│   └── API_CONTRACT.md                 # Hợp đồng API chi tiết giữa các module
│
├── frontend/                           # Client ReactJS
│   ├── public/                         # Static assets (favicon, icons)
│   ├── src/
│   │   ├── components/                 # UI components chung & UI primitives
│   │   │   └── ui/                     # Card, Button, Modal, Drawer, Table,...
│   │   ├── layouts/                    # Layout khung (AppShell, Navbar, Sidebar)
│   │   ├── pages/                      # Màn hình theo từng phân hệ chức năng
│   │   │   ├── auth/                   # Màn hình đăng nhập (F01)
│   │   │   ├── apartments/             # Quản lý căn hộ (F02)
│   │   │   ├── residents/              # Quản lý cư dân & hộ (F03)
│   │   │   ├── residency/              # Quản lý biến động cư trú (F04)
│   │   │   ├── fees/                   # Quản lý khoản thu & đóng góp (F05)
│   │   │   ├── payments/               # Thu tiền & thanh toán QR (F06)
│   │   │   ├── reports/                # Thống kê & báo cáo (F07)
│   │   │   ├── notices/                # Quản lý thông báo (F08)
│   │   │   └── issues/                 # Quản lý khiếu nại (F09)
│   │   ├── services/                   # HTTP client & API endpoints
│   │   ├── hooks/                      # Custom React hooks (useHealthCheck,...)
│   │   ├── data/                       # Mock data cho UI demo độc lập
│   │   ├── App.jsx                     # Cấu hình routes ứng dụng
│   │   ├── main.jsx                    # Entry point React
│   │   └── index.css                   # Global styling & Tailwind directives
│   ├── .env.example                    # Template biến môi trường frontend
│   └── package.json                    # Khai báo dependencies
│
└── backend/                            # Server Spring Boot
    ├── mvnw / mvnw.cmd                 # Maven wrapper
    ├── pom.xml                         # Cấu hình dependencies Spring Boot & Maven
    ├── src/main/resources/
    │   └── application.properties      # Cấu hình kết nối DB & Spring
    └── src/main/java/com/bluemoon/
        ├── BluemoonBackendApplication.java
        ├── config/                     # WebConfig, CorsConfig, SecurityConfig
        ├── controller/                 # REST Controllers tiếp nhận request HTTP
        ├── service/                    # Business Logic Interfaces & Impls
        ├── repository/                 # Spring Data JPA Repositories
        ├── entity/                     # JPA Entities ánh xạ database
        ├── dto/                        # Request / Response DTOs
        └── exception/                  # GlobalExceptionHandler & Custom Exceptions
```

### 4. Hướng dẫn Khởi chạy & Biến môi trường

#### Khởi chạy Database (MySQL)
1. Cài đặt MySQL 8.0.16+.
2. Tạo CSDL và nạp bảng bằng file script chuẩn:
   ```bash
   mysql -u root -p < database/schema.sql
   ```

#### Khởi chạy Backend (Spring Boot)
1. Cấu hình biến môi trường (hoặc để mặc định theo [backend/.env.example](BlueMoon-Project/backend/.env.example)):
   - `SERVER_PORT`: Cổng chạy backend (mặc định: `8080`).
   - `DB_HOST`: Địa chỉ máy chủ MySQL (mặc định: `localhost`).
   - `DB_PORT`: Cổng MySQL (mặc định: `3306`).
   - `DB_NAME`: Tên cơ sở dữ liệu (mặc định: `bluemoon_db` hoặc `BlueMoonV1`).
   - `DB_USERNAME`: Tài khoản MySQL (mặc định: `root`).
   - `DB_PASSWORD`: Mật khẩu MySQL.
   - `SEPAY_API_KEY`: Khóa xác thực webhook từ SePay.
2. Chạy lệnh:
   ```powershell
   cd backend
   .\mvnw.cmd spring-boot:run
   ```

#### Khởi chạy Frontend (React)
1. Cấu hình biến môi trường trong file `frontend/.env` (tham khảo `frontend/.env.example`):
   - `VITE_API_BASE_URL=http://localhost:8080/api/v1`
2. Chạy lệnh:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   Ứng dụng mở tại: `http://localhost:5173`.

---

<a name="c-module-ownership"></a>
## C. Module Ownership & Phân công trách nhiệm

| Thành viên | Mã nhóm | Tên Module | Trách nhiệm chi tiết |
|---|---|---|---|
| **Lê Quang Nguyên** | **F01**<br>**F08** | **Quản lý tài khoản & phân quyền**<br>**Quản lý thông báo** | - Xác thực JWT, quản lý phiên đăng nhập, cấp tài khoản, đổi mật khẩu, OTP.<br>- Tạo và gửi thông báo, cấu hình lịch tự động nhắc nộp phí, xóa thông báo (BQT). |
| **Tô Văn Thành** | **F02**<br>**F03**<br>**F04** | **Quản lý căn hộ**<br>**Quản lý cư dân & hộ gia đình**<br>**Quản lý biến động cư trú** | - Quản lý thông tin & diện tích căn hộ, trạng thái hoạt động.<br>- Quản lý nhân khẩu (Person), hộ gia đình (Household), liên kết cư trú theo thời gian.<br>- Ghi nhận biến động (chuyển đến, chuyển đi, tạm trú, tạm vắng).<br>- Cung cấp `ApartmentQueryService` cho F05, F06, F07. |
| **Nguyễn Bá Long** | **F05**<br>**F07** | **Quản lý khoản thu & đóng góp**<br>**Tra cứu và thống kê** | - Danh mục loại phí (`FeeType`), đơn giá mặc định.<br>- Tạo chiến dịch thu phí (`CollectionCampaign`), tính toán và snapshot nghĩa vụ (`FeeObligation`).<br>- Tạo chiến dịch đóng góp tự nguyện (`DonationCampaign`).<br>- Báo cáo tài chính, thống kê tổng thu/phải thu/còn nợ, theo dõi đóng góp.<br>- Cung cấp `FeeObligationQueryService` cho F06. |
| **Bùi Anh Tuấn** | **F06**<br>**F09** | **Thu tiền & thanh toán QR (SePay)**<br>**Quản lý khiếu nại** | - Ghi nhận nộp tiền mặt/chuyển khoản thủ công.<br>- Tạo yêu cầu thanh toán QR (`PaymentRequest`) kèm mã tham chiếu.<br>- Xử lý Webhook SePay IPN đối soát tự động, chống xử lý trùng lặp.<br>- Xử lý giao dịch ngoại lệ, nghiệp vụ đảo thanh toán (BQT).<br>- Tiếp nhận và cập nhật tiến độ xử lý khiếu nại của cư dân. |

### Nguyên tắc phối hợp bắt buộc:
1. **Trách nhiệm toàn diện:** Mỗi thành viên sở hữu trọn vẹn cả Frontend, Backend, DTO, Entity, Repository, Service và Unit/Integration Test cho module của mình.
2. **Không sửa chéo code của nhau:** Nghiêm cấm tự ý sửa đổi code, nghiệp vụ hoặc cấu trúc file thuộc module của thành viên khác.
3. **Giao tiếp qua Service Interface:** Khi module A cần dữ liệu từ module B, phải thông qua Interface do module B cung cấp (xem mục E).
4. **Thay đổi ảnh hưởng phải thảo luận trước:** Mọi thay đổi liên quan đến interface chung, DTO dùng chung, hoặc bảng dữ liệu liên kết phải được trao đổi và thống nhất trước khi commit.

---

<a name="d-database-rules"></a>
## D. Database Rules (Quy tắc Cơ sở dữ liệu)

Toàn bộ hệ thống tuân thủ nghiêm ngặt theo CSDL [BlueMoonV1_schema.sql](BlueMoon-Project/database/schema.sql).

### 1. Phân nhóm dữ liệu & Bảng CSDL

| Phân nhóm dữ liệu | Danh sách bảng (MySQL Table) | Module sở hữu |
|---|---|---|
| **Tài khoản & Phân quyền** | `Person`, `Account` | **F01** |
| **Căn hộ & Cư trú** | `Apartment`, `Household`, `HouseholdPerson`, `HouseholdApartment`, `ResidenceChange`, `ResidentChangeRequest` | **F02, F03, F04** |
| **Khoản thu & Đóng góp** | `FeeType`, `CollectionCampaign`, `FeeObligation`, `DonationCampaign`, `FeeObligationHistory` | **F05** |
| **Thanh toán & Giao dịch** | `PaymentRequest`, `Payment`, `BankTransaction` | **F06** |
| **Thông báo** | `NotificationSetting`, `Notification` | **F08** |
| **Khiếu nại** | `Complaint` | **F09** |

### 2. Các quyết định kiến trúc CSDL sống còn

1. **Nghĩa vụ phí bắt buộc phát sinh theo CĂN HỘ (`Apartment`), KHÔNG phát sinh theo `Household`:**
   - Bảng `FeeObligation` chứa khóa ngoại trực tiếp `apartment_id`.
   - `Household` chỉ đóng vai trò phân nhóm nhân khẩu sinh sống tại một thời điểm, không phải đối tượng chịu trách nhiệm pháp lý thanh toán trên schema.
2. **Snapshot bắt buộc tại thời điểm phát sinh:**
   - Bảng `FeeObligation` lưu cứng: `area_snapshot`, `unit_price_snapshot`, và `amount_due`.
   - Không được tính toán lại `amount_due` bằng cách join động với `Apartment.area` hay `FeeType.default_unit_price` trong tương lai vì diện tích hoặc đơn giá có thể thay đổi sau này.
3. **Thanh toán phí bắt buộc là 1 lần và đủ 100%:**
   - Cột sinh tự động có điều kiện: `confirmed_obligation_id = IF(payment_status = 'CONFIRMED' AND obligation_id IS NOT NULL, obligation_id, NULL)`.
   - Ràng buộc duy nhất `UNIQUE (confirmed_obligation_id)` đảm bảo một nghĩa vụ phí chỉ có duy nhất **MỘT** bản ghi thanh toán thành công.
   - `Payment.amount` phải bằng đúng `FeeObligation.amount_due`.
4. **Đóng góp tự nguyện KHÔNG sinh nghĩa vụ / công nợ:**
   - Chiến dịch đóng góp (`DonationCampaign`) không tạo dòng nào trong `FeeObligation`.
   - Khi cư dân đóng góp, bản ghi `Payment` liên kết trực tiếp với `donation_campaign_id` và `apartment_id`. Căn hộ không đóng góp hoàn toàn không có công nợ.
5. **Bảo toàn lịch sử thanh toán & Hủy bỏ (Không xóa vật lý):**
   - Đảo/hủy thanh toán không dùng lệnh `DELETE`. Chuyển `payment_status = 'REVERSED'` và điền các trường kiểm toán `reversed_by`, `reversed_at`, `reversal_reason`.
   - Hủy khoản thu: Chuyển `CollectionCampaign.status = 'CANCELLED'`, các nghĩa vụ liên quan chuyển `payment_status = 'CANCELLED'`.
6. **Bất biến Schema đã chốt:**
   - Tuyệt đối không tự ý thêm cột, sửa kiểu dữ liệu, đổi tên bảng/cột nếu chưa có sự đồng thuận của cả 4 thành viên.

### 3. Quy ước đặt tên trong CSDL và Mã nguồn

| Thành phần | Quy ước Database (SQL) | Quy ước Backend (Java) | Ví dụ |
|---|---|---|---|
| **Bảng / Entity** | `PascalCase` | `PascalCase` | `FeeObligation` |
| **Cột / Field** | `snake_case` | `camelCase` | `amount_due` ➔ `amountDue` |
| **Khóa chính** | `<table_name>_id` | `id` hoặc `<tableName>Id` | `payment_id` ➔ `paymentId` |
| **Khóa ngoại** | `<referenced_table>_id` | Đối tượng Entity hoặc Long ID | `apartment_id` ➔ `apartment` |
| **Repository** | — | `[Entity]Repository` | `FeeObligationRepository` |
| **Service** | — | `[Feature]Service` & `Impl` | `FeeCampaignService` |
| **DTO Request** | — | `[Action][Entity]Request` | `CreateFeeCampaignRequest` |
| **DTO Response** | — | `[Entity]Response` | `FeeObligationResponse` |

---

<a name="e-api-contract--integration-rules"></a>
## E. API Contract & Integration Rules (Hợp đồng API & Tích hợp)

### 1. Quy ước chung RESTful API
- **Base URI:** `/api/v1`
- **Headers:**
  - Client gửi: `Content-Type: application/json; charset=utf-8`
  - Client gửi: `Authorization: Bearer <JWT_TOKEN>` (trừ endpoint Login và SePay Webhook)
- **Định dạng dữ liệu:**
  - JSON keys: `camelCase`.
  - Tiền tệ (VND): Số nguyên (`Long` / `BigInteger`), không dùng float/double.
  - Diện tích, đơn giá: `BigDecimal`.
  - Ngày tháng: `YYYY-MM-DD` (VD: `2026-11-18`).
  - Thời gian: ISO-8601 UTC (VD: `2026-11-18T07:00:00Z`).
  - ID trong JSON: Trả về kiểu String (tránh lỗi làm tròn số nguyên lớn ở Javascript).

### 2. Định dạng Response chuẩn

**Thành công (Đơn lẻ hoặc Thao tác):**
```json
{
  "data": {
    "id": "105",
    "status": "SUCCESS"
  }
}
```

**Thành công (Danh sách phân trang):**
```json
{
  "data": {
    "items": [ /* Danh sách đối tượng */ ],
    "page": 0,
    "size": 20,
    "totalElements": 92,
    "totalPages": 5
  }
}
```

**Lỗi chuẩn (Error Response):**
```json
{
  "error": {
    "code": "OBLIGATION_ALREADY_PAID",
    "message": "Nghĩa vụ phí này đã được thanh toán đầy đủ trước đó.",
    "details": []
  }
}
```

### 3. Mã trạng thái HTTP (Status Codes)
- `200 OK`: Tra cứu hoặc cập nhật thành công.
- `201 Created`: Tạo mới tài nguyên thành công.
- `400 Bad Request`: Lỗi định dạng JSON, validation dữ liệu đầu vào thất bại.
- `401 Unauthorized`: Chưa đăng nhập hoặc token JWT không hợp lệ/hết hạn.
- `403 Forbidden`: Người dùng không có quyền (Role không đủ hoặc Cư dân truy cập ngoài căn hộ của mình).
- `404 Not Found`: Không tìm thấy ID tài nguyên.
- `409 Conflict`: Xung đột trạng thái, dữ liệu trùng lặp (VD: đợt thu phí đã tồn tại, nghĩa vụ đã thanh toán).
- `422 Unprocessable Entity`: Dữ liệu đúng định dạng nhưng vi phạm quy tắc nghiệp vụ (VD: số tiền không khớp `amountDue`).
- `500 Internal Server Error`: Lỗi hệ thống server chưa xử lý.

### 4. Bảng mã lỗi nghiệp vụ chuẩn (Business Error Codes)

| Mã lỗi (`code`) | HTTP Status | Ý nghĩa nghiệp vụ |
|---|:---:|---|
| `DUPLICATE_FEE_CAMPAIGN` | 409 | Loại phí này đã được lập đợt thu trong kỳ chỉ định. |
| `APARTMENT_NOT_ACTIVE` | 422 | Căn hộ không ở trạng thái hoạt động (`is_active = false`). |
| `OBLIGATION_ALREADY_PAID` | 409 | Nghĩa vụ phí đã được thanh toán trước đó. |
| `OBLIGATION_CANCELLED` | 409 | Nghĩa vụ phí đã bị hủy, không thể thu tiền. |
| `PAYMENT_AMOUNT_MISMATCH` | 422 | Số tiền nộp không bằng chính xác số tiền phải thu (`amountDue`). |
| `PAYMENT_REQUEST_EXPIRED` | 409 | Yêu cầu thanh toán QR đã hết hạn hiệu lực. |
| `PAYMENT_REFERENCE_NOT_MATCHED` | 422 | Không tìm thấy mã tham chiếu hợp lệ tương ứng với giao dịch. |
| `DUPLICATE_BANK_TRANSACTION` | 409 / 200 | Mã giao dịch ngân hàng ngoài (`external_transaction_id`) đã được xử lý. |
| `RESIDENT_APARTMENT_SCOPE_FORBIDDEN` | 403 | Cư dân cố tình xem hoặc thanh toán cho căn hộ không thuộc quyền sở hữu của mình. |

### 5. Service Interfaces tích hợp xuyên Module (In-Memory Contracts)

#### Module F03 cung cấp cho F05, F06, F07:
```java
public interface ApartmentQueryService {
    // Trả về danh sách tất cả căn hộ đang hoạt động (kể cả căn hộ trống) để F05 lập nghĩa vụ phí
    List<BillableApartmentSnapshot> findAllActiveApartments();

    // Lấy thông tin snapshot của 1 căn hộ
    BillableApartmentSnapshot getActiveApartment(Long apartmentId);

    // Xác định căn hộ hiện tại của tài khoản cư dân đang đăng nhập
    ResidentApartmentScope getCurrentApartmentForAccount(Long accountId);
}
```

#### Module F05 cung cấp cho F06, F07:
```java
public interface FeeObligationQueryService {
    // Lấy thông tin nghĩa vụ phí để F06 kiểm tra trước khi thu tiền
    PayableObligationDto getPayableObligation(Long obligationId);

    // F06 gọi khi xác nhận thanh toán thành công để chuyển trạng thái sang PAID
    void markObligationAsPaid(Long obligationId, Long paymentId);

    // F06 gọi khi BQT thực hiện đảo thanh toán để phục hồi trạng thái nghĩa vụ
    void revertObligationPayment(Long obligationId);
}
```

#### Module F01 cung cấp thông tin người dùng:
- Các module khác lấy thông tin người dùng qua `SecurityContext` / `CurrentUser` (`accountId`, `personId`, `role`). Không tự giải mã token JWT thô.

---

<a name="f-business-rules"></a>
## F. Business Rules (Quy tắc Nghiệp vụ cốt lõi)

1. **Công thức tính phí dịch vụ & quản lý:**
   $$\text{amountDue} = \text{ROUND}(\text{areaSnapshot} \times \text{appliedUnitPrice}, 0) \quad (\text{VND})$$
   - Sử dụng `BigDecimal` với chế độ làm tròn `RoundingMode.HALF_UP`.
   - Đơn giá mặc định: Phí dịch vụ: `2.500 VND/m²/tháng`; Phí quản lý: `7.000 VND/m²/tháng`. BQT có thể thay đổi đơn giá khi khởi tạo campaign.
2. **Quy tắc tạo khoản thu bắt buộc (`CollectionCampaign` & `FeeObligation`):**
   - Chỉ được lập khoản thu cho căn hộ đang hoạt động (`is_active = true`), kể cả căn hộ chưa có người ở. Căn hộ `is_active = false` bị loại trừ.
   - Không hỗ trợ tự động tạo định kỳ: BQT/NV phải chủ động kích hoạt tạo campaign.
   - Ngăn chặn trùng lặp: Ràng buộc `UNIQUE(fee_type_id, fee_period)` ngăn chặn việc tạo 2 campaign cùng loại phí trong 1 kỳ (kể cả campaign cũ bị hủy).
   - Backend tự query diện tích và tính toán `amount_due` trong một Transaction; không dùng số tiền từ frontend gửi lên để lưu vào DB.
3. **Quy tắc Đóng góp tự nguyện (`DonationCampaign`):**
   - Không sinh `FeeObligation`.
   - Người đóng góp tự nhập số tiền tùy tâm ($> 0$).
   - Căn hộ không đóng góp không bị ghi nhận công nợ và không bị tính là nợ phí.
4. **Quy tắc Quét mã QR & Đối soát SePay:**
   - Việc sinh mã QR chỉ tạo một bản ghi `PaymentRequest` (trạng thái `PENDING`) để hướng dẫn chuyển khoản. **Tạo QR không đồng nghĩa với đã thanh toán.**
   - Một nghĩa vụ phí chỉ có tối đa **1** yêu cầu QR ở trạng thái `PENDING` còn hiệu lực.
   - Webhook SePay IPN khi bắn về backend:
     - Phải xác thực chữ ký/API key.
     - Khóa chống trùng (Idempotency): Sử dụng `external_transaction_id` duy nhất trong bảng `BankTransaction`. Giao dịch đã nhận không bao giờ được tạo lại Payment lần thứ hai.
     - Chỉ khi giao dịch: `direction = 'IN'`, đúng số tài khoản thụ hưởng, khớp chính xác `payment_reference`, và `amount == amount_due` thì hệ thống mới tạo `Payment` (`CONFIRMED`, `QR`, `AUTOMATIC`) và chuyển `FeeObligation` sang `PAID`.
     - **Giao dịch không khớp:** Nếu thiếu dữ liệu, sai số tiền, sai nội dung tham chiếu hoặc không xác định được nghĩa vụ -> Lưu `BankTransaction` ở trạng thái `UNMATCHED` hoặc `INVALID` để BQT/NV xử lý thủ công. **Tuyệt đối không tự động xác nhận thanh toán cho giao dịch sai lệch!**
5. **Quy tắc Phân quyền Hủy & Đảo thanh toán:**
   - Chỉ vai trò `BQT` được phép hủy chiến dịch thu phí (`CollectionCampaign`). Không được hủy chiến dịch nếu đã có khoản nộp tiền `CONFIRMED` còn hiệu lực.
   - Chỉ vai trò `BQT` được phép đảo thanh toán (`POST /payments/{id}/reverse`). Khi đảo thanh toán, bản ghi Payment cũ chuyển thành `REVERSED`, nghĩa vụ phí được chuyển lại thành `UNPAID` (hoặc `OVERDUE` nếu đã quá hạn).
6. **Quy tắc Thống kê (F07):**
   - $\text{Tổng phải thu} = \sum \text{amount\_due}$ của các nghĩa vụ thuộc các campaign còn hiệu lực (loại bỏ campaign và obligation bị `CANCELLED`).
   - $\text{Tổng đã thu} = \sum \text{Payment.amount}$ của các giao dịch `CONFIRMED` (loại bỏ các giao dịch `REVERSED`).
   - $\text{Còn phải thu} = \text{Tổng phải thu} - \text{Tổng đã thu}$.
   - Tổng đóng góp tự nguyện được thống kê ở một chỉ số độc lập, không được cộng gộp vào doanh thu phí dịch vụ/quản lý.
7. **Quy tắc Bảo mật Phạm vi Cư dân:**
   - Cư dân chỉ được xem dữ liệu của chính căn hộ mình đang sinh sống.
   - Backend bắt buộc tự phân giải `apartmentId` từ token xác thực của cư dân, **tuyệt đối không tin cậy `apartmentId` do client truyền qua Query Param hoặc Request Body**.

---

<a name="g-coding-conventions"></a>
## G. Coding Conventions (Quy ước Lập trình)

### 1. Backend (Spring Boot / Java)

- **Cấu trúc phân lớp:**
  - `Controller`: Chỉ nhận HTTP request, validate dữ liệu đầu vào qua `@Valid`, ủy quyền xử lý cho Service, trả về `ResponseEntity<ApiResponse<T>>`.
  - `Service`: Chứa toàn bộ Business Logic, điều phối Repository, kiểm tra điều kiện nghiệp vụ.
  - `Repository`: Kế thừa `JpaRepository`, chỉ đảm nhận truy vấn và ghi dữ liệu CSDL.
  - `Entity`: Ánh xạ bảng CSDL; sử dụng `@Column` rõ ràng; tránh eager fetching không cần thiết (ưu tiên `FetchType.LAZY`).
  - `DTO`: Tách biệt hoàn toàn `RequestDTO` và `ResponseDTO`; không trả Entity thô về client.
- **Validation:** Bắt buộc dùng Bean Validation: `@NotNull`, `@NotBlank`, `@Size`, `@Min`, `@Positive`.
- **Exception Handling:**
  - Bắt lỗi tập trung tại `GlobalExceptionHandler` (`@RestControllerAdvice`).
  - Định nghĩa ngoại lệ nghiệp vụ thừa kế từ `AppException` (chứa HTTP Status và Error Code).
  - Không bao giờ để lộ SQL Exception hoặc StackTrace thô ra Response client.
- **Quản lý Transaction:**
  - Mọi phương thức Service tạo/sửa đổi dữ liệu nhiều bảng (như tạo Campaign + Obligation, hoặc Payment + Update Obligation Status) bắt buộc phải có annotation `@Transactional(rollbackFor = Exception.class)`.
- **Logging:**
  - Sử dụng SLF4J (`@Slf4j`).
  - Log các sự kiện nghiệp vụ quan trọng: tạo khoản thu, nhận webhook, xác nhận thanh toán, xử lý ngoại lệ.
  - Không dùng `System.out.println()`. Không log mật khẩu hoặc mã bí mật.

### 2. Frontend (React / Vite)

- **Tổ chức thư mục:**
  - `components/ui/`: Các thành phần nguyên tử dùng chung (Button, Input, Card, Modal, Badge, Drawer).
  - `pages/`: Các trang nghiệp vụ, đặt trong thư mục con tương ứng với module (ví dụ: `pages/fees/`, `pages/payments/`).
  - `services/`: Đóng gói các hàm gọi API (sử dụng fetch/axios tập trung tại `services/api.js`).
- **Trạng thái giao diện (UI States):**
  - Mọi màn hình tải dữ liệu đều phải xử lý đầy đủ 3 trạng thái:
    1. **Loading State:** Hiển thị spinner hoặc skeleton loading khi đang gọi API.
    2. **Error State:** Hiển thị alert hoặc toast thông báo lỗi rõ ràng nếu API thất bại.
    3. **Empty State:** Hiển thị giao diện thân thiện khi danh sách rỗng (ví dụ: "Chưa có khoản thu nào trong tháng này").
- **Tái sử dụng & Responsive:**
  - Sử dụng các UI component chung từ `components/ui` để đảm bảo đồng nhất thiết kế.
  - Thiết kế thích ứng (Responsive) tốt trên Desktop, Tablet và Mobile bằng các class của Tailwind CSS.

---

<a name="h-git-workflow"></a>
## H. Git Workflow & Quản lý Mã nguồn

### 1. Phân chia nhánh (Branching Strategy)
- `main`: Nhánh ổn định, chứa mã nguồn đã kiểm thử và sẵn sàng báo cáo/triển khai. Chỉ merge từ `develop`.
- `develop`: Nhánh tích hợp chính của cả nhóm. Mọi tính năng sau khi hoàn thành đều được tích hợp vào đây.
- `feat/<module>-<ten-tinh-nang>`: Nhánh phát triển chức năng mới (VD: `feat/f05-fee-preview`, `feat/f06-sepay-webhook`).
- `fix/<module>-<ten-loi>`: Nhánh sửa lỗi phát sinh trong quá trình tích hợp.

### 2. Quy trình làm việc nhóm
1. Kéo mã nguồn mới nhất từ `develop`:
   ```bash
   git checkout develop
   git pull origin develop
   ```
2. Tạo nhánh chức năng mới từ `develop`:
   ```bash
   git checkout -b feat/f06-qr-generation
   ```
3. Lập trình, kiểm thử cục bộ (Frontend build pass, Backend test pass).
4. Commit theo chuẩn Conventional Commits:
   ```bash
   git commit -m "feat(f06): generate dynamic QR code with payment reference"
   ```
5. Đẩy nhánh lên Remote và mở Pull Request (PR) vào `develop`.
6. Tối thiểu **1 thành viên khác** review và xác nhận không có xung đột trước khi merge vào `develop`.

### 3. Quy ước Commit Message
Cú pháp: `<type>(<scope>): <mô tả ngắn bằng tiếng Anh hoặc tiếng Việt>`
- `feat(f05)`: Thêm tính năng mới cho module F05.
- `fix(f06)`: Sửa lỗi cho module F06.
- `docs`: Cập nhật tài liệu hướng dẫn.
- `refactor`: Tái cấu trúc mã nguồn nhưng không đổi nghiệp vụ.
- `test`: Thêm hoặc sửa đổi unit/integration test.

### 4. Quy định an toàn & Bảo mật Git
Tuyệt đối **KHÔNG commit**:
- File `.env` hoặc file chứa thông tin credentials thật.
- Mật khẩu cơ sở dữ liệu, API key, JWT Secret Key, SePay Token.
- Thư mục build và dependencies: `node_modules/`, `target/`, `dist/`, `.idea/`, `.vscode/`.

---

<a name="i-ai-coding-rules"></a>
## I. AI Coding Rules (Quy tắc bắt buộc cho AI Agent)

Tất cả các AI Coding Agent (Cursor, GitHub Copilot, Gemini CLI, Claude Code, Antigravity,...) khi làm việc trong repository này **bắt buộc tuân thủ 12 điều răn sau**:

1. **Đọc `PROJECT_GUIDE.md` đầu tiên:** Nắm rõ phạm vi dự án, vai trò, phân công module và quy tắc dữ liệu trước khi viết bất kỳ dòng code nào.
2. **Đọc tài liệu module liên quan:** Nếu làm việc với F03–F07, phải đọc kỹ `docs/API_CONTRACT.md` và `BlueMoonV1_schema.sql`.
3. **Kiểm tra code hiện có trước khi sửa:** Không tạo lại các class, component, helper, enum hoặc hàm đã có sẵn trong codebase.
4. **Không tự ý thay đổi Scope & Rules:** Tuyệt đối không tự suy diễn thêm tính năng (như đòi thêm bảng, thêm vai trò SuperAdmin, thêm xuất Excel, hay đổi sang thanh toán trả góp).
5. **Không sửa đổi Schema CSDL:** Bảng, tên cột, khóa ngoại, kiểu dữ liệu đã được chốt trong `BlueMoonV1_schema.sql`. Không tự tạo migration làm lệch schema.
6. **Không xâm phạm Module của người khác:** Chỉ thao tác trên các file thuộc module được phân công. Nếu cần dữ liệu từ module khác, chỉ được sử dụng Service Interface đã quy định (mục E).
7. **Không hardcode thông tin bảo mật:** Không ghi cứng mật khẩu, tài khoản ngân hàng, mã bí mật trong code. Sử dụng biến môi trường.
8. **Đảm bảo tính trọn vẹn dữ liệu (Transaction):** Các thao tác cập nhật nhiều bảng phải luôn được bao bọc trong transaction thích hợp.
9. **Xử lý toàn diện các trạng thái UI:** Code frontend phải có đầy đủ loading state, error alert, empty state và form validation.
10. **Giữ nguyên hợp đồng API:** Không tự ý đổi tên field JSON, đổi URL endpoint hoặc kiểu dữ liệu trong `docs/API_CONTRACT.md`.
11. **Kiểm tra tính tương thích trước khi báo xong:** Luôn kiểm tra xem frontend có build thành công (`npm run build`) và backend có pass test (`mvn test`) hay không.
12. **Báo cáo rõ ràng sau khi hoàn thành task:** Báo cáo danh sách file đã tạo/sửa, logic nghiệp vụ đã hiện thực, kết quả test và các điểm cần lưu ý.

### Checklist câu hỏi AI phải tự trả lời trước khi sinh code:
- [ ] Task này thuộc Module nào (F01 ➔ F09)? Do ai phụ trách?
- [ ] Có đụng chạm đến schema CSDL không?
- [ ] Có phá vỡ API contract trong `docs/API_CONTRACT.md` không?
- [ ] Có đang can thiệp vào bảng/entity của thành viên khác không?

---

<a name="j-definition-of-done"></a>
## J. Definition of Done (Định nghĩa Hoàn thành - DoD)

Một công việc (Task / User Story) chỉ được coi là **HOÀN THÀNH (DONE)** khi đạt đầy đủ các tiêu chuẩn sau:

1. **Đúng nghiệp vụ:** Thực hiện chính xác yêu cầu trong `SRS.md` và các quy tắc nghiệp vụ tại Mục F.
2. **Tuân thủ thiết kế CSDL:** Khớp 100% với cấu trúc bảng, kiểu dữ liệu và ràng buộc của `BlueMoonV1_schema.sql`.
3. **Khớp API Contract:** Request body, Response body, HTTP Status Code và Error Code khớp hoàn toàn với `docs/API_CONTRACT.md`.
4. **Không ảnh hưởng module khác:** Module hoạt động độc lập, chỉ gọi các module khác qua Service Interface, không gây lỗi cho các phần khác.
5. **Đầy đủ xử lý ngoại lệ & Validation:** Các trường hợp thiếu dữ liệu, sai dữ liệu, dữ liệu rỗng, trùng lặp đều được bắt lỗi và phản hồi thân thiện.
6. **Xây dựng thành công:** 
   - Backend: Biên dịch và chạy test thành công (`.\mvnw.cmd test` báo `BUILD SUCCESS`).
   - Frontend: Kiểm tra kiểu và đóng gói thành công (`npm run build` không lỗi).
7. **Bảo mật & Sạch sẽ:** Không commit secrets, không có code thừa (comment out, console.log debug vô nghĩa).
8. **Đã được Review:** Đã tạo Pull Request vào `develop` và được thành viên phụ trách liên quan kiểm tra, chấp thuận.

---

<a name="k-open-issues--decisions-needed"></a>
## K. Open Issues / Decisions Needed (Vấn đề cần thống nhất)

Mục này ghi nhận các điểm khác biệt hoặc thông tin cần 4 thành viên xác nhận trong các buổi họp nhóm tiếp theo:

| STT | Vấn đề / Điểm mâu thuẫn | Hiện trạng trong tài liệu | Hướng đề xuất giải quyết | Trạng thái |
|---|---|---|---|:---:|
| 1 | **Đối tượng chịu công nợ phí bắt buộc** | `SRS.md` có chỗ đề cập "Công nợ theo dõi theo hộ gia đình", nhưng `BlueMoonV1_schema.sql` và `API_CONTRACT.md` đã chốt "Nghĩa vụ phí tạo theo Căn hộ (`apartment_id`)". | **Đã thống nhất theo Schema & Contract:** Nghĩa vụ thu phí gắn với Căn hộ. Bổ sung liên kết lịch sử để biết căn hộ đó tại thời điểm thu do hộ nào đang ở. | **ĐÃ CHỐT** |
| 2 | **Ngôn ngữ mã nguồn Frontend** | Khung ban đầu scaffold dùng TypeScript (`.ts`, `.tsx`), nhưng nhánh UI demo chuyển sang JavaScript (`.jsx`). | Nhóm cần chốt tiếp tục code bằng JavaScript (`.jsx`) kết hợp propTypes hoặc tái chuẩn hóa lại TypeScript để nhất quán toàn dự án. | **CẦN HỌP CHỐT** |
| 3 | **Môi trường Test Webhook SePay** | Chưa có tài khoản ngân hàng và SePay API Key môi trường staging thực tế. | Xây dựng 1 Mock SePay Controller nội bộ ở backend để giả lập bắn webhook IPN phục vụ kiểm thử tự động trước khi tích hợp SePay thật. | **ĐỀ XUẤT** |
| 4 | **Định dạng số tiền hiển thị trên UI** | Schema lưu số nguyên VND (`100000`), mock UI hiển thị dạng viết tắt (`100k`, `92,8 triệu ₫`). | Backend luôn trả về số nguyên VND (`DECIMAL(16,0)` ➔ `Long`/`BigInteger`). Frontend phụ trách toàn bộ việc format hiển thị tiền tệ tiếng Việt (`Intl.NumberFormat`). | **ĐÃ THỐNG NHẤT** |

---

*Tài liệu này là căn cứ tối cao cho mọi quyết định triển khai kỹ thuật của dự án BlueMoon. Bất kỳ sự thay đổi nào đối với tài liệu này đều phải được sự đồng thuận của cả 4 thành viên trong nhóm.*
