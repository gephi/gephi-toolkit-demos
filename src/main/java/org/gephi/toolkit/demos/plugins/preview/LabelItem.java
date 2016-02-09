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

import org.gephi.graph.api.Node;
import org.gephi.preview.api.Item;

/**
 * Basic item without properties but a node.
 */
public class LabelItem implements Item {

    Node node;

    public LabelItem(Node node) {
        this.node = node;
    }

    @Override
    public Object getSource() {
        return node;
    }

    @Override
    public String getType() {
        return "label.sometype";
    }

    @Override
    public <D> D getData(String key) {
        return null;
    }

    @Override
    public void setData(String key, Object value) {
    }

    @Override
    public String[] getKeys() {
        return new String[0];
    }

}
