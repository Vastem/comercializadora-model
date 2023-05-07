/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import entidades.Venta;
import java.util.List;

/**
 *
 * @author Vastem
 */
public interface IModeloVenta {
    public Venta consultar(Integer idVenta);
    public List<Venta> consultar();
    public Venta eliminar(Venta venta);
    public Venta registrar(Venta venta);
    public Venta actualizar(Venta venta);
}
