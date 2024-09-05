package sprintOauth2Github.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sprintOauth2Github.entity.User;
import sprintOauth2Github.repository.UserRepository;

import java.util.Optional;

public class security {

    @Autowired
    UserRepository userRepository;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(requests -> requests
                    .requestMatchers("/permitted").authenticated()
                    .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                //.loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(
                        oAuth2UserService()
                    )
                )
            );
        return http.build();
    }

//    @Bean
//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
//        DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
//        return request -> {
//            OAuth2User oauth2User   = defaultOAuth2UserService.loadUser(request);
//            String googleId         = oauth2User.getAttribute("sub");
//            String name             = oauth2User.getAttribute("name");
//            String email            = oauth2User.getAttribute("email");
//            String pictureUrl       = oauth2User.getAttribute("picture");
//            Optional<User> user = userRepository.findByGoogleId(googleId);
//            if (user.isEmpty()) {
//                User newUser = new User();
//                newUser.setGoogleId(googleId);
//                newUser.setName(name);
//                newUser.setEmail(email);
//                newUser.setPictureUrl(pictureUrl);
//                userRepository.save(newUser);
//            }
//            return oauth2User;
//        };
//    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User oauth2User = delegate.loadUser(request);
            String registrationId = request.getClientRegistration().getRegistrationId();

            if ("github".equals(registrationId)) {
                String githubId = oauth2User.getAttribute("id");
                String email = oauth2User.getAttribute("email");
                String name = oauth2User.getAttribute("name");

                // Save user information to your database
                Optional<User> existingUser = userRepository.findByGithubId(githubId);
                if (existingUser.isEmpty()) {
                    User newUser = new User();
                    newUser.setGithubId(githubId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    userRepository.save(newUser);
                }
            }

            // Continue with the existing OAuth2User
            return oauth2User;
        };
    }

}
