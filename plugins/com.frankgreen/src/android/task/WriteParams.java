package com.frankgreen.task;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

/**
 * Created by kevin on 6/8/15.
 */
public class WriteParams  extends Params{
    private int slotNumber = 0;
    private String data;
    private byte[] bytes;
    private int block = 4;

    public WriteParams( int slotNumber, int block, String data) {
        this.slotNumber = slotNumber;
        this.block = block;
        this.data = data;
    }

    public WriteParams(int slotNumber, int block , byte[] bytes) {
        this.slotNumber = slotNumber;
        this.bytes = bytes;
        this.block = block;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
