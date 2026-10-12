package de.hemolytix.server.config;

import de.hemolytix.server.model.Address;
import de.hemolytix.server.model.CompleteBloodCount;
import de.hemolytix.server.model.Patient;
import de.hemolytix.server.model.ReferenceRange;
import de.hemolytix.server.model.User;
import de.hemolytix.server.repository.CompleteBloodCountRepository;
import de.hemolytix.server.repository.PatientRepository;
import de.hemolytix.server.repository.ReferenceRangeRepository;
import de.hemolytix.server.repository.UserRepository;
import de.hemolytix.server.types.BloodType;
import de.hemolytix.server.types.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final CompleteBloodCountRepository cbcRepository;
    private final ReferenceRangeRepository referenceRangeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PatientRepository patientRepository,
                           CompleteBloodCountRepository cbcRepository,
                           ReferenceRangeRepository referenceRangeRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.cbcRepository = cbcRepository;
        this.referenceRangeRepository = referenceRangeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initUsers();
        initReferenceRanges();
        initPatientsAndBloodCounts();
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            User doctor = new User("doctor", passwordEncoder.encode("doctor123"), "Dr. med. Sarah Koch", "DOCTOR");
            User admin = new User("admin", passwordEncoder.encode("admin123"), "System Administrator", "ADMIN");
            userRepository.saveAll(List.of(doctor, admin));
        }
    }

    private void initReferenceRanges() {
        if (referenceRangeRepository.count() == 0) {
            List<ReferenceRange> ranges = List.of(
                    // --- HÄMOGLOBIN (g/dl) ---
                    new ReferenceRange("hemoglobin", Gender.MALE, 18, 99, 13.5, 17.5, "g/dl"),
                    new ReferenceRange("hemoglobin", Gender.FEMALE, 18, 99, 12.0, 16.0, "g/dl"),

                    // --- ERYTHROZYTEN (T/l) ---
                    new ReferenceRange("erythrocytes", Gender.MALE, 18, 99, 4.3, 5.9, "T/l"),
                    new ReferenceRange("erythrocytes", Gender.FEMALE, 18, 99, 3.9, 5.2, "T/l"),

                    // --- HÄMATOKRIT (%) ---
                    new ReferenceRange("hematocrit", Gender.MALE, 18, 99, 40.0, 52.0, "%"),
                    new ReferenceRange("hematocrit", Gender.FEMALE, 18, 99, 37.0, 48.0, "%"),

                    // --- ERYTHROZYTEN-INDIZES ---
                    new ReferenceRange("mcv", Gender.UNSPECIFIED, 18, 99, 80.0, 96.0, "fl"),
                    new ReferenceRange("mch", Gender.UNSPECIFIED, 18, 99, 28.0, 33.0, "pg"),
                    new ReferenceRange("mchc", Gender.UNSPECIFIED, 18, 99, 32.0, 36.0, "g/dl"),

                    // --- LEUKOZYTEN & THROMBOZYTEN (G/l) ---
                    new ReferenceRange("leukocytes", Gender.UNSPECIFIED, 18, 99, 4.0, 10.0, "G/l"),
                    new ReferenceRange("platelets", Gender.UNSPECIFIED, 18, 99, 150.0, 400.0, "G/l"),

                    // --- DIFFERENZIALBLUTBILD (%) ---
                    new ReferenceRange("neutrophilsRel", Gender.UNSPECIFIED, 18, 99, 40.0, 75.0, "%"),
                    new ReferenceRange("lymphocytesRel", Gender.UNSPECIFIED, 18, 99, 20.0, 45.0, "%"),
                    new ReferenceRange("monocytesRel", Gender.UNSPECIFIED, 18, 99, 2.0, 12.0, "%"),
                    new ReferenceRange("eosinophilsRel", Gender.UNSPECIFIED, 18, 99, 1.0, 6.0, "%"),
                    new ReferenceRange("basophilsRel", Gender.UNSPECIFIED, 18, 99, 0.0, 2.0, "%")
            );

            referenceRangeRepository.saveAll(ranges);
        }
    }

    private void initPatientsAndBloodCounts() {
        if (patientRepository.count() == 0) {
            List<Patient> patients = createPatientList();
            Random random = new Random(42); // Fester Seed für reproduzierbare Testdaten

            for (Patient patient : patients) {
                int numberOfReports = 5 + random.nextInt(6); // 5 bis 10 Bluttests
                LocalDateTime baseDate = LocalDateTime.now().minusMonths(numberOfReports * 2);

                for (int i = 0; i < numberOfReports; i++) {
                    CompleteBloodCount cbc = createSampleCBC(patient, baseDate.plusDays(i * 45 + random.nextInt(10)), random, i);
                    patient.addCompleteBloodCount(cbc);
                }
            }

            patientRepository.saveAll(patients);
        }
    }

    private List<Patient> createPatientList() {
        List<Patient> list = new ArrayList<>();

        // Patient 1
        Patient p1 = new Patient();
        p1.setFirstName("Maximilian");
        p1.setLastName("Musterfrau"); // für die Logik in createSampleCBC (Anämie-Verlauf) beibehalten
        p1.setDateOfBirth(LocalDate.of(1980, 4, 12));
        p1.setGender(Gender.MALE);
        p1.setBloodType(BloodType.A_POSITIVE);
        p1.setHealthInsuranceCompany("Techniker Krankenkasse");
        p1.setFamilyDoctor("Dr. med. Thomas Klein");
        p1.setAddress(new Address("Kaiser-Friedrich-Straße", "42", "41063", "Mönchengladbach"));
        list.add(p1);

        // Patient 2
        Patient p2 = new Patient();
        p2.setFirstName("Erika");
        p2.setLastName("Musterfrau");
        p2.setDateOfBirth(LocalDate.of(1992, 8, 25));
        p2.setGender(Gender.FEMALE);
        p2.setBloodType(BloodType.O_NEGATIVE);
        p2.setHealthInsuranceCompany("Barmer");
        p2.setFamilyDoctor("Dr. med. Bettina Neumann");
        p2.setAddress(new Address("Graf-Adolf-Straße", "15", "40210", "Düsseldorf"));
        list.add(p2);

        // Patient 3
        Patient p3 = new Patient();
        p3.setFirstName("Andreas");
        p3.setLastName("Weber"); // für Infekt-Verlauf beibehalten
        p3.setDateOfBirth(LocalDate.of(1965, 1, 15));
        p3.setGender(Gender.MALE);
        p3.setBloodType(BloodType.B_POSITIVE);
        p3.setHealthInsuranceCompany("AOK Rheinland/Hamburg");
        p3.setFamilyDoctor("Dr. med. Michael Wagner");
        p3.setAddress(new Address("Oststraße", "88", "47798", "Krefeld"));
        list.add(p3);

        // Patient 4
        Patient p4 = new Patient();
        p4.setFirstName("Maria");
        p4.setLastName("Schmidt");
        p4.setDateOfBirth(LocalDate.of(1978, 11, 30));
        p4.setGender(Gender.FEMALE);
        p4.setBloodType(BloodType.AB_POSITIVE);
        p4.setHealthInsuranceCompany("DAK Gesundheit");
        p4.setFamilyDoctor("Dr. med. Stefan Berger");
        p4.setAddress(new Address("Neusser Straße", "104", "50670", "Köln"));
        list.add(p4);

        // Patient 5
        Patient p5 = new Patient();
        p5.setFirstName("Lukas");
        p5.setLastName("Fischer");
        p5.setDateOfBirth(LocalDate.of(2001, 3, 5));
        p5.setGender(Gender.MALE);
        p5.setBloodType(BloodType.O_NEGATIVE);
        p5.setHealthInsuranceCompany("IKK classic");
        p5.setFamilyDoctor("Dr. med. Thomas Klein");
        p5.setAddress(new Address("Hochstraße", "31", "47798", "Krefeld"));
        list.add(p5);

        // Patient 6
        Patient p6 = new Patient();
        p6.setFirstName("Sophie");
        p6.setLastName("Schneider");
        p6.setDateOfBirth(LocalDate.of(1988, 6, 18));
        p6.setGender(Gender.FEMALE);
        p6.setBloodType(BloodType.A_NEGATIVE);
        p6.setHealthInsuranceCompany("Techniker Krankenkasse");
        p6.setFamilyDoctor("Dr. med. Bettina Neumann");
        p6.setAddress(new Address("Königsallee", "52", "40212", "Düsseldorf"));
        list.add(p6);

        // Patient 7
        Patient p7 = new Patient();
        p7.setFirstName("Heinrich");
        p7.setLastName("Hoffmann");
        p7.setDateOfBirth(LocalDate.of(1954, 9, 22));
        p7.setGender(Gender.MALE);
        p7.setBloodType(BloodType.B_NEGATIVE);
        p7.setHealthInsuranceCompany("Barmer");
        p7.setFamilyDoctor("Dr. med. Michael Wagner");
        p7.setAddress(new Address("Bismarckstraße", "9", "41061", "Mönchengladbach"));
        list.add(p7);

        // Patient 8
        Patient p8 = new Patient();
        p8.setFirstName("Laura");
        p8.setLastName("Wagner");
        p8.setDateOfBirth(LocalDate.of(1995, 12, 10));
        p8.setGender(Gender.FEMALE);
        p8.setBloodType(BloodType.O_POSITIVE);
        p8.setHealthInsuranceCompany("AOK Rheinland/Hamburg");
        p8.setFamilyDoctor("Dr. med. Stefan Berger");
        p8.setAddress(new Address("Ehrenstraße", "23", "50672", "Köln"));
        list.add(p8);

        // Patient 9
        Patient p9 = new Patient();
        p9.setFirstName("Jan");
        p9.setLastName("Becker");
        p9.setDateOfBirth(LocalDate.of(1972, 7, 8));
        p9.setGender(Gender.MALE);
        p9.setBloodType(BloodType.A_POSITIVE);
        p9.setHealthInsuranceCompany("DAK Gesundheit");
        p9.setFamilyDoctor("Dr. med. Thomas Klein");
        p9.setAddress(new Address("Dionysiusstraße", "7", "47798", "Krefeld"));
        list.add(p9);

        // Patient 10
        Patient p10 = new Patient();
        p10.setFirstName("Elena");
        p10.setLastName("Richter");
        p10.setDateOfBirth(LocalDate.of(1999, 2, 14));
        p10.setGender(Gender.FEMALE);
        p10.setBloodType(BloodType.AB_NEGATIVE);
        p10.setHealthInsuranceCompany("Techniker Krankenkasse");
        p10.setFamilyDoctor("Dr. med. Bettina Neumann");
        p10.setAddress(new Address("Friedrich-Ebert-Straße", "112", "42117", "Wuppertal"));
        list.add(p10);

        return list;
    }

    private CompleteBloodCount createSampleCBC(Patient patient, LocalDateTime timestamp, Random random, int index) {
        CompleteBloodCount cbc = new CompleteBloodCount();
        cbc.setRecordedAt(timestamp);

        boolean isMale = patient.getGender() == Gender.MALE;

        double hbBase = isMale ? 15.0 : 13.5;
        double eryBase = isMale ? 5.0 : 4.5;
        double hktBase = isMale ? 45.0 : 41.0;

        if (patient.getFirstName().equals("Erika") && index < 3) {
            hbBase -= 3.0;
            eryBase -= 1.0;
            hktBase -= 8.0;
            cbc.setNotes("Verdacht auf leicht erniedrigte Eisenwerte.");
        } else if (patient.getFirstName().equals("Andreas") && index == 2) {
            cbc.setLeukocytes(14.8);
            cbc.setNotes("Akuter Infekt - Leukozytose festgestellt.");
        } else {
            cbc.setNotes("Routine-Laborbefund ohne akute Auffälligkeiten.");
        }

        cbc.setHemoglobin(round(hbBase + (random.nextDouble() * 1.2 - 0.6)));
        cbc.setErythrocytes(round(eryBase + (random.nextDouble() * 0.6 - 0.3)));
        cbc.setHematocrit(round(hktBase + (random.nextDouble() * 3.0 - 1.5)));

        cbc.setMcv(round(88.0 + (random.nextDouble() * 6.0 - 3.0)));
        cbc.setMch(round(30.0 + (random.nextDouble() * 3.0 - 1.5)));
        cbc.setMchc(round(34.0 + (random.nextDouble() * 2.0 - 1.0)));

        if (cbc.getLeukocytes() == null) {
            cbc.setLeukocytes(round(6.5 + (random.nextDouble() * 3.0 - 1.5)));
        }

        cbc.setPlatelets(round(250.0 + (random.nextDouble() * 80.0 - 40.0)));

        cbc.setNeutrophilsRel(round(58.0 + (random.nextDouble() * 8.0 - 4.0)));
        cbc.setLymphocytesRel(round(30.0 + (random.nextDouble() * 6.0 - 3.0)));
        cbc.setMonocytesRel(round(6.0 + (random.nextDouble() * 2.0 - 1.0)));
        cbc.setEosinophilsRel(round(3.0 + (random.nextDouble() * 2.0 - 1.0)));
        cbc.setBasophilsRel(round(0.8 + (random.nextDouble() * 0.4 - 0.2)));

        return cbc;
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}