/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author malag
 */
@Entity
@Table(name = "solicitud_pr")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SolicitudPr.findAll", query = "SELECT s FROM SolicitudPr s"),
    @NamedQuery(name = "SolicitudPr.findByFecha", query = "SELECT s FROM SolicitudPr s WHERE s.fecha = :fecha"),
    @NamedQuery(name = "SolicitudPr.findByObservaciones", query = "SELECT s FROM SolicitudPr s WHERE s.observaciones = :observaciones"),
    @NamedQuery(name = "SolicitudPr.findByIdSolicitante", query = "SELECT s FROM SolicitudPr s WHERE s.idSolicitante = :idSolicitante"),
    @NamedQuery(name = "SolicitudPr.findByNumSol", query = "SELECT s FROM SolicitudPr s WHERE s.numSol = :numSol"),
    @NamedQuery(name = "SolicitudPr.findByRevisado", query = "SELECT s FROM SolicitudPr s WHERE s.revisado = :revisado"),
    @NamedQuery(name = "SolicitudPr.findByIdAo", query = "SELECT s FROM SolicitudPr s WHERE s.idAo = :idAo")})
public class SolicitudPr implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "observaciones")
    private String observaciones;
    @Basic(optional = false)
    @Column(name = "id_solicitante")
    private String idSolicitante;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "num_sol")
    private Double numSol;
    @Column(name = "revisado")
    private String revisado;
    @Column(name = "id_ao")
    private String idAo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "numSol")
    private List<CotizacionProd> cotizacionProdList;

    public SolicitudPr() {
    }

    public SolicitudPr(Double numSol) {
        this.numSol = numSol;
    }

    public SolicitudPr(Double numSol, String idSolicitante) {
        this.numSol = numSol;
        this.idSolicitante = idSolicitante;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getIdSolicitante() {
        return idSolicitante;
    }

    public void setIdSolicitante(String idSolicitante) {
        this.idSolicitante = idSolicitante;
    }

    public Double getNumSol() {
        return numSol;
    }

    public void setNumSol(Double numSol) {
        this.numSol = numSol;
    }

    public String getRevisado() {
        return revisado;
    }

    public void setRevisado(String revisado) {
        this.revisado = revisado;
    }

    public String getIdAo() {
        return idAo;
    }

    public void setIdAo(String idAo) {
        this.idAo = idAo;
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
        hash += (numSol != null ? numSol.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SolicitudPr)) {
            return false;
        }
        SolicitudPr other = (SolicitudPr) object;
        if ((this.numSol == null && other.numSol != null) || (this.numSol != null && !this.numSol.equals(other.numSol))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.SolicitudPr[ numSol=" + numSol + " ]";
    }
    
}
