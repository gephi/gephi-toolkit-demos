/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.toolkit.demos;

import java.io.File;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 * Shows how to manipulate attributes. Attributes are the data associated to
 * nodes and edges. The AttributeAPI's role is to store the different columns
 * information.
 * <p>
 * The demo shows how to add columns and values to nodes and how to iterate.
 *
 * @author Mathieu Bastian
 */
public class ManipulateAttributes {

    public void script() {
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);

        //Import file
        Container container;
        try {
            File file = new File(getClass().getResource("/org/gephi/toolkit/demos/polblogs.gml").toURI());
            container = importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
            container.getLoader().setAllowAutoNode(false);  //Don't create missing nodes
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //List node columns
        GraphModel model = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        for (Column col : model.getNodeTable()) {
            System.out.println(col);
        }

        //Add boolean column
        Column testCol = model.getNodeTable().addColumn("test", Boolean.class);

        //Write values to nodes
        for (Node n : model.getGraph().getNodes()) {
            n.setAttribute(testCol, Boolean.TRUE);
        }

        //Iterate values - fastest
        Column sourceCol = model.getNodeTable().getColumn("source");
        for (Node n : model.getGraph().getNodes()) {
            System.out.println(n.getAttribute(sourceCol));
        }

        //Iterate values - normal
        for (Node n : model.getGraph().getNodes()) {
            System.out.println(n.getAttribute("source"));
        }
    }
}
