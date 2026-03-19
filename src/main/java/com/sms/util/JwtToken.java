package com.sms.util;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;
@Component
public class JwtToken {
    @Value("${private.key.path}")
    private String PRIVATE_KEY;
    @Value("${public.key.path}")
    private String PUBLIC_KEY;

    public String getJwtToken(String audience, String subject, String role) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Instant now = Instant.now();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 5);
        long nextYearInMilliseconds = cal.getTimeInMillis();
        //long NO_OF_DAYS=1000*60*60*24;
        long NO_OF_DAYS = 1000*60*60*24;
        String jwtToken= Jwts.builder()
                .setIssuer("Medha Pro")
                .setAudience(audience)
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + NO_OF_DAYS))
                .signWith(getPrivateKey())
                .compact();

        return jwtToken;
    }

   /* public boolean isValidJWT(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try{
            Claims claims = Jwts.parser()
                    .verifyWith(getPublicKey())
                    .build().parseSignedClaims(token).getPayload();
        }
        catch (MalformedJwtException | ExpiredJwtException e){
            return false;
        }
        return true;
    }*/
   public boolean isValidJWT(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
       try {
           Claims claims = Jwts.parser()
                   .verifyWith(getPublicKey())
                   .build().parseSignedClaims(token).getPayload();
           return true;
       } catch (ExpiredJwtException e) {
           throw new ExpiredJwtException(null, null, "Session is expired");
       } catch (MalformedJwtException | IllegalArgumentException e) {
           throw new IllegalArgumentException("You are not authorized to access this");
       }
   }

    public RSAPublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File file1= new File(PUBLIC_KEY);
        //System.out.println("Publickey path"+file1.getAbsolutePath());
        FileInputStream fis1 = new FileInputStream(file1);
        DataInputStream dis1= new DataInputStream(fis1);
        byte[] keyBytes1= new byte[(int) file1.length()];
        dis1.readFully(keyBytes1);
        dis1.close();
        X509EncodedKeySpec spec1 = new X509EncodedKeySpec(keyBytes1);
        KeyFactory kf1= KeyFactory.getInstance("RSA");
        RSAPublicKey pubKey= (RSAPublicKey) kf1.generatePublic(spec1);
        return pubKey;
    }
    public RSAPrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        File file1= new File(PRIVATE_KEY);
        FileInputStream fis1 = new FileInputStream(file1);
        DataInputStream dis1= new DataInputStream(fis1);
        byte[] keyBytes1= new byte[(int) file1.length()];
        dis1.readFully(keyBytes1);
        dis1.close();
        PKCS8EncodedKeySpec spec1 = new PKCS8EncodedKeySpec(keyBytes1);
        KeyFactory kf1= KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKey= (RSAPrivateKey) kf1.generatePrivate(spec1);
        return privateKey;
    }
    /*public static ResponseEntity<Object> isAuthorized(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if(!JwtToken.isValidJWT(token.substring(7).trim())){
            return new ResponseEntity<>("You are not authorized to access this ", HttpStatus.UNAUTHORIZED);
        }
    }*/
}
