/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import EstructurasAux.itemsOrdenCompra;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author Oscar Dario Malagon Murcia
 */
public class fdc002 implements JRDataSource {

    private List<itemsOrdenCompra> pedido = new ArrayList<>();
    private int i = -1;

    public fdc002() {

    }

    @Override
    public boolean next() throws JRException {
        return ++i < pedido.size();
    }

    public void addAsistente(itemsOrdenCompra Asistente) {

        this.pedido.add(Asistente);

    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {

        Object valor = null;

        if ("cInterno".equals(jrf.getName())) {

            valor = pedido.get(i).getCinterno();

        } else if ("Desc".equals(jrf.getName())) {

            valor = pedido.get(i).getDesc();

        } else if ("Cant".equals(jrf.getName())) {

            valor = Float.toString(pedido.get(i).getCaprobada());

        } else if ("pres".equals(jrf.getName())) {
            valor = pedido.get(i).getPresen();
        } else if ("vUnitario".equals(jrf.getName())) {
            valor = Float.toString(pedido.get(i).getPrecioU());
        } else if ("Vtotal".equals(jrf.getName())) {
            valor = Float.toString(pedido.get(i).getvTotal());
        }

        return valor;
    }

    public static File metodo(HashMap parametros, ArrayList<itemsOrdenCompra> pedido) {
        // TODO code application logic here

        InputStream inputStream = null;
        JasperPrint jasperPrint = null;
        fdc002 datasource = new fdc002();
        File pdf=null;
        for (itemsOrdenCompra pp : pedido) {
            datasource.addAsistente(pp);
        }
        /*for (int i = 0; i <= 5; i++) {

         itemsOrdenCompra asist;
         asist = new itemsOrdenCompra("j", "j", "j", i, "j", i, "j", BigDecimal.ZERO);
         datasource.addAsistente(asist);

         }*/

        try {
            inputStream = new FileInputStream("E:\\Final Version\\Biot_Server\\src\\Logica\\Reportes\\fdc002.jrxml");
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error al leer el fichero de carga jasper report " + ex.getMessage());
        }

        try {

            JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, datasource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, "src/reporte01.pdf");
            pdf = new File("src/reporte01.pdf");
            try {
                Desktop.getDesktop().open(pdf);
            } catch (IOException ex) {
                Logger.getLogger(fdc002.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar fichero jrml jasper report " + e.getMessage());
        }
        return pdf;
    }

}
