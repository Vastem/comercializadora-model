/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import jakarta.persistence.EntityManager;

/**
 *
 * @author tonyd
 */
public interface IConexionBD {
    public EntityManager crearConexion() throws IllegalStateException;
}
