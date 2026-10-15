package com.bloodmatcher.request;

import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.RequestStatus;
import com.bloodmatcher.common.UrgencyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    Page<BloodRequest> findByRequesterId(Long requesterId, Pageable pageable);

    Page<BloodRequest> findByStatus(RequestStatus status, Pageable pageable);

    Page<BloodRequest> findByStatusIn(Collection<RequestStatus> statuses, Pageable pageable);

    Page<BloodRequest> findByBloodGroup(BloodGroup bloodGroup, Pageable pageable);

    Page<BloodRequest> findByUrgency(UrgencyLevel urgency, Pageable pageable);

    @Query("SELECT r FROM BloodRequest r JOIN FETCH r.requester WHERE r.id = :id")
    Optional<BloodRequest> findByIdWithRequester(@Param("id") Long id);

    long countByStatus(RequestStatus status);

    long countByBloodGroup(BloodGroup bloodGroup);
}
