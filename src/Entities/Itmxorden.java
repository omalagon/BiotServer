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
@Table(name = "itmxorden")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Itmxorden.findAll", query = "SELECT i FROM Itmxorden i"),
    @NamedQuery(name = "Itmxorden.findByObs", query = "SELECT i FROM Itmxorden i WHERE i.obs = :obs"),
    @NamedQuery(name = "Itmxorden.findByNumorden", query = "SELECT i FROM Itmxorden i WHERE i.itmxordenPK.numorden = :numorden"),
    @NamedQuery(name = "Itmxorden.findByCaprobada", query = "SELECT i FROM Itmxorden i WHERE i.itmxordenPK.caprobada = :caprobada"),
    @NamedQuery(name = "Itmxorden.findByPrecioU", query = "SELECT i FROM Itmxorden i WHERE i.itmxordenPK.precioU = :precioU")})
public class Itmxorden implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ItmxordenPK itmxordenPK;
    @Column(name = "obs")
    private String obs;
    @JoinColumn(name = "item_cinterno", referencedColumnName = "cinterno")
    @ManyToOne(optional = false)
    private Item itemCinterno;
    @JoinColumn(name = "proveedor_nit", referencedColumnName = "nit")
    @ManyToOne(optional = false)
    private Proveedor proveedorNit;

    public Itmxorden() {
    }

    public Itmxorden(ItmxordenPK itmxordenPK) {
        this.itmxordenPK = itmxordenPK;
    }

    public Itmxorden(double numorden, double caprobada, double precioU) {
        this.itmxordenPK = new ItmxordenPK(numorden, caprobada, precioU);
    }

    public ItmxordenPK getItmxordenPK() {
        return itmxordenPK;
    }

    public void setItmxordenPK(ItmxordenPK itmxordenPK) {
        this.itmxordenPK = itmxordenPK;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public Item getItemCinterno() {
        return itemCinterno;
    }

    public void setItemCinterno(Item itemCinterno) {
        this.itemCinterno = itemCinterno;
    }

    public Proveedor getProveedorNit() {
        return proveedorNit;
    }

    public void setProveedorNit(Proveedor proveedorNit) {
        this.proveedorNit = proveedorNit;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (itmxordenPK != null ? itmxordenPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Itmxorden)) {
            return false;
        }
        Itmxorden other = (Itmxorden) object;
        if ((this.itmxordenPK == null && other.itmxordenPK != null) || (this.itmxordenPK != null && !this.itmxordenPK.equals(other.itmxordenPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Itmxorden[ itmxordenPK=" + itmxordenPK + " ]";
    }
    
}
