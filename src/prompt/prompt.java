package prompt;

import java.io.IOException;
import java.util.Scanner;

import org.json.simple.parser.ParseException;

//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;

import functionalities.HashtagFilter;
import functionalities.HashtagFinderMultiT;
import functionalities.UserFinder;

public class prompt {

	public static void main(String[] args) {

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input = "";

		do {

			System.out.println("\n1. Estrai gli utenti da un insieme di tweets");
			System.out.println("2. Cerca quali sono i tweet più usati dagli utenti raccolti / Salva i tweet degli utenti ");
			System.out.println("3. Filtra gli hashtags in basa ad hashtags e parole ");
			System.out.println("q. Esci ");
			System.out.print("> ");

			input = scanner.nextLine();

			switch (input) {

			case "1":
				try {
					System.out.println("Inserisci il nome della cartella:");
					System.out.print("> ");
					String fileName = scanner.nextLine();
					UserFinder.findUser(fileName, true);
				} catch (ParseException | java.text.ParseException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				break;

			case "2":
				try {
					System.out.println("\nInserisci la data da cui vuoi cominciare a prendere tweets:");
					System.out.print("> ");
					String since = scanner.nextLine();
					
					System.out.println("Inserisci la data in cui vuoi terminare di prendere tweets:");
					System.out.print("> ");
					String until = scanner.nextLine();
					
					System.out.println("Inserisci il nome del file contenete gli utenti:");
					System.out.print("> ");
					String fileName = scanner.nextLine();
					
					System.out.println("Inserisci il nome della cartella in sui salvare i tweets raccolti:");
					System.out.print("> ");
					String folderName = scanner.nextLine();
					
					System.out.println("Inserisci il nome della cartella in cui salvare l'analisi relativa agli hashtags:");
					System.out.print("> ");
					String folderNameHashtag = scanner.nextLine();
					
					
					
					HashtagFinderMultiT.findHashtags(since, until, fileName, folderName, folderNameHashtag,true);
					
					
				} catch (ParseException | java.text.ParseException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;

			case "3":
				try {
					System.out.println("\nInserisci il percorso del file da analizzare:");
					System.out.print("> ");
					String filePath = scanner.nextLine();
					
					System.out.println("Vuoi scrivere i file di output?: (s SI, qualsiasi carettere NO)");
					System.out.print("> ");
					String c = scanner.nextLine();
					
					boolean write = false;
					
					if(c.equals("s")){
						write = true;
					}
					
					System.out.println("Inserisci il nome della cartela dove verranno salvati i tweets:");
					System.out.print("> ");
					String folderName = scanner.nextLine();
					
					System.out.println("Inserisci il nome della cartela dove verranno salvati i testi dei tweets:");
					System.out.print("> ");
					String folderNameText = scanner.nextLine();
					
					System.out.println("Inserisci il nome del file di output:");
					System.out.print("> ");
					String outName = scanner.nextLine();
					
					
					HashtagFilter.filterHashtags(filePath, write, folderName, folderNameText,outName);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;

			case "q":
				return;

			default:
				System.out.println("Valore errato"); break;

			}

		} while (!input.equals("q"));

	}

}
