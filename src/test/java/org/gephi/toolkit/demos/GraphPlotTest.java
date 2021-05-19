/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.toolkit.demos;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author henry
 */
public class GraphPlotTest extends TestCase{

    
    public GraphPlotTest() {
    }
    
    public void testJavaGexf(){
        GraphPlot graphPlot = new GraphPlot("/org/gephi/toolkit/demos/lesmiserables.gml",
        "test_1.pdf");  
    }
    

    
}
