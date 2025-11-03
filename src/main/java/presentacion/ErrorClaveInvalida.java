package presentacion;

import javax.swing.*;
import java.awt.*;

public class ErrorClaveInvalida extends JDialog {
    public ErrorClaveInvalida(JFrame parent) {
        super(parent, "Error", true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.decode("#B5EFE3"));

        JLabel lblMensaje = new JLabel("<html><center>El producto no existe o<br> introdujo mal su clave</center></html>", JLabel.CENTER);
        lblMensaje.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(lblMensaje, BorderLayout.CENTER);

        JButton btnOk = new JButton("Ok");
        btnOk.setBackground(new Color(189, 235, 190));
        btnOk.addActionListener(e -> dispose());
        JPanel panelBtn = new JPanel();
        panelBtn.setBackground(new Color(255, 245, 175));
        panelBtn.add(btnOk);
        add(panelBtn, BorderLayout.SOUTH);

        setSize(350, 200);
        setLocationRelativeTo(parent);
    }
}
