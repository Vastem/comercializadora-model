/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import entidades.Cliente;
import entidades.Pedido;
import entidades.Producto;
import interfaces.IConexionBD;
import interfaces.IModeloCliente;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/**
 *
 * @author Vastem
 */
public class ModeloCliente implements IModeloCliente {

    private final IConexionBD conexionBD;

    public ModeloCliente(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Cliente consultar(Integer idCliente) {
        EntityManager em = (EntityManager) this.conexionBD.crearConexion();
        em.clear();
        try {
            Cliente c = em.find(Cliente.class, idCliente);
            System.out.println(c);
            return c;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo consultar el cliente" + idCliente);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Cliente> consultar() {
        EntityManager em = (EntityManager) this.conexionBD.crearConexion();
        em.clear();
        try {
            Query query = em.createQuery("SELECT e FROM Cliente e");
            List<Cliente> clientes = new ArrayList();
            return clientes = query.getResultList();
        } catch (IllegalStateException e) {
            System.err.println("No se pudieron consultar los clientes");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Cliente eliminar(Cliente cliente) {
        EntityManager em = this.conexionBD.crearConexion();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("DELETE FROM Cliente e WHERE e.id = :idCliente");
            query.setParameter("idCliente", cliente.getId()).executeUpdate();
            em.getTransaction().commit();
            em.clear();
            return cliente;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo eliminar el cliente" + cliente.getId());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Cliente registrar(Cliente cliente) {
        EntityManager em = this.conexionBD.crearConexion();
        try {
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
            em.clear();
            return cliente;
        } catch (IllegalStateException e) {
            System.err.println("No se pudo agregar el cliente" + cliente.getId());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Cliente actualizar(Cliente cliente) {
        EntityManager em = this.conexionBD.crearConexion();
        Cliente clienteActualizar = this.consultar(cliente.getId());

        if (clienteActualizar != null) {
            try {
                em.getTransaction().begin();
                em.merge(cliente);
                em.getTransaction().commit();
                em.clear();
                return cliente;
            } catch (IllegalStateException e) {
                System.err.println("No se pudo actualizar el cliente" + cliente.getId());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
