package edu.ufp.inf.sd.rmi.server;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import edu.ufp.inf.sd.util.RabbitUtils;

public class Producer {

    // Name of the queue
    public static String TASK_QUEUE_NAME = "task_queue";
    /**
     * @param argv strings para hashing
     *             argv[0] queue name
     *             argv[1] palavas
     */
    public static void produce(String[] argv) throws Exception {

        //try-with-resources
        try(Connection connection= RabbitUtils.newConnection2Server("localhost", "guest", "guest");
            Channel channel= RabbitUtils.createChannel2Server(connection)){

            /* Declare a queue as Durable (queue won't be lost even if RabbitMQ restarts);
               RabbitMQ does not allow redefine an existing queue with different parameters (hence create a new one) */
            boolean durable=true;
            // cada taskGroup tem uma Task_queue diferente
            TASK_QUEUE_NAME= "task_queue" +argv[0];
            System.out.println("Producer----------->Taskqueue:"+ TASK_QUEUE_NAME);
            channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);
            
            String message= RabbitUtils.getMessage(argv, 1);

            /* To avoid loosing queues when rabbitmq crashes, mark messages as persistent by setting
             MessageProperties (which implements BasicProperties) MAIL_TO_ADDR value PERSISTENT_TEXT_PLAIN. */
            //channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
            channel.basicPublish("", TASK_QUEUE_NAME,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
    }

    public static void deleteQueue(String taskQuequeName) throws Exception {
        try(Connection connection= RabbitUtils.newConnection2Server("localhost", "guest", "guest");
            Channel channel= RabbitUtils.createChannel2Server(connection)){
            channel.queueDelete(taskQuequeName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void SendMatch(String[] argv) throws Exception {
         String EXCHANGE_NAME="logs_exchange";

        //Try-with-resources
        try (Connection connection= RabbitUtils.newConnection2Server("localhost", "guest", "guest");
             Channel channel= RabbitUtils.createChannel2Server(connection)) {

            System.out.println(" [x] Declare exchange: '" + EXCHANGE_NAME + "' of type " + BuiltinExchangeType.FANOUT.toString());
            /* Set the Exchange type MAIL_TO_ADDR FANOUT (multicast MAIL_TO_ADDR all queues). */
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

            String message= RabbitUtils.getMessage(argv, 3);

            /* Publish messages to the logs_exchange instead of the nameless one.
               Fanout exchanges will ignore routingKey (hence set routingKey="").
               Messages will be lost if no queue is bound to the exchange yet. */
            String routingKey="";
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent: '" + message + "'");
        }
    }
}
