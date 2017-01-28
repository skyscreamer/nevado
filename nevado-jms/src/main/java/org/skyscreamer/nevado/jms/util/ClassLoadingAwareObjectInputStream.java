package org.skyscreamer.nevado.jms.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;

/**
 * Copied from ActiveMQ project for addressing http://github.com/skyscreamer/nevado/issues/81
 
 * @author http://activemq.apache.org/maven/5.9.0/apidocs/org/apache/activemq/util/ClassLoadingAwareObjectInputStream.html
 *
 */
public class ClassLoadingAwareObjectInputStream extends ObjectInputStream {
	
    private static final ClassLoader FALLBACK_CLASS_LOADER = ClassLoadingAwareObjectInputStream.class.getClassLoader();

    public ClassLoadingAwareObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    protected Class resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return load(classDesc.getName(), cl);
    }

    protected Class resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class[] cinterfaces = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; i++)
            cinterfaces[i] = load(interfaces[i], cl);

        try {
            return Proxy.getProxyClass(cinterfaces[0].getClassLoader(), cinterfaces);
        } catch (IllegalArgumentException e) {
            throw new ClassNotFoundException(null, e);
        }
    }

    private Class load(String className, ClassLoader cl) throws ClassNotFoundException {
        try {
            return ClassLoading.loadClass(className, cl);
        } catch ( ClassNotFoundException e ) {
            return ClassLoading.loadClass(className, FALLBACK_CLASS_LOADER);
        }
    }

}
