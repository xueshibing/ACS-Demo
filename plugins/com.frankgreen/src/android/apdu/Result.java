package com.frankgreen.apdu;

import com.frankgreen.Util;

import java.util.Arrays;

/**
 * Created by kevin on 5/27/15.
 */
public class Result {
    //    private String command;
    private int size = 0;
    private String command;
    private byte[] data;
    private byte[] code;
    private Exception exception;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Result(String command, int byteCount, byte[] receiveBuffer) {
        if (byteCount >= 2) {
            this.size = byteCount - 2;
        } else {
            size = 0;
        }
        setReceiveBuffer(receiveBuffer);
        this.command = command;
    }

    public Result(String command, Exception exception) {
        this.exception = exception;
        this.command = command;
    }

    public Result() {
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getCode() {
        return code;
    }

    public String getCodeString() {
        return Util.toHexString(this.code);
    }

    public void setCode(byte[] code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return this.code[0] == (byte) 0x90 && this.code[1] == (byte) 0x00;
    }

    public void setReceiveBuffer(byte[] receiveBuffer) {
        if (size > 0) {
            this.code = new byte[2];
            this.code[0] = receiveBuffer[size];
            this.code[1] = receiveBuffer[size + 1];
            this.data = Arrays.copyOf(receiveBuffer, size);
        } else {
            this.code = new byte[2];
            if (receiveBuffer != null && receiveBuffer.length > 2) {
                this.code[0] = receiveBuffer[size];
                this.code[1] = receiveBuffer[size + 1];
            }
            this.data = null;
        }
    }
}