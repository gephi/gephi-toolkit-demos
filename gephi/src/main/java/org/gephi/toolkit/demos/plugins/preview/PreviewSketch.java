/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.toolkit.demos.plugins.preview;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewMouseEvent;
import org.gephi.preview.api.Vector;
import org.openide.util.Lookup;

/**
 *
 * @author mbastian
 */
public class PreviewSketch extends JPanel implements MouseListener, MouseWheelListener, MouseMotionListener {

    private static final int WHEEL_TIMER = 500;
    //Data
    private final PreviewController previewController;
    private final G2DTarget target;
    //Geometry
    private final Vector ref = new Vector();
    private final Vector lastMove = new Vector();
    //Utils
    private final RefreshLoop refreshLoop = new RefreshLoop();
    private Timer wheelTimer;
    private boolean inited;
    private final boolean isRetina;

    public PreviewSketch(G2DTarget target) {
        this.target = target;
        previewController = Lookup.getDefault().lookup(PreviewController.class);
        isRetina = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!inited) {
            //Listeners
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
            inited = true;
        }

        int width = (int) (getWidth() * (isRetina ? 2.0 : 1.0));
        int height = (int) (getHeight() * (isRetina ? 2.0 : 1.0));

        if (target.getWidth() != width || target.getHeight() != height) {
            target.resize(width, height);
        }

        g.drawImage(target.getImage(), 0, 0, getWidth(), getHeight(), this);
    }

    public void setMoving(boolean moving) {
        target.setMoving(moving);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (previewController.sendMouseEvent(buildPreviewMouseEvent(e, PreviewMouseEvent.Type.CLICKED))) {
            refreshLoop.refreshSketch();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        previewController.sendMouseEvent(buildPreviewMouseEvent(e, PreviewMouseEvent.Type.PRESSED));
        ref.set(e.getX(), e.getY());
        lastMove.set(target.getTranslate());

        refreshLoop.refreshSketch();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!previewController.sendMouseEvent(buildPreviewMouseEvent(e, PreviewMouseEvent.Type.RELEASED))) {
            setMoving(false);
        }

        refreshLoop.refreshSketch();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getUnitsToScroll() == 0) {
            return;
        }
        float way = -e.getUnitsToScroll() / Math.abs(e.getUnitsToScroll());
        target.setScaling(target.getScaling() * (way > 0 ? 2f : 0.5f));
        setMoving(true);
        if (wheelTimer != null) {
            wheelTimer.cancel();
            wheelTimer = null;
        }
        wheelTimer = new Timer();
        wheelTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setMoving(false);
                refreshLoop.refreshSketch();
                wheelTimer = null;
            }
        }, WHEEL_TIMER);
        refreshLoop.refreshSketch();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!previewController.sendMouseEvent(buildPreviewMouseEvent(e, PreviewMouseEvent.Type.DRAGGED))) {
            setMoving(true);
            Vector trans = target.getTranslate();
            trans.set(e.getX(), e.getY());
            trans.sub(ref);
            trans.mult(isRetina ? 2f : 1f);
            trans.div(target.getScaling()); // ensure const. moving speed whatever the zoom is
            trans.add(lastMove);

            refreshLoop.refreshSketch();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public void zoomPlus() {
        target.setScaling(target.getScaling() * 2f);
        refreshLoop.refreshSketch();
    }

    public void zoomMinus() {
        target.setScaling(target.getScaling() / 2f);
        refreshLoop.refreshSketch();
    }

    public void resetZoom() {
        target.reset();
        refreshLoop.refreshSketch();
    }

    private Vector screenPositionToModelPosition(Vector screenPos) {
        Vector center = new Vector(getWidth() / 2f, getHeight() / 2f);
        Vector scaledCenter = Vector.mult(center, target.getScaling());
        Vector scaledTrans = Vector.sub(center, scaledCenter);

        Vector modelPos = new Vector(screenPos.x, screenPos.y);
        modelPos.sub(scaledTrans);
        modelPos.div(target.getScaling());
        modelPos.sub(target.getTranslate());
        return modelPos;
    }

    private PreviewMouseEvent buildPreviewMouseEvent(MouseEvent evt, PreviewMouseEvent.Type type) {
        int mouseX = evt.getX();
        int mouseY = evt.getY();
        PreviewMouseEvent.Button button = PreviewMouseEvent.Button.LEFT;
        if (SwingUtilities.isMiddleMouseButton(evt)) {
            button = PreviewMouseEvent.Button.MIDDLE;
        } else if (SwingUtilities.isLeftMouseButton(evt)) {
            button = PreviewMouseEvent.Button.LEFT;
        } else if (SwingUtilities.isRightMouseButton(evt)) {
            button = PreviewMouseEvent.Button.RIGHT;
        }

        Vector pos = screenPositionToModelPosition(new Vector(mouseX, mouseY));

        return new PreviewMouseEvent((int) pos.x, (int) pos.y, type, button, null);
    }

    private class RefreshLoop {

        private final long DELAY = 100;
        private final AtomicBoolean running = new AtomicBoolean();
        private final AtomicBoolean refresh = new AtomicBoolean();
        //Timer
        private long timeout = DELAY * 10;
        private Timer timer;

        public RefreshLoop() {
            super();
        }

        public void refreshSketch() {
            refresh.set(true);
            if (!running.getAndSet(true)) {
                startTimer();
            }
        }

        private void startTimer() {
            timer = new Timer("PreviewRefreshLoop", true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (refresh.getAndSet(false)) {
                        target.refresh();
                        repaint();
                    } else if (timeout == 0) {
                        timeout = DELAY * 10;
                        stopTimer();
                    } else {
                        timeout -= DELAY;
                    }
                }
            }, 0, DELAY);
        }

        private void stopTimer() {
            timer.cancel();
            running.set(false);
        }
    }
}
