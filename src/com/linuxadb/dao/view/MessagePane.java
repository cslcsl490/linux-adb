package com.linuxadb.dao.view;

/**
 * Created by sl on 2016/5/1.
 */
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MessagePane extends JComponent {

    private static final int MAXLINE = 10000;

    private LinkedList<String> datas = new LinkedList<String>();

    private int lineH = 1;

    private JScrollBar bar = new JScrollBar(JScrollBar.VERTICAL);


    private int barValue = 0;

    private Runnable barRunnable = new Runnable() {

        @Override
        public void run() {
            bar.setValues(barValue, getHeight() / lineH, 0, datas.size() + 1);
        }
    };

    public MessagePane() {
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        add(bar, BorderLayout.EAST);
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
//控件大小变化时，更新滚动条
                updateBar();
            }
        });
        setFont(UIManager.getFont("Panel.font"));
        lineH = getFontMetrics(getFont()).getHeight() + 2;
        updateBar();
    }


    @Override
    public void setFont(Font font) {
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
//数据超出限定范围时，移除最旧的一条数据
            datas.pollFirst();
        } else {
//数据长度变化时，更新滚动条
            updateBar();
        }
        bar.setValue(datas.size());
        repaint();
    }

    public synchronized void clearDate() {
        datas.clear();
//清除数据后，滚动条值归零
        barValue = 0;
        repaint();
//清除数据后，更新滚动条
        updateBar();
    }

    private int getPaintWidth() {
        return getWidth() - bar.getWidth();
    }

    private int getPaintHeight() {
        return getHeight();
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
        g.setColor(getForeground());
        for (int i = barValue; i < datas.size(); i++) {
            y += lineH;
            if (y > clipY + h) {
//由于是从上往下绘制，因此大于绘制区域下沿时，终止绘制
                break;
            }
            g.drawString(datas.get(i).toString(), 0, y);
        }
    }

}