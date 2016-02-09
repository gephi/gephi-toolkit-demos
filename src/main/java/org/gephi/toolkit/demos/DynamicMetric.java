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

import java.util.Random;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Interval;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.types.TimestampDoubleMap;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.api.StatisticsController;
import org.gephi.statistics.plugin.dynamic.DynamicDegree;
import org.openide.util.Lookup;

/**
 * This demo main aim is to show how to execute a metric on a dynamic graph.
 * <p>
 * It does the following steps:
 * <ul>
 * <li>Create a project and a workspace, it is mandatory.</li>
 * <li>Generate a random graph into a container.</li>
 * <li>Append this container to the main graph structure.</li>
 * <li>Create a 'date' column (a simple INT between 1990 and 2010) and set
 * random values for each node.</li>
 * <li>Use the Data Laboratory merge strategy to convert this column to a real
 * time interval column.</li>
 * <li>Get a <code>DynamicGraph</code> and count the number of nodes for each
 * year. That shows how to get the subgraph for a particular period.</li>
 * <li>Put the result into a dynamic data structure, that shows how to store
 * result associated to a particular period.</li>
 * <li>Create a <code>InOutDegree</code> metric, execute it for each year and
 * collect the results in a dynamic data structure.</li>
 * </ul>
 * Instead of generating a random 'static' graph and add a fake date column, a
 * dynamic network can be imported with the GEXF file format.
 * <p>
 * The demo shows how to get a <code>DynamicGraph</code> instance, and get sub
 * graphs for a particular time period. Note that the dynamic graph maintains
 * only one sub graph at one time, so keeping multiple <code>Graph</code>
 * instances for further analysis won't work.
 * <p>
 * @author Mathieu Bastian
 */
public class DynamicMetric {

    public void script() {
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Generate a new random graph into a container
        Container container = Lookup.getDefault().lookup(Container.Factory.class).newContainer();
        RandomGraph randomGraph = new RandomGraph();
        randomGraph.setNumberOfNodes(500);
        randomGraph.setWiringProbability(0.005);
        randomGraph.generate(container.getLoader());

        //Append container to graph structure
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        importController.process(container, new DefaultProcessor(), workspace);

        //Add a fake 'Date' column to nodes
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

        //Add a random date to all nodes - between 1990 and 2010
        Graph graph = graphModel.getGraph();
        Random random = new Random();
        for (Node n : graph.getNodes()) {
            Integer randomDataValue = random.nextInt(21) + 1990;
            n.addInterval(new Interval(randomDataValue, Double.POSITIVE_INFINITY));
        }

        //Execute metric
        StatisticsController statisticsController = Lookup.getDefault().lookup(StatisticsController.class);
        DynamicDegree degree = new DynamicDegree();
        degree.setWindow(1.0);
        degree.setTick(1.0);
        degree.setBounds(graphModel.getTimeBounds());
        statisticsController.execute(degree);

        //Get averages
        System.out.println("Average degree:");
        TimestampDoubleMap averages = (TimestampDoubleMap) graph.getAttribute(DynamicDegree.DYNAMIC_AVGDEGREE);
        for (double t : averages.getTimestamps()) {
            System.out.println(t + " -> " + averages.getDouble(t));
        }
    }
}
