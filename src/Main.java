//Imports all packages needed
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.*;
import java.rmi.ConnectException;
import java.util.*;


public class Main {
    public static void main(String[] args) throws IOException {
        GUI login = new GUI();
        JFrame sframe = login.Addframe(500, 500, "Welcome");
        JPanel spanel = login.Addpanel(sframe);
        JButton senter = login.Addbutton(100, 100, 20, 50, "Enter", spanel);
        JButton sleave = login.Addbutton(100, 100, 20, 400, "Leave", spanel);
        sleave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login.Close(sframe);
            }
        });
        senter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String connected = Connect();
                    GUI home = new GUI();
                    JFrame hframe = home.Addframe(500, 500, "Home Page");
                    JPanel hpanel = home.Addpanel(hframe);
                    JButton upload = home.Addbutton(100, 100, 20, 50, "Upload", hpanel);
                    JButton view = home.Addbutton(100, 100, 20, 20, "View", hpanel);
                    JButton back = home.Addbutton(100,100,20,0,"Back",hpanel);
                    back.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            home.Close(hframe);
                        }
                    });
                    hframe.addWindowListener(new WindowAdapter() {
                        public void windowClosed(WindowEvent e) {
                            try {
                                String saved = GetRequest("Disconnect",null);
                                home.AddOP(saved);
                            }
                            catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            catch (ClassNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    });
                    upload.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JFileChooser file = new JFileChooser();
                            int select = file.showOpenDialog(null);
                            if (select == JFileChooser.APPROVE_OPTION) {
                                File uploadf = (file.getSelectedFile());
                                byte[] serialesd = new byte[0];
                                try {
                                    serialesd = SerialisedData(uploadf);
                                    home.AddOP(PostRequest(serialesd, uploadf.getName()));
                                }
                                catch (IOException ex) {
                                    home.AddOP("Server offline. Post Failed");
                                }
                            }
                        }
                    });
                    view.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JFrame vhome = home.Addframe(500, 500, "View Page");
                            JPanel vpanel = home.Addpanel(vhome);
                            String f = null;
                            try {
                                f = GetRequest("View", "*");
                            } catch (IOException | ClassNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                            Object[] files = Arrays.stream((f.split("SPACE", -2))).toArray();
                            JList vlist = home.AddList(files, vpanel);
                            JButton vdownload = home.Addbutton(100, 100, 20, 50, "Download", vpanel);
                            JButton vdelete=home.Addbutton(100,100,20,0,"Delete",vpanel);
                            JButton vback = home.Addbutton(100,100,20,100,"Back",vpanel);
                            vhome.addWindowListener(new WindowAdapter() {
                                public void windowClosed(WindowEvent e) {
                                    try {
                                        String saved = GetRequest("Disconnect", null);
                                        home.AddOP(saved);
                                    } catch (IOException | ClassNotFoundException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            });
                            vback.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    home.Close(vhome);
                                }
                            });
                            vdownload.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    String selectedfile = vlist.getSelectedValue().toString();
                                    try {
                                        String file = GetRequest("Download", selectedfile);
                                        home.AddOP(file);
                                    } catch (IOException | ClassNotFoundException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            });
                            vdelete.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    String selectedfile = vlist.getSelectedValue().toString();
                                    try {
                                        String result = GetRequest("Delete", selectedfile);
                                        home.AddOP(result);

                                    } catch (IOException | ClassNotFoundException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            });


                        }
                    });
                } catch (IOException c) {
                    login.AddOP("Server not found");
                }
            }
        });
    }

    public static String PostRequest(byte[] code, String name) throws IOException {
        String response = "";
        try {
            URL link = new URL("http://localhost:2023/EdgeBox");
            HttpURLConnection c = (HttpURLConnection) link.openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("Content-Type", "application/octet-stream");
            c.setRequestProperty("Name", name);
            c.setDoOutput(true);
            OutputStream o = c.getOutputStream();
            o.write(code, 0, code.length);
            if (c.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                response = "POST request completed";
            }
        } catch (ConnectException e) {
        }

        return response;
    }

    public static String Connect() throws IOException {
        URL link = new URL("http://localhost:2023/EdgeBox");
        HttpURLConnection c = (HttpURLConnection) link.openConnection();
        String response = "";
        try {
            c.setRequestMethod("GET");
            c.setRequestProperty("Target", "Start up");
            if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                char[] responsel = new char[c.getContentLength()];
                System.out.println(c.getInputStream());
                StringBuilder output = new StringBuilder();
                Reader in = new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8);
                for (int x = 0; (x = in.read(responsel, 0, responsel.length)) > 0; x++) {
                    output.append(responsel, 0, x);
                }
                response = output.toString();
            }
        } catch (ConnectException z) {

        }
        return response;
    }

    public static String GetRequest(String action, String name) throws IOException, ClassNotFoundException {
        URL link = new URL("http://localhost:2023/EdgeBox");
        HttpURLConnection c = (HttpURLConnection) link.openConnection();
        String foutput = "";
        c.setRequestMethod("GET");
        c.setRequestProperty("Target", action);
        c.setRequestProperty("Name", name);
        if (action.equals("View")) {
            if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                char[] responsel = new char[c.getContentLength()];
                StringBuilder output = new StringBuilder();
                Reader in = new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8);
                for (int x = 0; (x = in.read(responsel, 0, responsel.length)) > 0; x++) {
                    output.append(responsel, 0, x);
                }
                foutput = output.toString();
            } else if (c.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                foutput = "There are no saved files on Edge Box";
            }
        }
        else if (action.equals("Download")) {
            if (c.getResponseCode() == 200) {
                InputStream in = c.getInputStream();
                ObjectInputStream contents = (new ObjectInputStream(in));
                String fcontent = contents.readObject().toString();
                FileOutputStream nf = new FileOutputStream("C:\\Users\\TMBak\\IdeaProjects\\EdgeHillBox\\src\\EdgeHillBox\\" + name);
                nf.write(fcontent.getBytes(StandardCharsets.UTF_8));
                foutput=name+" has been downloaded to your EdgeBox folder";
            }

        }
        else if (action.equals(("Delete"))) {
            if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                char[] responsel = new char[c.getContentLength()];
                System.out.println(c.getInputStream());
                StringBuilder output = new StringBuilder();
                Reader in = new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8);
                for (int x = 0; (x = in.read(responsel, 0, responsel.length)) > 0; x++) {
                    output.append(responsel, 0, x);
                }
                foutput = output.toString();
            }
        }
        else if (action.equals("Disconnect")){
            if (c.getResponseCode()==HttpURLConnection.HTTP_OK){
                foutput="Server has been backed";
            }
        }
        return foutput;
    }

    public static byte[] SerialisedData(File file) throws IOException {
        StringBuilder f = new StringBuilder();
        BufferedReader r = new BufferedReader(new FileReader(file));
        String read = r.readLine();
        if (read != null) {
            do {
                f.append(read);
                read = r.readLine();
            } while (read != null);
        }
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(f);
        return b.toByteArray();
    }
}











