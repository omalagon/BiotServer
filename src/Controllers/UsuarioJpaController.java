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
import Entities.Permisos;
import Entities.Usuario;
import Entities.Descargo;
import java.util.ArrayList;
import java.util.List;
import Entities.Aprobados;
import Entities.Recepcion;
import Entities.CotizacionProd;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author malag
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getDescargoList() == null) {
            usuario.setDescargoList(new ArrayList<Descargo>());
        }
        if (usuario.getAprobadosList() == null) {
            usuario.setAprobadosList(new ArrayList<Aprobados>());
        }
        if (usuario.getRecepcionList() == null) {
            usuario.setRecepcionList(new ArrayList<Recepcion>());
        }
        if (usuario.getCotizacionProdList() == null) {
            usuario.setCotizacionProdList(new ArrayList<CotizacionProd>());
        }
        if (usuario.getUsuarioList() == null) {
            usuario.setUsuarioList(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Permisos permisos = usuario.getPermisos();
            if (permisos != null) {
                permisos = em.getReference(permisos.getClass(), permisos.getUsuarioId());
                usuario.setPermisos(permisos);
            }
            Usuario id1 = usuario.getId1();
            if (id1 != null) {
                id1 = em.getReference(id1.getClass(), id1.getId());
                usuario.setId1(id1);
            }
            List<Descargo> attachedDescargoList = new ArrayList<Descargo>();
            for (Descargo descargoListDescargoToAttach : usuario.getDescargoList()) {
                descargoListDescargoToAttach = em.getReference(descargoListDescargoToAttach.getClass(), descargoListDescargoToAttach.getId());
                attachedDescargoList.add(descargoListDescargoToAttach);
            }
            usuario.setDescargoList(attachedDescargoList);
            List<Aprobados> attachedAprobadosList = new ArrayList<Aprobados>();
            for (Aprobados aprobadosListAprobadosToAttach : usuario.getAprobadosList()) {
                aprobadosListAprobadosToAttach = em.getReference(aprobadosListAprobadosToAttach.getClass(), aprobadosListAprobadosToAttach.getIdAprobado());
                attachedAprobadosList.add(aprobadosListAprobadosToAttach);
            }
            usuario.setAprobadosList(attachedAprobadosList);
            List<Recepcion> attachedRecepcionList = new ArrayList<Recepcion>();
            for (Recepcion recepcionListRecepcionToAttach : usuario.getRecepcionList()) {
                recepcionListRecepcionToAttach = em.getReference(recepcionListRecepcionToAttach.getClass(), recepcionListRecepcionToAttach.getFechallegada());
                attachedRecepcionList.add(recepcionListRecepcionToAttach);
            }
            usuario.setRecepcionList(attachedRecepcionList);
            List<CotizacionProd> attachedCotizacionProdList = new ArrayList<CotizacionProd>();
            for (CotizacionProd cotizacionProdListCotizacionProdToAttach : usuario.getCotizacionProdList()) {
                cotizacionProdListCotizacionProdToAttach = em.getReference(cotizacionProdListCotizacionProdToAttach.getClass(), cotizacionProdListCotizacionProdToAttach.getId());
                attachedCotizacionProdList.add(cotizacionProdListCotizacionProdToAttach);
            }
            usuario.setCotizacionProdList(attachedCotizacionProdList);
            List<Usuario> attachedUsuarioList = new ArrayList<Usuario>();
            for (Usuario usuarioListUsuarioToAttach : usuario.getUsuarioList()) {
                usuarioListUsuarioToAttach = em.getReference(usuarioListUsuarioToAttach.getClass(), usuarioListUsuarioToAttach.getId());
                attachedUsuarioList.add(usuarioListUsuarioToAttach);
            }
            usuario.setUsuarioList(attachedUsuarioList);
            em.persist(usuario);
            if (permisos != null) {
                Usuario oldUsuarioOfPermisos = permisos.getUsuario();
                if (oldUsuarioOfPermisos != null) {
                    oldUsuarioOfPermisos.setPermisos(null);
                    oldUsuarioOfPermisos = em.merge(oldUsuarioOfPermisos);
                }
                permisos.setUsuario(usuario);
                permisos = em.merge(permisos);
            }
            if (id1 != null) {
                id1.getUsuarioList().add(usuario);
                id1 = em.merge(id1);
            }
            for (Descargo descargoListDescargo : usuario.getDescargoList()) {
                Usuario oldIdUsuarioOfDescargoListDescargo = descargoListDescargo.getIdUsuario();
                descargoListDescargo.setIdUsuario(usuario);
                descargoListDescargo = em.merge(descargoListDescargo);
                if (oldIdUsuarioOfDescargoListDescargo != null) {
                    oldIdUsuarioOfDescargoListDescargo.getDescargoList().remove(descargoListDescargo);
                    oldIdUsuarioOfDescargoListDescargo = em.merge(oldIdUsuarioOfDescargoListDescargo);
                }
            }
            for (Aprobados aprobadosListAprobados : usuario.getAprobadosList()) {
                Usuario oldIdDaOfAprobadosListAprobados = aprobadosListAprobados.getIdDa();
                aprobadosListAprobados.setIdDa(usuario);
                aprobadosListAprobados = em.merge(aprobadosListAprobados);
                if (oldIdDaOfAprobadosListAprobados != null) {
                    oldIdDaOfAprobadosListAprobados.getAprobadosList().remove(aprobadosListAprobados);
                    oldIdDaOfAprobadosListAprobados = em.merge(oldIdDaOfAprobadosListAprobados);
                }
            }
            for (Recepcion recepcionListRecepcion : usuario.getRecepcionList()) {
                Usuario oldIdUsuarioOfRecepcionListRecepcion = recepcionListRecepcion.getIdUsuario();
                recepcionListRecepcion.setIdUsuario(usuario);
                recepcionListRecepcion = em.merge(recepcionListRecepcion);
                if (oldIdUsuarioOfRecepcionListRecepcion != null) {
                    oldIdUsuarioOfRecepcionListRecepcion.getRecepcionList().remove(recepcionListRecepcion);
                    oldIdUsuarioOfRecepcionListRecepcion = em.merge(oldIdUsuarioOfRecepcionListRecepcion);
                }
            }
            for (CotizacionProd cotizacionProdListCotizacionProd : usuario.getCotizacionProdList()) {
                Usuario oldIdAoOfCotizacionProdListCotizacionProd = cotizacionProdListCotizacionProd.getIdAo();
                cotizacionProdListCotizacionProd.setIdAo(usuario);
                cotizacionProdListCotizacionProd = em.merge(cotizacionProdListCotizacionProd);
                if (oldIdAoOfCotizacionProdListCotizacionProd != null) {
                    oldIdAoOfCotizacionProdListCotizacionProd.getCotizacionProdList().remove(cotizacionProdListCotizacionProd);
                    oldIdAoOfCotizacionProdListCotizacionProd = em.merge(oldIdAoOfCotizacionProdListCotizacionProd);
                }
            }
            for (Usuario usuarioListUsuario : usuario.getUsuarioList()) {
                Usuario oldId1OfUsuarioListUsuario = usuarioListUsuario.getId1();
                usuarioListUsuario.setId1(usuario);
                usuarioListUsuario = em.merge(usuarioListUsuario);
                if (oldId1OfUsuarioListUsuario != null) {
                    oldId1OfUsuarioListUsuario.getUsuarioList().remove(usuarioListUsuario);
                    oldId1OfUsuarioListUsuario = em.merge(oldId1OfUsuarioListUsuario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getId()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getId());
            Permisos permisosOld = persistentUsuario.getPermisos();
            Permisos permisosNew = usuario.getPermisos();
            Usuario id1Old = persistentUsuario.getId1();
            Usuario id1New = usuario.getId1();
            List<Descargo> descargoListOld = persistentUsuario.getDescargoList();
            List<Descargo> descargoListNew = usuario.getDescargoList();
            List<Aprobados> aprobadosListOld = persistentUsuario.getAprobadosList();
            List<Aprobados> aprobadosListNew = usuario.getAprobadosList();
            List<Recepcion> recepcionListOld = persistentUsuario.getRecepcionList();
            List<Recepcion> recepcionListNew = usuario.getRecepcionList();
            List<CotizacionProd> cotizacionProdListOld = persistentUsuario.getCotizacionProdList();
            List<CotizacionProd> cotizacionProdListNew = usuario.getCotizacionProdList();
            List<Usuario> usuarioListOld = persistentUsuario.getUsuarioList();
            List<Usuario> usuarioListNew = usuario.getUsuarioList();
            List<String> illegalOrphanMessages = null;
            if (permisosOld != null && !permisosOld.equals(permisosNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Permisos " + permisosOld + " since its usuario field is not nullable.");
            }
            for (Descargo descargoListOldDescargo : descargoListOld) {
                if (!descargoListNew.contains(descargoListOldDescargo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Descargo " + descargoListOldDescargo + " since its idUsuario field is not nullable.");
                }
            }
            for (Aprobados aprobadosListOldAprobados : aprobadosListOld) {
                if (!aprobadosListNew.contains(aprobadosListOldAprobados)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Aprobados " + aprobadosListOldAprobados + " since its idDa field is not nullable.");
                }
            }
            for (Recepcion recepcionListOldRecepcion : recepcionListOld) {
                if (!recepcionListNew.contains(recepcionListOldRecepcion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Recepcion " + recepcionListOldRecepcion + " since its idUsuario field is not nullable.");
                }
            }
            for (CotizacionProd cotizacionProdListOldCotizacionProd : cotizacionProdListOld) {
                if (!cotizacionProdListNew.contains(cotizacionProdListOldCotizacionProd)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CotizacionProd " + cotizacionProdListOldCotizacionProd + " since its idAo field is not nullable.");
                }
            }
            for (Usuario usuarioListOldUsuario : usuarioListOld) {
                if (!usuarioListNew.contains(usuarioListOldUsuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Usuario " + usuarioListOldUsuario + " since its id1 field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (permisosNew != null) {
                permisosNew = em.getReference(permisosNew.getClass(), permisosNew.getUsuarioId());
                usuario.setPermisos(permisosNew);
            }
            if (id1New != null) {
                id1New = em.getReference(id1New.getClass(), id1New.getId());
                usuario.setId1(id1New);
            }
            List<Descargo> attachedDescargoListNew = new ArrayList<Descargo>();
            for (Descargo descargoListNewDescargoToAttach : descargoListNew) {
                descargoListNewDescargoToAttach = em.getReference(descargoListNewDescargoToAttach.getClass(), descargoListNewDescargoToAttach.getId());
                attachedDescargoListNew.add(descargoListNewDescargoToAttach);
            }
            descargoListNew = attachedDescargoListNew;
            usuario.setDescargoList(descargoListNew);
            List<Aprobados> attachedAprobadosListNew = new ArrayList<Aprobados>();
            for (Aprobados aprobadosListNewAprobadosToAttach : aprobadosListNew) {
                aprobadosListNewAprobadosToAttach = em.getReference(aprobadosListNewAprobadosToAttach.getClass(), aprobadosListNewAprobadosToAttach.getIdAprobado());
                attachedAprobadosListNew.add(aprobadosListNewAprobadosToAttach);
            }
            aprobadosListNew = attachedAprobadosListNew;
            usuario.setAprobadosList(aprobadosListNew);
            List<Recepcion> attachedRecepcionListNew = new ArrayList<Recepcion>();
            for (Recepcion recepcionListNewRecepcionToAttach : recepcionListNew) {
                recepcionListNewRecepcionToAttach = em.getReference(recepcionListNewRecepcionToAttach.getClass(), recepcionListNewRecepcionToAttach.getFechallegada());
                attachedRecepcionListNew.add(recepcionListNewRecepcionToAttach);
            }
            recepcionListNew = attachedRecepcionListNew;
            usuario.setRecepcionList(recepcionListNew);
            List<CotizacionProd> attachedCotizacionProdListNew = new ArrayList<CotizacionProd>();
            for (CotizacionProd cotizacionProdListNewCotizacionProdToAttach : cotizacionProdListNew) {
                cotizacionProdListNewCotizacionProdToAttach = em.getReference(cotizacionProdListNewCotizacionProdToAttach.getClass(), cotizacionProdListNewCotizacionProdToAttach.getId());
                attachedCotizacionProdListNew.add(cotizacionProdListNewCotizacionProdToAttach);
            }
            cotizacionProdListNew = attachedCotizacionProdListNew;
            usuario.setCotizacionProdList(cotizacionProdListNew);
            List<Usuario> attachedUsuarioListNew = new ArrayList<Usuario>();
            for (Usuario usuarioListNewUsuarioToAttach : usuarioListNew) {
                usuarioListNewUsuarioToAttach = em.getReference(usuarioListNewUsuarioToAttach.getClass(), usuarioListNewUsuarioToAttach.getId());
                attachedUsuarioListNew.add(usuarioListNewUsuarioToAttach);
            }
            usuarioListNew = attachedUsuarioListNew;
            usuario.setUsuarioList(usuarioListNew);
            usuario = em.merge(usuario);
            if (permisosNew != null && !permisosNew.equals(permisosOld)) {
                Usuario oldUsuarioOfPermisos = permisosNew.getUsuario();
                if (oldUsuarioOfPermisos != null) {
                    oldUsuarioOfPermisos.setPermisos(null);
                    oldUsuarioOfPermisos = em.merge(oldUsuarioOfPermisos);
                }
                permisosNew.setUsuario(usuario);
                permisosNew = em.merge(permisosNew);
            }
            if (id1Old != null && !id1Old.equals(id1New)) {
                id1Old.getUsuarioList().remove(usuario);
                id1Old = em.merge(id1Old);
            }
            if (id1New != null && !id1New.equals(id1Old)) {
                id1New.getUsuarioList().add(usuario);
                id1New = em.merge(id1New);
            }
            for (Descargo descargoListNewDescargo : descargoListNew) {
                if (!descargoListOld.contains(descargoListNewDescargo)) {
                    Usuario oldIdUsuarioOfDescargoListNewDescargo = descargoListNewDescargo.getIdUsuario();
                    descargoListNewDescargo.setIdUsuario(usuario);
                    descargoListNewDescargo = em.merge(descargoListNewDescargo);
                    if (oldIdUsuarioOfDescargoListNewDescargo != null && !oldIdUsuarioOfDescargoListNewDescargo.equals(usuario)) {
                        oldIdUsuarioOfDescargoListNewDescargo.getDescargoList().remove(descargoListNewDescargo);
                        oldIdUsuarioOfDescargoListNewDescargo = em.merge(oldIdUsuarioOfDescargoListNewDescargo);
                    }
                }
            }
            for (Aprobados aprobadosListNewAprobados : aprobadosListNew) {
                if (!aprobadosListOld.contains(aprobadosListNewAprobados)) {
                    Usuario oldIdDaOfAprobadosListNewAprobados = aprobadosListNewAprobados.getIdDa();
                    aprobadosListNewAprobados.setIdDa(usuario);
                    aprobadosListNewAprobados = em.merge(aprobadosListNewAprobados);
                    if (oldIdDaOfAprobadosListNewAprobados != null && !oldIdDaOfAprobadosListNewAprobados.equals(usuario)) {
                        oldIdDaOfAprobadosListNewAprobados.getAprobadosList().remove(aprobadosListNewAprobados);
                        oldIdDaOfAprobadosListNewAprobados = em.merge(oldIdDaOfAprobadosListNewAprobados);
                    }
                }
            }
            for (Recepcion recepcionListNewRecepcion : recepcionListNew) {
                if (!recepcionListOld.contains(recepcionListNewRecepcion)) {
                    Usuario oldIdUsuarioOfRecepcionListNewRecepcion = recepcionListNewRecepcion.getIdUsuario();
                    recepcionListNewRecepcion.setIdUsuario(usuario);
                    recepcionListNewRecepcion = em.merge(recepcionListNewRecepcion);
                    if (oldIdUsuarioOfRecepcionListNewRecepcion != null && !oldIdUsuarioOfRecepcionListNewRecepcion.equals(usuario)) {
                        oldIdUsuarioOfRecepcionListNewRecepcion.getRecepcionList().remove(recepcionListNewRecepcion);
                        oldIdUsuarioOfRecepcionListNewRecepcion = em.merge(oldIdUsuarioOfRecepcionListNewRecepcion);
                    }
                }
            }
            for (CotizacionProd cotizacionProdListNewCotizacionProd : cotizacionProdListNew) {
                if (!cotizacionProdListOld.contains(cotizacionProdListNewCotizacionProd)) {
                    Usuario oldIdAoOfCotizacionProdListNewCotizacionProd = cotizacionProdListNewCotizacionProd.getIdAo();
                    cotizacionProdListNewCotizacionProd.setIdAo(usuario);
                    cotizacionProdListNewCotizacionProd = em.merge(cotizacionProdListNewCotizacionProd);
                    if (oldIdAoOfCotizacionProdListNewCotizacionProd != null && !oldIdAoOfCotizacionProdListNewCotizacionProd.equals(usuario)) {
                        oldIdAoOfCotizacionProdListNewCotizacionProd.getCotizacionProdList().remove(cotizacionProdListNewCotizacionProd);
                        oldIdAoOfCotizacionProdListNewCotizacionProd = em.merge(oldIdAoOfCotizacionProdListNewCotizacionProd);
                    }
                }
            }
            for (Usuario usuarioListNewUsuario : usuarioListNew) {
                if (!usuarioListOld.contains(usuarioListNewUsuario)) {
                    Usuario oldId1OfUsuarioListNewUsuario = usuarioListNewUsuario.getId1();
                    usuarioListNewUsuario.setId1(usuario);
                    usuarioListNewUsuario = em.merge(usuarioListNewUsuario);
                    if (oldId1OfUsuarioListNewUsuario != null && !oldId1OfUsuarioListNewUsuario.equals(usuario)) {
                        oldId1OfUsuarioListNewUsuario.getUsuarioList().remove(usuarioListNewUsuario);
                        oldId1OfUsuarioListNewUsuario = em.merge(oldId1OfUsuarioListNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuario.getId();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Permisos permisosOrphanCheck = usuario.getPermisos();
            if (permisosOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Permisos " + permisosOrphanCheck + " in its permisos field has a non-nullable usuario field.");
            }
            List<Descargo> descargoListOrphanCheck = usuario.getDescargoList();
            for (Descargo descargoListOrphanCheckDescargo : descargoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Descargo " + descargoListOrphanCheckDescargo + " in its descargoList field has a non-nullable idUsuario field.");
            }
            List<Aprobados> aprobadosListOrphanCheck = usuario.getAprobadosList();
            for (Aprobados aprobadosListOrphanCheckAprobados : aprobadosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Aprobados " + aprobadosListOrphanCheckAprobados + " in its aprobadosList field has a non-nullable idDa field.");
            }
            List<Recepcion> recepcionListOrphanCheck = usuario.getRecepcionList();
            for (Recepcion recepcionListOrphanCheckRecepcion : recepcionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Recepcion " + recepcionListOrphanCheckRecepcion + " in its recepcionList field has a non-nullable idUsuario field.");
            }
            List<CotizacionProd> cotizacionProdListOrphanCheck = usuario.getCotizacionProdList();
            for (CotizacionProd cotizacionProdListOrphanCheckCotizacionProd : cotizacionProdListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the CotizacionProd " + cotizacionProdListOrphanCheckCotizacionProd + " in its cotizacionProdList field has a non-nullable idAo field.");
            }
            List<Usuario> usuarioListOrphanCheck = usuario.getUsuarioList();
            for (Usuario usuarioListOrphanCheckUsuario : usuarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Usuario " + usuarioListOrphanCheckUsuario + " in its usuarioList field has a non-nullable id1 field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario id1 = usuario.getId1();
            if (id1 != null) {
                id1.getUsuarioList().remove(usuario);
                id1 = em.merge(id1);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
