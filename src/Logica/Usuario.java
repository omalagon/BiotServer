/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import Controllers.DatosformatosJpaController;
import Controllers.ItemJpaController;
import Controllers.ItxsolJpaController;
import Controllers.IxpJpaController;
import Controllers.PermisosJpaController;
import Controllers.ProveedorJpaController;
import Controllers.SolicitudPrJpaController;
import Controllers.UsuarioJpaController;
import Controllers.exceptions.IllegalOrphanException;
import Controllers.exceptions.NonexistentEntityException;
import Controllers.exceptions.PreexistingEntityException;
import Entities.Datosformatos;
import Entities.Item;
import Entities.Itxsol;
import Entities.Ixp;
import Entities.Permisos;
import Entities.Proveedor;
import Entities.SolicitudPr;
import EstructurasAux.BuscarUsuario;
import EstructurasAux.solicitudPr;
import EstructurasAux.ItemInventario;
import EstructurasAux.aprobacion;
import EstructurasAux.cotizaciones;
import EstructurasAux.datosFormatos;
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
            Ixp itm= new Ixp();
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
            Ixp itm= new Ixp();
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
        }
        if (itemsEnviados == false) {
            try {
                con.destroy(numSol);
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
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
            retorno.add(s);
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
    public ArrayList<solicitudPr> getSolicitudes(String revisado) throws RemoteException {
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
            solicitudes.add(sol);
        }
        //emf.close();
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
            int indexProv=0;
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
                con.edit(found);
                itxActualizado = true;
                itemJpaController.edit(findItem);
                System.out.println(proveedor.get(indexProv));
                System.out.println(proveedor.size());
                this.asociarItem(item.getNumero(), proveedor.get(indexProv), Float.toString(item.getPrecio()));
                indexProv++;
            }
            q = em.createNamedQuery("Itxsol.findByNumSol");
            q.setParameter("numSol", numsol);
            List resultList = q.getResultList();
            if (itxActualizado && resultList.size() == items.size()) {
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
                retorno.add(new itemxproveedor(datosProveedor.getNombre(), new Float(ixp.getPrecio()), ixp.getCinterno()));
            }
        }
        emf.close();
        return retorno;

    }

    @Override
    public boolean desaprobarItems(ArrayList<ItemInventario> itemsSolicitud, solicitudPr sol, String proveedor) throws RemoteException {
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

                con.edit(found);

                itxActualizado = true;
                itemJpaController.edit(findItem);
                this.desasociarItem(item.getNumero(), proveedor, Float.toString(item.getPrecio()));
            }
            ArrayList<ItemInventario> listado = this.getItemsAprobado(sol.getNum_sol(), "");
            if (listado.size() == itemsSolicitud.size()) //Desaprobar todos
            {
                SolicitudPrJpaController s = new SolicitudPrJpaController(emf);
                SolicitudPr found = s.findSolicitudPr(new Double(sol.getNum_sol().toString()));
                found.setRevisado("NO");
                s.edit(found);
            }
        } catch (Exception ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return itxActualizado;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
     * @param numSol
     * @return solicitudPr
     * @throws RemoteException
     *
     * Busca los datos de una solicitud en particular
     */
    @Override
    public solicitudPr getSolicitud_NumSol(BigDecimal numSol) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
            sector = this.getDatosUsuario(id.toString()).getLab();

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
    public ArrayList<itemxproveedor> getItemxproveedor(String inv, String codigo) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Biot_ServerPU");
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
