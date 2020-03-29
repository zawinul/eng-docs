package it.eng.ms.camundademo.r3;

import java.util.ArrayList;
import java.util.List;

public class RichiestaR3Model {
	public String testo;
	public String mittente;
	public String oggetto;
	public String destinatario;
	public List<Allegato> allegati = new ArrayList<Allegato>();

	public static class Allegato {
		public String nome;
		public String mimetype;
		public String contentB64;
	}
}
