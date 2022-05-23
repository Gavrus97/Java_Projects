// LoadBalancer
// 1. слушает 3000 порт на прием UDP сообщений от серверов (каждый сервер знает, где находится лоад бэлансер)
// 2. LoadBalancer принимает информацию с серверов о их загрузке в следующем виде: "<server tcp port>:<load>"
// где <server tcp port> это порт, который открыт на сервере для обработки данных с Gateway
// 3. LoadBalancer вытаскивает из датаграиы с сервера инфлрмацию о сервере, а именно ip address, порт для tcp подключений
// и текущую нагрузку на сервер. Далее LoadBalancer созраняет эту информацию внутри себя в определенную структуру данных,
// добавляя к информации о сервере timestamp (время получения информации о сервере)
// 4. Каждую секунду LoadBalancer произвожит самоотчищение. Удаляет информацию о тех серверах, которые не давали о себе
// знать одну секунду.
// 5. Каждые 100 милисекунд LoadBalancer отправляет информацию о наименее загруженном сервере на gateway на порт 2001
// в формате "<server host (ip)>:<server tcp port>"

// По архитектуре
// 0. Необходима структура данных, хранящая информацию об актуальных серверах
// 1. На LoadBalancer есть один поток для приема сообщений с серверов
// 2. Еще один поток необходим для очищения структуры данных с серверами от отвалившихся серверов каждую секунду
// 3. Еще один поток необзодим для отправки каждые 100 милисекунд информации об оптимальном сервере на GateWay через
// UDP датаграму

// Gateway
// 1. Слушает 2000 порт для приема TCP соединений от клиентов (иными словами, открытый в интернете порт)
// 2. Gateway слушает 2001 порт для приема UDP датаграм от LoadBalancer
// 3. Хранит информацию об оптимальном сервере, полученную от LoadBalancer и перенаправляет TCP соединения
// на оптимальный сервер

// По архитектуре
// 1. Необходим поток для приема датаграм с LoadBalancer и обновдения информации об оптимальном сервере
// 2. Необхоим поток который принимает соединения с клиентов и добавляет таски в ThreadPool.
// обрабатываюшие эти соединения
// Каждый такой таск делает следущее: берет информацию об оптимальном сервере и устанавливает tcp
// подключение с этим сервером, далее перенаправляет данные с кдиента к серверу. а затем ответы с сервера к клиенту


// Sever
// 1. Слушает кастомный tcp порт (40хх) на прием о обработку данных от gateway
// 2. Каждые 100 мидисекунд отправляет udp датаграмму на LoadBalancer на порт 3000 о состоянии собственной загрузки
// в формате "<server tcp port>:<load>"

// По архитектуре
// 0. объект хранящий текущую нагрузку, доступный для потока отправки датаграм на LoadBalancer
// 1. поток на отправку загрузки
// 2. поток на прием и оьработку данных с gateway




import storage.IServerStorage;
import storage.ServerList;

import java.io.IOException;

public class LoadBalancer {

    private static final String DEFAULT_PROPS_PATH = "config/application.props";
    private static final String UDP_FROM_SERVER_PORT_KEY = "udp.balancer.port";
    private static final String UDP_TO_GATEWAY_PORT_KEY = "udp.gateway.port";
    private static final String GATEWAY_HOST_KEY = "gateway.host";

    public static void main(String[] args) throws IOException {
        String propsPath = args.length > 0 ? args[0] : DEFAULT_PROPS_PATH;
        ApplicationProperties properties = new ApplicationProperties(propsPath);

        int udpServerPort = Integer.parseInt(properties.getProperty(UDP_FROM_SERVER_PORT_KEY));

        IServerStorage serverStorage = new ServerList();
        UdpServerListener udpServerListener = new UdpServerListener(serverStorage, udpServerPort);
        new Thread(udpServerListener).start();

        int udpGatewayPort = Integer.parseInt(properties.getProperty(UDP_TO_GATEWAY_PORT_KEY));
        String gatewayHost = properties.getProperty(GATEWAY_HOST_KEY);
        UdpGatewaySender udpGatewaySender = new UdpGatewaySender(gatewayHost, serverStorage, udpGatewayPort, 100);
        new Thread(udpGatewaySender).start();

        Cleaner cleaner = new Cleaner(serverStorage, 1000);
        new Thread(cleaner).start();
    }
}
