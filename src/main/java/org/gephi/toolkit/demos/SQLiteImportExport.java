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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.database.drivers.SQLUtils;
import org.gephi.io.database.drivers.SQLiteDriver;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.plugin.database.EdgeListDatabaseImpl;
import org.gephi.io.importer.plugin.database.ImporterEdgeList;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 * This demo shows how to import data from a SQLite database. The database
 * format must be "Edge List", basically a table for nodes and a table for
 * edges.
 * <p>
 * To be found by the importer, you need to have following columns:
 * <ul><li><b>Nodes:</b> ID and LABEL</li>
 * <li><b>Edges:</b> SOURCE, TARGET and WEIGHT</li></ul>
 * Any other column will be imported as attributes. Other recognized columns are
 * X, Y and SIZE for nodes and ID and LABEL for edges.
 * <p>
 * A possible toolkit use-case is a layout server. Therefore this demo layout
 * the network imported from the database, layout it and update X, Y columns to
 * the database.
 *
 * @author Mathieu Bastian
 */
public class SQLiteImportExport {

    public void script() {
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

        //Copy example database to tmp
        File temp;
        try {
            File file = new File(getClass().getResource("/org/gephi/toolkit/demos/lesmiserables.sqlite3").toURI());
            temp = new File(System.getProperty("java.io.tmpdir"));
            FileUtil.copyFile(FileUtil.toFileObject(file), FileUtil.toFileObject(temp), "lesmiserables");
            temp = new File(temp, "lesmiserables.sqlite3");
            temp.deleteOnExit();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        temp.deleteOnExit();

        //Import database
        EdgeListDatabaseImpl db = new EdgeListDatabaseImpl();
        db.setHost(temp.getAbsolutePath());
        db.setDBName("");
        db.setSQLDriver(new SQLiteDriver());
        db.setNodeQuery("SELECT nodes.id AS id, nodes.label AS label FROM nodes");
        db.setEdgeQuery("SELECT edges.source AS source, edges.target AS target, edges.label AS label, edges.weight AS weight FROM edges");
        ImporterEdgeList edgeListImporter = new ImporterEdgeList();
        Container container = importController.importDatabase(db, edgeListImporter);
        container.getLoader().setAllowAutoNode(false);      //Don't create missing nodes
        container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);   //Force UNDIRECTED

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //See if graph is well imported
        UndirectedGraph graph = graphModel.getUndirectedGraph();
        System.out.println("Nodes: " + graph.getNodeCount());
        System.out.println("Edges: " + graph.getEdgeCount());

        //Layout - 100 Yifan Hu passes
        YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
        layout.setGraphModel(graphModel);
        layout.resetPropertiesValues();
        layout.initAlgo();
        for (int i = 0; i < 100 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }
        layout.endAlgo();

        //Export X, Y position to the DB
        //Connect database
        String url = SQLUtils.getUrl(db.getSQLDriver(), db.getHost(), db.getPort(), db.getDBName());
        Connection connection = null;
        try {
            System.err.println("Try to connect at " + url);
            connection = db.getSQLDriver().getConnection(url, db.getUsername(), db.getPasswd());
            //System.err.println("Database connection established");
        } catch (SQLException ex) {
            if (connection != null) {
                try {
                    connection.close();
                    System.err.println("Database connection terminated");
                } catch (Exception e) {
                    /* ignore close errors */ }
            }
            System.err.println("Failed to connect at " + url);
            ex.printStackTrace(System.err);
        }
        if (connection == null) {
            System.err.println("Failed to connect at " + url);
        }

        //Update
        int count = 0;
        for (Node node : graph.getNodes().toArray()) {
            String id = node.getId().toString();
            float x = node.x();
            float y = node.y();

            String query = "UPDATE nodes SET x = '" + x + "', y = '" + y + "' WHERE nodes.id='" + id + "'";
            try {
                Statement s = connection.createStatement();
                count += s.executeUpdate(query);
                s.close();

            } catch (SQLException e) {
                System.err.println("Failed to update line node id = " + id);
                e.printStackTrace();
            }
        }
        System.err.println(count + " rows were updated");

        //Close connection
        if (connection != null) {
            try {
                connection.close();
                //System.err.println("Database connection terminated");
            } catch (Exception e) {
                /* ignore close errors */ }
        }
    }
}
