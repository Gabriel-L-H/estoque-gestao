package br.com.estoquegestao.gabriel.service;

import br.com.estoquegestao.gabriel.dao.UserDAO;
import br.com.estoquegestao.gabriel.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {
    private final UserDAO userDAO;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void signup(User user, char[] password){
        try{
            if(this.userDAO.findUser(user).isPresent()){throw new RuntimeException("User exists");}
            String hash = PasswordUtil.hash(password);
            user.setSenha(hash);
            this.userDAO.create(user);
            logger.info("User {} sign-Up successful", user.getNome());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void login(User user){
        User dbUser = this.userDAO.findUser(user)
                        .orElseThrow();
        if(!PasswordUtil.verify(dbUser.getSenha(), user.getSenha().toCharArray())){
            throw new RuntimeException("Incorrect Password");
        }

        if (PasswordUtil.needRehash(dbUser.getSenha())){
            String newHash = PasswordUtil.hash(user.getSenha().toCharArray());
            user.setSenha(newHash);
            userDAO.updatePassword(user);
        }

        logger.info("User {} logged in successful", user.getNome());
    }
}
