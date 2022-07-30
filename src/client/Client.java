package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        try {
            Socket socket = new Socket("127.0.0.1", 9445); // 1. подкл. к удал. серверу
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());// 2. создаем поток отправки сообщений - СОЗДАЛИ ПОТОК ВЫВОДА
            DataInputStream is = new DataInputStream(socket.getInputStream()); // 16
            Scanner scanner = new Scanner(System.in); // 3. ждем, пока пользователь напишет
            Thread thread = new Thread(new Runnable() { //24
                @Override
                public void run() {
                    try {
                        while (true) {  // 27
                            String response = is.readUTF(); // 17       // 26
                            System.out.println(response); // 18
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start(); // 25
            while (true) { // 10. создали бескон. цикл для клиента, чтобы он мог всегда посылать сообщения, а не выкл-ась прогр. после 1го сообщения
                String message = scanner.nextLine(); // 4. читаем с клавиатуры
                out.writeUTF(message); // 5. метод записи в файл у потока вывода, UTF-кодировка по кириллице

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


// 16. НА сервере создали поток по отправке сообщений, в клиенте создали по ПРИЕМКЕ СООБЩЕНИЙ
// 17 на перем-ю "response" сохраняем то, что пришло на "is" через конструктор "new DataInputStream(socket.getInputStream());"
// 18 печатаем сообщения от других клиентов через сервер на консоль

// 24. Создаем многопоточность для того, чтобы одновременно могли одновременно получать и отправлять сообщения!!! 1:21:00
// 25. Запускаем многопоточность
// 26. Перенесли две строки длшя того, чтобы получать сообщения от сервера не зависимо от написания сообщений от других клиентов!

// 27. Делается еще один бескон цикл для того, чтобы все клиенты постоянно видели сообщения от других клиентов. Без цикла все видели только
// первое сообщение первого написавшего клиента и поток закрылся         1:26:00
