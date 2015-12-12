/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.IllegalOrphanException;
import Controllers.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.CotizacionProd;
import Entities.SolicitudPr;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
        if (solicitudPr.getCotizacionProdList() == null) {
            solicitudPr.setCotizacionProdList(new ArrayList<CotizacionProd>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CotizacionProd> attachedCotizacionProdList = new ArrayList<CotizacionProd>();
            for (CotizacionProd cotizacionProdListCotizacionProdToAttach : solicitudPr.getCotizacionProdList()) {
                cotizacionProdListCotizacionProdToAttach = em.getReference(cotizacionProdListCotizacionProdToAttach.getClass(), cotizacionProdListCotizacionProdToAttach.getId());
                attachedCotizacionProdList.add(cotizacionProdListCotizacionProdToAttach);
            }
            solicitudPr.setCotizacionProdList(attachedCotizacionProdList);
            em.persist(solicitudPr);
            for (CotizacionProd cotizacionProdListCotizacionProd : solicitudPr.getCotizacionProdList()) {
                SolicitudPr oldNumSolOfCotizacionProdListCotizacionProd = cotizacionProdListCotizacionProd.getNumSol();
                cotizacionProdListCotizacionProd.setNumSol(solicitudPr);
                cotizacionProdListCotizacionProd = em.merge(cotizacionProdListCotizacionProd);
                if (oldNumSolOfCotizacionProdListCotizacionProd != null) {
                    oldNumSolOfCotizacionProdListCotizacionProd.getCotizacionProdList().remove(cotizacionProdListCotizacionProd);
                    oldNumSolOfCotizacionProdListCotizacionProd = em.merge(oldNumSolOfCotizacionProdListCotizacionProd);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SolicitudPr solicitudPr) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SolicitudPr persistentSolicitudPr = em.find(SolicitudPr.class, solicitudPr.getNumSol());
            List<CotizacionProd> cotizacionProdListOld = persistentSolicitudPr.getCotizacionProdList();
            List<CotizacionProd> cotizacionProdListNew = solicitudPr.getCotizacionProdList();
            List<String> illegalOrphanMessages = null;
            for (CotizacionProd cotizacionProdListOldCotizacionProd : cotizacionProdListOld) {
                if (!cotizacionProdListNew.contains(cotizacionProdListOldCotizacionProd)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CotizacionProd " + cotizacionProdListOldCotizacionProd + " since its numSol field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<CotizacionProd> attachedCotizacionProdListNew = new ArrayList<CotizacionProd>();
            for (CotizacionProd cotizacionProdListNewCotizacionProdToAttach : cotizacionProdListNew) {
                cotizacionProdListNewCotizacionProdToAttach = em.getReference(cotizacionProdListNewCotizacionProdToAttach.getClass(), cotizacionProdListNewCotizacionProdToAttach.getId());
                attachedCotizacionProdListNew.add(cotizacionProdListNewCotizacionProdToAttach);
            }
            cotizacionProdListNew = attachedCotizacionProdListNew;
            solicitudPr.setCotizacionProdList(cotizacionProdListNew);
            solicitudPr = em.merge(solicitudPr);
            for (CotizacionProd cotizacionProdListNewCotizacionProd : cotizacionProdListNew) {
                if (!cotizacionProdListOld.contains(cotizacionProdListNewCotizacionProd)) {
                    SolicitudPr oldNumSolOfCotizacionProdListNewCotizacionProd = cotizacionProdListNewCotizacionProd.getNumSol();
                    cotizacionProdListNewCotizacionProd.setNumSol(solicitudPr);
                    cotizacionProdListNewCotizacionProd = em.merge(cotizacionProdListNewCotizacionProd);
                    if (oldNumSolOfCotizacionProdListNewCotizacionProd != null && !oldNumSolOfCotizacionProdListNewCotizacionProd.equals(solicitudPr)) {
                        oldNumSolOfCotizacionProdListNewCotizacionProd.getCotizacionProdList().remove(cotizacionProdListNewCotizacionProd);
                        oldNumSolOfCotizacionProdListNewCotizacionProd = em.merge(oldNumSolOfCotizacionProdListNewCotizacionProd);
                    }
                }
            }
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

    public void destroy(Double id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<String> illegalOrphanMessages = null;
            List<CotizacionProd> cotizacionProdListOrphanCheck = solicitudPr.getCotizacionProdList();
            for (CotizacionProd cotizacionProdListOrphanCheckCotizacionProd : cotizacionProdListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SolicitudPr (" + solicitudPr + ") cannot be destroyed since the CotizacionProd " + cotizacionProdListOrphanCheckCotizacionProd + " in its cotizacionProdList field has a non-nullable numSol field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
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
