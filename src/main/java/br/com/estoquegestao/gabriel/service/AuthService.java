package br.com.estoquegestao.gabriel.service;

import br.com.estoquegestao.gabriel.dao.UserDAO;
import br.com.estoquegestao.gabriel.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void signup(User user, char[] password){
        try{
            if(this.userDAO.findUser(user.getCpf()).isPresent()){throw new RuntimeException("User exists");}
            String hash = PasswordUtil.hash(password);
            user.setPassword(hash);
            this.userDAO.create(user);
            logger.info("User {} sign-Up successful", user.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void login(User user) throws SQLException {
        User dbUser = this.userDAO.findUser(user.getCpf())
                        .orElseThrow();
        if(!PasswordUtil.verify(dbUser.getPassword(), user.getPassword().toCharArray())){
            throw new RuntimeException("Incorrect Password");
        }

        if (PasswordUtil.needRehash(dbUser.getPassword())){
            String newHash = PasswordUtil.hash(user.getPassword().toCharArray());
            user.setPassword(newHash);
            userDAO.updatePassword(user);
        }

        logger.info("User {} logged in successful", user.getName());
    }
}
