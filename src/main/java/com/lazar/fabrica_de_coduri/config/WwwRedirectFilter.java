//package com.lazar.fabrica_de_coduri.config;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class WwwRedirectFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//
//        String host = request.getHeader("Host");
//        if (host != null && !host.startsWith("www.")) {
//            String redirectUrl = "https://www." + host + request.getRequestURI();
//            if (request.getQueryString() != null) {
//                redirectUrl += "?" + request.getQueryString();
//            }
//            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
//            response.setHeader("Location", redirectUrl);
//        } else {
//            chain.doFilter(req, res);
//        }
//    }
//}
