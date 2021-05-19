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

import com.itextpdf.text.PageSize;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PDFExporter;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import java.nio.file.Files;

/**
 * This class is used to take an acceptable input to the Gephi Toolkit and plot
 * it to a PDF.
 * <p>
 * Import can be performed from a file, database or Reader/InputStream. The
 * export should be done to a PDF file. There are several options that dictate
 * how the plot is done.
 * <p>
 * The ability to export graph or PDF to Writer or Byte array is showed at the
 * end.
 *
 * @author Henry Carscadden
 * @version 0.1
 * Credits to Mathieu Bastian for providing the code example that this is based
 * on.
 */
public class GraphPlot {
    private String inputPath, outputPath;
    private boolean directed, autoCreateNodes;
    
    public GraphPlot(String inputPath, String outputPath){
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        
        // Default to undirected and not auto-creating missing nodes.
        this.directed = false;
        this.autoCreateNodes = false;
    }
    
    
    public GraphPlot(String inputPath, String outputPath, boolean directed,
            boolean autoCreateNodes){
        
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.directed = this.directed;
        this.autoCreateNodes = this.autoCreateNodes;
    }
    
    
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
            File file = new File(getClass().getResource(this.inputPath).toURI());
            container = importController.importFile(file);
            if (this.directed){
                container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
            }
            else{
                container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);
            }
            container.getLoader().setAllowAutoNode(this.autoCreateNodes);  // Create missing nodes according to the input arguments.
            //Append imported data to GraphAPI
            importController.process(container, new DefaultProcessor(), workspace);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Reading input graph file " + this.inputPath + " failed.");
            System.exit(1);
        }

        
        //Export full graph
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);

        //PDF Exporter config and export to Byte array
        PDFExporter pdfExporter = (PDFExporter) ec.getExporter("pdf");
        pdfExporter.setPageSize(PageSize.A0);
        pdfExporter.setWorkspace(workspace);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ec.exportStream(baos, pdfExporter);
        byte[] pdf = baos.toByteArray();
        
        // Write byte-array to the specified PDF file.
        try{
        File outputFile = new File(this.outputPath);
        Files.write(outputFile.toPath(), pdf);
        }
        catch (IOException ex){
            ex.printStackTrace();
            System.out.println("Writing PDF plot to " + this.outputPath + " failed.");
            System.exit(1);
        }
        
    }
}
