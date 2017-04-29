package com.mageddo;

import java.sql.*;

/**
 * Created by elvis on 24/04/17.
 */
public class AppJdbc {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
//		spring.datasource.driver-class-name=org.h2.Driver

//		Class.forName("org.h2.Driver");
		Connection conn1 = null, conn2 = null;
		try{

			conn1 = getConnection();
			conn1.setAutoCommit(false);
			final int isolation = Connection.TRANSACTION_SERIALIZABLE;
			conn1.setTransactionIsolation(isolation);

			conn2 = getConnection();
			conn2.setAutoCommit(false);
			conn2.setTransactionIsolation(isolation);

			final int id = 2;

			conn1.getMetaData().supportsTransactionIsolationLevel(isolation);

			listCustomers(conn1, id);
			listCustomers(conn2, id);


			updateCustomer(conn1, id, 4);
			updateCustomer(conn2, id, 5);
			conn1.commit();
			conn2.commit();

		}catch (Exception e){
			e.printStackTrace();
			conn1.rollback();
			conn2.rollback();
		} finally {

			if(conn1 != null){
				conn1.close();
			}
			if(conn2 != null){
				conn2.close();
			}
		}

	}

	private static void updateCustomer(Connection conn, int id, double value) throws SQLException {
		try (
			final PreparedStatement stm = conn.prepareStatement("UPDATE CUSTOMERS SET BALANCE = ? WHERE ID = ?")
			) {
//			stm.setQueryTimeout(1);
			stm.setDouble(1, value);
			stm.setInt(2, id);
			System.out.println("update=" + stm.executeUpdate());;
		}

	}

	private static void listCustomers(Connection conn, int id) throws SQLException {

		try(
			final PreparedStatement stm = conn.prepareStatement("SELECT * FROM CUSTOMERS WHERE ID = ?")
			){
//				stm.setQueryTimeout(1);
				stm.setInt(1, id);
				final ResultSet resultSet = stm.executeQuery();
				while(resultSet.next()){
					System.out.printf("id=%d, balance=%.2f\n", resultSet.getInt("ID"), resultSet.getDouble("BALANCE"));
				}
				System.out.println("-----------------------");
		}
	}

	private static Connection getConnection() throws SQLException {
//		return DriverManager.getConnection("jdbc:h2:tcp://h2.dev:9092/h2/data;LOCK_TIMEOUT=300000;MODE=ORACLE", "sa", "");
		return DriverManager.getConnection("jdbc:postgresql://postgresql-server.dev:5432/root", "root", "root");
	}
}
