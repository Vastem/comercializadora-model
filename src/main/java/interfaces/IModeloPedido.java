/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import entidades.Pedido;
import java.util.List;

/**
 *
 * @author tonyd
 */
public interface IModeloPedido {
    public Pedido consultar(Integer idPedido);
    public List<Pedido> consultar();
    public Pedido eliminar(Pedido pedido);
    public Pedido registrar(Pedido pedido);
    public Pedido actualizar(Pedido pedido);
}
