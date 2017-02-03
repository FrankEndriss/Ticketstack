package net.neobp.ticketstack;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.happypeople.olingotest.web.OlingoServlet;

/** Simple main class as required by spring boot.
 * extends SpringBootServletInitializer is needed to run the app as a WAR within Tomcat (8.x)
 */
@SpringBootApplication
@ComponentScan({ "net.neobp", "com.happypeople" })
public class ApplicationMain extends SpringBootServletInitializer {

    private final static Logger log = Logger.getLogger(ApplicationMain.class);

    public static void main(final String[] args) {
        final ApplicationContext ctx = SpringApplication.run(ApplicationMain.class, args);

        if(log.isDebugEnabled()) {
        	log.debug("Let's inspect the beans provided by Spring Boot:");
        	final String[] beanNames = ctx.getBeanDefinitionNames();
        	Arrays.sort(beanNames);
        	for(final String beanName : beanNames)
        		log.debug("beans listing: "+beanName);
        }
    }
    
    /** Not sure why this is needed...
     * found at:
     * http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-create-a-deployable-war-file
     */
    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(ApplicationMain.class);
    }

    /////////////////// some Beans used in this app //////////////////////////////////////////
    @Autowired
    private ApplicationContext applicationContext;
    
	/** This method creates the Olingo based servlet to serv the OData service for Ticketstack
	 * @return A ServletRegistrationBean configured to register the OlingoServlet
	 */
	@Bean
	public ServletRegistrationBean olingoTicketstackServletRegistrationBean() {
		log.info("creating OlingoServlet");
		return new ServletRegistrationBean(applicationContext.getBean(OlingoServlet.class), "/TicketsService.svc/*");
	}
	
}
