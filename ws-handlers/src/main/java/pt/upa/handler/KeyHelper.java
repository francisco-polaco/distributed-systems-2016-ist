package pt.upa.handler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by xxlxpto on 05-05-2016.
 */
public class KeyHelper {
    public static void write(String publicKeyPath, String privateKeyPath) throws Exception {

        // generate RSA key pair
        System.out.println("Generating RSA keys ...");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();

//        System.out.println("Public key info:");
//        System.out.println("algorithm: " + key.getPublic().getAlgorithm());
//        System.out.println("format: " + key.getPublic().getFormat());

        System.out.println("Writing public key to " + publicKeyPath + " ...");
        byte[] pubEncoded = key.getPublic().getEncoded();
        writeFile(publicKeyPath, pubEncoded);

        System.out.println("---");

//        System.out.println("Private key info:");
//        System.out.println("algorithm: " + key.getPrivate().getAlgorithm());
//        System.out.println("format: " + key.getPrivate().getFormat());

        System.out.println("Writing private key to '" + privateKeyPath + "' ...");
        byte[] privEncoded = key.getPrivate().getEncoded();
        writeFile(privateKeyPath, privEncoded);
    }

    public static KeyPair read(String publicKeyPath, String privateKeyPath) throws Exception {

        System.out.println("Reading public key from file " + publicKeyPath + " ...");
        byte[] pubEncoded = readFile(publicKeyPath);

        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubEncoded);
        KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
        PublicKey pub = keyFacPub.generatePublic(pubSpec);
        System.out.println(pub);

        System.out.println("---");

        System.out.println("Reading private key from file " + privateKeyPath + " ...");
        byte[] privEncoded = readFile(privateKeyPath);

        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
        KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
        PrivateKey priv = keyFacPriv.generatePrivate(privSpec);

        System.out.println(priv);
        System.out.println("---");

        KeyPair keys = new KeyPair(pub, priv);
        return keys;
    }

    private static void writeFile(String path, byte[] content) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content);
        fos.close();
    }

    private static byte[] readFile(String path) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] content = new byte[fis.available()];
        fis.read(content);
        fis.close();
        return content;
    }
}
