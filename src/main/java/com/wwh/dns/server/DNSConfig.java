package com.wwh.dns.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置文件
 *
 * @author wangwh
 * @date 2024/05/28
 */
public class DNSConfig {
    private static final String CONFIG_FILE = "dns_resolve.conf";

    public static Map<String, String> loadConfigFile(String configFile) {
        Map<String, String> dnsRecords = new HashMap<>();

        if (configFile == null || "".equals(configFile)) {
            System.out.println("Use default profile！");
            configFile = CONFIG_FILE;
        }

        File file = new File(configFile);
        System.out.println("Read configuration file: " + file.getAbsolutePath());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if ("".equals(line) || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    dnsRecords.put(parts[1], parts[0]);
                } else {
                    System.out.println("Wrong configuration, skip: " + line);
                }
            }
            return dnsRecords;
        } catch (IOException e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
        }
        return dnsRecords;
    }
}
