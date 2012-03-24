package org.skyscreamer.nevado.jms.util;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class SerializeStringUtil
{
    public static String serialize( Serializable... serializables ) throws IOException
    {
        // Initialize buffer and converter
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output( byteArrayOutputStream );

        // Serialize objects
        hessian2Output.startMessage();
        hessian2Output.writeInt(serializables.length);
        for(Serializable serializable : serializables) {
            hessian2Output.writeObject( serializable );
        }
        hessian2Output.completeMessage();
        hessian2Output.close();

        // Return objects serialized as string
        return new String( Base64.encodeBase64(byteArrayOutputStream.toByteArray()) );
    }

    public static Object[] deserialize( String s ) throws IOException
    {
        // Initialize buffer and converter
        byte [] dataBytes = Base64.decodeBase64(s);
        Hessian2Input hessian2Input = new Hessian2Input( new ByteArrayInputStream(  dataBytes ) );

        // Convert
        hessian2Input.startMessage();
        int length = hessian2Input.readInt();
        Object[] objects = new Object[length];
        for(int i = 0 ; i < length ; ++i) {
            objects[i] = hessian2Input.readObject();
        }
        hessian2Input.completeMessage();
        hessian2Input.close();

        // Return strings
        return objects;
    }
}
