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
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * Demo about manipulating several workspaces. The aim of this demo is to use
 * two workspaces, layout networks in parallel and finally export the workspaces
 * as PDF files.
 * <p>
 * Workspaces are the container for all data and are manipulated from the
 * Project Controller. The demo show key aspects:
 * <ul><li>How to duplicate a workspace.</li>
 * <li>How to set a workspace as the "current" workspace. This is very useful as
 * many modules just do their job on this current workspace and doesn't allow to
 * specify which workspace to work on from the API. Therefore it is often
 * required to set current workspace, as showed in export here.</li></ul>
 * <p>
 * This demo use Java Concurrent packages to run layout tasks in a thread pool.
 *
 * @author Mathieu Bastian
 */
public class ParallelWorkspace {

    public void script() {
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        final Workspace workspace1 = pc.getCurrentWorkspace();

        //Generate a new random graph into a container
        Container container = Lookup.getDefault().lookup(Container.Factory.class).newContainer();
        RandomGraph randomGraph = new RandomGraph();
        randomGraph.setNumberOfNodes(500);
        randomGraph.setWiringProbability(0.005);
        randomGraph.generate(container.getLoader());

        //Append container to graph structure
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        importController.process(container, new DefaultProcessor(), workspace1);

        //Duplicate this workspace
        final Workspace workspace2 = pc.duplicateWorkspace(workspace1);

        //Create Thread Pool for parallel layout
        ExecutorService executor = Executors.newFixedThreadPool(2);

        //Run Tasks and wait for termination in the current thread
        Future<?> f1 = executor.submit(createLayoutRunnable(workspace1));
        Future<?> f2 = executor.submit(createLayoutRunnable(workspace2));
        try {
            f1.get();
            f2.get();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        executor.shutdown();

        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            pc.openWorkspace(workspace1);
            ec.exportFile(new File("parallel_worspace1.pdf"));
            pc.openWorkspace(workspace2);
            ec.exportFile(new File("parallel_worspace2.pdf"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
    }

    private Runnable createLayoutRunnable(final Workspace workspace) {
        return new Runnable() {

            public void run() {
                GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
                AutoLayout autoLayout = new AutoLayout(10, TimeUnit.SECONDS);
                autoLayout.setGraphModel(gm);
                autoLayout.addLayout(new YifanHuLayout(null, new StepDisplacement(1f)), 1f);
                autoLayout.execute();
            }
        };
    }
}
