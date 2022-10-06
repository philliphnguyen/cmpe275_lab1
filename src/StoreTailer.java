import java.io.*;

public class StoreTailer {
    private final SingleChronicleQueue queue;
    private int cycle;
    private long index;
    private long lastReadIndex;

    public StoreTailer(SingleChronicleQueue queue) {
        this.queue = queue;
        this.cycle = 0;
        this.index = 0;
    }

    public String readText() {
        StringBuilder sb = new StringBuilder();
        String filePath = queue.path + "/" + queue.toFileName(cycle);
        RandomAccessFile raf = null;

        try {
            raf = new RandomAccessFile(filePath, "r");

            // if we reached the end of the current cycle, move to the next cycle
            if (index >= raf.length()) {
                if (cycle + 1 < queue.lastCycle()) {
                    cycle++;
                    index = 0;      // reset index for next file
                    filePath = queue.path + "/" + queue.toFileName(cycle);
                    raf = new RandomAccessFile(filePath, "r");
                } else {
                    // reached end of queue
                    return null;
                }
            }
            raf.seek(index);

            // the first bit of the message indicates whether the message is completed, 1 means completed, 0 means incomplete
            byte[] complete = new byte[1];
            raf.read(complete, 0, 1);
            int completeFlag = Integer.parseInt(new String(complete), 2);
            if (completeFlag == 0) {
                return null;
            }

            lastReadIndex = index;

            // the size of the data is stored in the remaining 31 bits of the header
            byte[] length = new byte[31];
            raf.read(length, 0, 31);
            index += 32;
            int excerptLength = (int) Long.parseLong(new String(length), 2);       // convert 31-bit binary to decimal number

            // convert binary data to text
            int n = excerptLength / 8;      // we are going to read 8 bits each iteration
            for (int i = 0; i < n; i++) {
                byte[] bits = new byte[8];
                raf.read(bits, 0, 8);
                String bitString = new String(bits);
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

        // Note: The newline character is different on different OS
        index += 1;     // skip the newline character
        return sb.toString();
    }

    public int cycle() {
        return this.cycle;
    }

    public long lastReadIndex() {
        return this.lastReadIndex;
    }
}