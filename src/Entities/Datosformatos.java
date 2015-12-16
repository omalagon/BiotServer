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
@Table(name = "datosformatos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Datosformatos.findAll", query = "SELECT d FROM Datosformatos d"),
    @NamedQuery(name = "Datosformatos.findById", query = "SELECT d FROM Datosformatos d WHERE d.id = :id"),
    @NamedQuery(name = "Datosformatos.findByRevision", query = "SELECT d FROM Datosformatos d WHERE d.revision = :revision"),
    @NamedQuery(name = "Datosformatos.findByFechaactualizacion", query = "SELECT d FROM Datosformatos d WHERE d.fechaactualizacion = :fechaactualizacion"),
    @NamedQuery(name = "Datosformatos.findByTitulo", query = "SELECT d FROM Datosformatos d WHERE d.titulo = :titulo")})
public class Datosformatos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "revision")
    private String revision;
    @Column(name = "fechaactualizacion")
    private String fechaactualizacion;
    @Column(name = "titulo")
    private String titulo;

    public Datosformatos() {
    }

    public Datosformatos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getFechaactualizacion() {
        return fechaactualizacion;
    }

    public void setFechaactualizacion(String fechaactualizacion) {
        this.fechaactualizacion = fechaactualizacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
        if (!(object instanceof Datosformatos)) {
            return false;
        }
        Datosformatos other = (Datosformatos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Datosformatos[ id=" + id + " ]";
    }
    
}
