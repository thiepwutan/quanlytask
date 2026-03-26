package com.example.quanlytask.security;

import com.example.quanlytask.entity.User;
import com.example.quanlytask.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Lấy header Authorization
        String header = request.getHeader("Authorization");

        // Nếu không có token thì bỏ qua
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Cắt lấy token (bỏ "Bearer ")
        String token = header.substring(7);

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy email từ token → tìm user
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user != null) {
            // Lấy danh sách role của user
            var authorities = user.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                    .collect(Collectors.toList());

            // Set vào SecurityContext
            var authentication = new UsernamePasswordAuthenticationToken(
                    email, null, authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}