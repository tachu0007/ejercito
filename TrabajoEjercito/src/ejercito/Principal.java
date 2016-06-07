package ejercito;

import ejercito.MiMenu;
import ejercito.VEjercito;

public class Principal {

	public static void main(String[] args) {
		
		VEjercito VE=null;
		try
		{
			VE=new VEjercito();
			System.out.println("Conexi�n con la base de datos> �OK!");
		}
		catch (Exception e)
		{
			System.err.println("Error: "+e.getMessage());
			//fin del programa
			return;
		}
		
		//Men�
		MiMenu menu = new MiMenu();
		menu.add("Listado");
		menu.add("A�adir veh�culo");
		menu.add("Modificar veh�culo (uno a uno)");
		menu.add("Eliminar veh�culo(uno a uno)");
		menu.add("Veh�culo cuyo nombre empieza por");
		menu.add("Veh�culo cuyo nombre incluya");
		menu.add("Agrupaci�n");
		menu.add("Salida del programa");
		
		MiMenu reducido = new MiMenu();
		reducido.add("Nuevo");
		reducido.add("Salida");
		
		boolean salir = false;
		do
		{
			if (VE. estaVacia()==true)
			{
				System.out.println("BD vac�a");
				switch(reducido.ver("Veh�culos Ej�rcito"))
				{
				case 1:VE.a�adir();
				break;
				default:
					salir=true;
					break;
				}
			}
			else
			{
				switch(menu.ver("Veh�culos Ej�rcito"))
				{
					case 1: VE.listado();break;
					case 2: VE.a�adir();break;
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
		System.out.println("Fin del programa. SERGIO TEJERINA y CARLOS CA�ETE");
	}//Fin del main
}//Fin de la clase

