package net.datasa.nanum.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 시큐리티 환경설정
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    //로그인 없이 접근 가능한 경로
    private static final String[] PUBLIC_URLS = {
            "/"                         // root
            , "/images/**"              // 이미지 경로
            , "/css/**"                 // CSS파일
            , "/js/**"                  // JavaSCript 파일
            , "/member/join"            // 회원가입
            , "/member/idCheck"         // ID 중복 확인
            , "/share/shareList"        // 나눔 리스트
            , "/share/shareRead"        // 나눔글 읽기
//          , "/share/shareSave"      // 나눔글 작성
            , "/recycle/recycleList"    // 나눔리스트
            , "/recycle/recycleRead"    // 분리수거 규정 읽기
            , "/info/service"            // 서비스 소개
            , "/info/siteMap"            // 사이트맵
            , "/info/faq"                // FAQ
    };

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(author -> author
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(formLogin -> formLogin
                        .loginPage("/member/loginForm")
                        .usernameParameter("id")
                        .passwordParameter("password")
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/")
                );

        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
