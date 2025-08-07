package br.com.estoquegestao.gabriel.utils;

import br.com.estoquegestao.gabriel.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class JwtUtil {
    private static final Dotenv dotenv = Dotenv.configure()
                                        .directory("c:/Users/norag/OneDrive/Documentos/Projetos/estoque-gestao")
                                        .ignoreIfMissing()
                                        .load();
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final Algorithm secretKey = Algorithm.HMAC256(dotenv.get("JWT_SECRET_KEY"));
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
            logger.error("Error in creation the token by user {}", user.getCpf());
            throw new RuntimeException("Error in creation the token: " + e);
        }
    }

    public static DecodedJWT verifyToken(String token){
        try{
            DecodedJWT jwt = verifier.verify(token);
            return jwt;
        }catch (JWTVerificationException e){
            logger.error("Error in token verification");
            throw new RuntimeException("Error in token verification: " + e);
        }
    }

    public static List<String> authenticationToken(DecodedJWT jwt){
        String subject = jwt.getSubject();
        String roles = jwt.getClaim("role").asString();
        Instant expiresAt = jwt.getExpiresAt().toInstant();
        Instant now = Instant.now();

        if(expiresAt.isBefore(now)){
            logger.error("Token expired");
            throw new RuntimeException("Token expired at: " + expiresAt);
        }

        if(!"https://localhost:3306/estoque_vendas".equals(jwt.getIssuer())){
            logger.error("Invalid issuer");
            throw new RuntimeException("Invalid issuer: " + jwt.getIssuer());
        }

        return List.of(subject, roles);
    }
}