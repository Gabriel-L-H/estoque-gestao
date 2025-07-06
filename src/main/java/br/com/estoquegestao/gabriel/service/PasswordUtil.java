package br.com.estoquegestao.gabriel.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordUtil {
    private static final Argon2 argon = Argon2Factory.create();
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
            throw new RuntimeException("Err in verification password" + e);
        }finally {
            argon.wipeArray(password);
        }
    }

    public static boolean needRehash(String storedHash){
        return argon.needsRehash(storedHash, ITERATIONS, MEMORY_KB, PARALLELISM);
    }
}