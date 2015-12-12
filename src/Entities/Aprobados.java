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
@Table(name = "aprobados")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Aprobados.findAll", query = "SELECT a FROM Aprobados a"),
    @NamedQuery(name = "Aprobados.findByIdAprobado", query = "SELECT a FROM Aprobados a WHERE a.idAprobado = :idAprobado"),
    @NamedQuery(name = "Aprobados.findByCaprobada", query = "SELECT a FROM Aprobados a WHERE a.caprobada = :caprobada"),
    @NamedQuery(name = "Aprobados.findByFechaAprob", query = "SELECT a FROM Aprobados a WHERE a.fechaAprob = :fechaAprob")})
public class Aprobados implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAprobado")
    private Integer idAprobado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "caprobada")
    private Double caprobada;
    @Column(name = "fechaAprob")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAprob;
    @JoinColumn(name = "id_da", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario idDa;
    @JoinColumn(name = "id_cot", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CotizacionProd idCot;
    @JoinColumn(name = "cinterno", referencedColumnName = "cinterno")
    @ManyToOne(optional = false)
    private Item cinterno;

    public Aprobados() {
    }

    public Aprobados(Integer idAprobado) {
        this.idAprobado = idAprobado;
    }

    public Integer getIdAprobado() {
        return idAprobado;
    }

    public void setIdAprobado(Integer idAprobado) {
        this.idAprobado = idAprobado;
    }

    public Double getCaprobada() {
        return caprobada;
    }

    public void setCaprobada(Double caprobada) {
        this.caprobada = caprobada;
    }

    public Date getFechaAprob() {
        return fechaAprob;
    }

    public void setFechaAprob(Date fechaAprob) {
        this.fechaAprob = fechaAprob;
    }

    public Usuario getIdDa() {
        return idDa;
    }

    public void setIdDa(Usuario idDa) {
        this.idDa = idDa;
    }

    public CotizacionProd getIdCot() {
        return idCot;
    }

    public void setIdCot(CotizacionProd idCot) {
        this.idCot = idCot;
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
        hash += (idAprobado != null ? idAprobado.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Aprobados)) {
            return false;
        }
        Aprobados other = (Aprobados) object;
        if ((this.idAprobado == null && other.idAprobado != null) || (this.idAprobado != null && !this.idAprobado.equals(other.idAprobado))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Aprobados[ idAprobado=" + idAprobado + " ]";
    }
    
}
