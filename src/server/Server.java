package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class Server {
    public static void main(String[] args) {
        ArrayList<Socket> sockets = new ArrayList<>();
        try {
            ServerSocket serverSocket = new ServerSocket(9445);
            System.out.println("Сервер запущен");
            while (true) {
                Socket socket = serverSocket.accept();
                sockets.add(socket);
                System.out.println("Клиент подключился");
                System.out.println();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = "";
                        try {
                            DataInputStream is = new DataInputStream(socket.getInputStream());
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            out.writeUTF("Введите имя: ");
                            name = is.readUTF();
                            while (true) {
                                String message = is.readUTF();
                                for (Socket socket1 : sockets) {
                                    DataOutputStream out1 = new DataOutputStream(socket1.getOutputStream());
                                    out1.writeUTF(name + ": " + message);
                                }
                                System.out.println(message);
                            }
                        } catch (IOException e) {
                            sockets.remove(socket);
                            System.out.println(name + ": " + "Отключился");
                            for (Socket socket1 : sockets) {
                                try {
                                    DataOutputStream out1 = new DataOutputStream(socket1.getOutputStream());
                                    out1.writeUTF(name + ": " + "Отключился");
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
