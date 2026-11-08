export type Row = Record<string, string>
export const money = (amount: number) => `${amount.toLocaleString('vi-VN')} ₫`

export const apartments: Row[] = [
  ['P0101', '54 m²', 'Nguyễn Văn Minh', 'Đang ở', '2'], ['P0102', '62 m²', 'Trần Thu Hà', 'Đang ở', '3'], ['P0203', '70 m²', 'Lê Hoàng Nam', 'Tạm vắng', '2'], ['P0305', '76 m²', 'Nguyễn Minh Tuấn', 'Đang ở', '4'], ['P0402', '62 m²', 'Chưa cập nhật', 'Trống', '0'], ['P0507', '86 m²', 'Phạm Ngọc Anh', 'Đang ở', '3'],
].map(([code, area, owner, status, members]) => ({ code, area, owner, status, members }))

export const households: Row[] = [
  ['HG-0101', 'P0101', 'Nguyễn Văn Minh', '2', 'Chủ hộ'], ['HG-0102', 'P0102', 'Trần Thu Hà', '3', 'Chủ hộ'], ['HG-0305', 'P0305', 'Nguyễn Minh Tuấn', '4', 'Chủ hộ'], ['HG-0507', 'P0507', 'Phạm Ngọc Anh', '3', 'Chủ hộ'],
].map(([id, apartment, owner, members, status]) => ({ id, apartment, owner, members, status }))

export const residents: Row[] = [
  ['CD-001', 'Nguyễn Minh Tuấn', 'P0305', 'Chủ hộ', 'Đang cư trú'], ['CD-002', 'Lê Thu Phương', 'P0305', 'Vợ/chồng', 'Đang cư trú'], ['CD-003', 'Nguyễn Gia Hân', 'P0305', 'Con', 'Đang cư trú'], ['CD-004', 'Trần Thu Hà', 'P0102', 'Chủ hộ', 'Đang cư trú'], ['CD-005', 'Lê Hoàng Nam', 'P0203', 'Chủ hộ', 'Tạm vắng'],
].map(([id, name, apartment, relation, status]) => ({ id, name, apartment, relation, status }))

export const residency: Row[] = [
  ['BD-001', 'Tạm trú', 'Hoàng Anh Dũng', 'P0305', '12/10/2026', 'Chờ duyệt'], ['BD-002', 'Chuyển đến', 'Nguyễn Thu Trang', 'P0507', '08/10/2026', 'Đã duyệt'], ['BD-003', 'Tạm vắng', 'Lê Hoàng Nam', 'P0203', '04/10/2026', 'Đã duyệt'], ['BD-004', 'Chuyển đi', 'Phạm Kim Chi', 'P0402', '28/09/2026', 'Đã duyệt'],
].map(([id, type, resident, apartment, date, status]) => ({ id, type, resident, apartment, date, status }))

export const fees: Row[] = [
  ['KT-2610-QL', 'Phí quản lý tháng 10/2026', '10/2026', '68.400.000 ₫', '31.700.000 ₫', 'Đang thu'], ['KT-2610-DV', 'Phí dịch vụ tháng 10/2026', '10/2026', '24.400.000 ₫', '14.200.000 ₫', 'Đang thu'], ['KT-2609-QL', 'Phí quản lý tháng 09/2026', '09/2026', '68.400.000 ₫', '52.400.000 ₫', 'Quá hạn'], ['KT-2609-DV', 'Phí dịch vụ tháng 09/2026', '09/2026', '24.400.000 ₫', '17.100.000 ₫', 'Quá hạn'],
].map(([id, name, period, due, paid, status]) => ({ id, name, period, due, paid, status }))

export const payments: Row[] = [
  ['TT-0001', 'P0101', 'Phí quản lý', '10/2026', '378.000 ₫', 'Đã nộp'], ['TT-0002', 'P0102', 'Phí quản lý', '10/2026', '434.000 ₫', 'Đã nộp'], ['TT-0003', 'P0203', 'Phí dịch vụ', '10/2026', '175.000 ₫', 'Chờ xác nhận'], ['TT-0004', 'P0305', 'Phí quản lý', '10/2026', '532.000 ₫', 'Chưa nộp'], ['TT-0005', 'P0507', 'Phí dịch vụ', '09/2026', '215.000 ₫', 'Cần kiểm tra'],
].map(([id, apartment, fee, period, amount, status]) => ({ id, apartment, fee, period, amount, status }))

export const notices: Row[] = [
  ['TB-026', 'Thông báo thu phí tháng 10/2026', 'Nhắc phí', '25/10/2026', 'Đã gửi'], ['TB-025', 'Lịch bảo trì thang máy tòa A', 'Vận hành', '22/10/2026', 'Đã gửi'], ['TB-024', 'Tổ chức họp cư dân quý IV', 'Cộng đồng', '18/10/2026', 'Bản nháp'], ['TB-023', 'Quy định đăng ký khách ra vào', 'Quy định', '12/10/2026', 'Đã gửi'],
].map(([id, title, category, date, status]) => ({ id, title, category, date, status }))

export const issues: Row[] = [
  ['PA-101', 'Rò rỉ nước tại hành lang tầng 3', 'P0305', '25/10/2026', 'Mới'], ['PA-100', 'Đèn chiếu sáng bãi xe hỏng', 'P0102', '23/10/2026', 'Đang xử lý'], ['PA-099', 'Tiếng ồn sau 22 giờ', 'P0507', '20/10/2026', 'Đã xử lý'], ['PA-098', 'Cập nhật thẻ xe tháng 10', 'P0203', '18/10/2026', 'Đã xử lý'],
].map(([id, title, apartment, date, status]) => ({ id, title, apartment, date, status }))
