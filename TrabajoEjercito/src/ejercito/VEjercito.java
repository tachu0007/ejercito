package ejercito;

import java.sql.*;
import java.util.Scanner;

public class VEjercito {

		private Scanner teclado= new Scanner(System.in);
		
		//Constructor
		public VEjercito () throws SQLException 
		{
			this.conectar();
		}
		
		//Conexion
		private Connection conectar() throws SQLException
		{
			String CadenaConexion = "jdbc:mysql://localhost:3306/ejercito"
									+"?useUnicode=true&characterEncoding=UTF-8";
			String usuario="dam";
			String passwd="abcd1234";
			
			return DriverManager.getConnection(CadenaConexion, usuario, passwd);
		}
		
		//Calculos(Statement)
		public boolean estaVacia()
		{
			String consulta= "SELECT count(*) as total FROM vehículos";
			boolean sw=true; 
			try (
					Connection cn=this.conectar();
					Statement st=cn.createStatement();
					ResultSet rs= st.executeQuery(consulta);
				)
			{
				int total=0;
				if(rs.next())
					total=rs.getInt("total");
				sw =(total==0);
			}
			catch (SQLException e)
			{
				System.out.println("Error: "+ e.getMessage());
			}
			catch (Exception e)
			{
				System.out.println("Error: "+e.getMessage());
			}
			return sw;
		}
		
		//Insercción con métodos (Statement con ResultSet modificable)
		public void añadir() 
		{
			String consulta="SELECT * FROM vehículos ORDER BY nombre";
			try(
					 Connection cn = this.conectar();
					Statement st=cn.createStatement(
							ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					ResultSet rs= st.executeQuery(consulta);
				)
			{
				System.out.println("\n [Nuevo]");
				String localidad=teclado.nextLine().trim();
				
				System.out.print("Nombre del vehículo: ");
				String v = teclado.nextLine().trim();
				
				System.out.print("Matrícula: ");
				String mat = teclado.nextLine().trim();
				
				System.out.print("Turbo: (s/N)" );
				String tur=teclado.nextLine().trim().toLowerCase();
				boolean turbo = false;
				if(tur.length()>0)
				{
					char t = tur.charAt(0);
					if(t=='s')
					{
						turbo=true;
					}
				}
				
				System.out.print("IdAlmacen: ");
				int almac = Integer.parseInt(teclado.nextLine());
					
				rs.moveToInsertRow();
				rs.updateString("Nombre", v);
				rs.updateString("Matrícula", mat);
				rs.updateBoolean("Turbo", turbo);
				rs.updateInt("IdAlmacén", almac);
				
				rs.insertRow();
				
				System.out.println("¡Datos insertados en la tabla con éxito!");
			}
			catch(SQLException e)
			{
				System.out.println("Error BD: "+e.getMessage());
			}
			catch (Exception e)
			{
				System.out.println("Error: "+e.getMessage());
			}
		}
		
		//Recorrido(ResulSet)
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
					String nombre=rs.getString("Nombre");
					String matricula=rs.getString("Matrícula");
					boolean turbo=rs.getBoolean("Turbo");
					int almacen=rs.getInt("IdAlmacén");
					
					//Salida en consola
					System.out.printf("%-20s %6s %6B %d\n", nombre, matricula, turbo, almacen);
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
		
		private void encabezado()
		{
			System.out.printf("%-20s %6s %6s %-20s\n", "Vehículo", "Matrícula", "Turbo", "IdAlmacén");
			System.out.printf("%-20s %6s %6s %-20s\n", "_________", "______", "______", "__________");
		}
		
		private void cabecera (String titulo)
		{
			System.out.printf("\n [%s]\n", titulo);
		}
		
		//Consulta sql(statement)
		public void listado()
		{
			String s="SELECT * FROM vehículos ORDER BY Nombre";
			
			try
			(
					//TYPE_FORWARD_ONLY CONCUR_READ_ONLY
					Connection cn=this.conectar();
					Statement st=cn.createStatement();
					ResultSet rs=st.executeQuery(s);
			)
			{
				cabecera("Listado");
				int cont=verDatos(rs);
				System.out.printf("En lista: %2d vehículos\n", cont);
			}
			catch(SQLException e)
			{
				System.out.println("\nError BD: "+e.getMessage());
			}
			catch(Exception e)
			{
				System.out.print("\nError: "+e.getMessage());
			}
		}
		
		//Consulta Sql parametrizada (PreparedStatement)
		public int empiezaPor()
		{
			System.out.println("\n[Buscar (Empieza por)]");
			System.out.println("¿Vehículo?(Empieza por)");
			String buscar = teclado.nextLine().trim().toLowerCase();
			
			String s="select * from vehículos where Nombre like ? order by Nombre";
			
			
			int cont=0;
			try(
				Connection cn=this.conectar();
				PreparedStatement ps=cn.prepareStatement(s);
				)
			{
				//Valor para los parámetros
				ps.setString(1, buscar+"%");
				
				//Lanzamos la consulta
				try (ResultSet rs=ps.executeQuery())
				{
					cabecera("Buscar> empieza por");
					cont= verDatos(rs);
				}
				System.out.printf("En lista: %2d vehículos\n", cont);
			}
			catch(SQLException e)
			{
				System.out.println("\nError BD: "+e.getMessage());
			}
			catch(Exception e)
			{
				System.out.print("\nError: "+e.getMessage());
			}
			return cont;
		}
		
		//Consulta SQL parametrizada (por concatenación)
		public int incluye()
		{
			System.out.println("\n[Buscar (Incluye)]");
			System.out.println("¿Vehículo? (Incluye)");
			String buscar = teclado.nextLine().trim().toLowerCase();
			
			String s="SELECT * FROM vehículos WHERE Nombre LIKE '%"+buscar+"%' ORDER BY Nombre";
			
			int cont=0;
			try
			(
				Connection cn=this.conectar();
				Statement st=cn.createStatement();
				ResultSet rs=st.executeQuery(s);
			)
			{
				cabecera("Buscar> incluye");
				cont = verDatos(rs);
				System.out.printf("En lista: %2d vehículos\n", cont);
			}
			catch(SQLException e)
			{
				System.out.println("\nError BD: "+e.getMessage());
			}
			catch(Exception e)
			{
				System.out.print("\nError: "+e.getMessage());
			}
			return cont;
		}
		
		//Consulta SQL parametrizada por concatenacion (Statement)
		public void eliminarUnoAUno()
		{
			int cont=0, borrados=0;
			
			System.out.println("\n[Eliminar (uno a uno)/(Empieza por)]");
			
			System.out.print("¿Vehículo? ");
			String buscar=teclado.nextLine().trim().toLowerCase();
			
			String s="SELECT * FROM vehículos WHERE Nombre like '"+buscar+"%'ORDER BY Nombre";
			
			try
			(
					Connection cn=this.conectar();
					Statement st=cn.createStatement(
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_UPDATABLE);
					ResultSet rs=st.executeQuery(s);
			)
			{
				while (rs.next())
				{
					if (cont==0)
					{
						cabecera("Eliminar por nombre de vehículo");
						encabezado();
					}
					cont++;
					
					//Ver datos
					String nombre = rs.getString("Nombre");
					String matricula = rs.getString("Matrícula");
					boolean turbo = rs.getBoolean("Turbo");
					int almacen = rs.getInt("IdAlmacén");
					
					//Salida en consola
					System.out.printf("%-20s %6s %6B %-20d\n", nombre, matricula, turbo, almacen);
					
					System.out.print("¿Eliminar? (s/N)");
					String respuesta=teclado.nextLine().toLowerCase().trim();
					if (respuesta.length()>0)
					{
						char resp = respuesta.charAt(0);
						if(resp=='s')
						{
							rs.deleteRow();
							System.out.print("Vechículo borrado");
							borrados++;
						}
					}
				}
				if (cont==0)
					System.out.println("No se ha encontrado ningún vehículo");
			}
			catch(SQLException e)
			{
				System.out.println("\nError BD: "+e.getMessage());
			}
			catch(Exception e)
			{
				System.out.print("\nError: "+e.getMessage());
				e.printStackTrace();
			}
			System.out.printf("\nBorrados: %2d vehículo/s de %2d\n", borrados, cont);
		}
		
		//Consulta SQL parametrizada por concatenacion (statement)
		 public void modificarUnoAUno()
		 {
			 int cont=0, modificados=0; 
			 System.out.println("\n [Modificar(uno a uno)/(Empieza por)]");
			 
			 System.out.print("¿Vehículo? (Empieza por) ");
			 String buscar = teclado.nextLine().trim().toLowerCase();
			 
			 String s ="SELECT * FROM vehículos WHERE Nombre LIKE '"+buscar+"%' ORDER BY Nombre";
			 
			 try
			 (
					 Connection cn = this.conectar();
						Statement st=cn.createStatement(
								ResultSet.TYPE_SCROLL_SENSITIVE,
								ResultSet.CONCUR_UPDATABLE);
						ResultSet rs= st.executeQuery(s);
			 )
			 {
				 while(rs.next())
				 {
					 if(cont==0)
						 cabecera("Modificador");
					 cont++;
					 
					 //Ver datos
						String nombre = rs.getString("Nombre");
						String matricula = rs.getString("Matrícula");
						boolean turbo = rs.getBoolean("Turbo");
						int almacen = rs.getInt("IdAlmacén");
					 
					 //Salida en consola
					 System.out.printf("Vehículo: %s\nMatrícula: %s\nTurbo: %B\nAlmacén: %d\n", nombre, matricula, turbo, almacen);
					 
					 System.out.print("¿Modificar? (s/N)");
					 String respuesta=teclado.nextLine().toLowerCase().trim();
					 if(respuesta.length()>0)
					 {
						 char resp = respuesta.charAt(0);					 
						 if(resp=='s')
						 {
							 System.out.print("¿Vehículo?["+nombre+"]");
							 nombre=teclado.nextLine().trim();
							 	if (nombre.length()>0)
							 	{
							 		rs.updateString("Nombre", nombre);
							 	}
							 	
							 System.out.print("¿Matrícula? ["+matricula+"]");
							 matricula=teclado.nextLine().trim();
							 if(matricula.length()>0)
							 {
								 rs.updateString("Matrícula", matricula);
							 }
							 
							 System.out.print("¿Turbo? ["+turbo+"] (s/N)");
							 String tur = teclado.nextLine().trim().toLowerCase();
							 if(tur.length()>0)
							 {
								 char t = tur.charAt(0);
								 if(t=='s')
								 {
									 turbo=true;
								 }
								 else if(t=='n')
								 {
									 turbo=false;
								 }
								 rs.updateBoolean("Turbo", turbo);
							 }
								 
								 System.out.print("¿Almacén? ["+almacen+"]");
								 String almac=teclado.nextLine();
								 if(almac.length()>0)
									 rs.updateInt("IdAlmacén", Integer.parseInt(almac));
								 
								 rs.updateRow();
								 modificados++;
						 }
					 }
				 }
			 }
			 	catch(SQLException e)
				{
					System.out.println("Error BD: "+e.getMessage());
				}
				catch(Exception e)
				{
					System.out.print("Error: "+e.getMessage());
					e.printStackTrace();
				}
				System.out.printf("Modificados: %2d vehículo/s de %2d\n", modificados, cont);
		 }
		 
		 //Consulta SQL. Procedimiento almacenado (CallableStatement)
		 
		 //Trigger
		 //CREATE DEFINER='dam'@'localhost' PROCEDURE 'AgrupacionVehiculosPorTurbo'()
		 //BEGIN
		 //SELECT count(Turbo) AS Total
		 //FROM vehículos
		 //GROUP BY Turbo
		 //ORDER BY Nombre;
		 //END
		 
		 public void agrupacion()
		 {
			 try(
					 Connection cn=this.conectar();
					 CallableStatement cs=cn.prepareCall("{call AgrupacionVehiculosPorTurbo}");
					 ResultSet rs=cs.executeQuery(); 
					 )
			 {
				 if (rs.next())
				 {
					 System.out.println("\n [Agrupación por turbo]");
					 
					 System.out.printf("\nSin turbo> %5d", rs.getInt("MediaMax"));
					 System.out.printf("\nCon turbo> %5d\n", rs.getInt("MediaMin"));
				 }
				 else
				 {
					 System.out.println("Sin resultados");
				 }
			 }
			 catch(SQLException e)
				{
					System.out.println("Error BD: "+e.getMessage());
				}
			 catch(Exception e)
				{
					System.out.print("Error: "+e.getMessage());
					e.printStackTrace();
				}
		 }
		 
	}