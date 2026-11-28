import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Badge,
  Button,
  Card,
  Drawer,
  EmptyState,
  Field,
  inputClass,
  PageHeader,
  Toast,
  useToast,
} from '../../components/ui';
import { paymentService } from '../../services/paymentService';
import { feeService } from '../../services/feeService';

/**
 * Trang Thu tiền & Quản lý thanh toán (Thành viên 4 - Tuấn - F06).
 * Giai đoạn 1: Hoàn thiện thu phí thủ công (Tiền mặt / Chuyển khoản).
 */
export function PaymentsPage() {
  const [activeTab, setActiveTab] = useState('obligations'); // 'obligations' | 'history'
  const [obligations, setObligations] = useState([]);
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedObligation, setSelectedObligation] = useState(null);
  const [selectedPayment, setSelectedPayment] = useState(null);
  const [isResidentView, setIsResidentView] = useState(false);

  // Form nộp tiền thủ công
  const [paymentMethod, setPaymentMethod] = useState('CASH'); // 'CASH' | 'BANK_TRANSFER'
  const [paymentNotes, setPaymentNotes] = useState('');
  const [receiptNumber, setReceiptNumber] = useState('');
  const [formError, setFormError] = useState('');

  const { message, show } = useToast();

  // Tải dữ liệu ban đầu
  const loadData = async () => {
    setLoading(true);
    try {
      const [obligationsData, paymentsData] = await Promise.allSettled([
        feeService.getFeeObligations(),
        paymentService.getPayments(),
      ]);

      if (obligationsData.status === 'fulfilled' && Array.isArray(obligationsData.value)) {
        setObligations(obligationsData.value);
      }
      if (paymentsData.status === 'fulfilled' && Array.isArray(paymentsData.value)) {
        setPayments(paymentsData.value);
      }
    } catch (err) {
      console.error('Lỗi khi tải dữ liệu thanh toán:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Format tiền tệ VND
  const formatCurrency = (val) => {
    if (val === null || val === undefined) return '0 đ';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
  };

  // Format ngày tháng
  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dateStr;
    }
  };

  // Lọc danh sách nghĩa vụ phí
  const filteredObligations = useMemo(() => {
    return obligations.filter((item) => {
      const textMatch =
        (item.apartmentNumber || '').toLowerCase().includes(search.toLowerCase()) ||
        (item.feeTypeName || '').toLowerCase().includes(search.toLowerCase()) ||
        (item.campaignTitle || '').toLowerCase().includes(search.toLowerCase()) ||
        String(item.id || '').includes(search);

      if (!textMatch) return false;

      if (statusFilter === 'UNPAID') return item.status === 'UNPAID';
      if (statusFilter === 'PAID') return item.status === 'PAID';
      return true;
    });
  }, [obligations, search, statusFilter]);

  // Lọc lịch sử giao dịch
  const filteredPayments = useMemo(() => {
    return payments.filter((item) => {
      const textMatch =
        (item.apartmentNumber || '').toLowerCase().includes(search.toLowerCase()) ||
        (item.campaignTitle || '').toLowerCase().includes(search.toLowerCase()) ||
        (item.payerName || '').toLowerCase().includes(search.toLowerCase()) ||
        String(item.id || '').includes(search);

      return textMatch;
    });
  }, [payments, search]);

  // Xử lý mở Drawer nộp tiền
  const handleOpenPaymentDrawer = (obligation) => {
    setSelectedObligation(obligation);
    setPaymentMethod('CASH');
    setPaymentNotes('');
    setReceiptNumber('');
    setFormError('');
  };

  // Xử lý nộp tiền thủ công (F06 - Phase 1)
  const handleConfirmPayment = async (e) => {
    e.preventDefault();
    if (!selectedObligation) return;

    try {
      setSubmitting(true);
      setFormError('');

      const notesCombined = [
        receiptNumber ? `Mã biên nhận: ${receiptNumber}` : null,
        paymentNotes ? `Ghi chú: ${paymentNotes}` : null,
      ]
        .filter(Boolean)
        .join(' - ');

      const payload = {
        amount: selectedObligation.amountDue,
        method: paymentMethod,
        notes: notesCombined || undefined,
      };

      await paymentService.recordManualPayment(selectedObligation.id, payload);
      show(`Đã thu thành công ${formatCurrency(selectedObligation.amountDue)} cho căn hộ ${selectedObligation.apartmentNumber}!`);
      setSelectedObligation(null);
      await loadData();
    } catch (err) {
      setFormError(err.message || 'Ghi nhận thanh toán thất bại. Vui lòng thử lại.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-5">
      <PageHeader
        title={isResidentView ? 'Khoản phí của tôi' : 'Quản lý thu tiền & Thanh toán'}
        description={
          isResidentView
            ? 'Theo dõi nghĩa vụ phí và thực hiện thanh toán cho căn hộ của bạn.'
            : 'Ghi nhận nộp tiền mặt, chuyển khoản thủ công và đối soát thanh toán tự động.'
        }
        actions={
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => setIsResidentView(!isResidentView)}>
              {isResidentView ? 'Chuyển sang chế độ Thu ngân' : 'Xem góc nhìn Cư dân'}
            </Button>
            <Button variant="secondary" onClick={loadData} disabled={loading}>
              Làm mới dữ liệu
            </Button>
          </div>
        }
      />

      {/* Tabs chuyển đổi giữa Khoản phí cần thu và Lịch sử thanh toán */}
      <div className="flex gap-2 border-b border-line pb-2">
        <button
          onClick={() => setActiveTab('obligations')}
          className={`px-4 py-2 text-sm font-bold rounded-ui transition ${
            activeTab === 'obligations' ? 'bg-primary text-white' : 'text-mute hover:bg-soft'
          }`}
        >
          Khoản phí cần thu ({obligations.filter((o) => o.status === 'UNPAID').length})
        </button>
        <button
          onClick={() => setActiveTab('history')}
          className={`px-4 py-2 text-sm font-bold rounded-ui transition ${
            activeTab === 'history' ? 'bg-primary text-white' : 'text-mute hover:bg-soft'
          }`}
        >
          Lịch sử giao dịch ({payments.length})
        </button>
      </div>

      {/* Bộ lọc tìm kiếm */}
      <Card>
        <div className="flex flex-col gap-3 p-4 sm:flex-row">
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className={inputClass}
            placeholder={
              activeTab === 'obligations'
                ? 'Tìm kiếm theo số căn hộ, tên khoản thu hoặc mã nghĩa vụ...'
                : 'Tìm kiếm theo số căn hộ, mã phiếu thu, người nộp...'
            }
          />
          {activeTab === 'obligations' && (
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className={`${inputClass} sm:w-52`}
            >
              <option value="ALL">Mọi trạng thái</option>
              <option value="UNPAID">Chưa nộp</option>
              <option value="PAID">Đã nộp</option>
            </select>
          )}
        </div>
      </Card>

      {/* TAB 1: DANH SÁCH NGHĨA VỤ PHÍ CẦN THU */}
      {activeTab === 'obligations' && (
        <Card title="Danh sách khoản phí theo căn hộ">
          {loading ? (
            <div className="p-10 text-center text-sm text-mute">Đang tải dữ liệu khoản thu...</div>
          ) : filteredObligations.length === 0 ? (
            <EmptyState
              title="Không tìm thấy khoản phí nào"
              text="Chưa có nghĩa vụ phí phát sinh hoặc không có kết quả phù hợp với bộ lọc."
            />
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full text-left text-sm">
                <thead className="bg-canvas text-xs uppercase text-mute">
                  <tr>
                    <th className="px-4 py-3">Mã</th>
                    <th className="px-4 py-3">Căn hộ</th>
                    <th className="px-4 py-3">Khoản thu</th>
                    <th className="px-4 py-3">Diện tích</th>
                    <th className="px-4 py-3">Đơn giá</th>
                    <th className="px-4 py-3">Số tiền</th>
                    <th className="px-4 py-3">Trạng thái</th>
                    <th className="px-4 py-3 text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredObligations.map((item) => (
                    <tr key={item.id} className="border-t border-line hover:bg-canvas/50 transition">
                      <td className="px-4 py-3 font-semibold text-mute">#{item.id}</td>
                      <td className="px-4 py-3 font-bold text-ink">{item.apartmentNumber || `CH-${item.apartmentId}`}</td>
                      <td className="px-4 py-3">
                        <div className="font-semibold">{item.campaignTitle || item.feeTypeName}</div>
                        <div className="text-xs text-mute">{item.feeTypeName}</div>
                      </td>
                      <td className="px-4 py-3">{item.areaSnapshot} m²</td>
                      <td className="px-4 py-3">{formatCurrency(item.unitPriceSnapshot)}/m²</td>
                      <td className="px-4 py-3 font-extrabold text-ink">{formatCurrency(item.amountDue)}</td>
                      <td className="px-4 py-3">
                        <Badge value={item.status === 'PAID' ? 'Đã nộp' : 'Chưa nộp'} />
                      </td>
                      <td className="px-4 py-3 text-right">
                        {item.status === 'UNPAID' ? (
                          <Button
                            size="sm"
                            variant="primary"
                            onClick={() => handleOpenPaymentDrawer(item)}
                          >
                            {isResidentView ? 'Nộp tiền' : 'Ghi nhận thu tiền'}
                          </Button>
                        ) : (
                          <span className="text-xs text-ok font-semibold">✓ Đã thanh toán</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      )}

      {/* TAB 2: LỊCH SỬ GIAO DỊCH THANH TOÁN */}
      {activeTab === 'history' && (
        <Card title="Lịch sử các phiếu thu & giao dịch">
          {loading ? (
            <div className="p-10 text-center text-sm text-mute">Đang tải lịch sử giao dịch...</div>
          ) : filteredPayments.length === 0 ? (
            <EmptyState
              title="Chưa có giao dịch nào"
              text="Các khoản tiền được thu hoặc đối soát tự động sẽ hiển thị tại đây."
            />
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full text-left text-sm">
                <thead className="bg-canvas text-xs uppercase text-mute">
                  <tr>
                    <th className="px-4 py-3">Mã phiếu</th>
                    <th className="px-4 py-3">Căn hộ</th>
                    <th className="px-4 py-3">Khoản thu / Chiến dịch</th>
                    <th className="px-4 py-3">Số tiền</th>
                    <th className="px-4 py-3">Phương thức</th>
                    <th className="px-4 py-3">Thời gian nộp</th>
                    <th className="px-4 py-3">Trạng thái</th>
                    <th className="px-4 py-3 text-right">Chi tiết</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredPayments.map((p) => (
                    <tr key={p.id} className="border-t border-line hover:bg-canvas/50 transition">
                      <td className="px-4 py-3 font-bold text-primary">PT-{String(p.id).padStart(5, '0')}</td>
                      <td className="px-4 py-3 font-semibold">{p.apartmentNumber || `CH-${p.apartmentId}`}</td>
                      <td className="px-4 py-3">{p.campaignTitle || 'Phí quản lý & dịch vụ'}</td>
                      <td className="px-4 py-3 font-extrabold text-ink">{formatCurrency(p.amount)}</td>
                      <td className="px-4 py-3">
                        <span className="inline-flex items-center gap-1.5 text-xs font-semibold">
                          {p.paymentMethod === 'CASH' && 'Tiền mặt'}
                          {p.paymentMethod === 'BANK_TRANSFER' && 'Chuyển khoản'}
                          {p.paymentMethod === 'QR' && 'Quét mã QR'}
                          <span className="text-mute">({p.paymentSource === 'MANUAL' ? 'Thủ công' : 'Tự động'})</span>
                        </span>
                      </td>
                      <td className="px-4 py-3 text-mute">{formatDate(p.paidAt)}</td>
                      <td className="px-4 py-3">
                        <Badge value={p.paymentStatus === 'CONFIRMED' ? 'Đã xác nhận' : 'Đã hủy/đảo'} />
                      </td>
                      <td className="px-4 py-3 text-right">
                        <Button size="sm" variant="ghost" onClick={() => setSelectedPayment(p)}>
                          Xem phiếu
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      )}

      {/* DRAWER GHI NHẬN NỘP TIỀN THỦ CÔNG (F06 - PHASE 1) */}
      <Drawer
        open={!!selectedObligation}
        title="Ghi nhận nộp tiền thu phí"
        onClose={() => !submitting && setSelectedObligation(null)}
      >
        {selectedObligation && (
          <form onSubmit={handleConfirmPayment} className="space-y-5">
            {formError && (
              <Alert tone="bad" title="Không thể ghi nhận thanh toán">
                {formError}
              </Alert>
            )}

            {/* Thông tin nghĩa vụ */}
            <div className="rounded-ui bg-canvas p-4 space-y-2.5 text-sm">
              <div className="flex justify-between">
                <span className="text-mute">Căn hộ:</span>
                <b className="text-ink">{selectedObligation.apartmentNumber || `Căn hộ ${selectedObligation.apartmentId}`}</b>
              </div>
              <div className="flex justify-between">
                <span className="text-mute">Khoản thu:</span>
                <span className="font-semibold text-right">{selectedObligation.campaignTitle || selectedObligation.feeTypeName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-mute">Diện tích tính phí:</span>
                <span>{selectedObligation.areaSnapshot} m²</span>
              </div>
              <div className="flex justify-between">
                <span className="text-mute">Đơn giá áp dụng:</span>
                <span>{formatCurrency(selectedObligation.unitPriceSnapshot)}/m²</span>
              </div>
              <div className="flex justify-between border-t border-line pt-2 text-base">
                <span className="font-bold text-ink">Số tiền phải thu:</span>
                <b className="text-lg text-primary">{formatCurrency(selectedObligation.amountDue)}</b>
              </div>
            </div>

            {/* Form nhập thông tin nộp tiền */}
            <div className="space-y-4">
              <Field label="Phương thức thu tiền *">
                <select
                  value={paymentMethod}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className={inputClass}
                  disabled={submitting}
                >
                  <option value="CASH">Tiền mặt tại văn phòng BQT</option>
                  <option value="BANK_TRANSFER">Chuyển khoản trực tiếp vào TK chung cư</option>
                </select>
              </Field>

              <Field label="Mã biên nhận / Số hóa đơn">
                <input
                  value={receiptNumber}
                  onChange={(e) => setReceiptNumber(e.target.value)}
                  className={inputClass}
                  placeholder="Ví dụ: BL-2026-11-0428"
                  disabled={submitting}
                />
              </Field>

              <Field label="Ghi chú thêm">
                <textarea
                  value={paymentNotes}
                  onChange={(e) => setPaymentNotes(e.target.value)}
                  rows={3}
                  className="w-full rounded-ui border border-line bg-surface p-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
                  placeholder="Nhập tên người nộp, số điện thoại hoặc ghi chú khác nếu có..."
                  disabled={submitting}
                />
              </Field>
            </div>

            <div className="rounded-ui bg-warn-soft p-3 text-xs text-warn">
              <b>Quy tắc thanh toán bắt buộc:</b> Phí bắt buộc chỉ được thanh toán một lần và đúng 100% số tiền ({formatCurrency(selectedObligation.amountDue)}).
            </div>

            <div className="flex justify-end gap-2 pt-3 border-t border-line">
              <Button
                type="button"
                variant="secondary"
                onClick={() => setSelectedObligation(null)}
                disabled={submitting}
              >
                Hủy bỏ
              </Button>
              <Button type="submit" variant="primary" disabled={submitting}>
                {submitting ? 'Đang lưu giao dịch...' : 'Xác nhận đã thu đủ'}
              </Button>
            </div>
          </form>
        )}
      </Drawer>

      {/* DRAWER CHI TIẾT PHIẾU THU ĐÃ LƯU */}
      <Drawer
        open={!!selectedPayment}
        title="Chi tiết phiếu thu"
        onClose={() => setSelectedPayment(null)}
      >
        {selectedPayment && (
          <div className="space-y-5">
            <div className="rounded-ui bg-canvas p-4 space-y-3 text-sm">
              <div className="flex justify-between">
                <span className="text-mute">Mã phiếu thu:</span>
                <b className="text-primary text-base">PT-{String(selectedPayment.id).padStart(5, '0')}</b>
              </div>
              <div className="flex justify-between">
                <span className="text-mute">Căn hộ:</span>
                <b className="text-ink">{selectedPayment.apartmentNumber || `CH-${selectedPayment.apartmentId}`}</b>
              </div>
              <div className="flex justify-between">
                <span className="text-mute">Khoản thu / Chiến dịch:</span>
                <span className="font-semibold text-right">{selectedPayment.campaignTitle || 'Khoản thu chung cư'}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-mute">Số tiền đã thu:</span>
                <b className="text-lg text-ink">{formatCurrency(selectedPayment.amount)}</b>
              </div>
              <div className="flex justify-between">
                <span className="text-mute">Phương thức:</span>
                <span className="font-semibold">
                  {selectedPayment.paymentMethod === 'CASH' && 'Tiền mặt'}
                  {selectedPayment.paymentMethod === 'BANK_TRANSFER' && 'Chuyển khoản'}
                  {selectedPayment.paymentMethod === 'QR' && 'Quét mã QR'}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-mute">Nguồn ghi nhận:</span>
                <span>{selectedPayment.paymentSource === 'MANUAL' ? 'Thu ngân ghi nhận' : 'Tự động từ SePay'}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-mute">Thời gian thu:</span>
                <span>{formatDate(selectedPayment.paidAt)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-mute">Trạng thái:</span>
                <Badge value={selectedPayment.paymentStatus === 'CONFIRMED' ? 'Đã xác nhận' : 'Đã hủy/đảo'} />
              </div>
              {selectedPayment.notes && (
                <div className="border-t border-line pt-2">
                  <span className="text-mute block text-xs mb-1">Ghi chú giao dịch:</span>
                  <p className="text-sm italic">{selectedPayment.notes}</p>
                </div>
              )}
            </div>

            <div className="flex justify-end">
              <Button variant="secondary" onClick={() => setSelectedPayment(null)}>
                Đóng
              </Button>
            </div>
          </div>
        )}
      </Drawer>

      <Toast message={message} />
    </div>
  );
}

export default PaymentsPage;
