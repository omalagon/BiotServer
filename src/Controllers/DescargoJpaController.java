/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.NonexistentEntityException;
import Entities.Descargo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Item;
import Entities.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class DescargoJpaController implements Serializable {

    public DescargoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Descargo descargo) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Item cinterno = descargo.getCinterno();
            if (cinterno != null) {
                cinterno = em.getReference(cinterno.getClass(), cinterno.getCinterno());
                descargo.setCinterno(cinterno);
            }
            Usuario idUsuario = descargo.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getId());
                descargo.setIdUsuario(idUsuario);
            }
            em.persist(descargo);
            if (cinterno != null) {
                cinterno.getDescargoList().add(descargo);
                cinterno = em.merge(cinterno);
            }
            if (idUsuario != null) {
                idUsuario.getDescargoList().add(descargo);
                idUsuario = em.merge(idUsuario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Descargo descargo) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Descargo persistentDescargo = em.find(Descargo.class, descargo.getId());
            Item cinternoOld = persistentDescargo.getCinterno();
            Item cinternoNew = descargo.getCinterno();
            Usuario idUsuarioOld = persistentDescargo.getIdUsuario();
            Usuario idUsuarioNew = descargo.getIdUsuario();
            if (cinternoNew != null) {
                cinternoNew = em.getReference(cinternoNew.getClass(), cinternoNew.getCinterno());
                descargo.setCinterno(cinternoNew);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getId());
                descargo.setIdUsuario(idUsuarioNew);
            }
            descargo = em.merge(descargo);
            if (cinternoOld != null && !cinternoOld.equals(cinternoNew)) {
                cinternoOld.getDescargoList().remove(descargo);
                cinternoOld = em.merge(cinternoOld);
            }
            if (cinternoNew != null && !cinternoNew.equals(cinternoOld)) {
                cinternoNew.getDescargoList().add(descargo);
                cinternoNew = em.merge(cinternoNew);
            }
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getDescargoList().remove(descargo);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getDescargoList().add(descargo);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Double id = descargo.getId();
                if (findDescargo(id) == null) {
                    throw new NonexistentEntityException("The descargo with id " + id + " no longer exists.");
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
            Descargo descargo;
            try {
                descargo = em.getReference(Descargo.class, id);
                descargo.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The descargo with id " + id + " no longer exists.", enfe);
            }
            Item cinterno = descargo.getCinterno();
            if (cinterno != null) {
                cinterno.getDescargoList().remove(descargo);
                cinterno = em.merge(cinterno);
            }
            Usuario idUsuario = descargo.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getDescargoList().remove(descargo);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(descargo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Descargo> findDescargoEntities() {
        return findDescargoEntities(true, -1, -1);
    }

    public List<Descargo> findDescargoEntities(int maxResults, int firstResult) {
        return findDescargoEntities(false, maxResults, firstResult);
    }

    private List<Descargo> findDescargoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Descargo.class));
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

    public Descargo findDescargo(Double id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Descargo.class, id);
        } finally {
            em.close();
        }
    }

    public int getDescargoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Descargo> rt = cq.from(Descargo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
