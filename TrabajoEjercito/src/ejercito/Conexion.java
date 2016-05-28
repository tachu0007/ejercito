package ejercito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

	//Constructor
		public Conexion () throws SQLException 
		{
			this.conectar();
		}
		
		//Conexión
		private Connection conectar() throws SQLException
		{
			String CadenaConexion = "jdbc:mysql://localhost:3306/ejercito"
									+"?useUnicode=true&characterEncoding=UTF-8";
			String usuario="dam";
			String passwd="abcd1234";
			return DriverManager.getConnection(CadenaConexion, usuario, passwd);
		}
}
