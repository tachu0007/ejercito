package ejercito;

import temperatura.MiMenu;
import temperatura.Periodico;

public class Principal {

	public static void main(String[] args) {
		
		Periodico per=null;
		try
		{
			per=new Periodico();
			System.out.println("Conexión con la base de datos> ¡OK!");
		}
		catch (Exception e)
		{
			System.err.println("Error: "+e.getMessage());
			//fin del programa
			return;
		}
		
		//Menú
		MiMenu menu = new MiMenu();
		menu.add("Listado");
		menu.add("Nuevo");
		menu.add("Modificar (uno a uno)");
		menu.add("Eliminar (uno a uno)");
		menu.add("Empieza por");
		menu.add("Incluye");
		menu.add("Medias");
		menu.add("Salida");
		
		MiMenu reducido = new MiMenu();
		reducido.add("Nuevo");
		reducido.add("Salida");
		
		boolean salir = false;
		do
		{
			if (per. estaVacia()==true)
			{
				System.out.println("BD vacía");
				switch(reducido.ver("Periódico DAM"))
				{
				case 1:per.añadir();
				break;
				default:
					salir=true;
					break;
				}
			}
			else
			{
				switch(menu.ver("Periodico DAM"))
				{
					case 1: per.listado();break;
					case 2: per.añadir();break;
					case 3: per.modificarUnoAUno();break;
					case 4: per.eliminarUnoAUno();break;
					case 5: per.empiezaPor();break;
					case 6: per.incluye();break;
					case 7: per.medias();break;
					default:
						salir=true;
					break;
				}
			}
		}
		while (!salir);
		System.out.println("CMA-DAM. Fin del programa");
	}//Fin del main
}//Fin de la clase

