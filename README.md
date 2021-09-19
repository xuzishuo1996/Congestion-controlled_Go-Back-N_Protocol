# Computer Networks: Congestion-controlled Go-Back-N Protocol

## I. Brief Intro

Implemented a congestion-controlled Go-Back-N protocol, which is used to transfer a text file from one host to another across an unreliable network. The protocol should be able to handle network errors (packet loss), packet reordering, and duplicate packets. 

The protocol is unidirectional: data will flow in one direction and ACKs in the opposite direction.

The overall architecture is as follow:

![](/home/xuzishuo1996/找工/cs656.png)



## II. Usage

### 1 Build

```
# this command will remove previously generated .class files from target/
$ make clean

# this command will compile java source files from src/ and generate .class files in target/
$ make
```

### 2 Run

Run the script in the following sequence:

​	1) nEmluator

​	2) receiver

​	3) sender 

#### Example:

​	nEmluator - ubuntu1804-002

​	receiver - ubuntu1804-008

​	sender - ubuntu1804-010

```
$ ./nEmulator 50285 ubuntu1804-008 50288 50287 ubuntu1804-010 50286 1 0.2 1
$ ./receiver ubuntu1804-002 50287 50288 ./data/output/output2.txt
$ ./sender ubuntu1804-002 50285 50286 50 ./data/input/input2.txt
```

#### Command Line Arguments

##### 1) nEmulator

        • <emulator's receiving UDP port number in the forward (sender) direction> ,
        • <receiver’s network address> ,
        • <receiver’s receiving UDP port number> ,
        • <emulator's receiving UDP port number in the backward (receiver) direction> ,
        • <sender’s network address> ,
        • <sender’s receiving UDP port number> ,
        • <maximum delay of the link in units of millisecond> ,
        • <packet discard probability>
        • <verbose-mode> (Boolean: Set to 1, the network emulator will output its internal processing, one per line, e.g. receiving Packet seqnum /ACK seqnum , discarding Packet seqnum /ACK seqnum, forwarding Packet seqnum /ACK seqnum ).
##### 2) receiver

```
	1. <hostname for the network emulator>
	2. <UDP port number used by the link emulator to receive ACKs from the receiver>
	3. <UDP port number used by the receiver to receive data from the emulator>
	4. <name of the file into which the received data is written>
```

##### 3) sender

```
1. <host address of the network emulator> (hostname)
2. <UDP port number used by the emulator to receive data from the sender>
3. <UDP port number used by the sender to receive ACKs from the emulator>
4. <timeout interval in units of millisecond>
5. <name of the file to be transferred>
```



### 3 Environment

**Make version**: GNU Make 4.1

**java version**: openjdk version "11.0.10"

**javac version**: javac 11.0.10

**Shell version**: GNU bash, version 4.4.20(1)-release (x86_64-pc-linux-gnu)



### 4 Notes

1 Error messages are printed to the stderr.