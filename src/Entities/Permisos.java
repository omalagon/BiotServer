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
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author malag
 */
@Entity
@Table(name = "permisos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Permisos.findAll", query = "SELECT p FROM Permisos p"),
    @NamedQuery(name = "Permisos.findByUsuarioId", query = "SELECT p FROM Permisos p WHERE p.usuarioId = :usuarioId"),
    @NamedQuery(name = "Permisos.findByCrearItem", query = "SELECT p FROM Permisos p WHERE p.crearItem = :crearItem"),
    @NamedQuery(name = "Permisos.findByCrearProv", query = "SELECT p FROM Permisos p WHERE p.crearProv = :crearProv"),
    @NamedQuery(name = "Permisos.findByCrearUsuario", query = "SELECT p FROM Permisos p WHERE p.crearUsuario = :crearUsuario"),
    @NamedQuery(name = "Permisos.findByDescargarConsumos", query = "SELECT p FROM Permisos p WHERE p.descargarConsumos = :descargarConsumos"),
    @NamedQuery(name = "Permisos.findByRecibirPedido", query = "SELECT p FROM Permisos p WHERE p.recibirPedido = :recibirPedido"),
    @NamedQuery(name = "Permisos.findByRepDescargos", query = "SELECT p FROM Permisos p WHERE p.repDescargos = :repDescargos"),
    @NamedQuery(name = "Permisos.findByRepInventario", query = "SELECT p FROM Permisos p WHERE p.repInventario = :repInventario"),
    @NamedQuery(name = "Permisos.findByRepUsuarios", query = "SELECT p FROM Permisos p WHERE p.repUsuarios = :repUsuarios"),
    @NamedQuery(name = "Permisos.findByRepProv", query = "SELECT p FROM Permisos p WHERE p.repProv = :repProv"),
    @NamedQuery(name = "Permisos.findByRepixp", query = "SELECT p FROM Permisos p WHERE p.repixp = :repixp"),
    @NamedQuery(name = "Permisos.findBySolProd", query = "SELECT p FROM Permisos p WHERE p.solProd = :solProd"),
    @NamedQuery(name = "Permisos.findByRealizarCot", query = "SELECT p FROM Permisos p WHERE p.realizarCot = :realizarCot"),
    @NamedQuery(name = "Permisos.findByAprobarCot", query = "SELECT p FROM Permisos p WHERE p.aprobarCot = :aprobarCot"),
    @NamedQuery(name = "Permisos.findByOcompra", query = "SELECT p FROM Permisos p WHERE p.ocompra = :ocompra"),
    @NamedQuery(name = "Permisos.findByBloqUs", query = "SELECT p FROM Permisos p WHERE p.bloqUs = :bloqUs"),
    @NamedQuery(name = "Permisos.findByGenfdc001", query = "SELECT p FROM Permisos p WHERE p.genfdc001 = :genfdc001")})
public class Permisos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "usuario_id")
    private String usuarioId;
    @Column(name = "crearItem")
    private Character crearItem;
    @Column(name = "crearProv")
    private Character crearProv;
    @Column(name = "crearUsuario")
    private Character crearUsuario;
    @Column(name = "descargarConsumos")
    private Character descargarConsumos;
    @Column(name = "recibirPedido")
    private Character recibirPedido;
    @Column(name = "repDescargos")
    private Character repDescargos;
    @Column(name = "repInventario")
    private Character repInventario;
    @Column(name = "repUsuarios")
    private Character repUsuarios;
    @Column(name = "repProv")
    private Character repProv;
    @Column(name = "repixp")
    private Character repixp;
    @Column(name = "solProd")
    private Character solProd;
    @Column(name = "realizarCot")
    private Character realizarCot;
    @Column(name = "aprobarCot")
    private Character aprobarCot;
    @Column(name = "ocompra")
    private Character ocompra;
    @Column(name = "bloqUs")
    private Character bloqUs;
    @Column(name = "genfdc001")
    private Character genfdc001;
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Usuario usuario;

    public Permisos() {
    }

    public Permisos(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Character getCrearItem() {
        return crearItem;
    }

    public void setCrearItem(Character crearItem) {
        this.crearItem = crearItem;
    }

    public Character getCrearProv() {
        return crearProv;
    }

    public void setCrearProv(Character crearProv) {
        this.crearProv = crearProv;
    }

    public Character getCrearUsuario() {
        return crearUsuario;
    }

    public void setCrearUsuario(Character crearUsuario) {
        this.crearUsuario = crearUsuario;
    }

    public Character getDescargarConsumos() {
        return descargarConsumos;
    }

    public void setDescargarConsumos(Character descargarConsumos) {
        this.descargarConsumos = descargarConsumos;
    }

    public Character getRecibirPedido() {
        return recibirPedido;
    }

    public void setRecibirPedido(Character recibirPedido) {
        this.recibirPedido = recibirPedido;
    }

    public Character getRepDescargos() {
        return repDescargos;
    }

    public void setRepDescargos(Character repDescargos) {
        this.repDescargos = repDescargos;
    }

    public Character getRepInventario() {
        return repInventario;
    }

    public void setRepInventario(Character repInventario) {
        this.repInventario = repInventario;
    }

    public Character getRepUsuarios() {
        return repUsuarios;
    }

    public void setRepUsuarios(Character repUsuarios) {
        this.repUsuarios = repUsuarios;
    }

    public Character getRepProv() {
        return repProv;
    }

    public void setRepProv(Character repProv) {
        this.repProv = repProv;
    }

    public Character getRepixp() {
        return repixp;
    }

    public void setRepixp(Character repixp) {
        this.repixp = repixp;
    }

    public Character getSolProd() {
        return solProd;
    }

    public void setSolProd(Character solProd) {
        this.solProd = solProd;
    }

    public Character getRealizarCot() {
        return realizarCot;
    }

    public void setRealizarCot(Character realizarCot) {
        this.realizarCot = realizarCot;
    }

    public Character getAprobarCot() {
        return aprobarCot;
    }

    public void setAprobarCot(Character aprobarCot) {
        this.aprobarCot = aprobarCot;
    }

    public Character getOcompra() {
        return ocompra;
    }

    public void setOcompra(Character ocompra) {
        this.ocompra = ocompra;
    }

    public Character getBloqUs() {
        return bloqUs;
    }

    public void setBloqUs(Character bloqUs) {
        this.bloqUs = bloqUs;
    }

    public Character getGenfdc001() {
        return genfdc001;
    }

    public void setGenfdc001(Character genfdc001) {
        this.genfdc001 = genfdc001;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usuarioId != null ? usuarioId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Permisos)) {
            return false;
        }
        Permisos other = (Permisos) object;
        if ((this.usuarioId == null && other.usuarioId != null) || (this.usuarioId != null && !this.usuarioId.equals(other.usuarioId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Permisos[ usuarioId=" + usuarioId + " ]";
    }
    
}
