package org.example.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class HeapFile {

    private File file;
    private RandomAccessFile randomAccessFile;

    public HeapFile(File f) throws Exception {
        this.file=f;
        try{
            this.randomAccessFile = new RandomAccessFile(f, "rw");
        }catch (FileNotFoundException e){
            throw new FileNotFoundException("Could Not OPEN FILE: "+f.getPath());
        }catch (Exception e){
            throw new Exception("Error occurred");
        }
    }

    /**
     * READ PAGE FUNCTION  - TO READ SPECIFIC PAGE FROM THE DISK
     */
    public Page readPage(int pageID) throws Exception {
        Page p = new Page(pageID);
        int offset = pageID * Page.PAGE_SIZE;
        try{
            if(offset+Page.PAGE_SIZE > randomAccessFile.length()){
                throw new IllegalArgumentException("PAGE SIZE EXCEEDED");
            }
            randomAccessFile.seek(offset);
            randomAccessFile.readFully(p.getData());
            return p;
        }catch (IOException e){
            throw new RuntimeException("Error reading page: "+pageID,e);
        }
    }
    /**
     * WRITE A PAGE TO HEAP FILE
     */
    public void writePage(Page p){
        int offset = p.getPageID()*Page.PAGE_SIZE;
        try {
            randomAccessFile.seek(offset);
            randomAccessFile.write(p.getData());
        }catch (IOException e){
            throw new RuntimeException("Error writing page: "+p.getPageID(), e);
        }
    }

    /**
     * FUNCTION TO RETURN NUMBER OF PAGES CURRENTLY IN THE FILE
     */
    public int getNumPages(){
        try{
            return (int)(randomAccessFile.length()/Page.PAGE_SIZE);
        }catch (IOException e){
            throw new RuntimeException("Error reading file",e);
        }
    }
}
