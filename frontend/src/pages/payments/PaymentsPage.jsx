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
  Modal,
  PageHeader,
  Toast,
  useToast,
} from '../../components/ui';
import { paymentService } from '../../services/paymentService';
import { feeService } from '../../services/feeService';

/**
 * Trang Thu tiền & Quản lý thanh toán (Thành viên 4 - Tuấn - F06).
 * Đã hoàn thiện Chặng 1 & Chặng 2 (Mã VietQR STK 202417066 & Bảng test SePay Webhook trực tiếp bên cạnh).
 */
export function PaymentsPage() {
  const [activeTab, setActiveTab] = useState('obligations'); // 'obligations' | 'history' | 'sepay'
  const [obligations, setObligations] = useState([]);
  const [payments, setPayments] = useState([]);
  const [bankTransactions, setBankTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [isResidentView, setIsResidentView] = useState(false);

  // Ghi nhận nộp tiền thủ công (Chặng 1)
  const [selectedObligation, setSelectedObligation] = useState(null);
  const [selectedPayment, setSelectedPayment] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState('CASH');
  const [paymentNotes, setPaymentNotes] = useState('');
  const [receiptNumber, setReceiptNumber] = useState('');
  const [formError, setFormError] = useState('');

  // Thanh toán mã QR VietQR & Bộ test SePay trực tiếp bên cạnh (Chặng 2)
  const [qrModalOpen, setQrModalOpen] = useState(false);
  const [qrLoading, setQrLoading] = useState(false);
  const [qrData, setQrData] = useState(null);
  const [qrError, setQrError] = useState('');

  // Form giả lập SePay Webhook bên cạnh QR
  const [simTxId, setSimTxId] = useState('');
  const [simAmount, setSimAmount] = useState('');
  const [simContent, setSimContent] = useState('');
  const [simSubmitting, setSimSubmitting] = useState(false);
  const [simLog, setSimLog] = useState(null);

  // Modal giả lập SePay độc lập
  const [mockModalOpen, setMockModalOpen] = useState(false);
  const [mockTxId, setMockTxId] = useState('');
  const [mockAmount, setMockAmount] = useState('');
  const [mockContent, setMockContent] = useState('');
  const [mockError, setMockError] = useState('');

  const { message, show } = useToast();

  // Tải dữ liệu ban đầu
  const loadData = async () => {
    setLoading(true);
    try {
      const [obligationsData, paymentsData, bankTxData] = await Promise.allSettled([
        feeService.getFeeObligations(),
        paymentService.getPayments(),
        paymentService.getBankTransactions('ALL'),
      ]);

      if (obligationsData.status === 'fulfilled' && Array.isArray(obligationsData.value)) {
        setObligations(obligationsData.value);
      }
      if (paymentsData.status === 'fulfilled' && Array.isArray(paymentsData.value)) {
        setPayments(paymentsData.value);
      }
      if (bankTxData.status === 'fulfilled' && Array.isArray(bankTxData.value)) {
        setBankTransactions(bankTxData.value);
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

  const formatCurrency = (val) => {
    if (val === null || val === undefined) return '0 đ';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
  };

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

  const copyToClipboard = (text, label) => {
    navigator.clipboard?.writeText(text);
    show(`Đã sao chép ${label}: ${text}`);
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

  // Lọc lịch sử thanh toán
  const filteredPayments = useMemo(() => {
    return payments.filter((item) => {
      return (
        (item.apartmentNumber || '').toLowerCase().includes(search.toLowerCase()) ||
        (item.campaignTitle || '').toLowerCase().includes(search.toLowerCase()) ||
        (item.payerName || '').toLowerCase().includes(search.toLowerCase()) ||
        String(item.id || '').includes(search)
      );
    });
  }, [payments, search]);

  // Lọc biến động SePay
  const filteredBankTransactions = useMemo(() => {
    return bankTransactions.filter((item) => {
      return (
        (item.content || '').toLowerCase().includes(search.toLowerCase()) ||
        (item.externalTransactionId || '').toLowerCase().includes(search.toLowerCase()) ||
        (item.processingStatus || '').toLowerCase().includes(search.toLowerCase())
      );
    });
  }, [bankTransactions, search]);

  // Xử lý nộp tiền thủ công (Chặng 1)
  const handleOpenPaymentDrawer = (obligation) => {
    setSelectedObligation(obligation);
    setPaymentMethod('CASH');
    setPaymentNotes('');
    setReceiptNumber('');
    setFormError('');
  };

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
      setFormError(err.message || 'Ghi nhận thanh toán thất bại.');
    } finally {
      setSubmitting(false);
    }
  };

  // Mở Modal QR VietQR kèm bộ giả lập SePay trực tiếp bên cạnh (Chặng 2)
  const handleOpenQrModal = async (obligation) => {
    setQrModalOpen(true);
    setQrLoading(true);
    setQrError('');
    setQrData(null);
    setSimLog(null);

    try {
      const res = await paymentService.createFeePaymentRequest(obligation.id);
      setQrData(res);

      // Tự động điền dữ liệu vào bộ test SePay bên cạnh
      const randTx = String(Math.floor(100000 + Math.random() * 900000));
      setSimTxId(randTx);
      setSimAmount(String(res.amount));
      setSimContent(`${res.paymentReference} nop tien can ho ${obligation.apartmentNumber || obligation.apartmentId}`);
    } catch (err) {
      setQrError(err.message || 'Không thể tạo mã QR thanh toán.');
    } finally {
      setQrLoading(false);
    }
  };

  // Kiểm tra trạng thái thanh toán QR
  const handleCheckQrStatus = async () => {
    if (!qrData?.id) return;
    try {
      const updated = await paymentService.getPaymentRequestById(qrData.id);
      setQrData(updated);
      if (updated.status === 'MATCHED') {
        show('Tuyệt vời! Thanh toán QR đã được SePay ghi nhận và gạch nợ thành công!');
        await loadData();
      } else {
        show(`Trạng thái: ${updated.status === 'PENDING' ? 'Đang chờ nhận tiền chuyển khoản' : updated.status}`);
      }
    } catch (err) {
      show('Lỗi khi kiểm tra trạng thái.');
    }
  };

  // Gửi Webhook SePay trực tiếp từ bảng điều khiển bên cạnh
  const handleSimulateWebhook = async (customContent = null, customAmount = null) => {
    if (!qrData) return;
    setSimSubmitting(true);
    setSimLog(null);

    const txIdToSend = simTxId || String(Math.floor(100000 + Math.random() * 900000));
    const amountToSend = customAmount !== null ? customAmount : parseFloat(simAmount);
    const contentToSend = customContent !== null ? customContent : simContent;

    try {
      await paymentService.sendMockWebhook({
        transactionId: txIdToSend,
        amount: amountToSend,
        content: contentToSend,
        referenceCode: contentToSend,
      });

      // Kiểm tra lại trạng thái request
      const updatedReq = await paymentService.getPaymentRequestById(qrData.id);
      setQrData(updatedReq);

      // Lấy biến động mới nhất vừa ghi nhận
      const latestTxs = await paymentService.getBankTransactions('ALL');
      setBankTransactions(latestTxs);
      const myTx = latestTxs.find((t) => t.externalTransactionId === txIdToSend) || latestTxs[0];

      setSimLog({
        success: true,
        txId: txIdToSend,
        status: myTx?.processingStatus || (updatedReq.status === 'MATCHED' ? 'MATCHED' : 'RECEIVED'),
        notes: myTx?.notes || 'Đã tiếp nhận webhook',
      });

      if (updatedReq.status === 'MATCHED') {
        show('⚡ SePay đã đối soát thành công! Nghĩa vụ đã chuyển sang ĐÃ NỘP.');
      } else {
        show(`⚠️ SePay đã nhận webhook: ${myTx?.processingStatus || 'Chưa khớp'}`);
      }

      await loadData();
      // Sinh mã giao dịch mới cho lần bấm tiếp theo
      setSimTxId(String(Math.floor(100000 + Math.random() * 900000)));
    } catch (err) {
      setSimLog({
        success: false,
        txId: txIdToSend,
        status: 'ERROR',
        notes: err.message || 'Lỗi gửi webhook',
      });
      show(err.message || 'Gửi webhook thất bại.');
    } finally {
      setSimSubmitting(false);
    }
  };

  // Giả lập SePay độc lập
  const handleOpenMockModal = (obligation) => {
    setMockModalOpen(true);
    setMockError('');
    const randomId = String(Math.floor(100000 + Math.random() * 900000));
    setMockTxId(randomId);

    if (obligation) {
      setMockAmount(obligation.amountDue);
      setMockContent(`BM${obligation.id}_TEST Căn hộ ${obligation.apartmentNumber} nop tien`);
    } else {
      setMockAmount('500000');
      setMockContent('BM1_TEST Chuyen khoan phi chung cu');
    }
  };

  const handleSendMockWebhook = async (e) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      setMockError('');

      await paymentService.sendMockWebhook({
        transactionId: mockTxId,
        amount: parseFloat(mockAmount),
        content: mockContent,
        referenceCode: mockContent,
      });

      show('Đã gửi Mock Webhook! Hệ thống đã đối soát tự động.');
      setMockModalOpen(false);
      await loadData();
    } catch (err) {
      setMockError(err.message || 'Gửi mock webhook thất bại.');
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
            ? 'Quét mã VietQR để thanh toán phí căn hộ hoặc tra cứu lịch sử nộp tiền.'
            : 'Thu tiền mặt, quản lý yêu cầu QR và đối soát tự động qua SePay IPN Webhook.'
        }
        actions={
          <div className="flex flex-wrap gap-2">
            {!isResidentView && (
              <Button variant="secondary" onClick={() => handleOpenMockModal(null)}>
                Giả lập SePay (Mock Webhook)
              </Button>
            )}
            <Button variant="secondary" onClick={() => setIsResidentView(!isResidentView)}>
              {isResidentView ? 'Chuyển sang chế độ Thu ngân' : 'Xem góc nhìn Cư dân'}
            </Button>
            <Button variant="secondary" onClick={loadData} disabled={loading}>
              Làm mới
            </Button>
          </div>
        }
      />

      {/* Tabs */}
      <div className="flex gap-2 border-b border-line pb-2 overflow-x-auto">
        <button
          onClick={() => setActiveTab('obligations')}
          className={`px-4 py-2 text-sm font-bold rounded-ui transition whitespace-nowrap ${
            activeTab === 'obligations' ? 'bg-primary text-white' : 'text-mute hover:bg-soft'
          }`}
        >
          Khoản phí cần thu ({obligations.filter((o) => o.status === 'UNPAID').length})
        </button>
        <button
          onClick={() => setActiveTab('history')}
          className={`px-4 py-2 text-sm font-bold rounded-ui transition whitespace-nowrap ${
            activeTab === 'history' ? 'bg-primary text-white' : 'text-mute hover:bg-soft'
          }`}
        >
          Lịch sử phiếu thu ({payments.length})
        </button>
        {!isResidentView && (
          <button
            onClick={() => setActiveTab('sepay')}
            className={`px-4 py-2 text-sm font-bold rounded-ui transition whitespace-nowrap ${
              activeTab === 'sepay' ? 'bg-primary text-white' : 'text-mute hover:bg-soft'
            }`}
          >
            Biến động ngân hàng SePay ({bankTransactions.length})
          </button>
        )}
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
                ? 'Tìm theo căn hộ, khoản thu hoặc mã nghĩa vụ...'
                : activeTab === 'history'
                ? 'Tìm theo căn hộ, mã phiếu thu, người nộp...'
                : 'Tìm theo nội dung chuyển khoản, mã giao dịch...'
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
                      <td className="px-4 py-3 text-right space-x-1.5">
                        {item.status === 'UNPAID' ? (
                          <>
                            <Button
                              size="sm"
                              variant="primary"
                              onClick={() => handleOpenQrModal(item)}
                            >
                              Thanh toán QR & Test
                            </Button>
                            {!isResidentView && (
                              <Button
                                size="sm"
                                variant="secondary"
                                onClick={() => handleOpenPaymentDrawer(item)}
                              >
                                Thu tiền mặt
                              </Button>
                            )}
                          </>
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

      {/* TAB 3: BIẾN ĐỘNG NGÂN HÀNG SEPAY */}
      {activeTab === 'sepay' && !isResidentView && (
        <Card
          title="Nhật ký biến động số dư ngân hàng qua SePay"
          action={
            <Button size="sm" variant="secondary" onClick={() => handleOpenMockModal(null)}>
              + Tạo giao dịch test (Mock)
            </Button>
          }
        >
          {loading ? (
            <div className="p-10 text-center text-sm text-mute">Đang tải biến động ngân hàng...</div>
          ) : filteredBankTransactions.length === 0 ? (
            <EmptyState
              title="Chưa nhận được biến động nào từ SePay"
              text="Khi có người chuyển khoản vào số tài khoản chung cư (202417066), SePay sẽ gửi webhook tự động tới đây."
            />
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full text-left text-sm">
                <thead className="bg-canvas text-xs uppercase text-mute">
                  <tr>
                    <th className="px-4 py-3">Mã GD SePay</th>
                    <th className="px-4 py-3">Số tiền</th>
                    <th className="px-4 py-3">Hướng</th>
                    <th className="px-4 py-3">Nội dung chuyển khoản</th>
                    <th className="px-4 py-3">Thời gian</th>
                    <th className="px-4 py-3">Trạng thái đối soát</th>
                    <th className="px-4 py-3">Ghi chú</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredBankTransactions.map((tx) => (
                    <tr key={tx.id} className="border-t border-line hover:bg-canvas/50 transition">
                      <td className="px-4 py-3 font-semibold text-primary">{tx.externalTransactionId}</td>
                      <td className="px-4 py-3 font-bold text-ink">{formatCurrency(tx.amount)}</td>
                      <td className="px-4 py-3 font-semibold text-ok">TIỀN VÀO</td>
                      <td className="px-4 py-3 max-w-xs truncate" title={tx.content}>
                        {tx.content}
                      </td>
                      <td className="px-4 py-3 text-mute text-xs">{formatDate(tx.transactionDate)}</td>
                      <td className="px-4 py-3">
                        <Badge
                          value={
                            tx.processingStatus === 'MATCHED'
                              ? 'Đã khớp'
                              : tx.processingStatus === 'RECEIVED'
                              ? 'Mới nhận'
                              : tx.processingStatus === 'INVALID'
                              ? 'Không hợp lệ'
                              : 'Chưa khớp'
                          }
                        />
                      </td>
                      <td className="px-4 py-3 text-xs text-mute max-w-xs truncate" title={tx.notes}>
                        {tx.notes || '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      )}

      {/* MODAL MÃ QR VIETQR & BỘ TEST SEPAY WEBHOOK SONG SONG (CHẶNG 2) */}
      <Modal
        open={qrModalOpen}
        title="Thanh toán VietQR & Giả lập SePay Webhook đối soát"
        onClose={() => setQrModalOpen(false)}
        maxWidth="max-w-5xl"
        footer={
          <div className="flex justify-between w-full items-center">
            <span className="text-xs text-mute hidden sm:inline">
              Mã tham chiếu: <b>{qrData?.paymentReference || '...'}</b> | STK: <b>{qrData?.accountNumber || '202417066'}</b>
            </span>
            <div className="flex gap-2">
              <Button variant="secondary" onClick={handleCheckQrStatus}>
                Kiểm tra trạng thái
              </Button>
              <Button variant="primary" onClick={() => setQrModalOpen(false)}>
                Đóng
              </Button>
            </div>
          </div>
        }
      >
        {qrLoading ? (
          <div className="py-16 text-center text-sm text-mute">Đang sinh mã VietQR chuẩn và chuẩn bị bộ thử nghiệm...</div>
        ) : qrError ? (
          <Alert tone="bad" title="Lỗi">{qrError}</Alert>
        ) : qrData ? (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* CỘT TRÁI: MÃ QR VÀ THÔNG TIN CHUYỂN KHOẢN */}
            <div className="space-y-4 text-center border-b lg:border-b-0 lg:border-r border-line pb-6 lg:pb-0 lg:pr-6">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold uppercase tracking-wider text-mute">1. Mã VietQR (Phía Cư dân)</span>
                <Badge
                  value={
                    qrData.status === 'MATCHED'
                      ? 'Đã nộp thành công'
                      : qrData.status === 'PENDING'
                      ? 'Chờ thanh toán'
                      : qrData.status
                  }
                />
              </div>

              {qrData.status === 'MATCHED' ? (
                <div className="p-8 bg-ok-soft/30 rounded-ui border-2 border-ok text-center space-y-3">
                  <div className="text-4xl">✓</div>
                  <h3 className="text-lg font-bold text-ok">THANH TOÁN THÀNH CÔNG!</h3>
                  <p className="text-xs text-mute">
                    Hệ thống SePay đã nhận tiền và tự động gạch nợ thành công cho khoản phí này.
                  </p>
                </div>
              ) : (
                <div className="inline-block p-2 bg-white rounded-ui shadow-md border border-line">
                  <img
                    src={qrData.qrCodeUrl}
                    alt="Mã QR VietQR"
                    className="w-52 h-52 mx-auto object-contain"
                  />
                </div>
              )}

              <div className="rounded-ui bg-canvas p-3 text-left text-xs space-y-2">
                <div className="flex justify-between">
                  <span className="text-mute">Ngân hàng:</span>
                  <b className="text-ink">{qrData.bankId}</b>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-mute">Số tài khoản:</span>
                  <span className="flex items-center gap-1 font-bold text-ink">
                    {qrData.accountNumber}
                    <button
                      type="button"
                      onClick={() => copyToClipboard(qrData.accountNumber, 'Số tài khoản')}
                      className="text-primary text-[11px] hover:underline"
                    >
                      Sao chép
                    </button>
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-mute">Chủ tài khoản:</span>
                  <span className="font-semibold">{qrData.accountName}</span>
                </div>
                <div className="flex justify-between border-t border-line pt-1.5">
                  <span className="text-mute">Số tiền:</span>
                  <b className="text-sm text-primary">{formatCurrency(qrData.amount)}</b>
                </div>
                <div className="flex justify-between items-center bg-primary/10 p-1.5 rounded-ui border border-primary/20">
                  <span className="text-primary font-bold text-[11px]">Nội dung CK:</span>
                  <span className="flex items-center gap-1 font-extrabold text-ink">
                    {qrData.paymentReference}
                    <button
                      type="button"
                      onClick={() => copyToClipboard(qrData.paymentReference, 'Nội dung chuyển khoản')}
                      className="text-primary text-[11px] hover:underline"
                    >
                      Sao chép
                    </button>
                  </span>
                </div>
              </div>
            </div>

            {/* CỘT PHẢI: BỘ ĐIỀU KHIỂN GIẢ LẬP SEPAY WEBHOOK (ĐỐI SOÁT TỨC THÌ) */}
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold uppercase tracking-wider text-primary">2. Bảng test SePay Webhook</span>
                <span className="text-[11px] text-mute">Giả lập ngân hàng bắn biến động</span>
              </div>

              <div className="rounded-ui bg-primary/5 border border-primary/20 p-3 text-xs space-y-1.5">
                <b className="text-primary block">Cách SePay và BlueMoon hoạt động:</b>
                <p className="text-mute">
                  Khi khách chuyển khoản vào STK <b>{qrData.accountNumber}</b>, SePay bắt biến động và bắn HTTP POST Webhook chứa nội dung CK về backend.
                  Backend kiểm tra mã tham chiếu <code>{qrData.paymentReference}</code>, so khớp đúng số tiền và tự động gạch nợ.
                </p>
              </div>

              <div className="space-y-3 text-xs">
                <div>
                  <span className="font-semibold block mb-1">Mã GD Ngân hàng giả lập:</span>
                  <input
                    value={simTxId}
                    onChange={(e) => setSimTxId(e.target.value)}
                    className={`${inputClass} font-mono`}
                    placeholder="Mã GD SePay..."
                  />
                </div>
                <div>
                  <span className="font-semibold block mb-1">Số tiền chuyển khoản (VND):</span>
                  <input
                    type="number"
                    value={simAmount}
                    onChange={(e) => setSimAmount(e.target.value)}
                    className={`${inputClass} font-mono`}
                  />
                </div>
                <div>
                  <span className="font-semibold block mb-1">Nội dung chuyển khoản mô phỏng:</span>
                  <textarea
                    value={simContent}
                    onChange={(e) => setSimContent(e.target.value)}
                    rows={2}
                    className="w-full rounded-ui border border-line bg-surface p-2 text-xs font-mono outline-none focus:border-primary focus:ring-1 focus:ring-primary/20"
                  />
                </div>
              </div>

              {/* Nút gửi Webhook chính xác */}
              <div className="space-y-2 pt-1">
                <Button
                  className="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2.5"
                  onClick={() => handleSimulateWebhook()}
                  disabled={simSubmitting || qrData.status === 'MATCHED'}
                >
                  {simSubmitting
                    ? 'Đang gửi SePay Webhook...'
                    : qrData.status === 'MATCHED'
                    ? '✓ Đã thanh toán và gạch nợ hoàn tất'
                    : '⚡ Bấm để SePay gửi Webhook & Gạch nợ ngay'}
                </Button>

                {/* Các nút kịch bản ngoại lệ để test */}
                {qrData.status !== 'MATCHED' && (
                  <div className="flex gap-2">
                    <button
                      type="button"
                      onClick={() => handleSimulateWebhook(null, parseFloat(simAmount) - 10000)}
                      disabled={simSubmitting}
                      className="flex-1 py-1 px-2 border border-line text-mute hover:bg-canvas rounded text-[11px]"
                      title="Chuyển thiếu 10,000đ để kiểm tra SePay bắt lỗi INVALID"
                    >
                      ❌ Test sai số tiền (-10k)
                    </button>
                    <button
                      type="button"
                      onClick={() => handleSimulateWebhook('Chuyen tien khong ghi ma', null)}
                      disabled={simSubmitting}
                      className="flex-1 py-1 px-2 border border-line text-mute hover:bg-canvas rounded text-[11px]"
                      title="Không ghi mã BM... để kiểm tra SePay bắt lỗi UNMATCHED"
                    >
                      ❌ Test sai nội dung
                    </button>
                  </div>
                )}
              </div>

              {/* Log kết quả phản hồi của SePay Webhook */}
              {simLog && (
                <div
                  className={`p-3 rounded-ui text-xs border ${
                    simLog.status === 'MATCHED'
                      ? 'bg-ok-soft/30 border-ok text-ink'
                      : 'bg-warn-soft/30 border-warn text-ink'
                  }`}
                >
                  <div className="flex justify-between items-center mb-1">
                    <b>Kết quả đối soát SePay:</b>
                    <Badge
                      value={
                        simLog.status === 'MATCHED'
                          ? 'Đã khớp'
                          : simLog.status === 'INVALID'
                          ? 'Không hợp lệ'
                          : 'Chưa khớp'
                      }
                    />
                  </div>
                  <p className="text-mute">
                    Mã GD: <code className="font-mono">{simLog.txId}</code> - {simLog.notes}
                  </p>
                </div>
              )}
            </div>
          </div>
        ) : null}
      </Modal>

      {/* MODAL GIẢ LẬP MOCK WEBHOOK ĐỘC LẬP */}
      <Modal
        open={mockModalOpen}
        title="Giả lập biến động số dư SePay (Mock Webhook)"
        onClose={() => !submitting && setMockModalOpen(false)}
      >
        <form onSubmit={handleSendMockWebhook} className="space-y-4">
          {mockError && <Alert tone="bad" title="Lỗi">{mockError}</Alert>}

          <Field label="Mã giao dịch ngân hàng (ID)">
            <input
              value={mockTxId}
              onChange={(e) => setMockTxId(e.target.value)}
              className={inputClass}
              placeholder="Ví dụ: 981245"
              required
            />
          </Field>

          <Field label="Số tiền chuyển (VND)">
            <input
              type="number"
              value={mockAmount}
              onChange={(e) => setMockAmount(e.target.value)}
              className={inputClass}
              placeholder="Ví dụ: 493500"
              required
            />
          </Field>

          <Field label="Nội dung chuyển khoản (Content)">
            <textarea
              value={mockContent}
              onChange={(e) => setMockContent(e.target.value)}
              rows={3}
              className="w-full rounded-ui border border-line bg-surface p-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 font-mono"
              placeholder="Chứa mã tham chiếu dạng: BM1_XXXX"
              required
            />
          </Field>

          <div className="rounded-ui bg-soft p-3 text-xs text-mute">
            <b>Mẹo kiểm thử:</b> Nếu nội dung CK chứa mã tham chiếu (ví dụ <code>BM1_XXXX</code>) của một yêu cầu thanh toán đang PENDING với đúng số tiền, hệ thống sẽ tự động gạch nợ và đổi trạng thái nghĩa vụ sang <b>Đã nộp (PAID)</b> ngay lập tức!
          </div>

          <div className="flex justify-end gap-2 pt-3 border-t border-line">
            <Button type="button" variant="secondary" onClick={() => setMockModalOpen(false)}>
              Hủy
            </Button>
            <Button type="submit" variant="primary" disabled={submitting}>
              {submitting ? 'Đang gửi webhook...' : 'Gửi Webhook'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* DRAWER GHI NHẬN NỘP TIỀN THỦ CÔNG (CHẶNG 1) */}
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
                  placeholder="Nhập tên người nộp, số điện thoại hoặc ghi chú khác..."
                  disabled={submitting}
                />
              </Field>
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
                {submitting ? 'Đang lưu...' : 'Xác nhận đã thu đủ'}
              </Button>
            </div>
          </form>
        )}
      </Drawer>

      {/* DRAWER CHI TIẾT PHIẾU THU */}
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
