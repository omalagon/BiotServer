/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package exampleserver;

import Logica.Usuario;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class RMIServer {
 /*
    public static void main(String [] args)throws RemoteException{
        try {
            Registry reg = LocateRegistry.createRegistry(222);
            reg.bind("Test", new Usuario());
            System.out.println("Started");
        }
        catch (AlreadyBoundException ex) {
            Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessException ex) {
            Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    */
    public static void main(String [] args)throws RemoteException{
        try {
            Registry reg = LocateRegistry.createRegistry(222);
            reg.bind("Test", new Usuario());
            System.out.println("Started");
        }
        catch (AlreadyBoundException ex) {
            Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessException ex) {
            Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
