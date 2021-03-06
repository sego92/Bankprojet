package bankproject.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import bankproject.entities.Account;
import bankproject.entities.BankEntity;
import bankproject.entities.Customer;
import bankproject.exceptions.SrvException;

public class SrvAccount extends BankService {
	private static SrvAccount INSTANCE = new SrvAccount();
	
	public static SrvAccount getINSTANCE() {
		return INSTANCE;
	}


	public static void setINSTANCE(SrvAccount iNSTANCE) {
		INSTANCE = iNSTANCE;
	}
	
	
	public String createTableAccount() throws SQLException {
		StringBuilder sql =  new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS account (");
		sql.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
		sql.append("AccountNumber VARCHAR(255),");
		sql.append("Solde DOUBLE,");
		sql.append("customerID INTEGER,");
		sql.append("FOREIGN KEY (customerID) REFERENCES customer (id) ON DELETE CASCADE");
		sql.append(")");
		
		Statement st = SQLiteManager.getConnection().createStatement();
		st.execute(sql.toString());
		return sql.toString();
	}

	
	public void createAccount (Account entity) throws SQLException {
    	// TODO 
    	PreparedStatement ps = null;
		Connection connection = null;
		String sql = "INSERT INTO account (accountNumber, customerID, solde) VALUES (?, ?, ?)";
		try {
			connection = SQLiteManager.getConnection();
			ps = connection.prepareStatement(sql.toString());
			ps.setString(1, entity.getAccountNumber());
			ps.setInt(2, entity.getCustomer().getId());
			ps.setDouble(3, entity.getSolde());
			ps.execute();
			ResultSet rs=ps.getGeneratedKeys();
			if (rs.next()) {
				entity.setId(rs.getInt(1));
			}  else {
                System.out.println("Creating user failed, no ID obtained.");
			}
		}catch (SQLException e) {
			
		} finally {
			if (ps != null) {
				ps.close();
			}
			
			if (connection != null) {
				connection.close();
			}
		}
    }
    
    
    public void updateAccount (Account entity) throws SQLException {
    	// TODO 
    	PreparedStatement ps = null;
		Connection connection = null;
		String sql = "UPDATE account SET accountNumber=?, customerID=?, solde=? WHERE ID=?";
		try {
			connection = SQLiteManager.getConnection();
			ps = connection.prepareStatement(sql.toString());
			ps.setString(1, entity.getAccountNumber());
			ps.setInt(2, entity.getCustomer().getId());
			ps.setDouble(3, entity.getSolde());
			ps.setInt(4, entity.getId());
			ps.execute();
		}catch (SQLException e) {
			
		} finally {
			if (ps != null) {
				ps.close();
			}
			
			if (connection != null) {
				connection.close();
			}
		}
    }
    
    
    protected Account readEntity (ResultSet rs) throws Exception {
    	Account account = new Account();
    	
    	Integer customerID = rs.getInt("customerID");
 
    	SrvCustomer srvCustomer = SrvCustomer.getINSTANCE();
    	Customer customer = (Customer) srvCustomer.get(customerID);
    	
		account.setId(rs.getInt("id"));
		account.setCustomer(customer);
		account.setAccountNumber(rs.getString("accountNumber"));
		account.setSolde(rs.getDouble("solde"));
	
		return account;
    }


	@Override
	public void save(BankEntity entity) throws SrvException, SQLException {
		if (entity instanceof Account) {
			Account account = (Account)entity;
			if (account.getId() == null) {
				createAccount(account);
			} else {
				updateAccount(account);
			}
		} else {
			throw new SrvException();
		}
	
		
	}
	
    
	public void deleteAccount (String accountNumber, String lastName, String firstName, Double solde, int id) throws SQLException {
    	// TODO 
    	PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = SQLiteManager.getConnection();

			StringBuilder sql =  new StringBuilder();
			
			sql.append("DELETE account WHERE id = ?");
			ps = connection.prepareStatement(sql.toString());
			ps.setString(1, accountNumber);
			ps.setString(2, lastName);
			ps.setString(3, firstName);
			ps.setDouble(4, solde);
			ps.setInt(5, id);
			ps.execute();
		}catch (SQLException e) {
			
		} finally {
			if (ps != null) {
				ps.close();
			}
			
			if (connection != null) {
				connection.close();
			}
		}
    }
	
	
	public Account get(String accountNumber) throws Exception {
		Connection connection = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Account result = null;
		
		StringBuilder query = new StringBuilder("SELECT * FROM ");
		query.append(getBankTable());
		query.append(" WHERE accountNumber = ?");
		
		try {
			connection = SQLiteManager.getConnection();
			pst = connection.prepareStatement(query.toString());
			pst.setString(1, accountNumber);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				result = readEntity(rs);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (pst != null) {
				pst.close();
			}
			if (connection != null) {
				connection.close();
			}
			
		}
		
		return result;
	}
}