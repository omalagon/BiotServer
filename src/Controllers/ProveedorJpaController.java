/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.IllegalOrphanException;
import Controllers.exceptions.NonexistentEntityException;
import Controllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Itmxorden;
import Entities.Proveedor;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class ProveedorJpaController implements Serializable {

    public ProveedorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Proveedor proveedor) throws PreexistingEntityException, Exception {
        if (proveedor.getItmxordenList() == null) {
            proveedor.setItmxordenList(new ArrayList<Itmxorden>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Itmxorden> attachedItmxordenList = new ArrayList<Itmxorden>();
            for (Itmxorden itmxordenListItmxordenToAttach : proveedor.getItmxordenList()) {
                itmxordenListItmxordenToAttach = em.getReference(itmxordenListItmxordenToAttach.getClass(), itmxordenListItmxordenToAttach.getIdOCompra());
                attachedItmxordenList.add(itmxordenListItmxordenToAttach);
            }
            proveedor.setItmxordenList(attachedItmxordenList);
            em.persist(proveedor);
            for (Itmxorden itmxordenListItmxorden : proveedor.getItmxordenList()) {
                Proveedor oldProveedorNitOfItmxordenListItmxorden = itmxordenListItmxorden.getProveedorNit();
                itmxordenListItmxorden.setProveedorNit(proveedor);
                itmxordenListItmxorden = em.merge(itmxordenListItmxorden);
                if (oldProveedorNitOfItmxordenListItmxorden != null) {
                    oldProveedorNitOfItmxordenListItmxorden.getItmxordenList().remove(itmxordenListItmxorden);
                    oldProveedorNitOfItmxordenListItmxorden = em.merge(oldProveedorNitOfItmxordenListItmxorden);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProveedor(proveedor.getNit()) != null) {
                throw new PreexistingEntityException("Proveedor " + proveedor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Proveedor proveedor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedor persistentProveedor = em.find(Proveedor.class, proveedor.getNit());
            List<Itmxorden> itmxordenListOld = persistentProveedor.getItmxordenList();
            List<Itmxorden> itmxordenListNew = proveedor.getItmxordenList();
            List<String> illegalOrphanMessages = null;
            for (Itmxorden itmxordenListOldItmxorden : itmxordenListOld) {
                if (!itmxordenListNew.contains(itmxordenListOldItmxorden)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Itmxorden " + itmxordenListOldItmxorden + " since its proveedorNit field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Itmxorden> attachedItmxordenListNew = new ArrayList<Itmxorden>();
            for (Itmxorden itmxordenListNewItmxordenToAttach : itmxordenListNew) {
                itmxordenListNewItmxordenToAttach = em.getReference(itmxordenListNewItmxordenToAttach.getClass(), itmxordenListNewItmxordenToAttach.getIdOCompra());
                attachedItmxordenListNew.add(itmxordenListNewItmxordenToAttach);
            }
            itmxordenListNew = attachedItmxordenListNew;
            proveedor.setItmxordenList(itmxordenListNew);
            proveedor = em.merge(proveedor);
            for (Itmxorden itmxordenListNewItmxorden : itmxordenListNew) {
                if (!itmxordenListOld.contains(itmxordenListNewItmxorden)) {
                    Proveedor oldProveedorNitOfItmxordenListNewItmxorden = itmxordenListNewItmxorden.getProveedorNit();
                    itmxordenListNewItmxorden.setProveedorNit(proveedor);
                    itmxordenListNewItmxorden = em.merge(itmxordenListNewItmxorden);
                    if (oldProveedorNitOfItmxordenListNewItmxorden != null && !oldProveedorNitOfItmxordenListNewItmxorden.equals(proveedor)) {
                        oldProveedorNitOfItmxordenListNewItmxorden.getItmxordenList().remove(itmxordenListNewItmxorden);
                        oldProveedorNitOfItmxordenListNewItmxorden = em.merge(oldProveedorNitOfItmxordenListNewItmxorden);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = proveedor.getNit();
                if (findProveedor(id) == null) {
                    throw new NonexistentEntityException("The proveedor with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedor proveedor;
            try {
                proveedor = em.getReference(Proveedor.class, id);
                proveedor.getNit();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The proveedor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Itmxorden> itmxordenListOrphanCheck = proveedor.getItmxordenList();
            for (Itmxorden itmxordenListOrphanCheckItmxorden : itmxordenListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Proveedor (" + proveedor + ") cannot be destroyed since the Itmxorden " + itmxordenListOrphanCheckItmxorden + " in its itmxordenList field has a non-nullable proveedorNit field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(proveedor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Proveedor> findProveedorEntities() {
        return findProveedorEntities(true, -1, -1);
    }

    public List<Proveedor> findProveedorEntities(int maxResults, int firstResult) {
        return findProveedorEntities(false, maxResults, firstResult);
    }

    private List<Proveedor> findProveedorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Proveedor.class));
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

    public Proveedor findProveedor(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Proveedor.class, id);
        } finally {
            em.close();
        }
    }

    public int getProveedorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Proveedor> rt = cq.from(Proveedor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
