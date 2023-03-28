/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import entidades.Pedido;
import entidades.PedidoProducto;
import entidades.Producto;
import entidades.Venta;
import interfaces.IConexionBD;
import interfaces.IModeloPedido;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.Session;

/**
 *
 * @author Vastem
 */
public class ModeloPedido implements IModeloPedido {

    private final IConexionBD conexionBD;

    public ModeloPedido(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Pedido consultar(Integer idPedido) {

        EntityManager em = this.conexionBD.crearConexion();
        try {
            Pedido p = em.find(Pedido.class, idPedido);
            return p;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo consultar el pedido" + idPedido);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Pedido> consultar() {
        EntityManager em = this.conexionBD.crearConexion();
        try {
            Query query = em.createQuery("SELECT e FROM Pedido e");
            List<Pedido> pedido = new ArrayList();
            return pedido = query.getResultList();
        } catch (IllegalStateException e) {
            System.err.println("No se pudieron consultar los pedidos");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Pedido eliminar(Pedido pedido) {
        EntityManager em = this.conexionBD.crearConexion();
        try {
            em.getTransaction().begin();

            Query queryProductos = em.createQuery("SELECT e FROM PedidoProducto e");
            List<PedidoProducto> pProds = queryProductos.getResultList();
                
            pProds.forEach(pp -> {
                restarCantidadProducto(pp.getProducto(),pp.getCantidad());
            });
            
            Query queryPedidoProducto = em.createQuery("DELETE FROM PedidoProducto e WHERE e.pedido.id = :idPedido");
            queryPedidoProducto.setParameter("idPedido", pedido.getId()).executeUpdate();

            Query query = em.createQuery("DELETE FROM Pedido e WHERE e.id = :idPedido");
            query.setParameter("idPedido", pedido.getId()).executeUpdate();

            em.getTransaction().commit();
            return pedido;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo eliminar el pedido" + pedido.getId());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Pedido registrar(Pedido pedido) {
        EntityManager em = this.conexionBD.crearConexion();
        Session session = em.unwrap(Session.class);

        List<PedidoProducto> pedProds = pedido.getPedidosProducto();
        pedido.setPedidosProducto(null);
        try {
            session.getTransaction().begin();
            Integer id = (Integer) session.save(pedido);
            session.getTransaction().commit();

            Pedido p = this.consultar(id);

            //Introducir pedidosProducto a la base de datos
            session.getTransaction().begin();
            for (int i = 0; i < pedProds.size(); i++) {
                pedProds.get(i).setPedido(p);
                
                Producto prod = pedProds.get(i).getProducto();
                sumarCantidadProducto(prod, pedProds.get(i).getCantidad());
                
                session.save(pedProds.get(i));
            }
            session.getTransaction().commit();

            p = this.consultar(id);
            System.out.println(p);
            em.clear();
            return p;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo agregar el pedido" + pedido.getId());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Pedido actualizar(Pedido pedido) {
        EntityManager em = this.conexionBD.crearConexion();
        Pedido pedidoActualizar = this.consultar(pedido.getId());

        if (pedidoActualizar != null) {
            try {
                
                em.getTransaction().begin();
                Query query;
                Query queryProductos = em.createQuery("SELECT e FROM PedidoProducto e");
                List<PedidoProducto> pProds = queryProductos.getResultList();
                
                pProds.forEach(pp -> {
                    restarCantidadProducto(pp.getProducto(),pp.getCantidad());
                });
                
                //Eliminar pedidosProductos antiguos
                query = em.createNativeQuery("delete from pedidosproductos where id_pedido = ?");
                query.setParameter(1, pedidoActualizar.getId());
                query.executeUpdate();
                
                em.getTransaction().commit();

//                //Eliminar ventas antiguas ###IMPLEMENTAR
//                em.getTransaction().begin();
//                query = em.createNativeQuery("delete from etiquetas where id_publicacion = ?");
//                query.setParameter(1, publi.getId());
//                query.executeUpdate();
//                em.getTransaction().commit();

                //Editar pedido
                em.getTransaction().begin();
                List<PedidoProducto> pedProds = pedido.getPedidosProducto();
                //List<Venta> etiquetas = pedido.getVentas();
                pedidoActualizar.setPedidosProducto(null);
//                pedidoActualizar.setVentas(null);
                pedidoActualizar.setCliente(pedido.getCliente());
                pedidoActualizar.setFecha(pedido.getFecha());
                pedidoActualizar.setLugarEntrega(pedido.getLugarEntrega());
                pedidoActualizar.setObservaciones(pedido.getObservaciones());
                pedidoActualizar.setPrecioTotal(pedido.getPrecioTotal());

                em.persist(pedidoActualizar);
                em.getTransaction().commit();

                System.out.println("Tamano:: " + pedProds.size());
                
                //Crear pedidosProductos
                em.getTransaction().begin();
                pedProds.forEach(pp -> {
                    pp.setPedido(pedidoActualizar);
                    em.persist(pp);
                    sumarCantidadProducto(pp.getProducto(),pp.getCantidad() );
                });
                em.getTransaction().commit();

//                //Crear Ventas ###IMPLEMENTAR
//                em.getTransaction().begin();
//                for (int i = 0; i < etiquetas.size(); i++) {
//                    query = em.createQuery(
//                            "SELECT u "
//                            + "FROM Usuario u "
//                            + "WHERE u.usuario = :usuario");
//                    query.setParameter("usuario", etiquetas.get(i).getEtiquetado().getUsuario());
//                    Usuario user = (Usuario) query.getSingleResult();
//
//                    if (user != null) {
//                        etiquetas.get(i).setPublicacion(publi);
//                        etiquetas.get(i).setEtiquetado(user);
//                        em.persist(etiquetas.get(i));
//                    }
//                }
//                em.getTransaction().commit();
                  em.clear();
                return this.consultar(pedidoActualizar.getId());
            } catch (IllegalStateException e) {
                System.err.println("No se pudo actualizar el pedido" + pedido.getId());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private void sumarCantidadProducto(Producto p, int cantidad){
        EntityManager em = this.conexionBD.crearConexion();
        Producto productoActualizar = em.find(Producto.class, p.getId());
        
        if(productoActualizar != null){
            try {
                em.getTransaction().begin();
                productoActualizar.setCantidadApartada(productoActualizar.getCantidadApartada() + cantidad);
                em.merge(productoActualizar);
                em.getTransaction().commit();
                em.clear();
            } catch (IllegalStateException e) {
                System.err.println("No se pudo actualizar el producto" + p.getId());
                e.printStackTrace();
            }
        }
        
    }

    private void restarCantidadProducto(Producto p, int cantidad){
        EntityManager em = this.conexionBD.crearConexion();
        Producto productoActualizar = em.find(Producto.class, p.getId());
        
        if(productoActualizar != null){
            try {
                em.getTransaction().begin();
                productoActualizar.setCantidadApartada(productoActualizar.getCantidadApartada()-cantidad);
                em.merge(productoActualizar);
                em.getTransaction().commit();
                em.clear();
            } catch (IllegalStateException e) {
                System.err.println("No se pudo actualizar el producto" + p.getId());
                e.printStackTrace();
            }
        }
    }


}
