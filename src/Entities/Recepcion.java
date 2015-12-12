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
@Table(name = "recepcion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Recepcion.findAll", query = "SELECT r FROM Recepcion r"),
    @NamedQuery(name = "Recepcion.findByFechallegada", query = "SELECT r FROM Recepcion r WHERE r.fechallegada = :fechallegada"),
    @NamedQuery(name = "Recepcion.findByFechavencimiento", query = "SELECT r FROM Recepcion r WHERE r.fechavencimiento = :fechavencimiento"),
    @NamedQuery(name = "Recepcion.findByCcalidad", query = "SELECT r FROM Recepcion r WHERE r.ccalidad = :ccalidad"),
    @NamedQuery(name = "Recepcion.findByCesp", query = "SELECT r FROM Recepcion r WHERE r.cesp = :cesp"),
    @NamedQuery(name = "Recepcion.findByMverificacion", query = "SELECT r FROM Recepcion r WHERE r.mverificacion = :mverificacion"),
    @NamedQuery(name = "Recepcion.findByPrecioanterior", query = "SELECT r FROM Recepcion r WHERE r.precioanterior = :precioanterior")})
public class Recepcion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "fechallegada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechallegada;
    @Column(name = "fechavencimiento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechavencimiento;
    @Column(name = "ccalidad")
    private String ccalidad;
    @Column(name = "cesp")
    private String cesp;
    @Column(name = "mverificacion")
    private String mverificacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "precioanterior")
    private Double precioanterior;
    @JoinColumn(name = "cinterno", referencedColumnName = "cinterno")
    @ManyToOne(optional = false)
    private Item cinterno;
    @JoinColumn(name = "num_orden", referencedColumnName = "num_orden")
    @ManyToOne(optional = false)
    private Ordencompra numOrden;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario idUsuario;

    public Recepcion() {
    }

    public Recepcion(Date fechallegada) {
        this.fechallegada = fechallegada;
    }

    public Date getFechallegada() {
        return fechallegada;
    }

    public void setFechallegada(Date fechallegada) {
        this.fechallegada = fechallegada;
    }

    public Date getFechavencimiento() {
        return fechavencimiento;
    }

    public void setFechavencimiento(Date fechavencimiento) {
        this.fechavencimiento = fechavencimiento;
    }

    public String getCcalidad() {
        return ccalidad;
    }

    public void setCcalidad(String ccalidad) {
        this.ccalidad = ccalidad;
    }

    public String getCesp() {
        return cesp;
    }

    public void setCesp(String cesp) {
        this.cesp = cesp;
    }

    public String getMverificacion() {
        return mverificacion;
    }

    public void setMverificacion(String mverificacion) {
        this.mverificacion = mverificacion;
    }

    public Double getPrecioanterior() {
        return precioanterior;
    }

    public void setPrecioanterior(Double precioanterior) {
        this.precioanterior = precioanterior;
    }

    public Item getCinterno() {
        return cinterno;
    }

    public void setCinterno(Item cinterno) {
        this.cinterno = cinterno;
    }

    public Ordencompra getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(Ordencompra numOrden) {
        this.numOrden = numOrden;
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
        hash += (fechallegada != null ? fechallegada.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Recepcion)) {
            return false;
        }
        Recepcion other = (Recepcion) object;
        if ((this.fechallegada == null && other.fechallegada != null) || (this.fechallegada != null && !this.fechallegada.equals(other.fechallegada))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Recepcion[ fechallegada=" + fechallegada + " ]";
    }
    
}
