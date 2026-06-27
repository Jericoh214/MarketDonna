package com.example.demo.servicio;

import com.example.demo.modelo.ItemCarrito;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PdfService {

    public ByteArrayOutputStream generarBoletaPdf(List<ItemCarrito> items, double total) throws DocumentException {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.GRAY);
        Font fontCabeceraTabla = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new BaseColor(243, 156, 18)); // Naranja de Market Donna

        Paragraph titulo = new Paragraph("MARKET DONNA", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Boleta de Venta Electrónica", fontSubtitulo);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitulo);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Paragraph fecha = new Paragraph("Fecha de emisión: " + sdf.format(new Date()), fontNormal);
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingBefore(20);
        fecha.setSpacingAfter(20);
        document.add(fecha);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 5f, 2f, 2f}); 

        String[] cabeceras = {"Cant.", "Descripción", "P. Unitario", "Subtotal"};
        for (String cabecera : cabeceras) {
            PdfPCell cell = new PdfPCell(new Phrase(cabecera, fontCabeceraTabla));
            cell.setBackgroundColor(new BaseColor(52, 58, 64)); // Gris oscuro Bootstrap
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        for (ItemCarrito item : items) {
            PdfPCell celdaCant = new PdfPCell(new Phrase(String.valueOf(item.getCantidad()), fontNormal));
            celdaCant.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaCant.setPadding(5);
            table.addCell(celdaCant);

            PdfPCell celdaNombre = new PdfPCell(new Phrase(item.getProducto().getNombre(), fontNormal));
            celdaNombre.setPadding(5);
            table.addCell(celdaNombre);

            PdfPCell celdaPrecio = new PdfPCell(new Phrase(String.format("S/ %.2f", item.getProducto().getPrecio()), fontNormal));
            celdaPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaPrecio.setPadding(5);
            table.addCell(celdaPrecio);

            PdfPCell celdaSubtotal = new PdfPCell(new Phrase(String.format("S/ %.2f", item.getSubtotal()), fontNormal));
            celdaSubtotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaSubtotal.setPadding(5);
            table.addCell(celdaSubtotal);
        }
        document.add(table);

        Paragraph totalDoc = new Paragraph(String.format("TOTAL A PAGAR: S/ %.2f", total), fontTotal);
        totalDoc.setAlignment(Element.ALIGN_RIGHT);
        totalDoc.setSpacingBefore(20);
        document.add(totalDoc);

        Paragraph footer = new Paragraph("¡Gracias por su compra en Market Donna! \n Probablemente... las mejores frutas y verduras.", fontSubtitulo);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(40);
        document.add(footer);

        document.close();
        return out;
    }
}