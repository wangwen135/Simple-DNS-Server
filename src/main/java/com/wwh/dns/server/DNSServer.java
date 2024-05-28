package com.wwh.dns.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Map;

/**
 * DNS服务器<br>
 * 测试用
 *
 * @author wangwh
 * @date 2024/05/28
 */
public class DNSServer {
    private static final int DNS_PORT = 53;
    private String configFile;
    private boolean debug = false;
    private Map<String, String> dnsRecords;

    public DNSServer(String configFile) {
        this.configFile = configFile;
        String enableDebug = System.getProperty("enableDebug");
        if ("true".equalsIgnoreCase(enableDebug)) {
            System.out.println("Enable debug mode！");
            debug = true;
        }
    }

    private void loadDNSRecords() {
        dnsRecords = DNSConfig.loadConfigFile(configFile);

        System.out.println("Configuration file contents:");
        for (Map.Entry<String, String> e : dnsRecords.entrySet()) {
            System.out.println("- " + e.getValue() + "  " + e.getKey());
        }
    }

    public static void main(String[] args) {
        System.out.println("################################");
        System.out.println("####    Simple DNS Server   ####");
        System.out.println("################################");

        String configFilePath = null;

        if (args.length > 0) {
            configFilePath = args[0];
        }

        DNSServer server = new DNSServer(configFilePath);
        server.loadDNSRecords();

        System.out.println("\nStart service ...");

        server.start();
    }

    private void start() {
        try (DatagramSocket socket = new DatagramSocket(DNS_PORT)) {
            byte[] buffer = new byte[512];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                handleRequest(socket, request);
            }
        } catch (SocketException e) {
            System.err.println("Socket Error");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO Error");
            e.printStackTrace();
        }
    }

    private void handleRequest(DatagramSocket socket, DatagramPacket request) throws IOException {
        System.out.print("Receive a DNS request：");
        // Parse DNS query
        byte[] data = request.getData();
        if (debug) {
            System.out.println("\nThe request message：");
            printByte(data);
        }
        DNSMessage query = DNSMessage.parse(data);

        String domain = query.getQuestionDomain();
        System.out.println(domain);

        String responseIP = dnsRecords.get(domain);
        if (responseIP != null) {
            System.out.println("Match from configuration file：" + responseIP);
        } else {
            responseIP = querySystemDNS(domain);
            System.out.println("Return system analysis results：" + responseIP);
        }

        if (responseIP != null) {
            byte[] responseData = buildResponse(query, responseIP);
            DatagramPacket response = new DatagramPacket(responseData, responseData.length, request.getAddress(), request.getPort());
            socket.send(response);
        }
    }

    private String querySystemDNS(String domain) {
        try {
            InetAddress address = InetAddress.getByName(domain);
            return address.getHostAddress();
        } catch (IOException e) {
            System.err.println("System parsing exception：" + e.getMessage());
            return null;
        }
    }

    private byte[] buildResponse(DNSMessage query, String ipAddress) {

        byte[] response = query.buildResponse(ipAddress);
        if (debug) {
            System.out.println("The response message：");
            printByte(response);
        }

        return response;
    }

    public static void printByte(byte[] bytes) {
        System.out.println(bytesToHex(bytes));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
