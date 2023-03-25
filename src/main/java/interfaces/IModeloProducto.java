/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import entidades.Producto;
import java.util.List;

/**
 *
 * @author Usuario
 */
public interface IModeloProducto {
    public Producto consultar(Integer idProducto);
    public List<Producto> consultar();
    public Producto eliminar(Producto producto);
    public Producto registrar(Producto producto);
    public Producto actualizar(Producto producto);
}
