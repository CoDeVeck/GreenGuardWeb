package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.ReporteFiltroEstadoIncidenteClasificacion;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class ExportacionReporteService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Genera un archivo CSV con los reportes filtrados
     */
    public String generarCSV(List<ReporteFiltroEstadoIncidenteClasificacion> reportes) throws IOException {
        StringWriter writer = new StringWriter();
        
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Estado", "Tipo Incidente", "Clasificación", "Detalle", "Fecha Registro")
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (ReporteFiltroEstadoIncidenteClasificacion reporte : reportes) {
            	csvPrinter.printRecord(
            		    reporte.getIdReporte(),
            		    getEstadoTexto(reporte.getEstado().name()),
            		    reporte.getDescTipoInci(),          // ← NO reporte.getIdTipoInci()
            		    reporte.getDescTipoClasi(),         // ← NO reporte.getIdTipoClasi()
            		    reporte.getDetalleRepo() != null ? reporte.getDetalleRepo() : "Sin detalle",
            		    reporte.getRepoRegistado().format(DATE_FORMATTER)
            		);
            }
        }

        return writer.toString();
    }

    /**
     * Genera un archivo PDF con los reportes filtrados
     */
    public byte[] generarPDF(List<ReporteFiltroEstadoIncidenteClasificacion> reportes) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate()); // Horizontal para más columnas
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Reporte de Incidentes Ambientales", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Fecha de generación
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph date = new Paragraph("Generado el: " + 
                java.time.LocalDateTime.now().format(DATE_FORMATTER), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);

            // Total de reportes
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            Paragraph info = new Paragraph("Total de reportes: " + reportes.size(), infoFont);
            info.setSpacingAfter(15);
            document.add(info);

            // Tabla
            PdfPTable table = new PdfPTable(6); // 6 columnas
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Anchos de columnas
            float[] columnWidths = {1f, 1.5f, 2f, 2f, 4f, 2f};
            table.setWidths(columnWidths);

            // Encabezados
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            String[] headers = {"ID", "Estado", "Tipo Incidente", "Clasificación", "Detalle", "Fecha"};
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new BaseColor(41, 128, 185)); // Azul
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Datos
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
            for (ReporteFiltroEstadoIncidenteClasificacion reporte : reportes) {
                // ID
                table.addCell(createCell(String.valueOf(reporte.getIdReporte()), dataFont));
                
                // Estado con color
                PdfPCell estadoCell = new PdfPCell(new Phrase(
                    getEstadoTexto(reporte.getEstado().name()), dataFont));
                estadoCell.setBackgroundColor(getEstadoColor(reporte.getEstado().name()));
                estadoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                estadoCell.setPadding(5);
                table.addCell(estadoCell);
                
                // Tipo Incidente
                table.addCell(createCell(reporte.getDescTipoInci(), dataFont));
                
                // Clasificación
                table.addCell(createCell(reporte.getDescTipoClasi(), dataFont));
                
                // Detalle
                String detalle = reporte.getDetalleRepo() != null ? reporte.getDetalleRepo() : "Sin detalle";
                if (detalle.length() > 100) {
                    detalle = detalle.substring(0, 97) + "...";
                }
                table.addCell(createCell(detalle, dataFont));
                
                // Fecha
                table.addCell(createCell(reporte.getRepoRegistado().format(DATE_FORMATTER), dataFont));
            }

            document.add(table);

            // Footer
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY);
            Paragraph footer = new Paragraph(
                "Green Guard - Sistema de Gestión de Reportes Ambientales", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

        } finally {
            document.close();
        }

        return baos.toByteArray();
    }

    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private String getEstadoTexto(String estado) {
        return switch (estado) {
            case "PE" -> "Pendiente";
            case "EP" -> "En Proceso";
            case "RE" -> "Resuelto";
            case "CA" -> "Cancelado";
            default -> estado;
        };
    }

    private BaseColor getEstadoColor(String estado) {
        return switch (estado) {
            case "PE" -> new BaseColor(255, 243, 205); // Amarillo claro
            case "EP" -> new BaseColor(209, 236, 241); // Azul claro
            case "RE" -> new BaseColor(212, 237, 218); // Verde claro
            case "CA" -> new BaseColor(248, 215, 218); // Rojo claro
            default -> BaseColor.WHITE;
        };
    }
}