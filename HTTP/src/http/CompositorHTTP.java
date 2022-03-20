package http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

//Clase que permite componer mensajes http adecuados para el cliente.
public class CompositorHTTP {
    //Variables del mensaje.
    private final String ok = "HTTP/1.1 200 OK";
    private final String tipoMensajeHTML = "Content-Type: text/html";
    private final String tipoMensajeFavicon = "Content-Type: icon/ico";
    private final String tipoMensajePNG = "Content-Type: image/png";
    private final String tipoMensajeJS = "Content-Type: application/javascript";
    private final String tipoMensajeCSS = "Content-Type: text/css";
    private final String tamanioMensaje = "Content-Length: ";
    private final String salto = "\r\n";
    
    //Recibe el tipo de mensaje a componer y devuelve el mensaje compuesto
    public byte [] componerMensajeSegunTipo(String peticion, String extensionPeticion){
        byte [] mensajeCompuesto;
        
        switch(extensionPeticion){
            case "/": mensajeCompuesto = componerMensaje(tipoMensajeHTML, "index.html"); break;
            case "html":  mensajeCompuesto = componerMensaje(tipoMensajeHTML, peticion); break;
            case "css":  mensajeCompuesto = componerMensaje(tipoMensajeCSS, peticion);break;
            case "js": mensajeCompuesto = componerMensaje(tipoMensajeJS, peticion);break;
            case "ico":  mensajeCompuesto = componerMensaje(tipoMensajeFavicon, peticion);break;
            case "png":  mensajeCompuesto = componerMensaje(tipoMensajePNG, peticion);break;      
            default:  mensajeCompuesto = componerMensaje(tipoMensajeHTML, peticion); break;
        }
        
        return mensajeCompuesto;
    }
    
    //Compone el mensaje con el tipo de mensaje y el archivo a enviar proporcionados.
    public byte [] componerMensaje (String tipoMensaje,String archivo){
        byte [] ficheroAEnviar = ficheroABytes(archivo);
        String cabeceraMensaje = ok + salto + tipoMensaje + salto + tamanioMensaje + ficheroAEnviar.length + salto + salto;
        
        ByteBuffer mensajeCompuesto = ByteBuffer.allocate(cabeceraMensaje.getBytes().length + ficheroAEnviar.length);
        mensajeCompuesto.put(cabeceraMensaje.getBytes()).put(ficheroAEnviar);
        
        return mensajeCompuesto.array();
    }    

    //Genera un array de bytes a partir de un fichero de la carpeta site cuyo nombre ha sido proporcionado por el cliente.
    public byte [] ficheroABytes(String nombreFichero){
        byte [] bytesFichero = "404: PAGE NOT FOUND".getBytes();
        try {
            bytesFichero = Files.readAllBytes(Paths.get("site\\" + nombreFichero.replace("/", "")));
        } catch(NoSuchFileException e){
        } catch (IOException ex) {
            Logger.getLogger(CompositorHTTP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bytesFichero;
    }
    
}
