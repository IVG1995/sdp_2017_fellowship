package communication;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import vision.gui.SDPConsole;

import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Created by Simon Rovder
 */

public class SerialPortWrapper implements SerialPortEventListener {

    private SerialPort port = null;
    private SDPPort creator;
    private StringBuilder builder;
    private int seqNum = 0;
    private int windowSize = 16;
    private int maxCMD = 99;
    private int currentlyWaiting = 0;
    private ArrayList<String> savedCommands;
    private Queue<Integer> window;
    private final Object windowLock = new Object();

    public SerialPortWrapper(SDPPort creator) {
        this.creator = creator;
        this.builder = new StringBuilder();
        savedCommands = new ArrayList<String>(maxCMD + 1);
        window = new LinkedList<Integer>();
    }

    public void close() throws SerialPortException {
        if (this.port == null) return;
        this.port.closePort();
    }

    public void setSerialPort(SerialPort port) {
        if (port == null) return;
        try {
            if (this.port != null) this.port.removeEventListener();
            this.port = port;
            this.port.addEventListener(this);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String command) {

        String orgComm = command;
        String commandNoArgs;
        int splitIndx = command.indexOf(' ');
        if (splitIndx > 0) {
            commandNoArgs = command.substring(0, splitIndx);
        } else {
            commandNoArgs = command;
        }
        if (currentlyWaiting < windowSize) {
            if (seqNum > maxCMD) {
                seqNum = 0;
            }
            if (seqNum < maxCMD + 1) {
                int checkLength = commandNoArgs.length();
                String temp = Integer.toString(seqNum) + " " + Integer.toString(checkLength) + " ";
                command = temp + command;
                savedCommands.add(seqNum, command);
            }
            if (this.port == null) {
                return;
            }
            try {
                SDPConsole.writeln(command); //orgComm for pretty
                this.port.writeBytes((command + "\n").getBytes());
                currentlyWaiting++;
                synchronized (windowLock) {
                    window.add(seqNum);
                }
                seqNum++;
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Thread.sleep(100);
                sendCommand(orgComm);
            } catch (Exception e) {
                SDPConsole.writeln("Thread failed to sleep, window was full...");
            }
        }
    }

    private void nextString(String s) {

        int ackSeqNum;
        int removeCount = 0;
        Boolean acked = false;
        Boolean resend = true;
        String command;
        if (s.length() < 5) {
            try {
                ackSeqNum = Integer.parseInt(s);

                for (Integer sNum : window) {
                    removeCount++;
                    //SDPConsole.writeln("RDT: checking " + sNum + " against received" + ackSeqNum);
                    if (ackSeqNum == sNum) {
                        //SDPConsole.writeln("RDT: " + ackSeqNum + " was acknowledged");
                        acked = true;
                        break;
                    }
                }

                if (acked == true) {
                    for (int i = 0; i < removeCount; i++) {
                        //SDPConsole.writeln("RDT: Removing " + window.element());
                        window.poll();
                        currentlyWaiting--;
                    }
                    //SDPConsole.writeln("RDT: Removed " + removeCount + "packets from window");
                } else {
                    if (ackSeqNum == 99) {
                        ackSeqNum = -1;
                    }
                    if (currentlyWaiting > 0 && ackSeqNum >= (window.element() - 1)) {
                        for (Integer sNum : window) {
                            command = savedCommands.get(sNum);
                            SDPConsole.writeln("RDT is Resending: " + command);
                            this.port.writeBytes((command + "\n").getBytes());
                        }
                    }
                }
            } catch (SerialPortException spe) {
                spe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.creator.receivedStringHandler(s);
        }
    }

    private void nextPacket(String s) {
        this.nextString(s);
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                byte buffer[] = this.port.readBytes();
                for (byte b : buffer) {
                    if (b == '\r' || b == '\n') {
                        if (this.builder.length() > 0) {
                            String toProcess = this.builder.toString();
                            synchronized (windowLock) {
                                nextString(toProcess);
                            }
                            this.builder.setLength(0);
                        }
                    } else {
                        this.builder.append((char) b);
                    }
                }
            } catch (SerialPortException ex) {
                ex.printStackTrace();
                System.out.println("serialEvent");
            }
        }
    }
}
