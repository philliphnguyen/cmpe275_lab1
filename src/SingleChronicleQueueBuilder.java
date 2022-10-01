import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SingleChronicleQueueBuilder {
    private File path;

    protected SingleChronicleQueueBuilder() {

    }

     //No major changes needed
    public static SingleChronicleQueueBuilder binary(String basePath) {
        //What is WireType?
        /*
        WireType is used to configure how Chronicle Queue will store the data
        BINARY_LIGHT is the default
         */

        //return builder(new File(basePath), WireType.BINARY_LIGHT)
        return builder(new File(basePath));
    }

    //No major changes needed
    public static SingleChronicleQueueBuilder builder(File file) {
        //SingleChronicleQueueBuilder result = builder().wireType(wireType);
        SingleChronicleQueueBuilder result = builder();

        if (file.isFile()) {
            //TODO process if file exists?
            if (!file.getName().endsWith(".cq4")) {
                throw new IllegalArgumentException("Invalid file type: " + file.getName());
            }

            result.path(file.getParentFile());
        }
        else {
            result.path(file);
        }

        return result;
    }

    public static SingleChronicleQueueBuilder builder() {
        return new SingleChronicleQueueBuilder();
    }

    public SingleChronicleQueue build() {
        this.preBuild();
        
        SingleChronicleQueue chronicleQueue = new SingleChronicleQueue(this);

        this.postBuild(chronicleQueue);
        
        return chronicleQueue;
    }

    private void postBuild(SingleChronicleQueue chronicleQueue) {
        //What is create appender condition???
        return;
    }

    public SingleChronicleQueueBuilder path(File path) {
        this.path = path;
        return this;
    }

    protected void preBuild() {
        this.initializeMetadata();
    }

    private void initializeMetadata() {
        File metapath = this.metapath();

        //Roll cycle is how often to create new file so files don't become too large
        //Should we implement rolling cycles?
        //this.validateRollCycle(metapath)

        SCQMeta metadata = new SCQMeta(System.currentTimeMillis());

        //TODO Do something with metadata
        //Use SCQMeta and SingleTableBuilder class?f
        try {
            FileWriter writer = new FileWriter(metapath);
            writer.write("hello");
            writer.close();
            System.out.println("wrote to metadata file");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //No major changes needed
    private File metapath() {
        File storeFilePath;

        if ("".equals(this.path.getPath())) {
            storeFilePath = new File("metadata.cq4t");
        }
        else {
            storeFilePath = new File(this.path, "metadata.cq4t");
            this.path.mkdirs();
        }

        return storeFilePath;
    }

    public File path() {
        return this.path;
    }
}
