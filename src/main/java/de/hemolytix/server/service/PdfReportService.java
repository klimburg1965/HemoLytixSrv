package de.hemolytix.server.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import de.hemolytix.server.dto.BloodCountAnalysisResponse;
import de.hemolytix.server.dto.EvaluatedValue;
import de.hemolytix.server.dto.PatientResponse;
import de.hemolytix.server.types.BloodType;
import de.hemolytix.server.types.EvaluationStatus;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class PdfReportService {

    // Modernes Farbschema
    private static final Color PRIMARY_COLOR = new Color(0, 102, 153);    // Deep Medical Teal
    private static final Color SECONDARY_COLOR = new Color(240, 244, 248); // Soft Ice Blue (Background)
    private static final Color TEXT_COLOR = new Color(40, 40, 40);         // Charcoal Text
    private static final Color LIGHT_TEXT_COLOR = new Color(100, 100, 100);
    private static final Color ALT_ROW_COLOR = new Color(248, 250, 252);   // Zebra Striping

    // Status-Farben
    private static final Color COLOR_DANGER = new Color(180, 40, 40);      // Rot für Abweichungen
    private static final Color COLOR_SUCCESS = new Color(30, 130, 60);     // Grün für NORMAL

    // Schriftarten
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_COLOR);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, LIGHT_TEXT_COLOR);
    private static final Font SECTION_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_COLOR);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, TEXT_COLOR);
    private static final Font REGULAR_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_COLOR);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

    private static final Font DANGER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COLOR_DANGER);
    private static final Font SUCCESS_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, COLOR_SUCCESS);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final DecimalFormat GERMAN_NUMBER_FORMAT =
            new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.GERMAN));

    public ByteArrayInputStream generateLabReport(PatientResponse patient, List<BloodCountAnalysisResponse> analyses) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 1. Header & Logo-Bereich
            addHeader(document);

            // 2. Patienten- & Dokumenten-Informationen (Infobox)
            addPatientInfoCard(document, patient);

            // 3. Tabellen pro Analyse-Eintrag
            if (analyses == null || analyses.isEmpty()) {
                document.add(new Paragraph("Keine Analysedaten vorhanden.", REGULAR_FONT));
            } else {
                for (int i = 0; i < analyses.size(); i++) {
                    BloodCountAnalysisResponse analysis = analyses.get(i);
                    addBloodCountSection(document, analysis, i + 1);
                }
            }

            // 4. Footer / Abspann
            addFooterNotice(document);

            document.close();

        } catch (DocumentException ex) {
            throw new RuntimeException("Fehler beim Erstellen des PDF-Befundes", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addHeader(Document document) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{3, 1});

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.addElement(new Paragraph("HEMOLYTIX", TITLE_FONT));
        leftCell.addElement(new Paragraph("Laboratorium für Hämatologie & Diagnostik", SUBTITLE_FONT));

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph datePar = new Paragraph("Erstellt am:\n" + LocalDateTime.now().format(DATE_TIME_FORMATTER), SUBTITLE_FONT);
        datePar.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(datePar);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        LineSeparator line = new LineSeparator(1f, 100, PRIMARY_COLOR, Element.ALIGN_CENTER, -5);
        document.add(new Chunk(line));

        Paragraph spacing = new Paragraph(" ", REGULAR_FONT);
        spacing.setSpacingAfter(5);
        document.add(spacing);
    }

    private void addPatientInfoCard(Document document, PatientResponse patient) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1});

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(SECONDARY_COLOR);
        cell.setPadding(10);
        cell.setBorderColor(PRIMARY_COLOR);
        cell.setBorderWidth(1f);

        PdfPTable innerTable = new PdfPTable(2);
        innerTable.setWidthPercentage(100);

        // Stammdaten
        addMetaRow(innerTable, "Patienten-ID:", String.valueOf(patient.id() != null ? patient.id() : "-"));
        addMetaRow(innerTable, "Name:", (patient.lastName() != null ? patient.lastName() : "") + ", " + (patient.firstName() != null ? patient.firstName() : ""));
        addMetaRow(innerTable, "Geburtsdatum:", patient.birthDate() != null ? patient.birthDate().format(DATE_FORMATTER) : "k.A.");
        addMetaRow(innerTable, "Geschlecht:", patient.gender() != null ? patient.gender().name() : "k.A.");
        addMetaRow(innerTable, "Blutgruppe:", patient.bloodType() != null ? formatBloodType(patient.bloodType()) : "k.A.");

        // Neue Felder: Krankenkasse & Hausarzt
        addMetaRow(innerTable, "Krankenkasse:", patient.healthInsuranceCompany() != null && !patient.healthInsuranceCompany().isBlank() ? patient.healthInsuranceCompany() : "-");
        addMetaRow(innerTable, "Hausarzt:", patient.familyDoctor() != null && !patient.familyDoctor().isBlank() ? patient.familyDoctor() : "-");

        // Adresse formatieren, falls vorhanden
        if (patient.address() != null) {
            var addr = patient.address();
            String streetFormatted = (addr.street() != null ? addr.street() : "") + " " + (addr.houseNumber() != null ? addr.houseNumber() : "");
            String cityFormatted = (addr.zipCode() != null ? addr.zipCode() : "") + " " + (addr.city() != null ? addr.city() : "");

            addMetaRow(innerTable, "Anschrift:", streetFormatted.trim());
            addMetaRow(innerTable, "", cityFormatted.trim());
        } else {
            addMetaRow(innerTable, "Anschrift:", "-");
        }

        cell.addElement(innerTable);
        table.addCell(cell);

        PdfPCell emptyCell = new PdfPCell();
        emptyCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(emptyCell);

        document.add(table);
    }

    private void addMetaRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(2);
        table.addCell(labelCell);

        PdfPCell valCell = new PdfPCell(new Phrase(value, REGULAR_FONT));
        valCell.setBorder(Rectangle.NO_BORDER);
        valCell.setPadding(2);
        table.addCell(valCell);
    }

    private void addBloodCountSection(Document document, BloodCountAnalysisResponse analysis, int index) throws DocumentException {
        String timestamp = analysis.recordedAt() != null ? analysis.recordedAt().format(DATE_TIME_FORMATTER) : "Unbekannt";
        Paragraph sectionHeader = new Paragraph("Messung #" + index + " - Entnahme: " + timestamp, SECTION_FONT);
        sectionHeader.setSpacingBefore(12);
        sectionHeader.setSpacingAfter(6);
        document.add(sectionHeader);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 2, 2, 3});
        table.setKeepTogether(true);

        addTableHeader(table, "Parameter");
        addTableHeader(table, "Messwert");
        addTableHeader(table, "Einheit");
        addTableHeader(table, "Status");

        boolean isAlt = false;

        // Rotes Blutbild
        isAlt = addEvaluatedRow(table, "Hämoglobin (Hb)", analysis.hemoglobin(), isAlt);
        isAlt = addEvaluatedRow(table, "Erythrozyten", analysis.erythrocytes(), isAlt);
        isAlt = addEvaluatedRow(table, "Hämatokrit", analysis.hematocrit(), isAlt);

        // Indizes
        isAlt = addEvaluatedRow(table, "MCV", analysis.mcv(), isAlt);
        isAlt = addEvaluatedRow(table, "MCH", analysis.mch(), isAlt);
        isAlt = addEvaluatedRow(table, "MCHC", analysis.mchc(), isAlt);

        // Leukozyten & Thrombozyten
        isAlt = addEvaluatedRow(table, "Leukozyten", analysis.leukocytes(), isAlt);
        isAlt = addEvaluatedRow(table, "Thrombozyten", analysis.platelets(), isAlt);

        // Differenzialblutbild
        isAlt = addEvaluatedRow(table, "Neutrophile (%)", analysis.neutrophilsRel(), isAlt);
        isAlt = addEvaluatedRow(table, "Lymphozyten (%)", analysis.lymphocytesRel(), isAlt);
        isAlt = addEvaluatedRow(table, "Monozyten (%)", analysis.monocytesRel(), isAlt);
        isAlt = addEvaluatedRow(table, "Eosinophile (%)", analysis.eosinophilsRel(), isAlt);
        isAlt = addEvaluatedRow(table, "Basophile (%)", analysis.basophilsRel(), isAlt);

        document.add(table);
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(PRIMARY_COLOR);
        header.setPhrase(new Phrase(headerTitle, HEADER_FONT));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(6);
        table.addCell(header);
    }

    private boolean addEvaluatedRow(PdfPTable table, String paramLabel, EvaluatedValue evalVal, boolean isAlt) {
        if (evalVal == null) {
            return isAlt;
        }

        String valueStr = formatValue(evalVal.value());
        String unitStr = evalVal.unit() != null ? evalVal.unit() : "-";

        return addDataRow(table, paramLabel, valueStr, unitStr, evalVal.status(), isAlt);
    }

    private boolean addDataRow(PdfPTable table, String param, String value, String unit,
                               EvaluationStatus status, boolean isAlt) {
        Color bgColor = isAlt ? ALT_ROW_COLOR : Color.WHITE;

        PdfPCell paramCell = new PdfPCell(new Phrase(param, REGULAR_FONT));
        paramCell.setBackgroundColor(bgColor);
        paramCell.setPadding(5);
        table.addCell(paramCell);

        PdfPCell valCell = new PdfPCell(new Phrase(value, BOLD_FONT));
        valCell.setBackgroundColor(bgColor);
        valCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valCell.setPadding(5);
        table.addCell(valCell);

        PdfPCell unitCell = new PdfPCell(new Phrase(unit, REGULAR_FONT));
        unitCell.setBackgroundColor(bgColor);
        unitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        unitCell.setPadding(5);
        table.addCell(unitCell);

        PdfPCell statusCell = new PdfPCell(formatStatus(status));
        statusCell.setBackgroundColor(bgColor);
        statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        statusCell.setPadding(5);
        table.addCell(statusCell);

        return !isAlt;
    }

    private Phrase formatStatus(EvaluationStatus status) {
        if (status == null || status == EvaluationStatus.UNKNOWN) {
            return new Phrase("-", SUBTITLE_FONT);
        }

        String text = status.name();

        if (status == EvaluationStatus.LOW || status == EvaluationStatus.HIGH ||
                status == EvaluationStatus.CRITICAL_LOW || status == EvaluationStatus.CRITICAL_HIGH) {
            return new Phrase(text, DANGER_FONT);
        } else {
            return new Phrase(text, SUCCESS_FONT);
        }
    }

    private void addFooterNotice(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("\nDieser Befund wurde elektronisch erstellt und ist ohne Unterschrift gültig.", SUBTITLE_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    private String formatValue(Object val) {
        if (val == null) {
            return "-";
        }
        if (val instanceof Number) {
            return GERMAN_NUMBER_FORMAT.format(val);
        }
        return String.valueOf(val);
    }

    public static String formatBloodType(BloodType bloodType) {
        if (bloodType == null) return "k.A.";
        return bloodType.name()
                .replace("_POSITIVE", "+")
                .replace("_NEGATIVE", "-");
    }
}