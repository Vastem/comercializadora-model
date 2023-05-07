/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import conexiones.ConexionBD;
import entidades.Cliente;
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
            em.getTransaction().begin();
            Pedido p = em.find(Pedido.class, idPedido);
            em.getTransaction().commit();
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
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT e FROM Pedido e");
            List<Pedido> pedido = query.getResultList();
            em.getTransaction().commit();
            return pedido ;
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
            Query queryProductos = em.createQuery("SELECT e FROM PedidoProducto e WHERE e.pedido.id = :idPedido");
            queryProductos.setParameter("idPedido", pedido.getId()) ;
            List<PedidoProducto> pProds = queryProductos.getResultList();
            em.getTransaction().commit();
                
            pProds.forEach(pp -> {
                restarCantidadProducto(pp.getProducto(),pp.getCantidad());
            });
            
            
            em.getTransaction().begin();
            Query queryPedidoProducto = em.createQuery("DELETE FROM PedidoProducto e WHERE e.pedido.id = :idPedido");
            queryPedidoProducto.setParameter("idPedido", pedido.getId()).executeUpdate();
            em.getTransaction().commit();

            em.getTransaction().begin();
            Query query = em.createQuery("DELETE FROM Pedido e WHERE e.id = :idPedido");
            query.setParameter("idPedido", pedido.getId()).executeUpdate();
            em.getTransaction().commit();
            
            em.clear();
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
                session.save(pedProds.get(i));
            }
            session.getTransaction().commit();
            
            pedProds.forEach(pp -> {
                    System.out.println("cantidad sumar: "+pp.getCantidad());
                    sumarCantidadProducto(pp.getProducto(),pp.getCantidad() );
            });
            
            //Actualizar adeudo cliente
            session.getTransaction().begin();
            Cliente c = pedido.getCliente();
            c.setAdeudo(c.getAdeudo() + pedido.getSaldo());
            session.merge(c);
            session.getTransaction().commit();
            
            em.clear();
            return this.consultar(id);
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
                Query queryProductos = em.createQuery("SELECT e FROM PedidoProducto e WHERE e.pedido.id = :idPedido");
                queryProductos.setParameter("idPedido", pedido.getId()) ;
                List<PedidoProducto> pProds = queryProductos.getResultList();
                em.getTransaction().commit();
                
                //Eliminar pedidosProductos antiguos, ajustar cantidad de productos apartados
                pProds.forEach(pp -> {
                    System.out.println("cantidad restar: "+pp.getCantidad());  
                    restarCantidadProducto(pp.getProducto(),pp.getCantidad());
                });
                em.getTransaction().begin();
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

                //Editar pedido y persistir su actualizacion
                em.getTransaction().begin();
                List<PedidoProducto> pedProds = pedido.getPedidosProducto();
//                List<Venta> etiquetas = pedido.getVentas();
                pedidoActualizar.setPedidosProducto(null);
//                pedidoActualizar.setVentas(null);
                pedidoActualizar.setCliente(pedido.getCliente());
                pedidoActualizar.setFecha(pedido.getFecha());
                pedidoActualizar.setLugarEntrega(pedido.getLugarEntrega());
                pedidoActualizar.setObservaciones(pedido.getObservaciones());
                pedidoActualizar.setPrecioTotal(pedido.getPrecioTotal());
                em.persist(pedidoActualizar);
                em.getTransaction().commit();
                
                //Crear pedidosProductos, ajustar cantidad de productos apartados
                em.getTransaction().begin();
                pedProds.forEach(pp -> {
                    pp.setPedido(pedidoActualizar);
                    em.persist(pp);
                });
                em.getTransaction().commit(); 
                pedProds.forEach(pp -> {
                    System.out.println("cantidad sumar: "+pp.getCantidad());
                    sumarCantidadProducto(pp.getProducto(),pp.getCantidad() );
                });
                
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
                System.err.println("No se pudo actualizar el pedido " + pedido.getId() + e);
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private void sumarCantidadProducto(Producto p, int cantidad){
        EntityManager em = this.conexionBD.crearConexion();
        em.getTransaction().begin();
        Producto productoActualizar = em.find(Producto.class, p.getId());
        em.getTransaction().commit();
        
        if(productoActualizar != null){
            try {
                em.getTransaction().begin();
                productoActualizar.setCantidadApartada(productoActualizar.getCantidadApartada() + cantidad);
                em.merge(productoActualizar);
                em.getTransaction().commit();
            } catch (IllegalStateException e) {
                System.err.println("No se pudo actualizar la cantidad apartada del producto " + p.getId());
                e.printStackTrace();
            }
        }
    }

    private void restarCantidadProducto(Producto p, int cantidad){
        EntityManager em = this.conexionBD.crearConexion();
        em.getTransaction().begin();
        Producto productoActualizar = em.find(Producto.class, p.getId());
        em.getTransaction().commit();
        
        if(productoActualizar != null){
            try {
                em.getTransaction().begin();
                productoActualizar.setCantidadApartada(productoActualizar.getCantidadApartada() - cantidad);
                em.merge(productoActualizar);
                em.getTransaction().commit();
            } catch (IllegalStateException e) {
                System.err.println("No se pudo actualizar la cantidad apartada del producto " + p.getId());
                e.printStackTrace();
            }
        }
    }


}
