package de.raysha.lib.dbc.beans;

public class ConnectionInfo {
	private String className;
	private String jdbcUrl;
	private String user;
	private String pw;
	
	public ConnectionInfo(String className, String jdbcUrl, String user, String pw) {
		this.className = className;
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.pw = pw;
	}
	
	public String getClassName() {
		return className;
	}
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	public String getUser() {
		return user;
	}
	public String getPw() {
		return pw;
	}
	@Override
	public String toString() {
		return "ConnectInfo [className=" + className + ", jdbcUrl=" + jdbcUrl
				+ ", user=" + user + 
				", pw=" + ( pw == null ? "" : pw.replaceAll(".", "*") ) + "]";
	}
	
}
