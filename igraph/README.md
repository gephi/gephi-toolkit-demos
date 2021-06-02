### Dependencies 
Please use conda on the requirements.txt file.

### Required Arguments

* Input path: The path an input file this should be one accepted by the following readers:

    * Read_DL - igraph._igraph.GraphBase.Read_DL
    * Read_Edgelist - igraph._igraph.GraphBase.Read_Edgelist
    * Read_GML - igraph._igraph.GraphBase.Read_GML
    * Read_GraphDB - igraph._igraph.GraphBase.Read_GraphDB
    * Read_GraphML - igraph._igraph.GraphBase.Read_GraphML
    * Read_GraphMLz - igraph.Graph.Read_GraphMLz
    * Read_Lgl - igraph._igraph.GraphBase.Read_Lgl
    * Read_Ncol - igraph._igraph.GraphBase.Read_Ncol
    * Read_Pajek - igraph._igraph.GraphBase.Read_Pajek
    * Read_Pickle - igraph.Graph.Read_Pickle
    * Read_Picklez - igraph.Graph.Read_Picklez
    * Read_Picklez 0 - igraph.Graph.Read_Picklez 0
    
* Output path: The path to the output plot. The file suffix will determine the type automatically. PDF is the recommended ending.


### Optional Arguments

* algo - This sets the layout algorithm. Selection should come from the next section.

* output width - Sets the output width in pixels.

* output height - Sets the output height in pixels.

* scale - A string that allows you to choose how to scale the nodes if at all.
  * degree - Scales nodes by their degree in the graph. If nodes are contracted, the degree
  of the nodes in the contracted graph is used.
  * Community only methods
    * comm_degree - Scales nodes in a contracted graph by the sum of the degree of the node's members in the original graph. 
    If not clustering is performed, this is an error.
    * comm_size - This is for contracted graphs only. This scales nodes by the size of their community.

#### Boolean Switches

* contract - This will contract nodes into their communities as determined by the multilevel communities algorithm (Louvain-based).

* color - This will color nodes according to their communities as determined by the multilevel communities algorithm (Louvain-based). This will be run after nodes are contracted if contract is set.

* drop_isolates - This removes isolates. It will be run prior to any clustering.

### Layout algorithm options:

layout_circle
	

circle, circular
	

Deterministic layout that places the vertices on a circle
----------	
layout_drl


drl
	

The Distributed Recursive Layout algorithm for large graphs
----------	
layout_fruchterman_reingold
	

fr
	

Fruchterman-Reingold force-directed algorithm
----------	
layout_fruchterman_reingold_3d
	

fr3d, fr_3d
	

Fruchterman-Reingold force-directed algorithm in three dimensions
----------	
layout_grid_fruchterman_reingold
	

grid_fr
	

Fruchterman-Reingold force-directed algorithm with grid heuristics for large graphs
----------	
layout_kamada_kawai
	

kk
	

Kamada-Kawai force-directed algorithm
----------	
layout_kamada_kawai_3d
	

kk3d, kk_3d
	

Kamada-Kawai force-directed algorithm in three dimensions
----------	
layout_lgl
	

large, lgl, large_graph
	

The Large Graph Layout algorithm for large graphs
----------	
layout_random
	

random
	

Places the vertices completely randomly
----------	
layout_random_3d
	

random_3d
	

Places the vertices completely randomly in 3D
----------	
layout_reingold_tilford
	

rt, tree
	

Reingold-Tilford tree layout, useful for (almost) tree-like graphs
----------	
layout_reingold_tilford_circular
	

rt_circular

tree
	

Reingold-Tilford tree layout with a polar coordinate post-transformation, useful for (almost) tree-like graphs
----------	
layout_sphere
	

sphere, spherical, circular_3d
	

Deterministic layout that places the vertices evenly on the surface of a sphere
----------	