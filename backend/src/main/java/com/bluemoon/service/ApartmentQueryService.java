package com.bluemoon.service;

import com.bluemoon.dto.response.BillableApartmentSnapshot;
import com.bluemoon.dto.response.ResidentApartmentScope;

import java.util.List;

/**
 * Service nội bộ do Module F03 (Thành) cung cấp cho F05, F06, F07
 * Tuyệt đối không query chéo bảng cư trú; các module khác gọi interface này.
 */
public interface ApartmentQueryService {
    List<BillableApartmentSnapshot> findAllActiveApartments();
    BillableApartmentSnapshot getActiveApartment(String apartmentId);
    ResidentApartmentScope getCurrentApartmentForAccount(Long accountId);
}
