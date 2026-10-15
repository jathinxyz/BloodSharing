package com.bloodmatcher.matching;

import com.bloodmatcher.common.BloodGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BloodCompatibilityServiceTest {

    private BloodCompatibilityService compatibilityService;

    @BeforeEach
    void setUp() {
        compatibilityService = new BloodCompatibilityService();
    }

    @Test
    @DisplayName("O- recipient can only receive from O-")
    void testONegativeRecipient() {
        Set<BloodGroup> compatible = compatibilityService.getCompatibleDonorsForRecipient(BloodGroup.O_NEG);
        assertEquals(Set.of(BloodGroup.O_NEG), compatible);
        assertTrue(compatibilityService.isCompatible(BloodGroup.O_NEG, BloodGroup.O_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.O_POS, BloodGroup.O_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.A_NEG, BloodGroup.O_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.A_POS, BloodGroup.O_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.B_NEG, BloodGroup.O_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.B_POS, BloodGroup.O_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.AB_NEG, BloodGroup.O_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.AB_POS, BloodGroup.O_NEG));
    }

    @Test
    @DisplayName("O+ recipient can receive from O- and O+")
    void testOPositiveRecipient() {
        Set<BloodGroup> compatible = compatibilityService.getCompatibleDonorsForRecipient(BloodGroup.O_POS);
        assertEquals(Set.of(BloodGroup.O_NEG, BloodGroup.O_POS), compatible);
        assertTrue(compatibilityService.isCompatible(BloodGroup.O_NEG, BloodGroup.O_POS));
        assertTrue(compatibilityService.isCompatible(BloodGroup.O_POS, BloodGroup.O_POS));
        assertFalse(compatibilityService.isCompatible(BloodGroup.A_POS, BloodGroup.O_POS));
        assertFalse(compatibilityService.isCompatible(BloodGroup.B_POS, BloodGroup.O_POS));
    }

    @Test
    @DisplayName("A- recipient can receive from O- and A-")
    void testANegativeRecipient() {
        Set<BloodGroup> compatible = compatibilityService.getCompatibleDonorsForRecipient(BloodGroup.A_NEG);
        assertEquals(Set.of(BloodGroup.O_NEG, BloodGroup.A_NEG), compatible);
        assertTrue(compatibilityService.isCompatible(BloodGroup.O_NEG, BloodGroup.A_NEG));
        assertTrue(compatibilityService.isCompatible(BloodGroup.A_NEG, BloodGroup.A_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.A_POS, BloodGroup.A_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.B_NEG, BloodGroup.A_NEG));
    }

    @Test
    @DisplayName("A+ recipient can receive from O-, O+, A-, A+")
    void testAPositiveRecipient() {
        Set<BloodGroup> compatible = compatibilityService.getCompatibleDonorsForRecipient(BloodGroup.A_POS);
        assertEquals(Set.of(BloodGroup.O_NEG, BloodGroup.O_POS, BloodGroup.A_NEG, BloodGroup.A_POS), compatible);
        assertTrue(compatibilityService.isCompatible(BloodGroup.O_NEG, BloodGroup.A_POS));
        assertTrue(compatibilityService.isCompatible(BloodGroup.O_POS, BloodGroup.A_POS));
        assertTrue(compatibilityService.isCompatible(BloodGroup.A_NEG, BloodGroup.A_POS));
        assertTrue(compatibilityService.isCompatible(BloodGroup.A_POS, BloodGroup.A_POS));
        assertFalse(compatibilityService.isCompatible(BloodGroup.B_NEG, BloodGroup.A_POS));
        assertFalse(compatibilityService.isCompatible(BloodGroup.AB_POS, BloodGroup.A_POS));
    }

    @Test
    @DisplayName("B- recipient can receive from O- and B-")
    void testBNegativeRecipient() {
        Set<BloodGroup> compatible = compatibilityService.getCompatibleDonorsForRecipient(BloodGroup.B_NEG);
        assertEquals(Set.of(BloodGroup.O_NEG, BloodGroup.B_NEG), compatible);
        assertTrue(compatibilityService.isCompatible(BloodGroup.O_NEG, BloodGroup.B_NEG));
        assertTrue(compatibilityService.isCompatible(BloodGroup.B_NEG, BloodGroup.B_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.B_POS, BloodGroup.B_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.A_NEG, BloodGroup.B_NEG));
    }

    @Test
    @DisplayName("B+ recipient can receive from O-, O+, B-, B+")
    void testBPositiveRecipient() {
        Set<BloodGroup> compatible = compatibilityService.getCompatibleDonorsForRecipient(BloodGroup.B_POS);
        assertEquals(Set.of(BloodGroup.O_NEG, BloodGroup.O_POS, BloodGroup.B_NEG, BloodGroup.B_POS), compatible);
        assertTrue(compatibilityService.isCompatible(BloodGroup.O_NEG, BloodGroup.B_POS));
        assertTrue(compatibilityService.isCompatible(BloodGroup.O_POS, BloodGroup.B_POS));
        assertTrue(compatibilityService.isCompatible(BloodGroup.B_NEG, BloodGroup.B_POS));
        assertTrue(compatibilityService.isCompatible(BloodGroup.B_POS, BloodGroup.B_POS));
        assertFalse(compatibilityService.isCompatible(BloodGroup.A_POS, BloodGroup.B_POS));
        assertFalse(compatibilityService.isCompatible(BloodGroup.AB_POS, BloodGroup.B_POS));
    }

    @Test
    @DisplayName("AB- recipient can receive from O-, A-, B-, AB-")
    void testABNegativeRecipient() {
        Set<BloodGroup> compatible = compatibilityService.getCompatibleDonorsForRecipient(BloodGroup.AB_NEG);
        assertEquals(Set.of(BloodGroup.O_NEG, BloodGroup.A_NEG, BloodGroup.B_NEG, BloodGroup.AB_NEG), compatible);
        assertTrue(compatibilityService.isCompatible(BloodGroup.O_NEG, BloodGroup.AB_NEG));
        assertTrue(compatibilityService.isCompatible(BloodGroup.A_NEG, BloodGroup.AB_NEG));
        assertTrue(compatibilityService.isCompatible(BloodGroup.B_NEG, BloodGroup.AB_NEG));
        assertTrue(compatibilityService.isCompatible(BloodGroup.AB_NEG, BloodGroup.AB_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.O_POS, BloodGroup.AB_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.A_POS, BloodGroup.AB_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.B_POS, BloodGroup.AB_NEG));
        assertFalse(compatibilityService.isCompatible(BloodGroup.AB_POS, BloodGroup.AB_NEG));
    }

    @Test
    @DisplayName("AB+ recipient can receive from all groups (universal RBC recipient)")
    void testABPositiveRecipient() {
        Set<BloodGroup> compatible = compatibilityService.getCompatibleDonorsForRecipient(BloodGroup.AB_POS);
        assertEquals(8, compatible.size());
        for (BloodGroup bg : BloodGroup.values()) {
            assertTrue(compatibilityService.isCompatible(bg, BloodGroup.AB_POS));
        }
    }

    @Test
    @DisplayName("Null blood groups must throw IllegalArgumentException")
    void testNullBloodGroupThrows() {
        assertThrows(IllegalArgumentException.class, () -> compatibilityService.getCompatibleDonorsForRecipient(null));
        assertThrows(IllegalArgumentException.class, () -> compatibilityService.isCompatible(null, BloodGroup.O_POS));
        assertThrows(IllegalArgumentException.class, () -> compatibilityService.isCompatible(BloodGroup.O_POS, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "C+", "", "   ", "xyz"})
    @DisplayName("Invalid blood group strings fail parsing")
    void testInvalidBloodGroupParsing(String invalid) {
        assertThrows(IllegalArgumentException.class, () -> BloodGroup.fromString(invalid));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A+", "a+", "A_POS", "A-", "O+", "O-", "B+", "b-", "AB+", "ab-"})
    @DisplayName("Valid blood group strings parse successfully")
    void testValidBloodGroupParsing(String valid) {
        assertNotNull(BloodGroup.fromString(valid));
    }
}
