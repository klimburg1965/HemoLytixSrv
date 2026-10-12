package de.hemolytix.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complete_blood_count")
public class CompleteBloodCount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    // --- Red Blood Cell Count (Erythrozyten & Indizes) ---
    @Column(name = "erythrocytes")
    private Double erythrocytes; // T/l (Tera pro Liter)

    @Column(name = "hemoglobin")
    private Double hemoglobin; // g/dl

    @Column(name = "hematocrit")
    private Double hematocrit; // %

    @Column(name = "mcv")
    private Double mcv; // fl (Mean Corpuscular Volume)

    @Column(name = "mch")
    private Double mch; // pg (Mean Corpuscular Hemoglobin)

    @Column(name = "mchc")
    private Double mchc; // g/dl (Mean Corpuscular Hemoglobin Concentration)

    // --- Platelets (Thrombozyten) ---
    @Column(name = "platelets")
    private Double platelets; // G/l (Giga pro Liter)

    // --- White Blood Cell Count (Leukozyten gesamt) ---
    @Column(name = "leukocytes")
    private Double leukocytes; // G/l

    // --- Differential Blood Count (Differenzialblutbild in %) ---
    @Column(name = "neutrophils_rel")
    private Double neutrophilsRel; // %

    @Column(name = "lymphocytes_rel")
    private Double lymphocytesRel; // %

    @Column(name = "monocytes_rel")
    private Double monocytesRel; // %

    @Column(name = "eosinophils_rel")
    private Double eosinophilsRel; // %

    @Column(name = "basophils_rel")
    private Double basophilsRel; // %

    // --- Remarks / Clinical Notes ---
    @Column(length = 1000)
    private String notes;

    // --- Relationship ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    public CompleteBloodCount() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompleteBloodCount)) return false;
        CompleteBloodCount that = (CompleteBloodCount) o;
        return id != null && id.equals(that.getId());
    }

    @PrePersist
    protected void onCreate() {
        if (this.recordedAt == null) {
            this.recordedAt = LocalDateTime.now();
        }
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Double getErythrocytes() {
        return erythrocytes;
    }

    public void setErythrocytes(Double erythrocytes) {
        this.erythrocytes = erythrocytes;
    }

    public Double getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(Double hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public Double getHematocrit() {
        return hematocrit;
    }

    public void setHematocrit(Double hematocrit) {
        this.hematocrit = hematocrit;
    }

    public Double getMcv() {
        return mcv;
    }

    public void setMcv(Double mcv) {
        this.mcv = mcv;
    }

    public Double getMch() {
        return mch;
    }

    public void setMch(Double mch) {
        this.mch = mch;
    }

    public Double getMchc() {
        return mchc;
    }

    public void setMchc(Double mchc) {
        this.mchc = mchc;
    }

    public Double getPlatelets() {
        return platelets;
    }

    public void setPlatelets(Double platelets) {
        this.platelets = platelets;
    }

    public Double getLeukocytes() {
        return leukocytes;
    }

    public void setLeukocytes(Double leukocytes) {
        this.leukocytes = leukocytes;
    }

    public Double getNeutrophilsRel() {
        return neutrophilsRel;
    }

    public void setNeutrophilsRel(Double neutrophilsRel) {
        this.neutrophilsRel = neutrophilsRel;
    }

    public Double getLymphocytesRel() {
        return lymphocytesRel;
    }

    public void setLymphocytesRel(Double lymphocytesRel) {
        this.lymphocytesRel = lymphocytesRel;
    }

    public Double getMonocytesRel() {
        return monocytesRel;
    }

    public void setMonocytesRel(Double monocytesRel) {
        this.monocytesRel = monocytesRel;
    }

    public Double getEosinophilsRel() {
        return eosinophilsRel;
    }

    public void setEosinophilsRel(Double eosinophilsRel) {
        this.eosinophilsRel = eosinophilsRel;
    }

    public Double getBasophilsRel() {
        return basophilsRel;
    }

    public void setBasophilsRel(Double basophilsRel) {
        this.basophilsRel = basophilsRel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}