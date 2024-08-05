# file-storage-service
Implementation of a REST file storage service: 
- Files are stored on disk, in the file system. 
- File names can be 1-64 characters long and restricted to character set: a-zA-Z0-9_- 
- Solution must support many files: at least 10.000.000 files, assume a huge disk at your disposal 
- REST API must support the following operations: 
	+ File access: create, read, update, delete --> file is always identified by name 
	+ File enumeration: return file names matching a regexp 
	+ Size: return number of files in the storage 
	* Optimize file access operations, file enum and size can be slow. 
- Meaningful test coverage

Constraints: 
- Use Java 
- 3rd party libraries are ok for utils, DON'T use an existing database / storage library.

Evaluation criteria (in ascending order): 
0) Correctness - does the solution work? 
1) Code clarity / structure 
2) Performance
