/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import EstructurasAux.cotizaciones;
import EstructurasAux.fdc_001;
import EstructurasAux.itemsOrdenCompra;
import EstructurasAux.itemsfdc_001;
import EstructurasAux.proveedor;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Diego
 */
public class NewClass {
    public static void main(String args[]) throws RemoteException
    {
       Usuario u = new Usuario();
       ArrayList<itemsOrdenCompra> l = new ArrayList<>();
       l.add(new itemsOrdenCompra("j", "j", "j", 10, "j", 10, "j", BigDecimal.ZERO));
       l.add(new itemsOrdenCompra("j", "j", "j", 10, "j", 10, "j", BigDecimal.ZERO));
       l.add(new itemsOrdenCompra("j", "j", "j", 10, "j", 10, "j", BigDecimal.ZERO));
       l.add(new itemsOrdenCompra("j", "j", "j", 10, "j", 10, "j", BigDecimal.ZERO));
       l.add(new itemsOrdenCompra("j", "j", "j", 10, "j", 10, "j", BigDecimal.ZERO));
       l.add(new itemsOrdenCompra("j", "j", "j", 10, "j", 10, "j", BigDecimal.ZERO));
       l.add(new itemsOrdenCompra("j", "j", "j", 10, "j", 10, "j", BigDecimal.ZERO));
       l.add(new itemsOrdenCompra("j", "j", "j", 10, "j", 10, "j", BigDecimal.ZERO));
       u.npdf_002("", new proveedor("oscar", "nit"), l, 0, "String", "600");
    }
}
