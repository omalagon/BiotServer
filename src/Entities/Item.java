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
@Table(name = "item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i"),
    @NamedQuery(name = "Item.findByCinterno", query = "SELECT i FROM Item i WHERE i.cinterno = :cinterno"),
    @NamedQuery(name = "Item.findByInventario", query = "SELECT i FROM Item i WHERE i.inventario = :inventario"),
    @NamedQuery(name = "Item.findByDescripcion", query = "SELECT i FROM Item i WHERE i.descripcion = :descripcion"),
    @NamedQuery(name = "Item.findByPresentacion", query = "SELECT i FROM Item i WHERE i.presentacion = :presentacion"),
    @NamedQuery(name = "Item.findByCantidad", query = "SELECT i FROM Item i WHERE i.cantidad = :cantidad"),
    @NamedQuery(name = "Item.findByPrecio", query = "SELECT i FROM Item i WHERE i.precio = :precio"),
    @NamedQuery(name = "Item.findByCcalidad", query = "SELECT i FROM Item i WHERE i.ccalidad = :ccalidad"),
    @NamedQuery(name = "Item.findByCesp", query = "SELECT i FROM Item i WHERE i.cesp = :cesp")})
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "cinterno")
    private String cinterno;
    @Column(name = "inventario")
    private String inventario;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "presentacion")
    private String presentacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cantidad")
    private Double cantidad;
    @Column(name = "precio")
    private Double precio;
    @Column(name = "ccalidad")
    private String ccalidad;
    @Column(name = "cesp")
    private String cesp;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<Ixp> ixpList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cinterno")
    private List<Descargo> descargoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cinterno")
    private List<Itxsol> itxsolList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cinterno")
    private List<Aprobados> aprobadosList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cinterno")
    private List<Recepcion> recepcionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemCinterno")
    private List<Itmxorden> itmxordenList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cinterno")
    private List<CotizacionProd> cotizacionProdList;

    public Item() {
    }

    public Item(String cinterno) {
        this.cinterno = cinterno;
    }

    public String getCinterno() {
        return cinterno;
    }

    public void setCinterno(String cinterno) {
        this.cinterno = cinterno;
    }

    public String getInventario() {
        return inventario;
    }

    public void setInventario(String inventario) {
        this.inventario = inventario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getCcalidad() {
        return ccalidad;
    }

    public void setCcalidad(String ccalidad) {
        this.ccalidad = ccalidad;
    }

    public String getCesp() {
        return cesp;
    }

    public void setCesp(String cesp) {
        this.cesp = cesp;
    }

    @XmlTransient
    public List<Ixp> getIxpList() {
        return ixpList;
    }

    public void setIxpList(List<Ixp> ixpList) {
        this.ixpList = ixpList;
    }

    @XmlTransient
    public List<Descargo> getDescargoList() {
        return descargoList;
    }

    public void setDescargoList(List<Descargo> descargoList) {
        this.descargoList = descargoList;
    }

    @XmlTransient
    public List<Itxsol> getItxsolList() {
        return itxsolList;
    }

    public void setItxsolList(List<Itxsol> itxsolList) {
        this.itxsolList = itxsolList;
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
        hash += (cinterno != null ? cinterno.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        if ((this.cinterno == null && other.cinterno != null) || (this.cinterno != null && !this.cinterno.equals(other.cinterno))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Item[ cinterno=" + cinterno + " ]";
    }
    
}