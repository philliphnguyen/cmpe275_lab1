import java.io.File;
import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        String filePath = "./excerpts";

        SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(filePath).build();
    }
}
