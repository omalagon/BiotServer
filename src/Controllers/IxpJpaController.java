/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.NonexistentEntityException;
import Controllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Proveedor;
import Entities.Item;
import Entities.Ixp;
import Entities.IxpPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class IxpJpaController implements Serializable {

    public IxpJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ixp ixp) throws PreexistingEntityException, Exception {
        if (ixp.getIxpPK() == null) {
            ixp.setIxpPK(new IxpPK());
        }
        ixp.getIxpPK().setNit(ixp.getProveedor().getNit());
        ixp.getIxpPK().setCinterno(ixp.getItem().getCinterno());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedor proveedor = ixp.getProveedor();
            if (proveedor != null) {
                proveedor = em.getReference(proveedor.getClass(), proveedor.getNit());
                ixp.setProveedor(proveedor);
            }
            Item item = ixp.getItem();
            if (item != null) {
                item = em.getReference(item.getClass(), item.getCinterno());
                ixp.setItem(item);
            }
            em.persist(ixp);
            if (proveedor != null) {
                proveedor.getIxpList().add(ixp);
                proveedor = em.merge(proveedor);
            }
            if (item != null) {
                item.getIxpList().add(ixp);
                item = em.merge(item);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findIxp(ixp.getIxpPK()) != null) {
                throw new PreexistingEntityException("Ixp " + ixp + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ixp ixp) throws NonexistentEntityException, Exception {
        ixp.getIxpPK().setNit(ixp.getProveedor().getNit());
        ixp.getIxpPK().setCinterno(ixp.getItem().getCinterno());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ixp persistentIxp = em.find(Ixp.class, ixp.getIxpPK());
            Proveedor proveedorOld = persistentIxp.getProveedor();
            Proveedor proveedorNew = ixp.getProveedor();
            Item itemOld = persistentIxp.getItem();
            Item itemNew = ixp.getItem();
            if (proveedorNew != null) {
                proveedorNew = em.getReference(proveedorNew.getClass(), proveedorNew.getNit());
                ixp.setProveedor(proveedorNew);
            }
            if (itemNew != null) {
                itemNew = em.getReference(itemNew.getClass(), itemNew.getCinterno());
                ixp.setItem(itemNew);
            }
            ixp = em.merge(ixp);
            if (proveedorOld != null && !proveedorOld.equals(proveedorNew)) {
                proveedorOld.getIxpList().remove(ixp);
                proveedorOld = em.merge(proveedorOld);
            }
            if (proveedorNew != null && !proveedorNew.equals(proveedorOld)) {
                proveedorNew.getIxpList().add(ixp);
                proveedorNew = em.merge(proveedorNew);
            }
            if (itemOld != null && !itemOld.equals(itemNew)) {
                itemOld.getIxpList().remove(ixp);
                itemOld = em.merge(itemOld);
            }
            if (itemNew != null && !itemNew.equals(itemOld)) {
                itemNew.getIxpList().add(ixp);
                itemNew = em.merge(itemNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                IxpPK id = ixp.getIxpPK();
                if (findIxp(id) == null) {
                    throw new NonexistentEntityException("The ixp with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(IxpPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ixp ixp;
            try {
                ixp = em.getReference(Ixp.class, id);
                ixp.getIxpPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ixp with id " + id + " no longer exists.", enfe);
            }
            Proveedor proveedor = ixp.getProveedor();
            if (proveedor != null) {
                proveedor.getIxpList().remove(ixp);
                proveedor = em.merge(proveedor);
            }
            Item item = ixp.getItem();
            if (item != null) {
                item.getIxpList().remove(ixp);
                item = em.merge(item);
            }
            em.remove(ixp);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ixp> findIxpEntities() {
        return findIxpEntities(true, -1, -1);
    }

    public List<Ixp> findIxpEntities(int maxResults, int firstResult) {
        return findIxpEntities(false, maxResults, firstResult);
    }

    private List<Ixp> findIxpEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ixp.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Ixp findIxp(IxpPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ixp.class, id);
        } finally {
            em.close();
        }
    }

    public int getIxpCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ixp> rt = cq.from(Ixp.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
