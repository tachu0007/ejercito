package ejercito;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Listado {

	private void encabezado()
	{
		System.out.printf("%-20s %6s %6s %-20s\n", "Localidad", "Max", "min","Pronostico");
		System.out.printf("%-20s %6s %6s %-20s\n", "_________", "______", "______", "__________");
	}
	
	private int verDatos(ResultSet rs)
	{
		int cont=0;
		try
		{
			while(rs.next())
			{
				//acceso a los campos
				if(cont==0)
					encabezado(); 
				cont++;
				String localidad=rs.getString("Localidad");
				String pronostico=rs.getString("Pronóstico");
				double maxima=rs.getDouble("Máxima");
				double minima=rs.getDouble("Mínima");
				
				//Salida en consola
				System.out.printf("%-20s %6.2f %6.2f %-20s\n", localidad, maxima, minima, pronostico);
			}
		}
		catch (SQLException e)
		{
			System.out.print("Error: "+e.getMessage());
		}
		catch (Exception e)
		{
			System.out.print("Error : "+e.getMessage());
		}
		return cont;
	}
	
}
