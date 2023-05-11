/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import entidades.Cliente;
import entidades.Pedido;
import entidades.PedidoProducto;
import entidades.Venta;
import interfaces.IConexionBD;
import interfaces.IModeloVenta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author Vastem
 */
public class ModeoVenta implements IModeloVenta{
    private final IConexionBD conexionBD;

    public ModeoVenta(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }
    
    
    @Override
    public Venta consultar(Integer idVenta) {
        EntityManager em = this.conexionBD.crearConexion();
        try{
            em.getTransaction().begin();
            Venta v = em.find(Venta.class, idVenta);
            em.getTransaction().commit();
            return v;
        }
        catch(IllegalStateException e){
            System.err.println("No se pudo consultar la venta " + idVenta);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Venta> consultar() {
        EntityManager em = this.conexionBD.crearConexion();
        em.clear();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT e FROM Venta e");
            List<Venta> ventas = query.getResultList();
            em.getTransaction().commit();
            return ventas;
        } catch (IllegalStateException e) {
            System.err.println("No se pudieron consultar las ventas");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Venta eliminar(Venta venta) {
        EntityManager em = this.conexionBD.crearConexion();
        try {
            em.getTransaction().begin();  
            Query query = em.createQuery("DELETE FROM Venta e WHERE e.id = :idVenta");
            query.setParameter("idVenta", venta.getId()).executeUpdate();
            em.getTransaction().commit();
            em.clear();
            return venta;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo eliminar la venta " + venta.getId());
            e.printStackTrace();
            return null;
        }
        
    }

    @Override
    public Venta registrar(Venta venta) {
        EntityManager em = this.conexionBD.crearConexion();
        try {
            em.getTransaction().begin();
            em.persist(venta);
            em.getTransaction().commit();
            
            //Actualizar saldo del pedido
            em.getTransaction().begin();
            Pedido p = em.find(Pedido.class, venta.getIdPedido().getId()); 
            p.setSaldo(p.getSaldo() - venta.getPrecioVenta());
                //si el pedido ya esta pagado
            if(p.getSaldo() == 0){
                p.setPagado(true);
            }
            em.merge(p);
            em.getTransaction().commit();
            
            //Actualizar adedudo cliente
            em.getTransaction().begin();
            Cliente c = em.find(Cliente.class, venta.getIdPedido().getCliente().getId());
            c.setAdeudo(c.getAdeudo() - venta.getPrecioVenta());
            em.merge(c);
            em.getTransaction().commit();
            
            em.clear();
            return venta;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo agregar la venta" + venta.getId());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Venta actualizar(Venta venta) {
        EntityManager em = this.conexionBD.crearConexion();
        Venta ventaActualizar = this.consultar(venta.getId());
        
        if(ventaActualizar != null){
            try {
                em.getTransaction().begin();
                em.merge(venta);
                em.getTransaction().commit();
                em.clear();
                return venta;
            } catch (IllegalStateException e) {
                System.err.println("No se pudo actualizar la venta " + venta.getId());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
}
