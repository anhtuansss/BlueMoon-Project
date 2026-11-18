# BlueMoon V1 — API Contract F03–F05–F06–F07

> **Tài liệu cho AI coding agent và thành viên triển khai.** Đọc file này cùng `BlueMoonV1_schema_revised.sql` trước khi code. Đây là contract tích hợp giữa các module, không phải tài liệu hướng dẫn kiến trúc tổng thể.

## 1. Quy tắc bắt buộc khi vibecode

1. **Không tự ý đổi** tên bảng/cột, enum, endpoint, request/response field hoặc business rule trong file này.
2. Database baseline là `BlueMoonV1_schema_revised.sql`. Không tạo lại bảng hoặc thêm field nếu schema chưa có; nếu thật sự cần thay đổi, đề xuất migration và cập nhật contract trước.
3. Backend là **Spring Boot modular monolith**. Module gọi nhau qua service/interface nội bộ, không gọi HTTP đến chính backend.
4. Không query hoặc sửa trực tiếp bảng do module khác sở hữu. Dùng service contract ở mục 6.
5. Không hard-code tài khoản ngân hàng, JWT secret, SePay credential hoặc dữ liệu cá nhân thật.
6. Mọi thao tác tạo campaign/nghĩa vụ/thanh toán/đảo thanh toán phải có transaction phù hợp; không để dữ liệu cập nhật một nửa.
7. Viết test cho business rule trước khi coi task hoàn thành. Không chỉ tạo Controller/DTO rồi đánh dấu done.

## 2. Quy ước API chung

| Mục | Quy ước |
|---|---|
| Prefix | `/api/v1` |
| Content type | `application/json; charset=utf-8` |
| Auth | `Authorization: Bearer <JWT>`; webhook SePay dùng xác thực webhook riêng |
| Role | `BQT`, `NV`, `RESIDENT` |
| JSON | `camelCase` |
| ID | Trả về dạng string dù DB dùng `BIGINT UNSIGNED` |
| Tiền VND | Số nguyên, không dùng float/double |
| Diện tích/đơn giá | Decimal; xử lý bằng `BigDecimal` |
| Date | `YYYY-MM-DD` |
| Date-time | ISO-8601; thống nhất UTC ở backend |
| Pagination | `page=0`, `size=20`, tối đa `100` |

**Response thành công**

```json
{ "data": { "id": "123" } }
```

**Response danh sách**

```json
{
  "data": {
    "items": [], "page": 0, "size": 20,
    "totalElements": 0, "totalPages": 0
  }
}
```

**Response lỗi**

```json
{
  "error": {
    "code": "OBLIGATION_ALREADY_PAID",
    "message": "Nghĩa vụ này đã được thanh toán.",
    "details": []
  }
}
```

HTTP status: `200` đọc/cập nhật; `201` tạo mới; `400` validation; `401` chưa xác thực; `403` không đủ quyền/phạm vi; `404` không tìm thấy; `409` xung đột trạng thái/trùng; `422` dữ liệu hợp lệ về định dạng nhưng không thỏa nghiệp vụ; `500` lỗi server.

## 3. Quy tắc nghiệp vụ xuyên module

- **Khoản thu bắt buộc gắn với căn hộ**, dùng `apartment_id`; không gắn công nợ vào `household_id`.
- Phí dịch vụ mặc định `2.500 VND/m²/tháng`; phí quản lý mặc định `7.000 VND/m²/tháng`.
- Công thức: `amountDue = ROUND(areaSnapshot × appliedUnitPrice, 0)` VND. Dùng `BigDecimal` và `RoundingMode.HALF_UP`.
- Snapshot `area`, `unitPrice`, `amountDue` khi sinh nghĩa vụ. Không lấy diện tích/đơn giá từ frontend để quyết định số tiền.
- Tạo khoản thu cho `ONE_APARTMENT` hoặc `ALL_ACTIVE`. `ALL_ACTIVE` gồm căn hộ đang hoạt động kể cả căn hộ trống; loại căn hộ `is_active=false`.
- Một `FeeObligation` chỉ thanh toán **một lần và đủ 100%**. Không có partial payment.
- Tiền mặt/chuyển khoản thường do BQT/NV ghi nhận sau khi kiểm tra. QR chỉ tạo `PaymentRequest`; tạo QR không có nghĩa là đã thanh toán.
- Chỉ webhook SePay được xác thực, tiền vào đúng tài khoản, khớp duy nhất mã tham chiếu và đúng số tiền mới tự động tạo Payment.
- Đóng góp tự nguyện dùng `DonationCampaign`; chỉ ghi nhận số tiền thực tế đã nhận. Không tạo `FeeObligation`, không tính khoản chưa đóng thành công nợ.
- Chỉ BQT được hủy khoản thu và đảo/hủy hiệu lực bản ghi thanh toán. Không xóa vật lý lịch sử thanh toán.
- BQT/NV xem dữ liệu toàn hệ thống. Cư dân chỉ xem dữ liệu căn hộ hiện tại của chính mình; backend tự xác định căn hộ từ tài khoản, không tin `apartmentId` client gửi lên.

## 4. Contract theo module

### 4.1. F03 — Căn hộ, hộ gia đình, cư dân (Thành)

**F03 sở hữu:** `Apartment`, `Person`, `Household`, `HouseholdPerson`, `HouseholdApartment`, `ResidenceChange`, `ResidentChangeRequest`.

| Method | Endpoint | Quyền | Ghi chú |
|---|---|---|---|
| GET | `/apartments` | BQT/NV; cư dân theo phạm vi của mình | Lọc `isActive`, `keyword`; phân trang |
| GET | `/apartments/{apartmentId}` | BQT/NV; cư dân theo phạm vi của mình | Chi tiết căn hộ |
| GET | `/apartments/{apartmentId}/residents` | BQT/NV; cư dân trong phạm vi | Cư dân hiện tại liên quan căn hộ |
| GET/POST | `/residents` | BQT/NV | Tra cứu/tạo hồ sơ |
| GET/PATCH | `/residents/{personId}` | BQT/NV; cư dân với hồ sơ của mình | Xem/cập nhật hồ sơ được phép |
| GET/POST | `/households` | BQT/NV | Tra cứu/tạo hộ |
| POST | `/households/{householdId}/members` | BQT/NV | Thêm thành viên |
| GET | `/me/residence` | RESIDENT | Hồ sơ và căn hộ hiện tại của tài khoản |

DTO căn hộ F05 cần dùng:

```json
{
  "apartmentId": "101",
  "apartmentNumber": "A-101",
  "area": 70.5,
  "isActive": true
}
```

`GET /apartments?isActive=true` trả căn hộ đang hoạt động kể cả căn hộ chưa có hộ/cư dân.

**Service nội bộ F03 cung cấp:**

```java
public interface ApartmentQueryService {
    List<BillableApartmentSnapshot> findAllActiveApartments();
    BillableApartmentSnapshot getActiveApartment(String apartmentId);
    ResidentApartmentScope getCurrentApartmentForAccount(Long accountId);
}
```

`BillableApartmentSnapshot`: `apartmentId`, `apartmentNumber`, `area`, `isActive`.
`ResidentApartmentScope`: `accountId`, `personId`, `apartmentId` (có thể null nếu chưa có căn hộ hiện tại).

F05/F06/F07 không tự join bảng cư trú để suy ra căn hộ. Dùng service trên.

### 4.2. F05 — Khoản thu và đóng góp (Long)

**F05 sở hữu:** `FeeType`, `CollectionCampaign`, `FeeObligation`, `DonationCampaign`, `FeeObligationHistory`.

| Method | Endpoint | Quyền |
|---|---|---|
| GET | `/fee-types` | BQT/NV |
| PATCH | `/fee-types/{feeTypeId}/default-price` | BQT |
| POST | `/fee-campaigns/preview` | BQT/NV |
| POST | `/fee-campaigns` | BQT/NV |
| GET | `/fee-campaigns` và `/fee-campaigns/{campaignId}` | BQT/NV; cư dân chỉ dữ liệu liên quan căn hộ mình |
| PATCH | `/fee-campaigns/{campaignId}` | BQT/NV; chỉ sửa tên, hạn nộp, mô tả theo trạng thái |
| POST | `/fee-campaigns/{campaignId}/cancel` | BQT |
| GET | `/fee-obligations` và `/fee-obligations/{obligationId}` | BQT/NV; cư dân chỉ nghĩa vụ căn hộ mình |
| POST | `/donation-campaigns` | BQT/NV |
| GET | `/donation-campaigns` và `/donation-campaigns/{id}` | BQT/NV; cư dân chỉ xem đợt được phép |
| POST | `/donation-campaigns/{id}/activate` | BQT/NV |
| POST | `/donation-campaigns/{id}/close` | BQT/NV |
| POST | `/donation-campaigns/{id}/cancel` | BQT/NV |

**Preview khoản thu** — không ghi DB:

`POST /fee-campaigns/preview?page=0&size=50`

```json
{
  "feeTypeId": "2",
  "campaignName": "Phí quản lý tháng 11/2026",
  "feePeriod": "2026-11",
  "collectionStartDate": "2026-11-18",
  "dueDate": "2026-11-30",
  "description": "Phí quản lý tháng 11",
  "target": { "mode": "ALL_ACTIVE" }
}
```

Hoặc `target: { "mode": "ONE_APARTMENT", "apartmentId": "101" }`.

Response preview trả `appliedUnitPrice`, `apartmentCount`, `totalAmount`, danh sách item gồm `apartmentId`, `apartmentNumber`, `areaSnapshot`, `unitPriceSnapshot`, `amountDue`.

**Tạo khoản thu:** body giống preview. Backend tự truy vấn lại diện tích/đơn giá, tạo `CollectionCampaign` và toàn bộ `FeeObligation` trong một transaction. Chỉ tạo nghĩa vụ cho căn hộ target đang active. Không dùng dữ liệu preview do client gửi lại để ghi DB.

**Ràng buộc schema:** `UNIQUE(fee_type_id, fee_period)` khiến một loại phí chỉ có một campaign trong một kỳ, kể cả campaign cũ đã `CANCELLED`. Khi trùng trả `409 DUPLICATE_FEE_CAMPAIGN`. Không tự đổi constraint.

Không cho sửa snapshot `feeTypeId`, `feePeriod`, `appliedUnitPrice`, `areaSnapshot`, `unitPriceSnapshot`, `amountDue` sau khi tạo. Chỉ BQT hủy khoản thu; không xóa vật lý. Không cho hủy campaign có Payment `CONFIRMED` còn hiệu lực; phải xử lý đảo thanh toán đúng quy trình trước.

**Đóng góp tự nguyện:** body tạo campaign gồm `campaignName`, `description`, `startDate`, `endDate`, `targetAmount` (nullable). Campaign bắt đầu `DRAFT`, gọi `/activate` để chuyển sang nhận đóng góp. Không sinh `FeeObligation`. Việc ghi nhận tiền thực tế thuộc F06.

### 4.3. F06 — Thu tiền, QR, SePay (Tuấn)

**F06 sở hữu:** `Payment`, `PaymentRequest`, `BankTransaction`.

Enum đúng schema:

- `Payment.payment_status`: `CONFIRMED`, `REVERSED`.
- `Payment.payment_method`: `CASH`, `BANK_TRANSFER`, `QR`.
- `Payment.payment_source`: `MANUAL`, `AUTOMATIC`.
- `PaymentRequest.status`: `PENDING`, `MATCHED`, `EXPIRED`, `CANCELLED`.
- `BankTransaction.processing_status`: `RECEIVED`, `MATCHED`, `UNMATCHED`, `INVALID`.

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/fee-obligations/{id}/payments` | BQT/NV |
| POST | `/donation-campaigns/{id}/payments` | BQT/NV |
| POST | `/fee-obligations/{id}/payment-requests` | RESIDENT, căn hộ hiện tại |
| POST | `/donation-campaigns/{id}/payment-requests` | RESIDENT, căn hộ hiện tại |
| GET | `/payment-requests/{id}` | Người tạo và BQT/NV theo quyền |
| POST | `/integrations/sepay/webhook` | SePay đã xác thực; không dùng JWT user |
| GET | `/bank-transactions` và `/bank-transactions/{id}` | BQT/NV theo quyền |
| POST | `/bank-transactions/{id}/resolve` | BQT/NV theo quyền xử lý ngoại lệ |
| POST | `/payments/{id}/reverse` | Chỉ BQT |

**Ghi nhận phí bắt buộc thủ công** — `POST /fee-obligations/{id}/payments`:

```json
{
  "amount": 493500,
  "method": "CASH",
  "paidAt": "2026-11-18T07:00:00Z",
  "payerPersonId": "25"
}
```

`amount` phải bằng đúng `amount_due`; `method` chỉ `CASH` hoặc `BANK_TRANSFER`. Backend tự lấy `apartmentId`, `obligationId`, `recordedBy`; tạo Payment `CONFIRMED/MANUAL`; chuyển obligation sang `PAID` trong transaction. Từ chối nếu đã thanh toán, bị hủy, hoặc số tiền thiếu/thừa.

**Ghi nhận đóng góp thủ công** — `POST /donation-campaigns/{id}/payments`:

```json
{
  "apartmentId": "101",
  "amount": 200000,
  "method": "CASH",
  "paidAt": "2026-11-18T07:00:00Z",
  "payerPersonId": "25"
}
```

Số tiền thực nhận phải `> 0`; tạo Payment với `donationCampaignId`, `obligationId=null`, `payment_status=CONFIRMED`, `payment_source=MANUAL`. Không sinh nghĩa vụ phí.

**Tạo QR:**

- QR phí: `POST /fee-obligations/{id}/payment-requests`; không nhận số tiền từ client, lấy amount từ obligation.
- QR donation: `POST /donation-campaigns/{id}/payment-requests` với `{ "amount": 200000 }`; amount do cư dân chọn và phải `>0`.
- Backend lấy căn hộ từ F03 và gán `createdBy` là account đang đăng nhập. QR trả `paymentRequestId`, `amount`, `paymentReference`, `status`, `expiresAt`, thông tin tài khoản nhận và QR image URL.
- Một obligation chỉ có tối đa một PaymentRequest `PENDING` nhờ unique generated column. Nếu request còn hiệu lực đã tồn tại, trả request đó.
- Tạo/quét QR không tạo Payment và không đổi obligation sang `PAID`.

**Webhook SePay:**

- Payload thật và cơ chế xác thực phải theo tài liệu sản phẩm SePay mà nhóm sử dụng. Tạo adapter chuyển payload ngoài thành DTO nội bộ; không giả định chữ ký/payload nếu chưa xác minh.
- Xác thực trước khi xử lý. `external_transaction_id` là khóa idempotency (unique).
- Chỉ giao dịch `direction=IN`, đúng tài khoản nhận, khớp đúng một PaymentRequest `PENDING`, chưa hết hạn, đúng reference và amount mới tạo Payment `CONFIRMED/QR/AUTOMATIC`.
- Giao dịch hợp lệ nhưng không khớp: lưu `BankTransaction` `UNMATCHED` hoặc `INVALID`, không tạo Payment, không đổi obligation sang `PAID`.
- Webhook lặp cùng external ID không tạo thêm BankTransaction/Payment.

**Đảo thanh toán:** `POST /payments/{id}/reverse` với `{ "reason": "..." }`. Chỉ BQT. Giữ bản ghi, chuyển `payment_status=REVERSED`, set `reversedBy`, `reversedAt`, `reversalReason`. Nếu là phí bắt buộc, tính lại obligation về `UNPAID` hoặc `OVERDUE`. Nếu là donation, giảm tổng nhận thực tế. Thao tác cùng transaction.

### 4.4. F07 — Tra cứu và thống kê (Long)

**F07 chỉ đọc/tổng hợp**, không tự tạo hoặc sửa Payment/FeeObligation.

| Method | Endpoint | Quyền/nội dung |
|---|---|---|
| GET | `/reports/dashboard` | BQT/NV; KPI tổng quan |
| GET | `/reports/mandatory-fees/summary` | BQT/NV; tổng phải thu/đã thu/còn phải thu |
| GET | `/reports/mandatory-fees/obligations` | BQT/NV; danh sách nghĩa vụ và trạng thái |
| GET | `/reports/payments` | BQT/NV; lịch sử giao dịch, lọc theo căn hộ/khoản thu/phương thức/thời gian |
| GET | `/reports/donations/summary` | BQT/NV; tổng nhận và số căn hộ tham gia |
| GET | `/reports/donations/{id}/payments` | BQT/NV; chi tiết đóng góp |
| GET | `/me/financial-summary` | RESIDENT; căn hộ hiện tại |
| GET | `/me/fee-obligations` | RESIDENT; nghĩa vụ căn hộ hiện tại |
| GET | `/me/payments` | RESIDENT; lịch sử thanh toán căn hộ hiện tại |
| GET | `/me/donations` | RESIDENT; lịch sử đóng góp căn hộ hiện tại |

Công thức:

- `totalDue`: tổng `amountDue` của nghĩa vụ không bị hủy thuộc campaign không bị hủy.
- `totalCollected`: tổng Payment `CONFIRMED`; loại `REVERSED`.
- `outstanding = totalDue - totalCollected`.
- Donation: tổng Payment `CONFIRMED` theo `donationCampaignId`; loại `REVERSED`. Không có công nợ donation.
- `donationParticipantCount`: số `apartmentId` khác nhau có Payment donation `CONFIRMED` còn hiệu lực.
- Bản ghi bị hủy/đảo vẫn có thể xem trong lịch sử nhưng không cộng vào tổng hiện hành.
- Cư dân không truy cập báo cáo toàn chung cư. Không nhận `apartmentId` client để phân quyền; lấy từ F03.

## 5. Sở hữu dữ liệu và service nội bộ

| Chủ sở hữu | Bảng | Module được phép ghi |
|---|---|---|
| F03 — Thành | `Apartment`, `Person`, `Household`, `HouseholdPerson`, `HouseholdApartment`, `ResidenceChange`, `ResidentChangeRequest` | F03 |
| F05 — Long | `FeeType`, `CollectionCampaign`, `FeeObligation`, `DonationCampaign`, `FeeObligationHistory` | F05 |
| F06 — Tuấn | `Payment`, `PaymentRequest`, `BankTransaction` | F06 |
| F01 — Nguyên | `Account` | F01 |
| F07 — Long | Không sở hữu bảng nghiệp vụ; chỉ query | Read-only |

Service nội bộ tối thiểu:

- F03 cung cấp `ApartmentQueryService` như mục 4.1.
- F05 cung cấp `FeeObligationQueryService.getPayableObligation(id)` và service cập nhật trạng thái nghĩa vụ khi F06 xác nhận/đảo thanh toán. F06 không tự sửa Entity `FeeObligation`.
- F06 cung cấp query Payment hiệu lực cho F07; F07 không tự sửa dữ liệu thanh toán.
- Account do F01 sở hữu; các module lấy `accountId`, `personId`, `role` từ `CurrentUser`/Security Principal, không tự đọc JWT hoặc tạo Account.

## 6. Lỗi nghiệp vụ chuẩn

| Code | HTTP | Khi dùng |
|---|---:|---|
| `DUPLICATE_FEE_CAMPAIGN` | 409 | Trùng fee type + kỳ thu |
| `APARTMENT_NOT_ACTIVE` | 422 | Căn hộ không hoạt động |
| `OBLIGATION_ALREADY_PAID` | 409 | Nghĩa vụ đã có thanh toán hiệu lực |
| `OBLIGATION_CANCELLED` | 409 | Nghĩa vụ đã hủy |
| `PAYMENT_AMOUNT_MISMATCH` | 422 | Số tiền không bằng amount due |
| `PAYMENT_REQUEST_EXPIRED` | 409 | QR hết hạn |
| `PAYMENT_REFERENCE_NOT_MATCHED` | 422 | Không tìm được request khớp duy nhất |
| `DUPLICATE_BANK_TRANSACTION` | 409/idempotent | Webhook external ID đã xử lý |
| `RESIDENT_APARTMENT_SCOPE_FORBIDDEN` | 403 | Cư dân truy cập ngoài căn hộ hiện tại |

## 7. Checklist tích hợp tối thiểu

- [ ] F03 trả căn hộ active gồm cả căn hộ trống; loại căn hộ inactive.
- [ ] F05 preview và tạo nghĩa vụ có snapshot đúng; không sinh trùng campaign/nghĩa vụ.
- [ ] F05 tạo DonationCampaign không sinh FeeObligation.
- [ ] F06 ghi nhận phí đủ tiền: một Payment và obligation `PAID`; thanh toán thiếu/thừa/lần hai bị từ chối.
- [ ] F06 QR chưa có webhook thì chưa có Payment.
- [ ] Webhook khớp tạo đúng một Payment; webhook lặp không tạo bản ghi thứ hai.
- [ ] Webhook sai reference/amount/account hoặc chiều OUT không tạo Payment.
- [ ] F06 đảo payment giữ lịch sử và cập nhật lại obligation.
- [ ] F07 loại Payment `REVERSED` khỏi tổng thu và không coi donation là công nợ.
- [ ] Cư dân A không thể xem dữ liệu căn hộ B hoặc báo cáo toàn chung cư.

## 8. Baseline cần nhớ

- Database: `BlueMoonV1_schema_revised.sql`.
- ERD: ERD đã được nhóm chốt.
- Requirement: BlueMoon SRS v1.1 và file phân chia công việc.
- Nếu tài liệu mâu thuẫn: ưu tiên quyết định nghiệp vụ đã chốt trong SRS/nhóm; schema là baseline triển khai hiện tại. Nếu cần đổi schema, phải tạo migration và cập nhật file này trước khi code tiếp.
