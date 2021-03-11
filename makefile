# define a variable for java compiler
JC = javac

SRC_DIR = src
OUT_DIR = target

# define a variable for java compiler flags. -g:generating all debugging info.
JFLAGS = -d $(OUT_DIR) -sourcepath $(SRC_DIR) -classpath $(OUT_DIR)

emulator_src = $(SRC_DIR)/emulator/Emulator.java
receiver_src = $(SRC_DIR)/receiver/Receiver.java
sender_src = $(SRC_DIR)/sender/Sender.java

all: Emulator Receiver Sender scripts

Emulator: $(emulator_src)
	$(JC) $(JFLAGS) $(emulator_src)

Receiver: $(receiver_src)
	$(JC) $(JFLAGS) $(receiver_src)

Sender: $(sender_src)
	$(JC) $(JFLAGS) $(sender_src)

scripts:
	chmod +x nEmulator
	chmod +x receiver
	chmod +x sender

# .PHONY tells makefile to treat clean as a command
.PHONY: clean
clean:
	rm -rf $(OUT_DIR)


#build: Constant Packet UDPUtility MyFileReaderBytes Emulator Receiver Sender
#
#CONSTANT_SRC = $(SRC_DIR)/Constant.java
#PACKET_SRC = $(SRC_DIR)/Packet.java
#UDPUtility_SRC = $(SRC_DIR)/UDPUtility.java
#MyFileReaderBytes_SRC = $(SRC_DIR)/MyFileReaderBytes.java
##EMULATOR_SRC = $(SRC_DIR)/emulator.Emulator.java
#
## -d: Specify where to place generated class files
#
#Constant: $(CONSTANT_SRC)
#	$(JC) $(JFLAGS) -d $(OUT_DIR) $(CONSTANT_SRC)
#Packet: Constant $(PACKET_SRC)
#	$(JC) $(JFLAGS) -d $(OUT_DIR) $(PACKET_SRC)
#UDPUtility: $(UDPUtility_SRC)
#	$(JC) $(JFLAGS) -d $(OUT_DIR) $(UDPUtility_SRC)
#MyFileReaderBytes: $(MyFileReaderBytes_SRC)
#	$(JC) $(JFLAGS) -d $(OUT_DIR) $(MyFileReaderBytes_SRC)

#emulator.Emulator: $(EMULATOR_SRC)
#	$(JC) $(JFLAGS) -d $(OUT_DIR) $(EMULATOR_SRC)

#CLIENT_SRC = $(SRC_DIR)/Client.java
#Client: $(CLIENT_SRC)
#	$(JC) $(JFLAGS) -d $(OUT_DIR) $(CLIENT_SRC)

## .PHONY tells makefile to treat clean as a command
#.PHONY: clean
#clean:
#	rm -rf $(OUT_DIR)