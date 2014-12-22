package org.skyscreamer.nevado.jms.message;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import java.io.*;

/**
 * This class borrrows heavily from ActiveMQStreamMessage, Copyright 2004 Protique Ltd,
 * with great appreciation for its creators.  It is also licensed under Apache 2.0.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class NevadoBytesMessage extends NevadoMessage implements BytesMessage {
    transient private Log _log = LogFactory.getLog(NevadoBytesMessage.class);

    private ByteArray _body;

    public NevadoBytesMessage() {}

    protected NevadoBytesMessage(BytesMessage message) throws JMSException {
        super(message);
        message.reset();
        for(int count = 0 ; count < message.getBodyLength() ; ) {
            byte[] buffer = new byte[10240];
            int numRead = message.readBytes(buffer);
            writeBytes(buffer, 0, numRead);
            count += numRead;
        }
    }

    @Override
    public void internalClearBody() throws JMSException {
        _body = null;
        this.dataOut = null;
        this.dataIn = null;
        this.bytesOut = null;
    }

    private transient DataOutputStream dataOut;
    private transient ByteArrayOutputStream bytesOut;
    private transient DataInputStream dataIn;

    public void onSend() {
        super.onSend();
        storeContent();
    }

    private void storeContent() {
        if (dataOut != null) {
            try {
                dataOut.close();
                _body = new ByteArray(bytesOut.toByteArray());
                bytesOut = null;
                dataOut = null;
            } catch (IOException ioe) {
                // Since this is backed by memory, it's rare enough to turn to a runtime
                throw new RuntimeException(ioe);
            }
        }
    }

    /**
     * @param bodyAsBytes The bodyAsBytes to set.
     * @param offset
     * @param length
     */
    public void setBodyAsBytes(byte[] bodyAsBytes,int offset, int length) {
        _body = new ByteArray(bodyAsBytes, offset, length);
        dataOut = null;
        dataIn = null;
        bytesOut = null;
    }

    /**
     * Gets the number of bytes of the message body when the message is in read-only mode. The value returned can be
     * used to allocate a byte array. The value returned is the entire length of the message body, regardless of where
     * the pointer for reading the message is currently located.
     *
     * @return number of bytes in the message
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageNotReadableException if the message is in write-only mode.
     * @since 1.1
     */
    public long getBodyLength() throws JMSException {
        checkWriteOnlyBody();
        return _body.size();
    }

    /**
     * Reads a <code>boolean</code> from the bytes message stream.
     *
     * @return the <code>boolean</code> value read
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public boolean readBoolean() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readBoolean();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads a signed 8-bit value from the bytes message stream.
     *
     * @return the next byte from the bytes message stream as a signed 8-bit <code>byte</code>
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public byte readByte() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readByte();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads an unsigned 8-bit number from the bytes message stream.
     *
     * @return the next byte from the bytes message stream, interpreted as an unsigned 8-bit number
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public int readUnsignedByte() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readUnsignedByte();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads a signed 16-bit number from the bytes message stream.
     *
     * @return the next two bytes from the bytes message stream, interpreted as a signed 16-bit number
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public short readShort() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readShort();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads an unsigned 16-bit number from the bytes message stream.
     *
     * @return the next two bytes from the bytes message stream, interpreted as an unsigned 16-bit integer
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public int readUnsignedShort() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readUnsignedShort();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads a Unicode character value from the bytes message stream.
     *
     * @return the next two bytes from the bytes message stream as a Unicode character
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public char readChar() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readChar();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads a signed 32-bit integer from the bytes message stream.
     *
     * @return the next four bytes from the bytes message stream, interpreted as an <code>int</code>
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public int readInt() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readInt();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads a signed 64-bit integer from the bytes message stream.
     *
     * @return the next eight bytes from the bytes message stream, interpreted as a <code>long</code>
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public long readLong() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readLong();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads a <code>float</code> from the bytes message stream.
     *
     * @return the next four bytes from the bytes message stream, interpreted as a <code>float</code>
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public float readFloat() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readFloat();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads a <code>double</code> from the bytes message stream.
     *
     * @return the next eight bytes from the bytes message stream, interpreted as a <code>double</code>
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public double readDouble() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readDouble();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads a string that has been encoded using a modified UTF-8 format from the bytes message stream.
     * <P>
     * For more information on the UTF-8 format, see "File System Safe UCS Transformation Format (FSS_UTF)", X/Open
     * Preliminary Specification, X/Open Company Ltd., Document Number: P316. This information also appears in ISO/IEC
     * 10646, Annex P.
     *
     * @return a Unicode string from the bytes message stream
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageEOFException         if unexpected end of bytes stream has been reached.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public String readUTF() throws JMSException {
        initializeReading();
        try {
            return this.dataIn.readUTF();
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Reads a byte array from the bytes message stream.
     * <P>
     * If the length of array <code>value</code> is less than the number of bytes remaining to be read from the
     * stream, the array should be filled. A subsequent call reads the next increment, and so on.
     * <P>
     * If the number of bytes remaining in the stream is less than the length of array <code>value</code>, the bytes
     * should be read into the array. The return value of the total number of bytes read will be less than the length
     * of the array, indicating that there are no more bytes left to be read from the stream. The next read of the
     * stream returns -1.
     *
     * @param value the buffer into which the data is read
     * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end of the
     *         stream has been reached
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public int readBytes(byte[] value) throws JMSException {
        return readBytes(value, value.length);
    }

    /**
     * Reads a portion of the bytes message stream.
     * <P>
     * If the length of array <code>value</code> is less than the number of bytes remaining to be read from the
     * stream, the array should be filled. A subsequent call reads the next increment, and so on.
     * <P>
     * If the number of bytes remaining in the stream is less than the length of array <code>value</code>, the bytes
     * should be read into the array. The return value of the total number of bytes read will be less than the length
     * of the array, indicating that there are no more bytes left to be read from the stream. The next read of the
     * stream returns -1.
     * <p/>
     * If <code>length</code> is negative, or <code>length</code> is greater than the length of the array <code>value</code>,
     * then an <code>IndexOutOfBoundsException</code> is thrown. No bytes will be read from the stream for this
     * exception case.
     *
     * @param value  the buffer into which the data is read
     * @param length the number of bytes to read; must be less than or equal to <code>value.length</code>
     * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end of the
     *         stream has been reached
     * @throws JMSException                if the JMS provider fails to read the message due to some internal error.
     * @throws MessageNotReadableException if the message is in write-only mode.
     */
    public int readBytes(byte[] value, int length) throws JMSException {
        initializeReading();
        try {
            int n = 0;
            while (n < length) {
                int count = this.dataIn.read(value, n, length - n);
                if (count < 0) {
                    break;
                }
                n += count;
            }
            if (n == 0 && length > 0) {
                n = -1;
            }
            return n;
        }
        catch (EOFException eof) {
            JMSException jmsEx = new MessageEOFException(eof.getMessage());
            jmsEx.setLinkedException(eof);
            throw jmsEx;
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Format error occured" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Writes a <code>boolean</code> to the bytes message stream as a 1-byte value. The value <code>true</code> is
     * written as the value <code>(byte)1</code>; the value <code>false</code> is written as the value <code>(byte)0</code>.
     *
     * @param value the <code>boolean</code> value to be written
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeBoolean(boolean value) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.writeBoolean(value);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Writes a <code>byte</code> to the bytes message stream as a 1-byte value.
     *
     * @param value the <code>byte</code> value to be written
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeByte(byte value) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.writeByte(value);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Writes a <code>short</code> to the bytes message stream as two bytes, high byte first.
     *
     * @param value the <code>short</code> to be written
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeShort(short value) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.writeShort(value);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Writes a <code>char</code> to the bytes message stream as a 2-byte value, high byte first.
     *
     * @param value the <code>char</code> value to be written
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeChar(char value) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.writeChar(value);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Writes an <code>int</code> to the bytes message stream as four bytes, high byte first.
     *
     * @param value the <code>int</code> to be written
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeInt(int value) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.writeInt(value);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Writes a <code>long</code> to the bytes message stream as eight bytes, high byte first.
     *
     * @param value the <code>long</code> to be written
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeLong(long value) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.writeLong(value);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Converts the <code>float</code> argument to an <code>int</code> using the <code>floatToIntBits</code>
     * method in class <code>Float</code>, and then writes that <code>int</code> value to the bytes message stream
     * as a 4-byte quantity, high byte first.
     *
     * @param value the <code>float</code> value to be written
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeFloat(float value) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.writeFloat(value);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Converts the <code>double</code> argument to a <code>long</code> using the <code>doubleToLongBits</code>
     * method in class <code>Double</code>, and then writes that <code>long</code> value to the bytes message
     * stream as an 8-byte quantity, high byte first.
     *
     * @param value the <code>double</code> value to be written
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeDouble(double value) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.writeDouble(value);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Writes a string to the bytes message stream using UTF-8 encoding in a machine-independent manner.
     * <P>
     * For more information on the UTF-8 format, see "File System Safe UCS Transformation Format (FSS_UTF)", X/Open
     * Preliminary Specification, X/Open Company Ltd., Document Number: P316. This information also appears in ISO/IEC
     * 10646, Annex P.
     *
     * @param value the <code>String</code> value to be written
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeUTF(String value) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.writeUTF(value);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Writes a byte array to the bytes message stream.
     *
     * @param value the byte array to be written
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeBytes(byte[] value) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.write(value);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Writes a portion of a byte array to the bytes message stream.
     *
     * @param value  the byte array value to be written
     * @param offset the initial offset within the byte array
     * @param length the number of bytes to use
     * @throws JMSException                 if the JMS provider fails to write the message due to some internal error.
     * @throws MessageNotWriteableException if the message is in read-only mode.
     */
    public void writeBytes(byte[] value, int offset, int length) throws JMSException {
        initializeWriting();
        try {
            this.dataOut.write(value, offset, length);
        }
        catch (IOException ioe) {
            JMSException jmsEx = new JMSException("Could not write data:" + ioe.getMessage());
            jmsEx.setLinkedException(ioe);
            throw jmsEx;
        }
    }

    /**
     * Writes an object to the bytes message stream.
     * <P>
     * This method works only for the objectified primitive object types (<code>Integer</code>,<code>Double</code>,
     * <code>Long</code> &nbsp;...), <code>String</code> objects, and byte arrays.
     *
     * @param value the object in the Java programming language ("Java object") to be written; it must not be null
     * @throws JMSException                   if the JMS provider fails to write the message due to some internal error.
     * @throws MessageFormatException         if the object is of an invalid type.
     * @throws MessageNotWriteableException   if the message is in read-only mode.
     * @throws java.lang.NullPointerException if the parameter <code>value</code> is null.
     */
    public void writeObject(Object value) throws JMSException {
        if (value == null) {
            throw new NullPointerException();
        }
        initializeWriting();
        if (value instanceof Boolean) {
            writeBoolean(((Boolean) value).booleanValue());
        }
        else if (value instanceof Character) {
            writeChar(((Character) value).charValue());
        }
        else if (value instanceof Byte) {
            writeByte(((Byte) value).byteValue());
        }
        else if (value instanceof Short) {
            writeShort(((Short) value).shortValue());
        }
        else if (value instanceof Integer) {
            writeInt(((Integer) value).intValue());
        }
        else if (value instanceof Double) {
            writeDouble(((Double) value).doubleValue());
        }
        else if (value instanceof Long) {
            writeLong(((Long) value).longValue());
        }
        else if (value instanceof Float) {
            writeFloat(((Float) value).floatValue());
        }
        else if (value instanceof Double) {
            writeDouble(((Double) value).doubleValue());
        }
        else if (value instanceof String) {
            writeUTF(value.toString());
        }
        else if (value instanceof byte[]) {
            writeBytes((byte[]) value);
        }
        else {
            throw new MessageFormatException("Cannot write non-primitive type:" + value.getClass());
        }
    }

    /**
     * Puts the message body in read-only mode and repositions the stream of bytes to the beginning.
     *
     * @throws JMSException if an internal error occurs
     */
    public void reset() throws JMSException {
        storeContent();
        this.bytesOut = null;
        this.dataIn = null;
        this.dataOut = null;
    }

    private void initializeWriting() throws MessageNotWriteableException {
        checkReadOnlyBody();

        if (this.dataOut == null) {
            this.bytesOut = new ByteArrayOutputStream();
            this.dataOut = new DataOutputStream(this.bytesOut);
        }
    }

    private void initializeReading() throws MessageNotReadableException {
        checkWriteOnlyBody();

        if (this.dataIn == null) {
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(_body.toByteArray());
            this.dataIn = new DataInputStream(bytesIn);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NevadoBytesMessage that = (NevadoBytesMessage) o;

        if (_messageID != null ? !_messageID.equals(that._messageID) : that._messageID != null) return false;
        if (_body != null ? !_body.equals(that._body) : that._body != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(_messageID).append(_body).toHashCode();
    }
}
