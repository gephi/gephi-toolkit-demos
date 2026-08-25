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

public class Main {

    public static void main(String[] args) {

        System.out.println("\n=== Running HeadlessSimple: import, filter, layout, statistics and PDF export ===");
        HeadlessSimple headlessSimple = new HeadlessSimple();
        headlessSimple.script();

        System.out.println("\n=== Running WithAutoLayout: automatic layouts and PDF export ===");
        WithAutoLayout autoLayout = new WithAutoLayout();
        autoLayout.script();

        System.out.println("\n=== Running ParallelWorkspace: parallel layouts in two workspaces ===");
        ParallelWorkspace parallelWorkspace = new ParallelWorkspace();
        parallelWorkspace.script();

        System.out.println("\n=== Running PartitionGraph: node partitions, colors and PDF exports ===");
        PartitionGraph partitionGraph = new PartitionGraph();
        partitionGraph.script();

        System.out.println("\n=== Running RankingGraph: ranking transformations and PDF export ===");
        RankingGraph rankingGraph = new RankingGraph();
        rankingGraph.script();

        System.out.println("\n=== Running Filtering: graph filtering examples ===");
        Filtering filtering = new Filtering();
        filtering.script();

        System.out.println("\n=== Running ImportExport: graph import and export examples ===");
        ImportExport importExport = new ImportExport();
        importExport.script();

        System.out.println("\n=== Running SQLiteImportExport: SQLite import, layout and update ===");
        SQLiteImportExport mYSQLImportExport = new SQLiteImportExport();
        mYSQLImportExport.script();

        System.out.println("\n=== Running ManualGraph: manual graph creation and manipulation ===");
        ManualGraph manualGraph = new ManualGraph();
        manualGraph.script();

        System.out.println("\n=== Running ManipulateAttributes: graph attribute manipulation ===");
        ManipulateAttributes manipulateAttributes = new ManipulateAttributes();
        manipulateAttributes.script();

        System.out.println("\n=== Running DynamicMetric: metrics on a dynamic graph ===");
        DynamicMetric longitudinalGraph = new DynamicMetric();
        longitudinalGraph.script();

        System.out.println("\n=== Running ImportDynamic: dynamic graph import ===");
        ImportDynamic importDynamic = new ImportDynamic();
        importDynamic.script();
    }
}
