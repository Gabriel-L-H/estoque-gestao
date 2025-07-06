package br.com.estoquegestao.gabriel.service;

import br.com.estoquegestao.gabriel.config.KeyJWT;
import br.com.estoquegestao.gabriel.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final Algorithm secretKey = Algorithm.HMAC256(KeyJWT.getJwtSecretKey());
    private static final JWTVerifier verifier = JWT.require(secretKey)
                                                .withIssuer("https://localhost:3306/estoque_vendas")
                                                .build();

    public static String token(User user){
        try{
            return JWT.create()
                    .withIssuer("https://localhost:3306/estoque_vendas")
                    .withSubject(user.getCpf())
                    .withClaim("role", String.join(",", user.getRole()))
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 1800000))
                    .sign(secretKey);
        } catch (RuntimeException e) {
            logger.error("Err in creation the token by user {}", user.getCpf());
            throw new RuntimeException("Err in creation the token: " + e);
        }
    }

    public static DecodedJWT verifyToken(String token){
        try{
            DecodedJWT jwt = verifier.verify(token);
            return jwt;
        }catch (JWTVerificationException e){
            logger.error("Err in token verification");
            throw new RuntimeException("Err in token verification: " + e);
        }
    }
}