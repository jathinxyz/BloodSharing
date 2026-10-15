package com.bloodmatcher.matching;

import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.Constants;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Deterministic service implementing standard red-blood-cell preliminary compatibility rules.
 * <p>
 * IMPORTANT NOTICE:
 * This matrix is solely a preliminary software screening filter for RBC donor coordination.
 * It is NOT medical clearance and cannot guarantee transfusion safety. Formal clinical testing,
 * antibody screening, and cross-matching by certified blood-bank specialists remain strictly mandatory.
 */
@Service
public class BloodCompatibilityService {

    private final Map<BloodGroup, Set<BloodGroup>> recipientToDonorMatrix = new EnumMap<>(BloodGroup.class);

    public BloodCompatibilityService() {
        initializeMatrix();
    }

    private void initializeMatrix() {
        // O- recipient can receive from O-
        recipientToDonorMatrix.put(BloodGroup.O_NEG, EnumSet.of(BloodGroup.O_NEG));

        // O+ recipient can receive from O-, O+
        recipientToDonorMatrix.put(BloodGroup.O_POS, EnumSet.of(BloodGroup.O_NEG, BloodGroup.O_POS));

        // A- recipient can receive from O-, A-
        recipientToDonorMatrix.put(BloodGroup.A_NEG, EnumSet.of(BloodGroup.O_NEG, BloodGroup.A_NEG));

        // A+ recipient can receive from O-, O+, A-, A+
        recipientToDonorMatrix.put(BloodGroup.A_POS, EnumSet.of(BloodGroup.O_NEG, BloodGroup.O_POS, BloodGroup.A_NEG, BloodGroup.A_POS));

        // B- recipient can receive from O-, B-
        recipientToDonorMatrix.put(BloodGroup.B_NEG, EnumSet.of(BloodGroup.O_NEG, BloodGroup.B_NEG));

        // B+ recipient can receive from O-, O+, B-, B+
        recipientToDonorMatrix.put(BloodGroup.B_POS, EnumSet.of(BloodGroup.O_NEG, BloodGroup.O_POS, BloodGroup.B_NEG, BloodGroup.B_POS));

        // AB- recipient can receive from O-, A-, B-, AB-
        recipientToDonorMatrix.put(BloodGroup.AB_NEG, EnumSet.of(BloodGroup.O_NEG, BloodGroup.A_NEG, BloodGroup.B_NEG, BloodGroup.AB_NEG));

        // AB+ recipient can receive from all groups (universal recipient for RBCs)
        recipientToDonorMatrix.put(BloodGroup.AB_POS, EnumSet.allOf(BloodGroup.class));
    }

    /**
     * Returns the set of compatible donor blood groups for a given recipient blood group.
     */
    public Set<BloodGroup> getCompatibleDonorsForRecipient(BloodGroup recipientGroup) {
        if (recipientGroup == null) {
            throw new IllegalArgumentException("Recipient blood group cannot be null");
        }
        return Collections.unmodifiableSet(recipientToDonorMatrix.getOrDefault(recipientGroup, Collections.emptySet()));
    }

    /**
     * Checks if a donor's blood group is preliminary-compatible with a recipient's blood group.
     */
    public boolean isCompatible(BloodGroup donorGroup, BloodGroup recipientGroup) {
        if (donorGroup == null || recipientGroup == null) {
            throw new IllegalArgumentException("Donor and recipient blood groups must not be null");
        }
        Set<BloodGroup> compatibleDonors = recipientToDonorMatrix.get(recipientGroup);
        return compatibleDonors != null && compatibleDonors.contains(donorGroup);
    }

    /**
     * Produces an explainable description of compatibility.
     */
    public String getCompatibilityExplanation(BloodGroup donorGroup, BloodGroup recipientGroup) {
        if (isCompatible(donorGroup, recipientGroup)) {
            return String.format("Donor %s is compatible with recipient %s for preliminary red-blood-cell screening.",
                    donorGroup.getDisplayValue(), recipientGroup.getDisplayValue());
        } else {
            return String.format("Donor %s is NOT compatible with recipient %s.",
                    donorGroup.getDisplayValue(), recipientGroup.getDisplayValue());
        }
    }

    public String getMedicalDisclaimer() {
        return Constants.HEALTH_DISCLAIMER;
    }
}
