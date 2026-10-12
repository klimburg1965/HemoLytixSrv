package de.hemolytix.server.model;

import de.hemolytix.server.types.BloodType;
import de.hemolytix.server.types.Gender;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    private String healthInsuranceCompany;
    private String familyDoctor;

    // OneToMany / ManyToOne Relation zur Adresse
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompleteBloodCount> completeBloodCounts = new ArrayList<>();

    // Audit-Felder (falls bei dir vorhanden, beibehalten)
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Patient() {
    }

    // --- Helper Method ---
    public void addCompleteBloodCount(CompleteBloodCount cbc) {
        completeBloodCounts.add(cbc);
        cbc.setPatient(this);
    }

    // --- Getter und Setter ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public BloodType getBloodType() { return bloodType; }
    public void setBloodType(BloodType bloodType) { this.bloodType = bloodType; }

    public String getHealthInsuranceCompany() { return healthInsuranceCompany; }
    public void setHealthInsuranceCompany(String healthInsuranceCompany) { this.healthInsuranceCompany = healthInsuranceCompany; }

    public String getFamilyDoctor() { return familyDoctor; }
    public void setFamilyDoctor(String familyDoctor) { this.familyDoctor = familyDoctor; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public List<CompleteBloodCount> getCompleteBloodCounts() { return completeBloodCounts; }
    public void setCompleteBloodCounts(List<CompleteBloodCount> completeBloodCounts) { this.completeBloodCounts = completeBloodCounts; }
}