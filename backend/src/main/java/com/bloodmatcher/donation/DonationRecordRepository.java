package com.bloodmatcher.donation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRecordRepository extends JpaRepository<DonationRecord, Long> {

    @Query("SELECT d FROM DonationRecord d LEFT JOIN FETCH d.request WHERE d.donor.id = :donorId ORDER BY d.donationDate DESC")
    Page<DonationRecord> findByDonorId(@Param("donorId") Long donorId, Pageable pageable);

    List<DonationRecord> findByRequestId(Long requestId);

    long countByStatus(String status);
}
