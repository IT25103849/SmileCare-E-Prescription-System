package com.smilecare.service;

import com.smilecare.entity.Prescription;
import com.smilecare.entity.PrescriptionItem;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PrescriptionPdfService {

    private static final Font TITLE_FONT =
            new Font(
                    Font.HELVETICA,
                    20,
                    Font.BOLD
            );

    private static final Font SUBTITLE_FONT =
            new Font(
                    Font.HELVETICA,
                    11,
                    Font.NORMAL
            );

    private static final Font SECTION_FONT =
            new Font(
                    Font.HELVETICA,
                    12,
                    Font.BOLD
            );

    private static final Font HEADER_FONT =
            new Font(
                    Font.HELVETICA,
                    9,
                    Font.BOLD
            );

    private static final Font BODY_FONT =
            new Font(
                    Font.HELVETICA,
                    9,
                    Font.NORMAL
            );

    private static final Font SMALL_FONT =
            new Font(
                    Font.HELVETICA,
                    8,
                    Font.NORMAL
            );

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a"
            );


    public byte[] generatePrescriptionPdf(
            Prescription prescription) {

        if (!"ISSUED".equals(prescription.getStatus())) {

            throw new IllegalStateException(
                    "Only issued prescriptions can be downloaded as PDF."
            );
        }

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document =
                new Document(
                        PageSize.A4,
                        40,
                        40,
                        45,
                        45
                );

        try {

            PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();

            addHeader(
                    document,
                    prescription
            );

            addPrescriptionInformation(
                    document,
                    prescription
            );

            addMedicineTable(
                    document,
                    prescription
            );

            addNotes(
                    document,
                    prescription
            );

            addFooter(
                    document,
                    prescription
            );

            document.close();

            return outputStream.toByteArray();

        } catch (Exception exception) {

            if (document.isOpen()) {
                document.close();
            }

            throw new RuntimeException(
                    "Failed to generate prescription PDF",
                    exception
            );
        }
    }


    private void addHeader(
            Document document,
            Prescription prescription)
            throws Exception {

        Paragraph title =
                new Paragraph(
                        "SmileCare",
                        TITLE_FONT
                );

        title.setAlignment(
                Element.ALIGN_CENTER
        );

        title.setSpacingAfter(3);

        document.add(title);


        Paragraph subtitle =
                new Paragraph(
                        "Digital E-Prescription",
                        SUBTITLE_FONT
                );

        subtitle.setAlignment(
                Element.ALIGN_CENTER
        );

        subtitle.setSpacingAfter(6);

        document.add(subtitle);


        Paragraph prescriptionNumber =
                new Paragraph(
                        "Prescription #"
                                + prescription.getId(),
                        SECTION_FONT
                );

        prescriptionNumber.setAlignment(
                Element.ALIGN_CENTER
        );

        prescriptionNumber.setSpacingAfter(20);

        document.add(prescriptionNumber);
    }


    private void addPrescriptionInformation(
            Document document,
            Prescription prescription)
            throws Exception {

        Paragraph heading =
                new Paragraph(
                        "Prescription Information",
                        SECTION_FONT
                );

        heading.setSpacingAfter(10);

        document.add(heading);


        PdfPTable infoTable =
                new PdfPTable(4);

        infoTable.setWidthPercentage(100);

        infoTable.setWidths(
                new float[]{
                        1.2f,
                        1.8f,
                        1.2f,
                        1.8f
                }
        );


        addLabelCell(
                infoTable,
                "Patient ID"
        );

        addValueCell(
                infoTable,
                String.valueOf(
                        prescription.getPatientId()
                )
        );


        addLabelCell(
                infoTable,
                "Dentist ID"
        );

        addValueCell(
                infoTable,
                String.valueOf(
                        prescription.getDentistId()
                )
        );


        addLabelCell(
                infoTable,
                "Visit ID"
        );

        addValueCell(
                infoTable,
                prescription.getVisitId() != null
                        ? String.valueOf(
                        prescription.getVisitId()
                )
                        : "-"
        );


        addLabelCell(
                infoTable,
                "Status"
        );

        addValueCell(
                infoTable,
                prescription.getStatus()
        );


        addLabelCell(
                infoTable,
                "Date"
        );

        String date =
                prescription.getPrescriptionDate() != null
                        ? prescription
                        .getPrescriptionDate()
                        .format(DATE_FORMATTER)
                        : "-";

        PdfPCell dateCell =
                new PdfPCell(
                        new Phrase(
                                safeText(date),
                                BODY_FONT
                        )
                );

        dateCell.setPadding(8);
        dateCell.setColspan(3);
        dateCell.setBorderColor(
                new Color(
                        226,
                        232,
                        240
                )
        );

        infoTable.addCell(dateCell);


        infoTable.setSpacingAfter(22);

        document.add(infoTable);
    }


    private void addMedicineTable(
            Document document,
            Prescription prescription)
            throws Exception {

        Paragraph heading =
                new Paragraph(
                        "Prescribed Medicines",
                        SECTION_FONT
                );

        heading.setSpacingAfter(10);

        document.add(heading);


        PdfPTable table =
                new PdfPTable(7);

        table.setWidthPercentage(100);

        table.setWidths(
                new float[]{
                        0.5f,
                        2.2f,
                        1.2f,
                        1.4f,
                        1.6f,
                        1.3f,
                        2.0f
                }
        );


        addHeaderCell(
                table,
                "#"
        );

        addHeaderCell(
                table,
                "Medicine"
        );

        addHeaderCell(
                table,
                "Strength"
        );

        addHeaderCell(
                table,
                "Dosage"
        );

        addHeaderCell(
                table,
                "Frequency"
        );

        addHeaderCell(
                table,
                "Duration"
        );

        addHeaderCell(
                table,
                "Instructions"
        );


        int number = 1;

        for (PrescriptionItem item
                : prescription.getItems()) {

            addBodyCell(
                    table,
                    String.valueOf(number++)
            );

            addBodyCell(
                    table,
                    item.getMedicine() != null
                            ? item
                            .getMedicine()
                            .getMedicineName()
                            : "-"
            );

            addBodyCell(
                    table,
                    item.getMedicine() != null
                            && item
                            .getMedicine()
                            .getStrength() != null
                            ? item
                            .getMedicine()
                            .getStrength()
                            : "-"
            );

            addBodyCell(
                    table,
                    item.getDosage()
            );

            addBodyCell(
                    table,
                    item.getFrequency()
            );

            addBodyCell(
                    table,
                    item.getDuration()
            );

            addBodyCell(
                    table,
                    item.getInstructions() != null
                            && !item
                            .getInstructions()
                            .isBlank()
                            ? item.getInstructions()
                            : "-"
            );
        }

        table.setSpacingAfter(22);

        document.add(table);
    }


    private void addNotes(
            Document document,
            Prescription prescription)
            throws Exception {

        if (prescription.getNotes() == null
                || prescription
                .getNotes()
                .isBlank()) {

            return;
        }


        Paragraph heading =
                new Paragraph(
                        "Additional Notes",
                        SECTION_FONT
                );

        heading.setSpacingAfter(8);

        document.add(heading);


        PdfPTable notesTable =
                new PdfPTable(1);

        notesTable.setWidthPercentage(100);


        PdfPCell notesCell =
                new PdfPCell(
                        new Phrase(
                                safeText(
                                        prescription
                                                .getNotes()
                                ),
                                BODY_FONT
                        )
                );

        notesCell.setPadding(10);

        notesCell.setBackgroundColor(
                new Color(
                        248,
                        250,
                        252
                )
        );

        notesCell.setBorderColor(
                new Color(
                        226,
                        232,
                        240
                )
        );

        notesTable.addCell(notesCell);

        notesTable.setSpacingAfter(25);

        document.add(notesTable);
    }


    private void addFooter(
            Document document,
            Prescription prescription)
            throws Exception {

        Paragraph separator =
                new Paragraph(
                        "____________________________________________"
                );

        separator.setAlignment(
                Element.ALIGN_CENTER
        );

        separator.setSpacingBefore(10);
        separator.setSpacingAfter(8);

        document.add(separator);


        Paragraph footer =
                new Paragraph(
                        "This prescription was generated electronically "
                                + "by the SmileCare Dental Management System.",
                        SMALL_FONT
                );

        footer.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(footer);


        Paragraph id =
                new Paragraph(
                        "Prescription ID: "
                                + prescription.getId(),
                        SMALL_FONT
                );

        id.setAlignment(
                Element.ALIGN_CENTER
        );

        id.setSpacingBefore(4);

        document.add(id);
    }


    private void addLabelCell(
            PdfPTable table,
            String text) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                safeText(text),
                                HEADER_FONT
                        )
                );

        cell.setPadding(8);

        cell.setBackgroundColor(
                new Color(
                        248,
                        250,
                        252
                )
        );

        cell.setBorderColor(
                new Color(
                        226,
                        232,
                        240
                )
        );

        table.addCell(cell);
    }


    private void addValueCell(
            PdfPTable table,
            String text) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                safeText(text),
                                BODY_FONT
                        )
                );

        cell.setPadding(8);

        cell.setBorderColor(
                new Color(
                        226,
                        232,
                        240
                )
        );

        table.addCell(cell);
    }


    private void addHeaderCell(
            PdfPTable table,
            String text) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                safeText(text),
                                HEADER_FONT
                        )
                );

        cell.setPadding(7);

        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        cell.setBackgroundColor(
                new Color(
                        239,
                        246,
                        255
                )
        );

        cell.setBorderColor(
                new Color(
                        203,
                        213,
                        225
                )
        );

        table.addCell(cell);
    }


    private void addBodyCell(
            PdfPTable table,
            String text) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                safeText(text),
                                BODY_FONT
                        )
                );

        cell.setPadding(7);

        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        cell.setBorderColor(
                new Color(
                        226,
                        232,
                        240
                )
        );

        table.addCell(cell);
    }


    private String safeText(
            String text) {

        if (text == null) {
            return "-";
        }

        return text
                .replace("\n", " ")
                .replace("\r", " ");
    }
}