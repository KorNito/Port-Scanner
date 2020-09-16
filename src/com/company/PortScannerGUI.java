package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PortScannerGUI {
    private JButton buttonScan;
    private JPanel panelMain;
    private JTextField textFieldIP;
    private JTextField textFieldPort;
    private JTextArea textAreaOutput;
    private JLabel IPvalidator;
    private JLabel Portvalidator;
    private JButton buttonScanFromFileButton;

    public PortScannerGUI() {

        buttonScan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textAreaOutput.setText("");

                String ipAddress = textFieldIP.getText();
                String port = textFieldPort.getText();

                if (!validateIP(textFieldIP) && !validatePort(textFieldPort)) {
                    int convertedPort = Integer.parseInt(port);

                    try {
                        pingIP(ipAddress);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } finally {
                        try {
                            connectToServerWithPort(ipAddress, convertedPort);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }
        });

        buttonScanFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File inputFile = new File("IPsandPorts.txt");
                    Scanner scanner = new Scanner(inputFile);
                    FileWriter outputFile = new FileWriter("Results.txt");

                    while (scanner.hasNext()) {
                        String ipAddress = scanner.next();
                        String port = scanner.next();

                        int convertedPort = Integer.parseInt(port);
                        textAreaOutput.setText("");

                        scanFromFile(ipAddress, convertedPort, outputFile);
                    }
                    JOptionPane.showMessageDialog(null, "Results are saved in Results.txt file");
                    scanner.close();
                    outputFile.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "IPsandPorts.txt file does not exist");
                }
            }
        });
    }

    public boolean pingIP (String ipAddress) throws IOException {
        InetAddress address = InetAddress.getByName(ipAddress);

        textAreaOutput.append("Pinging " + ipAddress + "\n");

        if (address.isReachable(3000)) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean connectToServerWithPort (String ipAddress, int port) throws IOException {
        Socket socket;

        textAreaOutput.append("Connecting to port " + port + "\n");

        try {
            socket = new Socket(ipAddress, port);
            textAreaOutput.append(port + " is open\n");
            socket.close();
            return true;
        } catch (Exception e) {
            textAreaOutput.append(port + " is closed\n");
            return false;
        }
    }

    public void scanFromFile (String ipAddress, int port, FileWriter file) throws UnknownHostException, IOException
    {
        boolean scan = true;
        InetAddress address = InetAddress.getByName(ipAddress);
        Socket socket = null;

        while(scan){
            if (address.isReachable(4000)) {
                file.write(ipAddress + " is reachable" + " ");

                try {
                    socket = new Socket(ipAddress, port);

                    file.write(port + " is open" + " \n");

                    try {
                        socket.close();
                    } catch(IOException e) {
                        System.out.println(e);
                    }
                } catch (IOException e) {
                    file.write(port + " is closed" + " \n");
                } finally {
                    if (socket != null) {
                        socket.close();
                    }
                }
            } else {
                file.write(ipAddress + " is unreachable" + "\n");
            }

            scan = false;
        }
    }

    public boolean validateIP(JTextField textFieldIP){
        if(textFieldIP.getText().trim().isEmpty()){
            IPvalidator.setText("IP address is empty");
            return true;
        } else {
            IPvalidator.setText("");
            return false;
        }
    }

    public boolean validatePort(JTextField textFieldPort){
        if(textFieldPort.getText().trim().isEmpty()){
            Portvalidator.setText("Port is empty");
            return true;
        } else if (Integer.parseInt(textFieldPort.getText()) < 0 || Integer.parseInt(textFieldPort.getText()) > 65535) {
            Portvalidator.setText("Port range must be between 0 and 65535");
            return true;
        } else {
            Portvalidator.setText("");
            return false;
        }
    }

    public static void main (String[] args) {
        JFrame frame = new JFrame("Port Scanner");
        frame.setContentPane(new PortScannerGUI().panelMain);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
