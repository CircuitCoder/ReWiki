package tk.ccoder.lab.ReWiki.security;

import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import tk.ccoder.lab.ReWiki.data.Account;

import java.util.ArrayList;

/**
 * Created by lenovo2012-3a on 2016/5/1.
 */

@Component
public class ReWikiAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    MongoTemplate mongo;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String passwd = authentication.getCredentials().toString();

        Account result = mongo.findOne(new Query(Criteria.where("email").is(email)), Account.class);
        if(result.validatePassword(passwd)) {
            Authentication auth = new UsernamePasswordAuthenticationToken(result, passwd, new ArrayList<>());
            return auth;
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
