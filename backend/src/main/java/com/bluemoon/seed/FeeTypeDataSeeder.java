package com.bluemoon.seed;

import com.bluemoon.entity.FeeType;
import com.bluemoon.repository.FeeTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeder dữ liệu mẫu cho Loại khoản thu (FeeType) theo yêu cầu Week 1 (Long - F05).
 */
@Component
public class FeeTypeDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(FeeTypeDataSeeder.class);
    private final FeeTypeRepository feeTypeRepository;

    public FeeTypeDataSeeder(FeeTypeRepository feeTypeRepository) {
        this.feeTypeRepository = feeTypeRepository;
    }

    @Override
    public void run(String... args) {
        if (feeTypeRepository.count() == 0) {
            log.info("Khởi tạo dữ liệu mặc định cho fee_type...");

            FeeType serviceFee = new FeeType();
            serviceFee.setCode("PHI_DICH_VU");
            serviceFee.setName("Phí dịch vụ chung cư");
            serviceFee.setCategory(FeeType.FeeCategory.MANDATORY_SERVICE);
            serviceFee.setChargingMethod(FeeType.ChargingMethod.PER_SQM);
            serviceFee.setDefaultUnitPrice(new BigDecimal("2500.00"));
            serviceFee.setIsActive(true);
            serviceFee.setDescription("Phí dịch vụ cơ bản thu theo diện tích căn hộ (2,500 đ/m2/tháng)");

            FeeType managementFee = new FeeType();
            managementFee.setCode("PHI_QUAN_LY");
            managementFee.setName("Phí quản lý chung cư");
            managementFee.setCategory(FeeType.FeeCategory.MANDATORY_MANAGEMENT);
            managementFee.setChargingMethod(FeeType.ChargingMethod.PER_SQM);
            managementFee.setDefaultUnitPrice(new BigDecimal("7000.00"));
            managementFee.setIsActive(true);
            managementFee.setDescription("Phí quản lý vận hành thu theo diện tích căn hộ (7,000 đ/m2/tháng)");

            FeeType donationFee = new FeeType();
            donationFee.setCode("QUY_TU_THIEN");
            donationFee.setName("Quỹ từ thiện / Đóng góp vì người nghèo");
            donationFee.setCategory(FeeType.FeeCategory.DONATION);
            donationFee.setChargingMethod(FeeType.ChargingMethod.VOLUNTARY);
            donationFee.setDefaultUnitPrice(null);
            donationFee.setIsActive(true);
            donationFee.setDescription("Quỹ đóng góp tự nguyện của cư dân, không áp đặt định mức bắt buộc");

            feeTypeRepository.saveAll(List.of(serviceFee, managementFee, donationFee));
            log.info("Đã seed thành công 3 loại khoản thu mặc định.");
        }
    }
}
