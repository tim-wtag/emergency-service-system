// package com.emergency;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertInstanceOf;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.junit.jupiter.api.Assertions.fail;

// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;

// import com.emergency.exception.EmptyAlertException;
// import com.emergency.model.CoastGuardIncident;
// import com.emergency.model.EmergencyIncident;
// import com.emergency.model.FireIncident;
// import com.emergency.model.IncidentType;
// import com.emergency.model.MedicalIncident;
// import com.emergency.model.PoliceIncident;
// import com.emergency.model.UnknownIncident;
// import com.emergency.service.TriageService;

// public class TriageServiceTest {
//     private static TriageService triage;

//     @BeforeAll
//     public static void setTriage() {
//         triage = new TriageService();
//     }

//     @Test
//     public void shouldThrowExceptionWhenInputIsNull() {
//         assertThrows(EmptyAlertException.class, () -> triage.parse(null));
//     }

//     @Test
//     public void shouldThrowExceptionWhenInputIsEmpty() {
//         assertThrows(EmptyAlertException.class, () -> triage.parse(""));
//     }

//      @Test
//     public void shouldThrowExceptionForWhitespaceInput() {
//         assertThrows(EmptyAlertException.class, () -> triage.parse("  "));
//     }

//     @Test
//     public void shouldReturnFireIncidentForFireInput() {
//         assertInstanceOf(FireIncident.class, triage.parse("fire"));
//     }

//     @Test
//     public void shouldReturnMedicalIncidentForBleedInput() {
//         assertInstanceOf(MedicalIncident.class, triage.parse("bleed"));
//     }

//     @Test
//     public void shouldReturnMedicalIncidentForAmbulanceInput() {
//         assertInstanceOf(MedicalIncident.class, triage.parse("ambulance"));
//     }

//     @Test
//     public void shouldReturnMedicalIncidentForDoctorInput() {
//         assertInstanceOf(MedicalIncident.class, triage.parse("doctor"));
//     }

//     @Test
//     public void shouldReturnPoliceIncidentForPoliceInput() {
//         assertInstanceOf(PoliceIncident.class, triage.parse("theft"));
//     }

//     @Test
//     public void shouldReturnPoliceIncidentForCrimeInput() {
//         assertInstanceOf(PoliceIncident.class, triage.parse("crime"));
//     }

//     @Test
//     public void shouldReturnCoatGuardIncidentForCoastGuardInput() {
//         assertInstanceOf(CoastGuardIncident.class, triage.parse("drowning"));
//     }

//     @Test
//     public void shouldReturnUnknownIncidentForUnknownInput() {
//         assertInstanceOf(UnknownIncident.class, triage.parse("hello"));
//     }

//     @Test
//     public void shouldStoreCorrectDescription(){
//         EmergencyIncident result = triage.parse("fire");
//         assertEquals("fire", result.getDescription());
//     }

//     @Test
//     public void shouldSetTheCorrectTypeForFire(){
//         EmergencyIncident result = triage.parse("fire");
//         assertEquals(IncidentType.FIRE, result.getType());
//     }

//     @Test
//     public void shouldSetTheCorrectTypeForBleed(){
//         EmergencyIncident result = triage.parse("bleed");
//         assertEquals(IncidentType.MEDICAL, result.getType());
//     }

//     @Test
//     public void shouldSetTheCorrectTypeForTheft(){
//         EmergencyIncident result = triage.parse("theft");
//         assertEquals(IncidentType.POLICE, result.getType());
//     }

//     @Test
//     public void shouldSetTheCorrectTypeForDrowning(){
//         EmergencyIncident result = triage.parse("drowning");
//         assertEquals(IncidentType.COASTAL, result.getType());
//     }

//     @Test
//     public void shouldSetTheCorrectTypeForAnUnknownInput(){
//         EmergencyIncident result = triage.parse("anything");
//         assertEquals(IncidentType.UNKNOWN, result.getType());
//     }

//     @Test
//     public void shouldReturnFireIncidentWhenFireIsPartOfDescription(){
//         EmergencyIncident result = triage.parse("There is fire in the building");
//         assertInstanceOf(FireIncident.class, result);
//     }

//      @Test
//     public void shouldReturnMedicalIncidentWhenBleedIsPartOfDescription(){
//         EmergencyIncident result = triage.parse("bleed a lot");
//         assertInstanceOf(MedicalIncident.class, result);
//     }
// }
