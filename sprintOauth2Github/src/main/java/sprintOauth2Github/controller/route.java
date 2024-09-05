package sprintOauth2Github.controller;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.io.IOException;

@Controller
public class route {
    @GetMapping("/")
    public String home(Model model, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauthUser = oauthToken.getPrincipal();
            // For OIDC (OpenID Connect)
            if (oauthUser instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) oauthUser;
                String fullName = oidcUser.getFullName();
                String email = oidcUser.getEmail();
                String picture = oidcUser.getPicture();

                model.addAttribute("name", fullName);
                model.addAttribute("email", email);
                model.addAttribute("picture", picture);

            }
            // For OAuth2 (non-OpenID Connect)
            else if (oauthUser instanceof DefaultOAuth2User) {
                DefaultOAuth2User defaultUser = (DefaultOAuth2User) oauthUser;
                String name = defaultUser.getAttribute("name");
                String email = defaultUser.getAttribute("email");
                String picture = defaultUser.getAttribute("picture");

                model.addAttribute("name", name);
                model.addAttribute("email", email);
                model.addAttribute("picture", picture);

                System.out.println(name);
                System.out.println(email);
                System.out.println(picture);
            }
        } else {
            // Handle the case when the user is not authenticated
            System.out.println("User is not authenticated.");
            return "redirect:/login"; // Redirect to login page or handle accordingly
        }
        // Set tokens in HTTP-only cookies
        Cookie jwtCookie = new Cookie("JWT-TOKEN", "Testing-oauth-jwt-github-idp");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");

        Cookie refreshCookie = new Cookie("REFRESH-TOKEN", "Testing-oauth-ref-jwt-github-idp");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");

        response.addCookie(jwtCookie);
        response.addCookie(refreshCookie);

        try {
            response.sendRedirect("http://localhost:3000/dashboard");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";  // Returns the signup page (optional)
    }

}
