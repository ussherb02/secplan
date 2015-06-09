package secplan;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by ussher on 09.05.15.
 */
@WebListener 
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        /*
        * Load config data into system to be made available
        * */

        ServletContext context = sce.getServletContext();
        String path = context.getInitParameter("config.properties");
        Properties properties = new Properties();
        BufferedInputStream inputStream;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(path));
            properties.load(inputStream);
            inputStream.close();
            for (Map.Entry<Object,Object> p : properties.entrySet()){
            	System.out.println("key:" + p.getKey() + ":" + p.getValue());
                System.setProperty(p.getKey().toString(),p.getValue().toString().trim());
            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();

        }
        catch (IOException e){
            e.printStackTrace();
        }

        try {
            DBConnection connection = DBConnection.getInstance();
            connection.setParameters(System.getProperty("database"),System.getProperty("username"),System.getProperty("server")
            , System.getProperty("password"),Integer.valueOf(System.getProperty("port")));

            if(connection.connect()){
            	System.out.println("Conection to DB succesful");
                context.setAttribute("DATABASE_CONNECTION",connection);
            }
            else{
            	System.out.println("Conection to DB failed");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        //Disconnect database

        Object dbconnection = context.getAttribute("DATABASE_CONNECTION");
        if(dbconnection instanceof DBConnection){
            //Disconnect
            ((DBConnection) dbconnection).disconnect();
        }
        dbconnection = null;
        try {
            context.removeAttribute("DATABASE_CONNECTION");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
