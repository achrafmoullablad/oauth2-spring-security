package com.achrafmoullablad.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

public class GenerateKayPair {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		var keyPair = keyPairGenerator.generateKeyPair();
		
		byte[] pub = keyPair.getPublic().getEncoded();
		byte[] pri = keyPair.getPrivate().getEncoded();
		
		String resourcesPath = "src/main/resources";
        String directoryName = resourcesPath + "/certs";
        createDirectory(directoryName);
        
        String publicFileName = directoryName + "/public.pem";
        String privateFileName = directoryName + "/private.pem";
        String content = "This is a newly added file.";
		
		PemWriter pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(publicFileName)));
		PemObject pemObject = new PemObject("PUBLIC KEY", pub);
		pemWriter.writeObject(pemObject);
		writeFile(publicFileName, content);
		pemWriter.close();
		
		PemWriter pemWriter2 = new PemWriter(new OutputStreamWriter(new FileOutputStream(privateFileName)));
		PemObject pemObject2 = new PemObject("PRIVATE KEY", pri);
		pemWriter2.writeObject(pemObject2);
		writeFile(privateFileName, content);
		pemWriter2.close();
	}
	
	public static void createDirectory(String directoryName) {
        File directory = new File(directoryName);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created successfully: " + directoryName);
            } else {
                System.out.println("Failed to create directory: " + directoryName);
            }
        } else {
            System.out.println("Directory already exists: " + directoryName);
        }
    }

    public static void writeFile(String fileName, String content) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
            System.out.println("File written successfully: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
