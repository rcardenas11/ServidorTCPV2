package principal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import hilos.HiloServidorTCP;

public class Servidor {
	
	private static ServerSocket server;
	private static int id = 0;
	
	public static void main(String[] args) {
		
		try {
			server = new ServerSocket(3000,100);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Socket conexion = null;
		
		// escucha si alguien se quiere conectar a el y aceptar la conexion y guardar el socket
		
		do {
			try {
				conexion = esperarConexion();
				((HiloServidorTCP) new HiloServidorTCP(conexion, ++id)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}while(true);
		
	}
	
	private static Socket esperarConexion() throws IOException {
		System.out.println("Esperando Conexion");
		return server.accept();
	}
	

}
