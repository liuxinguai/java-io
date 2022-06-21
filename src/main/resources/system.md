##VFS: 虚拟文件系统
1. VFS目录树：节点映射到不同的物理位置，不同的存储介质
   1. FD：文件描述符
      1. inode：
      2. pageCache：4K
      3. dirty page: 该页值已经被修改
      4. flush: page到物理磁盘上
   2. 管道：| 两侧的命令 linux操作系统会启动两个子进程去执行分别的任务然后通过管道将两个进程的输入和输出对接起来
2. MappedByteBuffer:会直接映射到系统内核中的pageCache，当往这块内存写入数据时不需要经过内核，而是由java进程直接往内核中的pageCache写入缓存
3. Socket:四元组：<cip,cport,sip,sport>
   1. 当客户端与服务器端进行三次握手以后，会分别在客户端机器和服务端机器kernel上建立两个socket
   2. cip：客户端IP，cpot：客户端port，sip：服务器端ip，sport：服务器端port
   3. 服务器端与某个客户端建立了socket时，会为这个socket分配对应的缓存区（读写数据）
   4. 即使监听服务器端口的进程没有处理客户端的数据，内核也同样会接受客户端发送过来的数据，当监听程序开始处理客户端数据时，内核会开辟FD，供监听程序读取和写入socket网络数据
4. tcp层面上keeplive表明连接通道不关闭，即两端会发送心跳包来维持连接通道不关闭，保证双方都活着，即socket四元组不关闭
5. java ServerSocket监听的整个过程：
   1. system call socket()=fd1
   2. bind(fd1,prot)
   3. listen(fd1)
   4. accept(fd1)--->接受成功获取到fd2