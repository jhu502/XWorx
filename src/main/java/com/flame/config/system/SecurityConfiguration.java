package com.flame.config.system;

import com.flame.auths.IAuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.flame.auths.BasicUser;
import com.flame.xui.HREFactory;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;

@Configuration
@EnableWebSecurity // 启用Spring Security
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
//启用对PreAuthorize、securedEnabled、jsr250Enabled注解的拦截
public class SecurityConfiguration {
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    protected SecurityFilterChain configureSecurity(HttpSecurity httpSecurity) throws Exception {
        /**
         * anonymous() 允许匿名用户访问
         * permitAll() 持身份信息无条件允许访问
         */
        httpSecurity.headers(headers -> headers.httpStrictTransportSecurity(p -> p.disable())); //关闭HTTPS访问协议
        /**
         * 解决Spring boot 引入Spring Security后iframe或者frame所引用的页无法显示的问题
         * X-Frame-Options HTTP 响应头是用来给浏览器 指示允许一个页面 可否在<frame>,<iframe>,<embed>或<object>中展现的标记。
         * 站点可以通过确保网站没有被嵌入到别人的站点里面，从而避免被攻击。
         *
         * X-Frame-Options 有三个可能的值：
         * 	X-Frame-Options: deny   // 任意跨域
         *  	http.headers().frameOptions().disable();
         * 	X-Frame-Options: sameorigin // 同源跨域
         *  	http.headers().frameOptions().sameOrigin();
         * 	X-Frame-Options: allow-from url
         */
        httpSecurity.headers(headers -> headers.frameOptions(options -> options.disable()));
        /**
         * 启用csrf, 防止跨域伪造请求, csrf token存放在cookie中
         * httpSecurity.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
         * 禁用csrf: httpSecurity.csrf(csrf -> csrf.disable());
         */
        httpSecurity.csrf(csrf -> csrf.disable());
        /**
         * 禁止跨域资源共享
         */
        //httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource())).cors(cors -> cors.disable());
        /**
         * 设置认证Provider: AuthenticationProvider Bean或authenticationProvider()
         * httpSecurity.authenticationProvider(new XAuthenticationProvider());
         */
        /**
         * 匹配的全部无条件通过 permitAll
         */
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(
                /** 系统登录相关的访问 */
                "/login", "/logout", "/error", "/favicon.ico", //
                /** 静态资源 */
                "/css/**", "/fonts/**", "/images/**", "/js/**", //
                /** Swagger相关请求允许访问 */
                "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                /** 其他请求全部需要验证登录 */
                .anyRequest().authenticated() //
        );

        /**
         * Basic认证, Base64编码格式传递用户名/密码
         */
        httpSecurity.httpBasic(Customizer.withDefaults()).exceptionHandling((e) -> {
        });
        /**
         * 开启formLogin默认配置
         * loginPage: 登录页面
         * loginProcessingUrl: 登录接口
         */
        //httpSecurity.formLogin(login -> login.loginPage("/login").permitAll());

        //httpSecurity.logout(logout -> logout.logoutUrl("/logout").permitAll());

        /**
         * 如果不设置会话管理，那么每次访问都会请求:AuthenticationProvider
         */
        httpSecurity.sessionManagement(session -> session
                /** 总是创建一个新的HTTP会话 */
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS).invalidSessionUrl("/login/invalid") //
                /** 指定最大登录数 */
                .maximumSessions(2)
                /** true:保留先登录的用户,后登录用户无法登录；false:后用户踢出先登录用户 */
                .maxSessionsPreventsLogin(false) //当达到最大值时,是否保留已经登录的用户
                /** 当达到最大值时,旧用户被踢出后的操作 */
                .expiredSessionStrategy(new MultiLoginExpiredStrategy()) //
        );

        return httpSecurity.build();
    }

    @Bean
    public OpenAPI configureOpenAPI(Environment environment) {
        Profiles profiles = Profiles.of("dev", "test", "prod");
        environment.acceptsProfiles(profiles);

        return new OpenAPI().info(new Info().title("XFlame Swagger接口规范").description("Rest接口说明").version("v0.5.1").termsOfService(HREFactory.getBaseHREF())) //
                /**
                 * 为Swagger-UI配置API全局安全要求，使所有接口默认需要认证。
                 * 具体用途：
                 * 1. 定义认证方式：声明API支持三种安全方案(满足任意一种即可)：
                 *    - bearer-key - JWT Bearer Token 认证(Authorization: Bearer xxx)
                 *    - api-key - API Key 认证(Header: X-API-KEY)
                 *    - xsrf-token - XSRF Token 认证（Header: X-XSRF-TOKEN）
                 * 2. Swagger UI 效果：在/swagger-ui.html页面右上角显示"Authorize"按钮，用户可输入token进行认证测试
                 * 3. 全局生效：未特别标注的接口默认继承这些安全要求(可用 @SecurityRequirement 注解在控制器/方法上覆盖)
                 */
                .addSecurityItem(new SecurityRequirement().addList("bearer-key").addList("api-key").addList("xsrf-token")) //
                .components(new Components() //
                        .addSecuritySchemes("bearer-key", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))//
                        .addSecuritySchemes("api-key", new SecurityScheme().type(SecurityScheme.Type.APIKEY).name("X-API-KEY").in(In.HEADER))//
                        .addSecuritySchemes("xsrf-token", new SecurityScheme().type(SecurityScheme.Type.APIKEY).name("X-XSRF-TOKEN").in(In.HEADER)) //
                );
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOriginPattern("*"); //允许任何源
        corsConfig.addAllowedHeader("*"); //允许任何HTTP方法
        corsConfig.addAllowedMethod("*"); //允许任何HTTP头
        corsConfig.setAllowCredentials(true); //允许证书(Cookies)
        //corsConfig.setMaxAge(3600L); //预检请求的缓存时间(秒)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); //对所有的路径都应用这个配置
        return source;
    }

    @Bean
    public AuthenticationProvider configureProvider() {
        return new XAuthenticationProvider();
    }

    public class XAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
        @Override
        protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
            if (authentication.getCredentials() == null) {
                logger.debug("Authentication failed: no credentials provided");
                throw new BadCredentialsException(messages.getMessage("XAuthenticationProvider.badCredentials", "Bad credentials"));
            }

            String password = authentication.getCredentials().toString();
            if (password != null && !password.equals(userDetails.getPassword())) {
                throw new BadCredentialsException(messages.getMessage("XAuthenticationProvider.badCredentials", "Bad credentials"));
            } else if (password == null && userDetails.getPassword() != null) {
                throw new BadCredentialsException(messages.getMessage("XAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        }

        @Override
        protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
            IAuthUser authUser = (IAuthUser) userDetailsService.loadUserByUsername(username);

            /**
             * -默认的初始用户Administrator:xworx，系统初始化完成后自动失效
             */
            if (authUser == null) {
                if ("Administrator".equals(username)) {
                    authUser = new BasicUser();
                    authUser.setName("Administrator");
                    authUser.setPassword("xworx");
                    authUser.authorities(AuthorityUtils.commaSeparatedStringToAuthorityList("read,ROLE_ADMIN"));
                } else {
                    throw new BadCredentialsException(messages.getMessage("XAuthenticationProvider.badCredentials", "Bad credentials"));
                }
            } else {
                authUser.authorities(AuthorityUtils.commaSeparatedStringToAuthorityList("read,ROLE_ADMIN"));
            }
            return authUser;
        }
    }
}
