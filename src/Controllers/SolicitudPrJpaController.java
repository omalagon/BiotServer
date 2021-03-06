/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.NonexistentEntityException;
import Entities.SolicitudPr;
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
public class SolicitudPrJpaController implements Serializable {

    public SolicitudPrJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SolicitudPr solicitudPr) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(solicitudPr);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SolicitudPr solicitudPr) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            solicitudPr = em.merge(solicitudPr);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Double id = solicitudPr.getNumSol();
                if (findSolicitudPr(id) == null) {
                    throw new NonexistentEntityException("The solicitudPr with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Double id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SolicitudPr solicitudPr;
            try {
                solicitudPr = em.getReference(SolicitudPr.class, id);
                solicitudPr.getNumSol();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The solicitudPr with id " + id + " no longer exists.", enfe);
            }
            em.remove(solicitudPr);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SolicitudPr> findSolicitudPrEntities() {
        return findSolicitudPrEntities(true, -1, -1);
    }

    public List<SolicitudPr> findSolicitudPrEntities(int maxResults, int firstResult) {
        return findSolicitudPrEntities(false, maxResults, firstResult);
    }

    private List<SolicitudPr> findSolicitudPrEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SolicitudPr.class));
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

    public SolicitudPr findSolicitudPr(Double id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SolicitudPr.class, id);
        } finally {
            em.close();
        }
    }

    public int getSolicitudPrCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SolicitudPr> rt = cq.from(SolicitudPr.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
