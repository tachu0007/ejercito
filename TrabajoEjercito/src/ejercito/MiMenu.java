package ejercito;

import java.util.*;

public class MiMenu {
	
	// Versión 2010
	private ArrayList <String> menu = new ArrayList<String>();
	private ArrayList <Integer> codigos = new ArrayList<Integer>();
	
	private int opcion = 0; 
	
	public void add(String entrada)
	{
		this.add(entrada, 0);
	}
	
	public void add(String entrada, int codigo)
	{
		menu.add(new String(entrada));
		codigos.add(new Integer(codigo));
	}
	
	private int leerOpcion()
	{
		Scanner teclado = new Scanner(System.in);
		boolean sw = true;
		do {
			try {
				System.out.print("¿Opción? (1-"+menu.size()+")? ");
				opcion = Integer.parseInt(teclado.nextLine());
				if (!(opcion>=1 && opcion<=menu.size()))
					throw new Exception("Introduzca un valor entre 1 y "+menu.size());
				sw = false;
			} catch (NumberFormatException e) {
				System.out.println("Error: introduzca un valor numï¿½rico");
			} catch (Exception e) {
				System.out.println("Error: "+e.getMessage());
			}
		} while (sw);
		return opcion;
	}
	
	public int ver()
	{
		return this.ver("Menu");
	}
	
	public int ver(String encabezado)
	{
		this.opcion = 0;
		if (menu.size()==0) return this.opcion;
		
		// Encabezado 
		System.out.println("\n"+encabezado);
		for(int i=1;i<=encabezado.length();i++)
			System.out.print("=");
		
		// Menï¿½ de opciones
		for(int i=0;i<menu.size();i++)
		{
			System.out.printf("\n%3d. %s",i+1,menu.get(i));
		}
		System.out.println();
		return leerOpcion();
	}

	public int getOpcion() {
		return opcion;
	}
	
	public int getCodigo() {
		if (codigos.isEmpty()==false)
			return codigos.get(opcion-1).intValue();
		else
			return 0;
	}
}