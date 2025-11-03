package com.electropolar;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Funciones {

    public static void inicializarFechaYHora(JTextField txtFecha, JTextField txtHora) {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");

        Date ahora = new Date();
        txtFecha.setText(formatoFecha.format(ahora));
        txtHora.setText(formatoHora.format(ahora));

        txtFecha.setEditable(false);
        txtHora.setEditable(false);

        // Reloj en tiempo real
        Timer timer = new Timer(1000, e -> {
            txtHora.setText(formatoHora.format(new Date()));
        });
        timer.start();
    }
}
