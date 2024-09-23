package net.datasa.nanum.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import lombok.RequiredArgsConstructor;

/**
 * 시큐리티 환경설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
        // 로그인 없이 접근 가능한 경로
        private static final String[] PUBLIC_URLS = {
                        "/" // root
                        , "/images/**" // 이미지 경로
                        , "/css/**" // CSS파일
                        , "/js/**" // JavaSCript 파일
                        , "/member/join" // 회원가입
                        , "/member/sendEmail" // 이메일 전송 ajax 요청
                        , "/find/idFind" // ID 찾기
                        , "/find/pwFind" // 비밀번호 찾기
                        , "/find/idCheck" // ID 중복 확인
                        , "/find/nickCheck" // 닉네임 중복 확인
                        , "/share/list" // 나눔 리스트
                        , "/share/read" // 나눔글 읽기
                        , "/share/download" // shareBoard 테이블 파일이름 기준 다운로드
                        , "/share/mapList" // 나눔 지도 리스트
                        , "/share/serviceList" // 나눔 완료 지도 리스트
                        , "/info/service" // 서비스 소개
                        , "/info/siteMap" // 사이트맵
                        , "/info/faq" // FAQ
                        , "/recycle/list" // 버려요 게시글 리스트
                        , "/recycle/read" // 버려요 게시글 읽기
        };

        // 로그인 실패 핸들러 의존성 주입
        private final AuthenticationFailureHandler customFailureHandler;

        @Bean
        protected SecurityFilterChain config(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(author -> author
                                                .requestMatchers(PUBLIC_URLS).permitAll()
                                                .anyRequest().authenticated())
                                .httpBasic(Customizer.withDefaults())
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/member/loginForm")
                                                .usernameParameter("id")
                                                .passwordParameter("password")
                                                .loginProcessingUrl("/member/login")
                                                .defaultSuccessUrl("/", true)
                                                .failureHandler(customFailureHandler) // 로그인 실패 핸들러
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/member/logout")
                                                .logoutSuccessUrl("/"));

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
