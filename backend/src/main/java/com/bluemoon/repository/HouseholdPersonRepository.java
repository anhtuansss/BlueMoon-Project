package com.bluemoon.repository;

import com.bluemoon.entity.HouseholdPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseholdPersonRepository extends JpaRepository<HouseholdPerson, Long> {
    List<HouseholdPerson> findByHousehold_HouseholdId(Long householdId);
    List<HouseholdPerson> findByPerson_PersonId(Long personId);
}
