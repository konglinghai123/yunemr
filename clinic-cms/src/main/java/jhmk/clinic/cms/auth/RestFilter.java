//package jhmk.clinic.cms.auth;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Optional;
//
///**
// * 前后端分离RESTful接口过滤器
// * ，RestFilter是用于解决前后端分离时的跨域问题，服务端在响应头设置可以接受的请求参数
// *
// * @author ziyu.zhou
// * @date 2018/7/13 17:41
// */
//
//public class RestFilter implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest req = null;
//        if (request instanceof HttpServletRequest) {
//            req = (HttpServletRequest) request;
//        }
//        HttpServletResponse res = null;
//        if (response instanceof HttpServletResponse) {
//            res = (HttpServletResponse) response;
//        }
//        if (req != null && res != null) {
//            //设置允许传递的参数
//            res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
//            //设置允许带上cookie
//            res.setHeader("Access-Control-Allow-Credentials", "true");
//            String origin = Optional.ofNullable(req.getHeader("Origin")).orElse(req.getHeader("Referer"));
//            //设置允许的请求来源
//            res.setHeader("Access-Control-Allow-Origin", origin);
//            //设置允许的请求方法
//            res.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
//        }
//        chain.doFilter(request, response);
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//
//}