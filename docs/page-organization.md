# Page Organization

## Slotted Page Layout

Once we've organized data into fixed-size pages, we need an efficient way to organize data within each page. AeroDB uses the **Slotted Page Layout**, which provides flexibility and efficient space utilization.

## Page Structure

```
[PAGE HEADER][SLOT DIRECTORY][FREE SPACE][ROW DATA]
```

### The Parking Lot Analogy

Think of a page as a parking lot with one entry/exit at the top:

- **Page Header**: Valet booth storing metadata (cars parked, free spaces, lot ID)
- **Slot Directory**: Key chain tracking which car is in which spot
- **Free Space**: Available parking spaces
- **Row Data**: The actual cars (data records)

Cars are parked from the end of the lot, filling toward the entry/exit.

## Component Details

### Page Header
Contains essential metadata about the page:

```java
class PageHeader {
    int pageId;           // Unique page identifier
    int numSlots;         // Number of slots in use
    int freeSpaceStart;   // Offset where free space begins
    int freeSpaceEnd;     // Offset where free space ends
    int nextPage;         // Link to next page (for chaining)
    int prevPage;         // Link to previous page
}
```

### Slot Directory
An array of slot entries, each containing:

```java
class SlotEntry {
    int offset;    // Byte offset to the record in the page
    int length;    // Length of the record in bytes
    boolean isDeleted; // Deletion flag
}
```

The slot directory grows from the beginning of the page toward the middle.

### Free Space
The area between the slot directory and row data. This space can be used for:
- New slot entries
- New record data
- Expanding existing records

### Row Data
Actual record data stored from the end of the page toward the middle. Records are stored in reverse order to minimize fragmentation.

## Page Layout Diagram

```
+------------------+ <- Page Start (0)
| Page Header      |
+------------------+
| Slot 0          | -> Points to Record C
+------------------+
| Slot 1          | -> Points to Record A  
+------------------+
| Slot 2          | -> Points to Record B
+------------------+
| ...             |
+------------------+
|                 |
|   FREE SPACE    |
|                 |
+------------------+
| Record B        | <- Row Data grows upward
+------------------+
| Record A        |
+------------------+
| Record C        |
+------------------+ <- Page End (4096)
```

## Operations

### Insert Operation
1. Check if page has enough free space
2. Add new slot entry in slot directory
3. Write record data at the end of existing data
4. Update page header metadata

### Delete Operation
1. Mark slot as deleted (tombstone)
2. Optionally compact page to reclaim space
3. Update page header

### Update Operation
- **Same size**: Overwrite in place
- **Smaller size**: Overwrite and mark extra space as free
- **Larger size**: Delete old record and insert new one

## Advantages of Slotted Page Layout

### 1. Flexible Record Sizes
Unlike fixed-size slots, records can be of varying lengths.

### 2. Efficient Space Utilization
Free space is consolidated in the middle, making it easy to allocate.

### 3. Stable Record Addressing
Records are addressed by slot number, not physical offset. This allows records to move within the page without changing their logical address.

### 4. Easy Compaction
When space becomes fragmented, records can be moved to eliminate gaps while maintaining their slot assignments.

### 5. Deletion Handling
Deleted records leave tombstones in slots, allowing for easy space reclamation.

## Implementation Considerations

### Page Size Selection
- **4KB**: Most common, matches OS page size
- **8KB**: Better for larger records
- **16KB+**: Reduces page overhead but increases I/O cost

### Slot Directory Growth
The slot directory must not collide with row data. Implementation should:
- Monitor free space carefully
- Trigger page splits when space is exhausted
- Consider compaction before splitting

### Record Alignment
Records should be aligned to word boundaries (4 or 8 bytes) for optimal CPU access.

## Java Implementation Example

```java
public class SlottedPage {
    private ByteBuffer buffer;
    private PageHeader header;
    private List<SlotEntry> slots;
    
    public boolean insertRecord(byte[] data) {
        if (!hasSpace(data.length)) {
            return false;
        }
        
        // Add slot entry
        SlotEntry slot = new SlotEntry();
        slot.offset = header.freeSpaceEnd - data.length;
        slot.length = data.length;
        slots.add(slot);
        
        // Write data
        buffer.position(slot.offset);
        buffer.put(data);
        
        // Update header
        header.numSlots++;
        header.freeSpaceEnd -= data.length;
        
        return true;
    }
    
    private boolean hasSpace(int recordSize) {
        int slotSize = 8; // Size of one slot entry
        int freeSpace = header.freeSpaceEnd - header.freeSpaceStart;
        return freeSpace >= (recordSize + slotSize);
    }
}
```

## Performance Characteristics

- **Insert**: O(1) if space available
- **Delete**: O(1) for marking, O(n) for compaction
- **Update**: O(1) for same size, O(n) for size changes
- **Search**: O(n) within page (requires scanning slots)

The slotted page layout provides an excellent balance of flexibility, efficiency, and simplicity, making it the standard choice for most database systems.