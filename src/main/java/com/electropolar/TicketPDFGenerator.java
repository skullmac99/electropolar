package com.electropolar;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import Datos.ValidacionesBD;

public class TicketPDFGenerator {

    /**
     * Genera un PDF con el ticket de la venta.
     * 
     * @param venta      Objeto Venta con fecha, total y lista de detalles.
     * @param cliente    Cliente de la venta.
     * @param vendedor   Vendedor que atendió.
     * @param rutaSalida Ruta completa donde se guardará el PDF.
     */

    // Dentro del método generarPDF:
    public static void generarPDF(Venta venta, Cliente cliente, Vendedor vendedor, String rutaSalida)
            throws IOException {
        // Asegurar que la carpeta para el archivo PDF exista
        java.io.File archivo = new java.io.File(rutaSalida);
        java.io.File carpeta = archivo.getParentFile();
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;

            // 1. Cargar y dibujar logo centrado
            try {
                PDImageXObject logo = PDImageXObject.createFromFile("electropolar\\src\\main\\java\\presentacion\\logo.png",
                        doc);
                float imageWidth = 100;
                float imageHeight = 100;
                float xImage = (page.getMediaBox().getWidth() - imageWidth) / 2;
                y -= imageHeight;
                cs.drawImage(logo, xImage, y, imageWidth, imageHeight);
                y -= 20; // espacio debajo del logo
            } catch (IOException e) {
                System.err.println("Logo no encontrado o no se pudo cargar.");
            }

            // 2. Cabecera de texto con desplazamiento manual
            cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText("ELECTROPOLAR");
            cs.endText();
            y -= 15;

            cs.setFont(PDType1Font.HELVETICA, 10);
            String[] lineasCabecera = {
                    "MARCO ANTONIO RIVERA LOPEZ",
                    "Dir: IGNACIO ALLENDE No.245, Metepec, CP 52140",
                    "RFC: RILM900512JK8    Tel: 7224581930    WhatsApp: 7291045623",
                    "Correo: marcorivera90@gmail.com",
                    "Folio Venta: " + venta.getIdVenta(),
                    "Fecha: " + venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    "Cliente: " + cliente.getIdCliente() + " - " + cliente.getNombreCliente(),
                    "Vendedor: " + vendedor.toString()
            };

            for (String linea : lineasCabecera) {
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText(linea);
                cs.endText();
                y -= 13;
            }

            // 3. Tabla de productos
            y -= 10;
            cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText(String.format("%-20s %5s %9s %9s", "Producto", "Cant", "P.Unit", "Subtotal"));
            cs.endText();

            y -= 15;
            cs.setFont(PDType1Font.HELVETICA, 10);
            for (DetalleVenta det : venta.getDetalles()) {
                Producto prod = new ValidacionesBD().buscarProductoPorId(det.getIdProducto());
                String nombre = (prod != null ? prod.getNombre() : det.getIdProducto());

                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText(String.format("%-20.20s %5d %9.2f %9.2f",
                        nombre, det.getCantidad(), det.getPrecioUnitario(), det.getSubtotal()));
                cs.endText();
                y -= 13;
            }

            // 4. Total
            y -= 10;
            cs.moveTo(margin, y);
            cs.lineTo(page.getMediaBox().getWidth() - margin, y);
            cs.stroke();
            y -= 15;
            cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText(String.format("TOTAL A PAGAR: $%.2f", venta.getTotal()));
            cs.endText();
        }

        doc.save(rutaSalida);
        doc.close();
    }
}
