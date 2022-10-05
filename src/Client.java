import java.io.File;
import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        String filePath = "./excerpts";

        SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(filePath).build();

        StoreAppender appender = new StoreAppender(queue);
        String text = "Happy Wednesday!";
        appender.writeText(text);
    }
}
