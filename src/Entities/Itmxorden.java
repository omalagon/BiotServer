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
@Table(name = "itmxorden")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Itmxorden.findAll", query = "SELECT i FROM Itmxorden i"),
    @NamedQuery(name = "Itmxorden.findByNumorden", query = "SELECT i FROM Itmxorden i WHERE i.numorden = :numorden AND i.recibido=:recibido"),
    @NamedQuery(name = "Itmxorden.findByNumorden_item", query = "SELECT i FROM Itmxorden i WHERE i.numorden = :numorden AND i.itemCinterno =:cinterno"),
    @NamedQuery(name = "Itmxorden.findByCaprobada", query = "SELECT i FROM Itmxorden i WHERE i.caprobada = :caprobada"),
    @NamedQuery(name = "Itmxorden.findByPrecioU", query = "SELECT i FROM Itmxorden i WHERE i.precioU = :precioU"),
    @NamedQuery(name = "Itmxorden.findByAllParameters", query = "SELECT i FROM Itmxorden i WHERE i.proveedorNit =:nit AND i.caprobada = :caprobada AND i.precioU =:precio AND i.itemCinterno = :cinterno"),
    @NamedQuery(name = "Itmxorden.findByAllParameters2", query = "SELECT i FROM Itmxorden i WHERE i.proveedorNit =:nit AND i.caprobada = :caprobada AND i.precioU =:precio AND i.itemCinterno = :cinterno and i.numSolAsociado= :numsol"),
    @NamedQuery(name = "Itmxorden.findByIdOCompra", query = "SELECT i FROM Itmxorden i WHERE i.idOCompra = :idOCompra")})
public class Itmxorden implements Serializable {

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "numSolAsociado")
    private Double numSolAsociado;

    @Column(name = "recibido")
    private String recibido;

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "numorden")
    private double numorden;
    @Basic(optional = false)
    @Column(name = "caprobada")
    private double caprobada;
    @Basic(optional = false)
    @Column(name = "precio_u")
    private double precioU;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idOCompra")
    private Integer idOCompra;
    @JoinColumn(name = "item_cinterno", referencedColumnName = "cinterno")
    @ManyToOne(optional = false)
    private Item itemCinterno;
    @JoinColumn(name = "proveedor_nit", referencedColumnName = "nit")
    @ManyToOne(optional = false)
    private Proveedor proveedorNit;

    public Itmxorden() {
    }

    public Itmxorden(Integer idOCompra) {
        this.idOCompra = idOCompra;
    }

    public Itmxorden(Integer idOCompra, double numorden, double caprobada, double precioU) {
        this.idOCompra = idOCompra;
        this.numorden = numorden;
        this.caprobada = caprobada;
        this.precioU = precioU;
    }
    
    public Itmxorden(double numorden, double caprobada, double precioU) {
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

    public Integer getIdOCompra() {
        return idOCompra;
    }

    public void setIdOCompra(Integer idOCompra) {
        this.idOCompra = idOCompra;
    }

    public Item getItemCinterno() {
        return itemCinterno;
    }

    public void setItemCinterno(Item itemCinterno) {
        this.itemCinterno = itemCinterno;
    }

    public Proveedor getProveedorNit() {
        return proveedorNit;
    }

    public void setProveedorNit(Proveedor proveedorNit) {
        this.proveedorNit = proveedorNit;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOCompra != null ? idOCompra.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Itmxorden)) {
            return false;
        }
        Itmxorden other = (Itmxorden) object;
        if ((this.idOCompra == null && other.idOCompra != null) || (this.idOCompra != null && !this.idOCompra.equals(other.idOCompra))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Itmxorden[ idOCompra=" + idOCompra + " ]";
    }

    public String getRecibido() {
        return recibido;
    }

    public void setRecibido(String recibido) {
        this.recibido = recibido;
    }

    public Double getNumSolAsociado() {
        return numSolAsociado;
    }

    public void setNumSolAsociado(Double numSolAsociado) {
        this.numSolAsociado = numSolAsociado;
    }
    
}
