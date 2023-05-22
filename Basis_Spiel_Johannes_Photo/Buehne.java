
/**
 * Das Haus, das renoviert werden soll, mit der Umgebung.
 * 
 * @author Klaus Reinold
 * @version 1.0
 */
class Buehne extends Ereignisbehandlung
{
    Rechteck himmel;
    Rechteck boden;
    Rechteck haus;
    Rechteck fenster;
    Rechteck tuer;
    Dreieck dach;
    Kreis dachfenster; 
    Kreis sonne;
    AudioPlayer player;
    int i;

    /**
     * Konstruktor fuer Objekte der Klasse Buehne
     */
    Buehne()
    {
        i = 0;
        player = new AudioPlayer();
        player.playAudio("test.mp3");
        
        himmel = new Rechteck();
        himmel.PositionSetzen(0, 0);
        himmel.GroesseSetzen(800, 300);
        himmel.FarbeSetzen("magenta");

        boden = new Rechteck();
        boden.PositionSetzen(0, 300);
        boden.GroesseSetzen(800, 300);
        boden.FarbeSetzen("braun");

        haus = new Rechteck();
        haus.PositionSetzen(260, 180);
        haus.GroesseSetzen(250, 150);
        haus.FarbeSetzen("grau");

        fenster = new Rechteck();   
        fenster.PositionSetzen(280, 200);
        fenster.GroesseSetzen(130, 70);
        fenster.FarbeSetzen("weiss");
        fenster.Drehen(-15);

        tuer = new Rechteck();   
        tuer.PositionSetzen(450, 220);
        tuer.GroesseSetzen(70, 110);
        tuer.FarbeSetzen("orange");

        dachfenster = new Kreis();
        dachfenster.PositionSetzen(380, 150);
        dachfenster.Radiussetzen(10);
        dachfenster.FarbeSetzen("weiss");

        dach = new Dreieck();
        dach.PositionSetzen(285, 50);
        dach.GroesseSetzen(250, 80);

        new Kreis();
    }

    /**
     * Das Dach muss um 100 nach rechts und 50 nach unten.
     */
    void DachVerschieben()
    {
        //Code ergaenzen!
    }

    /**
     * Das Dachfenster muss in den Vordergrund gerueckt werden.
     */
    void DachfensterNachVorneHolen()
    {
        //Code ergaenzen!
    }
    
     /**
     * Das Haus soll weiss gestrichen werden.
     */
    void HausTuenchen()
    {
        //Code ergaenzen!
    }

    /**
     * Die Tuer muss um 20 nach links.
     */
    void TuerNachLinks()
    {
        //Code ergaenzen!
    }

    /**
     * Das Fenster muss um 15 Grad gedreht werden.
     */
    void FensterDrehen()
    {
        //Code ergaenzen!
    }

    /**
     * Der Boden muss begruent werden.
     */
    void BodenBegruenen()
    {
        //Code ergaenzen!
    }

    /**
     * Der Himmel muss blau werden.
     */
    void HimmelFaerben()
    {
        //Code ergaenzen!
    }

    /**
     * Die Sonne muss gelb und passender positioniert werden.
     */
    void SonneKorrigieren()
    {
        //erst bei Aufgabe c) bearbeiten!
        //sonne.FarbeSetzen("gelb");
        //sonne.PositionSetzen(700,80);
    }

    /**
     * Taktsteuerung des Films
     */
    @Override void TaktImpulsAusfuehren()
    {
        switch(i)
        {
            case 0: 
            DachVerschieben();
            break;
            case 1: 
            DachfensterNachVorneHolen();
            break;
            case 2: 
            HausTuenchen();
            break;
            case 3:
            TuerNachLinks();
            break;
            case 4: 
            FensterDrehen();
            break;
            case 5: 
            BodenBegruenen();
            break;
            case 6: 
            HimmelFaerben();
            break;
            case 7:
            SonneKorrigieren();
        }
        i = i + 1;
    }

}
