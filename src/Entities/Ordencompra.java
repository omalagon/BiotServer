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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "ordencompra")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ordencompra.findAll", query = "SELECT o FROM Ordencompra o"),
    @NamedQuery(name = "Ordencompra.findByNumOrden", query = "SELECT o FROM Ordencompra o WHERE o.numOrden = :numOrden"),
    @NamedQuery(name = "Ordencompra.findByAoId", query = "SELECT o FROM Ordencompra o WHERE o.aoId = :aoId ORDER BY o.numOrden DESC")})
public class Ordencompra implements Serializable {

    @Column(name = "observaciones")
    private String observaciones;

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "num_orden")
    private Double numOrden;
    @Column(name = "ao_id")
    private Double aoId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "numOrden")
    private List<Recepcion> recepcionList;

    public Ordencompra() {
    }

    public Ordencompra(Double numOrden) {
        this.numOrden = numOrden;
    }

    public Double getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(Double numOrden) {
        this.numOrden = numOrden;
    }

    public Double getAoId() {
        return aoId;
    }

    public void setAoId(Double aoId) {
        this.aoId = aoId;
    }

    @XmlTransient
    public List<Recepcion> getRecepcionList() {
        return recepcionList;
    }

    public void setRecepcionList(List<Recepcion> recepcionList) {
        this.recepcionList = recepcionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numOrden != null ? numOrden.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ordencompra)) {
            return false;
        }
        Ordencompra other = (Ordencompra) object;
        if ((this.numOrden == null && other.numOrden != null) || (this.numOrden != null && !this.numOrden.equals(other.numOrden))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Ordencompra[ numOrden=" + numOrden + " ]";
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
}
