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

import org.gephi.graph.api.Graph;
import org.gephi.preview.api.Item;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.spi.ItemBuilder;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ItemBuilder.class)
public class ItemBuilderTemplate implements ItemBuilder {

    @Override
    public Item[] getItems(Graph graph) {
//        Workspace workspace = graphModel.getWorkspace();
        PreviewProperties properties = Lookup.getDefault().lookup(PreviewController.class).getModel().getProperties();

        if (properties.hasProperty("display-label.node.id")) {
            String nodeId = properties.getStringValue("display-label.node.id");
            return new Item[]{new LabelItem(graph.getNode(nodeId))};
        } else {
            return new Item[0];
        }
    }

    @Override
    public String getType() {
        return "some.type-label";
    }
}
