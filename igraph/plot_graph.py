__author__ = "Henry Carscadden"
__contact__ = "hlc5v@virginia.edu"

import numpy as np
import traceback as tb
import igraph
import warnings
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("--input_path", required=True, type=str, help="Path to input file.")
parser.add_argument("--output_path", required=True, type=str, help="Path to output file.")

# All of the below are optional CLAs
parser.add_argument("-algo", "--layout_algorithm", required=False, default="auto", type=str, help="The layout algorithm"
                                                                                                  "that iGraph should "
                                                                                                  "use.")
parser.add_argument('--contract', required=False, type=bool, help="If this flag is provided, the script will "
                                                                  "attempt"
                                                                  "to contract nodes into their clusters. "
                                                                  "Recommended for larger graphs ~100k+.")
parser.add_argument("--color", required=False, type=str, help="This CLA may have three types of values. 1."
                                                              "comm_coloring - This colors the nodes by their "
                                                              "community. "
                                                              '2. An igraph supported color. One of: "red", "blue", '
                                                              '"black", "brown", "green", "orange", "yellow", '
                                                              '"magenta", "lime", "indigo", "cyan"' 
                                                              "3. A custom coloring scheme. - Not available yet.")
parser.add_argument("--output_width", required=False, default=2000,
                    type=int, help="Specify the output width in pixels.")
parser.add_argument("--output_height", required=False, default=1000,
                    type=int, help="Specify the output height in pixels.")
parser.add_argument("--scale", required=False, type=str, help="If this flag is provided, the script will scale"
                                                              "the vertices by proportionally to their degree.")
parser.add_argument("--drop_isolates", required=False, type=bool, help="If this flag is provided, the script will drop"
                                                                       "isolates from the graph plot.")


def main():
    args = parser.parse_args()
    # Path Arguments
    input_path = args.input_path
    output_path = args.output_path
    # Modifications to the plot
    contract = args.contract
    color = args.color
    scale = args.scale
    drop_isolates = args.drop_isolates
    # Set the output size
    output_width, output_height = int(args.output_width), int(args.output_height)
    # Layout algorithm
    layout_algorithm = args.layout_algorithm

    colors = ["red", "blue", "black", "brown", "green", "orange", "yellow", "magenta", "lime", "indigo", "cyan"]

    # Attempt to load the graph
    try:
        G = igraph.Graph.Load(input_path)
    except Exception as e:
        tb.print_exc()
        print(e)
        print("Failed to load the graph from: " + input_path)
        exit(1)

    visual_style = {}

    # Compute the total number of pixels in the output plot
    total_pixels = output_width * output_height
    # Scale the vertex and arrow size based on the number of output pixels
    vertex_size = min(total_pixels / ((G.vcount() * 6) + 1), 15)
    arrow_size = .5
    arrow_width = .5
    visual_style["edge_arrow_size"] = arrow_size
    visual_style["edge_arrow_width"] = arrow_width

    # Drop isolates if requested.
    if drop_isolates:
        G.delete_vertices(G.vs.select(_degree=0))

    if contract:
        warnings.warn(UserWarning("Contract will convert the graph to undirected."))
        # TODO: These are coming the next version. Do not worry Lucas.
        # TODO: Provide a more sophisticated contraction logic.
        G.to_undirected()

        best_cluster = G.community_multilevel()

        if scale != "degree":
            old_G = G.copy()

        G = best_cluster.cluster_graph()

        if color == "comm_coloring":
            G.vs['color'] = np.random.choice(colors, size=(G.vcount(),), replace=True)

    if color == "comm_coloring" and not contract:
        warnings.warn(UserWarning("Contract will convert the graph to undirected."))
        G.to_undirected()
        best_cluster = G.community_multilevel()
        pal = igraph.drawing.colors.ClusterColoringPalette(len(best_cluster))
        G.vs['color'] = pal.get_many(best_cluster.membership)
    elif color in colors:
        G.vs["color"] = color

    deg = G.degree()
    layout = G.layout(layout_algorithm)

    # Scale based on node degree if requested.
    if scale:
        if scale != "degree":
            if scale == "comm_degree":
                # TODO: This should be only using inter-cluster edges in degree.
                sizes = np.fromiter(
                    (sum([sum(1 for neighbor in old_G.vs[node].neighbors() if neighbor not in best_cluster[comm.index])
                          for node in best_cluster[comm.index]]) for comm in
                     G.vs),
                    dtype=float)
                sizes = ((sizes - np.mean(sizes)) / (1 + (np.std(sizes) * 2)) * vertex_size) + vertex_size
                G.vs["size"] = sizes
            elif scale == "comm_size":
                sizes = best_cluster.sizes()
                G.vs["size"] = (((sizes - np.mean(sizes)) / ((2 * np.std(sizes)) + 1)) * vertex_size) + vertex_size
            else:
                raise Exception(scale + " is not a valid scaling style.")
        else:
            G.vs["size"] = (((deg - np.mean(deg)) / ((2 * np.std(deg)) + 1)) * vertex_size) + vertex_size
    else:
        visual_style["vertex_size"] = vertex_size

    # Plot the graph with the requested settings and layout.
    igraph.plot(G, layout=layout,
                bbox=(output_width, output_height),
                target=output_path, **visual_style)


if __name__ == '__main__':
    main()
