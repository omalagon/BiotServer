/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import EstructurasAux.BuscarUsuario;
import EstructurasAux.solicitudPr;
import EstructurasAux.ItemInventario;
import EstructurasAux.aprobacion;
import EstructurasAux.cotizaciones;
import EstructurasAux.descargo;
import EstructurasAux.fdc_001;
import EstructurasAux.informeDescargos;
import EstructurasAux.itemRecep;
import EstructurasAux.itemsOrdenCompra;
import EstructurasAux.itemsfdc_001;
import EstructurasAux.itemxproveedor;
import EstructurasAux.proveedor;
import EstructurasAux.recepcionProd;
import EstructurasAux.users;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.InputStream;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author Oscar Dario Malagon Murcia
 */
public class Usuario extends UnicastRemoteObject implements interfaces.Usuario, Serializable {

    public Usuario() throws RemoteException {
        super();
    }

    //Metodos del administrador
    @Override
    public boolean crearRA(String identificacion, String nombre,
            String correo, String psw, String cargo, String area, BigDecimal id) throws RemoteException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        boolean creado = false;
        psw = this.encriptar(psw);
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement("INSERT INTO ra (LAB, CARGO, ID, NOMBRE, CORREO, PSW, ID_DA) VALUES(?,?,?,?,?,?,?)");
            ps.setString(1, area);
            ps.setString(2, cargo);
            ps.setBigDecimal(3, new BigDecimal(identificacion));
            ps.setString(4, nombre);
            ps.setString(5, correo);
            ps.setString(6, psw);
            ps.setBigDecimal(7, id);
            ps.executeUpdate();
            creado = true;
        } catch (SQLException ex) {
            creado = false;
            System.out.println("Error en la función \"Crear RA\"");

        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexión");
            }
        }

        return creado;
    }

    @Override
    public boolean crearAO(String identificacion, String nombre, String correo,
            String psw, BigDecimal id) throws RemoteException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        boolean creado = false;
        psw = this.encriptar(psw);
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement("INSERT INTO ao (ID, NOMBRE, CORREO, PSW, ID_DA) VALUES (?,?,?,?,?)");
            ps.setBigDecimal(1, new BigDecimal(identificacion));
            ps.setString(2, nombre);
            ps.setString(3, correo);
            ps.setString(4, psw);
            ps.setBigDecimal(5, id);
            ps.executeUpdate();
            creado = true;
        } catch (SQLException ex) {
            creado = false;
            System.out.println("Error en la función \"Crear AO\"");
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexión");
            }
        }

        return creado;
    }

    @Override
    public ArrayList<ItemInventario> itemInventarioAdmin() throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String numero;
        String lab;
        String descripcion;
        String presentacion;
        float cantidad;
        float precio;
        String cCalidad;
        String cEsp;
        String sucursal;
        ItemInventario in;
        ArrayList<ItemInventario> lista = new ArrayList<>();
        try {
            con = Conexion.conexion.getConnection();
            String statement = "select * from Item order by inventario asc, cinterno";
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                numero = rs.getString("CINTERNO");
                lab = rs.getString("INVENTARIO");
                descripcion = rs.getString("DESCRIPCION");
                presentacion = rs.getString("PRESENTACION");
                cantidad = rs.getFloat("CANTIDAD");
                precio = rs.getFloat("PRECIO");
                cCalidad = rs.getString("CCALIDAD");
                cEsp = rs.getString("CESP");
                sucursal = rs.getString("SUCURSAL");
                in = new ItemInventario(numero, lab, descripcion, presentacion, cantidad, precio, cCalidad, cEsp, sucursal, new Float(0));
                lista.add(in);
            }
        } catch (SQLException ex) {
            System.out.println("Error en la función \"Crear itemInventario\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando la conexión");
            }
        }

        return lista;
    }

    @Override
    public ArrayList<ItemInventario> ultimos() throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String numero;
        String lab;
        String descripcion;
        String presentacion;
        float cantidad;
        float precio;
        String cCalidad;
        String cEsp;
        String sucursal;
        ItemInventario in;
        ArrayList<ItemInventario> lista = new ArrayList<>();
        try {
            con = Conexion.conexion.getConnection();
            String statement = "(select * from item where cinterno like '%MMC%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%MT%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%AS%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%B%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%ER%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%I%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%MT%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%MV%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%N%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%PL%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%T%' order by CINTERNO desc limit 1)\n"
                    + "union\n"
                    + "(select * from item where cinterno like '%V%' order by CINTERNO desc limit 1)";
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                numero = rs.getString("CINTERNO");
                lab = rs.getString("INVENTARIO");
                descripcion = rs.getString("DESCRIPCION");
                presentacion = rs.getString("PRESENTACION");
                cantidad = rs.getFloat("CANTIDAD");
                precio = rs.getFloat("PRECIO");
                cCalidad = rs.getString("CCALIDAD");
                cEsp = rs.getString("CESP");
                sucursal = rs.getString("SUCURSAL");
                in = new ItemInventario(numero, lab, descripcion, presentacion, cantidad, precio, cCalidad, cEsp, sucursal, new Float(0));
                lista.add(in);
            }
        } catch (SQLException ex) {
            System.out.println("Error en la función \"Crear itemInventario\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando la conexión");
            }
        }

        return lista;

    }

    @Override
    public ArrayList<cotizaciones> getCotizaciones(String parametro) throws RemoteException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        String statement;
        String statement2;
        String statement3;
        String statement4;
        String statement5;
        String statement6;
        ArrayList<cotizaciones> cot = new ArrayList<>();
        cotizaciones c = null;
        try {
            con = Conexion.conexion.getConnection();
            statement = "create table part1 (select cp.COTIZACION_ID as cotID, a.NOMBRE as AONom, r.NOMBRE as RANom, cp.PROVEEDOR_NIT as pNit, cp.ITEM_INVENTARIO as lab, cp.ITEM_CINTERNO as itm, cp.PRECIO_U as pre, sp.NUM_SOL as nSol, cp.REVISADA as revi\n"
                    + "from COTIZACION_PROD cp, SOLICITUDPR sp, AO a, ra r\n"
                    + "where sp.NUM_SOL= cp.NUM_SOL and a.ID = cp.AO_ID  and A.ID = sp.AO_ID and r.ID = sp.RA_ID and cp.ENORDEN= 'NO');";
            statement2 = "create view part2 as (select cotid, aonom, ranom, pnit, lab, itm, pre, nsol, sum(cantidadsol) from part1, itxsol where nsol = itxsol.num_sol and itm = itxsol.item_cinterno and revi = '" + parametro + "' )";
            statement3 = "create view part3 as(select cotid, aonom, ranom, pnit, lab, itm, pre, nsol, cantidadsol from part1, itxsol where nsol = itxsol.num_sol and itm = itxsol.item_cinterno and revi = '" + parametro + "' )";
            //statement2 = "select distinct cotid, aonom, ranom, pnit, lab, itm, pre, nsol, cantidadsol from part1, itxsol where nsol = itxsol.num_sol and lab = itxsol.item_inventario and itm = itxsol.item_cinterno and revi = ?";
            statement4 = "select * from part2\n"
                    + "union\n"
                    + "select * from part3 where part3.cotid <>(select cotid from part2);";
            statement5 = "create view part4 as ((select cp.COTIZACION_ID as cotID, a.NOMBRE as AONom, d.NOMBRE as RANom, cp.PROVEEDOR_NIT as pNit, cp.ITEM_INVENTARIO as lab, cp.ITEM_CINTERNO as itm, cp.PRECIO_U as pre, sp.NUM_SOL as nSol, cp.REVISADA as revi\n"
                    + "from COTIZACION_PROD cp, SOLICITUDPR sp, AO a, da d\n"
                    + "where sp.NUM_SOL= cp.NUM_SOL and a.ID = cp.AO_ID  and A.ID = sp.AO_ID and d.ID_DA = sp.DA_ID and cp.ENORDEN= 'NO'));";
            statement6 = "insert into part1(select *from part4);";
            ps = con.prepareStatement(statement);
            ps.executeUpdate(statement);
            ps = con.prepareStatement(statement2);
            ps.executeUpdate(statement2);
            ps = con.prepareStatement(statement3);
            ps.executeUpdate(statement3);
            ps = con.prepareStatement(statement5);
            ps.executeUpdate(statement5);
            ps = con.prepareStatement(statement6);
            ps.executeUpdate(statement6);
            ps = con.prepareStatement(statement4);
            //ps.setString(1, parametro);
            rs = ps.executeQuery();
            System.out.println(statement);
            System.out.println(statement2);
            System.out.println(statement3);
            System.out.println(statement4);
            while (rs.next()) {
                c = new cotizaciones(rs.getBigDecimal(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getFloat(7), rs.getBigDecimal(8), rs.getFloat(9), new Float(-1));
                cot.add(c);
                System.out.println("hola");
            }
            ps.executeUpdate("drop table part1;");
            ps.executeUpdate("drop view part2;");
            ps.executeUpdate("drop view part3;");
            ps.executeUpdate("drop view part4;");
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("Error en la función \"Crear getCotizaciones\"");
        }

        return cot;
    }

    @Override
    public ArrayList<users> getUsuarios() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        String statement;
        ArrayList<users> user = new ArrayList<>();
        users us = null;
        try {
            con = Conexion.conexion.getConnection();
            statement = "select id, nombre, correo, cargo, lab from ra \n"
                    + "union\n"
                    + "select id, nombre, correo, 'Aux Oficina' as cargo, ' ' as lab from ao";
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                us = new users(rs.getBigDecimal(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                user.add(us);
            }
        } catch (SQLException ex) {
            System.out.println("Error en la función \"Crear getCotizaciones\"");
        }
        return user;
    }

    @Override
    public boolean aprobar(aprobacion ap, String par) throws RemoteException {
        Connection con = null;
        boolean validacion = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "";

        try {
            con = Conexion.conexion.getConnection();
            statement = "INSERT INTO APROBADOS (COTIZACION_ID, ITEM_CINTERNO, ITEM_INVENTARIO, CAPROBADA, FECHAAPROB, ID_DA) VALUES (?,?,?,?,?,?)";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, ap.getIdCot());
            ps.setString(2, ap.getCodigo());
            ps.setString(3, ap.getLab());
            ps.setFloat(4, ap.getAprobado());
            ps.setDate(5, new Date(ap.getFecha().getTimeInMillis()));
            ps.setBigDecimal(6, ap.getIdDA());
            ps.executeUpdate();
            validacion = true;
            this.actCot(ap, par);
        } catch (SQLException ex) {
            System.out.println("Error en el metodo \"aprobar\"");
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return validacion;
    }

    @Override
    public boolean actCot(aprobacion ap, String parametro) throws RemoteException {
        Connection con = null;
        boolean validacion = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "";
        try {
            con = Conexion.conexion.getConnection();
            statement = "UPDATE COTIZACION_PROD SET REVISADA = ? where cotizacion_id = ?  and item_cinterno =?";
            ps = con.prepareStatement(statement);
            ps.setString(1, parametro);
            ps.setBigDecimal(2, ap.getIdCot());
            ps.setString(3, ap.getCodigo());
            ps.executeUpdate();
            validacion = true;
        } catch (SQLException ex) {
            System.out.println("Error metodo \"actCot\"");
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return validacion;
    }

    @Override
    public boolean eliminarAprobacion(aprobacion ap) {
        Connection con = null;
        boolean validacion = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "";
        try {
            con = Conexion.conexion.getConnection();
            statement = "delete from aprobados where cotizacion_id = ? and item_cinterno =?";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, ap.getIdCot());
            ps.setString(2, ap.getCodigo());
            ps.executeUpdate();
            this.actCot(ap, "NO");
            validacion = true;
        } catch (SQLException | RemoteException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en el metodo \"eliminar aprobacion\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return validacion;

    }

    @Override
    public float getCantAprobada(cotizaciones c) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = new String();
        float cAprobada = 0;
        try {
            con = Conexion.conexion.getConnection();
            statement = "select caprobada "
                    + "from APROBADOS "
                    + "where COTIZACION_ID = ? and ITEM_CINTERNO = ?";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, c.getCotizacionId());
            ps.setString(2, c.getCinterno());
            rs = ps.executeQuery();
            while (rs.next()) {
                cAprobada = rs.getFloat(1);
            }
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            //System.out.println(rs.getFloat(1));
        } catch (SQLException ex) {
            System.out.println("Error en la funcion \"getCantAprobada\"");
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        System.out.println(cAprobada);
        return cAprobada;
    }

    @Override
    public float getCantSolicitada(String codigo, String lab, BigDecimal numSol) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "";
        float cSolicitada = 0;
        try {
            con = Conexion.conexion.getConnection();
            statement = "select CANTIDADSOL from itxsol where ITEM_CINTERNO = ?  and num_sol = ?";
            ps = con.prepareStatement(statement);
            ps.setString(1, codigo);
            ps.setBigDecimal(2, numSol);
            rs = ps.executeQuery();
            rs.next();
            cSolicitada = rs.getFloat(1);
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return cSolicitada;
    }

    @Override
    public fdc_001 datosItem(BigDecimal numSol, fdc_001 aux) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<itemsfdc_001> articulos = new ArrayList<>();
        itemsfdc_001 item = null;
        String statement1 = "create view datosItem1_" + numSol + " as (select i.CANTIDAD as Cantidad, i.INVENTARIO as Lab, i.CINTERNO as Codigo, i.DESCRIPCION as Descr,it.CANTIDADSOL as CSol, i.PRESENTACION as Pres, it.NUM_SOL as NumSol from ITXSOL it \n"
                + "left outer join ITEM i \n"
                + "on IT.ITEM_CINTERNO = I.CINTERNO);\n";

        String statement2 = "create view datosItem2_" + numSol + " as (select P.Cantidad, P.Lab, P.Codigo, P.Descr, P.CSol, P.Pres, P.NumSol, c.COTIZACION_ID  as coti, c.precio_U as precio\n"
                + "from datosItem1_" + numSol + " p , COTIZACION_PROD c \n"
                + "where p.Codigo = c.ITEM_CINTERNO and c.REVISADA = 'SI' and p.NumSol = c.NUM_SOL);";
        String statement3 = "select P.Cantidad, P.Lab, P.Codigo, P.Descr, P.CSol, P.Pres,APROBADOS.CAPROBADA,  p.precio,P.NumSol,p.coti"
                + " from datosItem2_" + numSol + " p, APROBADOS "
                + "where p.coti = APROBADOS.COTIZACION_ID and p.numsol = ?";
        System.out.println(statement1);
        System.out.println(statement2);
        System.out.println(statement3);
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement1);
            ps.executeUpdate();
            ps = con.prepareStatement(statement2);
            ps.executeUpdate();
            ps = con.prepareStatement(statement3);
            ps.setBigDecimal(1, numSol);
            rs = ps.executeQuery();
            while (rs.next()) {
                item = new itemsfdc_001(rs.getFloat(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getFloat(5), rs.getString(6), rs.getFloat(7), rs.getFloat(8), rs.getBigDecimal(9));
                articulos.add(item);

            }
            aux.setArticulos(articulos);
            ps.executeUpdate("drop view datosItem1_" + numSol + ";");
            ps.executeUpdate("drop view datosItem2_" + numSol + ";");
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return aux;

    }
    /*
     Retorna un objeto tipo fdc_001 que contiene: fecha, area o proceso solicitante, nombre del solicitante,
     observaciones y el auxiliar de oficina
     */

    @Override
    public ArrayList<informeDescargos> generarInforme(String mes) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "create view gInforme1 as(select i.CINTERNO, i.INVENTARIO, i.DESCRIPCION, i.CANTIDAD as enInventario, d.FECHA as fecha, (select nombre from ra where id = d.id) as nombre ,d.ID, d.AREA\n"
                + "from item  i right outer join DESCARGOS d\n"
                + "on i.CINTERNO = d.CINTERNO );";
        String statement2 = "create view gInforme2 as( select cinterno, inventario, descripcion, eninventario, fecha, nombre, id, area from gInforme1 where gInforme1.fecha like ?);";
        String statement3 = "create view gInforme3 as(select d.CINTERNO, d.INVENTARIO,sum(d.cantidad) as suma from descargos d, descargos dd where dd.NUMDESCARGO = d.NUMDESCARGO group by d.INVENTARIO, d.CINTERNO)";
        String statement4 = "select distinct  p2.cinterno, p2.inventario, p2.descripcion, p2.eninventario, p3.suma, p2.nombre, p2.id, p2.area\n"
                + " from gInforme2 p2 , gInforme3 p3 where p2.cinterno = p3.CINTERNO";
        informeDescargos fila = null;
        ArrayList<informeDescargos> listado = new ArrayList<>();
        try {
            con = Conexion.conexion.getConnection();
            mes = "%-" + mes + "-%";
            ps = con.prepareStatement(statement);
            ps.executeUpdate();
            ps = con.prepareStatement(statement2);
            ps.setString(1, mes);
            ps.executeUpdate();
            ps = con.prepareStatement(statement3);
            ps.executeUpdate();
            ps = con.prepareStatement(statement4);
            rs = ps.executeQuery();
            while (rs.next()) {
                fila = new informeDescargos(rs.getString(1), rs.getString(2), rs.getString(3), rs.getFloat(4), rs.getFloat(5), rs.getString(6), rs.getBigDecimal(7), rs.getString(8));
                listado.add(fila);
            }
            ps.executeUpdate("drop view gInforme1;");
            ps.executeUpdate("drop view gInforme2;");
            ps.executeUpdate("drop view gInforme3;");
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return listado;
    }

    @Override
    public ArrayList<informeDescargos> generarInformePorLab(String mes) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select DESCARGOS.CINTERNO, DESCARGOS.INVENTARIO, item.DESCRIPCION, item.CANTIDAD, sum(DESCARGOS.CANTIDAD) \n"
                + "from item right outer join DESCARGOS \n"
                + "on item.CINTERNO = DESCARGOS.CINTERNO and DESCARGOS.FECHA like ?\n"
                + "group by DESCARGOS.CINTERNO, DESCARGOS.INVENTARIO, item.DESCRIPCION, item.CANTIDAD\n"
                + "order by  DESCARGOS.INVENTARIO || DESCARGOS.CINTERNO";
        informeDescargos fila = null;
        System.out.println(statement);
        ArrayList<informeDescargos> listado = new ArrayList<>();
        try {
            con = Conexion.conexion.getConnection();
            mes = "%-" + mes + "-%";
            ps = con.prepareStatement(statement);
            ps.setString(1, mes);
            rs = ps.executeQuery();
            while (rs.next()) {
                fila = new informeDescargos(rs.getString(1), rs.getString(2), rs.getString(3), rs.getFloat(4), rs.getFloat(5));
                listado.add(fila);
                System.out.println(fila.getCinterno());
                System.out.println(fila.getEninventario());
                System.out.println(fila.getInventario());
                System.out.println(fila.getDescripcion());
                System.out.println(fila.getEmpleado());
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return listado;
    }

    @Override
    public ArrayList<informeDescargos> generarInformePorRA(String area, BigDecimal id) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select DESCARGOS.CINTERNO, DESCARGOS.INVENTARIO, item.DESCRIPCION, item.CANTIDAD, sum(DESCARGOS.CANTIDAD), DESCARGOS.FECHA\n"
                + "from item, DESCARGOS \n"
                + "where item.CINTERNO = DESCARGOS.CINTERNO and DESCARGOS.ID = ?\n"
                + "group by DESCARGOS.CINTERNO, DESCARGOS.INVENTARIO, item.DESCRIPCION, item.CANTIDAD, DESCARGOS.FECHA\n"
                + "order by  DESCARGOS.INVENTARIO || DESCARGOS.CINTERNO";
        informeDescargos fila = null;
        System.out.println(statement);
        ArrayList<informeDescargos> listado = new ArrayList<>();
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(rs.getDate(6));
                fila = new informeDescargos(rs.getString(1), rs.getString(2), rs.getString(3), rs.getFloat(4), rs.getFloat(5), c);
                listado.add(fila);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return listado;
    }

    @Override
    public boolean CrearProveedor(String NIT, String Nombre, String direccion, String telefono, String telefax, String ciudad, String pais) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement1 = "INSERT INTO PROVEEDOR (NIT, NOMBRE) VALUES (?,?)";
        String statement2 = "INSERT INTO DATOS (DIR, PROVEEDOR_NIT, TELEFONO, TELEFAX, CIUDAD, PAIS) VALUES (?,?,?,?,?,?)";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement1);
            ps.setString(1, NIT);
            ps.setString(2, Nombre);
            ps.executeUpdate();
            ps = con.prepareStatement(statement2);
            ps.setString(1, direccion);
            ps.setString(2, NIT);
            ps.setString(3, telefono);
            ps.setString(4, telefax);
            ps.setString(5, ciudad);
            ps.setString(6, pais);
            ps.executeUpdate();
            valido = true;
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return valido;
    }
    
    @Override
    public boolean EditarProveedor(String NIT, String Nombre, String direccion, String telefono, String telefax, String ciudad, String pais) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement1 = "UPDATE proveedor SET NOMBRE = ? WHERE NIT = ?;";
        String statement2 = "UPDATE datos SET DIR = ?,PROVEEDOR_NIT = ?, TELEFONO = ?,TELEFAX = ?,CIUDAD = ?,PAIS = ? WHERE PROVEEDOR_NIT = ?";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement1);
            ps.setString(2, NIT);
            ps.setString(1, Nombre);
            ps.executeUpdate();
            ps = con.prepareStatement(statement2);
            ps.setString(1, direccion);
            ps.setString(2, NIT);
            ps.setString(3, telefono);
            ps.setString(4, telefax);
            ps.setString(5, ciudad);
            ps.setString(6, pais);
            ps.setString(7, NIT);
            ps.executeUpdate();
            valido = true;
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return valido;
    }

    @Override
    public fdc_001 datosGenerales(BigDecimal numSol, BigDecimal numCot) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        fdc_001 archivo = null;
        ArrayList<itemxproveedor> arr = new ArrayList<>();
        ArrayList<ArrayList<itemxproveedor>> matrizProv = new ArrayList<>();
        String statement = "(select s.FECHA,r.LAB,r.NOMBRE,r.CARGO,s.OBSERVACIONES,a.NOMBRE \n"
                + "from SOLICITUDPR s, ra r, ao a \n"
                + "where r.ID = S.RA_ID and s.AO_ID= a.ID)\n"
                + "union\n"
                + "(select s.FECHA,\"Compras\",d.NOMBRE,\"DA\",s.OBSERVACIONES,a.NOMBRE \n"
                + "from SOLICITUDPR s, da d, ao a \n"
                + "where d.ID_DA = S.DA_ID and s.AO_ID= a.ID)";
        System.out.println(statement);
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            rs.next();
            archivo = new fdc_001(rs.getDate(1).toString(), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), numCot, numSol);
            archivo = this.datosItem(numSol, archivo);
            for (itemsfdc_001 i : archivo.getArticulos()) {
                arr = this.getItemxproveedor(i.getLab(), i.getCodigo());
                matrizProv.add(arr);
            }
            archivo.setProveedores(matrizProv);
            fdc_001.toString(archivo);
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return archivo;
    }

    @Override
    public boolean crearDA(String id, String nombre, String correo, String psw) throws RemoteException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        boolean creado = false;
        psw = this.encriptar(psw);
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement("INSERT INTO da VALUES(?,?,?,?)");
            ps.setBigDecimal(1, new BigDecimal(id));
            ps.setString(2, nombre);
            ps.setString(3, correo);
            ps.setString(4, psw);
            ps.executeUpdate();
            creado = true;
        } catch (SQLException ex) {
            creado = false;
            System.out.println("Error en la función \"Crear DA\"");

        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexión");
            }
        }

        return creado;
    }

    @Override
    public String getNombreDA(String id) throws RemoteException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        String nombre = "";
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement("select nombre from da where id_da = ?");
            ps.setBigDecimal(1, new BigDecimal(id));
            rs = ps.executeQuery();
            rs.next();
            nombre = rs.getString(1);
        } catch (SQLException ex) {
            System.out.println("Error en la función \"Crear RA\"");

        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexión");
            }
        }

        return nombre;
    }

    @Override
    public String getNombreRA(String id) throws RemoteException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        String nombre = "";
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement("select nombre from ra where id = ?");
            ps.setBigDecimal(1, new BigDecimal(id));
            rs = ps.executeQuery();
            rs.next();
            nombre = rs.getString(1);
        } catch (SQLException ex) {
            System.out.println("Error en la función \"Crear RA\"");

        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexión");
            }
        }

        return nombre;
    }

    @Override
    public String getNombreAO(String id) throws RemoteException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        String nombre = "";
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement("select nombre from ao where id = ?");
            ps.setBigDecimal(1, new BigDecimal(id));
            rs = ps.executeQuery();
            rs.next();
            nombre = rs.getString(1);
        } catch (SQLException ex) {
            System.out.println("Error en la función \"Crear RA\"");

        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexión");
            }
        }

        return nombre;
    }

    //Va a presentar problemas
    @Override
    public boolean crearItem(ItemInventario item) throws RemoteException {
        PreparedStatement ps = null;
        Connection con = null;
        String statement = "insert into item (inventario, descripcion, presentacion, cantidad, precio, ccalidad, sucursal, cesp, cinterno) values(?,?,?,?,?,?,?,?,?);";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, item.getInventario());
            ps.setString(2, item.getDescripcion());
            ps.setString(3, item.getPresentacion());
            ps.setFloat(4, item.getCantidad());
            ps.setFloat(5, item.getPrecio());
            ps.setString(6, item.getcCalidad());
            ps.setString(7, item.getSucursal());
            ps.setString(8, item.getCEsp());
            ps.setString(9, item.getNumero());
            ps.executeUpdate();
            valido = true;
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexión");
            }

        }
        return valido;
    }
    
    @Override
    public boolean editarItem(ItemInventario item) throws RemoteException {
        PreparedStatement ps = null;
        Connection con = null;
        String statement ="update item set inventario = ?, descripcion = ? , presentacion = ? , cantidad = ? , precio =?, ccalidad= ?, cesp =?, sucursal =? where cinterno =?;";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, item.getInventario());
            ps.setString(2, item.getDescripcion());
            ps.setString(3, item.getPresentacion());
            ps.setFloat(4, item.getCantidad());
            ps.setFloat(5, item.getPrecio());
            ps.setString(6, item.getcCalidad());
            ps.setString(7, item.getSucursal());
            ps.setString(8, item.getCEsp());
            ps.setString(9, item.getNumero());
            ps.executeUpdate();
            valido = true;
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexión");
            }

        }
        return valido;
    }

    @Override
    public ItemInventario buscarInfoItem(String cinterno) throws RemoteException{
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs = null;
        ItemInventario  item = null;
        String statement = "SELECT inventario, descripcion, presentacion, cantidad, precio, ccalidad, cesp, sucursal FROM item where cinterno = ?;";
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, cinterno);
            rs =ps.executeQuery();
            while(rs.next())
                item = new ItemInventario(null, rs.getString(2), rs.getString(3), rs.getFloat(4), rs.getFloat(5), rs.getString(6), rs.getString(1), rs.getString(8), rs.getString(7));
            
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexión");
            }

        }
        return item;
    }
    
    @Override
    public ArrayList<BuscarUsuario> buscarEmpleado(String parametro, String valor) throws RemoteException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        String statement = "select id, nombre, lab from ra where id like ?";
        String statement2 = "select id, nombre, lab from ra where nombre like ?";
        BuscarUsuario buscar = null;
        ArrayList<BuscarUsuario> lista = new ArrayList<>();
        try {
            con = Conexion.conexion.getConnection();
            if (parametro.equalsIgnoreCase("nombre")) {
                ps = con.prepareStatement(statement2);
            } else if (parametro.equalsIgnoreCase("id")) {
                ps = con.prepareStatement(statement);
            }
            valor = "%" + valor + "%";
            ps.setString(1, valor);
            System.out.println(statement);
            System.out.println(statement2);
            System.out.println(valor);
            rs = ps.executeQuery();
            while (rs.next()) {
                buscar = new BuscarUsuario(rs.getString(2), rs.getBigDecimal(1), rs.getString(3));
                System.out.println(rs.getString(2) + rs.getBigDecimal(1) + rs.getString(3));
                lista.add(buscar);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lista;
    }

    @Override
    public boolean asociarItem(String cinterno, String NIT, String labo, String precio) throws RemoteException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        boolean creado = false;
        String id = "";
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement("select * from item where cinterno = ? ;");
            ps.setString(1, cinterno);
            rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getString(1);
            }
            ps = con.prepareStatement("insert into ixp values(?,?,?,?,0);");
            ps.setString(1, NIT);
            ps.setString(2, id);
            ps.setString(3, labo);
            ps.setString(4, precio);
            ps.executeUpdate();
            creado = true;
        } catch (SQLException ex) {
            creado = false;
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en la función \"Crear itmxp\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexión");
            }
        }
        return creado;
    }

    @Override
    public File imprimirProveedores(String ruta) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
        java.util.Date date = new java.util.Date();

        File xls = new File(ruta + "\\ListaProveedores" + dateFormat.format(date) + ".xls");
        if (!xls.exists()) {
            try {
                xls.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Workbook libro = new HSSFWorkbook();
        FileOutputStream archivo;
        Sheet hoja;
        ArrayList<proveedor> todos;
        try {
            xls.createNewFile();
            archivo = new FileOutputStream(xls);
            hoja = libro.createSheet("Lista_Proveedores");
            todos = this.todosProveedores();
            int i = 0;
            for (proveedor t : todos) {
                Row fila = hoja.createRow(i);
                Cell aux;
                for (int j = 0; j < 7; j++) {
                    if (i == 0) {
                        aux = fila.createCell(j);
                        aux.setCellValue("NIT");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Nombre");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Dirección");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Teléfono");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Fax");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Celular");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Correo");
                        j++;
                    } else {
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getNIT());
                        hoja.autoSizeColumn(j);

                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getNombre());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getDireccion());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getTelefono());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getTelefax());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getCelular());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getCorreo());
                        hoja.autoSizeColumn(j);
                        j++;

                    }

                }
                i++;
            }

            libro.write(archivo);
            archivo.close();

        } catch (IOException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }

        return xls;
    }

    @Override
    public File imprimirInventario(String ruta) throws RemoteException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
        java.util.Date date = new java.util.Date();

        File xls = new File(ruta + "\\Inventario" + dateFormat.format(date) + ".xls");
        if (!xls.exists()) {
            try {
                xls.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Workbook libro = new HSSFWorkbook();
        FileOutputStream archivo;
        Sheet hoja;
        ArrayList<ItemInventario> todos;
        try {
            archivo = new FileOutputStream(xls);
            hoja = libro.createSheet("Inventario");
            todos = this.itemInventarioAdmin();
            int i = 0;
            for (ItemInventario t : todos) {
                Row fila = hoja.createRow(i);
                Cell aux;
                for (int j = 0; j < 8; j++) {
                    if (i == 0) {
                        aux = fila.createCell(j);
                        aux.setCellValue("Código");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Descripción");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Presentación");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Cantidad");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Precio");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Cert. Calidad");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Cump. Especificaciones");
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue("Sucursal");
                        j++;
                    } else {

                        aux = fila.createCell(j);
                        aux.setCellValue(t.getNumero());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getDescripcion());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getPresentacion());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getCantidad());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getPrecio());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getcCalidad());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getCEsp());
                        hoja.autoSizeColumn(j);
                        j++;
                        aux = fila.createCell(j);
                        aux.setCellValue(t.getSucursal());
                        hoja.autoSizeColumn(j);
                        j++;

                        System.out.println(t.getNumero());
                        System.out.println(t.getDescripcion());
                        System.out.println(t.getPresentacion());

                    }

                }
                i++;
            }

            libro.write(archivo);
            archivo.close();

        } catch (IOException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }

        return xls;
    }

    @Override
    public ArrayList<BigDecimal> numsSol() throws RemoteException {
        /*Connection conex = null;
         PreparedStatement ps = null;
         ResultSet rs = null;
         String statement = "select NUM_SOL from solicitudpr";
         ArrayList<BigDecimal> solicitudes = new ArrayList<>();
         ArrayList<ArrayList<ItemInventario>> todas = new ArrayList<>();
         try {
         conex = Conexion.conexion.getConnection();
         ps = conex.prepareStatement(statement);
         rs = ps.executeQuery();
         while (rs.next()) {
         solicitudes.add(rs.getBigDecimal(1));
         }
         for (BigDecimal s : solicitudes) {
         todas.add(this.getItems_numSol(s));
         System.out.println(s);
         }
         */
        ArrayList<solicitudPr> noRevisadas = this.getNoRevisadas();
        ArrayList<solicitudPr> revisadas = this.getRevisadas();
        GregorianCalendar fecha = new GregorianCalendar();
        Object[] datos = new Object[6];
        for (solicitudPr s : revisadas) {
            datos[0] = s.getSolicitudpr_id();
            datos[1] = fecha.get(Calendar.DAY_OF_MONTH) + "/" + (fecha.get(Calendar.MONTH) + 1) + "/" + fecha.get(Calendar.YEAR);
            datos[2] = s.getObservaciones();
            datos[3] = s.getNombreRA();
            datos[4] = "SI";
            datos[5] = this.getItems_numSol(s.getSolicitudpr_id());
        }
        for (solicitudPr s : noRevisadas) {
            datos[0] = s.getSolicitudpr_id();
            datos[1] = fecha.get(Calendar.DAY_OF_MONTH) + "/" + (fecha.get(Calendar.MONTH) + 1) + "/" + fecha.get(Calendar.YEAR);
            datos[2] = s.getObservaciones();
            datos[3] = s.getNombreRA();
            datos[4] = "NO";
            datos[5] = this.getItems_numSol(s.getSolicitudpr_id());
        }
        for (Object dato : datos) {
            System.out.println(dato);
        }

        return null;
    }

    //Metodos del usuario
    @Override
    public boolean validarTipoUsuario(String identificacion, String contrasena, String tipo) throws RemoteException {
        Connection conex = null;
        boolean validacion = false;
        String identificador = "id";
        PreparedStatement ps = null;
        ResultSet rs = null;
        contrasena = this.encriptar(contrasena);
        System.out.println(contrasena);
        if (tipo.length() > 2 && identificador.length() > 5) {
            validacion = false;
        }
        try {
            conex = Conexion.conexion.getConnection();
            if (tipo.equalsIgnoreCase("da")) {
                identificador = "id_da";
            }
            if (identificacion.isEmpty() == false || contrasena.isEmpty() == false || tipo.isEmpty() == false) {
                String statement;
                statement = "select nombre from " + tipo + " where " + identificador + " = ? and psw= ?";
                ps = conex.prepareStatement(statement);
                ps.setBigDecimal(1, new BigDecimal(identificacion));
                ps.setString(2, contrasena);
                rs = ps.executeQuery();
                rs.next();
                if (rs.getRow() != 0) {
                    validacion = true;
                }
            }
            System.out.println(contrasena);
        } catch (SQLException ex) {
            validacion = false;
            System.out.println(ex);
            System.out.println("Error funcion \"Validad tipo usuario\" ");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conex != null) {
                    conex.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }

        return validacion;
    }

    @Override
    public String area(BigDecimal id) throws RemoteException {
        String area = new String();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.conexion.getConnection();
            if (con == null) {
                System.out.println("Conexion mala");
            }
            String statement = "Select lab from ra where id = ?";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, id);
            rs = ps.executeQuery();
            rs.next();
            area = rs.getString(1);
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);

            System.out.println("Error funcion \"area\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }

        return area;
    }

    @Override
    public ArrayList<ItemInventario> itemInventario(BigDecimal id) throws RemoteException {
        String sector = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String numero;
        String descripcion;
        String presentacion;
        float cantidad;
        ItemInventario in;
        String statement = "select CINTERNO, DESCRIPCION, "
                + "PRESENTACION, CANTIDAD from Item where INVENTARIO = ?";
        ArrayList<ItemInventario> lista = new ArrayList<>();
        try {
            sector = this.area(id);

        } catch (RemoteException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            con = Conexion.conexion.getConnection();
            if (sector.equalsIgnoreCase("MA") || sector.equalsIgnoreCase("MB") || sector.equalsIgnoreCase("FQ")) {
                statement = "SELECT CINTERNO, DESCRIPCION, PRESENTACION, CANTIDAD"
                        + " FROM ITEM where INVENTARIO like '%MA%' or INVENTARIO like '%MB%' or INVENTARIO like '%FQ%'";
                ps = con.prepareStatement(statement);
            } else if (sector.equalsIgnoreCase("EQ")) {
                statement = "SELECT CINTERNO, DESCRIPCION, PRESENTACION, CANTIDAD"
                        + " FROM ITEM where INVENTARIO like '%Gene%' or INVENTARIO like '%ompras%'";
                ps = con.prepareStatement(statement);
            } else {
                ps = con.prepareStatement(statement);
                ps.setString(1, sector);
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                numero = rs.getString("CINTERNO");
                descripcion = rs.getString("DESCRIPCION");
                presentacion = rs.getString("PRESENTACION");
                cantidad = rs.getFloat("Cantidad");
                in = new ItemInventario(numero, sector, descripcion, presentacion, cantidad, 0, "", "", "", 0);
                lista.add(in);
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"item inventario\"");
            System.out.println(ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }

        return lista;
    }

    @Override
    public String getUsuario(BigDecimal id, String rol) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = null;
        String nombre = new String();
        try {
            if (rol.equalsIgnoreCase("ra")) {
                statement = "Select nombre from ra where id = ?";
            } else if (rol.equalsIgnoreCase("da")) {
                statement = "Select nombre from da where id_da = ?";
            } else if (rol.equalsIgnoreCase("ao")) {
                statement = "Select nombre from ao where id = ?";
            }
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, id);
            rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getRow() != 0) {
                    nombre = rs.getString("nombre");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"get usuario\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return nombre;
    }

    @Override
    public void crearSolicitud(solicitudPr sol, String rol) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = null;
        Date d = new Date(sol.getFecha().getTimeInMillis());
        try {
            con = Conexion.conexion.getConnection();
            if (rol.equalsIgnoreCase("ra")) {
                statement = "INSERT INTO SOLICITUDPR"
                        + "(FECHA,OBSERVACIONES,RA_ID) VALUES (?, ?,?)";
            } else if (rol.equalsIgnoreCase("da")) {
                statement = "INSERT INTO SOLICITUDPR"
                        + "(FECHA,OBSERVACIONES,DA_ID) VALUES (?, ?,?)";
            }
            ps = con.prepareStatement(statement);
            ps.setDate(1, d);
            ps.setString(2, sol.getObservaciones());
            ps.setBigDecimal(3, sol.getRa_id());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error funcion \"crear solicitud\"");
            System.out.println(ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex);
                System.out.println("Error cerrando conexion");
            }
        }
    }

    @Override
    public BigDecimal solicitudValida(BigDecimal id, String rol) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = null;
        BigDecimal valida = null;
        try {
            con = Conexion.conexion.getConnection();
            if (rol.equalsIgnoreCase("ra")) {
                statement = "select NUM_SOL from SOLICITUDPR where ra_id = ? order by NUM_SOL desc";
            } else if (rol.equalsIgnoreCase("da")) {
                statement = "select NUM_SOL from SOLICITUDPR where da_id = ? order by NUM_SOL desc";
            }

            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, id);
            rs = ps.executeQuery();
            rs.next();
            if (rs.getRow() != 0) {
                valida = rs.getBigDecimal(1);
            }

        } catch (SQLException ex) {
            System.out.println("Error funcion \"solicitud valida\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }

        return valida;
    }

    @Override
    public void itemxsolicitud(ArrayList<ItemInventario> itemsSolicitud, BigDecimal numSol) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        try {
            con = Conexion.conexion.getConnection();
            statement = "INSERT INTO ITXSOL (ITEM_CINTERNO, ITEM_INVENTARIO, NUM_SOL, CANTIDADSOL) "
                    + "VALUES (?,?,?,?)";
            ps = con.prepareStatement(statement);
            for (ItemInventario i : itemsSolicitud) {
                ps.setString(1, i.getNumero());
                ps.setString(2, i.getInventario());
                ps.setBigDecimal(3, numSol);
                ps.setFloat(4, i.getCantidadSolicitada());
                System.out.println(i.getNumero());
                System.out.println(i.getInventario());
                System.out.println(numSol);
                System.out.println(i.getCantidadSolicitada());
                ps.executeUpdate();
                System.out.println("Se registró: " + i.getNumero());
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("Error funcion \"item x solicitud\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
    }

    @Override
    public ArrayList<solicitudPr> getSolicitud_RA(BigDecimal id) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        ArrayList<solicitudPr> solicitudes = new ArrayList<>();
        solicitudPr solicitud = null;
        GregorianCalendar fecha = new GregorianCalendar();
        try {
            con = Conexion.conexion.getConnection();
            statement = "select  NUM_SOL, FECHA, OBSERVACIONES from SOLICITUDPR where RA_ID = ?";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                fecha.setTime(rs.getDate(2));
                solicitud = new solicitudPr(fecha, rs.getString(3), rs.getBigDecimal(1), id, null, null);
                solicitudes.add(solicitud);
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"get solicitud ra\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return solicitudes;
    }

    @Override
    public ArrayList<solicitudPr> getNoRevisadas() throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        ArrayList<solicitudPr> NoRevisadas = new ArrayList<>();
        solicitudPr rev = null;
        GregorianCalendar fecha = new GregorianCalendar();
        try {
            con = Conexion.conexion.getConnection();
            statement = "(select SOLICITUDPR.FECHA, SOLICITUDPR.OBSERVACIONES, SOLICITUDPR.NUM_SOL, ra.NOMBRE, ra.LAB from SOLICITUDPR, ra  where SOLICITUDPR.RA_ID = ra.ID and SOLICITUDPR.REVISADO = 'NO' order by SOLICITUDPR.NUM_SOL)\n"
                    + "union\n"
                    + "(select SOLICITUDPR.FECHA, SOLICITUDPR.OBSERVACIONES, SOLICITUDPR.NUM_SOL, da.nombre, \"DA\" from SOLICITUDPR, da  where SOLICITUDPR.DA_ID = da.ID_DA and SOLICITUDPR.REVISADO = 'NO' order by SOLICITUDPR.NUM_SOL)";
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                fecha.setTime(rs.getDate(1));
                rev = new solicitudPr(fecha, rs.getString(2), rs.getBigDecimal(3), null, rs.getString(4), rs.getString(5));
                NoRevisadas.add(rev);
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"get no revisadas\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }

        return NoRevisadas;
    }

    @Override
    public ArrayList<solicitudPr> getRevisadas() throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        ArrayList<solicitudPr> Revisadas = new ArrayList<>();
        solicitudPr rev = null;
        GregorianCalendar fecha = new GregorianCalendar();
        try {
            con = Conexion.conexion.getConnection();
            statement = "select SOLICITUDPR.FECHA, SOLICITUDPR.OBSERVACIONES, SOLICITUDPR.NUM_SOL, ra.NOMBRE, ra.LAB"
                    + " from SOLICITUDPR, ra "
                    + "where SOLICITUDPR.RA_ID = ra.ID and SOLICITUDPR.REVISADO = 'SI' order by SOLICITUDPR.NUM_SOL";
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                fecha.setTime(rs.getDate(1));
                rev = new solicitudPr(fecha, rs.getString(2), rs.getBigDecimal(3), null, rs.getString(4), rs.getString(5));
                Revisadas.add(rev);
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"get revisadas\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return Revisadas;
    }

    @Override
    public boolean RevisarSolicitud(BigDecimal id, BigDecimal numSol, String ope) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        String statement;
        ArrayList<solicitudPr> Revisadas = new ArrayList<>();
        solicitudPr rev = null;
        boolean act = false;
        try {
            con = Conexion.conexion.getConnection();
            statement = "UPDATE SOLICITUDPR SET REVISADO = ?, AO_ID = ? WHERE num_sol = ?";
            ps = con.prepareStatement(statement);
            ps.setString(1, ope);
            ps.setBigDecimal(2, id);
            ps.setBigDecimal(3, numSol);
            ps.executeUpdate();
            act = true;
            if (ope.equalsIgnoreCase("NO")) {
                eliminarAprobacion(numSol);
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"revisar solicitud\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return act;
    }

    @Override
    public solicitudPr getSolicitud_NumSol(BigDecimal numSol) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        solicitudPr solicitud = null;
        GregorianCalendar fecha = new GregorianCalendar();
        try {
            con = Conexion.conexion.getConnection();
            statement = "(select fecha, OBSERVACIONES, nombre, lab, id from SOLICITUDPR, ra where ra.ID = SOLICITUDPR.RA_ID and num_sol = ?)\n"
                    + "union\n"
                    + "(select fecha, OBSERVACIONES, nombre, \"DA\", ID_DA from SOLICITUDPR, da where da.ID_DA = SOLICITUDPR.DA_ID and num_sol = ?)";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, numSol);
            ps.setBigDecimal(2, numSol);
            rs = ps.executeQuery();
            rs.next();
            fecha.setTime(rs.getDate(1));
            solicitud = new solicitudPr(fecha, rs.getString(2), numSol, rs.getBigDecimal(5), rs.getString(3), rs.getString(4));
        } catch (SQLException ex) {
            System.out.println("Error funcion \"get solicitud numSol\"");
            System.out.println(ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return solicitud;
    }

    @Override
    public ArrayList<ItemInventario> getItems_numSol(BigDecimal numSol) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        ArrayList<ItemInventario> listado = new ArrayList<>();
        ItemInventario item = null;
        try {
            con = Conexion.conexion.getConnection();
            statement = "select cantidad, inventario, cinterno, DESCRIPCION, presentacion,cantidadsol, precio  "
                    + "from item, ITXSOL "
                    + "where item.CINTERNO = ITXSOL.ITEM_CINTERNO  and ITXSOL.NUM_SOL = ?";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, numSol);
            rs = ps.executeQuery();
            while (rs.next()) {
                item = new ItemInventario(rs.getString(3), rs.getString(2), rs.getString(4), rs.getString(5), rs.getFloat(1), rs.getFloat(7), "", "", "", rs.getFloat(6));
                System.out.println(rs.getFloat(6));
                listado.add(item);
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"get items numSol\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return listado;
    }

    @Override
    public ArrayList<itemxproveedor> getItemxproveedor(String inv, String codigo) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        ArrayList<itemxproveedor> listado = new ArrayList<>();
        itemxproveedor item = null;
        try {
            con = Conexion.conexion.getConnection();
            statement = "select PROVEEDOR.NOMBRE, ixp.precio, ixp.DISPONIBILIDAD, PROVEEDOR.NIT "
                    + "from item, ixp, PROVEEDOR "
                    + "where ixp.ITEM_CINTERNO  = ? and"
                    + "      item.CINTERNO = ? and "
                    + "      nit = ixp.PROVEEDOR_NIT";
            ps = con.prepareStatement(statement);
            ps.setString(1, codigo);
            ps.setString(2, codigo);
            rs = ps.executeQuery();
            while (rs.next()) {
                item = new itemxproveedor(rs.getString(4), rs.getString(1), rs.getFloat(2), rs.getFloat(3));
                listado.add(item);
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"get item x proveedor\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return listado;
    }

    @Override
    public boolean generarCotizacion(BigDecimal idAO, String proveedorNit, String codigo, String lab, BigDecimal numSol, float precio) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        ArrayList<solicitudPr> Revisadas = new ArrayList<>();
        solicitudPr rev = null;
        boolean act = false;
        try {
            con = Conexion.conexion.getConnection();
            statement = "INSERT INTO COTIZACION_PROD "
                    + "(AO_ID,PROVEEDOR_NIT, ITEM_CINTERNO, ITEM_INVENTARIO, NUM_SOL, PRECIO_U)"
                    + " VALUES (?,?,?,?,?,?)";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, idAO);
            ps.setString(2, proveedorNit);
            ps.setString(3, codigo);
            ps.setString(4, lab);
            ps.setBigDecimal(5, numSol);
            ps.setFloat(6, precio);
            ps.executeUpdate();
            act = true;
        } catch (SQLException ex) {
            System.out.println("Error funcion \"generar cotizacion\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return act;

    }

    @Override
    public void eliminarAprobacion(BigDecimal numSol) {
        Connection con = null;
        PreparedStatement ps = null;
        String statement;
        ArrayList<solicitudPr> Revisadas = new ArrayList<>();
        solicitudPr rev = null;
        boolean act = false;
        try {
            con = Conexion.conexion.getConnection();
            statement = "delete from APROBADOS a where a.COTIZACION_ID = "
                    + "(select aprobados.COTIZACION_ID from aprobados, COTIZACION_PROD"
                    + " where COTIZACION_PROD.COTIZACION_ID = aprobados.COTIZACION_ID and COTIZACION_PROD.NUM_SOL = ?)";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, numSol);
            ps.executeUpdate();
            act = true;
        } catch (SQLException ex) {
            System.out.println("Error funcion \"eliminar aprobacion\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
    }

    @Override
    public proveedor getDatosProveedor(String NIT) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select PROVEEDOR.NIT, PROVEEDOR.NOMBRE, datos.DIR, datos.TELEFONO, datos.TELEFAX, datos.CIUDAD, datos.PAIS from PROVEEDOR, datos where PROVEEDOR.NIT =? and datos.PROVEEDOR_NIT =?";
        proveedor p = null;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, NIT);
            ps.setString(2, NIT);
            rs = ps.executeQuery();
            while (rs.next()) {
                p = new proveedor(rs.getString(2), rs.getString(1), rs.getString(3), rs.getString(4), rs.getString(5), "", "");
            }
        } catch (SQLException ex) {
            System.out.println("Error en la funcion getDatosProveedor");
            System.out.println(ex);
            //Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return p;
    }

    @Override
    public ArrayList<itemsOrdenCompra> pedidoOrdenCompra(String proveedor) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select i.CINTERNO, i.INVENTARIO, i.DESCRIPCION, a.CAPROBADA, i.PRESENTACION, c.PRECIO_U, s.OBSERVACIONES,c.COTIZACION_ID\n"
                + "from COTIZACION_PROD c, APROBADOS a, item i, SOLICITUDPR s\n"
                + "where c.PROVEEDOR_NIT =?  and c.REVISADA = 'SI' and c.COTIZACION_ID = a.COTIZACION_ID and "
                + "c.ITEM_CINTERNO = i.CINTERNO and s.NUM_SOL = c.NUM_SOL and c.ENORDEN ='NO'";
        itemsOrdenCompra i = null;
        ArrayList<itemsOrdenCompra> pedido = new ArrayList<>();
        System.out.println(statement);
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, proveedor);
            rs = ps.executeQuery();
            while (rs.next()) {
                i = new itemsOrdenCompra(rs.getString(1), rs.getString(2), rs.getString(3), rs.getFloat(4), rs.getString(5), rs.getFloat(6), rs.getString(7), rs.getBigDecimal(8));
                i.setvTotal(i.getCaprobada() * i.getPrecioU());
                pedido.add(i);
            }
            for (itemsOrdenCompra p : pedido) {
                System.out.println(p.getCinterno());
            }
        } catch (SQLException ex) {
            System.out.println("Error en la funcion \"pedido orden compra\"");
            System.out.println(ex);
//Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return pedido;
    }

    @Override
    public void actualizarCotEnOrden(ArrayList<itemsOrdenCompra> pedido) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //Actualizar Tabla Cotizacion
        con = null;
        ps = null;
        rs = null;
        String statement = "UPDATE cotizacion_prod SET `ENORDEN`='SI' WHERE `COTIZACION_ID`=?";
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            for (itemsOrdenCompra p : pedido) {
                ps.setBigDecimal(1, p.getId_cotizacion());
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }

    }

    @Override
    public void crearOrdenCompra(BigDecimal idAo) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        try {
            con = Conexion.conexion.getConnection();
            statement = "INSERT INTO ORDENCOMPRA (AO_ID) VALUES (?)";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, idAo);
            ps.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error funcion \"crear OrdenCompra\"");
            System.out.println(ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
    }

    @Override
    public BigDecimal OrdenValida(BigDecimal id) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        BigDecimal valida = null;
        try {
            con = Conexion.conexion.getConnection();
            statement = "select NUM_ORDEN from ORDENCOMPRA where AO_id = ? order by NUM_ORDEN desc";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, id);
            rs = ps.executeQuery();
            rs.next();
            if (rs.getRow() != 0) {
                valida = rs.getBigDecimal(1);
            }

        } catch (SQLException ex) {
            System.out.println("Error funcion \"solicitud valida\"");
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }

        return valida;
    }

    @Override
    public boolean itemsxorden(BigDecimal orden, String proveedor, ArrayList<itemsOrdenCompra> pedido, String Obs) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "insert into itmxorden values(?,?,?,?,?,?,?)";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, orden);
            ps.setString(2, proveedor);
            ps.setString(7, Obs);
            for (itemsOrdenCompra p : pedido) {
                ps.setString(3, p.getCinterno());
                ps.setString(4, p.getInventario());
                ps.setFloat(5, p.getCaprobada());
                ps.setFloat(6, p.getPrecioU());
                ps.executeUpdate();
            }
            valido = true;
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }

        return valido;
    }

    @Override
    public recepcionProd getDatosRec(BigDecimal numorden, BigDecimal id, String area) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select ORDENCOMPRA_NUM_ORDEN, (select p.nombre from PROVEEDOR p where p.nit = PROVEEDOR_NIT),PROVEEDOR_NIT, ITEM_CINTERNO, ITEM_INVENTARIO, CAPROBADA, PRECIO_U, OBS\n"
                + "from ITMXORDEN where ORDENCOMPRA_NUM_ORDEN = ?";
        recepcionProd rec = null;
        itemRecep item = null;
        ArrayList<itemRecep> articulos = new ArrayList<>();
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, numorden);
            rs = ps.executeQuery();
            while (rs.next()) {
                rec = new recepcionProd(numorden, null, id, area, null);
                proveedor datosProveedor = this.getDatosProveedor(rs.getString(3));
                rec.setP(datosProveedor);
                item = new itemRecep(rs.getString(4), rs.getString(5), rs.getString(8), rs.getFloat(6), rs.getFloat(7));
                articulos.add(item);
                rec.setArticulos(articulos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return rec;
    }

    @Override
    public ItemInventario datosCompletosItem(String cinterno, String lab) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select CINTERNO, INVENTARIO, DESCRIPCION, PRESENTACION, CANTIDAD, PRECIO, CCALIDAD, CESP, SUCURSAL from item  where CINTERNO = ? ";
        ItemInventario itm = null;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, cinterno);
            rs = ps.executeQuery();
            while (rs.next()) {
                itm = new ItemInventario(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7), rs.getString(8), rs.getString(9), 0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return itm;
    }

    @Override
    public boolean recibirPedido(BigDecimal numOrden, BigDecimal idRec, String area, ArrayList<itemRecep> articulos) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "INSERT INTO RECEPCION (CINTERNO, INVENTARIO, NUM_ORDEN, FECHALLEGADA, FECHAVENCIMIENTO, CCALIDAD, CESP, MVERIFICACION, RECEPTOR, AREAREC, PRECIOANTERIOR) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        boolean valido = false;
        float precioAnterior = 0;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(3, numOrden);
            ps.setBigDecimal(9, idRec);
            ps.setString(10, area);
            for (itemRecep articulo : articulos) {
                ps.setString(1, articulo.getCinterno());
                ps.setString(2, articulo.getLab());
                ps.setDate(4, new Date(articulo.getfLlegada().getTime()));
                ps.setDate(5, new Date(articulo.getfVencimiento().getTime()));
                ps.setString(6, articulo.getcCalidad());
                ps.setString(7, articulo.getcEsp());
                ps.setString(8, articulo.getmVerificacion().toString());
                ps.setFloat(11, 0);
                ps.executeUpdate();
                this.updateCantidad(articulo.getCinterno(), articulo.getLab(), articulo.getcAprobada());
                ps = con.prepareStatement("SELECT precio from item where CINTERNO = ?");
                ps.setString(1, articulo.getCinterno());
                rs = ps.executeQuery();
                rs.next();
                precioAnterior = rs.getFloat(1);
                ps = con.prepareStatement("UPDATE recepcion SET PRECIOANTERIOR= ? WHERE CINTERNO=?  and NUM_ORDEN=?");
                ps.setFloat(1, precioAnterior);
                ps.setString(2, articulo.getCinterno());
                ps.setBigDecimal(3, numOrden);
                ps.executeUpdate();
                ps = con.prepareStatement("UPDATE item SET precio=?  WHERE CINTERNO=? ");
                ps.setFloat(1, articulo.getPrecio());
                ps.setString(2, articulo.getCinterno());
                ps.executeUpdate();
            }
            valido = true;
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return valido;

    }

    @Override
    public boolean updateCantidad(String cinterno, String lab, float cantidad) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "update item set cantidad = cantidad +? where CINTERNO =?";
        boolean updated = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setFloat(1, cantidad);
            ps.setString(2, cinterno);
            ps.executeUpdate();
            updated = true;
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return updated;
    }

    @Override
    public ArrayList<proveedor> todosProveedores() throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select p.NIT, p.NOMBRE, d.DIR, d.TELEFONO, d.TELEFAX, d.CORREO, d.CELULAR "
                + "from datos d, proveedor p"
                + " where d.PROVEEDOR_NIT = p.NIT order by nombre";
        ArrayList<proveedor> proveedores = new ArrayList<>();
        proveedor prov = null;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                prov = new proveedor(rs.getString(2), rs.getString(1), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(7), rs.getString(6));
                proveedores.add(prov);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return proveedores;
    }

    @Override
    public boolean realizarDescargo(descargo d) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "INSERT INTO DESCARGOS(FECHA, ID, AREA, CANTIDAD, CINTERNO, INVENTARIO) VALUES (?,?,?,?,?,?)";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setDate(1, new Date(d.getFecha().getTimeInMillis()));
            ps.setBigDecimal(2, d.getId());
            ps.setString(3, d.getArea());
            ps.setFloat(4, d.getCantidad());
            ps.setString(5, d.getCinterno());
            ps.setString(6, d.getArea());
            ps.executeUpdate();
            this.updateCantidad(d.getCinterno(), d.getArea(), d.getCantidad() * -1);
            valido = true;
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return valido;
    }

    //Generacion de archivos .pdf
    @Override
    public File pdf_001(String ruta, fdc_001 archivo) throws RemoteException {
        File pdf = null;
        Document documento = new Document(PageSize.A4);
        System.out.println(documento.getPageSize());
        boolean setMargins = documento.setMargins(40, 0, 40, 40);
        Font bf_titulos = FontFactory.getFont(FontFactory.TIMES_ROMAN);//BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        bf_titulos.setStyle(Font.BOLD);
        bf_titulos.setSize(9);
        Font bf_titulos1 = FontFactory.getFont(FontFactory.TIMES_ROMAN);//BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        bf_titulos1.setStyle(Font.BOLD);
        bf_titulos1.setSize(8);
        String espaciado = "                        ";
        String linea = "________________________________";
        float tamanoEncabezado[] = {20, 60, 30};
        float tamanoEncabezado1[] = {30, 40, 50};
        float tamanoItems[] = {20, 30, 50, 15, 35, 15, 20, 30};
        float tamanoProv[] = {40, 30, 30, 50};
        try {
            FileOutputStream fichero = new FileOutputStream(ruta + "\\Solicitud_" + archivo.getNumSol().toString() + ".pdf");
            PdfWriter escribir = PdfWriter.getInstance(documento, fichero);
            documento.open();
            Image logo = Image.getInstance("http://www.biotrendslab.com/wp-content/uploads/2014/03/Logo_Biotrends2.png");
            PdfPTable tabla = new PdfPTable(tamanoEncabezado);
            PdfPTable segFila = new PdfPTable(tamanoEncabezado1);
            PdfPTable items = new PdfPTable(tamanoItems);
            PdfPTable proveedores = new PdfPTable(tamanoProv);
            PdfPTable pie1 = new PdfPTable(1);
            PdfPTable pie2 = new PdfPTable(2);
            //el numero indica la cantidad de Columnas
            tabla.addCell(new Paragraph("F-DC-001\nRevision 04\nFecha\nActualizacion\n03-abr-14", bf_titulos));
            tabla.addCell(new Paragraph("\nSOLICITUD DE PROUCTOS O BIENES", bf_titulos));
            tabla.addCell(logo);
            segFila.addCell(new Paragraph("FECHA", bf_titulos));
            segFila.addCell(new Paragraph("AREA O PROCESO SOLICITANTE", bf_titulos));
            segFila.addCell(new Paragraph("NOMBRE DEL SOLICITANTE", bf_titulos));
            segFila.addCell(new Paragraph(archivo.getFecha(), bf_titulos));
            segFila.addCell(new Paragraph(archivo.getAreaOProceso(), bf_titulos));
            segFila.addCell(new Paragraph(archivo.getNombreRA(), bf_titulos));
            items.addCell(new Paragraph("INVENTARIO ACTUAL", bf_titulos1));
            items.addCell(new Paragraph("NOMBRE DEL PRODUCTO O BIEN", bf_titulos1));
            items.addCell(new Paragraph("DESCRIPCION DETALLADA (Especificaciones técnicas)", bf_titulos1));
            items.addCell(new Paragraph("CANT SOLICITADA", bf_titulos1));
            items.addCell(new Paragraph("PRESENTACION", bf_titulos1));
            items.addCell(new Paragraph("CANT APROBADA", bf_titulos1));
            items.addCell(new Paragraph("N° ORDEN COMPRA", bf_titulos1));
            items.addCell(new Paragraph("PRECIO UNITARIO", bf_titulos1));
            int numeroFilas = 128 - (archivo.getArticulos().size() * 8);
            ArrayList<itemsfdc_001> articulos = archivo.getArticulos();
            for (itemsfdc_001 ar : articulos) {
                items.addCell(new Paragraph(new Float(ar.getCantidad()).toString(), bf_titulos1));
                items.addCell(new Paragraph(ar.getLab() + "-" + ar.getCodigo(), bf_titulos1));
                items.addCell(new Paragraph(ar.getDescr(), bf_titulos1));
                items.addCell(new Paragraph(new Float(ar.getcSol()).toString(), bf_titulos1));
                items.addCell(new Paragraph(ar.getPresentacion(), bf_titulos1));
                items.addCell(new Paragraph(new Float(ar.getcApro()).toString(), bf_titulos1));
                items.addCell(new Paragraph("", bf_titulos1));
                items.addCell(new Paragraph(new Float(ar.getPrecio()).toString(), bf_titulos1));

            }
            for (int i = 0; i <= numeroFilas; i++) {
                items.addCell(new Paragraph(" ", bf_titulos));
            }
            proveedores.addCell(new Paragraph("NOMBRE", bf_titulos));
            proveedores.addCell(new Paragraph("PRECIO", bf_titulos));
            proveedores.addCell(new Paragraph("DISPONIBILIDAD", bf_titulos));
            proveedores.addCell(new Paragraph("OBSERVACION", bf_titulos));
            int numFilasProv = 16;
            numFilasProv -= (archivo.getProveedores().size() * 4);
            for (ArrayList<itemxproveedor> iter : archivo.getProveedores()) {

                for (itemxproveedor i : iter) {
                    proveedores.addCell(new Paragraph(i.getNombre(), bf_titulos1));
                    proveedores.addCell(new Paragraph(new Float(i.getPrecio()).toString(), bf_titulos1));
                    proveedores.addCell(new Paragraph(new Float(i.getDisponibilidad()).toString(), bf_titulos1));
                    proveedores.addCell(new Paragraph("", bf_titulos1));
                }
            }
            for (int i = 0; i <= numFilasProv; i++) {
                proveedores.addCell(new Paragraph(" ", bf_titulos1));

            }
            pie1.addCell(new Paragraph(espaciado + espaciado + espaciado + "           Biotrends Laboratorios S.A.S", bf_titulos1));
            pie2.addCell(new Paragraph(espaciado + "REVISO: Director Administrativo Comercial", bf_titulos1));
            pie2.addCell(new Paragraph(espaciado + "APROBO: Gerente", bf_titulos1));
            documento.add(tabla);
            documento.add(segFila);
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(items);
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(new Paragraph(espaciado + "PROVEEDORES-ALTERNATIVOS", bf_titulos));
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(proveedores);
            documento.add(new Paragraph(Chunk.NEWLINE));
            String subs1 = "";
            String subs2 = "";
            String subs3 = "";
            documento.add(new Paragraph(espaciado + "OBSERVACIONES:" + espaciado + archivo.getObs(), bf_titulos));
            documento.add(new Paragraph(" ", bf_titulos));
            documento.add(new Paragraph(" ", bf_titulos));
            System.out.println(archivo.getObs().length());
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(new Paragraph(espaciado + "_____" + archivo.getNombreRA() + "-" + archivo.getCargoRA() + "_____" + espaciado + espaciado + espaciado + espaciado + "    " + "____" + archivo.getRevisionAO() + "___", bf_titulos));
            documento.add(new Paragraph(espaciado + "     " + "ELABORADO POR (Nombre -Cargo)"
                    + espaciado + espaciado + espaciado + "REVISION- COMPRAS", bf_titulos));
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(pie1);
            documento.add(pie2);

            documento.close();
            pdf = new File(ruta + "\\Solicitud_" + archivo.getNumSol().toString() + ".pdf");
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pdf;
    }

    @Override
    public File pdf_002(String ruta, proveedor p, ArrayList<itemsOrdenCompra> pedido, float total, String obs, String numorden) throws RemoteException {

        File pdf = null;
        Document documento = new Document(PageSize.A4);
        System.out.println(documento.getPageSize());
        boolean setMargins = documento.setMargins(40, 0, 40, 40);
        Font bf_titulos = FontFactory.getFont(FontFactory.TIMES_ROMAN);//BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        bf_titulos.setStyle(Font.BOLD);
        bf_titulos.setSize(9);
        Font bf_titulos1 = FontFactory.getFont(FontFactory.TIMES_ROMAN);//BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        bf_titulos1.setStyle(Font.BOLD);
        bf_titulos1.setSize(8);
        String espaciado = "                        ";
        String linea = "________________________________";
        float tamanoEncabezado[] = {20, 60, 30};
        float TamdatosProv[] = {50, 30, 40, 40, 40};
        float tamanoItems[] = {20, 50, 20, 30, 30, 30};
        float tamanoProv[] = {40, 30, 30, 50};
        float tamFecha[] = {70, 30};
        GregorianCalendar hoy = new GregorianCalendar();
        try {
            FileOutputStream fichero = new FileOutputStream(ruta + "\\NumeroOrden_" + numorden + ".pdf");
            PdfWriter escribir = PdfWriter.getInstance(documento, fichero);
            documento.open();
            Image logo = Image.getInstance("http://www.biotrendslab.com/wp-content/uploads/2014/03/Logo_Biotrends2.png");
            PdfPTable tabla = new PdfPTable(tamanoEncabezado);
            PdfPTable fecha = new PdfPTable(tamFecha);
            PdfPTable datosProv = new PdfPTable(TamdatosProv);
            PdfPTable items = new PdfPTable(tamanoItems);
            PdfPTable pie1 = new PdfPTable(1);
            PdfPTable pie2 = new PdfPTable(2);
            //el numero indica la cantidad de Columnas
            tabla.addCell(new Paragraph("F-DC-002\nRevision 04\nFecha\nActualizacion\n01-dic-13", bf_titulos));
            tabla.addCell(new Paragraph("\nORDEN DE COMPRA", bf_titulos));
            tabla.addCell(logo);
            fecha.addCell(new Paragraph("FECHA ELABORACION" + hoy.get(Calendar.DAY_OF_MONTH) + "/" + (hoy.get(Calendar.MONTH) + 1) + "/" + hoy.get(Calendar.YEAR), bf_titulos));
            fecha.addCell(new Paragraph("N° ORDEN: " + numorden));
            datosProv.addCell(new Paragraph("NOMBRE PROVEEDOR/PRESTADOR DEL SERVICIO", bf_titulos));
            datosProv.addCell(new Paragraph("NIT", bf_titulos));
            datosProv.addCell(new Paragraph("DIRECCION", bf_titulos));
            datosProv.addCell(new Paragraph("CELULAR", bf_titulos));
            datosProv.addCell(new Paragraph("TELEFAX", bf_titulos));

            datosProv.addCell(new Paragraph(p.getNombre(), bf_titulos1));
            datosProv.addCell(new Paragraph(p.getNIT(), bf_titulos1));
            datosProv.addCell(new Paragraph(p.getDireccion(), bf_titulos1));
            datosProv.addCell(new Paragraph(p.getTelefono(), bf_titulos1));
            datosProv.addCell(new Paragraph(p.getTelefax(), bf_titulos1));

            items.addCell(new Paragraph("C.INTERNO", bf_titulos1));
            items.addCell(new Paragraph("DESCRIPCION", bf_titulos1));
            items.addCell(new Paragraph("CANTIDAD", bf_titulos1));
            items.addCell(new Paragraph("PRESENTACION", bf_titulos1));
            items.addCell(new Paragraph("VALOR UNITARIO", bf_titulos1));
            items.addCell(new Paragraph("VALOR TOTAL", bf_titulos1));
            int numeroFilas = 132 - (pedido.size() * 6);

            for (itemsOrdenCompra i : pedido) {
                System.out.println(i.getCaprobada());
            }
            for (itemsOrdenCompra i : pedido) {
                items.addCell(new Paragraph(i.getCinterno().toString(), bf_titulos1));
                items.addCell(new Paragraph(i.getDesc(), bf_titulos1));
                items.addCell(new Paragraph(new Float(i.getCaprobada()).toString(), bf_titulos1));
                items.addCell(new Paragraph(i.getPresen(), bf_titulos1));
                items.addCell(new Paragraph("$" + new Float(i.getPrecioU()).toString(), bf_titulos1));
                items.addCell(new Paragraph("$" + new Float(i.getCaprobada() * i.getPrecioU()).toString(), bf_titulos1));

            }
            for (int i = 0; i <= numeroFilas; i++) {
                items.addCell(new Paragraph(" ", bf_titulos));
            }

            pie1.addCell(new Paragraph(espaciado + espaciado + espaciado + "           Biotrends Laboratorios S.A.S", bf_titulos1));
            pie2.addCell(new Paragraph(espaciado + "REVISO: Director Administrativo Comercial", bf_titulos1));
            pie2.addCell(new Paragraph(espaciado + "APROBO: Gerente", bf_titulos1));
            documento.add(tabla);
            documento.add(datosProv);
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(items);
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(new Paragraph(espaciado + "SUBTOTAL (Antes de IVA):  $ " + total, bf_titulos));
            documento.add(new Paragraph(espaciado + "TOTAL:     $ " + total, bf_titulos));
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(new Paragraph(Chunk.NEWLINE));
            String subs1 = "";
            String subs2 = "";
            String subs3 = "";
            documento.add(new Paragraph(espaciado + "OBSERVACIONES:" + espaciado + obs, bf_titulos));
            documento.add(new Paragraph(" ", bf_titulos));
            documento.add(new Paragraph(" ", bf_titulos));
            documento.add(new Paragraph(Chunk.NEWLINE));
            //    documento.add(new Paragraph(espaciado + "_____" + archivo.getNombreRA() + "-" + archivo.getCargoRA() + "_____" + espaciado + espaciado + espaciado + espaciado + "    " + "____" + archivo.getRevisionAO() + "___", bf_titulos));
            documento.add(new Paragraph(espaciado + "     " + "ELABORADO POR (Nombre -Cargo)"
                    + espaciado + espaciado + espaciado + "REVISION- COMPRAS", bf_titulos));
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(pie1);
            documento.add(pie2);

            documento.close();
            pdf = new File(ruta + "\\NumeroOrden_" + numorden + ".pdf");
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pdf;
    }

    @Override
    public File pdf_007(fdc_001 archivo) throws RemoteException {
        File pdf = null;
        String ruta = "C:\\Users\\OscarDarío\\Desktop\\Servicios_Solicitud_" + archivo.getNumSol().toString() + ".pdf";
        Document documento = new Document(PageSize.A4.rotate());
        boolean setMargins = documento.setMargins(0, 0, 30, 30);
        Font bf_titulos = FontFactory.getFont(FontFactory.TIMES_ROMAN);//BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        bf_titulos.setStyle(Font.BOLD);
        bf_titulos.setSize(8);
        Font bf_titulos1 = FontFactory.getFont(FontFactory.TIMES_ROMAN);//BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        bf_titulos1.setStyle(Font.BOLD);
        bf_titulos1.setSize(6);
        String espaciado = "                        ";
        String linea = "________________________________";
        float tamanoEncabezado[] = {20, 60, 30};
        float tamanoEncabezado1[] = {30, 40, 50};
        float tamanoServicios[] = {30, 5, 30, 5, 30, 5};
        float tamanoItems[] = {30, 50, 60, 50, 25, 40};
        float tamanoProv[] = {40, 30, 30, 50};
        try {
            FileOutputStream fichero = new FileOutputStream(ruta);
            PdfWriter escribir = PdfWriter.getInstance(documento, fichero);
            documento.open();
            Image logo = Image.getInstance("http://www.biotrendslab.com/wp-content/uploads/2014/03/Logo_Biotrends2.png");
            PdfPTable tabla = new PdfPTable(tamanoEncabezado);
            PdfPTable segFila = new PdfPTable(tamanoEncabezado1);
            PdfPTable servicios = new PdfPTable(tamanoServicios);
            PdfPTable items = new PdfPTable(tamanoItems);
            PdfPTable proveedores = new PdfPTable(tamanoProv);
            PdfPTable pie1 = new PdfPTable(1);
            PdfPTable pie2 = new PdfPTable(2);
            //el numero indica la cantidad de Columnas
            tabla.addCell(new Paragraph("F-DC-007\nRevision 01\nFecha Actualizacion\n03-abr-14", bf_titulos));
            tabla.addCell(new Paragraph("\nSOLICITUD DE SERVICIOS", bf_titulos));
            tabla.addCell(logo);
            segFila.addCell(new Paragraph("FECHA", bf_titulos));
            segFila.addCell(new Paragraph("AREA O PROCESO SOLICITANTE", bf_titulos));
            segFila.addCell(new Paragraph("NOMBRE DEL SOLICITANTE", bf_titulos));
            segFila.addCell(new Paragraph(archivo.getFecha(), bf_titulos));
            segFila.addCell(new Paragraph(archivo.getAreaOProceso(), bf_titulos));
            segFila.addCell(new Paragraph(archivo.getNombreRA(), bf_titulos));
            servicios.addCell(new Paragraph(" " + "CALIBRACION", bf_titulos1));
            servicios.addCell(new Paragraph(" ", bf_titulos1));
            servicios.addCell(new Paragraph(" " + "MANTENIMIENTO PREVENTIVO", bf_titulos1));
            servicios.addCell(new Paragraph(" ", bf_titulos1));
            servicios.addCell(new Paragraph(" " + "MANTENIMIENTO CORRECTIVO", bf_titulos1));
            servicios.addCell(new Paragraph(" ", bf_titulos1));
            servicios.addCell(new Paragraph(" " + "CALIFICACION", bf_titulos1));
            servicios.addCell(new Paragraph(" ", bf_titulos1));
            servicios.addCell(new Paragraph(" " + "FUMIGACION", bf_titulos1));
            servicios.addCell(new Paragraph(" ", bf_titulos1));
            servicios.addCell(new Paragraph(" " + "LAVADO TANQUES", bf_titulos1));
            servicios.addCell(new Paragraph(" ", bf_titulos1));
            items.addCell(new Paragraph("CODIGO EQUIPO", bf_titulos1));
            items.addCell(new Paragraph("NOMBRE DEL EQUIPO", bf_titulos1));
            items.addCell(new Paragraph("ESPECIFICACIONES TÉCNICAS DEL SERVICIO", bf_titulos1));
            items.addCell(new Paragraph("PERSONAL CALIFICADO", bf_titulos1));
            items.addCell(new Paragraph("N° ORDEN COMPRA", bf_titulos1));
            items.addCell(new Paragraph("PRECIO UNITARIO", bf_titulos1));
            int numeroFilas = 60 - (archivo.getArticulos().size() * 6);
            ArrayList<itemsfdc_001> articulos = archivo.getArticulos();
            for (itemsfdc_001 ar : articulos) {
                items.addCell(new Paragraph(" ", bf_titulos1));
                items.addCell(new Paragraph(" ", bf_titulos1));
                items.addCell(new Paragraph(" ", bf_titulos1));
                items.addCell(new Paragraph(" ", bf_titulos1));
                items.addCell(new Paragraph(" ", bf_titulos1));
                items.addCell(new Paragraph(" ", bf_titulos1));
                items.addCell(new Paragraph(" ", bf_titulos1));
                items.addCell(new Paragraph(" ", bf_titulos1));

            }
            for (int i = 0; i <= numeroFilas; i++) {
                items.addCell(new Paragraph(" ", bf_titulos));
            }
            proveedores.addCell(new Paragraph("NOMBRE", bf_titulos));
            proveedores.addCell(new Paragraph("PRECIO", bf_titulos));
            proveedores.addCell(new Paragraph("DISPONIBILIDAD", bf_titulos));
            proveedores.addCell(new Paragraph("OBSERVACION", bf_titulos));
            int numFilasProv = 16;
            numFilasProv -= (archivo.getProveedores().size() * 4);
            for (ArrayList<itemxproveedor> iter : archivo.getProveedores()) {

                for (itemxproveedor i : iter) {
                    proveedores.addCell(new Paragraph(i.getNombre(), bf_titulos1));
                    proveedores.addCell(new Paragraph(new Float(i.getPrecio()).toString(), bf_titulos1));
                    proveedores.addCell(new Paragraph(new Float(i.getDisponibilidad()).toString(), bf_titulos1));
                    proveedores.addCell(new Paragraph("", bf_titulos1));
                }
            }
            for (int i = 0; i <= numFilasProv; i++) {
                proveedores.addCell(new Paragraph(" ", bf_titulos1));

            }
            pie1.addCell(new Paragraph(espaciado + espaciado + espaciado + "           Biotrends Laboratorios S.A.S", bf_titulos1));
            pie2.addCell(new Paragraph(espaciado + "REVISO: Director Administrativo Comercial", bf_titulos1));
            pie2.addCell(new Paragraph(espaciado + "APROBO: Gerente", bf_titulos1));
            documento.add(tabla);
            documento.add(segFila);
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(servicios);
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(items);
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(new Paragraph(espaciado + espaciado + "PROVEEDORES-ALTERNATIVOS", bf_titulos));
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(proveedores);
            documento.add(new Paragraph(Chunk.NEWLINE));
            String subs1 = "";
            String subs2 = "";
            String subs3 = "";
            documento.add(new Paragraph(espaciado + espaciado + "OBSERVACIONES:" + espaciado + archivo.getObs(), bf_titulos));
            documento.add(new Paragraph(" ", bf_titulos));
            documento.add(new Paragraph(" ", bf_titulos));
            System.out.println(archivo.getObs().length());
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(new Paragraph(espaciado + espaciado + espaciado + "_____" + archivo.getNombreRA() + "-" + archivo.getCargoRA() + "_____" + espaciado + espaciado + espaciado + espaciado + espaciado + espaciado + "    " + "____" + archivo.getRevisionAO() + "___", bf_titulos));
            documento.add(new Paragraph(espaciado + espaciado + espaciado + "ELABORADO POR (Nombre -Cargo)"
                    + espaciado + espaciado + espaciado + espaciado + espaciado + "REVISION- COMPRAS", bf_titulos));
            documento.add(new Paragraph(Chunk.NEWLINE));
            documento.add(pie1);
            documento.add(pie2);

            documento.close();
            pdf = new File(ruta);
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pdf;

    }

    @Override
    public boolean verificarClave(String anterior, BigDecimal id, String tipo) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "";
        if (tipo.equalsIgnoreCase("da")) {
            statement = "select psw from " + tipo + " where id_da = ?";
        } else {
            statement = "select psw from " + tipo + " where id = ?";
        }
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, id);
            rs = ps.executeQuery();
            rs.next();
            String string = rs.getString(1);
            System.out.println(string);
            System.out.println(this.encriptar(anterior));
            if (string.equalsIgnoreCase(this.encriptar(anterior))) {
                valido = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(valido);
        return valido;
    }

    @Override
    public boolean cambiarClave(String nueva, BigDecimal id, String tipo) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        String statement = "";
        nueva = this.encriptar(nueva);
        boolean hecho = false;
        if (tipo.equalsIgnoreCase("da")) {
            statement = "UPDATE da SET PSW= ? WHERE ID_da= ?";
        } else if (tipo.equalsIgnoreCase("ra")) {
            statement = "UPDATE ra SET PSW= ? WHERE ID= ?";
        } else if (tipo.equalsIgnoreCase("ao")) {
            statement = "UPDATE ao SET PSW= ? WHERE ID= ?";
        }
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, nueva);
            ps.setBigDecimal(2, id);
            ps.executeUpdate();
            hecho = true;
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hecho;
    }

    private String encriptar(String psw) {
        String md5Hex = DigestUtils.md5Hex(psw);
        return md5Hex;
    }

    @Override
    public ArrayList<itemxproveedor> tablaCotizacionesIXP(String cinterno) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select  nit, nombre, ITEM_CINTERNO, ITEM_INVENTARIO, precio from ixp, proveedor where item_cinterno = ? and  PROVEEDOR_NIT = NIT";
        itemxproveedor item = null;
        ArrayList<itemxproveedor> lista = new ArrayList<>();

        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, cinterno);
            rs = ps.executeQuery();
            while (rs.next()) {
                item = new itemxproveedor(rs.getString(2), rs.getFloat(5), rs.getString(1), rs.getString(3), rs.getString(4));
                lista.add(item);
                System.out.println("kasjf");
            }

        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }

        }
        return lista;
    }

    @Override
    public File npdf_002(String ruta, proveedor p, ArrayList<itemsOrdenCompra> pedido, float total, String obs, String numorden) throws RemoteException {
        File metodo=null;
        try {

            String direccion = "E:\\Final Version\\Biot_Server\\src\\Logica\\Reportes\\fdc002.jasper";
            HashMap parametros = new HashMap();
            GregorianCalendar fecha = new GregorianCalendar();
            parametros.put("nombreProv", p.getNombre());
            parametros.put("nit", p.getNIT());
            parametros.put("direccionProv", p.getDireccion());
            parametros.put("fax", p.getTelefax());
            parametros.put("oCompra", numorden);
            parametros.put("fechaElab", fecha.get(Calendar.DAY_OF_MONTH) + "/" + (fecha.get(Calendar.MONTH) + 1) + "/" + fecha.get(Calendar.YEAR));
            parametros.put("Obs", obs);
            //fdc002 fdc = new fdc002();
            metodo = fdc002.metodo(parametros, pedido);
            
        } catch (Exception e) {
        }
        return metodo;
    }

    public void generarfdc007() {
        try {

            String direccion = "E:\\Final Version\\Biot_Server\\src\\Logica\\Reportes\\ListadoProveedores.jasper";
            HashMap parametros = new HashMap();
            JasperViewer.viewReport(direccion, true);

        } catch (Exception e) {
        }
    }

    public static ArrayList fdc007() {
        return null;
    }

    @Override
    public void generarfdc002() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public ArrayList<ItemInventario> itemxProv(String nit) throws RemoteException
    {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select ITEM_CINTERNO, DESCRIPCION, PRESENTACION, Cantidad, item.precio, CCALIDAD, CESP, SUCURSAL "
                + "from ixp, proveedor, item "
                + "where PROVEEDOR_NIT =nit and PROVEEDOR_NIT = ? and ITEM_CINTERNO = CINTERNO;";
        ItemInventario item = null;
        ArrayList<ItemInventario> lista = new ArrayList<>();

        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, nit);
            rs = ps.executeQuery();
            while (rs.next()) {
                item = new ItemInventario(rs.getString(1), rs.getString(2), rs.getString(3), rs.getFloat(4), rs.getFloat(5), rs.getString(6), "", rs.getString(8), rs.getString(7));
                lista.add(item);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }

        }
        return lista;
    }
}
