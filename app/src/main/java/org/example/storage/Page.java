package org.example.storage;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Page {
    static final int PAGE_SIZE = 4096;
    private int pageID;
    private byte[] data;

    public Page(byte[] data, int pageID) {
        this.data = data;
        this.pageID = pageID;
        this.data=new byte[PAGE_SIZE];
    }

    public Page(int pageID){
        this.pageID = pageID;
        this.data=new byte[PAGE_SIZE];
    }

    public Page(int pageID, byte[] data){
        this.pageID = pageID;
        if(data.length!=PAGE_SIZE){
            throw new IllegalArgumentException("(ERROR!)Data must be of 4096 bytes");
        }
        this.data=data;
    }

    /**
    * HELPER METHOD TO READ AN INTEGER FROM A SPECIFIC OFFSET
    * OFFSET -
    */
    public void setInt(int offset, int value){
        ByteBuffer.wrap(data).putInt(offset,value);
    }

    public void getInt(int offset){
        ByteBuffer.wrap(data).getInt(offset);
    }

    @Override
    public String toString() {
        return "PageID : " + pageID;
    }

    public int getPageID() {
        return pageID;
    }

    public byte[] getData() {
        return data;
    }

    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
