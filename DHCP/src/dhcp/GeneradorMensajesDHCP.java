package dhcp;

import java.nio.ByteBuffer;

public class GeneradorMensajesDHCP {
    //Variables de campo
    //Cabecera
    private final byte op = 2; //Indica que es un servidor

    private final byte htype = 1;/**********/
    private final byte hlen = 6; /*hardware*/
    private final byte hops = 0; /*********/
    
    private byte[] xid = new byte[4]; //Transaction ID, recuperada del cliente en metodo del Main.
    private byte[] secs = new byte[2]; //vacia
    private byte[] flags = new byte[2]; //Tipo de transmision broadcast(1)/unicast(0). Solo el primer byte
    
    private byte[] ciaddr = new byte[4]; //direccion IP del cliente. vacia
    private byte[] yiaddr = new creadorDeInet().generarINET("192.168.1.120").getAddress(); 
    private byte[] siaddr = new byte[4]; //direccion IP del siguiente servidor dhcp. vacia
    private byte[] giaddr = new byte[4]; //direccion IP del relayAgent. vacia

    private byte[] chaddr = new byte[16]; //MAC del cliente, recuperada en metodo del Main.
    
    private byte[] sname = new byte[64]; //Server host name. vacia
    private byte[] file = new byte[128]; //Boot file name. vacia

    //Opciones
    private  byte [] magicCookie = {99,(byte)130,83,99}; //Magic Cookie, siempre es 99 130 83 99, preludio de las opciones.
    private  byte [] tipoMensaje = new byte[3]; //Tipo de Mensaje DHCP. Generada en metodos
    private  byte [] mascara = compiladorOpciones(6, 1, 4, -1, "255.255.255.0");
    private  byte [] servidorDNS = compiladorOpciones(6, 6, 4, -1, "8.8.8.8");
    private  byte [] gateway = compiladorOpciones(6, 3, 4, -1, "192.168.1.1");
    private  byte [] tiempoCesion = compiladorOpciones(6, 51, 4, 60, "");
    private  byte [] tiempoRenovacion = compiladorOpciones(6, 58, 4, 30, "");
    private  byte [] IPServidor = compiladorOpciones(6, 54, 4, -1, "192.168.1.50");
    private  byte end = (byte) 255;  

//Metodos para generar los mensajes
    //completos
    public byte [] generarMensajeDHCP(byte [] idNuevaTransaccion, byte [] MACCliente , String tipoMensajeDHCP){
        ByteBuffer DHCPOFFERBuffer = ByteBuffer.allocate(576);
        byte [] tipoEnvio = new byte[2];
        
        if (tipoMensajeDHCP.equals("OFF")) {
            byte [] envioBroadCast = {0,1}; tipoEnvio = envioBroadCast;
            byte[] tipoOFFER = {53,1,2};
            tipoMensaje = tipoOFFER;
        }
        
        if(tipoMensajeDHCP.equals("ACK")){
            byte [] envioUniCast = {0,0}; tipoEnvio = envioUniCast;
            byte[] tipoACK = {53,1,5};
            tipoMensaje = tipoACK;
        }

        
        DHCPOFFERBuffer.put(generarCabecera(idNuevaTransaccion, tipoEnvio, MACCliente))
                       .put(generarOpciones());
        
        return DHCPOFFERBuffer.array();
    }
    
    //partes   
    private byte [] generarCabecera(byte [] idNuevaTransaccion, byte [] tipoDeEnvio, byte [] MACCliente){
        ByteBuffer bufferCabecera = ByteBuffer.allocate(236);
        xid = idNuevaTransaccion;
        flags = tipoDeEnvio;
        chaddr = MACCliente;
                
        bufferCabecera.put(op)
                .put(htype)
                .put(hlen)
                .put(hops)
                .put(xid)
                .put(secs)
                .put(flags)
                .put(ciaddr)
                .put(yiaddr)
                .put(siaddr)
                .put(giaddr)
                .put(chaddr)
                .put(sname)
                .put(file)
                ;
        
        return bufferCabecera.array();
    }
    
    private byte [] generarOpciones(){
        ByteBuffer bufferOpciones = ByteBuffer.allocate(340);
        
        bufferOpciones.put(magicCookie)
                .put(tipoMensaje)
                .put(mascara)
                .put(servidorDNS)
                .put(gateway)
                .put(tiempoCesion)
                .put(tiempoRenovacion)
                .put(IPServidor)
                .put(end)
                ;        
        
        return bufferOpciones.array();
    }
    
//Metodos para generar Opciones
    //Genera una opcion a partir de los parametros, si no es una opcion de tiempo intenta, si existe, añadir una INET
    private byte [] compiladorOpciones (int numeroBytes, int numOpcion, int numBytes, int tiempo, String ip){
       ByteBuffer byteBufferOpciones = ByteBuffer.allocate(numeroBytes)
               .put((byte) numOpcion)
               .put((byte) numBytes)
               ;
        if (tiempo > -1) 
            byteBufferOpciones.putInt(tiempo);
        else if (!ip.equals(""))
            byteBufferOpciones.put(new creadorDeInet().generarINET(ip).getAddress());
       
       return byteBufferOpciones.array();
    }
}