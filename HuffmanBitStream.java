/**
 * Created by Abhijeet Sidhu
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class HuffmanBitStream {
    private BufferedOutputStream out;
    private BufferedInputStream in;
    private int count;
    private int temp;
    private int mask;

    public HuffmanBitStream(BufferedOutputStream var1) {
        this.out = var1;
    }

    public HuffmanBitStream(BufferedInputStream var1) {
        this.in = var1;
    }

    public void write(char var1) throws IOException {
        if(var1 == 49) {
            ++this.temp;
        } else if(var1 != 48) {
            throw new IOException();
        }

        if(this.count == 7) {
            this.out.write(this.temp);
            this.temp = 0;
            this.count = 0;
        } else {
            ++this.count;
            this.temp <<= 1;
        }

    }

    public void close() throws IOException {
        if(this.out != null) {
            if(this.count > 0) {
                this.temp <<= 7 - this.count;
                this.out.write(this.temp);
            }

            this.out.close();
        }

        if(this.in != null) {
            this.in.close();
        }

    }

    public int read() throws IOException {
        if(this.mask == 0) {
            this.temp = this.in.read();
            this.mask = 128;
        }

        int var1 = this.temp & this.mask;
        this.mask >>= 1;
        if(var1 != 0) {
            var1 = 1;
        }

        return var1;
    }
}

