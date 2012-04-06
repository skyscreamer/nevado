package org.skyscreamer.nevado.jms.util;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class SerializeUtil
{
    public static String serializeToString( Serializable serializable ) throws IOException
    {
        byte[] data = serialize(serializable);
        return new String( Base64.encodeBase64(data) );
    }

    public static byte[] serialize( Serializable serializable ) throws IOException {
        // Initialize buffer and converter
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output( byteArrayOutputStream );

        // Serialize objects
        hessian2Output.startMessage();
        if (serializable instanceof Character) {
            // Hessian doesn't properly serialize java.lang.Character
            serializable = new CharWrapper((Character)serializable);
        }
        hessian2Output.writeObject( serializable );
        hessian2Output.completeMessage();
        hessian2Output.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static Serializable deserializeFromString(String s) throws IOException
    {
        // Initialize buffer and converter
        byte [] dataBytes = Base64.decodeBase64(s);
        return deserialize(dataBytes);
    }

    public static Serializable deserialize(byte[] dataBytes) throws IOException {
        Hessian2Input hessian2Input = new Hessian2Input( new ByteArrayInputStream(  dataBytes ) );

        // Convert
        hessian2Input.startMessage();
        Serializable serializable = (Serializable)hessian2Input.readObject();
        if (serializable instanceof CharWrapper) {
            serializable = ((CharWrapper)serializable).charValue();
        }
        hessian2Input.completeMessage();
        hessian2Input.close();

        // Return strings
        return serializable;
    }
}
