package ejercito;

import ejercito.MiMenu;
import ejercito.VEjercito;

public class Principal {

	public static void main(String[] args) {
		
		VEjercito VE=null;
		try
		{
			VE=new VEjercito();
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
		menu.add("Añadir vehículo");
		menu.add("Modificar vehículo (uno a uno)");
		menu.add("Eliminar vehículo(uno a uno)");
		menu.add("Vehículo cuyo nombre empieza por");
		menu.add("Vehículo cuyo nombre incluya");
		menu.add("Agrupación");
		menu.add("Salida del programa");
		
		MiMenu reducido = new MiMenu();
		reducido.add("Nuevo");
		reducido.add("Salida");
		
		boolean salir = false;
		do
		{
			if (VE. estaVacia()==true)
			{
				System.out.println("BD vacía");
				switch(reducido.ver("Vehículos Ejército"))
				{
				case 1:VE.añadir();
				break;
				default:
					salir=true;
					break;
				}
			}
			else
			{
				switch(menu.ver("Vehículos Ejército"))
				{
					case 1: VE.listado();break;
					case 2: VE.añadir();break;
					case 3: VE.modificarUnoAUno();break;
					case 4: VE.eliminarUnoAUno();break;
					case 5: VE.empiezaPor();break;
					case 6: VE.incluye();break;
					case 7: VE.agrupacion();break;
					default:
						salir=true;
					break;
				}
			}
		}
		while (!salir);
		System.out.println("Fin del programa. SERGIO TEJERINA y CARLOS CAÑETE");
	}//Fin del main
}//Fin de la clase

