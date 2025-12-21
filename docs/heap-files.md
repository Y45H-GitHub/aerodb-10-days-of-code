# Heap Files

## Overview

A **Heap File** is the simplest way to store and manage a collection of pages. It's an unordered collection of pages that provides basic storage functionality without any particular ordering or indexing.

## The Problem

Consider a file with 1000 pages where you want to insert new data:

```
[PAGE-0][PAGE-1][PAGE-2][PAGE-3]...[PAGE-999]
```

**Brute Force Approach**: Check each page from 0 to 999 to find one with free space.
- Time Complexity: O(N)
- Inefficient for large files
- Doesn't scale well

## Heap File Solutions

Heap files solve this problem by maintaining metadata about page utilization. There are two main approaches:

### Method A: Linked List Approach

Maintain two separate linked lists within the file:
1. **Full Pages List**: Chain of pages that are completely filled
2. **Free Pages List**: Chain of pages that have available space

#### Structure
```
Header Page
├── Full Pages List Head → Page 5 → Page 12 → Page 23 → NULL
└── Free Pages List Head → Page 3 → Page 7 → Page 15 → NULL
```

#### Operations

**Insert Operation:**
1. Get first page from Free Pages List
2. Insert record into that page
3. If page becomes full, move it to Full Pages List
4. Update list pointers

**Delete Operation:**
1. Delete record from page
2. If page was full and now has space, move to Free Pages List
3. Update list pointers

#### Advantages
- Simple implementation
- Low metadata overhead
- Fast insertion when free pages exist

#### Disadvantages
- May need to scan entire free list to find a page with sufficient space
- No information about how much space each page has
- Fragmentation can lead to inefficient space usage

### Method B: Page Directory Approach

Instead of linked lists, maintain a **Header Page** that acts as a directory containing metadata about every page in the file.

#### Structure
```java
class PageDirectoryEntry {
    int pageId;
    int freeSpaceBytes;
    boolean isAllocated;
}

class HeaderPage {
    int totalPages;
    int nextPageId;
    PageDirectoryEntry[] directory;
}
```

#### Directory Layout
```
Header Page
├── Page 0: 0 bytes free (full)
├── Page 1: 1024 bytes free
├── Page 2: 0 bytes free (full)
├── Page 3: 2048 bytes free
├── Page 4: 512 bytes free
└── ...
```

#### Operations

**Insert Operation:**
1. Scan directory for page with sufficient free space
2. Insert record into selected page
3. Update directory entry with new free space amount

**Delete Operation:**
1. Delete record from page
2. Update directory entry with increased free space

**Page Allocation:**
1. Find empty slot in directory or extend directory
2. Allocate new page
3. Initialize directory entry

#### Advantages
- Know exact free space for each page
- Can select optimal page for insertion
- Better space utilization
- Supports more sophisticated allocation strategies

#### Disadvantages
- Higher metadata overhead
- Directory can become large for files with many pages
- More complex implementation

## Implementation Comparison

### Linked List Implementation

```java
public class LinkedListHeapFile {
    private int freeListHead;
    private int fullListHead;
    
    public PageId insertRecord(Record record) {
        if (freeListHead == -1) {
            // Allocate new page
            PageId newPage = allocateNewPage();
            freeListHead = newPage.pageNum;
        }
        
        Page page = loadPage(freeListHead);
        if (page.insertRecord(record)) {
            if (page.isFull()) {
                // Move to full list
                moveToFullList(freeListHead);
            }
            return new PageId(freeListHead, page.getLastSlotId());
        }
        
        return null; // Insertion failed
    }
}
```

### Page Directory Implementation

```java
public class PageDirectoryHeapFile {
    private HeaderPage headerPage;
    
    public PageId insertRecord(Record record) {
        int requiredSpace = record.getSize();
        
        // Find page with sufficient space
        for (PageDirectoryEntry entry : headerPage.directory) {
            if (entry.freeSpaceBytes >= requiredSpace) {
                Page page = loadPage(entry.pageId);
                if (page.insertRecord(record)) {
                    // Update directory
                    entry.freeSpaceBytes = page.getFreeSpace();
                    return new PageId(entry.pageId, page.getLastSlotId());
                }
            }
        }
        
        // No suitable page found, allocate new one
        return allocateNewPageAndInsert(record);
    }
}
```

## Advanced Heap File Features

### Free Space Tracking Granularity

Instead of just "free" or "full", track free space in ranges:
- **Full**: 0 bytes free
- **Nearly Full**: 1-256 bytes free  
- **Half Full**: 257-2048 bytes free
- **Nearly Empty**: 2049+ bytes free

This allows for more intelligent page selection based on record size.

### Page Allocation Strategies

1. **First Fit**: Use first page with sufficient space
2. **Best Fit**: Use page with least wasted space
3. **Worst Fit**: Use page with most free space (for future insertions)

### Defragmentation

Periodically reorganize pages to:
- Consolidate free space
- Remove deleted records
- Optimize page utilization

## Performance Characteristics

| Operation | Linked List | Page Directory |
|-----------|-------------|----------------|
| Insert (space available) | O(1) | O(N) directory scan |
| Insert (new page needed) | O(1) | O(N) directory scan |
| Delete | O(1) | O(1) |
| Find page with space | O(N) | O(N) |
| Space utilization | Poor | Good |

## Choosing the Right Approach

**Use Linked List when:**
- Simple implementation is preferred
- Low metadata overhead is critical
- Insert patterns are mostly sequential
- File size is relatively small

**Use Page Directory when:**
- Space utilization is important
- Records have varying sizes
- Need to support different allocation strategies
- Can afford higher metadata overhead

## Integration with Buffer Manager

Heap files work closely with the buffer manager:

```java
public class HeapFile {
    private BufferManager bufferManager;
    
    public Page getPage(int pageId) {
        return bufferManager.getPage(this.fileId, pageId);
    }
    
    public void releasePage(Page page, boolean isDirty) {
        bufferManager.releasePage(page, isDirty);
    }
}
```

The heap file provides logical organization while the buffer manager handles physical I/O and caching.

## Conclusion

Heap files provide the foundation for data storage in database systems. While simple in concept, the choice between linked list and page directory approaches significantly impacts performance and space utilization. The page directory approach is generally preferred for production systems due to better space management, despite its higher complexity.