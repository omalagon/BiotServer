/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Item;
import Entities.Itmxorden;
import Entities.Proveedor;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class ItmxordenJpaController implements Serializable {

    public ItmxordenJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Itmxorden itmxorden) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Item itemCinterno = itmxorden.getItemCinterno();
            if (itemCinterno != null) {
                itemCinterno = em.getReference(itemCinterno.getClass(), itemCinterno.getCinterno());
                itmxorden.setItemCinterno(itemCinterno);
            }
            Proveedor proveedorNit = itmxorden.getProveedorNit();
            if (proveedorNit != null) {
                proveedorNit = em.getReference(proveedorNit.getClass(), proveedorNit.getNit());
                itmxorden.setProveedorNit(proveedorNit);
            }
            em.persist(itmxorden);
            if (itemCinterno != null) {
                itemCinterno.getItmxordenList().add(itmxorden);
                itemCinterno = em.merge(itemCinterno);
            }
            if (proveedorNit != null) {
                proveedorNit.getItmxordenList().add(itmxorden);
                proveedorNit = em.merge(proveedorNit);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Itmxorden itmxorden) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Itmxorden persistentItmxorden = em.find(Itmxorden.class, itmxorden.getIdOCompra());
            Item itemCinternoOld = persistentItmxorden.getItemCinterno();
            Item itemCinternoNew = itmxorden.getItemCinterno();
            Proveedor proveedorNitOld = persistentItmxorden.getProveedorNit();
            Proveedor proveedorNitNew = itmxorden.getProveedorNit();
            if (itemCinternoNew != null) {
                itemCinternoNew = em.getReference(itemCinternoNew.getClass(), itemCinternoNew.getCinterno());
                itmxorden.setItemCinterno(itemCinternoNew);
            }
            if (proveedorNitNew != null) {
                proveedorNitNew = em.getReference(proveedorNitNew.getClass(), proveedorNitNew.getNit());
                itmxorden.setProveedorNit(proveedorNitNew);
            }
            itmxorden = em.merge(itmxorden);
            if (itemCinternoOld != null && !itemCinternoOld.equals(itemCinternoNew)) {
                itemCinternoOld.getItmxordenList().remove(itmxorden);
                itemCinternoOld = em.merge(itemCinternoOld);
            }
            if (itemCinternoNew != null && !itemCinternoNew.equals(itemCinternoOld)) {
                itemCinternoNew.getItmxordenList().add(itmxorden);
                itemCinternoNew = em.merge(itemCinternoNew);
            }
            if (proveedorNitOld != null && !proveedorNitOld.equals(proveedorNitNew)) {
                proveedorNitOld.getItmxordenList().remove(itmxorden);
                proveedorNitOld = em.merge(proveedorNitOld);
            }
            if (proveedorNitNew != null && !proveedorNitNew.equals(proveedorNitOld)) {
                proveedorNitNew.getItmxordenList().add(itmxorden);
                proveedorNitNew = em.merge(proveedorNitNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = itmxorden.getIdOCompra();
                if (findItmxorden(id) == null) {
                    throw new NonexistentEntityException("The itmxorden with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Itmxorden itmxorden;
            try {
                itmxorden = em.getReference(Itmxorden.class, id);
                itmxorden.getIdOCompra();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The itmxorden with id " + id + " no longer exists.", enfe);
            }
            Item itemCinterno = itmxorden.getItemCinterno();
            if (itemCinterno != null) {
                itemCinterno.getItmxordenList().remove(itmxorden);
                itemCinterno = em.merge(itemCinterno);
            }
            Proveedor proveedorNit = itmxorden.getProveedorNit();
            if (proveedorNit != null) {
                proveedorNit.getItmxordenList().remove(itmxorden);
                proveedorNit = em.merge(proveedorNit);
            }
            em.remove(itmxorden);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Itmxorden> findItmxordenEntities() {
        return findItmxordenEntities(true, -1, -1);
    }

    public List<Itmxorden> findItmxordenEntities(int maxResults, int firstResult) {
        return findItmxordenEntities(false, maxResults, firstResult);
    }

    private List<Itmxorden> findItmxordenEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Itmxorden.class));
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

    public Itmxorden findItmxorden(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Itmxorden.class, id);
        } finally {
            em.close();
        }
    }

    public int getItmxordenCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Itmxorden> rt = cq.from(Itmxorden.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
