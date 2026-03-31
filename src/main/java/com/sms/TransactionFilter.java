package com.sms;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import com.sms.util.JwtToken;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
@Order(1)
public class TransactionFilter implements Filter {
    @Autowired
    private JwtToken jwtToken;
    /*private static final List<String> allowedOrigins = Arrays.asList(
            "http://localhost:3000",
            "http://10.0.142.233:3000",
            "http://52.15.42.208:3000",
            "http://app.medhapro.com:3000"
    );*/
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        System.out.println("Initializing filter :" + this);
    }
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        //Get the origin of the request
       /* String origin = req.getHeader("Origin");
        if(allowedOrigins.contains(origin)){
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Access-Control-Allow-Credentials", "true");
        }*/

        // CORS headers
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        //res.setHeader("Access-Control-Allow-Origin", "https://erp.medhapro.com:3006");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Max-Age", "3600"); // 60*60 secs means 1 hour

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = req.getRequestURI();

        // Skip JWT validation for the login endpoint
        /*if ("/api/login".equals(path)) {
            chain.doFilter(request, response);
            return;
        }*/
        if ("/api/login".equals(path) || "/api/registration".equals(path) || "/api/verifyOtp".equals(path) || "/api/reset/password".equals(path) || "/api/password/verify/otp".equals(path) || "/api/resend/otp".equals(path) || "/api/resendOtp/registration".equals(path) || "/api/refresh-token".equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("You are not authorized to access this");
            return;
        }

        String token = authHeader.substring(7).trim();
        try {
            if (!jwtToken.isValidJWT(token)) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write("You are not authorized to access this");
                return;
            }
        } catch (ExpiredJwtException e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Session is expired");
            return;
        } catch (IllegalArgumentException e){
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("You are not authorized to access this");
            return;
        }catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("Internal server error");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("Destructing filter :{}" + this);
    }
}
