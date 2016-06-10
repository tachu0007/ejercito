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
		menu.add("Listado de veh�culos ");
		menu.add("A�adir veh�culo");
		menu.add("Modificar veh�culo (uno a uno)");
		menu.add("Modificar vehiculos (moverlos de un almac�n a otro por grupo)");
		menu.add("Eliminar veh�culo(uno a uno)");
		menu.add("Eliminar veh�culos entre un rango de a�os de matriculaci�n (por grupo)");
		menu.add("B�squeda de soldados por medallas");
		menu.add("Moda por turbo");
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
					case 4: VE.modificarGrupo();break;
					case 5: VE.eliminarUnoAUno();break;
					case 6: VE.eliminarGrupo();break;
					case 7: VE.busqueda();break;
					case 8: VE.moda();break;
					default:
						salir=true;
					break;
				}
			}
		}
		while (!salir);
		System.out.println("Fin del programa.\nSERGIO TEJERINA Mart�nez y CARLOS CA�ETE Garc�a-Arisco les desean un buen d�a");
	}//Fin del main
}//Fin de la clase