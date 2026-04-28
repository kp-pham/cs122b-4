import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@WebFilter(filterName = "LoginFilter", urlPatterns="/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        if (isEmployeeOnly(httpRequest.getRequestURI()) && httpRequest.getSession().getAttribute("employee") == null) {
            httpResponse.sendRedirect("_dashboard/login.html");
        } else if (httpRequest.getSession().getAttribute("customer") == null) {
            httpResponse.sendRedirect("login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    private boolean isEmployeeOnly(String requestURI) {
        return requestURI.startsWith("/cs122b_war/_dashboard") || requestURI.startsWith("/cs122b_war/api/employees");
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("_dashboard/login.html");
        allowedURIs.add("api/customers/login");
        allowedURIs.add("api/employees/login");

        allowedURIs.add(".css");
        allowedURIs.add(".ico");
        allowedURIs.add(".png");
    }
}