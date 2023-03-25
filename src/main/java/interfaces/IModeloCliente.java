/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import entidades.Cliente;
import java.util.List;

/**
 *
 * @author Usuario
 */
public interface IModeloCliente {
    public Cliente consultar(Integer idCliente);
    public List<Cliente> consultar();
    public Cliente eliminar(Cliente cliente);
    public Cliente registrar(Cliente cliente);
    public Cliente actualizar(Cliente cliente);
}
