/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import entidades.Pedido;
import entidades.PedidoProducto;
import entidades.Producto;
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
            return pedido =query.getResultList();
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
            Integer id = (Integer)session.save(pedido);
            session.getTransaction().commit();
            
            Pedido p = this.consultar(id);
            
            //Introducir pedidosProducto a la base de datos
            session.getTransaction().begin();
            for(int i = 0; i < pedProds.size(); i++){
                pedProds.get(i).setPedido(p);
                Producto prod = pedProds.get(i).getProducto();
                prod.setCantidadApartada(pedProds.get(i).getCantidad());
                session.update(prod);
                session.save(pedProds.get(i));
            }
            session.getTransaction().commit();
            
            session.getTransaction().begin();
            for(int i = 0; i < pedProds.size(); i++){
                Producto prod = pedProds.get(i).getProducto();
                prod.setCantidadApartada(pedProds.get(i).getCantidad());
                session.update(prod);
            }
            session.getTransaction().commit();
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
        
        if(pedidoActualizar != null){
            try {
                em.getTransaction().begin();
                em.merge(pedido);
                em.getTransaction().commit();
                return pedido;
            } catch (IllegalStateException e) {
                System.err.println("No se pudo actualizar el pedido" + pedido.getId());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
