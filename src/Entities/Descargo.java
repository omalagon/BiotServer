/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author malag
 */
@Entity
@Table(name = "descargo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Descargo.findAll", query = "SELECT d FROM Descargo d"),
    @NamedQuery(name = "Descargo.findById", query = "SELECT d FROM Descargo d WHERE d.id = :id"),
    @NamedQuery(name = "Descargo.findByFecha", query = "SELECT d FROM Descargo d WHERE d.fecha = :fecha"),
    @NamedQuery(name = "Descargo.findByArea", query = "SELECT d FROM Descargo d WHERE d.area = :area"),
    @NamedQuery(name = "Descargo.findByCantidad", query = "SELECT d FROM Descargo d WHERE d.cantidad = :cantidad")})
public class Descargo implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Double id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "area")
    private String area;
    @Column(name = "cantidad")
    private Double cantidad;
    @JoinColumn(name = "cinterno", referencedColumnName = "cinterno")
    @ManyToOne(optional = false)
    private Item cinterno;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario idUsuario;

    public Descargo() {
    }

    public Descargo(Date fecha,Usuario idUsuario, String area, Double cantidad, Item cinterno ) {
        this.fecha = fecha;
        this.area = area;
        this.cantidad = cantidad;
        this.cinterno = cinterno;
        this.idUsuario = idUsuario;
    }

    public Descargo(Double id) {
        this.id = id;
    }

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Item getCinterno() {
        return cinterno;
    }

    public void setCinterno(Item cinterno) {
        this.cinterno = cinterno;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
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
        if (!(object instanceof Descargo)) {
            return false;
        }
        Descargo other = (Descargo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Descargo[ id=" + id + " ]";
    }
    
}
