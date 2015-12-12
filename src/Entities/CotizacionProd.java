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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "cotizacion_prod")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CotizacionProd.findAll", query = "SELECT c FROM CotizacionProd c"),
    @NamedQuery(name = "CotizacionProd.findById", query = "SELECT c FROM CotizacionProd c WHERE c.id = :id"),
    @NamedQuery(name = "CotizacionProd.findByPrecioU", query = "SELECT c FROM CotizacionProd c WHERE c.precioU = :precioU"),
    @NamedQuery(name = "CotizacionProd.findByRevisada", query = "SELECT c FROM CotizacionProd c WHERE c.revisada = :revisada"),
    @NamedQuery(name = "CotizacionProd.findByEnorden", query = "SELECT c FROM CotizacionProd c WHERE c.enorden = :enorden")})
public class CotizacionProd implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Double id;
    @Column(name = "precio_u")
    private Double precioU;
    @Column(name = "revisada")
    private String revisada;
    @Column(name = "enorden")
    private String enorden;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCot")
    private List<Aprobados> aprobadosList;
    @JoinColumn(name = "num_sol", referencedColumnName = "num_sol")
    @ManyToOne(optional = false)
    private SolicitudPr numSol;
    @JoinColumn(name = "nit", referencedColumnName = "nit")
    @ManyToOne(optional = false)
    private Proveedor nit;
    @JoinColumn(name = "id_ao", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario idAo;
    @JoinColumn(name = "cinterno", referencedColumnName = "cinterno")
    @ManyToOne(optional = false)
    private Item cinterno;

    public CotizacionProd() {
    }

    public CotizacionProd(Double id) {
        this.id = id;
    }

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public Double getPrecioU() {
        return precioU;
    }

    public void setPrecioU(Double precioU) {
        this.precioU = precioU;
    }

    public String getRevisada() {
        return revisada;
    }

    public void setRevisada(String revisada) {
        this.revisada = revisada;
    }

    public String getEnorden() {
        return enorden;
    }

    public void setEnorden(String enorden) {
        this.enorden = enorden;
    }

    @XmlTransient
    public List<Aprobados> getAprobadosList() {
        return aprobadosList;
    }

    public void setAprobadosList(List<Aprobados> aprobadosList) {
        this.aprobadosList = aprobadosList;
    }

    public SolicitudPr getNumSol() {
        return numSol;
    }

    public void setNumSol(SolicitudPr numSol) {
        this.numSol = numSol;
    }

    public Proveedor getNit() {
        return nit;
    }

    public void setNit(Proveedor nit) {
        this.nit = nit;
    }

    public Usuario getIdAo() {
        return idAo;
    }

    public void setIdAo(Usuario idAo) {
        this.idAo = idAo;
    }

    public Item getCinterno() {
        return cinterno;
    }

    public void setCinterno(Item cinterno) {
        this.cinterno = cinterno;
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
        if (!(object instanceof CotizacionProd)) {
            return false;
        }
        CotizacionProd other = (CotizacionProd) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.CotizacionProd[ id=" + id + " ]";
    }
    
}
