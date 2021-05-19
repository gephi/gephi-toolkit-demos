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
        /*
        This class performs the actual import/export.
        It requires the path to the input file.
        It requires the path to export the PDF image to.
        It requires an argument to determine the directedness.
        It requires an argument on whether or not to fill in missing
        nodes.
        */
        GraphPlot graphPlot = new GraphPlot("/org/gephi/toolkit/demos/lesmiserables.gml",
         "test.pdf");
        graphPlot.script();

    }
}
