/*
Copyright 2008-2014 Gephi
Authors : Eduardo Ramos <eduardo.ramos@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.toolkit.demos.plugins.preview;

import javax.swing.JOptionPane;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.preview.api.PreviewMouseEvent;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.spi.PreviewMouseListener;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = PreviewMouseListener.class)
public class MouseListenerTemplate implements PreviewMouseListener {

    @Override
    public void mouseClicked(PreviewMouseEvent event, PreviewProperties properties, Workspace workspace) {
        for (Node node : Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace).getGraph().getNodes()) {
            if (clickingInNode(node, event)) {
                properties.putValue("display-label.node.id", node.getId());
                System.err.println("Node " + node.getLabel() + " clicked!");//System.out is ignored in Netbeans platform applications!!
                JOptionPane.showMessageDialog(null, "Node " + node.getLabel() + " clicked!");
                event.setConsumed(true);//So the renderer is executed and the graph repainted
                return;
            }
        }

        properties.removeSimpleValue("display-label.node.id");
        event.setConsumed(true);//So the renderer is executed and the graph repainted
    }

    @Override
    public void mousePressed(PreviewMouseEvent event, PreviewProperties properties, Workspace workspace) {
    }

    @Override
    public void mouseDragged(PreviewMouseEvent event, PreviewProperties properties, Workspace workspace) {
    }

    @Override
    public void mouseReleased(PreviewMouseEvent event, PreviewProperties properties, Workspace workspace) {
    }

    private boolean clickingInNode(Node node, PreviewMouseEvent event) {
        float xdiff = node.x() - event.x;
        float ydiff = -node.y() - event.y;//Note that y axis is inverse for node coordinates
        float radius = node.size();

        return xdiff * xdiff + ydiff * ydiff < radius * radius;
    }
}
