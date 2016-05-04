package com.linuxadb.dao.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

/**
 * Created by sl on 2016/5/1.
 */
public class CMDPane extends JComponent {

    private static final int MAXLINE = 10000;
    private LinkedList<String> datas = new LinkedList<String>();
    private int lineH = 1;
    private JScrollBar bar = new JScrollBar(JScrollBar.VERTICAL);
    private JTextField field = new JTextField();
    private JTextField label = new JTextField(">");
    private int barValue = 0;

    private Runnable barRunnable = new Runnable() {

        @Override
        public void run() {
            bar.setValues(barValue, getHeight() / lineH, 0, datas.size() + 1);
        }
    };

    public CMDPane() {
        initialize();
    }

    private void initialize() {
        setOpaque(true);
        setLayout(new GridBagLayout());
        label.setOpaque(true);
        field.setOpaque(true);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.VERTICAL;
        add(bar, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.SOUTH;
        add(label, c);
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        add(field, c);
        field.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        label.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 0));
        label.setEditable(false);
        bar.addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int newValue = bar.getValue();
                if (newValue != barValue) {
                    barValue = newValue;
                    repaint();
                }
            }
        });
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
//�ؼ���С�仯ʱ�����¹�����
                updateBar();
            }
        });
        setFont(UIManager.getFont("Panel.font"));
        lineH = getFontMetrics(getFont()).getHeight() + 2;
        updateBar();
    }

    @Override
    public void setBackground(Color bg) {
        label.setBackground(bg);
        field.setBackground(bg);
        super.setBackground(bg);
    }

    @Override
    public void setForeground(Color fg) {
        label.setForeground(fg);
        field.setForeground(fg);
        super.setForeground(fg);
    }

    @Override
    public void setFont(Font font) {
        label.setFont(font);
        field.setFont(font);
        super.setFont(font);
        if (font != null) {
            lineH = getFontMetrics(font).getHeight() + 2;
        }
    }

    private void updateBar() {
        if (SwingUtilities.isEventDispatchThread()) {
            barRunnable.run();
        } else {
            SwingUtilities.invokeLater(barRunnable);
        }
    }

    public synchronized void addData(String data) {
        datas.addLast(data);
        if (datas.size() > MAXLINE) {
//���ݳ����޶���Χʱ���Ƴ���ɵ�һ������
            datas.pollFirst();
        } else {
//���ݳ��ȱ仯ʱ�����¹�����
            updateBar();
        }
        bar.setValue(datas.size());
        repaint();
    }

    public synchronized String getResult() {
//        return  bar.getValue();
        return "";
    }

    public synchronized void clearDate() {
        datas.clear();
//������ݺ󣬹�����ֵ����
        barValue = 0;
        repaint();
//������ݺ󣬸��¹�����
        updateBar();
    }

    public void setCmdTip(Object text) {
        label.setText(text.toString());
        revalidate();
    }

    public String getCmdTip() {
        return label.getText();
    }

    public String getCmd() {
        return field.getText();
    }

    public void cleanCmd() {
        field.setText("");
        field.requestFocusInWindow();
    }

    public void addActionListener(ActionListener listener) {
        field.addActionListener(listener);
    }

    private int getPaintWidth() {
        return getWidth() - bar.getWidth();
    }

    private int getPaintHeight() {
        return getHeight() - field.getHeight();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getPaintWidth();
        int h = getPaintHeight();
        Rectangle clip = g.getClipBounds();
        int clipX = clip.x;
        int clipY = clip.y;
        int clipH = clip.height;
        int y = 0;
        g.setColor(getBackground());
        g.fillRect(clipX, clipY, w, clipH);
        g.setColor(getForeground());
        for (int i = barValue; i < datas.size(); i++) {
            y += lineH;
            if (y > clipY + h) {
//�����Ǵ������»��ƣ���˴��ڻ�����������ʱ����ֹ����
                break;
            }
            g.drawString(datas.get(i).toString(), 0, y);
        }
    }

}
