/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import Controllers.DatosformatosJpaController;
import Controllers.EvaluacionprovJpaController;
import Controllers.ItemJpaController;
import Controllers.ItmxordenJpaController;
import Controllers.ItxsolJpaController;
import Controllers.IxpJpaController;
import Controllers.OrdencompraJpaController;
import Controllers.PermisosJpaController;
import Controllers.ProveedorJpaController;
import Controllers.RecepcionJpaController;
import Controllers.SolicitudPrJpaController;
import Controllers.TablamostrarJpaController;
import Controllers.UsuarioJpaController;
import Controllers.exceptions.IllegalOrphanException;
import Controllers.exceptions.NonexistentEntityException;
import Controllers.exceptions.PreexistingEntityException;
import Entities.Datosformatos;
import Entities.Evaluacionprov;
import Entities.Item;
import Entities.Itmxorden;
import Entities.Itxsol;
import Entities.Ixp;
import Entities.Ordencompra;
import Entities.Permisos;
import Entities.Proveedor;
import Entities.Recepcion;
import Entities.SolicitudPr;
import Entities.Tablamostrar;
import EstructurasAux.BuscarUsuario;
import EstructurasAux.solicitudPr;
import EstructurasAux.ItemInventario;
import EstructurasAux.aprobacion;
import EstructurasAux.cotizaciones;
import EstructurasAux.datosFormatos;
import EstructurasAux.descargo;
import EstructurasAux.evProv;
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
import com.itextpdf.text.xml.simpleparser.EntitiesToSymbol;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import javax.persistence.*;

/**
 *
 * @author Oscar Dario Malagon Murcia
 */
public class Usuario extends UnicastRemoteObject implements interfaces.Usuario, Serializable {

    private Object Entities;

    public Usuario() throws RemoteException {
        super();
    }

    //Gestión Items
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean hecho = false;
        ItemJpaController itm = new ItemJpaController(emf);
        Item i = new Item(item.getNumero(), item.getInventario(), item.getDescripcion(), item.getPresentacion(), new Double(Float.toString(item.getCantidad())),
                new Double(Float.toString(item.getPrecio())), item.getcCalidad(), item.getCEsp());
        try {
            itm.create(i);
            hecho = true;
            emf.close();
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }

        return hecho;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean hecho = false;
        ItemJpaController itm = new ItemJpaController(emf);
        Item i = itm.findItem(item.getNumero());
        i.setCinterno(item.getNumero().trim());
        i.setInventario(item.getInventario());
        i.setDescripcion(item.getDescripcion());
        i.setPresentacion(item.getPresentacion());
        i.setCantidad(new Double(Float.toString(item.getCantidad())));
        i.setPrecio(new Double(Float.toString(item.getPrecio())));
        i.setCcalidad(item.getcCalidad());
        i.setCesp(item.getCEsp());
        try {
            itm.edit(i);
            hecho = true;
            emf.close();
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hecho;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        ItemJpaController itm = new ItemJpaController(emf);
        Item findItem = itm.findItem(cinterno);
        if (findItem == null) {
            emf.close();
            return new ItemInventario();
        } else {
            return findItem.EntityToItem(findItem);
        }
    }

    /**
     *
     * @param item
     * @return boolean
     * @throws RemoteException
     *
     * Con el cinterno del objeto obtenido por parámetro se elimina de la base
     * de datos
     */
    @Override
    public boolean eliminarItem(ItemInventario item) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean hecho = false;
        ItemJpaController itm = new ItemJpaController(emf);
        try {
            itm.destroy(item.getNumero());
            hecho = true;
            emf.close();
        } catch (IllegalOrphanException | NonexistentEntityException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hecho;
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
     * @return ArrayList
     * @throws RemoteException
     *
     * Genera una lista con todo el inventario existente en la base de datos
     */
    @Override
    public ArrayList<ItemInventario> itemInventarioAdmin() throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        ArrayList<ItemInventario> lista = new ArrayList<>();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Item.InventarioAdmin");
        List<Item> resultList = q.getResultList();
        for (Item i : resultList) {
            lista.add(i.EntityToItem(i));
        }
        emf.close();
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        boolean hecho = false;
        try {
            Query q = em.createNamedQuery("Ixp.findByCinterno_NIT");
            q.setParameter("cinterno", cinterno);
            q.setParameter("nit", NIT);
            IxpJpaController ixpCo = new IxpJpaController(emf);
            List<Ixp> resultList = q.getResultList();
            Ixp itm = new Ixp();
            if (!resultList.isEmpty()) {
                itm = resultList.get(0);
            }
            itm.setCinterno(cinterno);
            itm.setNit(NIT);
            itm.setPrecio(new Double(precio));
            if (resultList.isEmpty()) {
                ixpCo.create(itm);
            } else {
                ixpCo.edit(itm);
            }
            hecho = true;

        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf.close();
        return hecho;
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
    public boolean desasociarItem(String cinterno, String NIT, String precio) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        boolean hecho = false;
        try {
            Query q = em.createNamedQuery("Ixp.findByCinterno_NIT");
            q.setParameter("cinterno", cinterno);
            q.setParameter("nit", NIT);
            IxpJpaController ixpCo = new IxpJpaController(emf);
            List<Ixp> resultList = q.getResultList();
            Ixp itm = new Ixp();
            if (!resultList.isEmpty()) {
                itm = resultList.get(0);
                itm.setCinterno(cinterno);
                itm.setNit(NIT);
                itm.setPrecio(new Double(precio));
                ixpCo.destroy(itm.getId());
            }
            hecho = true;
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf.close();
        return hecho;
    }

//Gestión Proveedores
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean valido = false;
        ProveedorJpaController prov = new ProveedorJpaController(emf);
        Proveedor nuevo = new Proveedor(NIT, Nombre, direccion, correo, telefax, celular, ciudad, contacto);
        try {
            prov.create(nuevo);
            valido = true;
            emf.close();
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valido;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean hecho = false;
        ProveedorJpaController prov = new ProveedorJpaController(emf);
        Proveedor find = prov.findProveedor(NIT);
        find.setNombre(Nombre);
        find.setDir(direccion);
        find.setTel(telefono);
        find.setFax(telefax);
        find.setCiudad(ciudad);
        find.setCorreo(correo);
        find.setCelular(celular);
        find.setContacto(contacto);
        try {
            prov.edit(find);
            hecho = true;
            emf.close();
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hecho;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        ArrayList<proveedor> proveedores = new ArrayList<>();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Proveedor.findAllOrderByName");
        List<Proveedor> resultList = q.getResultList();
        for (Proveedor p : resultList) {
            proveedores.add(new proveedor(p.getNit(), p.getNombre(), p.getDir(), p.getTel(), p.getFax(), p.getCiudad(), p.getCelular(), p.getCorreo(), p.getContacto()));
        }
        emf.close();
        return proveedores;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        ProveedorJpaController prov = new ProveedorJpaController(emf);
        Proveedor find = prov.findProveedor(NIT);
        proveedor p = new proveedor(find.getNit(), find.getNombre(), find.getDir(), find.getTel(), find.getFax(), find.getCiudad(), find.getCelular(), find.getCorreo(), find.getContacto());
        emf.close();
        return p;
    }

    /**
     *
     * @param NIT
     * @return boolean
     * @throws RemoteException
     */
    @Override
    public boolean EliminarProveedor(String NIT) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean hecho = false;
        ProveedorJpaController prov = new ProveedorJpaController(emf);
        try {
            prov.destroy(NIT);
            hecho = true;
            emf.close();
        } catch (IllegalOrphanException | NonexistentEntityException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hecho;
    }

    //Gestión Usuarios
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean valido = false;
        UsuarioJpaController controller = new UsuarioJpaController(emf);
        Entities.Usuario findUsuario = controller.findUsuario(identificacion);
        if (findUsuario.getPsw().equalsIgnoreCase(this.encriptar(contrasena))) {
            valido = true;
        }
        emf.close();
        return valido;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean hecho = false;
        try {

            UsuarioJpaController contr = new UsuarioJpaController(emf);
            Entities.Usuario findUsuario = contr.findUsuario(id);
            findUsuario.setPsw(this.encriptar(nueva));
            contr.edit(findUsuario);
            hecho = true;

        } catch (NonexistentEntityException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf.close();
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query qNombre = em.createNamedQuery("Usuario.findByNombre");
        Query qId = em.createNamedQuery("Usuario.findById");
        ArrayList<BuscarUsuario> lista = new ArrayList<>();
        if (parametro.equalsIgnoreCase("nombre")) {
            qNombre.setParameter("nombre", "%" + valor + "%");
            List<Entities.Usuario> resultList = qNombre.getResultList();
            for (Entities.Usuario u : resultList) {
                lista.add(u.UsuarioToBuscarUsuario(u));
            }
        } else if (parametro.equalsIgnoreCase("id")) {
            qId.setParameter("id", "%" + valor + "%");
            List<Entities.Usuario> resultList = qId.getResultList();
            for (Entities.Usuario u : resultList) {
                lista.add(u.UsuarioToBuscarUsuario(u));
            }
        }
        emf.close();
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        UsuarioJpaController us = new UsuarioJpaController(emf);
        Entities.Usuario findUsuario = us.findUsuario(id);
        emf.close();
        return findUsuario.getPsw().equalsIgnoreCase(this.encriptar(anterior));
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean creado = false;
        UsuarioJpaController contr = new UsuarioJpaController(emf);
        Entities.Usuario nuevo = new Entities.Usuario(identificacion, this.encriptar(psw), nombre, correo, area, contr.findUsuario(id));
        try {
            contr.create(nuevo);
            creado = true;
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf.close();
        return creado;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        UsuarioJpaController us = new UsuarioJpaController(emf);
        Entities.Usuario f = us.findUsuario(id);
        return f.getNombre();
    }

    /**
     *
     * @return ArrayList
     *
     * Genera una lista con los usuarios actualmente registrados en el sistema.
     */
    @Override
    public ArrayList<users> getUsuarios() throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        UsuarioJpaController contr = new UsuarioJpaController(emf);
        List<Entities.Usuario> lst = contr.findUsuarioEntities();
        ArrayList<users> lista = new ArrayList<>();
        for (Entities.Usuario usuario : lst) {
            lista.add(usuario.UsuarioToUsers(usuario));
        }
        emf.close();
        return lista;
    }

    /**
     *
     * @param id
     * @return boolean
     * @throws RemoteException
     *
     * Elimina a un usuario del sistema
     */
    @Override
    public boolean EliminarUsuario(String id) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean hecho = false;
        UsuarioJpaController us = new UsuarioJpaController(emf);
        try {
            PermisosJpaController p = new PermisosJpaController(emf);
            Permisos findPermisos = p.findPermisos(id.trim());
            if (findPermisos != null) {
                p.destroy(id.trim());
            }
            us.destroy(id.trim());
            hecho = true;
        } catch (IllegalOrphanException | NonexistentEntityException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf.close();
        return hecho;
    }

    /**
     *
     * @param id
     * @return
     * @throws RemoteException
     */
    @Override
    public users getDatosUsuario(String id) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        UsuarioJpaController us = new UsuarioJpaController(emf);
        Entities.Usuario findUsuario = us.findUsuario(id);
        emf.close();
        return findUsuario.UsuarioToUsers(findUsuario);
    }

    /**
     *
     * @param u
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean EditarUsuario(users u) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean hecho = false;
        UsuarioJpaController us = new UsuarioJpaController(emf);
        Entities.Usuario findUsuario = us.findUsuario(u.getId().toString());
        findUsuario.setNombre(u.getNombre());
        findUsuario.setCorreo(u.getCorreo());
        findUsuario.setLab(u.getLab());
        try {
            us.edit(findUsuario);
            hecho = true;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf.close();
        return hecho;
    }

    /**
     *
     * @param p
     * @return
     * @throws RemoteException
     *
     * Asigna los permisos a un usuario
     */
    @Override
    public boolean AsignarPermisos(permisos p) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean hecho = false;
        PermisosJpaController per = new PermisosJpaController(emf);
        Permisos lista = per.findPermisos(p.getId());
        Permisos pp = new Permisos(p.getId());
        pp.setCrearItem((p.isCrearItem() == 1 ? '1' : '0'));
        pp.setCrearProv((p.isCrearProveedor()) == 1 ? '1' : '0');
        pp.setCrearUsuario((p.isCrearUsuario()) == 1 ? '1' : '0');
        pp.setDescargarConsumos((p.isDescargarConsumos()) == 1 ? '1' : '0');
        pp.setRecibirPedido((p.isRecibirPedidos()) == 1 ? '1' : '0');
        pp.setRepDescargos((p.isGenRepDescargos()) == 1 ? '1' : '0');
        pp.setRepInventario((p.isGenRepInventario()) == 1 ? '1' : '0');
        pp.setRepUsuarios((p.isGenRepUsuarios()) == 1 ? '1' : '0');
        pp.setRepProv((p.isGenRepProveedores()) == 1 ? '1' : '0');
        pp.setRepixp((p.isGenRepItemxProveedor()) == 1 ? '1' : '0');
        pp.setSolProd((p.isSolicitarProductos()) == 1 ? '1' : '0');
        pp.setRealizarCot((p.isRealizarCotizaciones()) == 1 ? '1' : '0');
        pp.setAprobarCot((p.isAprobarCotizaciones()) == 1 ? '1' : '0');
        pp.setOcompra((p.isGenerarOrdenesCompra()) == 1 ? '1' : '0');
        pp.setBloqUs((p.isBloquearUsuario()) == 1 ? '1' : '0');
        pp.setGenfdc001((p.getGenfdc001()) == 1 ? '1' : '0');
        if (lista == null) {
            try {
                pp.setUsuario(new UsuarioJpaController(emf).findUsuario(p.getId()));
                per.create(pp);
                hecho = true;
            } catch (PreexistingEntityException ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                pp.setUsuario(new UsuarioJpaController(emf).findUsuario(p.getId()));
                per.edit(pp);
                hecho = true;
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        emf.close();
        return hecho;
    }

    /**
     *
     * @param id
     * @return
     * @throws RemoteException
     *
     * Devuelve la lista de permisos correspondientes al usuario con el id
     * ingresado
     */
    @Override
    public permisos lista(String id) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        PermisosJpaController per = new PermisosJpaController(emf);
        Permisos aux = per.findPermisos(id);
        permisos listaPermisos = new permisos();
        if (aux != null) {
            listaPermisos = new permisos(id, (aux.getCrearItem() == '1' ? 1 : 0),
                    (aux.getCrearProv() == '1' ? 1 : 0), (aux.getCrearUsuario() == '1' ? 1 : 0),
                    (aux.getDescargarConsumos() == '1' ? 1 : 0), (aux.getRecibirPedido() == '1' ? 1 : 0),
                    (aux.getRepDescargos() == '1' ? 1 : 0), (aux.getRepInventario() == '1' ? 1 : 0),
                    (aux.getRepUsuarios() == '1' ? 1 : 0), (aux.getRepProv() == '1' ? 1 : 0),
                    (aux.getRepixp() == '1' ? 1 : 0), (aux.getSolProd() == '1' ? 1 : 0),
                    (aux.getRealizarCot() == '1' ? 1 : 0), (aux.getAprobarCot() == '1' ? 1 : 0),
                    (aux.getOcompra() == '1' ? 1 : 0), (aux.getBloqUs() == '1' ? 1 : 0), (aux.getGenfdc001() == '1' ? 1 : 0));

        }
        emf.close();
        return listaPermisos;
    }

    //Descargos
    /**
     *
     * @param descripcion
     * @param presentacion
     * @param inv
     * @return
     * @throws RemoteException
     */
    @Override
    public ArrayList<ItemInventario> busquedaItem(String descripcion, String presentacion, String inv) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Item.busqueda");
        q.setParameter("descripcion", "%" + descripcion + "%");
        q.setParameter("presentacion", "%" + presentacion + "%");
        q.setParameter("inv", "%" + inv + "%");
        List<Item> resultList = q.getResultList();
        if (resultList == null) {
            emf.close();
            return new ArrayList<>();
        } else {
            ArrayList<ItemInventario> lstRetorno = new ArrayList<>();
            for (Item i : resultList) {
                lstRetorno.add(i.EntityToItem(i));
            }
            emf.close();
            return lstRetorno;
        }
    }

    /**
     *
     * @param sol
     * @param itemsSolicitud
     * @return
     * @throws RemoteException
     *
     * Crea la solicitud
     */
    @Override
    public Integer crearSolicitud(solicitudPr sol, ArrayList<ItemInventario> itemsSolicitud) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean solCreada = false;
        boolean itemsEnviados = false;
        SolicitudPr s = new SolicitudPr();
        SolicitudPrJpaController con = new SolicitudPrJpaController(emf);
        s.setIdSolicitante(sol.getIdSolicitante());
        s.setFecha(new java.util.Date(sol.getFecha().getTimeInMillis()));
        s.setObservaciones(sol.getObservaciones());
        s.setRevisado("NO");
        con.create(s);
        solCreada = true;
        Double numSol = 0.0;
        if (solCreada == true) {
            EntityManager em = emf.createEntityManager();
            Query q = em.createNamedQuery("SolicitudPr.getUltima");
            q.setParameter("id", sol.getIdSolicitante());
            numSol = new Double(q.getResultList().get(0).toString());
            ItxsolJpaController conItems = new ItxsolJpaController(emf);
            for (ItemInventario i : itemsSolicitud) {
                if (i.getCantidadSolicitada() <= 0) {
                    itemsEnviados = false;
                } else {
                    conItems.create(new Itxsol(new Double(Float.toString(i.getCantidadSolicitada())), numSol, new Item(i.getNumero()), "NO", 0.0));
                }
            }
            itemsEnviados = true;
            TablamostrarJpaController conTabla = new TablamostrarJpaController(emf);
            Tablamostrar tablamostrar = new Tablamostrar();
            tablamostrar.setIdArchivo(numSol);
            tablamostrar.setIdUsuario(sol.getIdSolicitante());
            tablamostrar.setTipoArchivo("Solicitud");
            tablamostrar.setMostrar("SI");
            conTabla.create(tablamostrar);
            tablamostrar.setTipoArchivo("SolicitudRev");
            tablamostrar.setMostrar("SI");
            conTabla.create(tablamostrar);
            tablamostrar.setTipoArchivo("SolicitudNoRev");
            tablamostrar.setMostrar("SI");
            conTabla.create(tablamostrar);
        }
        if (itemsEnviados == false) {
            try {
                con.destroy(numSol);
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        emf.close();
        return (solCreada && itemsEnviados) ? numSol.intValue() : 0;
    }

    /**
     *
     * @param id
     * @return
     * @throws RemoteException
     */
    @Override
    public ArrayList<solicitudPr> getIdSolicitud(String id) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("SolicitudPr.findByIdSolicitante");
        q.setParameter("idSolicitante", id);
        List<SolicitudPr> resultList = q.getResultList();
        ArrayList<solicitudPr> retorno = new ArrayList<>();
        for (SolicitudPr pr : resultList) {
            solicitudPr s = pr.tosolicitudPr(pr, id);
            users datosUsuario = this.getDatosUsuario(id);
            s.setNombreSolicitante(datosUsuario.getNombre());
            s.setArea(datosUsuario.getLab());
            Query qMostrar = em.createNamedQuery("Tablamostrar.findByAllParameters");
            qMostrar.setParameter("idU", id);
            qMostrar.setParameter("idA", new Double(s.getNum_sol().toString()));
            qMostrar.setParameter("tipoA", "Solicitud");
            Tablamostrar get = new Tablamostrar();
            if (qMostrar.getResultList() != null && !qMostrar.getResultList().isEmpty()) {
                get = (Tablamostrar) qMostrar.getResultList().get(0);
                if (get.getMostrar().equalsIgnoreCase("SI") && get.getTipoArchivo().equalsIgnoreCase("Solicitud")) {
                    retorno.add(s);
                }
            }

        }
        emf.close();
        return retorno;
    }

    /**
     *
     * @param id
     * @return
     * @throws RemoteException
     */
    @Override
    public solicitudPr getSolicitud(String id) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        SolicitudPrJpaController contr = new SolicitudPrJpaController(emf);
        SolicitudPr found = contr.findSolicitudPr(new Double(id));
        users datosUsuario = this.getDatosUsuario(found.getIdSolicitante());
        solicitudPr s = found.tosolicitudPr(found, found.getIdSolicitante());
        s.setNombreSolicitante(datosUsuario.getNombre());
        s.setArea(datosUsuario.getLab());
        emf.close();
        return s;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        ItemJpaController control = new ItemJpaController(emf);
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Itxsol.findByNumSol");
        q.setParameter("numSol", new Double(numSol.toString()));
        List<Itxsol> resultList = q.getResultList();
        ArrayList<ItemInventario> retorno = new ArrayList<>();
        for (Itxsol i : resultList) {
            Item findItem = control.findItem(i.getCinterno().getCinterno());
            ItemInventario itm = findItem.EntityToItem(findItem);
            itm.setCantidadSolicitada(new Float(i.getCantidadsol()));
            retorno.add(itm);
        }
        emf.close();
        return retorno;
    }

    /**
     *
     * @return @throws RemoteException
     *
     * Selecciona las solicitudes relacionadas a un usuario
     */
    @Override
    public ArrayList<solicitudPr> numsSol() throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        ArrayList<solicitudPr> solicitudes = new ArrayList<>();
        SolicitudPrJpaController contr = new SolicitudPrJpaController(emf);
        List<SolicitudPr> findSolicitudPrEntities = contr.findSolicitudPrEntities();
        ItxsolJpaController cont = new ItxsolJpaController(emf);
        for (SolicitudPr f : findSolicitudPrEntities) {
            solicitudPr s = f.tosolicitudPr(f, f.getIdSolicitante());
            users datosUsuario = this.getDatosUsuario(f.getIdSolicitante());
            s.setNombreSolicitante(datosUsuario.getNombre());
            s.setArea(datosUsuario.getLab());
            solicitudes.add(s);
        }
        emf.close();
        return solicitudes;
    }

    /**
     *
     * @param revisado
     * @return
     * @throws RemoteException
     *
     * Genera un listado de solicitudes de acuerdo al parámetro ingresado: todas
     * "", las no revisadas "NO" y las revisadas "SI"
     */
    @Override
    public ArrayList<solicitudPr> getSolicitudes(String revisado, String idUsuario) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        ArrayList<solicitudPr> solicitudes = new ArrayList<>();
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("SolicitudPr.findByRevisado");
        q.setParameter("revisado", "%" + revisado + "%");
        List<SolicitudPr> resultList = q.getResultList();
        for (SolicitudPr s : resultList) {
            solicitudPr sol = s.tosolicitudPr(s, s.getIdSolicitante());
            users datosUsuario = this.getDatosUsuario(s.getIdSolicitante());
            sol.setNombreSolicitante(datosUsuario.getNombre());
            sol.setArea(datosUsuario.getLab());
            if ("".equalsIgnoreCase(idUsuario)) {
                solicitudes.add(sol);
            } else {
                Query qMostrar = em.createNamedQuery("Tablamostrar.findByAllParameters");
                qMostrar.setParameter("idU", idUsuario);
                qMostrar.setParameter("idA", s.getNumSol());
                System.out.println(idUsuario + " " + s.getNumSol());
                if (revisado.equalsIgnoreCase("NO")) {
                    qMostrar.setParameter("tipoA", "SolicitudNoRev");
                    Tablamostrar get = new Tablamostrar();
                    if (qMostrar.getResultList() != null && !qMostrar.getResultList().isEmpty()) {
                        get = (Tablamostrar) qMostrar.getResultList().get(0);
                        if (get.getMostrar().equalsIgnoreCase("SI") && get.getTipoArchivo().equalsIgnoreCase("SolicitudNoRev")) {
                            solicitudes.add(sol);
                            System.out.println("entra");
                        }
                    }
                } else {
                    qMostrar.setParameter("tipoA", "SolicitudRev");
                    Tablamostrar get = new Tablamostrar();
                    if (qMostrar.getResultList() != null && !qMostrar.getResultList().isEmpty()) {
                        get = (Tablamostrar) qMostrar.getResultList().get(0);
                        if (get.getMostrar().equalsIgnoreCase("SI") && get.getTipoArchivo().equalsIgnoreCase("SolicitudRev")) {
                            solicitudes.add(sol);
                        }
                    }
                }
            }
        }
        emf.close();
        return solicitudes;
    }

    //Procesamiento de solicitudes
    /**
     *
     * @param numSol
     * @param Aprobado
     * @return
     * @throws RemoteException
     */
    @Override
    public ArrayList<ItemInventario> getItemsAprobado(BigDecimal numSol, String Aprobado) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        ItemJpaController control = new ItemJpaController(emf);
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Itxsol.findByAprobado");
        q.setParameter("numSol", new Double(numSol.toString()));
        q.setParameter("aprobado", "%" + Aprobado + "%");
        List<Itxsol> resultList = q.getResultList();
        ArrayList<ItemInventario> retorno = new ArrayList<>();
        for (Itxsol i : resultList) {
            Item findItem = control.findItem(i.getCinterno().getCinterno());
            ItemInventario itm = findItem.EntityToItem(findItem);
            itm.setCantidadSolicitada(new Float(i.getCantidadsol()));
            retorno.add(itm);
        }
        emf.close();
        return retorno;
    }

    /**
     *
     * @param items
     * @param sol
     * @param proveedor
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean aprobarItems(ArrayList<ItemInventario> items, solicitudPr sol, ArrayList<String> proveedor) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        boolean itxActualizado = false;
        Double numsol = new Double(sol.getNum_sol().toString());
        try {
            SolicitudPrJpaController contr = new SolicitudPrJpaController(emf);
            SolicitudPr solicitud = contr.findSolicitudPr(numsol);
            solicitud.setIdAo(sol.getIdAO());
            EntityManager em = emf.createEntityManager();
            Query q = em.createNamedQuery("Itxsol.findSol_Item");
            q.setParameter("numSol", numsol);
            ItxsolJpaController con = new ItxsolJpaController(emf);
            int indexProv = 0;
            ArrayList<ItemInventario> itemsAprobado = this.getItemsAprobado(sol.getNum_sol(), "NO");
            for (ItemInventario item : items) {
                ItemJpaController itemJpaController = new ItemJpaController(emf);
                Item findItem = itemJpaController.findItem(item.getNumero());
                findItem.setPrecio(new Double(Float.toString(item.getPrecio())));
                q.setParameter("cinterno", findItem);
                List<Itxsol> resultList = q.getResultList();
                Itxsol get = resultList.get(0);
                Itxsol found = con.findItxsol(get.getId());
                found.setAprobado("SI");
                found.setCantidadaprobada(new Double(item.getCantidadSolicitada()));
                found.setGenerado("NO");
                found.setNitProveedor(proveedor.get(indexProv));
                con.edit(found);
                itxActualizado = true;
                itemJpaController.edit(findItem);
                this.asociarItem(item.getNumero(), proveedor.get(indexProv), Float.toString(item.getPrecio()));
                indexProv++;
                Tablamostrar tablamostrar = new Tablamostrar();
                tablamostrar.setIdArchivo(sol.getNum_sol().doubleValue());
                tablamostrar.setIdUsuario(sol.getIdAO());
                tablamostrar.setTipoArchivo("SolicitudRev");
                tablamostrar.setMostrar("SI");
                TablamostrarJpaController conTabla = new TablamostrarJpaController(emf);
                conTabla.create(tablamostrar);

            }

            if (itemsAprobado.size() == items.size()) {
                solicitud.setRevisado("SI");
                contr.edit(solicitud);
            }

        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf.close();
        return itxActualizado;
    }

    /**
     *
     * @param i
     * @return
     * @throws RemoteException
     */
    @Override
    public ArrayList<itemxproveedor> getProveedorAsociado(itemxproveedor i) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        String cinterno = i.getCinterno();
        Double precio = new Double(Float.toString(i.getPrecio()));
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Ixp.findByCinterno_Precio");
        q.setParameter("cinterno", cinterno);
        q.setParameter("precio", precio);
        List<Ixp> resultList = q.getResultList();
        ArrayList<itemxproveedor> retorno = new ArrayList<>();
        if (!resultList.isEmpty()) {
            for (Ixp ixp : resultList) {
                proveedor datosProveedor = this.getDatosProveedor(ixp.getNit());
                itemxproveedor itx = new itemxproveedor(datosProveedor.getNombre(), new Float(ixp.getPrecio()), ixp.getCinterno());
                itx.setNIT(ixp.getNit());
                retorno.add(itx);
            }
        }
        emf.close();
        return retorno;

    }

    @Override
    public boolean desaprobarItems(ArrayList<ItemInventario> itemsSolicitud, solicitudPr sol, ArrayList<String> proveedor) throws RemoteException {
        boolean itxActualizado = false;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        ArrayList<ItemInventario> itemsAprobado = this.getItemsAprobado(sol.getNum_sol(), "SI");
        ArrayList<ItemInventario> itemsAEditar = new ArrayList<>();
        for (ItemInventario i : itemsSolicitud) {
            for (ItemInventario j : itemsAprobado) {
                if (i.getNumero().equalsIgnoreCase(j.getNumero())) {
                    itemsAEditar.add(i);
                }
            }
        }
        try {
            EntityManager em = emf.createEntityManager();
            Query q = em.createNamedQuery("Itxsol.findSol_Item");
            q.setParameter("numSol", sol.getNum_sol());
            ItxsolJpaController con = new ItxsolJpaController(emf);
            int indexProv = 0;
            for (ItemInventario item : itemsAEditar) {
                ItemJpaController itemJpaController = new ItemJpaController(emf);
                Item findItem = itemJpaController.findItem(item.getNumero());
                findItem.setPrecio(0.0);
                q.setParameter("cinterno", findItem);
                List<Itxsol> resultList = q.getResultList();
                Itxsol get = resultList.get(0);
                Itxsol found = con.findItxsol(get.getId());
                found.setAprobado("NO");
                found.setCantidadaprobada(0.0);
                found.setGenerado("NO");
                found.setNitProveedor("");
                con.edit(found);
                itxActualizado = true;
                itemJpaController.edit(findItem);
                this.desasociarItem(item.getNumero(), proveedor.get(indexProv), Float.toString(item.getPrecio()));
                indexProv++;
            }
            SolicitudPrJpaController s = new SolicitudPrJpaController(emf);
            SolicitudPr found = s.findSolicitudPr(new Double(sol.getNum_sol().toString()));
            found.setRevisado("NO");
            s.edit(found);

        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf.close();
        return itxActualizado;
    }

    /**
     *
     * @param numSol
     * @param cinterno
     * @return
     * @throws RemoteException
     */
    @Override
    public String getCantAprobada(String numSol, String cinterno) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        ItemJpaController itm = new ItemJpaController(emf);
        Item findItem = itm.findItem(cinterno);
        Query q = em.createNamedQuery("Itxsol.findSol_Item");
        q.setParameter("numSol", new BigDecimal(numSol));
        q.setParameter("cinterno", findItem);
        List<Itxsol> resultList = q.getResultList();
        for (Itxsol r : resultList) {
            System.out.println(r.getCinterno().getCinterno());
        }
        emf.close();
        return resultList.get(0).getCantidadaprobada().toString();
    }

    @Override
    public ArrayList<proveedor> getProveedoresConSolicitudes(String generado) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Itxsol.findProveedorByGenerado");
        q.setParameter("generado", "%" + generado + "%");
        ArrayList<proveedor> listaProveedores = new ArrayList<>();
        List<String> resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            for (String i : resultList) {
                proveedor datosProveedor = this.getDatosProveedor(i);
                ArrayList<ItemInventario> itemsAsociados = this.getItemxProveedorSolicitudes(i, "NO");
                if (!itemsAsociados.isEmpty()) {
                    datosProveedor.setItemAsociado(itemsAsociados);
                } else {
                    datosProveedor.setItemAsociado(new ArrayList<ItemInventario>());
                }
                listaProveedores.add(datosProveedor);
            }
            emf.close();
            return listaProveedores;
        } else {
            emf.close();
            return null;
        }

    }

    @Override
    public ArrayList<ItemInventario> getItemxProveedorSolicitudes(String proveedor, String generado) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Itxsol.findItemByProveedor");
        q.setParameter("nit", proveedor);
        List<Itxsol> resultList = q.getResultList();
        ArrayList<ItemInventario> listaItems = new ArrayList<>();
        for (Itxsol i : resultList) {
            if (i.getGenerado().equalsIgnoreCase(generado)) {
                Item cinterno = i.getCinterno();
                ItemInventario EntityToItem = cinterno.EntityToItem(cinterno);
                EntityToItem.setNumSolAsociado(Double.toString(i.getNumSol()));
                EntityToItem.setCantidadAprobada(i.getCantidadaprobada().floatValue());
                listaItems.add(EntityToItem);
            }
        }
        if (!resultList.isEmpty()) {
            emf.close();
            return listaItems;
        } else {
            emf.close();
            return null;
        }
    }

    //Ordenes de Compra
    @Override
    public Double generarOCompra(ArrayList<ItemInventario> listaItems, String idAo) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        OrdencompraJpaController oCompra = new OrdencompraJpaController(emf);
        Ordencompra ordencompra = new Ordencompra();
        ordencompra.setAoId(new Double(idAo));
        oCompra.create(ordencompra);
        Query qOcompra = em.createNamedQuery("Ordencompra.findByAoId");
        qOcompra.setParameter("aoId", new Double(idAo));
        Ordencompra get = (Ordencompra) qOcompra.getResultList().get(0);
        Query q = em.createNamedQuery("Itxsol.findByNumsolAndCinterno");
        ItxsolJpaController contr = new ItxsolJpaController(emf);
        ItmxordenJpaController contrOrden = new ItmxordenJpaController(emf);
        Itmxorden itemOrden = new Itmxorden();
        for (ItemInventario i : listaItems) {
            try {
                q.setParameter("numSol", new Double(i.getNumSolAsociado()));
                q.setParameter("cinterno", new ItemJpaController(emf).findItem(i.getNumero()));
                List<Itxsol> resultList = q.getResultList();
                Itxsol findItxsol = contr.findItxsol(resultList.get(0).getId());
                findItxsol.setGenerado("SI");
                contr.edit(findItxsol);
                itemOrden = new Itmxorden(get.getNumOrden().intValue(), i.getCantidadAprobada(), i.getPrecio());
                itemOrden.setItemCinterno(new ItemJpaController(emf).findItem(i.getNumero()));
                itemOrden.setProveedorNit(new ProveedorJpaController(emf).findProveedor(findItxsol.getNitProveedor()));
                itemOrden.setNumSolAsociado(new Double(i.getNumSolAsociado()));
                itemOrden.setRecibido("NO");
                contrOrden.create(itemOrden);
                Tablamostrar tablamostrar = new Tablamostrar();
                tablamostrar.setIdArchivo(get.getNumOrden());
                tablamostrar.setIdUsuario(idAo);
                tablamostrar.setTipoArchivo("Compra");
                tablamostrar.setMostrar("SI");
                TablamostrarJpaController conTabla = new TablamostrarJpaController(emf);
                conTabla.create(tablamostrar);
            } catch (Exception ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        emf.close();
        return get.getNumOrden();
    }

    @Override
    public int buscarOcompra(ItemInventario i, String proveedor) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Itmxorden.findByAllParameters");
        q.setParameter("nit", new ProveedorJpaController(emf).findProveedor(proveedor));
        q.setParameter("caprobada", i.getCantidadAprobada());
        q.setParameter("precio", i.getPrecio());
        q.setParameter("cinterno", new ItemJpaController(emf).findItem(i.getNumero()));
        List<Itmxorden> resultList = q.getResultList();
        double numorden = resultList.get(0).getNumorden();
        emf.close();
        return new Double(numorden).intValue();
    }

    public boolean devolverOCompra(ItemInventario itm, double numorden) throws RemoteException {
        boolean hecho = false;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
            EntityManager em = emf.createEntityManager();
            Query q = em.createNamedQuery("Itmxorden.findByNumorden_item");
            q.setParameter("numorden", numorden);
            Item findItem = new ItemJpaController(emf).findItem(itm.getNumero());
            q.setParameter("cinterno", findItem);
            List<Itmxorden> resultList = q.getResultList();
            ItmxordenJpaController con = new ItmxordenJpaController(emf);
            Itmxorden f = con.findItmxorden(resultList.get(0).getIdOCompra());
            con.destroy(f.getIdOCompra());
            Query qq = em.createNamedQuery("Itxsol.findSol_Item");
            qq.setParameter("numSol", new Double(itm.getNumSolAsociado()));
            qq.setParameter("cinterno", findItem);
            Itxsol get = (Itxsol) qq.getResultList().get(0);
            ItxsolJpaController itxCont = new ItxsolJpaController(emf);
            Itxsol found = itxCont.findItxsol(get.getId());
            found.setGenerado("NO");
            itxCont.edit(found);
            hecho = true;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hecho;
    }

    //Datos formatos
    /**
     *
     * @param id
     * @return
     * @throws RemoteException
     *
     * Devuelve los datos de un formulario de acuerdo al id ingresado
     */
    @Override
    public datosFormatos getDatos(String id) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        DatosformatosJpaController datos = new DatosformatosJpaController(emf);
        Datosformatos found = datos.findDatosformatos(new Integer(id));
        emf.close();
        return new datosFormatos(found.getRevision(), found.getFechaactualizacion(), found.getTitulo());
    }

    //Auxiliar
    @Override
    public String getFecha() throws RemoteException {
        GregorianCalendar hoy = new GregorianCalendar();
        String cadenaFecha = hoy.get(Calendar.DAY_OF_MONTH) + "/" + (hoy.get(Calendar.MONTH) + 1) + "/" + hoy.get(Calendar.YEAR);
        return cadenaFecha;
    }

    @Override
    public int buscarOrdenByNumSol(ItemInventario i, String proveedor, String numSol) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Itmxorden.findByAllParameters2");
        q.setParameter("nit", new ProveedorJpaController(emf).findProveedor(proveedor));
        q.setParameter("caprobada", i.getCantidadAprobada());
        q.setParameter("precio", i.getPrecio());
        q.setParameter("cinterno", new ItemJpaController(emf).findItem(i.getNumero()));
        q.setParameter("numsol", new Double(numSol));
        List<Itmxorden> resultList = q.getResultList();
        double numorden = -1;
        if (resultList != null && !resultList.isEmpty() && resultList.get(0) != null) {
            numorden = resultList.get(0).getNumorden();
        }
        emf.close();
        return new Double(numorden).intValue();
    }

    //Descargos
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
            Logger.getLogger(Usuario.class
                    .getName()).log(Level.SEVERE, null, ex);
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
        boolean valido = false;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
            Recepcion r = new Recepcion();
            RecepcionJpaController contr = new RecepcionJpaController(emf);
            Ordencompra orden = new Ordencompra();
            for (itemRecep a : articulos) {

                orden = new OrdencompraJpaController(emf).findOrdencompra(new Double(numOrden.toString()));
                ItemJpaController itemJpaController = new ItemJpaController(emf);
                Item findItem = itemJpaController.findItem(a.getCinterno());
                findItem.setCantidad(findItem.getCantidad() + a.getcAprobada());
                findItem.setCcalidad(a.getcCalidad());
                findItem.setCesp(a.getcEsp());
                itemJpaController.edit(findItem);
                r = new Recepcion(a.getfLlegada());
                r.setFechavencimiento(a.getfVencimiento());
                r.setCcalidad(a.getcCalidad());
                r.setCesp(a.getcEsp());
                r.setMverificacion(a.getmVerificacion().toString());
                r.setCinterno(findItem);
                r.setIdUsuario(new UsuarioJpaController(emf).findUsuario(idRec));
                r.setNumOrden(orden);
                r.setPrecioanterior(new Double(a.getPrecio()));
                r.setObservaciones(a.getObs());
                contr.create(r);
                EntityManager em = emf.createEntityManager();
                Query q = em.createNamedQuery("Itmxorden.findByNumorden_item");
                q.setParameter("numorden", orden.getNumOrden());
                ItmxordenJpaController itmcontrol = new ItmxordenJpaController(emf);
                q.setParameter("cinterno", findItem);
                List<Itmxorden> resultList = q.getResultList();
                Itmxorden findItmxorden = itmcontrol.findItmxorden(resultList.get(0).getIdOCompra());
                findItmxorden.setRecibido("SI");
                itmcontrol.edit(findItmxorden);
                valido = true;
            }
            emf.close();
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valido;
    }

    /**
     *
     * @param numOrden
     * @param idRec
     * @param articulos
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean devolverPedido(BigDecimal numOrden, String idRec, ArrayList<itemRecep> articulos) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Itmxorden.findByNumorden");
        q.setParameter("numorden", new Double(numOrden.toString()));
        q.setParameter("recibido", "SI");
        List<Itmxorden> resultList = q.getResultList();
        ItmxordenJpaController itm = new ItmxordenJpaController(emf);
        for (Itmxorden i : resultList) {
            for (itemRecep rec : articulos) {
                if (i.getItemCinterno().getCinterno().equalsIgnoreCase(rec.getCinterno())) {
                    try {
                        this.updateCantidad(rec.getCinterno(), -rec.getcAprobada());
                        i.setRecibido("NO");
                        itm.edit(i);
                    } catch (Exception ex) {
                        Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        q = em.createNamedQuery("Recepcion.findByNumorden");
        q.setParameter("numorden", new Ordencompra(new Double(numOrden.toString())));
        List<Recepcion> recepcion = q.getResultList();
        RecepcionJpaController contrRec = new RecepcionJpaController(emf);
        for (Recepcion r : recepcion) {
            for (itemRecep rec : articulos) {
                if (rec.getCinterno().equalsIgnoreCase(rec.getCinterno())) {
                    try {
                        contrRec.destroy(r.getFechallegada());
                    } catch (NonexistentEntityException ex) {
                        Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return true;

    }

    /**
     *
     * @param e
     * @throws RemoteException
     */
    @Override
    public void evaluarProv(evProv e) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        Evaluacionprov ev = new Evaluacionprov(e.getNit(), e.getNumorden(), e.getEv1(), e.getEv2(), e.getEv3(), e.getEv4(), e.getEv5(), e.getEv6(), e.getEv7(), e.getEv8());
        EvaluacionprovJpaController contr = new EvaluacionprovJpaController(emf);
        contr.create(ev);
        emf.close();
    }

    /**
     *
     * @param e
     * @throws RemoteException
     */
    @Override
    public void borrarEvaluacion(evProv e) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Evaluacionprov.findByNumorden");
        q.setParameter("numorden", e.getNumorden());
        List<Evaluacionprov> ev = q.getResultList();
        EvaluacionprovJpaController contr = new EvaluacionprovJpaController(emf);
        for (Evaluacionprov ee : ev) {
            try {
                contr.destroy(ee.getId());
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        emf.close();
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
            Logger
                    .getLogger(Usuario.class
                            .getName()).log(Level.SEVERE, null, ex);
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
     * @param numorden
     * @param id
     * @return
     * @throws RemoteException
     *
     * Retorna los datos completos de una orden de compra.
     */
    @Override
    public recepcionProd getDatosRec(BigDecimal numorden, String id) throws RemoteException {
        recepcionProd rec = null;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Itmxorden.findByNumorden");
        q.setParameter("numorden", new Double(numorden.toString()));
        q.setParameter("recibido", "NO");
        List<Itmxorden> resultList = q.getResultList();
        ArrayList<itemRecep> items = new ArrayList<>();
        proveedor p = new proveedor();
        for (Itmxorden i : resultList) {
            Proveedor prov = i.getProveedorNit();
            p = new proveedor(prov.getNit(), prov.getNombre(), prov.getDir(), prov.getTel(), prov.getFax(), prov.getCiudad(), prov.getCelular(), prov.getCorreo(), p.getContacto());
            Item itm = i.getItemCinterno();
            items.add(new itemRecep(itm.getCinterno(), "", new Float(i.getCaprobada()), new Float(i.getPrecioU())));
        }
        Query qq = em.createNamedQuery("Ordencompra.findByNumOrden");
        qq.setParameter("numOrden", new Double(numorden.toString()));
        if (qq.getResultList().isEmpty()) {
            return null;
        } else {
            Ordencompra o = (Ordencompra) qq.getResultList().get(0);
            emf.close();
            rec = new recepcionProd(numorden, p, id, items, o.getObservaciones());
            return rec;
        }
    }

    @Override
    public recepcionProd getDatosRec2(BigDecimal numorden, String id) throws RemoteException {
        recepcionProd rec = null;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Itmxorden.findByNumorden2");
        q.setParameter("numorden", new Double(numorden.toString()));
        List<Itmxorden> resultList = q.getResultList();
        ArrayList<itemRecep> items = new ArrayList<>();
        proveedor p = new proveedor();
        for (Itmxorden i : resultList) {
            Proveedor prov = i.getProveedorNit();
            p = new proveedor(prov.getNit(), prov.getNombre(), prov.getDir(), prov.getTel(), prov.getFax(), prov.getCiudad(), prov.getCelular(), prov.getCorreo(), p.getContacto());
            Item itm = i.getItemCinterno();
            items.add(new itemRecep(itm.getCinterno(), "", new Float(i.getCaprobada()), new Float(i.getPrecioU())));
        }
        Query qq = em.createNamedQuery("Ordencompra.findByNumOrden");
        qq.setParameter("numOrden", new Double(numorden.toString()));
        if (qq.getResultList().isEmpty()) {
            return null;
        } else {
            Ordencompra o = (Ordencompra) qq.getResultList().get(0);
            emf.close();
            rec = new recepcionProd(numorden, p, id, items, o.getObservaciones());
            return rec;
        }
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
    public recepcionProd getDatosPedidoRecibido(BigDecimal numorden, String id) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Itmxorden.findByNumorden");
        q.setParameter("numorden", new Double(numorden.toString()));
        q.setParameter("recibido", "SI");
        ArrayList<itemRecep> items = new ArrayList<>();
        List<Itmxorden> resultList = q.getResultList();
        if (!resultList.isEmpty() || resultList != null) {
            Itmxorden get = resultList.get(0);
            proveedor p = new proveedor(get.getProveedorNit().getNit(), get.getProveedorNit().getNombre(), get.getProveedorNit().getDir(), get.getProveedorNit().getTel(), get.getProveedorNit().getFax(), get.getProveedorNit().getCiudad(), get.getProveedorNit().getCelular(), get.getProveedorNit().getCorreo(), get.getProveedorNit().getContacto());
            for (Itmxorden itmxorden : resultList) {
                Item itemCinterno = itmxorden.getItemCinterno();
                Query qq = em.createNamedQuery("Recepcion.findByNumorden");
                qq.setParameter("numorden", new Ordencompra(new Double(numorden.toString())));
                List<Recepcion> recepcion = qq.getResultList();
                for (Recepcion r : recepcion) {
                    if (r.getCinterno().getCinterno().equalsIgnoreCase(itemCinterno.getCinterno())) {
                        itemRecep itmRecibido = new itemRecep(itemCinterno.getCinterno(), r.getFechallegada(), r.getFechavencimiento(),
                                r.getCcalidad(), r.getCesp(), r.getMverificacion(), r.getObservaciones(),
                                new Float(itmxorden.getCaprobada()), new Float(r.getPrecioanterior()));
                        items.add(itmRecibido);
                    }
                }
            }
            recepcionProd recepcionProd = new recepcionProd(numorden, p, "", items, id);
            return recepcionProd;
        } else {
            return null;
        }
    }

    /**
     *
     * @param numorden
     * @return
     * @throws RemoteException
     */
    @Override
    public evProv getEvaluacionProv(double numorden) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Evaluacionprov.findByNumorden");
        q.setParameter("numorden", numorden);
        List<Evaluacionprov> resultList = q.getResultList();
        evProv ev = null;
        for (Evaluacionprov e : resultList) {
            ev = new evProv(e.getNitProv(), e.getNumorden(), e.getEv1(), e.getEv2(), e.getEv3(), e.getEv4(), e.getEv5(), e.getEv6(), e.getEv7(), e.getEv8());
        }
        emf.close();
        return ev;
    }

    /**
     *
     * @return @throws RemoteException
     */
    @Override
    public ArrayList<Integer> numerosDeOrden() throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        OrdencompraJpaController contr = new OrdencompraJpaController(emf);
        List<Ordencompra> resultList = contr.findOrdencompraEntities();
        ArrayList<Integer> ordenes = new ArrayList<>();
        for (Ordencompra r : resultList) {
            ordenes.add(r.getNumOrden().intValue());
        }
        emf.close();
        return ordenes;
    }

    @Override
    public ArrayList<Integer> numerosDeOrdenRecibidas() throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Recepcion.findAllOrdenes");
        List<Ordencompra> resultList = q.getResultList();
        ArrayList<Integer> numorden = new ArrayList<>();
        for (Ordencompra r : resultList) {
            numorden.add(r.getNumOrden().intValue());
        }
        return numorden;
    }

    /**
     *
     * @param idUsuario
     * @param idRecurso
     * @param tipoRecurso
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean ocultar(String idUsuario, String idRecurso, String tipoRecurso) throws RemoteException {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
            EntityManager em = emf.createEntityManager();
            Query q = em.createNamedQuery("Tablamostrar.findByAllParameters");
            q.setParameter("idU", idUsuario);
            q.setParameter("idA", new Double(idRecurso));
            q.setParameter("tipoA", tipoRecurso);
            List<Tablamostrar> resultList = q.getResultList();
            Integer idMostrar = resultList.get(0).getIdMostrar();
            TablamostrarJpaController controller = new TablamostrarJpaController(emf);
            Tablamostrar found = controller.findTablamostrar(idMostrar);
            found.setMostrar("NO");
            controller.edit(found);
            return true;
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
            Logger.getLogger(Usuario.class
                    .getName()).log(Level.SEVERE, null, ex);
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

    /*
     Retorna un objeto tipo fdc_001 que contiene: fecha, area o proceso solicitante, nombre del solicitante,
     observaciones y el auxiliar de oficina
     */
    @Override
    public ArrayList<informeDescargos> generarInformePorLab(String mes) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
            Logger.getLogger(Usuario.class
                    .getName()).log(Level.SEVERE, null, ex);
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
            Logger.getLogger(Usuario.class
                    .getName()).log(Level.SEVERE, null, ex);
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
     * @param cinterno
     * @param cantidad
     * @return
     * @throws RemoteException
     *
     * Actualiza la cantidad de un ítem en particular.
     */
    @Override
    public boolean updateCantidad(String cinterno, float cantidad) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
            Logger.getLogger(Usuario.class
                    .getName()).log(Level.SEVERE, null, ex);
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
    public boolean editarFormato(int formato, datosFormatos datos) throws RemoteException {
        boolean hecho = false;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
        DatosformatosJpaController controller = new DatosformatosJpaController(emf);
        Datosformatos found = controller.findDatosformatos(formato);
        found.setRevision(datos.getRevision());
        found.setFechaactualizacion(datos.getFechaActualizacion());
        found.setTitulo(datos.getTitulo());
        try {
            controller.edit(found);
            hecho = true;
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hecho;
    }

    private String encriptar(String psw) {
        String md5Hex = DigestUtils.md5Hex(psw);
        return md5Hex;
    }

}
