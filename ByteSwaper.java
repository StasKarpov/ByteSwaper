

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.awt.color.*;
import java.nio.charset.StandardCharsets;

class ByteSwaper extends JPanel  {

    private JFileChooser fileChooser;
    private JButton  folderB;
    private JTextField extensionF;
    private JButton aStringB;
    private JButton bStringB;
    private JButton aStringTextB;
    private JButton bStringTextB;
    private JButton doB;
    private byte[] a;
    private byte[] b;
    private JFrame frame;
    private JLabel messageL;
    private String dirname;
    private String extension;
    private ChangeLog changeLog;

    private void setDefault(){
        a = null;
        b = null;
        folderB.setBackground(null);
        folderB.setText("Choose a folder path");
        aStringB.setBackground(null);
        aStringB.setText("Import A-data sequence from file");
        aStringTextB.setBackground(null);
        bStringB.setBackground(null);
        bStringB.setText("Import B-data sequence from file");
        bStringTextB.setBackground(null);
        extensionF.setText("Enter an extension");
        messageL.setText("Hello!");

    }

    private String getExtension(String filename){
        String ext = "";

        int i = filename.lastIndexOf('.');
        if (i > 0) {
            ext = filename.substring(i+1);
        }
        return ext;
    }

    /*Function checks all files in dirname
      *  if its directory it calls itself
      *  if its file and extension passing calls replaceBytes
      *  @return numbers of changes
      */

    private void checkFile(File rootFile) throws IOException{
        String[] paths = rootFile.list();
        File sonFile = null;
        changeLog.newDir();
        messageL.setText("\nReading "+ rootFile.getName() + "...");
        try {
            for (String path : paths) {

                sonFile = new File(rootFile.getPath() + "/" + path);
                if (sonFile.isDirectory()) {//if it is a directory recursivly open file
                    this.checkFile(sonFile);
                } else {
                    changeLog.newFile();
                    if (getExtension(sonFile.getName()).equals(extension)) {
                        changeLog.newFileExt();
                        replaceBytes(sonFile);
                    }
                }
            }
        }catch (NullPointerException exc){
            exc.printStackTrace();
        }


    }

    // Creates filter stream that filtering all a[] to b[] in file
    private void replaceBytes(File file) throws IOException{
        messageL.setText("Searching in " + file.getName() );
        ReplacingInputStream filter = new ReplacingInputStream(file,a,b);
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

        int x;
        while ( !filter.isEnd()  ){
            x=filter.read();
            outBytes.write(x);
        }


        changeLog.addMatches(filter.readNumOfMatches());

        FileOutputStream outFile = new FileOutputStream(file,false);
        outBytes.writeTo(outFile);
        outFile.close();

    }

    public void runGui(){
        frame=new JFrame("ByteSwaper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 510, 350);

        fileChooser = new JFileChooser();


        final JLabel info = new JLabel("<html><h5>ByteChanger search for all files with given <i>extension</i> in given <i>directory</i> <br>" +
                                          "then change all A sequence of data to B  in found files.</html>");
        info.setBounds(10,10,500,30);
        frame.add(info);


        folderB = new JButton("Choose a directory");
        folderB.setBounds(15, 55, 460, 30);
        folderB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(ByteSwaper.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    dirname = fileChooser.getSelectedFile().getPath();
                    folderB.setBackground(Color.GREEN);
                    folderB.setText(dirname);
                } else {
                    messageL.setText("Open command cancelled by user.\n") ;
                }
            }
        });
        frame.add(folderB);


        aStringB=new JButton("Import A-data sequence from file");
        aStringB.setBounds(15, 100, 430, 30);
        aStringB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = fileChooser.showOpenDialog(ByteSwaper.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        File aFile = fileChooser.getSelectedFile();
                        a = Files.readAllBytes(Paths.get(aFile.getPath()));
                        if(a.length < 128){
                            messageL.setText("Please choose a file bigger than 128 bytes");
                            a = null;
                            aStringB.setBackground(Color.getHSBColor(360,80,60));
                        }else {
                            aStringB.setBackground(Color.GREEN);
                            aStringTextB.setBackground(Color.GREEN);
                            aStringB.setText(aFile.getPath());
                            messageL.setText("File " + fileChooser.getSelectedFile().getName() + " is choosen.\n") ;
                        }
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }

                } else {
                    messageL.setText("Open command cancelled by user.\n") ;
                }
            }
        });
        frame.add(aStringB);

        //input byte sequence as text
        aStringTextB=new JButton(new ImageIcon("pencil_icon.jpg"));
        aStringTextB.setBounds(445, 100, 30, 30);
        aStringTextB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                a = JOptionPane.showInputDialog("Enter A-data sequence as text").getBytes();
                if(a.length != 0){
                    aStringB.setBackground(Color.GREEN);
                    aStringTextB.setBackground(Color.GREEN);
                    aStringB.setText(new String(a,0));

                }else{
                    messageL.setText("Please enter an A-data sequence or import it from file");
                    a = null;
                    aStringB.setBackground(Color.getHSBColor(360,80,60));
                }
            }
        });
        frame.add(aStringTextB);

        bStringB=new JButton("Import B-data sequence from file");
        bStringB.setBounds(15, 145, 430, 30);
        bStringB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int returnVal = fileChooser.showOpenDialog(ByteSwaper.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        File aFile = fileChooser.getSelectedFile();
                        b = Files.readAllBytes(Paths.get(aFile.getPath()));
                        bStringB.setBackground(Color.GREEN);
                        bStringB.setText(aFile.getPath());
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                    messageL.setText("File " + fileChooser.getSelectedFile().getName() + " is choosen.\n" );
                } else {
                    messageL.setText("Open command cancelled by user.\n") ;
                }
            }
        });
        frame.add(bStringB);

        //input byte sequence as text
        bStringTextB=new JButton(new ImageIcon("pencil_icon.jpg"));
        bStringTextB.setBounds(445, 145, 30, 30);
        bStringTextB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                b = JOptionPane.showInputDialog("Enter B-data sequence as text:").getBytes();
                if(b.length != 0){
                    bStringB.setBackground(Color.GREEN);
                    bStringTextB.setBackground(Color.GREEN);
                    bStringB.setText(new String(b,0));
                }else{
                    messageL.setText("Please enter an B-data sequence or import it from file");
                    b = null;
                    bStringB.setBackground(Color.getHSBColor(360,80,60));
                }
            }
        });
        frame.add(bStringTextB);


        extensionF = new JTextField("Enter an extension");
        extensionF.setBounds(15, 190, 460, 30);
        extensionF.addMouseListener(new MouseListener() {
                                        @Override
                                        public void mouseClicked(MouseEvent mouseEvent) {
                                            extensionF.setText("");
                                        }

                                        @Override
                                        public void mousePressed(MouseEvent mouseEvent) {

                                        }

                                        @Override
                                        public void mouseReleased(MouseEvent mouseEvent) {

                                        }

                                        @Override
                                        public void mouseEntered(MouseEvent mouseEvent) {

                                        }

                                        @Override
                                        public void mouseExited(MouseEvent mouseEvent) {

                                        }
                                    });
        frame.add(extensionF);


        doB=new JButton("Click to change!");
        doB.setBounds(40, 250, 400, 30);
        doB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if( (dirname == null)){
                    folderB.setBackground(Color.getHSBColor(360,80,60));
                    messageL.setText("Please enter root folder name path.");
                    return;
                }
                if( (extensionF.getText()).isEmpty() ){
                    messageL.setText("Please enter extensionF.");
                    return;
                }else extension = extensionF.getText();
                if( a == null){
                    aStringB.setBackground(Color.getHSBColor(360,80,60));
                    messageL.setText("Please choose A bytes.");
                    return;
                }
                if( b == null){
                    bStringB.setBackground(Color.getHSBColor(360,80,60));
                    messageL.setText("Please choose B bytes.");
                }
                try {
                    changeLog = new ChangeLog(extension);
                    checkFile(new File(dirname));
                    messageL.setText("Ready!");
                    JOptionPane.showMessageDialog(null, changeLog.getString());
                    setDefault();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
        frame.add(doB);

        messageL = new JLabel("Hello!");
        messageL.setBounds(15,300,460,40);
        frame.add(messageL);

        frame.setLayout(null);
        frame.setVisible(true);
    }



    public static void main(String[] args) throws Exception {

        ByteSwaper bs = new ByteSwaper();

        bs.runGui();

    }
}
