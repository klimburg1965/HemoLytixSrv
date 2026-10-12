package de.hemolytix.server.types;

public enum BloodParameter {
    HEMOGLOBIN("hemoglobin"),
    ERYTHROCYTES("erythrocytes"),
    HEMATOCRIT("hematocrit"),
    MCV("mcv"),
    MCH("mch"),
    MCHC("mchc"),
    LEUKOCYTES("leukocytes"),
    PLATELETS("platelets");

    private final String dbKey;

    BloodParameter(String dbKey) {
        this.dbKey = dbKey;
    }

    public String getDbKey() {
        return dbKey;
    }
}