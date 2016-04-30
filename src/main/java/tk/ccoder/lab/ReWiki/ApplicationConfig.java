package tk.ccoder.lab.ReWiki;

import com.mongodb.Mongo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;

/**
 * Created by lenovo2012-3a on 2016/5/1.
 */

@Configuration
public class ApplicationConfig {
    public @Bean Mongo mongo() throws UnknownHostException {
        return new Mongo("localhost");
    }

    public @Bean MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), "rewiki");
    }
}
