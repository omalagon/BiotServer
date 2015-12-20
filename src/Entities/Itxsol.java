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
@Table(name = "itxsol")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Itxsol.findAll", query = "SELECT i FROM Itxsol i"),
    @NamedQuery(name = "Itxsol.findSol_Item", query = "SELECT i FROM Itxsol i where i.numSol = :numSol AND i.cinterno = :cinterno"),
    @NamedQuery(name = "Itxsol.findProveedorByGenerado", query = "SELECT DISTINCT i.nitProveedor FROM Itxsol i where i.generado like :generado"),
    @NamedQuery(name = "Itxsol.findByNumsolAndCinterno", query = "SELECT i FROM Itxsol i where i.numSol =:numSol AND i.cinterno = :cinterno"),
    @NamedQuery(name = "Itxsol.findItemsAsociadosAProveedor", query = "SELECT i FROM Itxsol i where i.nitProveedor =:nit"),
    @NamedQuery(name = "Itxsol.findItemByProveedor", query = "SELECT i FROM Itxsol i where i.nitProveedor =:nit"),
    @NamedQuery(name = "Itxsol.findById", query = "SELECT i FROM Itxsol i WHERE i.id = :id"),
    @NamedQuery(name = "Itxsol.findByCantidadsol", query = "SELECT i FROM Itxsol i WHERE i.cantidadsol = :cantidadsol"),
    @NamedQuery(name = "Itxsol.findByAprobado", query = "SELECT i FROM Itxsol i WHERE i.numSol = :numSol AND i.aprobado like :aprobado"),
    @NamedQuery(name = "Itxsol.findByNumSol", query = "SELECT i FROM Itxsol i WHERE i.numSol = :numSol")})
public class Itxsol implements Serializable {

    @Column(name = "generado")
    private String generado;
    @Column(name = "nitProveedor")
    private String nitProveedor;
    @Column(name = "cantidadaprobada")
    private Double cantidadaprobada;

    @Column(name = "aprobado")
    private String aprobado;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cantidadsol")
    private Double cantidadsol;
    @Basic(optional = false)
    @Column(name = "num_sol")
    private double numSol;
    @JoinColumn(name = "cinterno", referencedColumnName = "cinterno")
    @ManyToOne(optional = false)
    private Item cinterno;

    public Itxsol() {
    }

    public Itxsol(Double cantidadsol, double numSol, Item cinterno) {
        this.cantidadsol = cantidadsol;
        this.numSol = numSol;
        this.cinterno = cinterno;
    }

    public Itxsol(Double cantidadsol,  double numSol,Item cinterno, String aprobado, Double cantidadaprobada ) {
        this.cantidadaprobada = cantidadaprobada;
        this.aprobado = aprobado;
        this.cantidadsol = cantidadsol;
        this.numSol = numSol;
        this.cinterno = cinterno;
    }
    

    public Itxsol(Integer id) {
        this.id = id;
    }

    public Itxsol(Integer id, double numSol) {
        this.id = id;
        this.numSol = numSol;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getCantidadsol() {
        return cantidadsol;
    }

    public void setCantidadsol(Double cantidadsol) {
        this.cantidadsol = cantidadsol;
    }

    public double getNumSol() {
        return numSol;
    }

    public void setNumSol(double numSol) {
        this.numSol = numSol;
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
        if (!(object instanceof Itxsol)) {
            return false;
        }
        Itxsol other = (Itxsol) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Itxsol[ id=" + id + " ]";
    }

    public String getAprobado() {
        return aprobado;
    }

    public void setAprobado(String aprobado) {
        this.aprobado = aprobado;
    }

    public Double getCantidadaprobada() {
        return cantidadaprobada;
    }

    public void setCantidadaprobada(Double cantidadaprobada) {
        this.cantidadaprobada = cantidadaprobada;
    }

    public String getGenerado() {
        return generado;
    }

    public void setGenerado(String generado) {
        this.generado = generado;
    }

    public String getNitProveedor() {
        return nitProveedor;
    }

    public void setNitProveedor(String nitProveedor) {
        this.nitProveedor = nitProveedor;
    }
    
}
