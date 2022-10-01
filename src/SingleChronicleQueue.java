import java.io.File;

public class SingleChronicleQueue {
    File path;
    public SingleChronicleQueue(SingleChronicleQueueBuilder builder) {
        //Construction process from builder design pattern
        this.path = builder.path();
    }
}
