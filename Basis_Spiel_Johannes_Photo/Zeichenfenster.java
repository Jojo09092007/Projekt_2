import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
/**
 * Die Klasse stellt ein Fenster mit einer Malflaeche zur Verfuegung,
 * auf der Objekte der Klassen Rechteck, Kreis und Dreieck sowie Turtle dargestellt
 * werden koennen.
 * Die Zeichenflaeche wird beim ersten Anlegen eines Zeichenobjekts automatisch
 * nach dem Muster Singleton angelegt.
 * 
 * @author Albert Wiedemann 
 * @version 1.0
 */
public class Zeichenfenster
{
    /** Interface fuer die Aktionsausfuehrung. */
    interface AktionsEmpfaenger
    {
        /** Methode wird vom Taktgeber aufgerufen. */
        void Ausfuehren();
        void Taste (char taste);
        void SonderTaste (int taste);
        void Geklickt (int x, int y, int anzahl);
    }
    
    /** Aufzaehlung der erzeugbaren Objektarten. */
    static enum SymbolArt {kreis, dreieck, rechteck, text;};
    
    /** Einziges Objekt der Zeichenflaeche. */
    private static Zeichenfenster zeichenflaeche = null;
    
    /** Fenster fuer die Zeichenflaeche. */
    private JFrame fenster;
    /** Die eigentliche Darstellungskomponente. */
    private JComponent malflaeche;
    /** Stop-Knopf fuer den Taktgeber. */
    private JButton stop;
    /** Start-Knopf fuer den Taktgeber. */
    private JButton start;
    /** Einsteller fuer die Taktrate*/
    private JSlider slider;
    /** Feld aller zu zeichnenden Objekte. */
    private ArrayList<GrafikSymbol> alleSymbole;
    /** Feld aller zu zeichnenden Objekte. */
    private ArrayList<AktionsEmpfaenger> aktionsEmpfaenger;
    /** Timerobjekt fuer die zentrale Zeitverwaltung */
    private javax.swing.Timer timer;

    /**
     * Legt das Fenster und die Malflaeche an
     */
    public Zeichenfenster ()
    {
        alleSymbole = new ArrayList<GrafikSymbol>();
        aktionsEmpfaenger = new ArrayList<AktionsEmpfaenger>();
        fenster = new JFrame("Zeichenfenster");
        fenster.setLocation(50, 50);
        fenster.setSize(800, 600);
        fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close-Button kann nicht versteckt oder abgestellt werden.
        
        malflaeche = new JComponent()
        {
            public void paint (Graphics g)
            {
                g.setColor(new Color (230, 230, 230));
                g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
                synchronized (malflaeche)
                {
                    for (GrafikSymbol s: alleSymbole)
                    {
                        if (s.sichtbar)
                        {
                            s.Zeichnen(g);
                        }
                    }
                }
            }
        };
        malflaeche.setOpaque(true);
        malflaeche.addMouseListener(new MouseAdapter ()
        {
            /**
             * Gibt den Ort eines Mouseclicks an die eigentliche Aktionsmethode weiter.
             * @param e das zugrestellte Ereignis
             */
            public void mousePressed(MouseEvent e)
            {
                malflaeche.requestFocus();
                ArrayList<AktionsEmpfaenger> empfaenger = new ArrayList<AktionsEmpfaenger>(aktionsEmpfaenger);
                for (AktionsEmpfaenger em: empfaenger)
                {
                    em.Geklickt(e.getX(), e.getY(), e.getClickCount());
                }
            }
        }
        );
        malflaeche.addKeyListener(new KeyAdapter ()
        {
            /**
             * Gibt die Taste an die eigentliche Aktionsmethode weiter.
             * @param e das zugestellte Ereignis
             */
            public void keyPressed(KeyEvent e)
            {
                ArrayList<AktionsEmpfaenger> empfaenger = new ArrayList<AktionsEmpfaenger>(aktionsEmpfaenger);
                if ((int) e.getKeyChar() == KeyEvent.CHAR_UNDEFINED)
                {
                    switch (e.getKeyCode())
                    {
                        case KeyEvent.VK_ENTER:
                            for (AktionsEmpfaenger em: empfaenger)
                            {
                                em.Taste((char) KeyEvent.VK_ENTER);
                            }
                            break;
                        default:
                            for (AktionsEmpfaenger em: empfaenger)
                            {
                                em.SonderTaste(e.getKeyCode());
                            }
                    }
                }
                else
                {
                    for (AktionsEmpfaenger em: empfaenger)
                    {
                        em.Taste(e.getKeyChar());
                    }
                }
            }
        }
        );
        
        fenster.add(malflaeche, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(200, 60));
        panel.setSize(200,60);
        panel.setVisible(true);
        panel.setLayout(new GridLayout(1, 2));
        JPanel panel2 = new JPanel();
        panel2.setMinimumSize(new Dimension(100, 60));
        panel2.setSize(100,60);
        panel2.setVisible(true);
        panel2.setLayout(new GridLayout(1, 1));
        stop = new JButton();
        start = new JButton();
        start.setLocation(10, 10);
        start.setSize(80, 30);
        start.setText("Start");
        start.setVisible(true);
        start.addActionListener(new ActionListener ()
        {
            public void actionPerformed (ActionEvent evt)
            {
                TaktgeberStartenIntern();
                malflaeche.requestFocus();
            }
        }
        );
        panel2.add(start);
        stop.setLocation(100, 10);
        stop.setSize(80, 30);
        stop.setText("Stop");
        stop.setVisible(true);
        stop.setEnabled(false);
        stop.addActionListener(new ActionListener ()
        {
            public void actionPerformed (ActionEvent evt)
            {
                TaktgeberStoppenIntern();
                malflaeche.requestFocus();
            }
        }
        );
        panel2.add(stop);
        panel.add(panel2);
        slider = new JSlider(0, 1000, 100);
        slider.setLocation(190, 10);
        slider.setSize(160, 40);
        slider.setMinimumSize(new Dimension(160, 40));
        slider.setPreferredSize(new Dimension(160, 40));
        slider.setMajorTickSpacing(100);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setValue(1000);
        slider.addChangeListener(new ChangeListener()
        {
            public void stateChanged​(ChangeEvent e)
            {
                timer.setDelay(slider.getValue());
                malflaeche.requestFocus();
            }
        }
        );
        panel.add(slider);
        
        fenster.add(panel, BorderLayout.SOUTH);
        fenster.setVisible(true);
        malflaeche.requestFocus();

        timer = new javax.swing.Timer (1000, new ActionListener ()
        {
            /**
             * Vom Timer aufgerufen.
             * Erzeugt den naechsten Taktimpuls
             * @param evt der Timerevent
             */
            public void actionPerformed (ActionEvent evt)
            {
                ArrayList<AktionsEmpfaenger> empfaenger = new ArrayList<AktionsEmpfaenger>(aktionsEmpfaenger);
                for (AktionsEmpfaenger e: empfaenger)
                {
                    e.Ausfuehren();
                }
            }
        }
        );
    }
    
    /**
     * Meldet die aktuelle Breite der Malflaeche.
     * @returns Breite der Malflaeche
     */
    static int MalflaechenBreiteGeben()
    {
        if (zeichenflaeche == null)
        {
            zeichenflaeche = new Zeichenfenster();
        }
        return zeichenflaeche.malflaeche.getWidth();
    }
    
    /**
     * Meldet die aktuelle Hoehe der Malflaeche.
     * @returns Hoehe der Malflaeche
     */
    static int MalflaechenHoeheGeben()
    {
        if (zeichenflaeche == null)
        {
            zeichenflaeche = new Zeichenfenster();
        }
        return zeichenflaeche.malflaeche.getHeight();
    }
    
    /**
     * Traegt einen neuen Aktionsempfaenger ein.
     * @param neu der neue Aktionsempfaenger
     */
    static void AktionsEmpfaengerEintragen(AktionsEmpfaenger neu)
    {
        if (zeichenflaeche == null)
        {
            zeichenflaeche = new Zeichenfenster();
        }
        zeichenflaeche.aktionsEmpfaenger.add(neu);
    }
    
    /**
     * Loescht einen Aktionsempfaenger aus der Liste.
     * @param alt der zu loeschende Aktionsempfaenger
     */
    static void AktionsEmpfaengerEntfernen(AktionsEmpfaenger alt)
    {
        if (zeichenflaeche == null)
        {
            zeichenflaeche = new Zeichenfenster();
        }
        zeichenflaeche.aktionsEmpfaenger.remove(alt);
    }
    
    /**
     * Erzeugt ein neues darzustelledes Symbol.
     * Die moeglichen Symbole sind im Aufzaehlungstyp SymbolArt beschrieben.
     * @param art Art des zu erzeugenden Symbols.
     * @return Referenz auf das Delegate-Objekt.
     */
    static GrafikSymbol SymbolErzeugen (SymbolArt art)
    {
        if (zeichenflaeche == null)
        {
            zeichenflaeche = new Zeichenfenster();
        }
        return zeichenflaeche.SymbolAnlegen(art);
    }
    
    /**
     * Startet den Taktgeber.
     */
    static void TaktgeberStarten ()
    {
        if (zeichenflaeche == null)
        {
            zeichenflaeche = new Zeichenfenster();
        }
        zeichenflaeche.TaktgeberStartenIntern();
    }
    
    /**
     * Stoppt den Taktgeber.
     */
    static void TaktgeberStoppen ()
    {
        if (zeichenflaeche == null)
        {
            zeichenflaeche = new Zeichenfenster();
        }
        zeichenflaeche.TaktgeberStoppenIntern();
    }
    
    /**
     * Ablaufgeschwindigkeit des Zeitgebers einstellen.
     * 
     * @param dauer: Angabe in Millisekunden
     */
    static void TaktdauerSetzen (int dauer)
    {
        if (zeichenflaeche == null)
        {
            zeichenflaeche = new Zeichenfenster();
        }
        zeichenflaeche.slider.setValue(dauer < 0 ? 0 : (dauer > 1000 ? 1000: dauer));
    }
    
    /**
     * Erzeugt das neue Symbol tatsaechlich.
     * @param art Art des zu erzeugenden Symbols.
     * @return Referenz auf das Delegate-Objekt.
     */
    private GrafikSymbol SymbolAnlegen (SymbolArt art)
    {
        GrafikSymbol neu = null;
        switch (art)
        {
            case rechteck:
                neu = new RechteckIntern();
                break;
            case kreis:
                neu = new EllipseIntern();
                break;
            case dreieck:
                neu = new DreieckIntern();
                break;
            
            
               
            case text:
                neu = new TextIntern();
                break;
        }
        synchronized (zeichenflaeche.malflaeche)
        {
            zeichenflaeche.alleSymbole.add(neu);
        }
        malflaeche.repaint();
        return neu;
    }
    
    /**
     * Startet den Taktgeber.
     */
    private void TaktgeberStartenIntern()
    {
        start.setEnabled(false);
        stop.setEnabled(true);
        timer.start();
    }
    
    /**
     * Stoppt den Taktgeber.
     */
    private void TaktgeberStoppenIntern()
    {
        start.setEnabled(true);
        stop.setEnabled(false);
        timer.stop();
    }
    
    /**
     * Oberklasse fuer alle verfuegbaren Grafiksymbole.
     * Alle Grafiksymbole werden ueber ihr umgebendes Rechteck beschrieben.
     */
    abstract class GrafikSymbol
    {
        /** x-Position der linken oberen Ecke. */
        protected int x;
        /** y-Position der linken oberen Ecke. */
        protected int y;
        /** Breite des umgebenden Rechtecks. */
        protected int b;
        /** Hoehe des umgebenden Rechtecks. */
        protected int h;
        /** Farbe des Symbols. */
        protected Color c;
        /** Sichtbarkeit des Symbols. */
        protected boolean sichtbar;
        /** Drehwinkel (mathematisch positiver Drehsinn) des Symbols. */
        protected int winkel;
        /** Die Form des Grafiksymbols. */
        protected Area form;
        /** Farbe Hellgelb. */
        protected final Color hellgelb = new Color(255,255,128);
        /** Farbe Hellgruen. */
        protected final Color hellgruen = new Color(128,255,128);
        /** Farbe Orange. */
        protected final Color orange = new Color(255,128,0);
        /** Farbe Braun. */
        protected final Color braun = new Color(128,64,0);
        
        /**
         * Der Konstruktor erzeugt ein rotes Symbol in der linken oberen Ecke des Fensters.
         */
        GrafikSymbol()
        {
            x = 10;
            y = 10;
            b = 100;
            h = 100;
            c = Color.RED;
            sichtbar = true;
            winkel = 0;
            FormErzeugen();
        }
        
        /**
         * Normiert den Winkel auf Werte im Bereich [0; 360[
         * @param winkel der Eingabewinkel
         * @return der normierte Winkel
         */
        int WinkelNormieren(int winkel)
        {
            while (winkel < 0)
            {
                winkel += 360;
            }
            return winkel % 360;
        }
        
        /**
         * Setzt die Position (der linken oberen Ecke) des Objekts.
         * @param x x-Position der linken oberen Ecke
         * @param y y-Position der linken oberen Ecke
         */
        void PositionSetzen (int x, int y)
        {
            this.x = x;
            this.y = y;
            FormErzeugen();
            zeichenflaeche.malflaeche.repaint();
        }
        
        /**
         * Setzt die Groesse des Objekts.
         * @param breite (neue) Breite des Objekts
         * @param hoehe (neue) Hoehe des Objekts
         */
        void GroesseSetzen (int breite, int hoehe)
        {
            b = breite;
            h = hoehe;
            FormErzeugen();
            zeichenflaeche.malflaeche.repaint();
        }
    
        /**
         * Bestimmt die RGB-Farbe fuer den gegeben String.
         * @param farbe die Farbe als String
         * @return die Farbe als RGB-Farbe
         */
        Color FarbeCodieren (String farbe)
        {
            farbe = farbe.toLowerCase();
            switch (farbe)
            {
                
                case "weiss":
                    return Color.WHITE;
                case "rot":
                    return Color.RED;
                
                case "gruen":
                    return Color.GREEN;
                case "blau":
                    return Color.BLUE;
                case "gelb":
                    return Color.YELLOW;
                case "magenta":
                    return Color.MAGENTA;
                case "cyan":
                    return Color.CYAN;
                case "hellgelb":
                    return hellgelb;
               
                case "hellgruen":
                    return hellgruen;
                case "orange":
                    return orange;
                case "braun":
                    return braun;
                case "grau":
                    return Color.GRAY;
                case "schwarz":
                    return Color.BLACK;
                default:
                    return Color.BLACK;
            }
        }

        /**
         * Setzt die Farbe des Objekts.
         * @param farbe (neue) Farbe des Objekts
         */
        void FarbeSetzen (String farbe)
        {
            FarbeSetzen(FarbeCodieren(farbe));
        }
        
        /**
         * Setzt die Farbe des Objekts.
         * @param c (neue) Farbe des Objekts
         */
        void FarbeSetzen (Color c)
        {
            this.c = c;
            zeichenflaeche.malflaeche.repaint();
        }
        
        /**
         * Setzt die Sichtbarkeit des Objekts.
         * @param sichtbar (neue) Sichtbarkeit des Objekts
         */
        void SichtbarkeitSetzen (boolean sichtbar)
        {
            this.sichtbar = sichtbar;
            zeichenflaeche.malflaeche.repaint();
        }
        
        /**
         * Setzt den Drehwinkel des Objekts.
         * @param winkel der (neue) Drehwinkel des Objekts
         */
        void WinkelSetzen (int winkel)
        {
            this.winkel = WinkelNormieren(winkel);
            FormErzeugen();
            zeichenflaeche.malflaeche.repaint();
        }
        
        /**
         * Entfernt das Objekt aus dem Zeichenfenster.
         */
        void Entfernen ()
        {
            synchronized (zeichenflaeche.malflaeche)
            {
                zeichenflaeche.alleSymbole.remove(this);
                zeichenflaeche.malflaeche.repaint();
            }
        }
        
        /**
         * Bringt das Objekt eine Ebene nach vorn.
         */
        void NachVornBringen ()
        {
            synchronized (zeichenflaeche.malflaeche)
            {
                int index = zeichenflaeche.alleSymbole.indexOf(this);
                if (index < zeichenflaeche.alleSymbole.size() - 1)
                {
                    zeichenflaeche.alleSymbole.set(index, zeichenflaeche.alleSymbole.get(index + 1));
                    zeichenflaeche.alleSymbole.set(index + 1, this);
                    zeichenflaeche.malflaeche.repaint();
                }
            }
        }
        
        /**
         * Bringt das Objekt in die vorderste Ebene.
         */
        void GanzNachVornBringen ()
        {
            synchronized (zeichenflaeche.malflaeche)
            {
                int index = zeichenflaeche.alleSymbole.indexOf(this);
                if (index < zeichenflaeche.alleSymbole.size() - 1)
                {
                    zeichenflaeche.alleSymbole.remove(index);
                    zeichenflaeche.alleSymbole.add(this);
                    zeichenflaeche.malflaeche.repaint();
                }
            }
        }
        
        /**
         * Bringt das Objekt eine Ebene nach hinten.
         */
        void NachHintenBringen ()
        {
            synchronized (zeichenflaeche.malflaeche)
            {
                int index = zeichenflaeche.alleSymbole.indexOf(this);
                if (index > 0)
                {
                    zeichenflaeche.alleSymbole.set(index, zeichenflaeche.alleSymbole.get(index - 1));
                    zeichenflaeche.alleSymbole.set(index - 1, this);
                    zeichenflaeche.malflaeche.repaint();
                }
            }
        }
        
        /**
         * Bringt das Objekt in die hinterste Ebene.
         */
        void GanzNachHintenBringen ()
        {
            synchronized (zeichenflaeche.malflaeche)
            {
                int index = zeichenflaeche.alleSymbole.indexOf(this);
                if (index > 0)
                {
                    zeichenflaeche.alleSymbole.remove(index);
                    zeichenflaeche.alleSymbole.add(0, this);
                    zeichenflaeche.malflaeche.repaint();
                }
            }
        }
        
        /**
         * Testet, ob der angegebene Punkt innerhalb der Figur ist.
         * @param x x-Koordinate des zu testenden Punktes
         * @param y y-Koordinate des zu testenden Punktes
         * @return wahr, wenn der Punkt innerhalb der Figur ist
         */
        boolean IstInnerhalb (int x, int y)
        {
            return form.contains(x, y);
        }
        
        /**
         * Testet, ob die beiden Figuren ueberlappen.
         * @param wen die andere Form
         * @return wahr, wenn die beiden Formen ueberlappen.
         */
        boolean Schneidet (Area wen)
        {
            Area area = new Area(form);
            area.intersect (wen);
            return !area.isEmpty();
        }
        
        /**
         * Zeichnet das Objekt
         * @param g das Grafikobjekt zum Zeichnen
         */
        void Zeichnen(Graphics g)
        {
            g.setColor(c);
            ((Graphics2D) g).fill(form);
        }
        
        /**
         * Berechnet den Drehwinkel gemaess den Konventionen des Graphik-Frameworks.
         * Fuer Java: Winkel in Radians, positive Drehrichtng im Uhrzeiger.
         * @param winkel: Der Winkel in Grad, mathematischer Drehsinn
         * @return Winkel fuer Graphik-Framework
         */
        double DrehwinkelGeben (int winkel)
        {
            return - Math.PI * winkel / 180.0;
        }
        
        /**
         * Erstellt die Form des Objekts.
         */
        abstract void FormErzeugen();
    }
    
    /**
     * Objekte dieser Klasse verwalten ein Rechteck.
     */
    private class RechteckIntern extends GrafikSymbol
    {        
        /**
         * Erstellt die Form des Rechtecks.
         */
        @Override void FormErzeugen()
        {
            AffineTransform a = new AffineTransform();
            a.rotate(DrehwinkelGeben (winkel), this.x + b / 2, this.y + h / 2);
            form = new Area(new Path2D.Double (new Rectangle2D.Double(this.x, this.y, b, h), a));
        }
    }
    
    /**
     * Objekte dieser Klasse verwalten eine Ellipse.
     */
    private class EllipseIntern extends GrafikSymbol
    {
        /**
         * Erstellt die Form der Ellipse.
         */
        @Override void FormErzeugen()
        {
            AffineTransform a = new AffineTransform();
            a.rotate(DrehwinkelGeben (winkel), this.x + b / 2, this.y + h / 2);
            form = new Area(new Path2D.Double (new Ellipse2D.Double(this.x, this.y, b, h), a));
        }
    }
    
    /**
     * Objekte dieser Klasse verwalten ein Dreieck.
     */
    private class DreieckIntern extends GrafikSymbol
    {
        /**
         * Erstellt die Form des Dreiecks.
         */
        @Override void FormErzeugen()
        {
            Polygon rand = new Polygon (new int [] {x + b / 2, x + b, x, x + b / 2},
                                        new int [] {y, y + h, y + h, y}, 4);
            AffineTransform a = new AffineTransform();
            a.rotate(DrehwinkelGeben (winkel), this.x + b / 2, this.y + h / 2);
            form = new Area(new Path2D.Double (rand, a));
        }
    }
    
    /**
     * Objekte dieser Klasse verwalten einen Text.
     */
    class TextIntern extends GrafikSymbol
    {
        /** Der aktuelle Text. */
        private String text;
        /** Die aktuelle Textgroesse. */
        float size;

        /**
         * Belegt text und size mit Defaultwerten.
         */
        TextIntern ()
        {
            super();
            text = "Text";
            size = 12;
            c = Color.black;
        }
        
        /**
         * Erstellt die Form des Textes.
         * Dummy, legt ein leeres Area an.
         */
        @Override void FormErzeugen()
        {
            form = new Area();
        }
        
        /**
         * Testet, ob der angegebene Punkt innerhalb der Figur ist.
         * @param x x-Koordinate des zu testenden Punktes
         * @param y y-Koordinate des zu testenden Punktes
         * @return falsch
         */
        @Override boolean IstInnerhalb (int x, int y)
        {
            return false;
        }
        
        /**
         * Setzt den aktuellen Text.
         * @param t der neue Text
         */
        void TextSetzen (String t)
        {
            text = t;
            zeichenflaeche.malflaeche.repaint();
        }
        
        /**
         * Setzt die Groesse des Textes.
         */
        void TextGroesseSetzen (int groesse)
        {
            size = groesse;
            zeichenflaeche.malflaeche.repaint();
        }
        
        /**
         * Vergroessert den Text.
         */
        void TextVergroessern()
        {
            if (size <= 10)
            {
                size += 1;
            }
            else if (size <= 40)
            {
                size += 2;
            }
            else
            {
                size += 4;
            }
            zeichenflaeche.malflaeche.repaint();
        }
        
        /**
         * Verkleinert den Text.
         */
        void TextVerkleinern()
        {
            if (size <= 10)
            {
                size -= 1;
            }
            else if (size <= 40)
            {
                size -= 2;
            }
            else
            {
                size -= 4;
            }
            if (size < 1)
            {
                size = 1;
            }
            zeichenflaeche.malflaeche.repaint();
        }
        
        /**
         * Zeichnet das Objekt als Dreieck in der gegebenen Farbe.
         * @param g das Grafikobjekt zum Zeichnen
         */
        @Override void Zeichnen(Graphics g)
        {
            g.setColor(c);
            Font f = g.getFont();
            Font f2 = f.deriveFont(size);
            g.setFont(f2);
            
            if (winkel == 0)
            {
                g.drawString(text, x, y);
            }
            else
            {
                Graphics2D g2 = (Graphics2D) g;
                AffineTransform alt = g2.getTransform();
                //g2.rotate(DrehwinkelGeben (winkel), x + b / 2, y + h / 2);
                //g2.rotate(DrehwinkelGeben (winkel), x + text.length() * size / 4, y + size / 2);
                Rectangle2D bounds = f2.getStringBounds(text, g2.getFontRenderContext());
                g2.rotate(DrehwinkelGeben (winkel), x + bounds.getWidth() / 2, y - bounds.getHeight() / 2);
                g.drawString(text, x, y);
                g2.setTransform(alt);
            }
            g.setFont(f);
        }
    }
    
        
    
    
    
   
    
}
