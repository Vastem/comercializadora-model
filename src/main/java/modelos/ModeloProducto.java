/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import entidades.Cliente;
import entidades.Pedido;
import entidades.Producto;
import interfaces.IConexionBD;
import interfaces.IModeloProducto;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/**
 *
 * @author Vastem
 */
public class ModeloProducto implements IModeloProducto{
    private final IConexionBD conexionBD;

    public ModeloProducto(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }
    
    @Override
    public Producto consultar(Integer idProducto) {
        EntityManager em = (EntityManager) this.conexionBD.crearConexion();
        try {
            Producto p = em.find(Producto.class, idProducto);
            return p;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo consultar el producto" + idProducto);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Producto> consultar() {
        EntityManager em = (EntityManager) this.conexionBD.crearConexion();
        try {
            Query query = em.createQuery("SELECT e FROM Producto e");
            List<Producto> prod = query.getResultList();
            return prod;
        } catch (IllegalStateException e) {
            System.err.println("No se pudieron consultar los productos");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Producto eliminar(Producto producto) {
        EntityManager em = this.conexionBD.crearConexion();
        try {
            em.getTransaction().begin();  
            Query query = em.createQuery("DELETE FROM Producto e WHERE e.id = :idProducto");
            query.setParameter("idProducto", producto.getId()).executeUpdate();
            em.getTransaction().commit();
            em.clear();
            return producto;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo eliminar el producto" + producto.getId());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Producto registrar(Producto producto) {
        EntityManager em = this.conexionBD.crearConexion();
        try {
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
            em.clear();
            return producto;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo agregar el producto" + producto.getId());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Producto actualizar(Producto producto) {
        EntityManager em = this.conexionBD.crearConexion();
        Producto clienteActualizar = this.consultar(producto.getId());
        
        if(clienteActualizar != null){
            try {
                em.getTransaction().begin();
                em.merge(producto);
                em.getTransaction().commit();
                em.clear();
                return producto;
            } catch (IllegalStateException e) {
                System.err.println("No se pudo actualizar el producto" + producto.getId());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
}
