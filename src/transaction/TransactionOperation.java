package transaction;
import java.util.*;
import java.sql.*;
import java.sql.Date;
public class TransactionOperation {

	public static void main(String[] args) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","system","wahidur");
			Scanner sc=new Scanner(System.in);
			PreparedStatement ps1 =con.prepareStatement("select * from bank51 where accno=?");
			PreparedStatement ps2=con.prepareStatement("update bank51 set bal=bal+? where accno=?");
			
			//PreparedStatement ps0=con.prepareStatement("select sysdate from dual");
			//Date dt=new Date();
			
			PreparedStatement ps3=con.prepareStatement("insert into translog51 values(?,?,?,dt)");
			
			
			con.setAutoCommit(false);
			Savepoint sp=con.setSavepoint();
			System.out.println("Enter home Account number");
			long hAccNo=sc.nextLong();
			ps1.setLong(1, hAccNo);
			ResultSet rs1=ps1.executeQuery();
			if(rs1.next()) {
				float bal=rs1.getFloat(3);
				System.out.println("Enter amoutn to be transfer ..");
				float amt=sc.nextFloat();
				if(amt<=bal) {
					System.out.println("Enter bAccount Number");
					long bAccNo=sc.nextLong();
					ps1.setLong(1, bAccNo);
					ResultSet rs2=ps1.executeQuery();
					if(rs2.next()) {
						//subT1 update
						ps2.setFloat(1, -amt);
						ps2.setLong(2, hAccNo);
						int i=ps2.executeUpdate();
						//sutT2 update
						ps2.setFloat(1, amt);
						ps2.setLong(2, bAccNo);
						int j=ps2.executeUpdate();
						if(i==1 && j==1) {
							
							
							ps3.setLong(1, hAccNo);
							ps3.setLong(2, bAccNo);
							ps3.setFloat(3,amt);
							int k=ps3.executeUpdate();
							if(k>0) {
								System.out.println("time ....");
								con.commit();
							}
														
							System.out.println("Transaction successfull");
						}else {
							con.rollback(sp);
							System.out.println("transaction failed");
						}
						
					}else {
						System.out.println("Invalid bAccount number");
					}
					
				}else {
					System.out.println("insufficient balance");
				}
			}else {
				System.out.println("Invalid home account number");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
