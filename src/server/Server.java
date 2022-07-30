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
                            DataInputStream is = new DataInputStream(socket.getInputStream()); //6
            //8
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // 15  //19
                            out.writeUTF("Введите имя: "); // 28
                            name = is.readUTF(); // 29
                            while (true) { // 14
                                String message = is.readUTF(); // 9. создаем перем-ю "message" и сохраняем на нее то, что переслано от клиента. Т.к. перемылали в UTF, то и сохраняем "is.readUTF();"
                                for (Socket socket1 : sockets) { // 22
                                    DataOutputStream out1 = new DataOutputStream(socket1.getOutputStream()); //23
                                    out1.writeUTF(name + ": " + message); // 30
                                }
                                System.out.println(message);
                            }
                        } catch (IOException e) {
                            sockets.remove(socket); // 31
                            System.out.println(name + ": " + "Отключился"); // 32
                            for (Socket socket1 : sockets) {
                                try {
                                    DataOutputStream out1 = new DataOutputStream(socket1.getOutputStream());
                                    out1.writeUTF(name + ": " + "Отключился"); // 30         //33
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }
                    }
                });
                thread.start(); // 13. Запускаем поток бесконечного чтения сообщений от клиентов
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

// 6. создаем поток данных для получения информации
// 8. Когда в этом файле появился "socket", обращ-ся к нему и вызываем метод .getInputStream()

// 14. Когда клиент прислал сообщение, сервер его принял, вывел на консоль и закрывался. ДЛя предотвращения сделали бескон цикл "while (true)"
// Теперь в сервере раб два потока одновременно: "Socket socket = serverSocket.accept()" для подключения клиентов
// и "Thread thread = new Thread" для ожидания сообщений. Это стало возможно благодаря "thread.start()" - запуска многопоточности

//                   Теперь прописываем отправку сообщений от одного клиента через сервер другим клиентам, чтобы был общий чат
// 15. "new DataOutputStream(socket.getOutputStream())" - создаем поток отправки сообщений клиентам

// 19 Для того, чтобы сделать рассылку всем подключенным клиентам. Чтобы отправлять сообщения, используется поток вывода "DataOutputStream"
// в который кладется "socket" того клиента, который к нам подключился (ServerSocket serverSocket = new ServerSocket)!
// и для которого мы запустили ПОТОК!

// 20. У каждого клиента свой СОКЕТ. Потому каждому нужно на его поток ввода посылать сообщения. СОБИРАЮТСЯ все сокеты всех клиентов в ARRAYLIST!!!
// 21. когда подключился клиент, ДОБАВЛЯЕМ его СОКЕТ в коллекцию
// 22. Когда коллекция наполнилась, нужно перед отправкой перебрать все СОкеты всех клиентов. ДЛя этого создается цикл FOReach
// 23. ДЛя каждого "socket1" создается ПОТОК ВЫВОДА

// 28. Просим клиента представиться
// 29. Получаем от клиентов сообщение
// 30. Когда создали перем. "name", на которую сохраняется имя, теперь добавляем её в те места, где клиенты будут писать сообщения

// 31. Те клиенты, которые отключились, должны быть убраны из чата. Для этого из коллекции убираем их по сокетам 1:44:00
// 32. На сервере выводим, что клиент отключился
// 33. Пишем остальным, что клиент такой то отключился



