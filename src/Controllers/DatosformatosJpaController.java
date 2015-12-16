/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.NonexistentEntityException;
import Controllers.exceptions.PreexistingEntityException;
import Entities.Datosformatos;
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
public class DatosformatosJpaController implements Serializable {

    public DatosformatosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Datosformatos datosformatos) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(datosformatos);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDatosformatos(datosformatos.getId()) != null) {
                throw new PreexistingEntityException("Datosformatos " + datosformatos + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Datosformatos datosformatos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            datosformatos = em.merge(datosformatos);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = datosformatos.getId();
                if (findDatosformatos(id) == null) {
                    throw new NonexistentEntityException("The datosformatos with id " + id + " no longer exists.");
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
            Datosformatos datosformatos;
            try {
                datosformatos = em.getReference(Datosformatos.class, id);
                datosformatos.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The datosformatos with id " + id + " no longer exists.", enfe);
            }
            em.remove(datosformatos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Datosformatos> findDatosformatosEntities() {
        return findDatosformatosEntities(true, -1, -1);
    }

    public List<Datosformatos> findDatosformatosEntities(int maxResults, int firstResult) {
        return findDatosformatosEntities(false, maxResults, firstResult);
    }

    private List<Datosformatos> findDatosformatosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Datosformatos.class));
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

    public Datosformatos findDatosformatos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Datosformatos.class, id);
        } finally {
            em.close();
        }
    }

    public int getDatosformatosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Datosformatos> rt = cq.from(Datosformatos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
