package org.skyscreamer.nevado.jms;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Carter Page
 * Date: 3/25/12
 * Time: 6:47 PM
 */
public class RandomData {
    private static final Random RAND = new Random(); // Thread-safe

    public static Integer readInt() {
        return RAND.nextInt();
    }
    
    public static String readString() {
        return UUID.randomUUID().toString();
    }

    public static Boolean readBoolean() {
        return RAND.nextBoolean();
    }
    
    public static Byte readByte() {
        byte[] aByte = new byte[1];
        RAND.nextBytes(aByte);
        return aByte[0];
    }
    
    public static Short readShort() {
        return readInt().shortValue();
    }
    
    public static Character readChar() {
        return UUID.randomUUID().toString().charAt(0);
    }
    
    public static Long readLong() {
        return RAND.nextLong();
    }
    
    public static Float readFloat() {
        return RAND.nextFloat();
    }

    public static Double readDouble() {
        return RAND.nextDouble();
    }

    public static byte[] readBytes(int numBytes) {
        byte[] bytes = new byte[numBytes];
        RAND.nextBytes(bytes);
        return bytes;
    }
}
