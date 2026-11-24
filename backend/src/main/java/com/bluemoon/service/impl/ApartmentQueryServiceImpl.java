package com.bluemoon.service.impl;

import com.bluemoon.dto.response.BillableApartmentSnapshot;
import com.bluemoon.dto.response.ResidentApartmentScope;
import com.bluemoon.entity.Account;
import com.bluemoon.entity.Apartment;
import com.bluemoon.entity.HouseholdPerson;
import com.bluemoon.exception.AppException;
import com.bluemoon.repository.AccountRepository;
import com.bluemoon.repository.ApartmentRepository;
import com.bluemoon.repository.HouseholdApartmentRepository;
import com.bluemoon.repository.HouseholdPersonRepository;
import com.bluemoon.service.ApartmentQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentQueryServiceImpl implements ApartmentQueryService {

    private final ApartmentRepository apartmentRepository;
    private final AccountRepository accountRepository;
    private final HouseholdPersonRepository householdPersonRepository;
    private final HouseholdApartmentRepository householdApartmentRepository;

    public ApartmentQueryServiceImpl(ApartmentRepository apartmentRepository,
                                     AccountRepository accountRepository,
                                     HouseholdPersonRepository householdPersonRepository,
                                     HouseholdApartmentRepository householdApartmentRepository) {
        this.apartmentRepository = apartmentRepository;
        this.accountRepository = accountRepository;
        this.householdPersonRepository = householdPersonRepository;
        this.householdApartmentRepository = householdApartmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillableApartmentSnapshot> findAllActiveApartments() {
        // Trả về tất cả căn hộ đang hoạt động (kể cả căn hộ trống) cho F05 lập campaign
        return apartmentRepository.findByIsActiveTrue().stream()
                .map(apt -> new BillableApartmentSnapshot(
                        String.valueOf(apt.getApartmentId()),
                        apt.getApartmentNumber(),
                        apt.getArea(),
                        apt.getIsActive()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BillableApartmentSnapshot getActiveApartment(String apartmentId) {
        Long id = Long.valueOf(apartmentId);
        Apartment apt = apartmentRepository.findById(id)
                .orElseThrow(() -> new AppException("APARTMENT_NOT_FOUND", "Không tìm thấy căn hộ", HttpStatus.NOT_FOUND));

        if (Boolean.FALSE.equals(apt.getIsActive())) {
            throw new AppException("APARTMENT_NOT_ACTIVE", "Căn hộ không ở trạng thái hoạt động", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new BillableApartmentSnapshot(
                String.valueOf(apt.getApartmentId()),
                apt.getApartmentNumber(),
                apt.getArea(),
                apt.getIsActive()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResidentApartmentScope getCurrentApartmentForAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException("ACCOUNT_NOT_FOUND", "Không tìm thấy tài khoản", HttpStatus.NOT_FOUND));

        if (account.getPerson() == null) {
            return new ResidentApartmentScope(accountId, null, null);
        }

        Long personId = account.getPerson().getPersonId();
        // Tìm hộ gia đình đang ở
        List<HouseholdPerson> list = householdPersonRepository.findByPerson_PersonId(personId);
        Long apartmentId = null;

        for (HouseholdPerson hp : list) {
            if (hp.getEndDate() == null) { // Đang ở
                var haOpt = householdApartmentRepository.findActiveByHouseholdId(hp.getHousehold().getHouseholdId());
                if (haOpt.isPresent()) {
                    apartmentId = haOpt.get().getApartment().getApartmentId();
                    break;
                }
            }
        }

        return new ResidentApartmentScope(accountId, personId, apartmentId);
    }
}
