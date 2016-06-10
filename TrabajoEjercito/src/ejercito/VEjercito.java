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
			String consalm="SELECT * FROM almacenes";
			try(
					 Connection cn = this.conectar();
					Statement st=cn.createStatement(
							ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					ResultSet rs= st.executeQuery(consulta);
				)
			{
				System.out.println("\n [Nuevo]");
				String nom=teclado.nextLine().trim();
				
				System.out.print("Nombre del vehículo: ");
				String v = teclado.nextLine().trim();
				
				System.out.print("Matrícula: (0000 AAA)");
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
				
				System.out.print("Fecha de matriculación(0000): ");
				int fecha = Integer.parseInt(teclado.nextLine());
				
				try(ResultSet rsa=st.executeQuery(consalm))
				{	
					boolean swalmacen=true;	int idalmac=0;			
					while(swalmacen)
					{	
						System.out.print("\nIntroduzca el número 0 para ver el listado de Almacenes\nIdentificador del almacén: ");
						String almacS=teclado.nextLine().trim().toLowerCase();
						if(almacS.length()>0)
						{
							int almac=Integer.parseInt(almacS);
							if(almac==0)
							{
								System.out.printf("\n|%-12s | %13s        | %15s      | %20s|\n", "Identificador", "Nombre", "Dirección", "Jefe de almacén");
								System.out.printf("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
									while(rsa.next())
									{
										int Id=rsa.getInt("Id_Almacén");
										String nombre=rsa.getString("Nombre");
										String direccion=rsa.getString("Dirección");
										String jefe=rsa.getString("Jefe de almacén");
									
										//Salida en consola
										System.out.printf("|%-13d | %12s  | %20s | %20s|\n", Id, nombre, direccion, jefe);
									}			
							}
							else
							{
								idalmac=almac;
								swalmacen=false;
							}
						}
					}
					ResultSet rs2 = st.executeQuery(consulta);
					
						rs2.moveToInsertRow();
						rs2.updateString("Nombre", v);
						rs2.updateString("Matrícula", mat);
						rs2.updateBoolean("Turbo", turbo);
						rs2.updateInt("FechaMatriculación", fecha);
						rs2.updateInt("IdAlmacén", idalmac);
						
						rs2.insertRow();
						
						System.out.println("¡Datos insertados en la tabla con éxito!");
				}
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
		
		private void encabezado()
		{
			System.out.printf("|%-20s | %8s | %6s | %10s | %s |\n", "Vehículo", "Matrícula", "Turbo", "FechaMatriculación", "IdAlmacén");
			System.out.print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
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
					int fecha=rs.getInt("FechaMatriculación");
					int almacen=rs.getInt("IdAlmacén");
					
					//Salida en consola
					System.out.printf("|%-20s | %9s | %6B | %10d         | %4d      |\n", nombre, matricula, turbo, fecha, almacen);
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
		
		//Consulta SQL parametrizada por concatenacion (Statement)
		public void eliminarUnoAUno()
		{
			int cont=0, borrados=0;
			
			System.out.println("\n[Eliminar (uno a uno)/(Incluye)]");
			
			System.out.print("¿Vehículo? ");
			String buscar=teclado.nextLine().trim().toLowerCase();
			
			String s="SELECT * FROM vehículos WHERE Nombre LIKE '%"+buscar+"%'ORDER BY Nombre";
			
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
					int fecha = rs.getInt("FechaMatriculación");
					int almacen = rs.getInt("IdAlmacén");
					
					//Salida en consola
					System.out.printf("|%-20s | %9s | %6B | %10d         | %4d      |\n", nombre, matricula, turbo, fecha, almacen);
					
					System.out.print("¿Eliminar? (s/N)");
					String respuesta=teclado.nextLine().toLowerCase().trim();
					if (respuesta.length()>0)
					{
						char resp = respuesta.charAt(0);
						if(resp=='s')
						{
							rs.deleteRow();
							System.out.print("\nVechículo borrado\n");
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
						int fechamatriculacion = rs.getInt("FechaMatriculación");
						int almacen = rs.getInt("IdAlmacén");
					 
					 //Salida en consola
					 System.out.printf("\n"
					 		+ "Vehículo: %s\nMatrícula: %s\nTurbo: %B\nFecha de matriculación: %d\nAlmacén: %d\n", nombre, matricula, turbo, fechamatriculacion, almacen);
					 System.out.println();
					 
					 System.out.print("¿Modificar? (s/N)");
					 String respuesta=teclado.nextLine().toLowerCase().trim();
					 if(respuesta.length()>0)
					 {
						 char resp = respuesta.charAt(0);					 
						 if(resp=='s')
						 {
							 System.out.print("¿Nombre del vehículo?["+nombre+"]");
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
							
							 System.out.print("¿Fecha de matriculación? ["+fechamatriculacion+"]");
							 String fecha = teclado.nextLine().trim().toLowerCase();
							 if(fecha.length()>0)
							 {
								 int fechamat = Integer.parseInt(fecha);
								 rs.updateInt("FechaMatriculación", fechamat);
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
		 
		 //Modificación en grupo
		 public void eliminarGrupo()
		 {
			 System.out.print("Dame dos fechas (fechas entre las que se matricularon los vehículos)\nFecha (mínima)####: ");
			 int fecha1 = Integer.parseInt(teclado.nextLine());
			 System.out.print("Fecha (máxima)####: ");
			 int fecha2 = Integer.parseInt(teclado.nextLine());
			 
			 //Tratamiento fechas
			 if(fecha1>fecha2)
			 {
				 int fechabuffer=fecha2;
				 fecha2=fecha1;
				 fecha1=fechabuffer;
			 }
			 String cons="SELECT * FROM vehículos WHERE FechaMatriculación BETWEEN " + fecha1 + " AND " + fecha2 + " ORDER BY Nombre";
			 String cons2="DELETE FROM vehículos WHERE FechaMatriculación BETWEEN " + fecha1 + " AND " + fecha2 + "";
			 int cont=0, eli=0;
			 try
			 (
					 Connection cn = this.conectar();
						Statement st=cn.createStatement(
								ResultSet.TYPE_SCROLL_SENSITIVE,
								ResultSet.CONCUR_UPDATABLE);
						ResultSet rs= st.executeQuery(cons);
			)
			 {
				 while(rs.next())
				 {
					 if(cont==0)
						 cabecera("[Eliminar en grupo]");
					 cont++;
					 
					 //Ver datos
						String nombre = rs.getString("Nombre");
						String matricula = rs.getString("Matrícula");
						boolean turbo = rs.getBoolean("Turbo");
						int fechamatriculacion = rs.getInt("FechaMatriculación");
						int almacen = rs.getInt("IdAlmacén");
					 
					 //Salida en consola
					 System.out.printf("\nVehículo: %s\nMatrícula: %s\nTurbo: %B\nFecha de matriculación: %d\nAlmacén: %d\n", nombre, matricula, turbo, fechamatriculacion, almacen);

				 }
				 if(cont>0)
				 {
					 System.out.printf("\n¿Desea eliminarlos? (s/N)");
					 String resp = teclado.nextLine().trim().toLowerCase();
					 if(resp.length()>0)
					 {
						 char r=resp.charAt(0);
						 if(r=='s')
						 {
						 
							 eli=st.executeUpdate(cons2);
						 }
					 }
				 }
				 System.out.printf("\nSe han encontrado %d vehículos y se han eliminados %d\n", cont, eli);
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
		 
		 
		 //Consulta SQL. Procedimiento almacenado (CallableStatement) 
		 //DELIMITER $$
		 //CREATE DEFINER='dam'@'localhost' PROCEDURE AgrupacionVehiculosPorTurbo()
		 //BEGIN
		 //SELECT count(Turbo) AS Total
		 //FROM vehículos
		 //GROUP BY Turbo;
		 //END
		 
		 public void moda()
         {
             try(
                     Connection cn=this.conectar();
                     CallableStatement cs=cn.prepareCall("{call AgrupacionVehiculosPorTurbo}");
                     ResultSet rs=cs.executeQuery();
                     )
             {
                 int tur1=0;
                 int tur2=0;
                 while(rs.next())
                 {
                     int tur=rs.getInt("Total");
                     if(tur1==0)
                     {
                         System.out.printf("\nHay %d vehículos sin turbo", tur);
                         tur1=tur;
                     }
                     else
                     {
                        System.out.printf("\nHay %d vehículos con turbo\n", tur);
                        tur2=tur;
                     }
                 } 
                 int turSum= tur1+ tur2;
                 if(tur1>tur2)
                 {
                	 int dif=tur1-tur2;
                	 double tanto=100*dif/turSum;
                   System.out.printf("\nHay un %f%% mas de vehículos del tipo sin turbo \nEn total hay %d vehículos\n", tanto, turSum);
                 }
                 else
                 {
                	 int dif=tur2-tur1;
                	 double tanto=100*dif/turSum;
                   System.out.printf("\nHay un %.2f%% mas de vehículos del tipo con turbo \nEn total hay %d vehículos\n", tanto, turSum);
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
		 
		 
		 //Modifi cación en grupo
		 public void modificarGrupo()
		    {
			 cabecera("Modificación en grupo");
			 
			 int valor=0;
		        
			 	System.out.print("\nIntroduzca el número 0 para ver el listado de Almacenes\nIdentificador del almacén del que desea coger los vehículos: ");
		        String buscar=teclado.nextLine().trim();
		        if(buscar.length()>0)
		        {
		            valor=Integer.parseInt(buscar);
		            
		            String consAlm="SELECT * FROM almacenes";
		                try(
		                        Connection cn=this.conectar();
		                        Statement st=cn.createStatement();
		                        ResultSet rs=st.executeQuery(consAlm);
		                        )
		                {
		                    if(valor==0)
		                    {
		                        int cont=0;
		                        while(rs.next())
		                        {
		                            if(cont==0)
		                            {
		                                System.out.printf("\n|%-12s | %13s        | %15s      | %20s|\n", "Identificador", "Nombre", "Dirección", "Jefe de almacén");
		                                System.out.printf("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		                            }
		                            cont++;
		                    
		                            int id=rs.getInt("Id_Almacén");
		                            String nombre=rs.getString("Nombre");
		                            String direccion=rs.getString("Dirección");
		                            String jefe=rs.getString("Jefe de almacén");
		                    
		                            System.out.printf("|%-13d | %12s  | %20s | %20s|\n", id, nombre, direccion, jefe);
		                        }
		                    
		                        System.out.printf("\nSe han listado %d almacenes\n", cont);
		                
		                        System.out.println("\nIndica el identificador del almacén que desea: ");
		                        String id=teclado.nextLine().trim();
		                        if(id.length()>0)
		                        {
		                            valor=Integer.parseInt(id);
		                        }
		                    }
		                    //Sacamos valor, de un lado o del otro;
		                    String cons="SELECT * FROM vehículos WHERE IdAlmacén="+valor+"";
		                    ResultSet rs2 = st.executeQuery(cons);
		                    this.encabezado();
		                    int cont=this.verDatos(rs2);
		                    System.out.printf("Se han encontrado %d vehículos \n", cont);
		                    
		                    System.out.print("Introduzca el número 0 para ver el listado de Almacenes\nIdentificador del almacén al que desea mover los vehículos: ");
		                    buscar=teclado.nextLine().trim();
		                    
		                    if(buscar.length()>0)
		                    {
		                        int valor2=Integer.parseInt(buscar);
		                    
		                        ResultSet rs3 = st.executeQuery(consAlm);
		                        if(valor2==0)
		                        {
		                        
		                            cont=0;
		                            while(rs3.next())
		                            {
		                                if(cont==0)
		                                {
		                                	System.out.printf("\n|%-12s | %13s        | %15s      | %20s|\n", "Identificador", "Nombre", "Dirección", "Jefe de almacén");
			                                System.out.printf("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		                                }
		                                cont++;
		                            
		                                int id=rs3.getInt("Id_Almacén");
		                                String nombre=rs3.getString("Nombre");
		                                String direccion=rs3.getString("Dirección");
		                                String jefe=rs3.getString("Jefe de almacén");
		                            
		                                System.out.printf("|%-13d | %12s  | %20s | %20s|\n", id, nombre, direccion, jefe);
		                            }
		                            System.out.printf("Se han listado %d almacenes\n", cont);
		                        
		                            System.out.println("\nIndica el identificador del almacén que desea: ");
		                            String id=teclado.nextLine().trim();
		                            if(id.length()>0)
		                                {
		                                    valor2=Integer.parseInt(id);
		                                }
		                        }
		                        //Valor de los dos lados
		                        String consAztu="UPDATE vehículos set IdAlmacén = "+valor2+" WHERE IdAlmacén = "+valor+"";
		                        int cont2=st.executeUpdate(consAztu);
		                        
		                        System.out.printf("¡%d vehículos se han movido del almacén %d al almacén %d con éxito!", cont, valor, valor2);
		                    }
		                }        
		                catch (SQLException e)
		                {
		                    System.out.print("Error: "+e.getMessage());
		                }
		                catch (Exception e)
		                {
		                    System.out.print("BD error: "+e.getMessage());
		                }
		            }
		        }
		//Consulta Sql parametrizada (PreparedStatement)
	       public void busqueda()
	       {
	          int cont=0;
	          try(
	              Connection cn=this.conectar();
	              )
	          {
	              System.out.println("\n[Busqueda de soldaditos por medalla]");
	              ////FALTA DE TIEMPO, POR ESO NO LES SON MOSTRADOS TAMBIEN POR BASE DE DATOS
	              System.out.println(" 1. Real y Distinguida Orden Española de Carlos III \n 2. Real Orden de Isabel la Católica \n 3. Orden del Mérito Civil \n 4. Orden de cisneros \n 5. Orden civil de sanidad \n");
	              ///
	              System.out.println("¡Medalla a buscar por identificador!");
	              String buscar =teclado.nextLine().trim();
	         
	              if(buscar.length()>0)
	              {
	                 int buscarInt= Integer.parseInt(buscar);
	                  String cons="SELECT * FROM militares WHERE Id_Medallas= ? ORDER BY Nombre";
	                  PreparedStatement ps=cn.prepareStatement(cons);
	                 
	                 //Asignamos valores parametros
	                 ps.setInt(1, buscarInt);
	                  try (ResultSet rs=ps.executeQuery())
	                  {
	                      System.out.printf("Soldaditos que tienen la medalla con el identificador %d\n", buscarInt);
	                     cont=0;
	                       while(rs.next())
	                       {
	                           if(cont==0)
	                           {
	                        	   System.out.printf("\n|%-10s | %10s |%10s  | %10s | %20s|\n", "Nombre", "Apellidos", "DNI", "Telefono", "dirección");
	                                System.out.printf("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
	                           }
	                           
	                           cont++;
	                           
	                           String nombre=rs.getString("Nombre");
	                           String apellidos=rs.getString("Apellidos");
	                           String dni=rs.getString("DNI");
	                           int telefono=rs.getInt("Teléfono");
	                           String direccion=rs.getString("Dirección");
	                           
	                           System.out.printf("\n|%10s | %10s | %10s | %10d | %20s|", nombre, apellidos, dni, telefono, direccion);
	                           
	                       }
	                  }
	           }
	              //Listado resumen
	              System.out.printf("\nEn lista: %2d soldados\n", cont);
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
}