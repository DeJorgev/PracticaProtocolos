package dhcp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DHCPServidorUDP {       
    public static void main(String[] args) {
        String ipServidor = "192.168.1.50";
        InetAddress inetServidor = new creadorDeInet().generarINET(ipServidor);        
        DatagramSocket datagramSocketServidor = getDatagramSocket(inetServidor);

        if (datagramSocketServidor != null) {
            boolean fin = false;
            while ( fin != true) {                
                DatagramPacket paqueteDiscover = recibirMensajeDHCP(datagramSocketServidor);
                                                
                //Solo se admiten mensajes del puerto DHCP cliente de tipo discover
                if (paqueteDiscover.getPort() == 68 && extraerTipoDeMensajeDHCP(paqueteDiscover.getData()) == 1) {
                    byte [] datosDiscover = paqueteDiscover.getData();
                    byte [] transactionIDCliente = extraerDeCabecera(datosDiscover, 4, 4, 8);
                    byte [] MACCLiente = extraerDeCabecera(datosDiscover, 16, 28, 44);
                    enviarMensajeDHCP(datagramSocketServidor, transactionIDCliente, MACCLiente, "OFF");
                    
                    boolean RequestRecibido = false;                            
                    while (RequestRecibido != true) {
                        DatagramPacket paqueteRequest = recibirMensajeDHCP(datagramSocketServidor);
                                               
                       //Solo se admiten mensajes del puerto DHCP cliente de tipo Request y con la misma TransactionID 
                       if (paqueteRequest.getPort() == 68 && extraerTipoDeMensajeDHCP(paqueteRequest.getData()) == 3)  {
                            byte[] datosRequest = paqueteRequest.getData();  
                            
                            if(compararArraysDeBytes(transactionIDCliente, extraerDeCabecera(datosRequest, 4, 4, 8))){
                                enviarMensajeDHCP(datagramSocketServidor, transactionIDCliente, MACCLiente, "ACK");
                                RequestRecibido = true; fin = true;
                            }   
                        }
                    }
                }
            }
            datagramSocketServidor.close();
        }        
    }

//Métodos de control de canales de comunicacion.
    //Crea un DatagramSocket con puerto servidor DHCP (67)
    private static DatagramSocket getDatagramSocket(InetAddress inetServidor) {
        DatagramSocket dSocket = null;

        try {
            dSocket = new DatagramSocket(67,inetServidor);
        } catch (SocketException ex) {
            Logger.getLogger(DHCPServidorUDP.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dSocket;
    }

//Métodos de comunicación.
    //Recibe un mensaje DHCP del cliente de hasta 576 bytes
    private static DatagramPacket recibirMensajeDHCP(DatagramSocket datagramSocketServidor) {
        byte[] datosMensaje = new byte[576];

        DatagramPacket paquete = new DatagramPacket(datosMensaje, datosMensaje.length);
        try {
            datagramSocketServidor.receive(paquete);
        } catch (IOException ex) {
            Logger.getLogger(DHCPServidorUDP.class.getName()).log(Level.SEVERE, null, ex);
        }

        return paquete;
    }
    
    //Envia un mensaje del tipo seleccionado al cliente de hasta 576 bytes.
    private static void  enviarMensajeDHCP(DatagramSocket datagramSocketServidor, byte [] transactionID, byte [] MAC ,String tipoMensaje){
        GeneradorMensajesDHCP generadorDHCP = new GeneradorMensajesDHCP();
        byte [] mensajeAEnviar = new byte[576];
        
        //Tipo OFFER
        if (tipoMensaje.equals("OFF"))
            mensajeAEnviar = generadorDHCP.generarMensajeDHCP(transactionID, MAC,tipoMensaje);

        //Tipo ACK
        if (tipoMensaje.equals("ACK"))        
            mensajeAEnviar = generadorDHCP.generarMensajeDHCP(transactionID, MAC,tipoMensaje);
        
        DatagramPacket paqueteOffer = new DatagramPacket(mensajeAEnviar, mensajeAEnviar.length,new creadorDeInet().generarINET("255.255.255.255"),68); 
        
        try {
            datagramSocketServidor.send(paqueteOffer);
        } catch (IOException ex) {Logger.getLogger(DHCPServidorUDP.class.getName()).log(Level.SEVERE, null, ex);}
    }

//Métodos de tratado de paquete de datos.
    private static byte [] extraerDeCabecera(byte [] datosPaquete, int tamanioDato,int posicionInicial, int posicionFinal){
        byte [] datosPaqueteExtraidos = new byte[tamanioDato];
        
        for (int i = posicionInicial, k = 0; i < posicionFinal ; i++,k++) {
            datosPaqueteExtraidos[k] = datosPaquete[i];
        }
        
        return datosPaqueteExtraidos;
    }
        
    private static int extraerTipoDeMensajeDHCP(byte [] datosPaquete){
        int tipoDeMensaje = 0;
        
        for (int i = 236; i < datosPaquete.length; ++i)
            if (datosPaquete[i] == 53 && datosPaquete[i+1] == 1) 
                tipoDeMensaje = datosPaquete[i+2];
      
            
        
        return tipoDeMensaje;
    }
    
//Varios
    //Compara dos arrays de bytes de la misma longitud. Usado para comparar transaction ID
    private static boolean  compararArraysDeBytes(byte [] array1 ,byte [] array2){
        boolean iguales = true;
        boolean diferentes = false;
        
        for (int i = 0; i < array1.length && !diferentes; i++) {
            if(array1[i] != array2[i]){
                iguales = false;
                diferentes = true;
            }
        }
        
        return iguales;
    }    
}
