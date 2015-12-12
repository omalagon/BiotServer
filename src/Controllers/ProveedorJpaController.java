/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.IllegalOrphanException;
import Controllers.exceptions.NonexistentEntityException;
import Controllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Ixp;
import java.util.ArrayList;
import java.util.List;
import Entities.Itmxorden;
import Entities.CotizacionProd;
import Entities.Proveedor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class ProveedorJpaController implements Serializable {

    public ProveedorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Proveedor proveedor) throws PreexistingEntityException, Exception {
        if (proveedor.getIxpList() == null) {
            proveedor.setIxpList(new ArrayList<Ixp>());
        }
        if (proveedor.getItmxordenList() == null) {
            proveedor.setItmxordenList(new ArrayList<Itmxorden>());
        }
        if (proveedor.getCotizacionProdList() == null) {
            proveedor.setCotizacionProdList(new ArrayList<CotizacionProd>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Ixp> attachedIxpList = new ArrayList<Ixp>();
            for (Ixp ixpListIxpToAttach : proveedor.getIxpList()) {
                ixpListIxpToAttach = em.getReference(ixpListIxpToAttach.getClass(), ixpListIxpToAttach.getIxpPK());
                attachedIxpList.add(ixpListIxpToAttach);
            }
            proveedor.setIxpList(attachedIxpList);
            List<Itmxorden> attachedItmxordenList = new ArrayList<Itmxorden>();
            for (Itmxorden itmxordenListItmxordenToAttach : proveedor.getItmxordenList()) {
                itmxordenListItmxordenToAttach = em.getReference(itmxordenListItmxordenToAttach.getClass(), itmxordenListItmxordenToAttach.getItmxordenPK());
                attachedItmxordenList.add(itmxordenListItmxordenToAttach);
            }
            proveedor.setItmxordenList(attachedItmxordenList);
            List<CotizacionProd> attachedCotizacionProdList = new ArrayList<CotizacionProd>();
            for (CotizacionProd cotizacionProdListCotizacionProdToAttach : proveedor.getCotizacionProdList()) {
                cotizacionProdListCotizacionProdToAttach = em.getReference(cotizacionProdListCotizacionProdToAttach.getClass(), cotizacionProdListCotizacionProdToAttach.getId());
                attachedCotizacionProdList.add(cotizacionProdListCotizacionProdToAttach);
            }
            proveedor.setCotizacionProdList(attachedCotizacionProdList);
            em.persist(proveedor);
            for (Ixp ixpListIxp : proveedor.getIxpList()) {
                Proveedor oldProveedorOfIxpListIxp = ixpListIxp.getProveedor();
                ixpListIxp.setProveedor(proveedor);
                ixpListIxp = em.merge(ixpListIxp);
                if (oldProveedorOfIxpListIxp != null) {
                    oldProveedorOfIxpListIxp.getIxpList().remove(ixpListIxp);
                    oldProveedorOfIxpListIxp = em.merge(oldProveedorOfIxpListIxp);
                }
            }
            for (Itmxorden itmxordenListItmxorden : proveedor.getItmxordenList()) {
                Proveedor oldProveedorNitOfItmxordenListItmxorden = itmxordenListItmxorden.getProveedorNit();
                itmxordenListItmxorden.setProveedorNit(proveedor);
                itmxordenListItmxorden = em.merge(itmxordenListItmxorden);
                if (oldProveedorNitOfItmxordenListItmxorden != null) {
                    oldProveedorNitOfItmxordenListItmxorden.getItmxordenList().remove(itmxordenListItmxorden);
                    oldProveedorNitOfItmxordenListItmxorden = em.merge(oldProveedorNitOfItmxordenListItmxorden);
                }
            }
            for (CotizacionProd cotizacionProdListCotizacionProd : proveedor.getCotizacionProdList()) {
                Proveedor oldNitOfCotizacionProdListCotizacionProd = cotizacionProdListCotizacionProd.getNit();
                cotizacionProdListCotizacionProd.setNit(proveedor);
                cotizacionProdListCotizacionProd = em.merge(cotizacionProdListCotizacionProd);
                if (oldNitOfCotizacionProdListCotizacionProd != null) {
                    oldNitOfCotizacionProdListCotizacionProd.getCotizacionProdList().remove(cotizacionProdListCotizacionProd);
                    oldNitOfCotizacionProdListCotizacionProd = em.merge(oldNitOfCotizacionProdListCotizacionProd);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProveedor(proveedor.getNit()) != null) {
                throw new PreexistingEntityException("Proveedor " + proveedor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Proveedor proveedor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedor persistentProveedor = em.find(Proveedor.class, proveedor.getNit());
            List<Ixp> ixpListOld = persistentProveedor.getIxpList();
            List<Ixp> ixpListNew = proveedor.getIxpList();
            List<Itmxorden> itmxordenListOld = persistentProveedor.getItmxordenList();
            List<Itmxorden> itmxordenListNew = proveedor.getItmxordenList();
            List<CotizacionProd> cotizacionProdListOld = persistentProveedor.getCotizacionProdList();
            List<CotizacionProd> cotizacionProdListNew = proveedor.getCotizacionProdList();
            List<String> illegalOrphanMessages = null;
            for (Ixp ixpListOldIxp : ixpListOld) {
                if (!ixpListNew.contains(ixpListOldIxp)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ixp " + ixpListOldIxp + " since its proveedor field is not nullable.");
                }
            }
            for (Itmxorden itmxordenListOldItmxorden : itmxordenListOld) {
                if (!itmxordenListNew.contains(itmxordenListOldItmxorden)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Itmxorden " + itmxordenListOldItmxorden + " since its proveedorNit field is not nullable.");
                }
            }
            for (CotizacionProd cotizacionProdListOldCotizacionProd : cotizacionProdListOld) {
                if (!cotizacionProdListNew.contains(cotizacionProdListOldCotizacionProd)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CotizacionProd " + cotizacionProdListOldCotizacionProd + " since its nit field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Ixp> attachedIxpListNew = new ArrayList<Ixp>();
            for (Ixp ixpListNewIxpToAttach : ixpListNew) {
                ixpListNewIxpToAttach = em.getReference(ixpListNewIxpToAttach.getClass(), ixpListNewIxpToAttach.getIxpPK());
                attachedIxpListNew.add(ixpListNewIxpToAttach);
            }
            ixpListNew = attachedIxpListNew;
            proveedor.setIxpList(ixpListNew);
            List<Itmxorden> attachedItmxordenListNew = new ArrayList<Itmxorden>();
            for (Itmxorden itmxordenListNewItmxordenToAttach : itmxordenListNew) {
                itmxordenListNewItmxordenToAttach = em.getReference(itmxordenListNewItmxordenToAttach.getClass(), itmxordenListNewItmxordenToAttach.getItmxordenPK());
                attachedItmxordenListNew.add(itmxordenListNewItmxordenToAttach);
            }
            itmxordenListNew = attachedItmxordenListNew;
            proveedor.setItmxordenList(itmxordenListNew);
            List<CotizacionProd> attachedCotizacionProdListNew = new ArrayList<CotizacionProd>();
            for (CotizacionProd cotizacionProdListNewCotizacionProdToAttach : cotizacionProdListNew) {
                cotizacionProdListNewCotizacionProdToAttach = em.getReference(cotizacionProdListNewCotizacionProdToAttach.getClass(), cotizacionProdListNewCotizacionProdToAttach.getId());
                attachedCotizacionProdListNew.add(cotizacionProdListNewCotizacionProdToAttach);
            }
            cotizacionProdListNew = attachedCotizacionProdListNew;
            proveedor.setCotizacionProdList(cotizacionProdListNew);
            proveedor = em.merge(proveedor);
            for (Ixp ixpListNewIxp : ixpListNew) {
                if (!ixpListOld.contains(ixpListNewIxp)) {
                    Proveedor oldProveedorOfIxpListNewIxp = ixpListNewIxp.getProveedor();
                    ixpListNewIxp.setProveedor(proveedor);
                    ixpListNewIxp = em.merge(ixpListNewIxp);
                    if (oldProveedorOfIxpListNewIxp != null && !oldProveedorOfIxpListNewIxp.equals(proveedor)) {
                        oldProveedorOfIxpListNewIxp.getIxpList().remove(ixpListNewIxp);
                        oldProveedorOfIxpListNewIxp = em.merge(oldProveedorOfIxpListNewIxp);
                    }
                }
            }
            for (Itmxorden itmxordenListNewItmxorden : itmxordenListNew) {
                if (!itmxordenListOld.contains(itmxordenListNewItmxorden)) {
                    Proveedor oldProveedorNitOfItmxordenListNewItmxorden = itmxordenListNewItmxorden.getProveedorNit();
                    itmxordenListNewItmxorden.setProveedorNit(proveedor);
                    itmxordenListNewItmxorden = em.merge(itmxordenListNewItmxorden);
                    if (oldProveedorNitOfItmxordenListNewItmxorden != null && !oldProveedorNitOfItmxordenListNewItmxorden.equals(proveedor)) {
                        oldProveedorNitOfItmxordenListNewItmxorden.getItmxordenList().remove(itmxordenListNewItmxorden);
                        oldProveedorNitOfItmxordenListNewItmxorden = em.merge(oldProveedorNitOfItmxordenListNewItmxorden);
                    }
                }
            }
            for (CotizacionProd cotizacionProdListNewCotizacionProd : cotizacionProdListNew) {
                if (!cotizacionProdListOld.contains(cotizacionProdListNewCotizacionProd)) {
                    Proveedor oldNitOfCotizacionProdListNewCotizacionProd = cotizacionProdListNewCotizacionProd.getNit();
                    cotizacionProdListNewCotizacionProd.setNit(proveedor);
                    cotizacionProdListNewCotizacionProd = em.merge(cotizacionProdListNewCotizacionProd);
                    if (oldNitOfCotizacionProdListNewCotizacionProd != null && !oldNitOfCotizacionProdListNewCotizacionProd.equals(proveedor)) {
                        oldNitOfCotizacionProdListNewCotizacionProd.getCotizacionProdList().remove(cotizacionProdListNewCotizacionProd);
                        oldNitOfCotizacionProdListNewCotizacionProd = em.merge(oldNitOfCotizacionProdListNewCotizacionProd);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = proveedor.getNit();
                if (findProveedor(id) == null) {
                    throw new NonexistentEntityException("The proveedor with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedor proveedor;
            try {
                proveedor = em.getReference(Proveedor.class, id);
                proveedor.getNit();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The proveedor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ixp> ixpListOrphanCheck = proveedor.getIxpList();
            for (Ixp ixpListOrphanCheckIxp : ixpListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Proveedor (" + proveedor + ") cannot be destroyed since the Ixp " + ixpListOrphanCheckIxp + " in its ixpList field has a non-nullable proveedor field.");
            }
            List<Itmxorden> itmxordenListOrphanCheck = proveedor.getItmxordenList();
            for (Itmxorden itmxordenListOrphanCheckItmxorden : itmxordenListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Proveedor (" + proveedor + ") cannot be destroyed since the Itmxorden " + itmxordenListOrphanCheckItmxorden + " in its itmxordenList field has a non-nullable proveedorNit field.");
            }
            List<CotizacionProd> cotizacionProdListOrphanCheck = proveedor.getCotizacionProdList();
            for (CotizacionProd cotizacionProdListOrphanCheckCotizacionProd : cotizacionProdListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Proveedor (" + proveedor + ") cannot be destroyed since the CotizacionProd " + cotizacionProdListOrphanCheckCotizacionProd + " in its cotizacionProdList field has a non-nullable nit field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(proveedor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Proveedor> findProveedorEntities() {
        return findProveedorEntities(true, -1, -1);
    }

    public List<Proveedor> findProveedorEntities(int maxResults, int firstResult) {
        return findProveedorEntities(false, maxResults, firstResult);
    }

    private List<Proveedor> findProveedorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Proveedor.class));
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

    public Proveedor findProveedor(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Proveedor.class, id);
        } finally {
            em.close();
        }
    }

    public int getProveedorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Proveedor> rt = cq.from(Proveedor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
