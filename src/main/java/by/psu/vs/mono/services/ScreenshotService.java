package by.psu.vs.mono.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ScreenshotService {

    static {
        // Необходимо для работы с библиотекой оконного графического интерфейса AWT,
        // в частности, для получения скриншота экрана через объект класса java.awt.Robot
        System.setProperty("java.awt.headless", "false");
    }

    /**
     * Шедулер получения картинки раз в секунду
     */
    @Scheduled(fixedDelay = 100)
    public void  scheduleFixedDelayTask() {
        makeScreenShot("saved.png");
    }

    /**
     * Возвращает файл картинки в виде массива байт
     * @return byte[] - байтовое представление файла картинки
     * @throws IOException
     */
    synchronized public byte[] getScreenShot()  throws IOException {
        File outputFile = new File("saved.png");
        return Files.readAllBytes(outputFile.toPath());
    }

    /**
     * Возвращает сетевое имя компьютера, на котором запущена программа
     *
     * @return String - имя компьютера
     */
    public String getComputerName()
    {
        String hostname = "";
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            hostname = address.getHostName();
        }
        catch (UnknownHostException ex) {
            hostname = "Undefined";
        }
        return hostname;
    }


    /**
     * Фабрика для restTemplate объекта.
     * Используется для установки тайм-аутов соединения и чтения
     *
     * @author Vladimir Hlukhau
     */
    private SimpleClientHttpRequestFactory getClientHttpRequestFactory()
    {
        SimpleClientHttpRequestFactory clientHttpRequestFactory
                = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactory.setConnectTimeout(100);

        //Read timeout
        clientHttpRequestFactory.setReadTimeout(1000);
        return clientHttpRequestFactory;
    }

    // Карта обнаруженных IP-адресов
    public Map<String, String> ips = new HashMap<>();

    Runnable scan = () -> {
        Map<String, String> newIps = new HashMap<>();

        try {
            // попытка создания соединения с Интернет-сайтом google.com
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));

            // взятие локального IP-адреса, через который происходит соединение с реальной сетью
            String myIp = (((InetSocketAddress) socket.getLocalSocketAddress()).getAddress()).toString().replace("/", "");

            // Создание RestTemplate объекта для запроса http:// ресурсов
            // в конструктор передается фабрика, в которой имеется возможность установить таймаут установки соединения
            // 10мс, т.о. все 254 адреса будут перебраны за 2.5 секунды,
            // а по ЛВС 10мс должно хватать на установку соединения
            RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

            // разделение IP-адреса на четыре целых числа
            String[] ip = myIp.split("[.]");

            // перебор 254 адресов локальной сети С-класса, адрес с последним числом 255 - широковещательный
            for (int i = 1; i < 255; i++) {

                // формирование http ссылки на сформированный IP и порт 8888 для получения имени компьютера
                // и запроса текущего скриншота
                final String url = "http://" + ip[0] + "." + ip[1] + "." + ip[2] + "." + i + ":8888/name";
                final String image = "http://" + ip[0] + "." + ip[1] + "." + ip[2] + "." + i + ":8888/image";

                try {
                    // попытка получить сформировать локатор
                    URI uri = new URI(url);

                    // получение ответа от сервера:
                    // - если ошибка, значит сервер на данном IP не запущен
                    // - иначе вернется имя компьютера
                    ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

                    // помещение пары (url-получения скриншота, имя компьютера) в карту
                    newIps.put(image, result.getBody());
                } catch (Exception ignored) {}
            }
        }
        catch (IOException ignored) {}

        ips = newIps;
        log.info("Scan finished");
    };

    Thread scanThread = null;
    /**
     * Метод пересканирует раз в минуту локальную сеть в поисках установленных на портах 8888 программ Mono
     * и заполняет локальный кеш ips.
     *
     * @author Vladimir Hlukhau
     */
    @Scheduled(initialDelay=0, fixedDelay = 60000)
    public void scanIps() {

        if (scanThread == null) {
            scanThread = new Thread(scan);
            scanThread.start();
            log.info("New thread created");
        }

        if (! scanThread.isAlive()) {
            scanThread = new Thread(scan);
            scanThread.start();
            log.info("Thread was closed and starts again");
        }
    }

    /**
     * Метод создает скриншот экрана и сохраняет его на диск в указанный файл.
     *
     * Данный метод синхронизирован по this для избежания ситуации,
     * когда чтение картинки происходит в момент ее создания.
     * Синхронизация необходима, поскольку встроенный в SpringBoot приложение
     * Web-сервер Tomcat является многопоточным и запросы на чтение картинки
     * могут поступать асинхронно, относительно запуска шедулера по генерированию скриншота.
     *
     * @param path - путь и имя файла для сохранения скриншота
     * @author Vladimir Hlukhau
     */
    synchronized public void makeScreenShot(String path) {
        try {
            // Создаем объект класса java.awt.Robot
            Robot robot = new Robot();

            // Получение размера экрана
            final Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            BufferedImage bufferedImage;

            // Завершение всех событий перерисовки экрана
            robot.setAutoWaitForIdle(true);

            // снятие скриншота
            bufferedImage = robot.createScreenCapture(area);

            try {
                // сохранение скриншота в файл
                File outputFile = new File(path);
                ImageIO.write(bufferedImage, "png", outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch(AWTException e) {
            e.printStackTrace();
        }
    }
}
