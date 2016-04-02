package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAOs.JSONDAO;

/**
 * Servlet implementation class ChartsServlet
 */
public class JSONServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JSONServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		String chart = request.getParameter("chart");
		if(chart == null){
			return;
		}
		
		if(chart.equals("usingTor")){
			out.print(JSONDAO.getPercentageTorUsers());
		}
		else if(chart.equals("cookiesEnabled")){
			out.print(JSONDAO.getCookiesEnabled());
		}
		else if(chart.equals("OS")){
			out.print(JSONDAO.getOSBreakdown());
		}
		else if(chart.equals("browser")){
			out.print(JSONDAO.getBrowserBreakdown());
		}
		else if(chart.equals("timezone")){
			out.print(JSONDAO.getTimezones());
		}
		else if(chart.equals("language")){
			out.print(JSONDAO.getLanguages());
		}
		else if(chart.equals("adsBlocked")){
			out.print(JSONDAO.getAdsBlocked());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
