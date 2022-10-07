import java.io.IOException;

public class Server {
    public static void main(String[] args) {
        String filePath = "./excerpts";

        SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(filePath).build();

        StoreTailer tailer = new StoreTailer(queue);

        while (true) {
            String text = tailer.readText();


            System.out.println(text);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //queue.close();
    }
}
