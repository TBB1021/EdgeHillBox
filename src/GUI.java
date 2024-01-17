//This file is dedicated to the creation of Swing GUI widgets
//Below are the packages imported
import javax.swing.*;
import java.awt.*;

//Creates public class GUI
public class GUI {
    //creates and returns JFframe based on ints and strings which
    public JFrame Addframe (int wid, int hei, String name){
        JFrame frame = new JFrame(name);
        frame.setSize(wid,hei);
        frame.setVisible(true);
        return frame;
    }

    //Creates and returns a custom Jbutton and places it in JPanel panel
    public JButton Addbutton(int hei, int wid, int x, int y, String name, JPanel panel){
        JButton button = new JButton(name);
        button.setSize(wid,hei);
        GridBagConstraints grid = new GridBagConstraints();
        grid.fill=GridBagConstraints.HORIZONTAL;
        grid.gridx=x;
        grid.gridy=y;
        panel.add(button,grid);
        return button;
    }

    //creates and returns Jpanel which is placed in Jframe frame
    public JPanel Addpanel(JFrame frame) {
        JPanel panel= new JPanel(new GridBagLayout());
        frame.add(panel);
        return panel;
    }

    //closes passed through Jframe frame
    public void Close(JFrame frame){
        frame.dispose();
    }

    //Creates and returns JLIST which is filled by Object files and placed in panel
    public JList AddList(Object files, JPanel panel) {
        JList list = new JList((Object[]) files);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(0);
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 80));
        listScroller.setVisible(true);
        panel.add(list);
        return list;
    }

    //Creates a JOptionPane that outputs a string message
    public void AddOP(String message){
        JOptionPane.showMessageDialog(null,message);
    }


}





