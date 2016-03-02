import java.util.Random;
import java.util.Scanner;



class Cliente extends Thread {
	// Tiempo m·ximo que tarda el cliente en hacer la compra.
private static final int MAX_DELAY = 2000;
	private static final int MAX_COST = 100;
	private int id;
	private Cola cola;
	private int num_caja;
	
	Cliente(int id, Cola cola) {
		this.id = id;
		this.cola=cola;
	}
	public void run() {
		try {
			System.out.println("Cliente " + id + " realizando compra");
			Thread.sleep(new Random().nextInt(MAX_DELAY));
			long s = System.currentTimeMillis();
			num_caja = cola.esperarColaGeneral(id);
			System.out.print("Cliente " + id + " en cola con ");
			cola.imprimir();
			cola.atender(new Random().nextInt(MAX_COST), num_caja);
			System.out.println("Cliente " + id + " atendido en: "+num_caja);
			cola.finalizarcompra();
			long espera = System.currentTimeMillis() - s;
			Resultados.tiempo_espera += espera;
			System.out.println("Cliente " + id + " saliendo despés de esperar " + espera);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}


class Resultados {
	public static int ganancias;
	public static long tiempo_espera;
	public static int clientes_atendidos;
}



class Caja {
	private static final int MAX_TIME = 1000;
	private boolean ocupada;
	
	public Caja(){
		this.ocupada=false;
	}
	
	public boolean ocupada(){
		return ocupada;
	}
	
	synchronized public void AtenderDeColaGeneral(int pago) throws InterruptedException {
		ocupada=true;
		int tiempo_atencion = new Random().nextInt(MAX_TIME);
		Thread.sleep(tiempo_atencion);
		Resultados.ganancias += pago;
		Resultados.clientes_atendidos++;
		ocupada=false;
	}
}



 class Cola {

	int num_cajas;
	int num_clientes;
	Caja [] cajas;
	private static final int MAX_TIME = 1000;

	Nodo raiz, fondo;

	class Nodo {
		int cliente;
		Nodo sig;
	}

	public Cola(int n) {
		this.cajas = new Caja[n];
		for (int i = 0; i < n; i++) {
			cajas[i] = new Caja();
		}
		raiz = null;
		fondo = null;
	}

	synchronized public int esperarColaGeneral(int id_cliente) throws InterruptedException {
		int caja_id;
		Nodo nuevo;
		nuevo = new Nodo();

		nuevo.cliente = id_cliente;
		nuevo.sig = null;

		// si la cola est· vacÌa...
		if (vacia()==true) {
			raiz = nuevo;
			fondo = nuevo;
		} else {
			fondo.sig = nuevo;
			fondo = nuevo;
		}

		while ((caja_id=cajavacia()) == cajas.length || (raiz.cliente != id_cliente)) {
			System.out.println("El cliente " + id_cliente + "esta esperando una cola única");
			wait();
		}
			raiz = raiz.sig;
			return caja_id;
	}
	
	
	public void atender(int pago, int id_caja) throws InterruptedException{
		cajas[id_caja].AtenderDeColaGeneral(pago);
	}
	
	synchronized public void finalizarcompra(){
		notify();
	}
	synchronized public int cajavacia() {

		int caja = 0;	
		while (caja < cajas.length) {
			if(!cajas[caja].ocupada()){
				break;
			}caja++;
		}
		return caja;
	}
	
	private boolean vacia() {
		if (raiz == null)
			//si esta vacÌa == true
			return true;
		else
			//si esta llena == false
			return false;
	}
	
	synchronized public void imprimir() {
		Nodo reco = raiz;
		while (reco != null) {
			System.out.print(reco.cliente + "-");
			reco = reco.sig;
		}
		System.out.println();
	}

}




public class ModernSuperMarket {
	
	public static void main(String[] args) throws InterruptedException {
		Scanner sc = new Scanner(System.in);
		//numero cajas
		int N = sc.nextInt();
		//numero clientes
		Cola cola = new Cola(N);
		int M = sc.nextInt();
		Cliente clientes[] = new Cliente[M];
		for (int j, i = 0; i < M; i++) {
			// Seleccionamos ya en qué caja se situara
			clientes[i] = new Cliente(i, cola);
			clientes[i].start();
		}
		try {
			for (int i = 0; i < M; i++) {
				clientes[i].join();
			}
		} catch (InterruptedException ex) {
			System.out.println("Hilo principal interrumpido.");
		}
		System.out.println("Supermercado cerrado.");
		System.out.println("Ganancias: " + Resultados.ganancias);
		System.out.println("Tiempo medio de espera: " + (Resultados.tiempo_espera / Resultados.clientes_atendidos));
	}
	
}