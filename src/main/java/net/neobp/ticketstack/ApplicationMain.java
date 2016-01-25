package net.neobp.ticketstack;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/** Simple main class as required by spring boot.
 */
@SpringBootApplication
public class ApplicationMain {

    private final static Logger log = Logger.getLogger(ApplicationMain.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(ApplicationMain.class, args);

        if(log.isDebugEnabled()) {
        	log.debug("Let's inspect the beans provided by Spring Boot:");
        	final String[] beanNames = ctx.getBeanDefinitionNames();
        	Arrays.sort(beanNames);
        	for(final String beanName : beanNames)
        		log.debug("beans listing: "+beanName);
        }
    }
}
