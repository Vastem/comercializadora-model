/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package test;

import entidades.Cliente;
import entidades.Pedido;
import entidades.PedidoProducto;
import entidades.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Cliente cliente = new Cliente("ea", "dp");
        Pedido p = new Pedido();
        p.setPrecioTotal(1234);
        p.setFecha(GregorianCalendar.getInstance());
        p.setLugarEntrega("Almacen 3");
        

        EntityManager em;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("camaronbd");

        
        em = emf.createEntityManager();
        em.getTransaction().begin();
        
        //em.persist(cliente);
        
        Cliente c = em.find(Cliente.class, 1);
        p.setCliente(c);
        
        Producto producto = new Producto();
        producto.setCantidad(100);
        producto.setNombre("Camaron");
        producto.setPrecio(240);
        
        //em.persist(producto);
        
        Producto prod = em.find(Producto.class,1);
        
        PedidoProducto pp = new PedidoProducto();
        pp.setCantidad(20);
        pp.setPedido(p);
        pp.setProducto(prod);
        
        
        List<PedidoProducto> peds = new ArrayList() ;
        peds.add(pp);
        p.setPedidosProducto(peds);
        
        em.persist(p);
        em.getTransaction().commit();
        
        
        em.close();
    }
    
}
