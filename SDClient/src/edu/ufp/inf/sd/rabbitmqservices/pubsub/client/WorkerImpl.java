package edu.ufp.inf.sd.rabbitmqservices.pubsub.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import edu.ufp.inf.sd.rabbitmqservices.pubsub.chatgui.GuiClient;
import edu.ufp.inf.sd.rmi.server.HashSessionRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class WorkerImpl extends UnicastRemoteObject implements WorkerRI {

    public HashSessionRI hashSessionRI;

    protected WorkerAlgorithms workerAlgorithms;

    public boolean paused = false;

    public String id;

    public GuiClient guiClient;

    public WorkerImpl(String id, HashSessionRI hashSessionRI, GuiClient guiClient) throws RemoteException {
        super();
        this.id = id;
        this.hashSessionRI=hashSessionRI;
        this.guiClient=guiClient;
    }

    private static final String EXCHANGE_NAME = "topic_logs";

    public void consume(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        String taskQueueName = argv[0];
        System.out.println("Worker----------->Taskqueue:"+taskQueueName);
        channel.queueDeclare(taskQueueName, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //topicos
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();

        if (argv.length < 1) {
            System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
            System.exit(1);
        }

        for (String bindingKey : argv) {
            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
        }

        System.out.println(" [*] Waiting for messages v2. To exit press CTRL+C");

        /*DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message1 = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message1 + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback1, consumerTag -> { });*/
        // fim topicos
        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            System.out.println(" [x] Received '" + message + "'");
            try {
                workerAlgorithms = new WorkerAlgorithms(this,message,hashSessionRI);
                workerAlgorithms.run();
            } finally {
                workerAlgorithms =null;
                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        channel.basicConsume(taskQueueName, false, deliverCallback, consumerTag -> { });
    }

    public void sendMatch(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            String routingKey = getRouting(argv);
            String message = getMessage(argv);

            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public String getID() throws RemoteException {
        return this.id;
    }

    @Override
    public String userID() throws RemoteException {
        return hashSessionRI.getID();
    }

    @Override
    public void pauseWorker() throws RemoteException {
        this.setPaused(true);
    }

    @Override
    public void taskGroupIsPaused(String tgID) throws RemoteException {
        guiClient.updateTextArea("TaskGroup"+ tgID);
    }

    public String getRouting(String[] strings) {
        if (strings.length < 1)
            return "anonymous.info";
        return strings[0];
    }

    public  String getMessage(String[] strings) {
        if (strings.length < 2)
            return "Hello World!";
        return joinStrings(strings, " ", 1);
    }

    public String joinStrings(String[] strings, String delimiter, int startIndex) {
        int length = strings.length;
        if (length == 0) return "";
        if (length < startIndex) return "";
        StringBuilder words = new StringBuilder(strings[startIndex]);
        for (int i = startIndex + 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }

    public WorkerAlgorithms getWorkerAlgorithms() {
        return workerAlgorithms;
    }
}
