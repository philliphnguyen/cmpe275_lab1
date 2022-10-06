import java.io.File;
import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        String filePath = "./excerpts";

        SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(filePath).build();

        StoreAppender appender = new StoreAppender(queue);
        String text = "Test header overwriting for third msg";
        appender.writeText(text);
        // appender.close();

        StoreTailer tailer = new StoreTailer(queue);
        System.out.println("Content: " + tailer.readText() + " -> Index: " + tailer.lastReadIndex());
        System.out.println("Content: " + tailer.readText() + " -> Index: " + tailer.lastReadIndex());
        System.out.println("Content: " + tailer.readText() + " -> Index: " + tailer.lastReadIndex());
        System.out.println("Content: " + tailer.readText() + " -> Index: " + tailer.lastReadIndex());
        System.out.println("Content: " + tailer.readText() + " -> Index: " + tailer.lastReadIndex());
        System.out.println("Content: " + tailer.readText() + " -> Index: " + tailer.lastReadIndex());
    }
}
