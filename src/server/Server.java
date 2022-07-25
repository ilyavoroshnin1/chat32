package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(9445);
            System.out.println("Сервер запущен");
            serverSocket.accept();
            System.out.println("Клиент подключился");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
