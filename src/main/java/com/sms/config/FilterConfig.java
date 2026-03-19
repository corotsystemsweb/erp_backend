/*
package com.sms.config;

import com.sms.TransactionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    // uncomment this and comment the @Component in the filter class definition to register only for a url pattern
    @Bean
    public FilterRegistrationBean<TransactionFilter> transactionFilter() {
        FilterRegistrationBean<TransactionFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new TransactionFilter());

        registrationBean.addUrlPatterns("/api/login/*");
        registrationBean.setOrder(1);

        return registrationBean;

    }
}
*/
