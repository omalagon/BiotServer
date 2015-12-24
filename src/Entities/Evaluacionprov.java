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
@Table(name = "evaluacionprov")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Evaluacionprov.findAll", query = "SELECT e FROM Evaluacionprov e"),
    @NamedQuery(name = "Evaluacionprov.findById", query = "SELECT e FROM Evaluacionprov e WHERE e.id = :id"),
    @NamedQuery(name = "Evaluacionprov.findByNitProv", query = "SELECT e FROM Evaluacionprov e WHERE e.nitProv = :nitProv"),
    @NamedQuery(name = "Evaluacionprov.findByNumorden", query = "SELECT e FROM Evaluacionprov e WHERE e.numorden = :numorden"),
    @NamedQuery(name = "Evaluacionprov.findByEv1", query = "SELECT e FROM Evaluacionprov e WHERE e.ev1 = :ev1"),
    @NamedQuery(name = "Evaluacionprov.findByEv2", query = "SELECT e FROM Evaluacionprov e WHERE e.ev2 = :ev2"),
    @NamedQuery(name = "Evaluacionprov.findByEv3", query = "SELECT e FROM Evaluacionprov e WHERE e.ev3 = :ev3"),
    @NamedQuery(name = "Evaluacionprov.findByEv4", query = "SELECT e FROM Evaluacionprov e WHERE e.ev4 = :ev4"),
    @NamedQuery(name = "Evaluacionprov.findByEv5", query = "SELECT e FROM Evaluacionprov e WHERE e.ev5 = :ev5"),
    @NamedQuery(name = "Evaluacionprov.findByEv6", query = "SELECT e FROM Evaluacionprov e WHERE e.ev6 = :ev6"),
    @NamedQuery(name = "Evaluacionprov.findByEv7", query = "SELECT e FROM Evaluacionprov e WHERE e.ev7 = :ev7"),
    @NamedQuery(name = "Evaluacionprov.findByEv8", query = "SELECT e FROM Evaluacionprov e WHERE e.ev8 = :ev8")})
public class Evaluacionprov implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nitProv")
    private String nitProv;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "numorden")
    private Double numorden;
    @Column(name = "ev1")
    private String ev1;
    @Column(name = "ev2")
    private String ev2;
    @Column(name = "ev3")
    private String ev3;
    @Column(name = "ev4")
    private String ev4;
    @Column(name = "ev5")
    private String ev5;
    @Column(name = "ev6")
    private String ev6;
    @Column(name = "ev7")
    private String ev7;
    @Column(name = "ev8")
    private String ev8;

    public Evaluacionprov() {
    }

    public Evaluacionprov(String nitProv, Double numorden, String ev1, String ev2, String ev3, String ev4, String ev5, String ev6, String ev7, String ev8) {
        this.nitProv = nitProv;
        this.numorden = numorden;
        this.ev1 = ev1;
        this.ev2 = ev2;
        this.ev3 = ev3;
        this.ev4 = ev4;
        this.ev5 = ev5;
        this.ev6 = ev6;
        this.ev7 = ev7;
        this.ev8 = ev8;
    }

    
    public Evaluacionprov(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNitProv() {
        return nitProv;
    }

    public void setNitProv(String nitProv) {
        this.nitProv = nitProv;
    }

    public Double getNumorden() {
        return numorden;
    }

    public void setNumorden(Double numorden) {
        this.numorden = numorden;
    }

    public String getEv1() {
        return ev1;
    }

    public void setEv1(String ev1) {
        this.ev1 = ev1;
    }

    public String getEv2() {
        return ev2;
    }

    public void setEv2(String ev2) {
        this.ev2 = ev2;
    }

    public String getEv3() {
        return ev3;
    }

    public void setEv3(String ev3) {
        this.ev3 = ev3;
    }

    public String getEv4() {
        return ev4;
    }

    public void setEv4(String ev4) {
        this.ev4 = ev4;
    }

    public String getEv5() {
        return ev5;
    }

    public void setEv5(String ev5) {
        this.ev5 = ev5;
    }

    public String getEv6() {
        return ev6;
    }

    public void setEv6(String ev6) {
        this.ev6 = ev6;
    }

    public String getEv7() {
        return ev7;
    }

    public void setEv7(String ev7) {
        this.ev7 = ev7;
    }

    public String getEv8() {
        return ev8;
    }

    public void setEv8(String ev8) {
        this.ev8 = ev8;
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
        if (!(object instanceof Evaluacionprov)) {
            return false;
        }
        Evaluacionprov other = (Evaluacionprov) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Evaluacionprov[ id=" + id + " ]";
    }
    
}
