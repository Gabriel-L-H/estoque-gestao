package br.com.estoquegestao.gabriel.config;

import io.github.cdimascio.dotenv.Dotenv;

public class KeyJWT {
    private static final Dotenv dotenv = Dotenv.configure()
                                        .ignoreIfMissing()
                                        .load();

    public static String getJwtSecretKey() {
        return dotenv.get("JWT_SECRET_KEY");
    }
}