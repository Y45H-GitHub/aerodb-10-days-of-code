# AeroDB - A Simple Storage Engine

AeroDB is a educational storage engine implementation in pure Java, designed to demonstrate the fundamental concepts of database storage systems.

## Overview

A storage engine is the core component of a database system responsible for:
- Managing memory and disk operations
- Organizing data storage and retrieval
- Handling indexes and access methods
- Managing concurrent access and transactions

## Why Build a Storage Engine?

Understanding storage engines helps developers appreciate the complexity behind simple database operations and the engineering decisions that make databases fast and reliable.

## Documentation Structure

This project is documented across multiple focused guides:

### Core Concepts
- **[File System vs Database System](docs/filesystem-vs-database.md)** - Why databases exist and their advantages over simple file storage
- **[Page Organization](docs/page-organization.md)** - How data is organized within pages using slotted page layout
- **[Heap Files](docs/heap-files.md)** - Managing collections of pages efficiently

### Coming Soon
Additional documentation will cover:
- Serialization - Converting Java objects to bytes for storage
- Record Identifiers (RID) - Unique addressing system for records  
- ByteBuffer Management - Efficient byte-level data manipulation
- Project Phases - Development roadmap and implementation phases
- Disk Manager - Low-level disk I/O operations
- Buffer Manager - Memory management and caching
- Access Methods - Indexing and data retrieval strategies

## Project Structure

```
app/src/main/java/org/example/
├── Main.java                    # Entry point
├── buffer/
│   └── BufferManager.java       # Memory management
└── storage/
    ├── HeapFile.java           # Page collection management
    └── Page.java               # Individual page operations
```

## Getting Started

### Prerequisites
- Java 11 or higher
- Gradle 7.0 or higher

### Building the Project
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

## Key Design Principles

1. **Minimize Disk I/O** - The fundamental rule of database performance
2. **Fixed-Size Pages** - Leverage OS and hardware optimizations
3. **Efficient Memory Management** - Smart caching and buffer management
4. **Modular Architecture** - Clean separation of concerns

## Contributing

This is an educational project. Feel free to explore the code and documentation to understand storage engine concepts.

## License

This project is for educational purposes.
