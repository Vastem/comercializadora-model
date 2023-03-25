/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexiones;

import interfaces.IConexionBD;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 * @author tonyd
 */
public class ConexionBD implements IConexionBD {

    private static ConexionBD conexion;
    private EntityManager manejadorEntidades;

    public static ConexionBD getInstance() {
        if (conexion == null) {
            conexion = new ConexionBD();
        }
        return conexion;
    }

    @Override
    public EntityManager crearConexion() throws IllegalStateException {
        if (manejadorEntidades == null) {
            //Obtiene acceso alemFactory a partir de la persistence unit utilizada
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("camaronbd");
            // Creamos una em(bd) para realizar operaciones con la bd
            manejadorEntidades = emFactory.createEntityManager();
        }
        return manejadorEntidades;
    }

}
