package com.linuxadb.dao.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class NewJFrame extends javax.swing.JFrame {
	private JPanel jPanel1;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NewJFrame inst = new NewJFrame();
				inst.setSize(new Dimension(350, 500));
//				inst.setExtendedState( inst.MAXIMIZED_BOTH );
//				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public NewJFrame() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setBackground(new java.awt.Color(255,255,255));
			{

				// 背景图片
				jPanel1 = new JPanel(){
					private static final long serialVersionUID = 1L;

					protected void paintComponent(Graphics g) {
						try {
							BufferedImage img = ImageIO.read(new File("/e:/download.jpg"));
							g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};

			}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addComponent(jPanel1, 0, 262, Short.MAX_VALUE));
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addComponent(jPanel1, 0, 384, Short.MAX_VALUE));
			pack();
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}

}
