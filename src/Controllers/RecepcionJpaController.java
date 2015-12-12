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
import Entities.Item;
import Entities.Ordencompra;
import Entities.Recepcion;
import Entities.Usuario;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class RecepcionJpaController implements Serializable {

    public RecepcionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Recepcion recepcion) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Item cinterno = recepcion.getCinterno();
            if (cinterno != null) {
                cinterno = em.getReference(cinterno.getClass(), cinterno.getCinterno());
                recepcion.setCinterno(cinterno);
            }
            Ordencompra numOrden = recepcion.getNumOrden();
            if (numOrden != null) {
                numOrden = em.getReference(numOrden.getClass(), numOrden.getNumOrden());
                recepcion.setNumOrden(numOrden);
            }
            Usuario idUsuario = recepcion.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getId());
                recepcion.setIdUsuario(idUsuario);
            }
            em.persist(recepcion);
            if (cinterno != null) {
                cinterno.getRecepcionList().add(recepcion);
                cinterno = em.merge(cinterno);
            }
            if (numOrden != null) {
                numOrden.getRecepcionList().add(recepcion);
                numOrden = em.merge(numOrden);
            }
            if (idUsuario != null) {
                idUsuario.getRecepcionList().add(recepcion);
                idUsuario = em.merge(idUsuario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRecepcion(recepcion.getFechallegada()) != null) {
                throw new PreexistingEntityException("Recepcion " + recepcion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Recepcion recepcion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Recepcion persistentRecepcion = em.find(Recepcion.class, recepcion.getFechallegada());
            Item cinternoOld = persistentRecepcion.getCinterno();
            Item cinternoNew = recepcion.getCinterno();
            Ordencompra numOrdenOld = persistentRecepcion.getNumOrden();
            Ordencompra numOrdenNew = recepcion.getNumOrden();
            Usuario idUsuarioOld = persistentRecepcion.getIdUsuario();
            Usuario idUsuarioNew = recepcion.getIdUsuario();
            if (cinternoNew != null) {
                cinternoNew = em.getReference(cinternoNew.getClass(), cinternoNew.getCinterno());
                recepcion.setCinterno(cinternoNew);
            }
            if (numOrdenNew != null) {
                numOrdenNew = em.getReference(numOrdenNew.getClass(), numOrdenNew.getNumOrden());
                recepcion.setNumOrden(numOrdenNew);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getId());
                recepcion.setIdUsuario(idUsuarioNew);
            }
            recepcion = em.merge(recepcion);
            if (cinternoOld != null && !cinternoOld.equals(cinternoNew)) {
                cinternoOld.getRecepcionList().remove(recepcion);
                cinternoOld = em.merge(cinternoOld);
            }
            if (cinternoNew != null && !cinternoNew.equals(cinternoOld)) {
                cinternoNew.getRecepcionList().add(recepcion);
                cinternoNew = em.merge(cinternoNew);
            }
            if (numOrdenOld != null && !numOrdenOld.equals(numOrdenNew)) {
                numOrdenOld.getRecepcionList().remove(recepcion);
                numOrdenOld = em.merge(numOrdenOld);
            }
            if (numOrdenNew != null && !numOrdenNew.equals(numOrdenOld)) {
                numOrdenNew.getRecepcionList().add(recepcion);
                numOrdenNew = em.merge(numOrdenNew);
            }
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getRecepcionList().remove(recepcion);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getRecepcionList().add(recepcion);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Date id = recepcion.getFechallegada();
                if (findRecepcion(id) == null) {
                    throw new NonexistentEntityException("The recepcion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Date id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Recepcion recepcion;
            try {
                recepcion = em.getReference(Recepcion.class, id);
                recepcion.getFechallegada();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The recepcion with id " + id + " no longer exists.", enfe);
            }
            Item cinterno = recepcion.getCinterno();
            if (cinterno != null) {
                cinterno.getRecepcionList().remove(recepcion);
                cinterno = em.merge(cinterno);
            }
            Ordencompra numOrden = recepcion.getNumOrden();
            if (numOrden != null) {
                numOrden.getRecepcionList().remove(recepcion);
                numOrden = em.merge(numOrden);
            }
            Usuario idUsuario = recepcion.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getRecepcionList().remove(recepcion);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(recepcion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Recepcion> findRecepcionEntities() {
        return findRecepcionEntities(true, -1, -1);
    }

    public List<Recepcion> findRecepcionEntities(int maxResults, int firstResult) {
        return findRecepcionEntities(false, maxResults, firstResult);
    }

    private List<Recepcion> findRecepcionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Recepcion.class));
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

    public Recepcion findRecepcion(Date id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Recepcion.class, id);
        } finally {
            em.close();
        }
    }

    public int getRecepcionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Recepcion> rt = cq.from(Recepcion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
