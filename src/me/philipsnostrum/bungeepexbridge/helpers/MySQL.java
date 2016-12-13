package me.philipsnostrum.bungeepexbridge.helpers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class MySQL {
	private String url;
	/** The connection properties... user, pass, autoReconnect.. */
	private Properties info;
    public boolean enabled;
	
	private static final int MAX_CONNECTIONS = 8;
	private static Connection[] connectionPool = new Connection[MAX_CONNECTIONS];
	
	public MySQL(String host, String user, String pass, String database, String port){
		info = new Properties();
		info.put("autoReconnect", "true");
		info.put("user", user);
		info.put("password", pass);
		info.put("useUnicode", "true");
		info.put("characterEncoding", "utf8");
		this.url = "jdbc:mysql://"+host+":"+port+"/"+database;
		
        enabled = getNextConnection() != null;
	}

    public void close(){
        for(int i = 0; i < MAX_CONNECTIONS; i++) {
            Connection connection = connectionPool[i];
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	/**
	 * Gets the database connection for
	 * executing queries on.
	 * @return The database connection
	 */
	public Connection getNextConnection(){
		for(int i = 0; i < MAX_CONNECTIONS; i++){
			Connection connection = connectionPool[i];
			try{
				//If we have a current connection, fetch it
				if(connection != null && !connection.isClosed()){
					if(connection.isValid(10)){
						return connection;
					}
					//Else, it is invalid, so we return another connection.
				}
				connection = DriverManager.getConnection(this.url, info);
				
				connectionPool[i] = connection;
				
				return connection;
			}
			catch(SQLException ignored){
			}
		}
		return null;
	}

    public List<String> resultSetToList(ResultSet res, String column){
        List<String> list = new ArrayList<String>();
        try {
            while (res.next())
                list.add(res.getString(column));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}