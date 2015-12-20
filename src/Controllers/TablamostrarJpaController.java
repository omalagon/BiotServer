/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.NonexistentEntityException;
import Entities.Tablamostrar;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author malag
 */
public class TablamostrarJpaController implements Serializable {

    public TablamostrarJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tablamostrar tablamostrar) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(tablamostrar);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tablamostrar tablamostrar) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            tablamostrar = em.merge(tablamostrar);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tablamostrar.getIdMostrar();
                if (findTablamostrar(id) == null) {
                    throw new NonexistentEntityException("The tablamostrar with id " + id + " no longer exists.");
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
            Tablamostrar tablamostrar;
            try {
                tablamostrar = em.getReference(Tablamostrar.class, id);
                tablamostrar.getIdMostrar();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tablamostrar with id " + id + " no longer exists.", enfe);
            }
            em.remove(tablamostrar);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tablamostrar> findTablamostrarEntities() {
        return findTablamostrarEntities(true, -1, -1);
    }

    public List<Tablamostrar> findTablamostrarEntities(int maxResults, int firstResult) {
        return findTablamostrarEntities(false, maxResults, firstResult);
    }

    private List<Tablamostrar> findTablamostrarEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tablamostrar.class));
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

    public Tablamostrar findTablamostrar(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tablamostrar.class, id);
        } finally {
            em.close();
        }
    }

    public int getTablamostrarCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tablamostrar> rt = cq.from(Tablamostrar.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
