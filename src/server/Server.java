package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
   static ArrayList<User> users = new ArrayList<>(); // 1.1
    public static void main(String[] args) {
//        ArrayList<Socket> sockets = new ArrayList<>();

        try {
            ServerSocket serverSocket = new ServerSocket(9445);
            System.out.println("Сервер запущен");
            while (true) {
                Socket socket = serverSocket.accept();
                User currentUser = new User(socket); // 2.1
//                sockets.add(socket);
                users.add(currentUser); // 3.1
                System.out.println("Клиент подключился");
                System.out.println();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
// String name = "";
                        try {
// DataInputStream is = new DataInputStream(socket.getInputStream()); //6
//  //8                                                                                           // 4.1
// DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // 15  //19
// out.writeUTF("Введите имя: "); // 28
                            currentUser.getOut().writeUTF("{\"msg\":\"Введите имя:\"}"); // 5.1
// name = is.readUTF(); // 29
                            currentUser.setName(currentUser.getIs().readUTF()); // 6.1
                            sendOnlineUsers();
                            while (true) { // 14
// String message = is.readUTF(); // 9
                                String message = currentUser.getIs().readUTF();
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("msg", currentUser.getName()+ ": " +message);
// for (Socket socket1 : sockets) { // 22
                                for (User user : users) { // 7.1
// DataOutputStream out1 = new DataOutputStream(socket1.getOutputStream()); //23
// out1.writeUTF(name + ": " + message); // 30
                                    if (!currentUser.getUuid().toString().equals(user.getUuid().toString())) // 10.1
                                        user.getOut().writeUTF(currentUser.getName() + ": " + message); // 8.1


                                }
                                System.out.println(message);
                            }
                        } catch (IOException e) { // ПРИ ОТКЛЮЧЕНИИ ПОЛЬЗОВАТЕЛЯ УДАЛЯЕМ ЕГО
// sockets.remove(socket); // 31
                            users.remove(currentUser); // 9.1
                            System.out.println(currentUser.getName() + ": " + "Отключился"); // 32
                            for (User user: users) { // РАССЫЛАЕМ ВСЕМ, ЧТО ПОЛЬЗОВАТЕЛЬ ОТКЛЮЧИЛСЯ
                                try {
// DataOutputStream out1 = new DataOutputStream(socket1.getOutputStream());
                                    user.getOut().writeUTF(currentUser.getName() + ": " + "Отключился"); // 30         //33
                                    sendOnlineUsers();
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
    static void sendOnlineUsers() throws IOException {
        JSONArray onlineUsersJSON = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (User user : users) {
            onlineUsersJSON.add(user.getName());
        }
        System.out.println(onlineUsersJSON.toString());
        jsonObject.put("users", onlineUsersJSON);
        for (User user : users) {
            user.getOut().writeUTF(onlineUsersJSON.toJSONString());
        }
    }
}

// 6. создаем поток данных для получения информации
// 8. Когда в этом файле появился "socket", обращ-ся к нему и вызываем метод .getInputStream()
// 9. создаем перем-ю "message" и сохраняем на нее то, что переслано от клиента. Т.к. перемылали в UTF, то и сохраняем "is.readUTF();"

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


//                                                17 занятие
// 1.1 Переделываем в новую коллекцию, потому что сокеты нигде не используются
// 2.1 Теперь добавляем подключившихся пользователей в коллекцию "users". В класс "User" мы собираем "currentUser (текущий)" и через конструктор
// добавляем (socket), который берется от подключившегося пользователя
// 3.1 Добавляем "currentUser (текущий)" в коллекцию. теперь, КОГДА ПОДКЛЮЧИЛСЯ ПОЛЬЗОВАТЕЛЬ, МЫ ДЛЯ НЕГО СОЗДАЛИ ОБЪЕКТ И ДОБ. В КОЛ-ЦИЮ

// 4.1 Убрали потоки вывода и ввода, потому что они есть в классе USER
// 5.1 Обращаемся теперь к текущему пользователю через "currentUser.getOut()"

// 6.1 Полученное сообщение записываем в качестве имени
// 7.1 Перебираем теперь массив USEROV
// 8.1 "out1" уже не существует, заменили на "user.getOut()", "name" заменили на "currentUser.getName()"

// 9.1 теперь из новой коллекции "users" мы удаляем текущего пользователя под названием "currentUser"

// 10.1 Делаем запрет на отправку сообщения пользователю самому себе через "UUID", который является объектом. Но мы через метод "toString()" переводим объект
// в строку и сравниваем через метод "equals"


