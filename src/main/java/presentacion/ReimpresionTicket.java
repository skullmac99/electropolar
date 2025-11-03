package presentacion;

import javax.swing.*;
import java.awt.*;

public class ReimpresionTicket extends JPanel {

    private JTextField txtFolio;
    private JTable tabla;

    public ReimpresionTicket() {
        setLayout(new BorderLayout());
        setBackground(new Color(247, 215, 132)); // fondo principal

        // Panel superior con título, folio y logo
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#B5EFE3"));
        //panelSuperior.setBorder(new LineBorder(Color.BLACK, 1, true));

        JLabel titulo = new JLabel("Reimpresión de Tickets", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelSuperior.add(titulo, BorderLayout.NORTH);

        JPanel filaFolio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaFolio.setBackground(Color.decode("#B5EFE3"));
        filaFolio.add(new JLabel("Folio de la venta"));
        txtFolio = new JTextField(15);
        txtFolio.setBackground(Color.decode("#A4C5E1"));
        filaFolio.add(txtFolio);
        panelSuperior.add(filaFolio, BorderLayout.WEST);

        // Logo a la derecha
        ImageIcon imageIcon = new ImageIcon("electropolar\\src\\main\\java\\presentacion\\logo.png");
        Image imagenEscalada = imageIcon.getImage().getScaledInstance(95, 95, Image.SCALE_SMOOTH);
        ImageIcon iconoPersonalizado = new ImageIcon(imagenEscalada);
        JLabel imageLabel = new JLabel(iconoPersonalizado);
        panelSuperior.add(imageLabel, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Nombre", "Cantidad", "Precio Un", "Importe"};
        tabla = new JTable(new Object[0][columnas.length], columnas);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setBackground(Color.decode("#7CBBBB"));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(tabla);
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.decode("#FFFFFF"));
        panelTabla.add(scroll, BorderLayout.CENTER);

        add(panelTabla, BorderLayout.CENTER);

        // Botones inferiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.setBackground(Color.decode("#FFFFFF"));

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setBackground(Color.decode("#A4C5E1"));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.decode("#A4C5E1"));

        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    // Getters si los necesitas desde fuera
    public JTextField getTxtFolio() {
        return txtFolio;
    }

    public JTable getTabla() {
        return tabla;
    }
}