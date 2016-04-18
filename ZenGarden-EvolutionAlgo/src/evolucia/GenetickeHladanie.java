package evolucia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.text.html.MinimalHTMLWriter;

public class GenetickeHladanie {

	
	int dlzkaChromozomu;
	Zahrada zahrada;
	int populacia[][];
	
	//======Editovatelne premenne=======================================
	private static int VELKOST_POPULACIE=100;
	private final int MAX_GENERACI=29;
	private static int METODA_VYBERU = 2;	//1-Ruleta , 2-Turnaj
	private static int METODA_KRIZENIA = 2;	//1-ONE POINT, 2-TWO POINT
	private final float MIN_MUTACIA = 0.02f;
	private final float MAX_MUTACIA = 0.35f;			
	//==================================================================
	
	
	public GenetickeHladanie(Zahrada zahrada){
		this.dlzkaChromozomu = zahrada.polObvod + zahrada.pocetKamenov;
		this.zahrada = zahrada;
		this.populacia = new int[VELKOST_POPULACIE][dlzkaChromozomu];
		
		hladajRiesenie();
	}
	
	
	public void hladajRiesenie(){
		int[] fitnessHodnoty = new int[VELKOST_POPULACIE];
		float aktualnamutacia = MIN_MUTACIA;
		//generovanie populacie
		for(int i=0;i<VELKOST_POPULACIE;i++){
			populacia[i]=generujChromozom();			
		}
		
		int pocetgeneraci = 0;
		while(pocetgeneraci++ < MAX_GENERACI){
			int[][] potomok = new int[VELKOST_POPULACIE][dlzkaChromozomu];
			int maxFitness, minFitness ,maxIndex ,sum;
			
			for(int i=0;i<VELKOST_POPULACIE;i++){
				fitnessHodnoty[i] = zahrada.pohrab(populacia[i],false);
				//System.out.println();
			}
			
			maxFitness = fitnessHodnoty[0];
			maxIndex = 0;
			minFitness = fitnessHodnoty[0];
			sum = 0;
			for(int i=0; i<VELKOST_POPULACIE; i++){				
				if(fitnessHodnoty[i] > maxFitness){maxFitness = fitnessHodnoty[i];maxIndex=i;}
				if(fitnessHodnoty[i] < minFitness){minFitness = fitnessHodnoty[i];}
				//fitnessHodnoty[i] = fitnessHodnoty[i] - minFitness;//nechcem  najhorsich jednicov generacie aby sa mnozili
				sum += fitnessHodnoty[i];
			}
			
			System.out.printf("Populacia: %d maxFitness: %d minFitness: %d priemer: %d \n",pocetgeneraci,maxFitness,minFitness,sum / VELKOST_POPULACIE);
			
			if(maxFitness == zahrada.pocetNaPohrabanie){//nasli sme riesenie
				//zahrada.pohrab(populacia[maxIndex], true);
				vypisChromozom(populacia[maxIndex]);
				System.out.println("Nasli sme riesenie v "+pocetgeneraci+" generacii");
				break;
			}
			
			/*for (int i = 0; i < VELKOST_POPULACIE; i++) {//nechcem  najhorsich jednicov generacie aby sa mnozili
				fitnessHodnoty[i] = fitnessHodnoty[i] - minFitness;
			}*/
			
			for(int i=0; i<VELKOST_POPULACIE; i += 2){
				int individual1,individual2;
				int[][] vracajuca;
				if(METODA_VYBERU == 1){
					individual1 = ruleta(fitnessHodnoty, sum);
					individual2 = ruleta(fitnessHodnoty, sum);
				}else{
					individual1 = turnaj(fitnessHodnoty);
					individual2 = turnaj(fitnessHodnoty);
				}
				if(METODA_KRIZENIA == 1){
					vracajuca=onePoint(populacia[individual1], populacia[individual2]);
				}else{
					vracajuca=twoPoint(populacia[individual1], populacia[individual2]);
				}
				potomok[i]=vracajuca[0];
				potomok[i+1]=vracajuca[1];
				
				for(int dieta=0;dieta<2;dieta++){//mutacia deti
	                for(int j=0;j<dlzkaChromozomu;j++){
	                    if(Math.random() < aktualnamutacia ){
	                    	int mutacneCislo=(int) (Math.random() * (zahrada.obvod()-1)) +1;
	                    	aktualnamutacia += 0.01f;
	                    	
	                    	int index=-1;
	                    	for(int k=0;k<dlzkaChromozomu;k++){//ci tam take cislo mame
	                    		if ( potomok[i+dieta][k] == mutacneCislo){
	                    			index=k;
	                    			break;
	                    		}
	                    	}
	                        
	                    	if (index != -1){//ak sa uz take nachadza tak iba vymen v indexoch
	                    		int pom=potomok[i+dieta][j];
	                    		potomok[i+dieta][j]=potomok[i+dieta][index];
	                    		potomok[i+dieta][index]=pom;
	                    	}
	                    	else{//nemame take cislo, tak prepis hodnotu
	                    		potomok[i+dieta][j]=mutacneCislo;
	                    	}
	                    }
	                }
				}				
				if(aktualnamutacia >= MAX_MUTACIA){aktualnamutacia = MIN_MUTACIA;}//reset pravdepodobnosti mutacie
			
			}
			for (int i = 0; i < dlzkaChromozomu; i++){potomok[0][i] = populacia[maxIndex][i];}//najlepsieho z predchadzajucej generacie si nechame
			populacia = potomok;
		}
	}
	
	
	
	public int[][] onePoint(int[] rodic1, int[] rodic2){
		int[][] deti = new int[2][dlzkaChromozomu];
		int bod = (int) (Math.random() * dlzkaChromozomu);
		
		for(int i=0; i<dlzkaChromozomu; i++){
			if(i<bod){
				deti[0][i] = rodic1[i];
				deti[1][i] = rodic2[i];
			}
			else{
				deti[1][i] = rodic1[i];
				deti[0][i] = rodic2[i];
			}
		}				
		return deti;
	}
	
	public int[][] twoPoint(int[] rodic1, int[] rodic2){
		int [][] deti = new int[2][dlzkaChromozomu];
		int bod1 = (int)(Math.random() * dlzkaChromozomu);
		int bod2 = (int)(Math.random() * dlzkaChromozomu);
		for(int i=0; i<dlzkaChromozomu; i++){
			if(i<bod1 || i>bod2){
				deti[0][i] = rodic1[i];
				deti[1][i] = rodic2[i];
			}else{
				deti[1][i] = rodic1[i];
				deti[0][i] = rodic2[i];
			}
		}			
		return deti;
	}
	
	public int[] generujChromozom(){
		int[] chromozom = new int[dlzkaChromozomu];
		List<Integer> rozsahCisel = new ArrayList<Integer>();
 
		for (int i = 1; i <= zahrada.obvod(); i++) {
			rozsahCisel.add(i);
		}

		Collections.shuffle(rozsahCisel);

		for (int i = 0; i < dlzkaChromozomu; i++) {
			int cislo = rozsahCisel.get(i);					
			chromozom[i] = cislo;
		}
		return chromozom;
	}
	
	
	public int ruleta(int[] fitness,int sumaFitness){
		float hranica=(float) (Math.random() * sumaFitness);
		int suma=0;
		for(int i=0;i<VELKOST_POPULACIE;i++){
            suma+= fitness[i];
            if(suma > hranica) return i;
        }
	    return 0;	
	}
	
	
	public int turnaj(int[] fitness){
		int individual1=(int) (Math.random()*VELKOST_POPULACIE);
		int individual2=(int) (Math.random()*VELKOST_POPULACIE);
		
		int max=fitness[individual1] > fitness[individual2] ? individual1 : individual2;
				
		return max;
	}
	
	public void vypisChromozom(int[] chromozom){
		for(int i=0;i<dlzkaChromozomu;i++){
			System.out.printf("%d ",chromozom[i]);
		}
		System.out.println();
	}
}
