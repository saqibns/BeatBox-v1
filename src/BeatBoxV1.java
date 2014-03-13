/**
 * This is the first version of BeatBox
 * 
 * @author Saqib Nizam Shamsi
 * @version 1.3.1
 */

import javax.swing.*;
import javax.sound.midi.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class BeatBoxV1 implements Serializable
{
    static final long serialVersionUID = -4383073477395643401L;         //Serial ID to provide backward compatibility with the saved patterns
    private JFrame frame;
    private String instNms[]={"Bass Drum","Closed Hi-Hat","Open Hi-Hat","Acoustic Snare","Crash Cymbal","Hand Clap","High Tom","High Bongo","Maracas","Whistle","Low Conga","Cowbell","Vibraslap","Low Mid-Tom", "High Agogo", "Open High Conga"};
    private int insts[]={35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
    private JButton start, stop, tempoUp, tempoDown, clear, credits;
    private JPanel rightPanel, leftPanel, centralPanel, creditsPanel;
    private ArrayList<JCheckBox> cBoxList;
    private JCheckBox repeater;
    private JSeparator sepone, septwo, septhree, sepfour;
    private Sequencer S;
    private Box creditsBox;
    private float t=120.0F;
    private JLabel tempo, Credits, headfirst, ks, bertb;
    private JMenuItem saveMenuItem, loadMenuItem, aboutMenuItem;
    
    /*
     * Constructor
     */
    
    public BeatBoxV1()
    {
    }
    
    /*
     * Main function to begin execution
     */
    public static void main(String args[])
    {
        BeatBoxV1 bb=new BeatBoxV1();
        bb.gui();
        bb.createSequencer();
    }
    
    /*
     * The function builds the Graphical User Interface
     */
    public void gui()
    {
        /* Create a frame with a title*/
        frame = new JFrame("BeatBox v1.3.1");
        
        /*Set the icon of the frame*/
        frame.setIconImage(new ImageIcon("..//resources//BeatBoxIcon32.jpg").getImage());
         
        /*Set the close properties*/
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        /* Create a panel with BorderLayout as the Layout Manager.
         * The addition of various widgets gets a little easier 
         */
        BorderLayout layout=new BorderLayout();
        JPanel bckgrnd=new JPanel(layout);
        
        /*Improvement of aesthetic appeal by adding a blank border around the base panel*/
        bckgrnd.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        /* The layout which will hold the widgets in the EAST and the WEST areas on the GUI.
         * Just some more aesthetic improvement.
         */
        GridLayout G=new GridLayout(16,1);
        
        /*Create the right panel which holds the button 'Start', 'Stop' etc*/
        rightPanel=new JPanel(G);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        
        /*The elements in the right panel*/
        
        start=new JButton("Start");                             //Start button
        start.addActionListener(new StartListener());
        stop=new JButton("Stop");                               //Stop button
        stop.addActionListener(new StopListener());
        tempoUp=new JButton("Tempo Up");                        //Tempo Up button
        tempoUp.addActionListener(new TempoUpListener());
        tempoDown=new JButton("Tempo Down");                    //Tempo down button
        tempoDown.addActionListener(new TempoDownListener());
        tempo=new JLabel("Tempo: 120 BPM");                     //Current tempo display
        tempo.setHorizontalAlignment(SwingConstants.CENTER); 
        clear=new JButton("Clear");                             //Clear button
        clear.addActionListener(new ClearListener());
        repeater=new JCheckBox("Repeat");                       //Check box to toggle between loop/don't loop
        repeater.setHorizontalAlignment(SwingConstants.CENTER);        
        repeater.addItemListener(new RepeaterListener());;
        repeater.setSelected(false);							//Initialize the check box so that loop is disabled on start
        
        /*Seperators to be used in the right panel*/
        sepone=new JSeparator();
        sepone.setOrientation(SwingConstants.HORIZONTAL);
        septwo=new JSeparator();
        septwo.setOrientation(SwingConstants.HORIZONTAL);
        septwo.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        septhree=new JSeparator();
        septhree.setOrientation(SwingConstants.HORIZONTAL);
        sepfour=new JSeparator();
        sepfour.setOrientation(SwingConstants.HORIZONTAL);
               
        /*Set up the Right Panel*/
        rightPanel.add(start);
        rightPanel.add(stop);
        rightPanel.add(sepone);
        rightPanel.add(tempoUp);
        rightPanel.add(tempoDown);
        rightPanel.add(tempo);
        rightPanel.add(septwo);
        rightPanel.add(clear);
        rightPanel.add(septhree);
        rightPanel.add(repeater);
		/*Finished setting up the right panel*/
        
		/*Create the right panel which holds the labels that display the names of the instruments.
		* A border is created for the left panel as well.
		*/
        leftPanel=new JPanel(G);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        
		/*Create labels and Assign the names of the instruments to them*/
        for(String s:instNms)
        {
            leftPanel.add(new JLabel(s));
        }
        /*Finished setting up the left panel*/
		
		/*Grid Layout for placing the check boxes*/
        GridLayout glayout=new GridLayout(16,16);
        glayout.setVgap(0);
        glayout.setHgap(0);
		
		/*Setting the central panel to hold check boxes*/
        centralPanel= new JPanel(glayout);
        cBoxList=new ArrayList<JCheckBox>();
        Dimension size=new Dimension(266,252);
        
        centralPanel.setMinimumSize(size);
        centralPanel.setMaximumSize(size);
        
		/*Initialize the check box list. Create check boxes and initialize them to false so that they are unchecked on start*/
        for(int i=0;i<256;i++)
        {
            JCheckBox cb=new JCheckBox();
            cb.setSelected(false);
            cBoxList.add(cb);
            centralPanel.add(cb);
        }
        
        /*Menu bar Logic*/
        
        JMenuBar menu = new JMenuBar();
        
        //File Menu
        
        JMenu fileMenu = new JMenu("File");
        saveMenuItem = new JMenuItem("Save");
        loadMenuItem = new JMenuItem("Load");
        
        saveMenuItem.addActionListener(new SaveMenuListener());
        loadMenuItem.addActionListener(new LoadMenuListener());
        
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        
        menu.add(fileMenu);
        
        //Help Menu
        
        JMenu helpMenu = new JMenu("Help");
        aboutMenuItem = new JMenuItem("About BeatBox");
        
        aboutMenuItem.addActionListener(new AboutMenuItemListener());
        
        helpMenu.add(aboutMenuItem);
        
        menu.add(helpMenu);
        
		//Add the menu bar to the frame
        frame.setJMenuBar(menu);
        
		//Menu bar Logic ends
		
		/*Put everything in place*/
        bckgrnd.add(BorderLayout.EAST,rightPanel);
        bckgrnd.add(BorderLayout.WEST,leftPanel);
        bckgrnd.add(BorderLayout.CENTER,centralPanel);
        frame.getContentPane().add(bckgrnd);
        
		/*We've set up the show. Now we are ready to go*/
        frame.setBounds(300,100,600,400);
        frame.pack();
        frame.setVisible(true);
    }
    
	/*A utility method that is used to get a reference to a sequencer which is responsible for playing MIDI events*/
    public void createSequencer()
    {
        try
        {
            S=MidiSystem.getSequencer();
            S.open();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /*Method responsible for creating MIDI. A tuned version of createMelody()*/
    public void createMelodyRemastered()
    {
        int inst, i, j, tick, channel;                                                                                //75
        tick=i=0;
        channel = 1;
        try
        {
            //Get the sequence
            Sequence seq=new Sequence(Sequence.PPQ,4);
            //Create the track in the sequence
            Track track=seq.createTrack();
            
            //Make the midiEvent (Sound data) to be read by the sequencer and add it to the track
            
            for(i=0;i<16;i++)
            {
                for(j=0;j<16*16;j+=16)
                {
                    JCheckBox temp = cBoxList.get(i+j);
                    inst = insts[j/16];
                    if(temp.isSelected())
                    {
                        track.add(makeEvent(192,9,inst,0,16));
                        track.add(makeEvent(144,9,inst,100,tick));
                        track.add(makeEvent(128,9,inst,100,tick+1));
                    }
                    channel++;
                    if(channel>10)
                    channel = 1;
                }
                tick+=4;
            }
            S.setSequence(seq);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
	/*A not so good version to create MIDI*/
    public void createMelody()
    {
        int inst,i,j,tick;
        tick=i=0;
        j=1;
        try
        {            
            //Get the sequence
            Sequence seq=new Sequence(Sequence.PPQ,4);
            //Create the track in the sequence
            Track track=seq.createTrack();
            
            //Make the midiEvent (Sound data) to be read by the sequencer and add it to the track
            for(JCheckBox temp: cBoxList)
            {
                if(temp.isSelected())
                {
                    inst=insts[i/16];
                    track.add(makeEvent(192,1,inst,0,tick));
                    track.add(makeEvent(144,1,2+(j*7),100,tick));
                    track.add(makeEvent(128,1,2+(j*7),100,tick+2));
                    tick+=4;
                }
                i++;
                j++;
                if(i%16==0)
                j=1;
            }
            S.setSequence(seq);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public MidiEvent makeEvent(int one, int two, int three, int four, int five)
    {
        MidiEvent event =null;
        try
        {
            //Create a message
            ShortMessage S=new ShortMessage();
        
            //Put the instruction in the message
            S.setMessage(one, two, three, four);
            event=new MidiEvent(S, five);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return event;
    }
    
    
    private class StartListener implements ActionListener		//ActionListener for Start button
    {
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                createMelodyRemastered();
                S.setTempoInBPM(t);
                S.start();
                //System.out.println(S.getTempoInBPM());
                //S.setTempoInBPM(200);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    private class StopListener implements ActionListener		//ActionListener for Stop button
    {
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                S.stop();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    private class TempoUpListener implements ActionListener		//ActionListener for Tempo up button
    {
        public void actionPerformed(ActionEvent e)
        {
            t=t+5.0F;
            S.setTempoInBPM(t);
            int temp=(int)t;
            tempo.setText("Tempo: "+String.valueOf(temp)+" BPM");
            //System.out.println(t);
        }
    }
    
    private class TempoDownListener implements ActionListener	//ActionListener for Tempo down button
    {
        public void actionPerformed(ActionEvent e)
        {
            t=t-5.0F;
            if(t<0.0F)
            t=0.0F;
            S.setTempoInBPM(t);
            int temp=(int)t;
            tempo.setText("Tempo: "+String.valueOf(temp)+" BPM");
            //System.out.println(t);
        }
    }
        
    private class ClearListener implements ActionListener		//ActionListener for Clear button
    {
        public void actionPerformed(ActionEvent e)
        {
            for(JCheckBox temp: cBoxList)
                temp.setSelected(false);
            // frame.repaint();
            
        }
    }
    
    private class RepeaterListener implements ItemListener		//ItemListener for Repeat check box				
    {
        public void itemStateChanged(ItemEvent e)
        {
            if(repeater.isSelected())
            S.setLoopCount(S.LOOP_CONTINUOUSLY);
            //System.out.println(S.LOOP_CONTINUOUSLY);
            else
            S.setLoopCount(0);
            //System.out.println("0");
        }
    }
    
    private class SaveMenuListener implements ActionListener	//ActionListenr for File -> Save menu
    {
        public void actionPerformed(ActionEvent e)
        {
            int state;
			/*Get reference to a JFileChooser with only one selection enabled to get reference to a file from by browsing the system files*/
            JFileChooser dialog = new JFileChooser();
            dialog.setMultiSelectionEnabled(false);
			
			/*Set frame as parent and the dialog to display options typical of a save dialog box*/
            state = dialog.showSaveDialog(frame);
            dialog.setVisible(true);
			
			/*What happens when the user either clicks cancel or an error occurs*/
            if(state!=JFileChooser.CANCEL_OPTION && state!=JFileChooser.ERROR_OPTION)
            saveFile(dialog.getSelectedFile());											//Utility function for handling the low level details like streams
        }
    }
    
	//Utility function to take care of the dirty work of handling low level streams
    private void saveFile(File file)
    {
        boolean []list = new boolean[256];
        
        for(int i=0;i<256;i++)
        {
            JCheckBox box = cBoxList.get(i);
            list[i] = box.isSelected();
            
        }
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
        
            oos.close();
            fos.close();
                      
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
       
             
              
    }
    
    private class LoadMenuListener implements ActionListener		//ActionListener for File -> Load menu
    {
        public void actionPerformed(ActionEvent e)
        {
            int state;
			/*Same as that for File -> Save menu*/
            JFileChooser dialog = new JFileChooser();
            dialog.setMultiSelectionEnabled(false);
            state = dialog.showDialog(frame, "Load");
            dialog.setVisible(true);
            if(state!=JFileChooser.CANCEL_OPTION && state!=JFileChooser.ERROR_OPTION)
            loadFile(dialog.getSelectedFile());
        }
    }

    //Utility function to handle streams
    private void loadFile(File file)
    {
        FileInputStream fis;
        ObjectInputStream ois;
        
        S.stop();
        boolean []list = new boolean[256];
                       
        try
        {
        
        fis = new FileInputStream(file);
        ois = new ObjectInputStream(fis);
        Object ob = ois.readObject();
            
        if(ob instanceof boolean[])
        list = (boolean[])ob;
            
            
        ois.close();
        fis.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            
        }
        for(int i=0;i<256;i++)
        {
            JCheckBox box = cBoxList.get(i);
            if(list[i])
            box.setSelected(true);
            else
            box.setSelected(false);
        }
        createMelodyRemastered();
    }
        
    private class AboutMenuItemListener implements ActionListener	//ActionListener for Help -> About menu
    {
        public void actionPerformed(ActionEvent e)
        {
            String info = String.format("BeatBoxxxx Version 1.3.1\n\nCoded and Designed by: Saqib Nizam Shamsi\nCredits: Kathy Sierra and Bert Bates (Head First: Java)");
            ImageIcon icon = new ImageIcon("..//resources//BeatBoxIcon64.jpg");
            JOptionPane about = new JOptionPane();
            about.showMessageDialog(frame, info, "About BeatBox", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }
    
    private class AboutPanel extends JPanel				//The display of Help -> About menu
    {
        public void paintComponent(Graphics g)
        {
            g.setColor(Color.black);
            Font customFont = new Font(Font.SANS_SERIF, Font.BOLD, 28);
            g.setFont(customFont);
            g.drawString("BeatBox Version 1.3.1",5,24);
            //Font regularFont = new Font(Font.SERIF);
            
        }
    }
        
}
