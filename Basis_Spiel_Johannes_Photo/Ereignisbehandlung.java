import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Zugriff auf die Ereignisse einschliesslich Taktgeber.
 * 
 * @author Albert Wiedemann
 * @version 1.0
 */
public class Ereignisbehandlung
{
    /**
     * Der Konstruktor meldet den Taktgeber
     * und die Eventlistener bei der Zeichenflaeche an.
     */
    Ereignisbehandlung ()
    {
        Zeichenfenster.AktionsEmpfaengerEintragen(new Zeichenfenster.AktionsEmpfaenger()
        {
            public void Ausfuehren ()
            {
                TaktImpulsAusfuehren();
            }
            
            public void Taste (char taste)
            {
                TasteGedrueckt(taste);
            }
            
            public void SonderTaste (int taste)
            {
                SonderTasteGedrueckt(taste);
            }
            
            public void Geklickt (int x, int y, int anzahl)
            {
                MausGeklickt(x, y, anzahl);
            }
        });
    }
    
    /**
     * Die eigentliche Aktionsmethode des Zeitgebers.
     * <br>Muss bei Bedarf von einer Unterklasse ueberschrieben werden. 
     */
    void TaktImpulsAusfuehren ()
    {
        System.out.println ("Tick");
    }

    /**
     * Zeitgeber starten.
     */
    void Starten ()
    {
        Zeichenfenster.TaktgeberStarten();
    }

    /**
     * Zeitgeber anhalten.
     */
    void Anhalten ()
    {
        Zeichenfenster.TaktgeberStoppen();
    }
    
    /**
     * Ablaufgeschwindigkeit des Zeitgebers einstellen.
     * 
     * @param dauer: Angabe in Millisekunden
     */
    void TaktdauerSetzen (int dauer)
    {
        Zeichenfenster.TaktdauerSetzen(dauer);
    }
    
    /**
     * Die eigentliche Aktionsmethode fuer gedrueckte Tasten.
     * <br>Muss bei Bedarf von einer Unterklasse ueberschrieben werden. 
     * @param taste die gedrueckte Taste
     */
    void TasteGedrueckt (char taste)
    {
        System. out. println ("Taste: " + taste);
    }
    
    /**
     * Die eigentliche Aktionsmethode fuer gedrueckte Sondertasten.
     * <br>Muss bei Bedarf von einer Unterklasse ueberschrieben werden. 
     * @param taste KeyCode der gedrueckten Taste
     */
    void SonderTasteGedrueckt (int taste)
    {
        System. out. println ("Sondertaste: " + taste);
    }
    
    /**
     * Die eigentliche Aktionsmethode fuer einen Mausklick.
     * <br>Muss bei Bedarf von einer Unterklasse ueberschrieben werden. 
     * @param x x-Position des Mausklicks
     * @param y y-Position des Mausklicks
     * @param anzahl Anzahl der aufeinanderfolgenden Mausklicks
     */
    void MausGeklickt (int x, int y, int anzahl)
    {
        System. out. println ("Maus: (" + x + "|" + y + "), " + anzahl + " mal");
    }
}
