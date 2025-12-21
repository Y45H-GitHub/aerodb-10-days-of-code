package org.example.storage;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Page {

    public static final int PAGE_SIZE = 4096; // Standard 4KB
    private int pageID;
    private byte[] data;

    // CONSTRUCTOR 1: Creating a brand new, empty page
    public Page(int pageID) {
        this.pageID = pageID;
        this.data = new byte[PAGE_SIZE]; // 4KB of empty zeros
    }

    // CONSTRUCTOR 2: Loading an existing page from disk
    public Page(int pageID, byte[] data) {
        this.pageID = pageID;
        if (data.length != PAGE_SIZE) {
            throw new IllegalArgumentException("ERROR: Page must be exactly 4096 bytes");
        }
        this.data = data; // Keep the data we passed in!
    }

    /**
     * WRITE AN INTEGER
     * Helper to write 4 bytes at a specific location.
     * Example: setInt(0, 5) writes the Page ID at the very top.
     */
    public void setInt(int offset, int value) {
        // Wrap creates a "view" of the array, puts the int, and updates the original array
        ByteBuffer.wrap(data).putInt(offset, value);
    }

    /**
     * READ AN INTEGER
     * Helper to read 4 bytes from a location.
     */
    public int getInt(int offset) {
        return ByteBuffer.wrap(data).getInt(offset);
    }

    // --- Getters and Setters ---
    public int getPageID() { return pageID; }
    public byte[] getData() { return data; }
}