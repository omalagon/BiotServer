/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author malag
 */
@Entity
@Table(name = "proveedor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Proveedor.findAll", query = "SELECT p FROM Proveedor p"),
    @NamedQuery(name = "Proveedor.findByNit", query = "SELECT p FROM Proveedor p WHERE p.nit = :nit"),
    @NamedQuery(name = "Proveedor.findByNombre", query = "SELECT p FROM Proveedor p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Proveedor.findByDir", query = "SELECT p FROM Proveedor p WHERE p.dir = :dir"),
    @NamedQuery(name = "Proveedor.findByCorreo", query = "SELECT p FROM Proveedor p WHERE p.correo = :correo"),
    @NamedQuery(name = "Proveedor.findByTel", query = "SELECT p FROM Proveedor p WHERE p.tel = :tel"),
    @NamedQuery(name = "Proveedor.findByFax", query = "SELECT p FROM Proveedor p WHERE p.fax = :fax"),
    @NamedQuery(name = "Proveedor.findByCelular", query = "SELECT p FROM Proveedor p WHERE p.celular = :celular"),
    @NamedQuery(name = "Proveedor.findByCiudad", query = "SELECT p FROM Proveedor p WHERE p.ciudad = :ciudad"),
    @NamedQuery(name = "Proveedor.findByContacto", query = "SELECT p FROM Proveedor p WHERE p.contacto = :contacto")})
public class Proveedor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "nit")
    private String nit;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "dir")
    private String dir;
    @Column(name = "correo")
    private String correo;
    @Column(name = "tel")
    private String tel;
    @Column(name = "fax")
    private String fax;
    @Column(name = "celular")
    private String celular;
    @Column(name = "ciudad")
    private String ciudad;
    @Column(name = "contacto")
    private String contacto;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proveedor")
    private List<Ixp> ixpList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proveedorNit")
    private List<Itmxorden> itmxordenList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nit")
    private List<CotizacionProd> cotizacionProdList;

    public Proveedor() {
    }

    public Proveedor(String nit) {
        this.nit = nit;
    }

    public Proveedor(String nit, String nombre, String dir) {
        this.nit = nit;
        this.nombre = nombre;
        this.dir = dir;
    }

    public Proveedor(String nit, String nombre, String dir, String correo, String fax, String celular, String ciudad, String contacto) {
        this.nit = nit;
        this.nombre = nombre;
        this.dir = dir;
        this.correo = correo;
        this.fax = fax;
        this.celular = celular;
        this.ciudad = ciudad;
        this.contacto = contacto;
    }

    
    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    @XmlTransient
    public List<Ixp> getIxpList() {
        return ixpList;
    }

    public void setIxpList(List<Ixp> ixpList) {
        this.ixpList = ixpList;
    }

    @XmlTransient
    public List<Itmxorden> getItmxordenList() {
        return itmxordenList;
    }

    public void setItmxordenList(List<Itmxorden> itmxordenList) {
        this.itmxordenList = itmxordenList;
    }

    @XmlTransient
    public List<CotizacionProd> getCotizacionProdList() {
        return cotizacionProdList;
    }

    public void setCotizacionProdList(List<CotizacionProd> cotizacionProdList) {
        this.cotizacionProdList = cotizacionProdList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nit != null ? nit.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Proveedor)) {
            return false;
        }
        Proveedor other = (Proveedor) object;
        if ((this.nit == null && other.nit != null) || (this.nit != null && !this.nit.equals(other.nit))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Proveedor[ nit=" + nit + " ]";
    }
    
}
