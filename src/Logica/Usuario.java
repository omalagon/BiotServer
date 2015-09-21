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
import EstructurasAux.permisos;
import EstructurasAux.proveedor;
import EstructurasAux.recepcionProd;
import EstructurasAux.users;
import java.io.File;
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
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author Oscar Dario Malagon Murcia
 */
public class Usuario extends UnicastRemoteObject implements interfaces.Usuario, Serializable {

    public Usuario() throws RemoteException {
        super();
    }

    /**
     *
     * @param identificacion
     * @param contrasena
     * @return boolean
     * @throws RemoteException
     *
     * Válida la existencia del usuario en la base de datos.
     */
    @Override
    public boolean validarUsuario(String identificacion, String contrasena) throws RemoteException {
        Connection conex = null;
        boolean validacion = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        contrasena = this.encriptar(contrasena);
        System.out.println(contrasena);

        try {
            conex = Conexion.conexion.getConnection();

            if (identificacion.isEmpty() == false || contrasena.isEmpty() == false) {
                String statement;
                statement = "select nombre from usuario where id = ? and psw= ?";
                ps = conex.prepareStatement(statement);
                ps.setString(1, identificacion);
                ps.setString(2, contrasena);
                rs = ps.executeQuery();
                rs.next();
                if (rs.getRow() != 0) {
                    validacion = true;
                }
            }
        } catch (SQLException ex) {
            validacion = false;
            System.out.println(ex);
            System.out.println("Error función \"Validar Usuario\" ");
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

    /**
     *
     * @param nueva
     * @param id
     * @return boolean
     * @throws RemoteException
     *
     * Cambia la clave del usuario
     */
    @Override
    public boolean cambiarClave(String nueva, String id) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        String statement = "UPDATE usuario SET PSW= ? WHERE ID= ?";
        boolean hecho = false;
        try {
            if (id.length() != 0) {
                nueva = this.encriptar(nueva);
                con = Conexion.conexion.getConnection();
                ps = con.prepareStatement(statement);
                ps.setString(1, nueva);
                ps.setString(2, id);
                ps.executeUpdate();
                hecho = true;
            } else {
                System.out.println("false");
            }
        } catch (SQLException ex) {
            System.out.println("Error función \"Cambiar Clave\"");
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
        return hecho;
    }

    /**
     *
     * @param parametro = nombre o id
     * @param valor = cadena de entrada para realizar la búsqueda
     * @return ArrayList
     * @throws RemoteException
     *
     * Busca todos los usuarios que tienen un "valor" parecido o igual al
     * ingresado
     */
    @Override
    public ArrayList<BuscarUsuario> buscarEmpleado(String parametro, String valor) throws RemoteException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        String statement = "select id, nombre, lab from usuario where id like ?";
        String statement2 = "select id, nombre, lab from usuario where nombre like ?";
        BuscarUsuario buscar = null;
        ArrayList<BuscarUsuario> lista = new ArrayList<>();
        try {
            con = Conexion.conexion.getConnection();
            if (parametro.equalsIgnoreCase("nombre")) {
                ps = con.prepareStatement(statement2);
            } else if (parametro.equalsIgnoreCase("id")) {
                ps = con.prepareStatement(statement);
            }
            if (!valor.equalsIgnoreCase("")) {
                valor = "%" + valor + "%";
                ps.setString(1, valor);
                rs = ps.executeQuery();
                while (rs.next()) {
                    buscar = new BuscarUsuario(rs.getString(2), rs.getString(1), rs.getString(3));
                    lista.add(buscar);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"Buscar Empleado\"");
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

        return lista;
    }

    /**
     *
     * @param anterior
     * @param id
     * @return boolean
     * @throws RemoteException
     *
     * Verifica la validez de la contraseña anterior, antes de realizar el
     * cambio de clave.
     */
    @Override
    public boolean verificarClave(String anterior, String id) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select psw from usuario where id = ?";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, id);
            rs = ps.executeQuery();
            rs.next();
            String string = rs.getString(1);
            System.out.println(string);
            System.out.println(this.encriptar(anterior));
            if (string.equalsIgnoreCase(this.encriptar(anterior))) {
                valido = true;
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"Verificar Clave\"");
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
        return valido;
    }

    /**
     *
     * @param identificacion
     * @param nombre
     * @param correo
     * @param psw
     * @param area
     * @param id
     * @return boolean
     * @throws RemoteException
     *
     * Crea un usuario completamente nuevo
     */
    @Override
    public boolean crearUsuario(String identificacion, String nombre,
            String correo, String psw, String area, String id) throws RemoteException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        boolean creado = false;
        psw = this.encriptar(psw);
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement("INSERT INTO usuario (id,nombre,correo,psw,lab,id1)VALUE(?,?,?,?,?,?);");
            ps.setString(1, identificacion);
            ps.setString(2, nombre);
            ps.setString(3, correo);
            ps.setString(4, psw);
            ps.setString(5, area);
            ps.setString(6, id);
            ps.executeUpdate();
            creado = true;
        } catch (SQLException ex) {
            creado = false;
            System.out.println("Error en la función \"Crear Usuario\"");
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

    /**
     *
     * @param NIT
     * @param Nombre
     * @param direccion
     * @param telefono
     * @param telefax
     * @param ciudad
     * @param correo
     * @param celular
     * @return boolean
     * @throws RemoteException
     *
     * Crea un proveedor en la base de datos.
     */
    @Override
    public boolean CrearProveedor(String NIT, String Nombre, String direccion, String telefono, String telefax, String ciudad, String correo, String celular, String contacto) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "INSERT INTO proveedor(nit, nombre, dir, tel,fax, ciudad, correo, celular, contacto)VALUES(?,?,?,?,?,?,?,?,?);";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, NIT);
            ps.setString(2, Nombre);
            ps.setString(3, direccion);
            ps.setString(4, telefono);
            ps.setString(5, telefax);
            ps.setString(6, ciudad);
            ps.setString(7, correo);
            ps.setString(8, celular);
            ps.setString(9, contacto);
            ps.executeUpdate();
            valido = true;
        } catch (SQLException ex) {
            System.out.println("Error función \"Crear Proveedor\"");
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

    /**
     *
     * @param NIT
     * @return proveedor
     * @throws RemoteException
     *
     * Busca los datos de un proveedor de acuerdo al nit recibido
     */
    @Override
    public proveedor getDatosProveedor(String NIT) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select nit, nombre, dir, tel,fax, ciudad, correo, celular, contacto from proveedor where nit = ?";
        proveedor p = null;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, NIT);
            rs = ps.executeQuery();
            while (rs.next()) {
                p = new proveedor(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9));
            }
        } catch (SQLException ex) {
            System.out.println("Error en la funcion getDatosProveedor");
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
        return p;
    }

    /**
     *
     * @param NIT
     * @param Nombre
     * @param direccion
     * @param telefono
     * @param telefax
     * @param ciudad
     * @param correo
     * @param celular
     * @return boolean
     * @throws RemoteException
     *
     * Edita la información de un proveedor.
     */
    @Override
    public boolean EditarProveedor(String NIT, String Nombre, String direccion, String telefono, String telefax, String ciudad, String correo, String celular, String contacto) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "update proveedor set  nombre=?, dir=?, tel=?,fax=?, ciudad=?, correo=?, celular=? , contacto =? where nit=?";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, Nombre);
            ps.setString(2, direccion);
            ps.setString(3, telefono);
            ps.setString(4, telefax);
            ps.setString(5, ciudad);
            ps.setString(6, correo);
            ps.setString(7, celular);
            ps.setString(8, contacto);
            ps.setString(9, NIT);
            ps.executeUpdate();
            valido = true;
        } catch (SQLException ex) {
            System.out.println("Error función \"editar Proveedor\"");
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

    /**
     *
     * @return ArrayList
     * @throws RemoteException
     *
     * Genera una lista con todos los proveedores que se encuentran en el
     * sistema
     */
    @Override
    public ArrayList<proveedor> todosProveedores() throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select nit, nombre, dir, tel,fax, ciudad, correo, celular, contacto from proveedor order by nombre";
        ArrayList<proveedor> proveedores = new ArrayList<>();
        proveedor prov = null;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                prov = new proveedor(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9));
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

    /**
     *
     * @param item
     * @return boolean
     * @throws RemoteException
     *
     * Crea un ítem
     */
    @Override
    public boolean crearItem(ItemInventario item) throws RemoteException {
        PreparedStatement ps = null;
        Connection con = null;
        String statement = "insert into item (inventario, descripcion, presentacion, cantidad, precio, ccalidad,  cesp, cinterno) values(?,?,?,?,?,?,?,?);";
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
            ps.setString(7, item.getCEsp());
            ps.setString(8, item.getNumero());
            ps.executeUpdate();
            valido = true;
        } catch (SQLException ex) {
            System.out.println("Error función \"Crear Ítem\"");
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

    /**
     *
     * @param item
     * @return
     * @throws RemoteException
     *
     * Edita la información de un ítem ya existente.
     */
    @Override
    public boolean editarItem(ItemInventario item) throws RemoteException {
        PreparedStatement ps = null;
        Connection con = null;
        String statement = "update item set inventario = ?, descripcion = ? , presentacion = ? , cantidad = ? , precio =?, ccalidad= ?, cesp =? where cinterno =?;";
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
            ps.setString(7, item.getCEsp());
            ps.setString(8, item.getNumero());
            ps.executeUpdate();
            valido = true;
        } catch (SQLException ex) {
            System.out.println("Error función \"Editar Ítem\"");
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

    /**
     *
     * @param cinterno
     * @return
     * @throws RemoteException
     *
     * Buscar la información de un ítem de acuerdo al código interno ingresado
     */
    @Override
    public ItemInventario buscarInfoItem(String cinterno) throws RemoteException {
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs = null;
        ItemInventario item = null;
        String statement = "SELECT inventario, descripcion, presentacion, cantidad, precio, ccalidad, cesp FROM item where cinterno = ?;";
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, cinterno);
            rs = ps.executeQuery();
            while (rs.next()) {
                item = new ItemInventario(null, rs.getString(2), rs.getString(3), rs.getFloat(4), rs.getFloat(5), rs.getString(6), rs.getString(1), "", rs.getString(7));
            }

        } catch (SQLException ex) {
            System.out.println("Error función \"Buscar Info Ítem\"");
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
        return item;
    }

    /**
     *
     * @return ArrayList
     * @throws RemoteException
     *
     * Devuelve un listado con los últimos ítems que han sido ingresados.
     */
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
                in = new ItemInventario(numero, lab, descripcion, presentacion, cantidad, precio, cCalidad, cEsp, "", new Float(0));
                lista.add(in);
            }
        } catch (SQLException ex) {
            System.out.println("Error en la función \"CVer últimos\"");
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

    /**
     *
     * @param cinterno
     * @param NIT
     * @param precio
     * @return
     * @throws RemoteException
     *
     * Asocia un ítem a un proveedor
     */
    @Override
    public boolean asociarItem(String cinterno, String NIT, String precio) throws RemoteException {
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
            ps = con.prepareStatement("insert into ixp values(?,?,?);");
            ps.setString(1, NIT);
            ps.setString(2, id);
            ps.setString(3, precio);
            ps.executeUpdate();
            creado = true;
        } catch (SQLException ex) {
            creado = false;
            System.out.println("Error en la función \"Asociar Ítem\"");
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

    /**
     *
     * @return ArrayList
     * @throws RemoteException
     *
     * Genera una lista con todo el inventario existente en la base de datos
     */
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
                in = new ItemInventario(numero, lab, descripcion, presentacion, cantidad, precio, cCalidad, cEsp, "", new Float(0));
                lista.add(in);
            }
        } catch (SQLException ex) {
            System.out.println("Error en la función \"Inventario Item Admin\"");
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
                System.out.println("Error cerrando la conexión");
            }
        }

        return lista;
    }

    /**
     *
     * @return ArrayList
     *
     * Genera una lista con los usuarios actualmente registrados en el sistema.
     */
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
            statement = "select id, nombre, correo, lab from usuario \n";
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                us = new users(rs.getBigDecimal(1), rs.getString(2), rs.getString(3), rs.getString(4));
                user.add(us);
            }
        } catch (SQLException ex) {
            System.out.println("Error en la función \"Crear get Usuarios\"");
        }
        return user;
    }

    /**
     *
     * @param nit
     * @return ArayList
     * @throws RemoteException
     *
     * Genera un listado con los ítems asociados a un proveedor en particular
     */
    @Override
    public ArrayList<ItemInventario> itemxProv(String nit) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select  item.cinterno, item.descripcion, item.presentacion, item.cantidad, item.precio, item.ccalidad, item.cesp"
                + " from ixp, proveedor, item"
                + " where ixp.cinterno =item.cinterno and ixp.nit = proveedor.nit and proveedor.nit= ?;";
        ItemInventario item = null;
        ArrayList<ItemInventario> lista = new ArrayList<>();

        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, nit);
            rs = ps.executeQuery();
            while (rs.next()) {
                item = new ItemInventario(rs.getString(1), rs.getString(2), rs.getString(3), rs.getFloat(4), rs.getFloat(5), rs.getString(6), "", "", rs.getString(7));
                lista.add(item);
            }

        } catch (SQLException ex) {
            System.out.println("Error funcion \"Item por Proveedor \"");
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

    /**
     *
     * @param nit
     * @param cinterno
     * @return ArayList
     * @throws RemoteException
     *
     * Genera un listado con los ítems asociados a un proveedor en particular
     */
    @Override
    public ArrayList<ItemInventario> itemxProv(String nit, String cinterno) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select  item.cinterno, item.descripcion, item.presentacion, item.cantidad, item.precio, item.ccalidad, item.cesp"
                + " from ixp, proveedor, item"
                + " where ixp.cinterno =item.cinterno and ixp.nit = proveedor.nit and proveedor.nit= ? and item.cinterno =?;";
        ItemInventario item = null;
        ArrayList<ItemInventario> lista = new ArrayList<>();
        System.out.println(statement);

        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, nit);
            ps.setString(2, cinterno);
            rs = ps.executeQuery();
            while (rs.next()) {
                item = new ItemInventario(rs.getString(1), rs.getString(2), rs.getString(3), rs.getFloat(4), rs.getFloat(5), rs.getString(6), "", "", rs.getString(7));
                lista.add(item);
            }

        } catch (SQLException ex) {
            System.out.println("Error funcion \"Item por Proveedor \"");
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

    /**
     *
     * @param id
     * @return String
     * @throws RemoteException
     *
     * Buscar el laboratorio al cual pertenece un usuario
     */
    @Override
    public String area(String id) throws RemoteException {
        String area = new String();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.conexion.getConnection();
            String statement = "Select lab from usuario where id = ?";
            ps = con.prepareStatement(statement);
            ps.setString(1, id);
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

    /**
     *
     * @param id
     * @return String
     * @throws RemoteException
     *
     * Busca el nombre de un usuario de acuerdo a su numero de identificacion
     */
    @Override
    public String getUsuario(String id) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select nombre from usuario where id = ?";
        String nombre = new String();
        try {

            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, id);
            rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getRow() != 0) {
                    nombre = rs.getString(1);
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

    /**
     *
     * @param sol
     * @throws RemoteException
     *
     * Crea la solicitud
     */
    @Override
    public void crearSolicitud(solicitudPr sol) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "insert into solicitud_pr (fecha, observaciones, id_solicitante) values(?,?,?);";
        Date d = new Date(sol.getFecha().getTimeInMillis());
        try {
            con = Conexion.conexion.getConnection();

            ps = con.prepareStatement(statement);
            ps.setDate(1, d);
            ps.setString(2, sol.getObservaciones());
            ps.setString(3, sol.getIdSolicitante());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error funcion \"crear solicitud\"");
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
    }

    /**
     * @param id
     * @return BigDecimal
     * @throws RemoteException
     *
     * Método auxiliar para obtener el número de la solicitud.
     */
    @Override
    public BigDecimal solicitudValida(String id) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select num_sol from solicitud_pr where id_solicitante = ? order by num_sol desc";
        BigDecimal valida = null;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, id);
            rs = ps.executeQuery();
            rs.next();
            if (rs.getRow() != 0) {
                valida = rs.getBigDecimal(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     *
     * @param itemsSolicitud
     * @param numSol
     * @throws RemoteException Inserta en la base de datos los ítems solicitados
     * por el usuario asociados a un numero de solicitud .
     */
    @Override
    public void itemxsolicitud(ArrayList<ItemInventario> itemsSolicitud, BigDecimal numSol) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        try {
            con = Conexion.conexion.getConnection();
            statement = "INSERT INTO ITXSOL (CANTIDADSOL,NUM_SOL,CINTERNO ) "
                    + "VALUES (?,?,?)";
            ps = con.prepareStatement(statement);
            for (ItemInventario i : itemsSolicitud) {
                ps.setFloat(1, i.getCantidadSolicitada());
                ps.setBigDecimal(2, numSol);
                ps.setString(3, i.getNumero());
                ps.executeUpdate();
                System.out.println("Se registró: " + i.getNumero());
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     *
     * @return ArrayList
     * @throws RemoteException
     *
     * Genera el listado de las solicitudes que aún no se han revisado
     */
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
            statement = "select s.fecha, s.observaciones, s.num_sol,u.id,  u.nombre, u.lab "
                    + "from usuario u, solicitud_pr s where u.id = s.id_solicitante and s.revisado = 'NO' order by s.num_sol;";
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                fecha.setTime(rs.getDate(1));
                rev = new solicitudPr(fecha, rs.getString(2), rs.getBigDecimal(3), rs.getString(4), rs.getString(5), rs.getString(6));
                NoRevisadas.add(rev);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     *
     * @param numSol
     * @return ArrayList
     * @throws RemoteException
     *
     * Genera el listado de ítems solicitados asociados a un numero de
     * solicitud.
     */
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
            statement = "select cantidad, inventario, item.cinterno, descripcion, presentacion,cantidadsol, precio"
                    + " from item, ITXSOL where item.CINTERNO = ITXSOL.CINTERNO  and ITXSOL.NUM_SOL = ?;";
            System.out.println(statement);
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

    /**
     *
     * @param numSol
     * @return solicitudPr
     * @throws RemoteException
     *
     * Busca los datos de una solicitud en particular
     */
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
            statement = "select fecha, OBSERVACIONES, nombre, lab, id from SOLICITUD_PR, usuario where usuario.ID = SOLICITUD_PR.id_solicitante and num_sol = ?;";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, numSol);
            rs = ps.executeQuery();
            rs.next();
            fecha.setTime(rs.getDate(1));
            solicitud = new solicitudPr(fecha, rs.getString(2), numSol, rs.getString(5), rs.getString(3), rs.getString(4));
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

    /**
     *
     * @param cinterno
     * @return ArrayList
     *
     * Genera una lista con los proveedores asociados a un ítem al momento de
     * realziar una cotización.
     */
    @Override
    public ArrayList<itemxproveedor> tablaCotizacionesIXP(String cinterno) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select  p.nit, p.nombre, i.CINTERNO, it.inventario, i.precio "
                + "from ixp i, proveedor p, item it where i.cinterno = ? and  i.NIT = p.NIT and it.cinterno = i.cinterno;";
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
            }

        } catch (SQLException ex) {
            System.out.println("Error función \"tablaCotizacionesIXP\"");
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

    /**
     *
     * @param idAO
     * @param proveedorNit
     * @param codigo
     * @param lab
     * @param numSol
     * @param precio
     * @return
     * @throws RemoteException
     *
     * Envía la cotización a la base de datos, para ser posteriormente revisada.
     */
    @Override
    public boolean generarCotizacion(String idAO, String proveedorNit, String codigo, String lab, BigDecimal numSol, float precio) throws RemoteException {
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
                    + "(id_ao,nit, cinterno,num_sol, precio_u)"
                    + " VALUES (?,?,?,?,?)";
            ps = con.prepareStatement(statement);
            ps.setString(1, idAO);
            ps.setString(2, proveedorNit);
            ps.setString(3, codigo);
            ps.setBigDecimal(4, numSol);
            ps.setFloat(5, precio);
            ps.executeUpdate();
            act = true;
        } catch (SQLException ex) {
            System.out.println("Error funcion \"generar cotizacion\"");
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
        return act;

    }

    /**
     *
     * @param id
     * @param numSol
     * @param ope
     * @return boolean
     * @throws RemoteException
     *
     * Actualiza el parámetro "Revisado" de la solicitud.
     */
    @Override
    public boolean RevisarSolicitud(String id, BigDecimal numSol, String ope) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        String statement;
        ArrayList<solicitudPr> Revisadas = new ArrayList<>();
        solicitudPr rev = null;
        boolean act = false;
        try {
            con = Conexion.conexion.getConnection();
            statement = "UPDATE SOLICITUD_PR SET REVISADO = ?, id_ao = ? WHERE num_sol = ?";
            ps = con.prepareStatement(statement);
            ps.setString(1, ope);
            ps.setString(2, id);
            ps.setBigDecimal(3, numSol);
            ps.executeUpdate();
            act = true;
            if (ope.equalsIgnoreCase("NO")) {
                eliminarAprobacion(numSol);
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"revisar solicitud\"");
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
        return act;
    }

    /**
     *
     * @param parametro
     * @return ArrayList
     * @throws RemoteException
     *
     * Genera la lista de cotizaciones revisadas o no, dependiendo del parámetro
     * de entrada.
     */
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
            statement = "create table part1 (select  cod.id as cotID, u.nombre as AONom, r.nombre as RANom, cod.nit as pNit, cod.cinterno as itm, cod.precio_u as pre, sol.num_sol as nSol, cod.revisada as revi\n"
                    + "from cotizacion_prod cod, solicitud_pr sol, usuario u, usuario r\n"
                    + "where sol.num_sol = cod.num_sol and u.id =cod.id_ao and u.id = sol.id_ao and sol.id_solicitante = r.id and cod.enorden = 'NO');";
            statement2 = "create view part2 as (select cotid, aonom, ranom, pnit, itm, pre, nsol, sum(cantidadsol) from part1, itxsol where nsol = itxsol.num_sol and itm = itxsol.cinterno and revi = '" + parametro + "' )";
            statement3 = "create view part3 as (select cotid, aonom, ranom, pnit, itm, pre, nsol, cantidadsol from part1, itxsol where nsol = itxsol.num_sol and itm = itxsol.cinterno and revi = '" + parametro + "' )";
            statement4 = "select * from part2\n"
                    + "union\n"
                    + "select * from part3 where part3.cotid <>(select cotid from part2) order by nsol;";

            ps = con.prepareStatement(statement);
            ps.executeUpdate(statement);
            ps = con.prepareStatement(statement2);
            ps.executeUpdate(statement2);
            ps = con.prepareStatement(statement3);
            ps.executeUpdate(statement3);
            ps = con.prepareStatement(statement4);
            //ps.setString(1, parametro);
            rs = ps.executeQuery();
            while (rs.next()) {
                c = new cotizaciones(rs.getBigDecimal(1), rs.getString(2), rs.getString(3), rs.getString(4), "", rs.getString(5), rs.getFloat(6), rs.getBigDecimal(7), rs.getFloat(8), new Float(-1));
                cot.add(c);
                System.out.println("hola");
            }
            ps.executeUpdate("drop table part1;");
            ps.executeUpdate("drop view part2;");
            ps.executeUpdate("drop view part3;");
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en la función \"Crear getCotizaciones\"");
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

        return cot;
    }

    /**
     *
     * @param ap
     * @param par
     * @return
     * @throws RemoteException
     *
     * Aprueba la cantidad de cada solicitud-cotización
     */
    @Override
    public boolean aprobar(aprobacion ap, String par) throws RemoteException {
        Connection con = null;
        boolean validacion = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "";

        try {
            con = Conexion.conexion.getConnection();
            statement = "INSERT INTO APROBADOS (id_cot, cinterno, caprobada, fechaaprob, id_da) VALUES (?,?,?,?,?)";
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, ap.getIdCot());
            ps.setString(2, ap.getCodigo());
            ps.setFloat(3, ap.getAprobado());
            ps.setDate(4, new Date(ap.getFecha().getTimeInMillis()));
            ps.setString(5, ap.getIdDA());
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

    /**
     *
     * @param ap
     * @param parametro
     * @return
     * @throws RemoteException Actualiza el estado de la cotización.
     */
    @Override
    public boolean actCot(aprobacion ap, String parametro) throws RemoteException {
        Connection con = null;
        boolean validacion = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "";
        try {
            con = Conexion.conexion.getConnection();
            statement = "UPDATE COTIZACION_PROD SET REVISADA = ? where id = ?  and cinterno =?";
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

    /**
     *
     * @param ruta
     * @return
     *
     * Genera un archivo con la información de todos los proveedores
     */
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
            for (int j = 0; j < 8; j++) {
                Row fila = hoja.createRow(i);
                Cell aux;
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
                    aux = fila.createCell(j);
                    aux.setCellValue("Contacto");
                    j++;
                    i++;
                }
            }

            for (proveedor t : todos) {
                Row fila = hoja.createRow(i);
                Cell aux;
                for (int j = 0; j < 7; j++) {

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
                    aux = fila.createCell(j);
                    aux.setCellValue(t.getContacto());
                    hoja.autoSizeColumn(j);
                    j++;

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

    /**
     *
     * @param idAo
     * @throws RemoteException Crea la orden de compra en la base de datos.
     */
    @Override
    public void crearOrdenCompra(String idAo) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        try {
            con = Conexion.conexion.getConnection();
            statement = "INSERT INTO ORDENCOMPRA (AO_ID) VALUES (?)";
            ps = con.prepareStatement(statement);
            ps.setString(1, idAo);
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

    /**
     *
     * @param id
     * @return
     * @throws RemoteException
     *
     * Verifica que la insersión de la orden de compra haya sido exitosa
     */
    @Override
    public BigDecimal OrdenValida(String id) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        BigDecimal valida = null;
        try {
            con = Conexion.conexion.getConnection();
            statement = "select NUM_ORDEN from ORDENCOMPRA where AO_id = ? order by NUM_ORDEN desc";
            ps = con.prepareStatement(statement);
            ps.setString(1, id);
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

    /**
     *
     * @param ruta
     * @return
     * @throws RemoteException
     *
     * Genera un archivo con el listado de items en la base de datos.
     */
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

            for (int j = 0; j < 8; j++) {
                Row fila = hoja.createRow(i);
                Cell aux;
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
                    i++;
                }
            }

            for (ItemInventario t : todos) {
                Row fila = hoja.createRow(i);
                Cell aux;
                for (int j = 0; j < 8; j++) {
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
                }
                i++;
            }

            libro.write(archivo);
            archivo.close();

        } catch (IOException ex) {
            System.out.println("Error funcion\"Imprimir Inventario\"");
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }

        return xls;
    }

    /**
     *
     * @param orden
     * @param proveedor
     * @param pedido
     * @param Obs
     * @return
     * @throws RemoteException
     *
     * Genera el listado de ítems que se van a pedir en la orden
     */
    @Override
    public boolean itemsxorden(BigDecimal orden, String proveedor, ArrayList<itemsOrdenCompra> pedido, String Obs) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "insert into itmxorden values(?,?,?,?,?,?)";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(4, orden);
            ps.setString(3, proveedor);
            ps.setString(1, Obs);
            for (itemsOrdenCompra p : pedido) {
                ps.setString(2, p.getCinterno());
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

    /**
     *
     * @param pedido
     * @throws RemoteException
     *
     * Indica si la cotización ya está en una orden de compra.
     */
    @Override
    public void actualizarCotEnOrden(ArrayList<itemsOrdenCompra> pedido) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //Actualizar Tabla Cotizacion
        con = null;
        ps = null;
        rs = null;
        String statement = "UPDATE cotizacion_prod SET `ENORDEN`='SI' WHERE `ID`=?";
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

    /**
     *
     * @param d
     * @return
     * @throws RemoteException
     *
     * Registra el descargo de un ítem en la base de datos.
     */
    @Override
    public boolean realizarDescargo(descargo d) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "INSERT INTO DESCARGO(FECHA, ID_usuario, AREA, CANTIDAD, CINTERNO) VALUES (?,?,?,?,?)";
        boolean valido = false;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setDate(1, new Date(d.getFecha().getTimeInMillis()));
            ps.setString(2, d.getId());
            ps.setString(3, d.getArea());
            ps.setFloat(4, d.getCantidad());
            ps.setString(5, d.getCinterno());
            ps.executeUpdate();
            this.updateCantidad(d.getCinterno(), d.getCantidad() * -1);
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

    /**
     *
     * @param cinterno
     * @param cantidad
     * @return
     * @throws RemoteException
     *
     * Actualiza la cantidad de un ítem en particular.
     */
    @Override
    public boolean updateCantidad(String cinterno, float cantidad) throws RemoteException {
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

    /**
     *
     * @param numOrden
     * @param idRec
     * @param articulos
     * @return
     * @throws RemoteException
     *
     * Función para recibir el pedido y registrarlo en la base de datos.
     */
    @Override
    public boolean recibirPedido(BigDecimal numOrden, String idRec, ArrayList<itemRecep> articulos) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "INSERT INTO RECEPCION (CINTERNO, NUM_ORDEN, FECHALLEGADA, FECHAVENCIMIENTO, CCALIDAD, CESP, MVERIFICACION, ID_USUARIO, PRECIOANTERIOR) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        boolean valido = false;
        float precioAnterior = 0;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(2, numOrden);
            ps.setString(8, idRec);
            for (itemRecep articulo : articulos) {
                ps.setString(1, articulo.getCinterno());
                ps.setDate(3, new Date(articulo.getfLlegada().getTime()));
                ps.setDate(4, new Date(articulo.getfVencimiento().getTime()));
                ps.setString(5, articulo.getcCalidad());
                ps.setString(6, articulo.getcEsp());
                ps.setString(7, articulo.getmVerificacion().toString());
                ps.setFloat(9, 0);
                ps.executeUpdate();
                this.updateCantidad(articulo.getCinterno(), articulo.getcAprobada());
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

    /**
     *
     * @param cinterno
     * @return
     * @throws RemoteException
     *
     * Retorna los datos completos del item relacionado con el cinterno
     * ingresado como parámetro
     */
    @Override
    public ItemInventario datosCompletosItem(String cinterno) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select CINTERNO, INVENTARIO, DESCRIPCION, PRESENTACION, CANTIDAD, PRECIO, CCALIDAD, CESP from item  where CINTERNO = ? ";
        ItemInventario itm = null;
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setString(1, cinterno);
            rs = ps.executeQuery();
            while (rs.next()) {
                itm = new ItemInventario(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7), rs.getString(8), "", 0);
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

    /**
     *
     * @param numorden
     * @param id
     * @return
     * @throws RemoteException
     *
     * Retorna los datos completos de una orden de compra.
     */
    @Override
    public recepcionProd getDatosRec(BigDecimal numorden, String id) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select numorden, (select p.nombre from PROVEEDOR p where p.nit = PROVEEDOR_NIT),PROVEEDOR_NIT, ITEM_CINTERNO, CAPROBADA, PRECIO_U, OBS\n"
                + "from ITMXORDEN where numorden = ?";
        recepcionProd rec = null;
        itemRecep item = null;
        System.out.println(statement);
        ArrayList<itemRecep> articulos = new ArrayList<>();
        try {
            con = Conexion.conexion.getConnection();
            ps = con.prepareStatement(statement);
            ps.setBigDecimal(1, numorden);
            rs = ps.executeQuery();
            while (rs.next()) {
                rec = new recepcionProd(numorden, null, id, null);
                proveedor datosProveedor = this.getDatosProveedor(rs.getString(3));
                rec.setP(datosProveedor);
                item = new itemRecep(rs.getString(4), rs.getString(7), rs.getFloat(5), rs.getFloat(6));
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

    /**
     *
     * @return @throws RemoteException
     *
     * Selecciona las solicitudes relacionadas a un usuario
     */
    @Override
    public ArrayList<solicitudPr> numsSol() throws RemoteException {
        ArrayList<solicitudPr> solicitudes = new ArrayList<>();
        Connection conex = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            String statement = "select sol.FECHA, sol.OBSERVACIONES, sol.NUM_SOL, ra.id, ra.NOMBRE, ra.LAB "
                    + "from SOLICITUD_PR sol, usuario ra "
                    + "where sol.id_solicitante = ra.ID and sol.REVISADO = 'SI' order by sol.NUM_SOL";
            String statement2 = "select s.fecha, s.observaciones, s.num_sol,u.id,  u.nombre, u.lab "
                    + "from usuario u, solicitud_pr s where u.id = s.id_solicitante and s.revisado = 'NO' order by s.num_sol;";

            ArrayList<ArrayList<ItemInventario>> todas = new ArrayList<>();

            conex = Conexion.conexion.getConnection();
            ps = conex.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                solicitudes.add(new solicitudPr(new GregorianCalendar(), rs.getString(2), rs.getBigDecimal(3), rs.getString(4), rs.getString(5), rs.getString(6)));
            }

            ps = conex.prepareStatement(statement2);
            rs = ps.executeQuery();
            while (rs.next()) {
                solicitudes.add(new solicitudPr(new GregorianCalendar(), rs.getString(2), rs.getBigDecimal(3), rs.getString(4), rs.getString(5), rs.getString(6)));
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
                if (conex != null) {
                    conex.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return solicitudes;
    }

    /**
     *
     * @param p
     * @throws RemoteException
     *
     * Asigna los permisosa un usuario
     */
    @Override
    public boolean AsignarPermisos(permisos p) throws RemoteException {
        boolean ok = false;
        Connection conex = null;
        PreparedStatement ps = null;
        try {

            String statement = "INSERT INTO permisos"
                    + "(`usuario_id`,`crearItem`,`crearProv`,`crearUsuario`,"
                    + "`descargarConsumos`,`recibirPedido`,`repDescargos`,`repInventario`,"
                    + "`repUsuarios`,`repProv`,`repixp`,`solProd`,`realizarCot`,`aprobarCot`,"
                    + "`ocompra`,`bloqUs`)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            conex = Conexion.conexion.getConnection();
            ps = conex.prepareStatement(statement);
            ps.setString(1, p.getId());
            ps.setInt(2, p.isCrearItem());
            ps.setInt(3, p.isCrearProveedor());
            ps.setInt(4, p.isCrearUsuario());
            ps.setInt(5, p.isDescargarConsumos());
            ps.setInt(6, p.isRecibirPedidos());
            ps.setInt(7, p.isGenRepDescargos());
            ps.setInt(8, p.isGenRepInventario());
            ps.setInt(9, p.isGenRepUsuarios());
            ps.setInt(10, p.isGenRepProveedores());
            ps.setInt(11, p.isGenRepItemxProveedor());
            ps.setInt(12, p.isSolicitarProductos());
            ps.setInt(13, p.isRealizarCotizaciones());
            ps.setInt(14, p.isAprobarCotizaciones());
            ps.setInt(15, p.isGenerarOrdenesCompra());
            ps.setInt(16, p.isBloquearUsuario());
            ps.executeUpdate();
            ok = true;
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (conex != null) {
                    conex.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return ok;
    }

    public permisos lista(String id) throws RemoteException {
        boolean ok = false;
        Connection conex = null;
        PreparedStatement ps = null;
        permisos p = new permisos();
        ResultSet rs = null;
        try {

            String statement = "select "
                    + "`crearItem`,`crearProv`,`crearUsuario`,"
                    + "`descargarConsumos`,`recibirPedido`,`repDescargos`,`repInventario`,"
                    + "`repUsuarios`,`repProv`,`repixp`,`solProd`,`realizarCot`,`aprobarCot`,"
                    + "`ocompra`,`bloqUs`, genfdc001 from permisos where usuario_id =?";
            conex = Conexion.conexion.getConnection();
            ps = conex.prepareStatement(statement);
            ps.setString(1, id);
            rs = ps.executeQuery();
            rs.next();
            p = new permisos("", rs.getInt(1), rs.getInt(2), rs.getInt(3),
                    rs.getInt(4), rs.getInt(5), rs.getInt(6),
                    rs.getInt(7), rs.getInt(8), rs.getInt(9),
                    rs.getInt(10), rs.getInt(11), rs.getInt(12),
                    rs.getInt(13), rs.getInt(14), rs.getInt(15), rs.getInt(16));

        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (conex != null) {
                    conex.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conexion");
            }
        }
        return p;
    }

    /**
     *
     * @param mes
     * @return
     * @throws RemoteException
     *
     * Genera el informe de descargos de acuerdo al mes ingresado
     */
    @Override
    public ArrayList<informeDescargos> generarInforme(String mes) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "create view gInforme1 as(select i.CINTERNO, i.INVENTARIO, i.DESCRIPCION, i.CANTIDAD as enInventario, d.FECHA as fecha, (select nombre from usuario where id = d.id_usuario) as nombre ,d.id_usuario as id, d.AREA\n"
                + "from item  i right outer join DESCARGO d\n"
                + "on i.CINTERNO = d.CINTERNO);";
        String statement2 = "create view gInforme2 as( select cinterno, inventario, descripcion, eninventario, fecha, nombre, id, area from gInforme1 where gInforme1.fecha like ?);";
        String statement3 = "create view gInforme3 as(select d.CINTERNO, \"\",sum(d.cantidad) as suma from descargo d, descargo dd where dd.id= d.id group by d.CINTERNO);";
        String statement4 = "select distinct  p2.cinterno, p2.inventario, p2.descripcion, p2.eninventario, p3.suma, p2.nombre, p2.id, p2.area\n"
                + " from gInforme2 p2 , gInforme3 p3 where p2.cinterno = p3.CINTERNO; ";
        informeDescargos fila = null;
        ArrayList<informeDescargos> listado = new ArrayList<>();

        System.out.println(statement);
        System.out.println(statement2);
        System.out.println(statement3);
        System.out.println(statement4);
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

    /**
     * *****************************************************************************************************
     */
    //Metodos del administrador
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
                    + "where id_cot = ? and cinterno = ?";
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
    public ArrayList<informeDescargos> generarInformePorLab(String mes) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select D.CINTERNO, \"-\", item.DESCRIPCION, item.CANTIDAD, sum(D.CANTIDAD) \n"
                + "from item right outer join DESCARGO d\n"
                + "on item.CINTERNO = D.CINTERNO and d.FECHA like ?\n"
                + "group by D.CINTERNO, item.DESCRIPCION, item.CANTIDAD\n"
                + "order by D.CINTERNO";
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
        String statement = "select D.CINTERNO, \"-\", item.DESCRIPCION, item.CANTIDAD, sum(D.CANTIDAD), D.FECHA\n"
                + "from item, DESCARGO d\n"
                + "where item.CINTERNO = D.CINTERNO and D.ID_usuario = ?\n"
                + "group by D.CINTERNO, item.DESCRIPCION, item.CANTIDAD, D.FECHA";
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

    //Metodos del usuario
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
            sector = this.area(id.toString());

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
    public ArrayList<solicitudPr> getIdSolicitud(String id) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement;
        ArrayList<solicitudPr> solicitudes = new ArrayList<>();
        solicitudPr solicitud = null;
        GregorianCalendar fecha = new GregorianCalendar();
        try {
            con = Conexion.conexion.getConnection();
            statement = "select  NUM_SOL, FECHA, OBSERVACIONES from SOLICITUD_PR where id_solicitante = ?";
            ps = con.prepareStatement(statement);
            ps.setString(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                fecha.setTime(rs.getDate(2));
                solicitud = new solicitudPr(fecha, rs.getString(3), rs.getBigDecimal(1), id, null, null);
                solicitudes.add(solicitud);
            }
        } catch (SQLException ex) {
            System.out.println("Error funcion \"get Id Solicitud\"");
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
            statement = "select sol.FECHA, sol.OBSERVACIONES, sol.NUM_SOL, ra.NOMBRE, ra.LAB \n"
                    + "from SOLICITUD_PR sol, usuario ra \n"
                    + "where sol.id_solicitante = ra.ID and sol.REVISADO = 'SI' order by sol.NUM_SOL";
            ps = con.prepareStatement(statement);
            rs = ps.executeQuery();
            System.out.println(statement);
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
    public ArrayList<itemsOrdenCompra> pedidoOrdenCompra(String proveedor) throws RemoteException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select i.cinterno, i.inventario, i.descripcion, a.caprobada, i.presentacion, c.precio_u, s.observaciones,c.id\n"
                + "from cotizacion_prod c, aprobados a, item i, solicitud_pr s\n"
                + "where c.nit = ?  and c.revisada = 'SI' and c.id = a.id_cot and c.cinterno = i.cinterno and s.num_sol = c.num_sol and c.enorden ='NO'";
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

    private String encriptar(String psw) {
        String md5Hex = DigestUtils.md5Hex(psw);
        return md5Hex;
    }

}
