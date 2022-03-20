package dhcp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

//Permite generar objetos InetAddress sin caer en la repeticion de código.
public class creadorDeInet {
    public InetAddress generarINET(String IP){
        InetAddress ipNueva = null;
        try {
            ipNueva = InetAddress.getByName(IP);
        } catch (UnknownHostException ex) {
            Logger.getLogger(DHCPServidorUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ipNueva;
    }
}
