/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Rest;

/**
 *
 * @author Dell
 */
import java.net.URI;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;

import iitbh.effi.io.Parameter;
import iitbh.effi.io.ServiceRequest;
import iitbh.effi.io.ServiceResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.servlet.ServletContext;
@Path("/")
public class alumni{
    
    @Context
    private ServletContext context;
    
    @Path("/saveData")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces({MediaType.TEXT_HTML})
    public String hello(String inputxml) throws Exception{
        ServiceRequest serviceReq = null;
        ServiceResponse serviceRes = new ServiceResponse();
        String opname = null;
        String returnHTML = "";
        try{
            serviceReq = ServiceRequest.validateAndUnmarshal(inputxml);
            opname = serviceReq.getOperationName();
            if (opname.equals("addUser")){
                
                serviceRes.setServiceName(serviceReq.getServiceName());
		serviceRes.setOperationName(serviceReq.getOperationName());
		serviceRes.setToken(serviceReq.getToken());
                
                List<Parameter> params = serviceReq.getParameters();
                if(params.isEmpty() || params == null){
                    serviceRes.setError("parameters are missing");
                    return responseToHTML(serviceRes.marshalToXml());
                }
                Parameter param = params.get(0);
                if(param.size() == 0 || param == null){
                    serviceRes.setError("parameters are missing");
                    return responseToHTML(serviceRes.marshalToXml());
                }
                String name = param.getValue("name");
                String id = param.getValue("ID");
                String DOB = param.getValue("DOB");
                
                int res = saveToDB(name,id,DOB);
                
                if(res == 0){
                    serviceRes.setError("duplicate data found");
                }
                else{
                    Parameter p = new Parameter();
                    p.setName("Message");
                    p.setValue("New user has been added.");
                    params.clear();
                    params.add(p);
                    serviceRes.setParameters(params);
                }         
                returnHTML = responseToHTML(serviceRes.marshalToXml());
                
            }
            else
                serviceRes.setError("requested operation not found");
                returnHTML = responseToHTML(serviceRes.marshalToXml());
        }
        catch(Exception e){
            return "<h2>"+e.getMessage()+"</h2>";
        }
        return returnHTML;
    }
    
    private String responseToHTML(String responseXml) {
        String htmlStr = "<div class=\"response\">";
        try {
            ServiceResponse res = ServiceResponse.validateAndUnmarshal(responseXml);
            if(res.getError() != null){
                htmlStr = htmlStr + "<h2>Error message::"+res.getError()+"</h2>";
            }
            List<Parameter> params= res.getParametersToXml();
            if(params != null){
                Parameter param = params.get(0);
                if(param != null){
                    for(int i=0;i<param.size();i++){
                        htmlStr = htmlStr + "<h2>"+param.getName(i)+"::"+param.getValue(i)+"</h2>";
                    }
                }
            }
            htmlStr = htmlStr + "</div>";
        } catch (Exception e) {
            return null;
        }
        return htmlStr;
		
    }
    
    
    @Path("/toFORM")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces({MediaType.TEXT_HTML})
    public String toFORM(String requestXml){
        String outputHTML = "";
        String filePath = "";
        
        try {
            ServiceRequest serviceReq = ServiceRequest.validateAndUnmarshal(requestXml);
            String opName = serviceReq.getOperationName().toLowerCase();
            if (opName.equalsIgnoreCase("addUser")) {
                  // create a jsp page and forward request.
                filePath = context.getRealPath("/WEB-INF/html/form.jsp");
		StringBuilder contentBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(filePath)));
			String str;
			while ((str = in.readLine()) != null) {
				contentBuilder.append(str);
			}
			in.close();
		} catch (Exception e) {
			return "<html>"
                    + "<head>"
                    + "<title>error</title>"
                    + "</head>"
                    + "<body>"
                    + "<h1>error</h1>"
                    + "</body>"
                    + "</html>";
		}

		outputHTML = contentBuilder.toString();
            }
        } catch (Exception e) {
            //Forward to error web page
            return "<html>"
                    + "<head>"
                    + "<title>error</title>"
                    + "</head>"
                    + "<body>"
                    + "<h1>error</h1>"
                    + "</body>"
                    + "</html>";
            
        }
        return outputHTML;
        
    }
    
    public String getData(){
        List<testModel> tma = new ArrayList<>();
        String query = "select * from ALUMNI";
        //String result = "<sql-response>";
        try {
            Statement st = getConnection().createStatement();
            ResultSet res = st.executeQuery(query);
            while(res.next()){
                testModel tm = new testModel();
                tm.setID(res.getInt("ID"));
                tm.setName(res.getString("NAME"));
                tm.setDOB(res.getDate("DOB"));
                tma.add(tm);
            }
        } catch (SQLException ex) {
            Logger.getLogger(alumni.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String ret = "";
        for (testModel tm : tma) {
            ret = ret+"<h3>"+tm.getID()+"  "+tm.getName()+"  "+tm.getDOB().toString()+"</h3>";
        }
        return ret;
    }
    
    public int saveToDB(String name, String id, String DOB) throws Exception{
        int result;
        try {
            PreparedStatement ps = getConnection().prepareStatement("insert into ALUMNI values(?,?,?)");
            ps.setInt(1, Integer.parseInt(id));
            ps.setString(2, name);
            ps.setDate(3, Date.valueOf(DOB));
            result = ps.executeUpdate();
        } catch (SQLException ex) {
            result = 0;
        }
        return result;
    }
    
    public Connection getConnection(){
        Connection con = null;
        String username = "abc";
        String password = "abc";
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/studentDetail",username,password);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(alumni.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return con;
    }
}