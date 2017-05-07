package com.mageddo;

import com.mageddo.utils.DBUtils;
import com.mageddo.utils.DefaultTransactionDefinition;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.*;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@EnableTransactionManagement
public class Application {

    public static void main(String[] args) {
//        final ServiceLoaderFactoryBean serviceLoaderFactoryBean = new ServiceLoaderFactoryBean();
//        serviceLoaderFactoryBean.setBeanClassLoader(ClassUtils.getDefaultClassLoader());
//        serviceLoaderFactoryBean.setSingleton(true);



        final DefaultListableBeanFactory listableBeanFactory = new DefaultListableBeanFactory();
        listableBeanFactory.setBeanClassLoader(ClassUtils.getDefaultClassLoader());

        final CustomAutowireConfigurer configurer = new CustomAutowireConfigurer();
        configurer.postProcessBeanFactory(listableBeanFactory);


        final TransactionTemplate template = new TransactionTemplate(
          DBUtils.getTx(),
          new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_DEFAULT)
        );

        final StandardEnvironment env = new StandardEnvironment();

        final AbstractEnvironment commandLine = new AbstractEnvironment(){
            @Override
            protected void customizePropertySources(MutablePropertySources propertySources) {
                super.customizePropertySources(propertySources);
                propertySources.addLast(new SimpleCommandLinePropertySource(args));
            }
        };
        env.merge(commandLine);
        final MutablePropertySources propertySources = env.getPropertySources();

        try {
            Properties properties;
            String propertiesName = getPropertiesName("");
            properties = loadProfileProperties(propertiesName);
            if (properties != null){
                propertySources.addLast(new PropertiesPropertySource(propertiesName, properties));
            }
            final String activeProfiles = env.getProperty("spring-profiles-active");
            for ( final String profile : activeProfiles.split(", ?") ){

                propertiesName = getPropertiesName(profile);
                properties = loadProfileProperties(propertiesName);
                if (properties != null){
                    propertySources.addLast(new PropertiesPropertySource(propertiesName, properties));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        template.execute(ts -> {
           return DBUtils.getTemplate().queryForList("SELECT 1 FROM DUAL");
        });


//        listableBeanFactory.getBean()

//        configurer.postProcessBeanFactory();

//        new BeanFactoryDataSourceLookup()
//        serviceLoaderFactoryBean.
//        BeanFactoryUtils.beanOfTypeIncludingAncestors()
    }

    private static Properties loadProfileProperties(String propertiesName) throws IOException {

        final InputStream profileIn = ClassUtils
          .getDefaultClassLoader()
          .getResourceAsStream(propertiesName);

        if (profileIn == null){
            return null;
        }
        final Properties properties = new Properties();
        properties.load(profileIn);
        return properties;
    }

    private static String getPropertiesName(String profileName) {
        return "application" + (StringUtils.isEmpty(profileName) ? "" : "-" + profileName) + ".properties";
    }



}
