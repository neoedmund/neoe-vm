# neoe-vm
    What is it? 

Like JVM, It's a virtual machine. It has a set of bytecode.

    What special is it? 

The vm is designed for multi-thread in nature. It has no stack or registers. and every instrument is an atomic operation. so it cost nothing when context switching.

    How fast is it? 

Well, it is about 20x - 100x times slower than Java JVM or C on x86. It is reasonable for interpreted ones. I is an experimental VM just for my joy and curious. there is a benchmark

    What plan is it in the future? 

It was done, and I saw the result. No plan at this time.

    Why make it opensource? 

Let someone interested in such topic to have some story to read :)

    Will the VM rewrite in C or C++ for better performance? 

No. I rewrote the interpreter using JNI in C++ and performance 2x times faster. But other test shows C C++ is not very faster than Java.
Terms - Privacy - Project Hosting Help
Powered by Google Project Hosting 
