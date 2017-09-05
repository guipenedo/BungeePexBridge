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

    private static final class CachedConnection {
    	private Connection connection;
    	private long lastTested;
    	private boolean valid = true;

    	public CachedConnection(Connection connection, long lastTested) {
			this.connection = connection;
			this.lastTested = lastTested;
		}


		public boolean isValid() throws SQLException{
			if(!valid)
				return false;

    		if(lastTested + 30 * 1000 < System.currentTimeMillis()){
    			if(!connection.isClosed() && connection.isValid(10))
    				return true;
    			else
    				valid = false;
    		}
    		return valid;
    	}
    }

	private static final int MAX_CONNECTIONS = 8;
	private static CachedConnection[] connectionPool = new CachedConnection[MAX_CONNECTIONS];

	public MySQL(String host, String user, String pass, String database, String port){
		info = new Properties();
		info.put("autoReconnect", "true");
		info.put("user", user);
		info.put("password", pass);
		info.put("useUnicode", "true");
		info.put("characterEncoding", "utf8");
		this.url = "jdbc:mysql://"+host+":"+port+"/"+database;

        try {
        	enabled = getNextConnection() != null;
        }catch(Exception e){
        	System.err.println("Having error while creating MySQL client.");
        	e.printStackTrace();
        	enabled = false;
        }
	}

    public void close(){
        for(int i = 0; i < MAX_CONNECTIONS; i++) {
        	if(connectionPool[i] != null) {
				Connection connection = connectionPool[i].connection;
				try {
					if (connection != null && !connection.isClosed())
						connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
        }
    }

	/**
	 * Gets the database connection for
	 * executing queries on.
	 * @return The database connection
	 */
	public Connection getNextConnection() throws SQLException{
		SQLException last = null;
		for(int i = 0; i < MAX_CONNECTIONS; i++){
			CachedConnection connection = connectionPool[i];
			try{
				//If we have a current connection, fetch it
				if(connection != null && connection.isValid())
					return connection.connection;

				connection = new CachedConnection(DriverManager.getConnection(this.url, info), System.currentTimeMillis());
				connectionPool[i] = connection;
				return connection.connection;
			}
			catch(SQLException e){
				last = e;
			}
		}

		//Throw the last exception to break the stack. Dont do stuff this an broken SQL connection.
		if(last != null){
			System.err.println("Having exception on finding next connection!");
			throw last;
		}

		throw new SQLException("Cant find a valid SQL connection!");
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