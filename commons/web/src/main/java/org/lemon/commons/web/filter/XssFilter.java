package org.lemon.commons.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.lemon.commons.core.utils.StringUtils;
import org.lemon.commons.core.utils.spring.SpringUtils;
import org.lemon.commons.web.config.properties.XssProperties;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 防止XSS攻击的过滤器
 *
 * @author ruoyi
 */
public class XssFilter extends HttpFilter {
    /**
     * 排除链接
     */
    public List<String> excludes = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        XssProperties properties = SpringUtils.getBean(XssProperties.class);
        excludes.addAll(properties.getExcludeUrls());
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (handleExcludeURL(request)) {
            chain.doFilter(request, response);
            return;
        }
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(request);
        chain.doFilter(xssRequest, response);
    }

    private boolean handleExcludeURL(HttpServletRequest request) {
        String url = request.getServletPath();
        String method = request.getMethod();
        // GET DELETE 不过滤
        if (method == null || HttpMethod.GET.matches(method) || HttpMethod.DELETE.matches(method)) {
            return true;
        }
        return StringUtils.matches(url, excludes);
    }
}
