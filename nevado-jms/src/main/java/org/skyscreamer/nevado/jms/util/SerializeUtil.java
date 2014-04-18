package org.skyscreamer.nevado.jms.util;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class SerializeUtil
{
	
    public static Serializable copyOOS( Serializable serializable ) throws IOException
    {
        return deserializeOOS(serializeOOS(serializable));
    }
    
    public static Serializable copy( Serializable serializable ) throws IOException
    {
        return deserialize(serialize(serializable));
    }

    public static String serializeToString( Serializable serializable ) throws IOException
    {
        byte[] data = serialize(serializable);
        return new String( Base64.encodeBase64(data) );
    }
    
    public static String serializeToStringOOS( Serializable serializable ) throws IOException
    {
        byte[] data = serializeOOS(serializable);
        return new String( Base64.encodeBase64(data) );
    }

    public static byte[] serialize( Serializable serializable ) throws IOException {
        // Initialize buffer and converter
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output( byteArrayOutputStream );
        hessian2Output.setSerializerFactory( new SerializerFactory( SerializeUtil.class.getClassLoader() ) );

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
    
    /**
     *  @see https://github.com/skyscreamer/nevado/issues/81
     */
    public static byte[] serializeOOS(Serializable serializable, boolean compress) throws IOException {
  
	ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
	OutputStream os = bytesOut;
	
	if (compress) {
		os = new DeflaterOutputStream(os);
	}
	
	DataOutputStream dataOut = new DataOutputStream(os);
	ObjectOutputStream objOut = new ObjectOutputStream(dataOut);
	objOut.writeObject(serializable);
	objOut.flush();
	objOut.reset();
	objOut.close();
	return bytesOut.toByteArray();

    }

    public static Serializable deserializeFromString(String s) throws IOException
    {
        // Initialize buffer and converter
        byte [] dataBytes = Base64.decodeBase64(s.getBytes("UTF-8"));
        return deserialize(dataBytes);
    }
    
    
    public static Serializable deserializeFromStringOOS(String s) throws IOException
    {
        // Initialize buffer and converter
        byte [] dataBytes = Base64.decodeBase64(s.getBytes("UTF-8"));
        return deserializeOOS(dataBytes);
    }

    public static Serializable deserialize(byte[] dataBytes) throws IOException {
        Hessian2Input hessian2Input = new Hessian2Input( new ByteArrayInputStream(  dataBytes ) );
        hessian2Input.setSerializerFactory( new SerializerFactory( SerializeUtil.class.getClassLoader() ) );

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
    
    /**
     *  @see https://github.com/skyscreamer/nevado/issues/81
     */
    public static Serializable deserializeOOS(byte[] dataBytes, boolean isCompressed) throws IOException {

	InputStream is = new ByteArrayInputStream(dataBytes);
	if(isCompressed) {
		is = new InflaterInputStream(is);
	}
	DataInputStream dataIn = new DataInputStream(is);
	ClassLoadingAwareObjectInputStream objIn = new ClassLoadingAwareObjectInputStream(dataIn);
	try {
	 	return (Serializable) objIn.readObject();
	 	
	} catch (ClassNotFoundException ce) {
		throw new IOException(ce.getMessage());
		
	} finally {
		objIn.close();
		dataIn.close();
	}

    }
}
