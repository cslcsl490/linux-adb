/*
 * Copyright (c) 2010, Oracle. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of Oracle nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.linuxadb.dao.view;

import com.sun.deploy.util.StringUtils;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Annetra extends javax.swing.JFrame {



    // 定时器
    private java.util.Timer timer = new java.util.Timer();
    /** Creates new form Antenna */
    public Annetra() {
        initComponents();

// 启动一个CMD
        try {
            this.process = Runtime.getRuntime().exec("cmd");
            inputStream = process.getInputStream();
            errorStream = process.getErrorStream();
            errorWorker.execute();
            inputWorker.execute();
            this.outStream = new PrintWriter(process.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

// 命令框中按下回车键的事件
        messagePane.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = messagePane.getCmd();
                if (cmd == null || "".equals(cmd)) {
                    return;
                } else if ("cls".equals(cmd)) {
                    messagePane.clearDate();
                    messagePane.cleanCmd();
                    return;
                } else if ("exit".equals(cmd)) {
                    dispose();
                    return;
                }
                messagePane.addData(messagePane.getCmdTip() + cmd);
                outStream.println(cmd);
                outStream.flush();
                messagePane.cleanCmd();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    outStream.close();
                    inputStream.close();
                    errorStream.close();
                    process.destroy();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        messagePane.cleanCmd();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jTextField7 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listmode=new DefaultListModel();
//        listmode.addElement("yoyo");
//        listmode.addElement("yoy12o");
//        listmode.addElement("yoy22o");
        jList1 = new JList(listmode);
        jList1.setFixedCellWidth(100);
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
//        jPanel2 = new javax.swing.JPanel();

        device_s = jList1.getSelectedValue()==null?" ":(" -s "+(String) jList1.getSelectedValue());
            // 背景图片
        jPanel2 = flushScreenShot(jList1.getSelectedValue()==null?"":(String) jList1.getSelectedValue());

        jPanel2.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

                startX = (int) Math.floor(e.getX() * PHONE_X/getX());
                startY = (int) Math.floor(e.getY() * PHONE_Y/getY());
            }


            // swipe adb shell input swipe 250 250 300 300
            // click adb shell input tap 447 1860
            // wakeup adb shell input keyevent 26
            // input adb shell text xxx

            @Override
            public void mouseReleased(MouseEvent e) {
                endX = (int) Math.floor(e.getX() * PHONE_X/getX());
                endY = (int) Math.floor(e.getY() * PHONE_Y/getY());
                if(endX < 0 || endY < 0){
                    return ;
                }
                String cmd;
                if(Point.distance(endX,endY,startX,startY)<=5){
                    // 执行点击 => 截图
                    cmd = "adb  "+device_s+" shell input tap "+endX+" "+endY;
                    messagePane.addData(messagePane.getCmdTip() + cmd);
                    outStream.println(cmd);
                    outStream.flush();
                    messagePane.cleanCmd();
                }else{
                    // 执行滑动 => 截图
                    cmd = "adb  "+device_s+" shell input swipe "+startX+" "+startY +" "+endX+" "+endY;
                    messagePane.addData(messagePane.getCmdTip() + cmd);
                    outStream.println(cmd);
                    outStream.flush();
                    messagePane.cleanCmd();
                }
                cmd = "adb  "+device_s+" shell /system/bin/screencap -p /sdcard/screenshot.png";
                messagePane.addData(messagePane.getCmdTip() + cmd);
                outStream.println(cmd);
                outStream.flush();
                messagePane.cleanCmd();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                jPanel2 = flushScreenShot("1");
//                repaint();
//            }
//        }, 0, 1000);
//        thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
//                .addComponent(jPanel1, 0, 262, Short.MAX_VALUE));
//        thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
//                .addComponent(jPanel1, 0, 384, Short.MAX_VALUE));
//        messagePane = new javax.swing.JPanel();
        // TODO 86功能按钮
//        jButton8.setEnabled(false);
//        jButton6.setEnabled(false);

        messagePane = new CMDPane();
        messagePane.setForeground(Color.WHITE);
        messagePane.setBackground(Color.BLACK);
        messagePane.setFont(messagePane.getFont().deriveFont(14f));
        add(messagePane, BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Antenna");
        jButton8.setText("截图");
        jButton6.setText("复制消息");


        jButton6.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String text = jTextField7.getText();
                if(text==null || text.equals("")){
                    return ;
                }

                String cmd = "adb  "+device_s+" shell input text "+text;
                messagePane.addData(messagePane.getCmdTip() + cmd);
                outStream.println(cmd);
                outStream.flush();
                messagePane.cleanCmd();

            }
        });

        jList1.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                device_s = jList1.getSelectedValue()==null?" ":(" -s "+(String) jList1.getSelectedValue());
            }
        });


        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String cmd = "adb  "+device_s+" shell /system/bin/screencap -p /sdcard/screenshot.png";
                messagePane.addData(messagePane.getCmdTip() + cmd);
                outStream.println(cmd);
                outStream.flush();
                messagePane.cleanCmd();

                cmd = "adb  "+device_s+" pull /sdcard/screenshot.png d:\\download"+(jList1.getSelectedValue()==null?"":(String) jList1.getSelectedValue()+".png");
                messagePane.addData(messagePane.getCmdTip() + cmd);
                outStream.println(cmd);
                outStream.flush();
                messagePane.cleanCmd();

                jPanel2 = flushScreenShot(jList1.getSelectedValue()==null?"":(String) jList1.getSelectedValue());
                jPanel2.repaint();
            }
        });

//        adb shell /system/bin/screencap -p /sdcard/screenshot.png
//        adb pull /sdcard/screenshot.png d:\download

        jTextField7.setText("");

        jButton7.setText("连接端口查看");
        // TODO flush devices
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String cmd = "adb devices";
                messagePane.addData(messagePane.getCmdTip() + cmd);
//                messagePane.getCmdTip
                outStream.println(cmd);
                outStream.flush();
                messagePane.cleanCmd();
            }
        });

//        jList1.setModel(new javax.swing.AbstractListModel() {
//            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
//            public int getSize() { return strings.length; }
//            public Object getElementAt(int i) { return strings[i]; }
//        });
        jScrollPane1.setViewportView(jList1);

        jButton9.setText("提交命令");
        // TODO   command commit
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandActionPerformed(evt);
            }
        });

        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandActionPerformed(evt);
            }
        });


        jPanel1.setPreferredSize(new java.awt.Dimension(500, 500));

        messagePane.setPreferredSize(new java.awt.Dimension(500, 500));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(messagePane);
        messagePane.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 500, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 500, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(messagePane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(messagePane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jPanel5Layout.createSequentialGroup()
                                                .add(jTextField7)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jButton9)
                                                .addContainerGap())
                                        .add(jPanel5Layout.createSequentialGroup()
                                                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                                        .add(jScrollPane1)
                                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5Layout.createSequentialGroup()
                                                                .add(jButton7)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(jButton8)))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jButton6)
                                                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                .add(0, 0, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel5Layout.createSequentialGroup()
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jTextField7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jButton9))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jButton7)
                                        .add(jButton8)
                                        .add(jButton6))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 498, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel3Layout.createSequentialGroup()
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel3Layout.createSequentialGroup()
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);


        org.jdesktop.layout.GroupLayout jPanel2Layout2 = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout2);
        jPanel2Layout2.setHorizontalGroup(
                jPanel2Layout2.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 215, Short.MAX_VALUE)
        );
        jPanel2Layout2.setVerticalGroup(
                jPanel2Layout2.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 339, Short.MAX_VALUE)
        );
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap()
                                .add(0, 15, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(layout.createSequentialGroup()
                                .add(20, 20, 20)
                                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(0, 15, Short.MAX_VALUE))
        );



        pack();
    }// </editor-fold>

    private JPanel flushScreenShot(String device) {

        return new JPanel(){
            private static final long serialVersionUID = 1L;

            public void paint(Graphics g) {
                super.paint(g);
                try {
                    File file = new File("/d:/download"+(jList1.getSelectedValue()==null?"":(String) jList1.getSelectedValue()+".png"));
                    BufferedImage img = null;
                    img = ImageIO.read(file);
                    g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                repaint();
            }
        };
    }

    private void commandActionPerformed(ActionEvent evt) {
        String cmd = jTextField7.getText();
        if (cmd == null || "".equals(cmd)) {
            return;
        } else if ("cls".equals(cmd)) {
            messagePane.clearDate();
            messagePane.cleanCmd();
            return;
        } else if ("exit".equals(cmd)) {
            dispose();
            return;
        }
        messagePane.addData(messagePane.getCmdTip() + cmd);
        outStream.println(cmd);
        outStream.flush();
        messagePane.cleanCmd();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Annetra();
            }
        });
    }

    private static final long serialVersionUID = 1L;

    private Process process;
    private PrintWriter outStream;
    private InputStream inputStream;
    private InputStream errorStream;
    private InputWorker inputWorker = new InputWorker();
    private ErrorWorker errorWorker = new ErrorWorker();



    class ErrorWorker extends SwingWorker<String, String> {

        @Override
        protected void process(List<String> chunks) {
            boolean needRecode = false;
            for (String string : chunks) {
                messagePane.addData(string);
            }
        }

        @Override
        protected String doInBackground() throws Exception {
            byte[] buf = new byte[1024];
            int size;
            while (true) {
                if ((size = errorStream.read(buf)) != -1) {
                    String org = new String(buf, 0, size, "gbk");
                    String[] lines = org.split("\\n");
                    if (lines != null) {
                        for (int i = 0; i < lines.length; i++) {
                            publish(lines[i]);
                        }
                    }
                }
            }
        }

    }

    class InputWorker extends SwingWorker<String, String> {

        public InputWorker() {
            addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("cmd".equals(evt.getPropertyName())) {
                        messagePane.setCmdTip(evt.getNewValue());
                    }
                }
            });
        }
        private boolean needRecode = false;
        @Override
        protected void process(List<String> chunks) {
            for (String string : chunks) {
                messagePane.addData(string);
                if(needRecode && string.contains("device") ){
                    string = string.replace("device","").trim();
                    listmode.addElement(string);
                    jList1.updateUI();
                }else{
                    needRecode = false;
                }
                if(string.indexOf("List of devices attached")>-1){
                    listmode.removeAllElements();
                    needRecode = true;
                }
            }
        }

        @Override
        protected String doInBackground() throws Exception {
            byte[] buf = new byte[1024];
            int size;
            while (true) {
                if ((size = inputStream.read(buf)) != -1) {
                    String org = new String(buf, 0, size, "gbk");
                    String[] lines = org.split("\\n");
                    if (lines != null && lines.length > 0) {
                        for (int i = 0; i < lines.length - 1; i++) {
//给予一定的延时，保证正常信息在错误信息之后显示
                            Thread.sleep(10);
                            publish(lines[i]);
                        }
                        firePropertyChange("cmd", null,
                                lines[lines.length - 1]);
                    }
                }
            }
        }

    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private DefaultListModel listmode;
    private CMDPane messagePane;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField7;
    private String  device_s;
    private static int startX;
    private static int startY;
    private static int endX;
    private static int endY;
    private static Double PHONE_X=480.0;
    private static Double PHONE_Y=480.0;
    // End of variables declaration

}
