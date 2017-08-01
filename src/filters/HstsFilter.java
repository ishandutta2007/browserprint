package filters;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class HstsFilter.
 * Enabled HSTS on domains other than the ones hsts\d+\..*
 * We want to exclude those domains since HSTS is used to demonstrate HSTS supercookies on them.
 */
public class HstsFilter implements Filter {
	private Pattern domainPattern;
	
	/**
	 * Default constructor.
	 */
	public HstsFilter() {
		this.domainPattern = Pattern.compile("^hsts\\d+\\..*$");
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
		if(request.getServerPort() == 443){
			Matcher m = domainPattern.matcher(request.getServerName());
	
			//Enable HSTS for domains other than ones starting with hsts\d+.
			if(m.matches() == false){
				((HttpServletResponse)response).setHeader("Strict-Transport-Security", "max-age=31622400");
			}
		}

		// pass the request along the filter chain
		if(chain != null){
			chain.doFilter(request, response);
		}
		else{
			return;
		}
	}
}
