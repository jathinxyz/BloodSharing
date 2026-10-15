package com.bloodmatcher.invitation;

import com.bloodmatcher.common.InvitationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorInvitationRepository extends JpaRepository<DonorInvitation, Long> {

    boolean existsByRequestIdAndDonorId(Long requestId, Long donorId);

    List<DonorInvitation> findByRequestId(Long requestId);

    @Query("SELECT di FROM DonorInvitation di " +
           "JOIN FETCH di.request r " +
           "JOIN FETCH di.donor d " +
           "WHERE di.id = :id")
    Optional<DonorInvitation> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT di FROM DonorInvitation di " +
           "JOIN FETCH di.request r " +
           "WHERE di.donor.id = :donorId")
    Page<DonorInvitation> findByDonorId(@Param("donorId") Long donorId, Pageable pageable);

    @Query("SELECT di FROM DonorInvitation di " +
           "JOIN FETCH di.request r " +
           "WHERE di.donor.id = :donorId AND di.status = :status")
    Page<DonorInvitation> findByDonorIdAndStatus(@Param("donorId") Long donorId,
                                                @Param("status") InvitationStatus status,
                                                Pageable pageable);

    long countByStatus(InvitationStatus status);

    long countByRequestIdAndStatus(Long requestId, InvitationStatus status);
}
