# BlueMoon – Apartment Management System

Hệ thống quản lý chung cư BlueMoon là dự án Bài tập lớn môn **Kỹ thuật Phần mềm**.

Mục tiêu của dự án là xây dựng một ứng dụng hỗ trợ Ban quản trị và nhân viên quản lý các hoạt động của chung cư, tập trung vào quản lý căn hộ, hộ gia đình, cư dân, các khoản thu, thanh toán và những nghiệp vụ liên quan.

---

## 1. Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Frontend | ReactJS, JavaScript (JSX), Vite |
| UI | Tailwind CSS v4 |
| Routing | React Router DOM |
| Backend | Java Spring Boot |
| Build Backend | Maven Wrapper |
| ORM | Spring Data JPA |
| Database | MySQL 8.x |
| API | RESTful API |

---

## 2. Cấu trúc Repository

```text
BlueMoon-Project/
├── frontend/                       # Ứng dụng giao diện
│   ├── public/                     # Static assets
│   ├── src/
│   │   ├── assets/                 # Hình ảnh, icons
│   │   ├── components/             # Reusable UI components
│   │   │   └── ui/                 # Các component giao diện dùng chung
│   │   ├── layouts/                # Layout chung của ứng dụng
│   │   ├── pages/                  # Các trang giao diện
│   │   ├── data/                   # Mock data
│   │   ├── services/               # API services
│   │   ├── hooks/                  # Custom React hooks
│   │   ├── App.jsx                 # Root component và routing
│   │   ├── main.jsx                # Entry point
│   │   └── index.css               # Global styles và design tokens
│   ├── .env.example                # Mẫu biến môi trường
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.js
│
├── backend/                        # Ứng dụng Backend
│   ├── src/main/java/com/bluemoon/
│   │   ├── controller/             # REST Controllers
│   │   ├── service/                 # Service interfaces và nghiệp vụ
│   │   │   └── impl/               # Service implementations
│   │   ├── repository/              # Spring Data JPA Repositories
│   │   ├── entity/                  # JPA Entities
│   │   ├── dto/
│   │   │   ├── request/             # Request DTOs
│   │   │   └── response/            # Response DTOs
│   │   ├── config/                  # Cấu hình ứng dụng
│   │   ├── exception/               # Xử lý ngoại lệ
│   │   └── BluemoonBackendApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── .env.example
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── .gitignore
└── README.md
```

### Vai trò các thành phần chính

**Frontend**
- `pages/`: Chứa các trang giao diện theo từng nhóm chức năng.
- `components/`: Chứa các component có thể tái sử dụng.
- `layouts/`: Chứa layout chung, ví dụ sidebar và header.
- `services/`: Chứa các module giao tiếp với Backend.
- `hooks/`: Chứa custom hooks.
- `data/`: Dữ liệu mẫu phục vụ giao diện và quá trình phát triển.

**Backend**
- `controller/`: Tiếp nhận HTTP request và trả về response.
- `service/`: Xử lý logic nghiệp vụ.
- `repository/`: Truy vấn và thao tác dữ liệu thông qua Spring Data JPA.
- `entity/`: Ánh xạ các đối tượng Java với bảng trong database.
- `dto/`: Định nghĩa dữ liệu đầu vào và đầu ra của API.
- `config/`: Cấu hình hệ thống như CORS.
- `exception/`: Xử lý lỗi tập trung.

---

## 3. Các nhóm chức năng

Hệ thống được chia thành 9 nhóm chức năng chính:

| Mã | Nhóm chức năng | Mô tả |
|---|---|---|
| F01 | Đăng nhập và quản lý tài khoản | Đăng nhập, quản lý tài khoản và mật khẩu |
| F02 | Quản lý căn hộ | Quản lý thông tin và trạng thái căn hộ |
| F03 | Quản lý hộ gia đình và cư dân | Quản lý hộ gia đình, thành viên và thông tin cư dân |
| F04 | Quản lý biến động cư trú | Quản lý chuyển đến, chuyển đi, tạm trú, tạm vắng và lịch sử cư trú |
| F05 | Quản lý khoản thu | Quản lý các khoản thu bắt buộc và đóng góp tự nguyện |
| F06 | Quản lý thanh toán | Theo dõi, ghi nhận và quản lý các giao dịch thanh toán |
| F07 | Thống kê và báo cáo | Tổng hợp tình hình thu, đã thu và còn nợ |
| F08 | Thông báo | Quản lý thông báo và nhắc phí |
| F09 | Quản lý phản ánh | Tiếp nhận và xử lý phản ánh của cư dân |

---

## 4. Hướng dẫn cài đặt và khởi chạy

### 4.1. Yêu cầu môi trường

Cài đặt các công cụ sau trước khi chạy dự án:

| Công cụ | Phiên bản |
|---|---|
| Node.js | 18+ (khuyến nghị Node 20+) |
| npm | Đi kèm Node.js |
| Java JDK | 17+ (khuyến nghị JDK 21) |
| MySQL Server | 8.x |
| Git | Phiên bản mới nhất |

Kiểm tra môi trường:

```bash
node -v
npm -v
java -version
git --version
```

### 4.2. Clone repository

```bash
git clone <repository-url>
cd BlueMoon-Project
```

Thay `<repository-url>` bằng URL repository Git của nhóm.

### 4.3. Khởi chạy Backend

**Bước 1: Cấu hình database**

Đảm bảo MySQL Server đang chạy.

Tạo database:

```sql
CREATE DATABASE bluemoon_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

**Bước 2: Cấu hình biến môi trường**

Backend sử dụng các biến môi trường sau:

| Biến | Giá trị mặc định | Mô tả |
|---|---|---|
| `SERVER_PORT` | `8080` | Cổng chạy Backend |
| `DB_HOST` | `localhost` | Địa chỉ MySQL |
| `DB_PORT` | `3306` | Cổng MySQL |
| `DB_NAME` | `bluemoon_db` | Tên database |
| `DB_USERNAME` | `root` | Tài khoản MySQL |
| `DB_PASSWORD` | Trống | Mật khẩu MySQL |
| `JPA_DDL_AUTO` | `update` | Chế độ cập nhật schema |

Có thể tham khảo `backend/.env.example`.

Lưu ý: Spring Boot cần nhận được các biến môi trường thông qua hệ điều hành, IDE hoặc cấu hình chạy. Không mặc định rằng file `.env` sẽ tự động được Spring Boot đọc.

Không commit thông tin đăng nhập database hoặc credentials lên Git.

**Bước 3: Chạy Backend**

Trên Windows PowerShell hoặc CMD:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Trên macOS/Linux:

```bash
cd backend
./mvnw spring-boot:run
```

Khi khởi động thành công, Backend mặc định chạy tại:

- Base URL: `http://localhost:8080`
- Health check: `http://localhost:8080/api/health`

Có thể truy cập endpoint health check trên trình duyệt để kiểm tra Backend.

### 4.4. Khởi chạy Frontend

Mở một terminal khác tại thư mục gốc của repository.

**Bước 1: Cài đặt dependencies**

```bash
cd frontend
npm install
```

**Bước 2: Cấu hình biến môi trường**

Tạo file `.env` trong thư mục `frontend/`:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Có thể tham khảo `frontend/.env.example`.

**Bước 3: Chạy Frontend**

```bash
npm run dev
```

Frontend mặc định chạy tại:

`http://localhost:5173`

---

## 5. Các lệnh thường dùng

### Frontend

Chạy tại thư mục `frontend/`:

| Lệnh | Mục đích |
|---|---|
| `npm install` | Cài đặt dependencies |
| `npm run dev` | Chạy môi trường development |
| `npm run build` | Build ứng dụng |
| `npm run lint` | Kiểm tra lint |

### Backend

Chạy tại thư mục `backend/`:

| Lệnh | Mục đích |
|---|---|
| `.\mvnw.cmd spring-boot:run` | Chạy Backend trên Windows |
| `./mvnw spring-boot:run` | Chạy Backend trên macOS/Linux |
| `.\mvnw.cmd test` | Chạy test trên Windows |
| `./mvnw test` | Chạy test trên macOS/Linux |
| `.\mvnw.cmd clean package` | Build Backend trên Windows |
| `./mvnw clean package` | Build Backend trên macOS/Linux |

---

## 6. Trạng thái phát triển hiện tại

### Frontend

Giao diện demo cho các nhóm chức năng F01–F09 đã được triển khai. Đây là
giai đoạn hoàn thiện UI; các nghiệp vụ, xác thực và tích hợp API sẽ được bổ sung
sau bởi nhóm phát triển Backend/chức năng.

Frontend hiện có:
- Routing giữa các màn hình.
- Layout và các UI components dùng chung.
- Responsive layout.
- Các form, bảng dữ liệu, modal, drawer và toast.
- Mock data để trình diễn giao diện.
- Các tương tác cơ bản ở phía client.

### Các route giao diện hiện có

| Route | Màn hình |
|---|---|
| `/login` | Đăng nhập demo |
| `/dashboard` | Dashboard quản lý |
| `/apartments` | Quản lý căn hộ |
| `/residents` | Hộ gia đình và cư dân |
| `/residency` | Biến động cư trú |
| `/fees` | Khoản thu và tạo khoản thu |
| `/payments` | Thu phí, giao dịch và QR minh họa |
| `/reports` | Thống kê thu phí |
| `/notices` | Thông báo và nhắc phí |
| `/issues` | Phản ánh cư dân |

**Lưu ý:** Các thao tác trên giao diện hiện tại sử dụng mock data và local
state. Các màn hình tạo/cập nhật, ghi nhận thanh toán, QR và đăng nhập chỉ mô
phỏng luồng UI; chúng chưa xác thực người dùng, chưa gọi API và chưa lưu dữ liệu
vào database.

### Backend và Database

Backend được phát triển độc lập bằng Spring Boot và MySQL. Các API và logic nghiệp vụ sẽ được triển khai, kiểm thử và tích hợp với Frontend theo tiến độ phát triển của nhóm.
