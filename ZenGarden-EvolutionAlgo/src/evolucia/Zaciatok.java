package evolucia;

import java.io.File;
import evolucia.Zahrada;

public class Zaciatok {
	
	public static final String fileName="test1.txt";

	public static void main(String[] args) {
		File file=new File(fileName);
		Zahrada zahrada = new Zahrada(file);
		
		//zahrada.vypisZahradu();
		GenetickeHladanie gh = new GenetickeHladanie(zahrada);

	}

}
