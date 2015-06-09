/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/
package secplan;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class MyServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -463786621936530430L;
	private JSONObject data;
    DBConnection dbConnection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        //get database connection from servlet context
        dbConnection = (DBConnection) getServletContext().getAttribute("DATABASE_CONNECTION");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        doPost(req,resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    	if(req.getParameter("command").equals("login")){
    		int id = Integer.valueOf(req.getParameter("id"));
        	
        	if(dbConnection.getPersonalData(id) != null){
        		data = dbConnection.getPersonalData(id);
        		try {
    				data.put("reports", dbConnection.getDefaultReport().get("reports"));
    				JSONObject temp = dbConnection.getWeeklyTasks();
    				if(temp != null){
    					data.put("work", temp.get("task"));
    					data.put("completedID", new JSONObject(temp.get("_id").toString()).getString("$oid"));
    				}
    				else{
    					temp = new JSONObject();
    					temp.put("work", "No work for this week");
    				}
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
        	}
        	else
        		System.out.println("NULL");

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            PrintWriter out = resp.getWriter();
            out.print(data);
            out.flush();
    	}
    	else if(req.getParameter("command").equals("sendReport")){
    		if(dbConnection.getPersonalData(Integer.valueOf(req.getParameter("id"))) != null){
    			dbConnection.sendReport(req.getParameter("json"));
    			data = new JSONObject();
    			try {
    				data.put("status", "Report sent");
				} catch (JSONException e) {
					e.printStackTrace();
				}
    		}
    		else {
    			try {
					data.put("status", "Report not sent");
				} catch (JSONException e) {
					e.printStackTrace();
				}
    		}
    		resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            PrintWriter out = resp.getWriter();
            out.print(data);
            out.flush();
    	}
    }
}
