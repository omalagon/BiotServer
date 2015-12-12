/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    @NamedQuery(name = "Ixp.findByNit", query = "SELECT i FROM Ixp i WHERE i.ixpPK.nit = :nit"),
    @NamedQuery(name = "Ixp.findByCinterno", query = "SELECT i FROM Ixp i WHERE i.ixpPK.cinterno = :cinterno"),
    @NamedQuery(name = "Ixp.findByPrecio", query = "SELECT i FROM Ixp i WHERE i.precio = :precio")})
public class Ixp implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected IxpPK ixpPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "precio")
    private Double precio;
    @JoinColumn(name = "nit", referencedColumnName = "nit", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Proveedor proveedor;
    @JoinColumn(name = "cinterno", referencedColumnName = "cinterno", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Item item;

    public Ixp() {
    }

    public Ixp(IxpPK ixpPK) {
        this.ixpPK = ixpPK;
    }

    public Ixp(String nit, String cinterno) {
        this.ixpPK = new IxpPK(nit, cinterno);
    }

    public IxpPK getIxpPK() {
        return ixpPK;
    }

    public void setIxpPK(IxpPK ixpPK) {
        this.ixpPK = ixpPK;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ixpPK != null ? ixpPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ixp)) {
            return false;
        }
        Ixp other = (Ixp) object;
        if ((this.ixpPK == null && other.ixpPK != null) || (this.ixpPK != null && !this.ixpPK.equals(other.ixpPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Ixp[ ixpPK=" + ixpPK + " ]";
    }
    
}
