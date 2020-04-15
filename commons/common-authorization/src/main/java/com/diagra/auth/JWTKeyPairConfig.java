package com.diagra.auth;

import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;


import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Configuration
@PropertySource("classpath:application-auth.properties")
public class JWTKeyPairConfig {

    public static final String ALGORITHM = "RSA";
    private final String publicKeyPath;
    private final String privateKeyPath;

    public JWTKeyPairConfig(@Value("${publicKeyPath}") String publicKeyPath, @Value("${privateKeyPath}") String privateKeyPath) {
        this.publicKeyPath = publicKeyPath;
        this.privateKeyPath = privateKeyPath;
    }

    @Bean
    public KeyPair keyPair() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        final ResourceLoader resourceLoader = new DefaultResourceLoader();
        PemReader publicKey = new PemReader(new FileReader(resourceLoader.getResource(publicKeyPath).getFile()));
        PemReader privateKey = new PemReader(new FileReader(resourceLoader.getResource(privateKeyPath).getFile()));
        X509EncodedKeySpec specPB = new X509EncodedKeySpec(publicKey.readPemObject().getContent());
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        PKCS8EncodedKeySpec specPR = new PKCS8EncodedKeySpec(privateKey.readPemObject().getContent());
        return new KeyPair(kf.generatePublic(specPB), kf.generatePrivate(specPR));
    }


}
