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
@Table(name = "tablamostrar")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tablamostrar.findAll", query = "SELECT t FROM Tablamostrar t"),
    @NamedQuery(name = "Tablamostrar.findByAllParameters", query = "SELECT t FROM Tablamostrar t where t.idUsuario =:idU AND t.idArchivo=:idA AND t.tipoArchivo = :tipoA"),
    @NamedQuery(name = "Tablamostrar.findByIdMostrar", query = "SELECT t FROM Tablamostrar t WHERE t.idMostrar = :idMostrar"),
    @NamedQuery(name = "Tablamostrar.findByTipoArchivo", query = "SELECT t FROM Tablamostrar t WHERE t.tipoArchivo = :tipoArchivo"),
    @NamedQuery(name = "Tablamostrar.findByIdArchivo", query = "SELECT t FROM Tablamostrar t WHERE t.idArchivo = :idArchivo"),
    @NamedQuery(name = "Tablamostrar.findByIdUsuario", query = "SELECT t FROM Tablamostrar t WHERE t.idUsuario = :idUsuario"),
    @NamedQuery(name = "Tablamostrar.findByMostrar", query = "SELECT t FROM Tablamostrar t WHERE t.mostrar = :mostrar")})
public class Tablamostrar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idMostrar")
    private Integer idMostrar;
    @Column(name = "tipoArchivo")
    private String tipoArchivo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "idArchivo")
    private Double idArchivo;
    @Column(name = "idUsuario")
    private String idUsuario;
    @Column(name = "mostrar")
    private String mostrar;

    public Tablamostrar() {
    }

    public Tablamostrar(Integer idMostrar) {
        this.idMostrar = idMostrar;
    }

    public Integer getIdMostrar() {
        return idMostrar;
    }

    public void setIdMostrar(Integer idMostrar) {
        this.idMostrar = idMostrar;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public Double getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(Double idArchivo) {
        this.idArchivo = idArchivo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMostrar() {
        return mostrar;
    }

    public void setMostrar(String mostrar) {
        this.mostrar = mostrar;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMostrar != null ? idMostrar.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tablamostrar)) {
            return false;
        }
        Tablamostrar other = (Tablamostrar) object;
        if ((this.idMostrar == null && other.idMostrar != null) || (this.idMostrar != null && !this.idMostrar.equals(other.idMostrar))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Tablamostrar[ idMostrar=" + idMostrar + " ]";
    }
    
}
