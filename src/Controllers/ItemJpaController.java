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
import Entities.Descargo;
import Entities.Itxsol;
import Entities.Aprobados;
import Entities.Recepcion;
import Entities.Itmxorden;
import Entities.CotizacionProd;
import Entities.Item;
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
        if (item.getIxpList() == null) {
            item.setIxpList(new ArrayList<Ixp>());
        }
        if (item.getDescargoList() == null) {
            item.setDescargoList(new ArrayList<Descargo>());
        }
        if (item.getItxsolList() == null) {
            item.setItxsolList(new ArrayList<Itxsol>());
        }
        if (item.getAprobadosList() == null) {
            item.setAprobadosList(new ArrayList<Aprobados>());
        }
        if (item.getRecepcionList() == null) {
            item.setRecepcionList(new ArrayList<Recepcion>());
        }
        if (item.getItmxordenList() == null) {
            item.setItmxordenList(new ArrayList<Itmxorden>());
        }
        if (item.getCotizacionProdList() == null) {
            item.setCotizacionProdList(new ArrayList<CotizacionProd>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Ixp> attachedIxpList = new ArrayList<Ixp>();
            for (Ixp ixpListIxpToAttach : item.getIxpList()) {
                ixpListIxpToAttach = em.getReference(ixpListIxpToAttach.getClass(), ixpListIxpToAttach.getIxpPK());
                attachedIxpList.add(ixpListIxpToAttach);
            }
            item.setIxpList(attachedIxpList);
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
            List<Aprobados> attachedAprobadosList = new ArrayList<Aprobados>();
            for (Aprobados aprobadosListAprobadosToAttach : item.getAprobadosList()) {
                aprobadosListAprobadosToAttach = em.getReference(aprobadosListAprobadosToAttach.getClass(), aprobadosListAprobadosToAttach.getIdAprobado());
                attachedAprobadosList.add(aprobadosListAprobadosToAttach);
            }
            item.setAprobadosList(attachedAprobadosList);
            List<Recepcion> attachedRecepcionList = new ArrayList<Recepcion>();
            for (Recepcion recepcionListRecepcionToAttach : item.getRecepcionList()) {
                recepcionListRecepcionToAttach = em.getReference(recepcionListRecepcionToAttach.getClass(), recepcionListRecepcionToAttach.getFechallegada());
                attachedRecepcionList.add(recepcionListRecepcionToAttach);
            }
            item.setRecepcionList(attachedRecepcionList);
            List<Itmxorden> attachedItmxordenList = new ArrayList<Itmxorden>();
            for (Itmxorden itmxordenListItmxordenToAttach : item.getItmxordenList()) {
                itmxordenListItmxordenToAttach = em.getReference(itmxordenListItmxordenToAttach.getClass(), itmxordenListItmxordenToAttach.getItmxordenPK());
                attachedItmxordenList.add(itmxordenListItmxordenToAttach);
            }
            item.setItmxordenList(attachedItmxordenList);
            List<CotizacionProd> attachedCotizacionProdList = new ArrayList<CotizacionProd>();
            for (CotizacionProd cotizacionProdListCotizacionProdToAttach : item.getCotizacionProdList()) {
                cotizacionProdListCotizacionProdToAttach = em.getReference(cotizacionProdListCotizacionProdToAttach.getClass(), cotizacionProdListCotizacionProdToAttach.getId());
                attachedCotizacionProdList.add(cotizacionProdListCotizacionProdToAttach);
            }
            item.setCotizacionProdList(attachedCotizacionProdList);
            em.persist(item);
            for (Ixp ixpListIxp : item.getIxpList()) {
                Item oldItemOfIxpListIxp = ixpListIxp.getItem();
                ixpListIxp.setItem(item);
                ixpListIxp = em.merge(ixpListIxp);
                if (oldItemOfIxpListIxp != null) {
                    oldItemOfIxpListIxp.getIxpList().remove(ixpListIxp);
                    oldItemOfIxpListIxp = em.merge(oldItemOfIxpListIxp);
                }
            }
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
            for (Aprobados aprobadosListAprobados : item.getAprobadosList()) {
                Item oldCinternoOfAprobadosListAprobados = aprobadosListAprobados.getCinterno();
                aprobadosListAprobados.setCinterno(item);
                aprobadosListAprobados = em.merge(aprobadosListAprobados);
                if (oldCinternoOfAprobadosListAprobados != null) {
                    oldCinternoOfAprobadosListAprobados.getAprobadosList().remove(aprobadosListAprobados);
                    oldCinternoOfAprobadosListAprobados = em.merge(oldCinternoOfAprobadosListAprobados);
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
            for (CotizacionProd cotizacionProdListCotizacionProd : item.getCotizacionProdList()) {
                Item oldCinternoOfCotizacionProdListCotizacionProd = cotizacionProdListCotizacionProd.getCinterno();
                cotizacionProdListCotizacionProd.setCinterno(item);
                cotizacionProdListCotizacionProd = em.merge(cotizacionProdListCotizacionProd);
                if (oldCinternoOfCotizacionProdListCotizacionProd != null) {
                    oldCinternoOfCotizacionProdListCotizacionProd.getCotizacionProdList().remove(cotizacionProdListCotizacionProd);
                    oldCinternoOfCotizacionProdListCotizacionProd = em.merge(oldCinternoOfCotizacionProdListCotizacionProd);
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
            List<Ixp> ixpListOld = persistentItem.getIxpList();
            List<Ixp> ixpListNew = item.getIxpList();
            List<Descargo> descargoListOld = persistentItem.getDescargoList();
            List<Descargo> descargoListNew = item.getDescargoList();
            List<Itxsol> itxsolListOld = persistentItem.getItxsolList();
            List<Itxsol> itxsolListNew = item.getItxsolList();
            List<Aprobados> aprobadosListOld = persistentItem.getAprobadosList();
            List<Aprobados> aprobadosListNew = item.getAprobadosList();
            List<Recepcion> recepcionListOld = persistentItem.getRecepcionList();
            List<Recepcion> recepcionListNew = item.getRecepcionList();
            List<Itmxorden> itmxordenListOld = persistentItem.getItmxordenList();
            List<Itmxorden> itmxordenListNew = item.getItmxordenList();
            List<CotizacionProd> cotizacionProdListOld = persistentItem.getCotizacionProdList();
            List<CotizacionProd> cotizacionProdListNew = item.getCotizacionProdList();
            List<String> illegalOrphanMessages = null;
            for (Ixp ixpListOldIxp : ixpListOld) {
                if (!ixpListNew.contains(ixpListOldIxp)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ixp " + ixpListOldIxp + " since its item field is not nullable.");
                }
            }
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
            for (Aprobados aprobadosListOldAprobados : aprobadosListOld) {
                if (!aprobadosListNew.contains(aprobadosListOldAprobados)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Aprobados " + aprobadosListOldAprobados + " since its cinterno field is not nullable.");
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
            for (CotizacionProd cotizacionProdListOldCotizacionProd : cotizacionProdListOld) {
                if (!cotizacionProdListNew.contains(cotizacionProdListOldCotizacionProd)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CotizacionProd " + cotizacionProdListOldCotizacionProd + " since its cinterno field is not nullable.");
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
            item.setIxpList(ixpListNew);
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
            List<Aprobados> attachedAprobadosListNew = new ArrayList<Aprobados>();
            for (Aprobados aprobadosListNewAprobadosToAttach : aprobadosListNew) {
                aprobadosListNewAprobadosToAttach = em.getReference(aprobadosListNewAprobadosToAttach.getClass(), aprobadosListNewAprobadosToAttach.getIdAprobado());
                attachedAprobadosListNew.add(aprobadosListNewAprobadosToAttach);
            }
            aprobadosListNew = attachedAprobadosListNew;
            item.setAprobadosList(aprobadosListNew);
            List<Recepcion> attachedRecepcionListNew = new ArrayList<Recepcion>();
            for (Recepcion recepcionListNewRecepcionToAttach : recepcionListNew) {
                recepcionListNewRecepcionToAttach = em.getReference(recepcionListNewRecepcionToAttach.getClass(), recepcionListNewRecepcionToAttach.getFechallegada());
                attachedRecepcionListNew.add(recepcionListNewRecepcionToAttach);
            }
            recepcionListNew = attachedRecepcionListNew;
            item.setRecepcionList(recepcionListNew);
            List<Itmxorden> attachedItmxordenListNew = new ArrayList<Itmxorden>();
            for (Itmxorden itmxordenListNewItmxordenToAttach : itmxordenListNew) {
                itmxordenListNewItmxordenToAttach = em.getReference(itmxordenListNewItmxordenToAttach.getClass(), itmxordenListNewItmxordenToAttach.getItmxordenPK());
                attachedItmxordenListNew.add(itmxordenListNewItmxordenToAttach);
            }
            itmxordenListNew = attachedItmxordenListNew;
            item.setItmxordenList(itmxordenListNew);
            List<CotizacionProd> attachedCotizacionProdListNew = new ArrayList<CotizacionProd>();
            for (CotizacionProd cotizacionProdListNewCotizacionProdToAttach : cotizacionProdListNew) {
                cotizacionProdListNewCotizacionProdToAttach = em.getReference(cotizacionProdListNewCotizacionProdToAttach.getClass(), cotizacionProdListNewCotizacionProdToAttach.getId());
                attachedCotizacionProdListNew.add(cotizacionProdListNewCotizacionProdToAttach);
            }
            cotizacionProdListNew = attachedCotizacionProdListNew;
            item.setCotizacionProdList(cotizacionProdListNew);
            item = em.merge(item);
            for (Ixp ixpListNewIxp : ixpListNew) {
                if (!ixpListOld.contains(ixpListNewIxp)) {
                    Item oldItemOfIxpListNewIxp = ixpListNewIxp.getItem();
                    ixpListNewIxp.setItem(item);
                    ixpListNewIxp = em.merge(ixpListNewIxp);
                    if (oldItemOfIxpListNewIxp != null && !oldItemOfIxpListNewIxp.equals(item)) {
                        oldItemOfIxpListNewIxp.getIxpList().remove(ixpListNewIxp);
                        oldItemOfIxpListNewIxp = em.merge(oldItemOfIxpListNewIxp);
                    }
                }
            }
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
            for (Aprobados aprobadosListNewAprobados : aprobadosListNew) {
                if (!aprobadosListOld.contains(aprobadosListNewAprobados)) {
                    Item oldCinternoOfAprobadosListNewAprobados = aprobadosListNewAprobados.getCinterno();
                    aprobadosListNewAprobados.setCinterno(item);
                    aprobadosListNewAprobados = em.merge(aprobadosListNewAprobados);
                    if (oldCinternoOfAprobadosListNewAprobados != null && !oldCinternoOfAprobadosListNewAprobados.equals(item)) {
                        oldCinternoOfAprobadosListNewAprobados.getAprobadosList().remove(aprobadosListNewAprobados);
                        oldCinternoOfAprobadosListNewAprobados = em.merge(oldCinternoOfAprobadosListNewAprobados);
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
            for (CotizacionProd cotizacionProdListNewCotizacionProd : cotizacionProdListNew) {
                if (!cotizacionProdListOld.contains(cotizacionProdListNewCotizacionProd)) {
                    Item oldCinternoOfCotizacionProdListNewCotizacionProd = cotizacionProdListNewCotizacionProd.getCinterno();
                    cotizacionProdListNewCotizacionProd.setCinterno(item);
                    cotizacionProdListNewCotizacionProd = em.merge(cotizacionProdListNewCotizacionProd);
                    if (oldCinternoOfCotizacionProdListNewCotizacionProd != null && !oldCinternoOfCotizacionProdListNewCotizacionProd.equals(item)) {
                        oldCinternoOfCotizacionProdListNewCotizacionProd.getCotizacionProdList().remove(cotizacionProdListNewCotizacionProd);
                        oldCinternoOfCotizacionProdListNewCotizacionProd = em.merge(oldCinternoOfCotizacionProdListNewCotizacionProd);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = item.getCinterno();
                System.out.println(id);
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
            List<Ixp> ixpListOrphanCheck = item.getIxpList();
            for (Ixp ixpListOrphanCheckIxp : ixpListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Item (" + item + ") cannot be destroyed since the Ixp " + ixpListOrphanCheckIxp + " in its ixpList field has a non-nullable item field.");
            }
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
            List<Aprobados> aprobadosListOrphanCheck = item.getAprobadosList();
            for (Aprobados aprobadosListOrphanCheckAprobados : aprobadosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Item (" + item + ") cannot be destroyed since the Aprobados " + aprobadosListOrphanCheckAprobados + " in its aprobadosList field has a non-nullable cinterno field.");
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
            List<CotizacionProd> cotizacionProdListOrphanCheck = item.getCotizacionProdList();
            for (CotizacionProd cotizacionProdListOrphanCheckCotizacionProd : cotizacionProdListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Item (" + item + ") cannot be destroyed since the CotizacionProd " + cotizacionProdListOrphanCheckCotizacionProd + " in its cotizacionProdList field has a non-nullable cinterno field.");
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
