package org.example.buffer;

import org.example.storage.HeapFile;
import org.example.storage.Page;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BufferManager {

    private final HeapFile diskManager;
    private final Map<Integer, Page> cachePages;

    private final int maxPages;

    private final Map<Integer, Boolean> dirtyPages;

    public BufferManager(HeapFile diskManager, int maxPages) {
        this.diskManager = diskManager;
        this.maxPages = maxPages;
        this.dirtyPages=new HashMap<>();

        this.cachePages = new LinkedHashMap<>(maxPages, 0.75f, true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Page> eldest) {
                if(size()>BufferManager.this.maxPages){
                    evictPage(eldest.getKey());
                    return true;
                }
                return false;
            }
        };

    }

    public Page getPage(int pageID) throws Exception {
        if(cachePages.containsKey(pageID)){
            return cachePages.get(pageID);
        }else{
            // we need to get that page from heap file
            try {
                Page p = diskManager.readPage(pageID);
                cachePages.put(pageID,p);
                return p;
            }catch (Exception e){
                throw new RuntimeException("Error reading page: "+pageID+".",e);
            }
        }
    }

    public void setPageDirty(int pageID, boolean dirty){
        if(cachePages.containsKey(pageID)){
            dirtyPages.put(pageID, dirty);
        }
    }

    private void evictPage(int pageId){
        if(dirtyPages.containsKey(pageId)){
            Page p  = cachePages.get(pageId);
            if(p!=null){
                diskManager.writePage(p);
                dirtyPages.remove(p.getPageID());
            }
        }
    }

    public void flushAll(){
        for (Integer pageID : cachePages.keySet()){
            evictPage(pageID);
        }
    }

    public int allocateNewPage(){
        int newPageID = diskManager.getNumPages();
        Page p = new Page(newPageID);
        diskManager.writePage(p);
        return newPageID;
    }
}
