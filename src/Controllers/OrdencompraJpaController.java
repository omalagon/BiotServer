/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.IllegalOrphanException;
import Controllers.exceptions.NonexistentEntityException;
import Entities.Ordencompra;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Recepcion;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class OrdencompraJpaController implements Serializable {

    public OrdencompraJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ordencompra ordencompra) {
        if (ordencompra.getRecepcionList() == null) {
            ordencompra.setRecepcionList(new ArrayList<Recepcion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Recepcion> attachedRecepcionList = new ArrayList<Recepcion>();
            for (Recepcion recepcionListRecepcionToAttach : ordencompra.getRecepcionList()) {
                recepcionListRecepcionToAttach = em.getReference(recepcionListRecepcionToAttach.getClass(), recepcionListRecepcionToAttach.getFechallegada());
                attachedRecepcionList.add(recepcionListRecepcionToAttach);
            }
            ordencompra.setRecepcionList(attachedRecepcionList);
            em.persist(ordencompra);
            for (Recepcion recepcionListRecepcion : ordencompra.getRecepcionList()) {
                Ordencompra oldNumOrdenOfRecepcionListRecepcion = recepcionListRecepcion.getNumOrden();
                recepcionListRecepcion.setNumOrden(ordencompra);
                recepcionListRecepcion = em.merge(recepcionListRecepcion);
                if (oldNumOrdenOfRecepcionListRecepcion != null) {
                    oldNumOrdenOfRecepcionListRecepcion.getRecepcionList().remove(recepcionListRecepcion);
                    oldNumOrdenOfRecepcionListRecepcion = em.merge(oldNumOrdenOfRecepcionListRecepcion);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ordencompra ordencompra) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ordencompra persistentOrdencompra = em.find(Ordencompra.class, ordencompra.getNumOrden());
            List<Recepcion> recepcionListOld = persistentOrdencompra.getRecepcionList();
            List<Recepcion> recepcionListNew = ordencompra.getRecepcionList();
            List<String> illegalOrphanMessages = null;
            for (Recepcion recepcionListOldRecepcion : recepcionListOld) {
                if (!recepcionListNew.contains(recepcionListOldRecepcion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Recepcion " + recepcionListOldRecepcion + " since its numOrden field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Recepcion> attachedRecepcionListNew = new ArrayList<Recepcion>();
            for (Recepcion recepcionListNewRecepcionToAttach : recepcionListNew) {
                recepcionListNewRecepcionToAttach = em.getReference(recepcionListNewRecepcionToAttach.getClass(), recepcionListNewRecepcionToAttach.getFechallegada());
                attachedRecepcionListNew.add(recepcionListNewRecepcionToAttach);
            }
            recepcionListNew = attachedRecepcionListNew;
            ordencompra.setRecepcionList(recepcionListNew);
            ordencompra = em.merge(ordencompra);
            for (Recepcion recepcionListNewRecepcion : recepcionListNew) {
                if (!recepcionListOld.contains(recepcionListNewRecepcion)) {
                    Ordencompra oldNumOrdenOfRecepcionListNewRecepcion = recepcionListNewRecepcion.getNumOrden();
                    recepcionListNewRecepcion.setNumOrden(ordencompra);
                    recepcionListNewRecepcion = em.merge(recepcionListNewRecepcion);
                    if (oldNumOrdenOfRecepcionListNewRecepcion != null && !oldNumOrdenOfRecepcionListNewRecepcion.equals(ordencompra)) {
                        oldNumOrdenOfRecepcionListNewRecepcion.getRecepcionList().remove(recepcionListNewRecepcion);
                        oldNumOrdenOfRecepcionListNewRecepcion = em.merge(oldNumOrdenOfRecepcionListNewRecepcion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Double id = ordencompra.getNumOrden();
                if (findOrdencompra(id) == null) {
                    throw new NonexistentEntityException("The ordencompra with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Double id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ordencompra ordencompra;
            try {
                ordencompra = em.getReference(Ordencompra.class, id);
                ordencompra.getNumOrden();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ordencompra with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Recepcion> recepcionListOrphanCheck = ordencompra.getRecepcionList();
            for (Recepcion recepcionListOrphanCheckRecepcion : recepcionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ordencompra (" + ordencompra + ") cannot be destroyed since the Recepcion " + recepcionListOrphanCheckRecepcion + " in its recepcionList field has a non-nullable numOrden field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(ordencompra);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ordencompra> findOrdencompraEntities() {
        return findOrdencompraEntities(true, -1, -1);
    }

    public List<Ordencompra> findOrdencompraEntities(int maxResults, int firstResult) {
        return findOrdencompraEntities(false, maxResults, firstResult);
    }

    private List<Ordencompra> findOrdencompraEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ordencompra.class));
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

    public Ordencompra findOrdencompra(Double id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ordencompra.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdencompraCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ordencompra> rt = cq.from(Ordencompra.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
