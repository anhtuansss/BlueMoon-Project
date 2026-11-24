package com.bluemoon.repository;

import com.bluemoon.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByIdentityNumber(String identityNumber);
    boolean existsByIdentityNumber(String identityNumber);
    Page<Person> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);
}
