package com.bizzan.bitrade.config;

import com.bizzan.bitrade.ext.OrdinalToEnumConverterFactory;
import com.bizzan.bitrade.interceptor.MemberInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年02月06日
 * @see
 */
@Configuration
public class ApplicationConfig extends WebMvcConfigurerAdapter {


    /**
     * 国际化
     *
     * @return
     */
    @Bean(name = "messageSource")
    public ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setBasenames("i18n/messages", "i18n/ValidationMessages");
        resourceBundleMessageSource.setCacheSeconds(3600);
        return resourceBundleMessageSource;
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(getMessageSource());
        return validator;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/asset/**").addResourceLocations("classpath:/asset/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new OrdinalToEnumConverterFactory());
        super.addFormatters(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MemberInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/register/**", "/mobile/code", "/login", "/app_login"
                        , "/check/login", "/start/captcha", "/support/country", "/support/country/app",
                        "/ancillary/**", "/announcement/**", "/mobile/reset/code"
                        , "/reset/email/code", "/reset/login/password", "/vote/info"
                        , "/coin/supported", "/financial/items/**"
                        , "/coin/guess/index"
                        , "/coin/guess/record"
                        , "/coin/guess/detail"
                        , "/coin/guess/type"
                        , "/activity/page-query"
                        , "/activity/detail"
                        , "/coin/getContractByProtocol"
                        , "/promotion/toprank"
                        , "/promotioncard/detail"
                        , "/redenvelope/query"
                        , "/redenvelope/query-detail"
                        , "/redenvelope/receive"
                        , "/redenvelope/code"
                        , "/reg/email/code"
                        , "/register/check/username"
                        , "/register/check/email"
                        , "/payment/list"
                        , "/withdraw/approve");
        super.addInterceptors(registry);
    }

    @Bean
    public FilterRegistrationBean corsFilterForBusi() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("x-auth-token");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

}
