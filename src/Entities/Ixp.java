/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author malag
 */
@Entity
@Table(name = "ixp")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ixp.findAll", query = "SELECT i FROM Ixp i"),
    @NamedQuery(name = "Ixp.findById", query = "SELECT i FROM Ixp i WHERE i.id = :id"),
    @NamedQuery(name = "Ixp.findByNit", query = "SELECT i FROM Ixp i WHERE i.nit = :nit"),
    @NamedQuery(name = "Ixp.findByCinterno", query = "SELECT i FROM Ixp i WHERE i.cinterno = :cinterno"),
    @NamedQuery(name = "Ixp.findByCinterno_NIT", query = "SELECT i FROM Ixp i WHERE i.cinterno = :cinterno AND i.nit =:nit"),
    @NamedQuery(name = "Ixp.findByCinterno_Precio", query = "SELECT i FROM Ixp i WHERE i.cinterno = :cinterno AND i.precio = :precio ORDER BY i.id DESC"),
    @NamedQuery(name = "Ixp.findByPrecio", query = "SELECT i FROM Ixp i WHERE i.precio = :precio")})
public class Ixp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "nit")
    private String nit;
    @Basic(optional = false)
    @Column(name = "cinterno")
    private String cinterno;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "precio")
    private Double precio;

    public Ixp() {
    }

    public Ixp(Integer id) {
        this.id = id;
    }

    public Ixp(Integer id, String nit, String cinterno) {
        this.id = id;
        this.nit = nit;
        this.cinterno = cinterno;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getCinterno() {
        return cinterno;
    }

    public void setCinterno(String cinterno) {
        this.cinterno = cinterno;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
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
        if (!(object instanceof Ixp)) {
            return false;
        }
        Ixp other = (Ixp) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Ixp[ id=" + id + " ]";
    }
    
}
