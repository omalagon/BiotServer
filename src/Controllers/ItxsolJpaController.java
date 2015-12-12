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
import Entities.Itxsol;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class ItxsolJpaController implements Serializable {

    public ItxsolJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Itxsol itxsol) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Item cinterno = itxsol.getCinterno();
            if (cinterno != null) {
                cinterno = em.getReference(cinterno.getClass(), cinterno.getCinterno());
                itxsol.setCinterno(cinterno);
            }
            em.persist(itxsol);
            if (cinterno != null) {
                cinterno.getItxsolList().add(itxsol);
                cinterno = em.merge(cinterno);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Itxsol itxsol) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Itxsol persistentItxsol = em.find(Itxsol.class, itxsol.getId());
            Item cinternoOld = persistentItxsol.getCinterno();
            Item cinternoNew = itxsol.getCinterno();
            if (cinternoNew != null) {
                cinternoNew = em.getReference(cinternoNew.getClass(), cinternoNew.getCinterno());
                itxsol.setCinterno(cinternoNew);
            }
            itxsol = em.merge(itxsol);
            if (cinternoOld != null && !cinternoOld.equals(cinternoNew)) {
                cinternoOld.getItxsolList().remove(itxsol);
                cinternoOld = em.merge(cinternoOld);
            }
            if (cinternoNew != null && !cinternoNew.equals(cinternoOld)) {
                cinternoNew.getItxsolList().add(itxsol);
                cinternoNew = em.merge(cinternoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = itxsol.getId();
                if (findItxsol(id) == null) {
                    throw new NonexistentEntityException("The itxsol with id " + id + " no longer exists.");
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
            Itxsol itxsol;
            try {
                itxsol = em.getReference(Itxsol.class, id);
                itxsol.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The itxsol with id " + id + " no longer exists.", enfe);
            }
            Item cinterno = itxsol.getCinterno();
            if (cinterno != null) {
                cinterno.getItxsolList().remove(itxsol);
                cinterno = em.merge(cinterno);
            }
            em.remove(itxsol);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Itxsol> findItxsolEntities() {
        return findItxsolEntities(true, -1, -1);
    }

    public List<Itxsol> findItxsolEntities(int maxResults, int firstResult) {
        return findItxsolEntities(false, maxResults, firstResult);
    }

    private List<Itxsol> findItxsolEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Itxsol.class));
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

    public Itxsol findItxsol(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Itxsol.class, id);
        } finally {
            em.close();
        }
    }

    public int getItxsolCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Itxsol> rt = cq.from(Itxsol.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
