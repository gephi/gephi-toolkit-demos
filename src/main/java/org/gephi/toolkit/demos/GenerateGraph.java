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

import org.gephi.io.generator.plugin.DynamicGraph;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerUnloader;
import org.gephi.io.importer.api.ColumnDraft;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.AppendProcessor;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.graph.api.Configuration;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.TimeRepresentation;
import org.gephi.graph.api.types.IntervalDoubleMap;
import org.gephi.graph.api.types.TimestampDoubleMap;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 * Demo how to generate a graph with generators.
 * <p>
 * The code shows how to use <code>RandomGraph</code> and
 * <code>WattsStrogatz</code> generators and push result into the graph
 * structure using <code>ImportController</code>.
 * <p>
 * In Gephi import and generate are not directly appened to the main graph
 * structure for consistency reasons. New data are pushed in a
 * <code>Container</code> and then appened to the graph structure with the help
 * of a <code>Processor</code>.
 * <p>
 * In this demo, two workspaces are created. Manipulate workspaces from
 * <code>ProjectController</code>
 *
 * @author Mathieu Bastian
 */
public class GenerateGraph {

    public void script() {
        //Generate a new random graph into a container
        Container container = Lookup.getDefault().lookup(Container.Factory.class).newContainer();
        RandomGraph randomGraph = new RandomGraph();
        randomGraph.setNumberOfNodes(500);
        randomGraph.setWiringProbability(0.005);
        randomGraph.generate(container.getLoader());

        //Create a correctly configured project/workspace and process the graph
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        Workspace workspace = importController.process(container);

        //Generate another graph and append it to the current workspace
        Container container2 = Lookup.getDefault().lookup(Container.Factory.class).newContainer();
        RandomGraph randomGraph2 = new RandomGraph();
        randomGraph2.setNumberOfNodes(100);
        randomGraph2.setWiringProbability(0.01);
        randomGraph2.generate(container2.getLoader());
        importController.process(container2, new AppendProcessor(), workspace);     //Use AppendProcessor to append to current workspace

        //Generate dynamic graph into workspace 2
        Container container3 = Lookup.getDefault().lookup(Container.Factory.class).newContainer();

        DynamicGraph dynamicGraph = new DynamicGraph();
        dynamicGraph.generate(container3.getLoader());

        //Create the second workspace with its immutable temporal configuration
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        ContainerUnloader unloader = container3.getUnloader();
        Class<?> elementIdType = unloader.getElementIdType().getTypeClass();
        Configuration.Builder configurationBuilder = graphController.getDefaultConfigurationBuilder()
                .timeRepresentation(unloader.getTimeRepresentation())
                .nodeIdType(elementIdType)
                .edgeIdType(elementIdType);
        if (unloader.getEdgeTypeLabelClass() != null) {
            configurationBuilder.edgeLabelType(unloader.getEdgeTypeLabelClass());
        }
        ColumnDraft weightColumn = unloader.getEdgeColumn("weight");
        if (weightColumn != null && weightColumn.isDynamic()) {
            configurationBuilder.edgeWeightType(unloader.getTimeRepresentation() == TimeRepresentation.INTERVAL
                    ? IntervalDoubleMap.class : TimestampDoubleMap.class);
        }
        Configuration configuration = configurationBuilder.build();
        Workspace workspace2 = pc.openNewWorkspace(configuration);
        importController.process(container3, new DefaultProcessor(), workspace2);
    }
}
