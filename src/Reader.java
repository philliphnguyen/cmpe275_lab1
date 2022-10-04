import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reader {
    private List<String> fileNames = new ArrayList<String>();
    private int cycle;
    private long index;
    private long lastReadIndex;

    public Reader() {
        this.cycle = 0;
        this.index = 0;
        String path = "./excerpts";
        File dir = new File(path);
        String[] files = dir.list();
        Arrays.sort(files);

        for (String fileName: files) {
            if (fileName.endsWith(".cq4")) {
                fileNames.add(fileName);
                // System.out.println(fileName);
            }
        }
    }

    public String readText() {
        StringBuilder sb = new StringBuilder();
        String filePath = "./excerpts/" + fileNames.get(cycle);
        RandomAccessFile raf = null;

        try {
            raf = new RandomAccessFile(filePath, "r");

            if (index >= raf.length()) {
                if (cycle + 1 < fileNames.size()) {
                    cycle++;
                    index = 0;      // reset index for next file
                    filePath = "./excerpts/" + fileNames.get(cycle);
                    raf = new RandomAccessFile(filePath, "r");
                } else {
                    // reached end of queue
                    return null;
                }
            }
            raf.seek(index);

            lastReadIndex = index;

            // calculate the length of the data from the excerpt 4-byte header
            byte[] arr = new byte[32];      // data is represented as bits, 4 bytes = 32 bits
            raf.read(arr, 0, 32);
            index += 32;
            int excerptLength = (int) Long.parseLong(new String(arr), 2);       // convert 32-bit binary to int
            // System.out.println("Binary: " + new String(arr) + " -> Integer: " + excerptLength);

            // convert binary data to text
            int n = excerptLength / 8;      // we are going to read 8 bits each iteration
            for (int i = 0; i < n; i++) {
                byte[] bits = new byte[8];
                raf.read(bits, 0, 8);
                String bitString = new String(bits);
                // System.out.println(bitString);
                int charCode = Integer.parseInt(bitString, 2);
                String letter = Character.toString((char) charCode);
                sb.append(letter);
                index+=8;
            }

            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public int cycle() {
        return this.cycle;
    }

    public long lastReadIndex() {
        return this.lastReadIndex;
    }

    public boolean addFile(String fileName) {
        return fileNames.add(fileName);
    }

    public static void main(String[] args) {
        Reader reader = new Reader();
        System.out.println(reader.readText());
        System.out.println(reader.readText());
        System.out.println(reader.readText());
        System.out.println(reader.readText());
        System.out.println(reader.readText());
        System.out.println(reader.readText());
        System.out.println(reader.readText());
        System.out.println(reader.readText());
        System.out.println(reader.readText());
    }
}
