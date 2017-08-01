package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class CookieFilter.
 * Creates a cookie that's used to test whether cookies are enabled.
 */
public class CookieFilter implements Filter {
	private static final String COOKIE_NAME = "cookies_enabled";

	/**
	 * Default constructor.
	 */
	public CookieFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		Cookie cookie = new Cookie(COOKIE_NAME, "true");
		cookie.setPath("/browserprint/");
		cookie.setMaxAge(-1);// Expire when exit
		((HttpServletResponse) response).addCookie(cookie);

		// pass the request along the filter chain
		if(chain != null){
			chain.doFilter(request, response);
		}
		else{
			return;
		}
	}
}
