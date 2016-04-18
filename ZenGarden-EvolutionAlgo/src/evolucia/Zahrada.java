package evolucia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Zahrada {
	int zahrada[][];
	int sirka;
	int vyska;
	int polObvod;
	int pocetKamenov;
	int pocetNaPohrabanie;
	
	private static final int Hore = 1;
	private static final int Doprava = 2;
	private static final int Dole = 3;
	private static final int Dolava = 4;
	
	public static final int Piesok = 0;
	public static final int Kamen = -1;
	
	
	public Zahrada(File file){
		citajVstup(file);
		pocetNaPohrabanie = (vyska*sirka)-pocetKamenov;
	}
	
	public boolean vHranici(int vyska,int sirka){
		if ( (vyska >= 0 && vyska < this.vyska) && (sirka >=0 && sirka < this.sirka) ){return true;}
		else return false;
	}
	
	public Suradnica smer(int cislo){
		if(cislo <= sirka){return new Suradnica(0,cislo-1,Dole,null);}
		if(cislo > sirka && cislo <= sirka+vyska){return new Suradnica(cislo-sirka-1,sirka-1,Dolava,null);}
		if(cislo > sirka+vyska && cislo <= (2*sirka)+vyska){return new Suradnica(vyska-1,cislo-polObvod-1,Hore,null);}
		else{return new Suradnica(cislo-polObvod-sirka-1,0,Doprava,null);}
	}
	
	public Suradnica zmenSmer(Suradnica suradnica){
		Suradnica novaSur;
		if(suradnica.smer == Hore || suradnica.smer == Dole){//isli sme po vyske
			if(Math.random() < 0.5){//chod doprava
				novaSur = new Suradnica(suradnica.vyska, suradnica.sirka+1, Doprava, suradnica);
				
				if(!vHranici(novaSur.vyska, novaSur.sirka)){return novaSur;}// vysiel som mimo zahrady
				
				if(zahrada[novaSur.vyska][novaSur.sirka] != Piesok){//doprava to nejde, skus dolava
					
					novaSur = new Suradnica(suradnica.vyska, suradnica.sirka-1, Dolava, suradnica);
					
					if(!vHranici(novaSur.vyska, novaSur.sirka)){return novaSur;}// vysiel som mimo zahrady
					
					if(zahrada[novaSur.vyska][novaSur.sirka] != Piesok){//ani dolava to nejde
						return null;						
					}
				}
			}else{//chod dolava
				novaSur = new Suradnica(suradnica.vyska, suradnica.sirka-1, Dolava, suradnica);
				
				if(!vHranici(novaSur.vyska, novaSur.sirka)){return novaSur;}// vysiel som mimo zahrady
				
				if(zahrada[novaSur.vyska][novaSur.sirka] != Piesok){//dolava to nejde, skus doprava
					
					novaSur = new Suradnica(suradnica.vyska, suradnica.sirka+1, Doprava, suradnica);
					
					if(!vHranici(novaSur.vyska, novaSur.sirka)){return novaSur;}// vysiel som mimo zahrady
					
					if(zahrada[novaSur.vyska][novaSur.sirka] != Piesok){//ani doprava to nejde
						return null;						
					}
				}
			}
		}else{//isli sme po sirke
			if(Math.random() < 0.5){//chod hore
				novaSur = new Suradnica(suradnica.vyska-1, suradnica.sirka, Hore, suradnica);
				
				if(!vHranici(novaSur.vyska, novaSur.sirka)){return novaSur;}
				
				if(zahrada[novaSur.vyska][novaSur.sirka] != Piesok){//hore sa neda, skus dole
					
					novaSur = new Suradnica(suradnica.vyska+1, suradnica.sirka, Dole, suradnica);
					
					if(!vHranici(novaSur.vyska, novaSur.sirka)){return novaSur;}
					
					if(zahrada[novaSur.vyska][novaSur.sirka] != Piesok){//ani dole to nejde
						return null;						
					}
					
				}
			}else{//chod dole
				novaSur = new Suradnica(suradnica.vyska+1, suradnica.sirka, Dole, suradnica);
				
				if(!vHranici(novaSur.vyska, novaSur.sirka)){return novaSur;}//vysiel mimo zahradu
				
				if(zahrada[novaSur.vyska][novaSur.sirka] != Piesok){//dole sa neda, skus hore
					
					novaSur = new Suradnica(suradnica.vyska-1, suradnica.sirka, Hore, suradnica);
					
					if(!vHranici(novaSur.vyska, novaSur.sirka)){return novaSur;}
					
					if(zahrada[novaSur.vyska][novaSur.sirka] != Piesok){//ani hore to nejde
						return null;						
					}
				
				}
			}
		}
		return novaSur;
	}
	
	
	public int pohrab(int[] chromozome, boolean vypis) {
		int prechod=1;
		boolean uviaznutie = false;
		resetZahradu();
		
		for (int i = 0; i < chromozome.length; i++) {
			Suradnica suradnica = smer(chromozome[i]);
			
			
			//mozeme vojst
			if(zahrada[suradnica.vyska][suradnica.sirka] == 0){
				
				while(vHranici(suradnica.vyska,suradnica.sirka)){
					
					if(zahrada[suradnica.vyska][suradnica.sirka] != Piesok){//narazenie
						suradnica = suradnica.predch;
						suradnica = zmenSmer(suradnica);
						
						if(suradnica == null){uviaznutie = true;break;}//neda sa kam otocit, skonci
						
						if (!vHranici(suradnica.vyska,suradnica.sirka)) {//otocili sme sa a sme mimo zahradu
							break;
						}
					}
					
					zahrada[suradnica.vyska][suradnica.sirka] = prechod;
					if(suradnica.smer == Hore){
						suradnica = new Suradnica(suradnica.vyska-1,suradnica.sirka,Hore,suradnica);
					}
					if(suradnica.smer == Doprava){
						suradnica = new Suradnica(suradnica.vyska,suradnica.sirka+1,Doprava,suradnica);
					}
					if(suradnica.smer == Dole){
						suradnica = new Suradnica(suradnica.vyska+1,suradnica.sirka,Dole,suradnica);
					}
					if(suradnica.smer == Dolava){
						suradnica = new Suradnica(suradnica.vyska,suradnica.sirka-1,Dolava,suradnica);
					}
				}
				prechod++;
				
			}
			if(uviaznutie){break;}
			uviaznutie = false;
		}
				
		
		if(pocetPohrabanych() == pocetNaPohrabanie){
			for(int i=0; i<vyska; i++){
				for(int j=0; j<sirka; j++){
					System.out.print(zahrada[i][j]+" ");
				}
				System.out.println();
			}
		}
		
		return pocetPohrabanych();
	}
	
	public void resetZahradu(){
		for(int i=0; i<vyska; i++){
			for(int j=0; j<sirka; j++){
				if(zahrada[i][j] >= 0){zahrada[i][j] = 0;}				
			}
		}
	}
	
	public int pocetPohrabanych(){
		int sum=0;
		for(int i=0; i<vyska; i++){
			for(int j=0; j<sirka; j++){
				if(zahrada[i][j] > 0){sum++;}
			}
		}
		return sum;
	}
	
	
	public void citajVstup(File file){
		try (Scanner sc = new Scanner(file)) {			
			vyska=sc.nextInt();
			sirka=sc.nextInt();
			
			this.zahrada=new int[vyska][sirka];
			this.polObvod=vyska+sirka;			
			
			//zahrada
			for(int i=0;i<vyska;i++){
			  for(int j=0; j< sirka; j++){
					zahrada[i][j]=Piesok;
					
			  }
			}			
			//pridaj kamene
			while (sc.hasNextLine()) {				
				zahrada[sc.nextInt()][sc.nextInt()]=Kamen;
				pocetKamenov++;				
			}			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void vypisZahradu(){
		for(int i=0;i<vyska;i++){
			for(int j=0;j<sirka;j++){
				if ( zahrada[i][j] == Kamen ) System.out.print("-1");
				else{System.out.print("0");}
			}
			System.out.println();
		}
	}
	
	
	public int obvod(){
		return polObvod*2;
	}
	
	public int getMaxGenom(){
		return polObvod + pocetKamenov;
	}








	
}
