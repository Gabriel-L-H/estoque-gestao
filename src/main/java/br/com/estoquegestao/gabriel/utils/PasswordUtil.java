package br.com.estoquegestao.gabriel.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordUtil {
    private static final Argon2 argon = Argon2Factory.create();
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
    private static final int ITERATIONS  = 3;
    private static final int MEMORY_KB   = 128 * 1024; // 128 MiB
    private static final int PARALLELISM = 1;

    public static String hash(char[] password){
        try{
            return argon.hash(ITERATIONS, MEMORY_KB, PARALLELISM, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            argon.wipeArray(password);
        }
    }

    public static boolean verify(String storedHash, char[] password){
        try{
            return argon.verify(storedHash, password);
        } catch (Exception e) {
            logger.error("Incorrect password");
            throw new RuntimeException("Err in verification password" + e);
        }finally {
            argon.wipeArray(password);
        }
    }

    public static boolean needRehash(String storedHash){
        return argon.needsRehash(storedHash, ITERATIONS, MEMORY_KB, PARALLELISM);
    }
}