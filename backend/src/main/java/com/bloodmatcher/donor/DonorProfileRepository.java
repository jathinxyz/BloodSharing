package com.bloodmatcher.donor;

import com.bloodmatcher.common.BloodGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonorProfileRepository extends JpaRepository<DonorProfile, Long> {

    Optional<DonorProfile> findByUserId(Long userId);

    @Query("SELECT dp FROM DonorProfile dp JOIN dp.user u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<DonorProfile> findByUserEmailIgnoreCase(@Param("email") String email);

    boolean existsByUserId(Long userId);

    @Query("SELECT dp FROM DonorProfile dp JOIN FETCH dp.user u " +
           "WHERE dp.bloodGroup IN :bloodGroups " +
           "AND dp.available = true " +
           "AND u.enabled = true")
    List<DonorProfile> findCandidateDonors(@Param("bloodGroups") Collection<BloodGroup> bloodGroups);

    Page<DonorProfile> findByBloodGroup(BloodGroup bloodGroup, Pageable pageable);

    Page<DonorProfile> findByAvailable(boolean available, Pageable pageable);

    long countByAvailable(boolean available);

    long countByBloodGroup(BloodGroup bloodGroup);
}
