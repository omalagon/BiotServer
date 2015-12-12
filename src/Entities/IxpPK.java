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
public class IxpPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "nit")
    private String nit;
    @Basic(optional = false)
    @Column(name = "cinterno")
    private String cinterno;

    public IxpPK() {
    }

    public IxpPK(String nit, String cinterno) {
        this.nit = nit;
        this.cinterno = cinterno;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nit != null ? nit.hashCode() : 0);
        hash += (cinterno != null ? cinterno.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IxpPK)) {
            return false;
        }
        IxpPK other = (IxpPK) object;
        if ((this.nit == null && other.nit != null) || (this.nit != null && !this.nit.equals(other.nit))) {
            return false;
        }
        if ((this.cinterno == null && other.cinterno != null) || (this.cinterno != null && !this.cinterno.equals(other.cinterno))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.IxpPK[ nit=" + nit + ", cinterno=" + cinterno + " ]";
    }
    
}
