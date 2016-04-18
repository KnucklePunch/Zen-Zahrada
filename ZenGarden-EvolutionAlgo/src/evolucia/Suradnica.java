package evolucia;


public class Suradnica {

	public int vyska;
	public int sirka;
	public int smer;	
	public Suradnica predch;
	
	public Suradnica(int vyska, int sirka, int smer, Suradnica predch) {
		this.vyska=vyska;
		this.sirka=sirka;
		this.smer=smer;		
		this.predch=predch;
	}
}
