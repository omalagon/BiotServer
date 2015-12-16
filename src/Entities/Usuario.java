/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import EstructurasAux.BuscarUsuario;
import EstructurasAux.users;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author malag
 */
@Entity
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u WHERE u.id like :id"),
    @NamedQuery(name = "Usuario.findByPsw", query = "SELECT u FROM Usuario u WHERE u.psw = :psw"),
    @NamedQuery(name = "Usuario.findByNombre", query = "SELECT u FROM Usuario u WHERE u.nombre like :nombre"),
    @NamedQuery(name = "Usuario.findByCorreo", query = "SELECT u FROM Usuario u WHERE u.correo = :correo"),
    @NamedQuery(name = "Usuario.findByLab", query = "SELECT u FROM Usuario u WHERE u.lab = :lab")})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private String id;
    @Basic(optional = false)
    @Column(name = "psw")
    private String psw;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "correo")
    private String correo;
    @Column(name = "lab")
    private String lab;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario")
    private List<Descargo> descargoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idDa")
    private List<Aprobados> aprobadosList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario")
    private List<Recepcion> recepcionList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "usuario")
    private Permisos permisos;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idAo")
    private List<CotizacionProd> cotizacionProdList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "id1")
    private List<Usuario> usuarioList;
    @JoinColumn(name = "id1", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario id1;

    public Usuario() {
    }

    public Usuario(String id) {
        this.id = id;
    }

    public Usuario(String id, String psw, String nombre) {
        this.id = id;
        this.psw = psw;
        this.nombre = nombre;
    }

    public Usuario(String id, String psw, String nombre, String correo, String lab, Usuario id1) {
        this.id = id;
        this.psw = psw;
        this.nombre = nombre;
        this.correo = correo;
        this.lab = lab;
        this.id1 = id1;
    }
    
    public Usuario(String id, String psw, String nombre, String correo, String lab) {
        this.id = id;
        this.psw = psw;
        this.nombre = nombre;
        this.correo = correo;
        this.lab = lab;
    }
    
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }

    @XmlTransient
    public List<Descargo> getDescargoList() {
        return descargoList;
    }

    public void setDescargoList(List<Descargo> descargoList) {
        this.descargoList = descargoList;
    }

    @XmlTransient
    public List<Aprobados> getAprobadosList() {
        return aprobadosList;
    }

    public void setAprobadosList(List<Aprobados> aprobadosList) {
        this.aprobadosList = aprobadosList;
    }

    @XmlTransient
    public List<Recepcion> getRecepcionList() {
        return recepcionList;
    }

    public void setRecepcionList(List<Recepcion> recepcionList) {
        this.recepcionList = recepcionList;
    }

    public Permisos getPermisos() {
        return permisos;
    }

    public void setPermisos(Permisos permisos) {
        this.permisos = permisos;
    }

    @XmlTransient
    public List<CotizacionProd> getCotizacionProdList() {
        return cotizacionProdList;
    }

    public void setCotizacionProdList(List<CotizacionProd> cotizacionProdList) {
        this.cotizacionProdList = cotizacionProdList;
    }

    @XmlTransient
    public List<Usuario> getUsuarioList() {
        return usuarioList;
    }

    public void setUsuarioList(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    public Usuario getId1() {
        return id1;
    }

    public void setId1(Usuario id1) {
        this.id1 = id1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Usuario[ id=" + id + " ]";
    }
    
    public users UsuarioToUsers(Usuario u)
    {
        users uu = new users(new BigDecimal(u.getId()), u.getNombre(), u.getCorreo(), u.getLab());
        return uu;
    }
    
    public BuscarUsuario UsuarioToBuscarUsuario(Usuario u)
    {
        BuscarUsuario uu = new BuscarUsuario(u.getNombre(), u.getId(), u.getLab());
        return uu;
    }
}
