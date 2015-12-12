/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author malag
 */
@Embeddable
public class ItmxordenPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "numorden")
    private double numorden;
    @Basic(optional = false)
    @Column(name = "caprobada")
    private double caprobada;
    @Basic(optional = false)
    @Column(name = "precio_u")
    private double precioU;

    public ItmxordenPK() {
    }

    public ItmxordenPK(double numorden, double caprobada, double precioU) {
        this.numorden = numorden;
        this.caprobada = caprobada;
        this.precioU = precioU;
    }

    public double getNumorden() {
        return numorden;
    }

    public void setNumorden(double numorden) {
        this.numorden = numorden;
    }

    public double getCaprobada() {
        return caprobada;
    }

    public void setCaprobada(double caprobada) {
        this.caprobada = caprobada;
    }

    public double getPrecioU() {
        return precioU;
    }

    public void setPrecioU(double precioU) {
        this.precioU = precioU;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) numorden;
        hash += (int) caprobada;
        hash += (int) precioU;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItmxordenPK)) {
            return false;
        }
        ItmxordenPK other = (ItmxordenPK) object;
        if (this.numorden != other.numorden) {
            return false;
        }
        if (this.caprobada != other.caprobada) {
            return false;
        }
        if (this.precioU != other.precioU) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.ItmxordenPK[ numorden=" + numorden + ", caprobada=" + caprobada + ", precioU=" + precioU + " ]";
    }
    
}
