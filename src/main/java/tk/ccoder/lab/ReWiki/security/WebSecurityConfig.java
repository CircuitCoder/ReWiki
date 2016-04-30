package tk.ccoder.lab.ReWiki.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by lenovo2012-3a on 2016/5/1.
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    ReWikiAuthenticationProvider provider;

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.formLogin()
                .loginPage("/account/login")
                .defaultSuccessUrl("/profile")
                .permitAll()
                .and()

                .logout()
                .logoutUrl("/account/logout")
                .logoutSuccessUrl("/account/login");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(provider);
    }
}