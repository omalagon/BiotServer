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
import Entities.SolicitudPr;
import Entities.Proveedor;
import Entities.Usuario;
import Entities.Item;
import Entities.Aprobados;
import Entities.CotizacionProd;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class CotizacionProdJpaController implements Serializable {

    public CotizacionProdJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CotizacionProd cotizacionProd) {
        if (cotizacionProd.getAprobadosList() == null) {
            cotizacionProd.setAprobadosList(new ArrayList<Aprobados>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SolicitudPr numSol = cotizacionProd.getNumSol();
            if (numSol != null) {
                numSol = em.getReference(numSol.getClass(), numSol.getNumSol());
                cotizacionProd.setNumSol(numSol);
            }
            Proveedor nit = cotizacionProd.getNit();
            if (nit != null) {
                nit = em.getReference(nit.getClass(), nit.getNit());
                cotizacionProd.setNit(nit);
            }
            Usuario idAo = cotizacionProd.getIdAo();
            if (idAo != null) {
                idAo = em.getReference(idAo.getClass(), idAo.getId());
                cotizacionProd.setIdAo(idAo);
            }
            Item cinterno = cotizacionProd.getCinterno();
            if (cinterno != null) {
                cinterno = em.getReference(cinterno.getClass(), cinterno.getCinterno());
                cotizacionProd.setCinterno(cinterno);
            }
            List<Aprobados> attachedAprobadosList = new ArrayList<Aprobados>();
            for (Aprobados aprobadosListAprobadosToAttach : cotizacionProd.getAprobadosList()) {
                aprobadosListAprobadosToAttach = em.getReference(aprobadosListAprobadosToAttach.getClass(), aprobadosListAprobadosToAttach.getIdAprobado());
                attachedAprobadosList.add(aprobadosListAprobadosToAttach);
            }
            cotizacionProd.setAprobadosList(attachedAprobadosList);
            em.persist(cotizacionProd);
            if (numSol != null) {
                numSol.getCotizacionProdList().add(cotizacionProd);
                numSol = em.merge(numSol);
            }
            if (nit != null) {
                nit.getCotizacionProdList().add(cotizacionProd);
                nit = em.merge(nit);
            }
            if (idAo != null) {
                idAo.getCotizacionProdList().add(cotizacionProd);
                idAo = em.merge(idAo);
            }
            if (cinterno != null) {
                cinterno.getCotizacionProdList().add(cotizacionProd);
                cinterno = em.merge(cinterno);
            }
            for (Aprobados aprobadosListAprobados : cotizacionProd.getAprobadosList()) {
                CotizacionProd oldIdCotOfAprobadosListAprobados = aprobadosListAprobados.getIdCot();
                aprobadosListAprobados.setIdCot(cotizacionProd);
                aprobadosListAprobados = em.merge(aprobadosListAprobados);
                if (oldIdCotOfAprobadosListAprobados != null) {
                    oldIdCotOfAprobadosListAprobados.getAprobadosList().remove(aprobadosListAprobados);
                    oldIdCotOfAprobadosListAprobados = em.merge(oldIdCotOfAprobadosListAprobados);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CotizacionProd cotizacionProd) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CotizacionProd persistentCotizacionProd = em.find(CotizacionProd.class, cotizacionProd.getId());
            SolicitudPr numSolOld = persistentCotizacionProd.getNumSol();
            SolicitudPr numSolNew = cotizacionProd.getNumSol();
            Proveedor nitOld = persistentCotizacionProd.getNit();
            Proveedor nitNew = cotizacionProd.getNit();
            Usuario idAoOld = persistentCotizacionProd.getIdAo();
            Usuario idAoNew = cotizacionProd.getIdAo();
            Item cinternoOld = persistentCotizacionProd.getCinterno();
            Item cinternoNew = cotizacionProd.getCinterno();
            List<Aprobados> aprobadosListOld = persistentCotizacionProd.getAprobadosList();
            List<Aprobados> aprobadosListNew = cotizacionProd.getAprobadosList();
            List<String> illegalOrphanMessages = null;
            for (Aprobados aprobadosListOldAprobados : aprobadosListOld) {
                if (!aprobadosListNew.contains(aprobadosListOldAprobados)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Aprobados " + aprobadosListOldAprobados + " since its idCot field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (numSolNew != null) {
                numSolNew = em.getReference(numSolNew.getClass(), numSolNew.getNumSol());
                cotizacionProd.setNumSol(numSolNew);
            }
            if (nitNew != null) {
                nitNew = em.getReference(nitNew.getClass(), nitNew.getNit());
                cotizacionProd.setNit(nitNew);
            }
            if (idAoNew != null) {
                idAoNew = em.getReference(idAoNew.getClass(), idAoNew.getId());
                cotizacionProd.setIdAo(idAoNew);
            }
            if (cinternoNew != null) {
                cinternoNew = em.getReference(cinternoNew.getClass(), cinternoNew.getCinterno());
                cotizacionProd.setCinterno(cinternoNew);
            }
            List<Aprobados> attachedAprobadosListNew = new ArrayList<Aprobados>();
            for (Aprobados aprobadosListNewAprobadosToAttach : aprobadosListNew) {
                aprobadosListNewAprobadosToAttach = em.getReference(aprobadosListNewAprobadosToAttach.getClass(), aprobadosListNewAprobadosToAttach.getIdAprobado());
                attachedAprobadosListNew.add(aprobadosListNewAprobadosToAttach);
            }
            aprobadosListNew = attachedAprobadosListNew;
            cotizacionProd.setAprobadosList(aprobadosListNew);
            cotizacionProd = em.merge(cotizacionProd);
            if (numSolOld != null && !numSolOld.equals(numSolNew)) {
                numSolOld.getCotizacionProdList().remove(cotizacionProd);
                numSolOld = em.merge(numSolOld);
            }
            if (numSolNew != null && !numSolNew.equals(numSolOld)) {
                numSolNew.getCotizacionProdList().add(cotizacionProd);
                numSolNew = em.merge(numSolNew);
            }
            if (nitOld != null && !nitOld.equals(nitNew)) {
                nitOld.getCotizacionProdList().remove(cotizacionProd);
                nitOld = em.merge(nitOld);
            }
            if (nitNew != null && !nitNew.equals(nitOld)) {
                nitNew.getCotizacionProdList().add(cotizacionProd);
                nitNew = em.merge(nitNew);
            }
            if (idAoOld != null && !idAoOld.equals(idAoNew)) {
                idAoOld.getCotizacionProdList().remove(cotizacionProd);
                idAoOld = em.merge(idAoOld);
            }
            if (idAoNew != null && !idAoNew.equals(idAoOld)) {
                idAoNew.getCotizacionProdList().add(cotizacionProd);
                idAoNew = em.merge(idAoNew);
            }
            if (cinternoOld != null && !cinternoOld.equals(cinternoNew)) {
                cinternoOld.getCotizacionProdList().remove(cotizacionProd);
                cinternoOld = em.merge(cinternoOld);
            }
            if (cinternoNew != null && !cinternoNew.equals(cinternoOld)) {
                cinternoNew.getCotizacionProdList().add(cotizacionProd);
                cinternoNew = em.merge(cinternoNew);
            }
            for (Aprobados aprobadosListNewAprobados : aprobadosListNew) {
                if (!aprobadosListOld.contains(aprobadosListNewAprobados)) {
                    CotizacionProd oldIdCotOfAprobadosListNewAprobados = aprobadosListNewAprobados.getIdCot();
                    aprobadosListNewAprobados.setIdCot(cotizacionProd);
                    aprobadosListNewAprobados = em.merge(aprobadosListNewAprobados);
                    if (oldIdCotOfAprobadosListNewAprobados != null && !oldIdCotOfAprobadosListNewAprobados.equals(cotizacionProd)) {
                        oldIdCotOfAprobadosListNewAprobados.getAprobadosList().remove(aprobadosListNewAprobados);
                        oldIdCotOfAprobadosListNewAprobados = em.merge(oldIdCotOfAprobadosListNewAprobados);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Double id = cotizacionProd.getId();
                if (findCotizacionProd(id) == null) {
                    throw new NonexistentEntityException("The cotizacionProd with id " + id + " no longer exists.");
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
            CotizacionProd cotizacionProd;
            try {
                cotizacionProd = em.getReference(CotizacionProd.class, id);
                cotizacionProd.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cotizacionProd with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Aprobados> aprobadosListOrphanCheck = cotizacionProd.getAprobadosList();
            for (Aprobados aprobadosListOrphanCheckAprobados : aprobadosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CotizacionProd (" + cotizacionProd + ") cannot be destroyed since the Aprobados " + aprobadosListOrphanCheckAprobados + " in its aprobadosList field has a non-nullable idCot field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            SolicitudPr numSol = cotizacionProd.getNumSol();
            if (numSol != null) {
                numSol.getCotizacionProdList().remove(cotizacionProd);
                numSol = em.merge(numSol);
            }
            Proveedor nit = cotizacionProd.getNit();
            if (nit != null) {
                nit.getCotizacionProdList().remove(cotizacionProd);
                nit = em.merge(nit);
            }
            Usuario idAo = cotizacionProd.getIdAo();
            if (idAo != null) {
                idAo.getCotizacionProdList().remove(cotizacionProd);
                idAo = em.merge(idAo);
            }
            Item cinterno = cotizacionProd.getCinterno();
            if (cinterno != null) {
                cinterno.getCotizacionProdList().remove(cotizacionProd);
                cinterno = em.merge(cinterno);
            }
            em.remove(cotizacionProd);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CotizacionProd> findCotizacionProdEntities() {
        return findCotizacionProdEntities(true, -1, -1);
    }

    public List<CotizacionProd> findCotizacionProdEntities(int maxResults, int firstResult) {
        return findCotizacionProdEntities(false, maxResults, firstResult);
    }

    private List<CotizacionProd> findCotizacionProdEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CotizacionProd.class));
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

    public CotizacionProd findCotizacionProd(Double id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CotizacionProd.class, id);
        } finally {
            em.close();
        }
    }

    public int getCotizacionProdCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CotizacionProd> rt = cq.from(CotizacionProd.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
