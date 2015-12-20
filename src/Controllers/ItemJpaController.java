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
import Entities.Descargo;
import Entities.Item;
import java.util.ArrayList;
import java.util.List;
import Entities.Itxsol;
import Entities.Recepcion;
import Entities.Itmxorden;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class ItemJpaController implements Serializable {

    public ItemJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Item item) throws PreexistingEntityException, Exception {
        if (item.getDescargoList() == null) {
            item.setDescargoList(new ArrayList<Descargo>());
        }
        if (item.getItxsolList() == null) {
            item.setItxsolList(new ArrayList<Itxsol>());
        }
        if (item.getRecepcionList() == null) {
            item.setRecepcionList(new ArrayList<Recepcion>());
        }
        if (item.getItmxordenList() == null) {
            item.setItmxordenList(new ArrayList<Itmxorden>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Descargo> attachedDescargoList = new ArrayList<Descargo>();
            for (Descargo descargoListDescargoToAttach : item.getDescargoList()) {
                descargoListDescargoToAttach = em.getReference(descargoListDescargoToAttach.getClass(), descargoListDescargoToAttach.getId());
                attachedDescargoList.add(descargoListDescargoToAttach);
            }
            item.setDescargoList(attachedDescargoList);
            List<Itxsol> attachedItxsolList = new ArrayList<Itxsol>();
            for (Itxsol itxsolListItxsolToAttach : item.getItxsolList()) {
                itxsolListItxsolToAttach = em.getReference(itxsolListItxsolToAttach.getClass(), itxsolListItxsolToAttach.getId());
                attachedItxsolList.add(itxsolListItxsolToAttach);
            }
            item.setItxsolList(attachedItxsolList);
            List<Recepcion> attachedRecepcionList = new ArrayList<Recepcion>();
            for (Recepcion recepcionListRecepcionToAttach : item.getRecepcionList()) {
                recepcionListRecepcionToAttach = em.getReference(recepcionListRecepcionToAttach.getClass(), recepcionListRecepcionToAttach.getFechallegada());
                attachedRecepcionList.add(recepcionListRecepcionToAttach);
            }
            item.setRecepcionList(attachedRecepcionList);
            List<Itmxorden> attachedItmxordenList = new ArrayList<Itmxorden>();
            for (Itmxorden itmxordenListItmxordenToAttach : item.getItmxordenList()) {
                itmxordenListItmxordenToAttach = em.getReference(itmxordenListItmxordenToAttach.getClass(), itmxordenListItmxordenToAttach.getIdOCompra());
                attachedItmxordenList.add(itmxordenListItmxordenToAttach);
            }
            item.setItmxordenList(attachedItmxordenList);
            em.persist(item);
            for (Descargo descargoListDescargo : item.getDescargoList()) {
                Item oldCinternoOfDescargoListDescargo = descargoListDescargo.getCinterno();
                descargoListDescargo.setCinterno(item);
                descargoListDescargo = em.merge(descargoListDescargo);
                if (oldCinternoOfDescargoListDescargo != null) {
                    oldCinternoOfDescargoListDescargo.getDescargoList().remove(descargoListDescargo);
                    oldCinternoOfDescargoListDescargo = em.merge(oldCinternoOfDescargoListDescargo);
                }
            }
            for (Itxsol itxsolListItxsol : item.getItxsolList()) {
                Item oldCinternoOfItxsolListItxsol = itxsolListItxsol.getCinterno();
                itxsolListItxsol.setCinterno(item);
                itxsolListItxsol = em.merge(itxsolListItxsol);
                if (oldCinternoOfItxsolListItxsol != null) {
                    oldCinternoOfItxsolListItxsol.getItxsolList().remove(itxsolListItxsol);
                    oldCinternoOfItxsolListItxsol = em.merge(oldCinternoOfItxsolListItxsol);
                }
            }
            for (Recepcion recepcionListRecepcion : item.getRecepcionList()) {
                Item oldCinternoOfRecepcionListRecepcion = recepcionListRecepcion.getCinterno();
                recepcionListRecepcion.setCinterno(item);
                recepcionListRecepcion = em.merge(recepcionListRecepcion);
                if (oldCinternoOfRecepcionListRecepcion != null) {
                    oldCinternoOfRecepcionListRecepcion.getRecepcionList().remove(recepcionListRecepcion);
                    oldCinternoOfRecepcionListRecepcion = em.merge(oldCinternoOfRecepcionListRecepcion);
                }
            }
            for (Itmxorden itmxordenListItmxorden : item.getItmxordenList()) {
                Item oldItemCinternoOfItmxordenListItmxorden = itmxordenListItmxorden.getItemCinterno();
                itmxordenListItmxorden.setItemCinterno(item);
                itmxordenListItmxorden = em.merge(itmxordenListItmxorden);
                if (oldItemCinternoOfItmxordenListItmxorden != null) {
                    oldItemCinternoOfItmxordenListItmxorden.getItmxordenList().remove(itmxordenListItmxorden);
                    oldItemCinternoOfItmxordenListItmxorden = em.merge(oldItemCinternoOfItmxordenListItmxorden);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findItem(item.getCinterno()) != null) {
                throw new PreexistingEntityException("Item " + item + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Item item) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Item persistentItem = em.find(Item.class, item.getCinterno());
            List<Descargo> descargoListOld = persistentItem.getDescargoList();
            List<Descargo> descargoListNew = item.getDescargoList();
            List<Itxsol> itxsolListOld = persistentItem.getItxsolList();
            List<Itxsol> itxsolListNew = item.getItxsolList();
            List<Recepcion> recepcionListOld = persistentItem.getRecepcionList();
            List<Recepcion> recepcionListNew = item.getRecepcionList();
            List<Itmxorden> itmxordenListOld = persistentItem.getItmxordenList();
            List<Itmxorden> itmxordenListNew = item.getItmxordenList();
            List<String> illegalOrphanMessages = null;
            for (Descargo descargoListOldDescargo : descargoListOld) {
                if (!descargoListNew.contains(descargoListOldDescargo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Descargo " + descargoListOldDescargo + " since its cinterno field is not nullable.");
                }
            }
            for (Itxsol itxsolListOldItxsol : itxsolListOld) {
                if (!itxsolListNew.contains(itxsolListOldItxsol)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Itxsol " + itxsolListOldItxsol + " since its cinterno field is not nullable.");
                }
            }
            for (Recepcion recepcionListOldRecepcion : recepcionListOld) {
                if (!recepcionListNew.contains(recepcionListOldRecepcion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Recepcion " + recepcionListOldRecepcion + " since its cinterno field is not nullable.");
                }
            }
            for (Itmxorden itmxordenListOldItmxorden : itmxordenListOld) {
                if (!itmxordenListNew.contains(itmxordenListOldItmxorden)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Itmxorden " + itmxordenListOldItmxorden + " since its itemCinterno field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Descargo> attachedDescargoListNew = new ArrayList<Descargo>();
            for (Descargo descargoListNewDescargoToAttach : descargoListNew) {
                descargoListNewDescargoToAttach = em.getReference(descargoListNewDescargoToAttach.getClass(), descargoListNewDescargoToAttach.getId());
                attachedDescargoListNew.add(descargoListNewDescargoToAttach);
            }
            descargoListNew = attachedDescargoListNew;
            item.setDescargoList(descargoListNew);
            List<Itxsol> attachedItxsolListNew = new ArrayList<Itxsol>();
            for (Itxsol itxsolListNewItxsolToAttach : itxsolListNew) {
                itxsolListNewItxsolToAttach = em.getReference(itxsolListNewItxsolToAttach.getClass(), itxsolListNewItxsolToAttach.getId());
                attachedItxsolListNew.add(itxsolListNewItxsolToAttach);
            }
            itxsolListNew = attachedItxsolListNew;
            item.setItxsolList(itxsolListNew);
            List<Recepcion> attachedRecepcionListNew = new ArrayList<Recepcion>();
            for (Recepcion recepcionListNewRecepcionToAttach : recepcionListNew) {
                recepcionListNewRecepcionToAttach = em.getReference(recepcionListNewRecepcionToAttach.getClass(), recepcionListNewRecepcionToAttach.getFechallegada());
                attachedRecepcionListNew.add(recepcionListNewRecepcionToAttach);
            }
            recepcionListNew = attachedRecepcionListNew;
            item.setRecepcionList(recepcionListNew);
            List<Itmxorden> attachedItmxordenListNew = new ArrayList<Itmxorden>();
            for (Itmxorden itmxordenListNewItmxordenToAttach : itmxordenListNew) {
                itmxordenListNewItmxordenToAttach = em.getReference(itmxordenListNewItmxordenToAttach.getClass(), itmxordenListNewItmxordenToAttach.getIdOCompra());
                attachedItmxordenListNew.add(itmxordenListNewItmxordenToAttach);
            }
            itmxordenListNew = attachedItmxordenListNew;
            item.setItmxordenList(itmxordenListNew);
            item = em.merge(item);
            for (Descargo descargoListNewDescargo : descargoListNew) {
                if (!descargoListOld.contains(descargoListNewDescargo)) {
                    Item oldCinternoOfDescargoListNewDescargo = descargoListNewDescargo.getCinterno();
                    descargoListNewDescargo.setCinterno(item);
                    descargoListNewDescargo = em.merge(descargoListNewDescargo);
                    if (oldCinternoOfDescargoListNewDescargo != null && !oldCinternoOfDescargoListNewDescargo.equals(item)) {
                        oldCinternoOfDescargoListNewDescargo.getDescargoList().remove(descargoListNewDescargo);
                        oldCinternoOfDescargoListNewDescargo = em.merge(oldCinternoOfDescargoListNewDescargo);
                    }
                }
            }
            for (Itxsol itxsolListNewItxsol : itxsolListNew) {
                if (!itxsolListOld.contains(itxsolListNewItxsol)) {
                    Item oldCinternoOfItxsolListNewItxsol = itxsolListNewItxsol.getCinterno();
                    itxsolListNewItxsol.setCinterno(item);
                    itxsolListNewItxsol = em.merge(itxsolListNewItxsol);
                    if (oldCinternoOfItxsolListNewItxsol != null && !oldCinternoOfItxsolListNewItxsol.equals(item)) {
                        oldCinternoOfItxsolListNewItxsol.getItxsolList().remove(itxsolListNewItxsol);
                        oldCinternoOfItxsolListNewItxsol = em.merge(oldCinternoOfItxsolListNewItxsol);
                    }
                }
            }
            for (Recepcion recepcionListNewRecepcion : recepcionListNew) {
                if (!recepcionListOld.contains(recepcionListNewRecepcion)) {
                    Item oldCinternoOfRecepcionListNewRecepcion = recepcionListNewRecepcion.getCinterno();
                    recepcionListNewRecepcion.setCinterno(item);
                    recepcionListNewRecepcion = em.merge(recepcionListNewRecepcion);
                    if (oldCinternoOfRecepcionListNewRecepcion != null && !oldCinternoOfRecepcionListNewRecepcion.equals(item)) {
                        oldCinternoOfRecepcionListNewRecepcion.getRecepcionList().remove(recepcionListNewRecepcion);
                        oldCinternoOfRecepcionListNewRecepcion = em.merge(oldCinternoOfRecepcionListNewRecepcion);
                    }
                }
            }
            for (Itmxorden itmxordenListNewItmxorden : itmxordenListNew) {
                if (!itmxordenListOld.contains(itmxordenListNewItmxorden)) {
                    Item oldItemCinternoOfItmxordenListNewItmxorden = itmxordenListNewItmxorden.getItemCinterno();
                    itmxordenListNewItmxorden.setItemCinterno(item);
                    itmxordenListNewItmxorden = em.merge(itmxordenListNewItmxorden);
                    if (oldItemCinternoOfItmxordenListNewItmxorden != null && !oldItemCinternoOfItmxordenListNewItmxorden.equals(item)) {
                        oldItemCinternoOfItmxordenListNewItmxorden.getItmxordenList().remove(itmxordenListNewItmxorden);
                        oldItemCinternoOfItmxordenListNewItmxorden = em.merge(oldItemCinternoOfItmxordenListNewItmxorden);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = item.getCinterno();
                if (findItem(id) == null) {
                    throw new NonexistentEntityException("The item with id " + id + " no longer exists.");
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
            Item item;
            try {
                item = em.getReference(Item.class, id);
                item.getCinterno();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The item with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Descargo> descargoListOrphanCheck = item.getDescargoList();
            for (Descargo descargoListOrphanCheckDescargo : descargoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Item (" + item + ") cannot be destroyed since the Descargo " + descargoListOrphanCheckDescargo + " in its descargoList field has a non-nullable cinterno field.");
            }
            List<Itxsol> itxsolListOrphanCheck = item.getItxsolList();
            for (Itxsol itxsolListOrphanCheckItxsol : itxsolListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Item (" + item + ") cannot be destroyed since the Itxsol " + itxsolListOrphanCheckItxsol + " in its itxsolList field has a non-nullable cinterno field.");
            }
            List<Recepcion> recepcionListOrphanCheck = item.getRecepcionList();
            for (Recepcion recepcionListOrphanCheckRecepcion : recepcionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Item (" + item + ") cannot be destroyed since the Recepcion " + recepcionListOrphanCheckRecepcion + " in its recepcionList field has a non-nullable cinterno field.");
            }
            List<Itmxorden> itmxordenListOrphanCheck = item.getItmxordenList();
            for (Itmxorden itmxordenListOrphanCheckItmxorden : itmxordenListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Item (" + item + ") cannot be destroyed since the Itmxorden " + itmxordenListOrphanCheckItmxorden + " in its itmxordenList field has a non-nullable itemCinterno field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(item);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Item> findItemEntities() {
        return findItemEntities(true, -1, -1);
    }

    public List<Item> findItemEntities(int maxResults, int firstResult) {
        return findItemEntities(false, maxResults, firstResult);
    }

    private List<Item> findItemEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Item.class));
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

    public Item findItem(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Item.class, id);
        } finally {
            em.close();
        }
    }

    public int getItemCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Item> rt = cq.from(Item.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
