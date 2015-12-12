/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.NonexistentEntityException;
import Entities.Aprobados;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Usuario;
import Entities.CotizacionProd;
import Entities.Item;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class AprobadosJpaController implements Serializable {

    public AprobadosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Aprobados aprobados) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idDa = aprobados.getIdDa();
            if (idDa != null) {
                idDa = em.getReference(idDa.getClass(), idDa.getId());
                aprobados.setIdDa(idDa);
            }
            CotizacionProd idCot = aprobados.getIdCot();
            if (idCot != null) {
                idCot = em.getReference(idCot.getClass(), idCot.getId());
                aprobados.setIdCot(idCot);
            }
            Item cinterno = aprobados.getCinterno();
            if (cinterno != null) {
                cinterno = em.getReference(cinterno.getClass(), cinterno.getCinterno());
                aprobados.setCinterno(cinterno);
            }
            em.persist(aprobados);
            if (idDa != null) {
                idDa.getAprobadosList().add(aprobados);
                idDa = em.merge(idDa);
            }
            if (idCot != null) {
                idCot.getAprobadosList().add(aprobados);
                idCot = em.merge(idCot);
            }
            if (cinterno != null) {
                cinterno.getAprobadosList().add(aprobados);
                cinterno = em.merge(cinterno);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Aprobados aprobados) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Aprobados persistentAprobados = em.find(Aprobados.class, aprobados.getIdAprobado());
            Usuario idDaOld = persistentAprobados.getIdDa();
            Usuario idDaNew = aprobados.getIdDa();
            CotizacionProd idCotOld = persistentAprobados.getIdCot();
            CotizacionProd idCotNew = aprobados.getIdCot();
            Item cinternoOld = persistentAprobados.getCinterno();
            Item cinternoNew = aprobados.getCinterno();
            if (idDaNew != null) {
                idDaNew = em.getReference(idDaNew.getClass(), idDaNew.getId());
                aprobados.setIdDa(idDaNew);
            }
            if (idCotNew != null) {
                idCotNew = em.getReference(idCotNew.getClass(), idCotNew.getId());
                aprobados.setIdCot(idCotNew);
            }
            if (cinternoNew != null) {
                cinternoNew = em.getReference(cinternoNew.getClass(), cinternoNew.getCinterno());
                aprobados.setCinterno(cinternoNew);
            }
            aprobados = em.merge(aprobados);
            if (idDaOld != null && !idDaOld.equals(idDaNew)) {
                idDaOld.getAprobadosList().remove(aprobados);
                idDaOld = em.merge(idDaOld);
            }
            if (idDaNew != null && !idDaNew.equals(idDaOld)) {
                idDaNew.getAprobadosList().add(aprobados);
                idDaNew = em.merge(idDaNew);
            }
            if (idCotOld != null && !idCotOld.equals(idCotNew)) {
                idCotOld.getAprobadosList().remove(aprobados);
                idCotOld = em.merge(idCotOld);
            }
            if (idCotNew != null && !idCotNew.equals(idCotOld)) {
                idCotNew.getAprobadosList().add(aprobados);
                idCotNew = em.merge(idCotNew);
            }
            if (cinternoOld != null && !cinternoOld.equals(cinternoNew)) {
                cinternoOld.getAprobadosList().remove(aprobados);
                cinternoOld = em.merge(cinternoOld);
            }
            if (cinternoNew != null && !cinternoNew.equals(cinternoOld)) {
                cinternoNew.getAprobadosList().add(aprobados);
                cinternoNew = em.merge(cinternoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = aprobados.getIdAprobado();
                if (findAprobados(id) == null) {
                    throw new NonexistentEntityException("The aprobados with id " + id + " no longer exists.");
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
            Aprobados aprobados;
            try {
                aprobados = em.getReference(Aprobados.class, id);
                aprobados.getIdAprobado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The aprobados with id " + id + " no longer exists.", enfe);
            }
            Usuario idDa = aprobados.getIdDa();
            if (idDa != null) {
                idDa.getAprobadosList().remove(aprobados);
                idDa = em.merge(idDa);
            }
            CotizacionProd idCot = aprobados.getIdCot();
            if (idCot != null) {
                idCot.getAprobadosList().remove(aprobados);
                idCot = em.merge(idCot);
            }
            Item cinterno = aprobados.getCinterno();
            if (cinterno != null) {
                cinterno.getAprobadosList().remove(aprobados);
                cinterno = em.merge(cinterno);
            }
            em.remove(aprobados);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Aprobados> findAprobadosEntities() {
        return findAprobadosEntities(true, -1, -1);
    }

    public List<Aprobados> findAprobadosEntities(int maxResults, int firstResult) {
        return findAprobadosEntities(false, maxResults, firstResult);
    }

    private List<Aprobados> findAprobadosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Aprobados.class));
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

    public Aprobados findAprobados(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Aprobados.class, id);
        } finally {
            em.close();
        }
    }

    public int getAprobadosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Aprobados> rt = cq.from(Aprobados.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
