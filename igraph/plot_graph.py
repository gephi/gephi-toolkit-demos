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
parser.add_argument('--contract', dest="contract",action='store_true', default=False, required=False,  help="If this flag is provided, the script will "
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
parser.add_argument("--cluster", required=False,
                    type=str, nargs="*", default=["community_multilevel"], help="This takes a list of graph "
                                                                                "clustering algorithm names. "
                                                                                "They should be selected from: "
                                                                                "components the connected "
                                                                                "components, "
                                                                                "cohesive_blocks, "
                                                                                "community_edge_betweenness, "
                                                                                "community_fastgreedy, "
                                                                                "community_infomap, "
                                                                                "community_label_propagation, "
                                                                                "community_leading_eigenvector, "
                                                                                "community_leading_eigenvector_naive, "
                                                                                "community_leiden, "
                                                                                "community_multilevel "
                                                                                "(a version of Louvain), "
                                                                                "community_optimal_modularity "
                                                                                "(exact solution, < 100 "
                                                                                "vertices), "
                                                                                "community_spinglass, "
                                                                                "community_walktrap. "
                                                                                "If none is selected "
                                                                                " community_multilevel is used."
                    )
parser.add_argument("--output_width", required=False, default=2000,
                    type=int, help="Specify the output width in pixels.")
parser.add_argument("--output_height", required=False, default=1000,
                    type=int, help="Specify the output height in pixels.")
parser.add_argument("--scale", required=False, type=str, help="If this flag is provided, the script will scale"
                                                              "the vertices by proportionally to their degree.")
parser.add_argument("--drop_isolates", action='store_true',dest="drop_isolates", required=False,default=False, help="If this flag is provided, the script will drop"
                                                                       "isolates from the graph plot.")


def cluster(G, algo_str):
    """
    This is a helper function to compute clusters using user-selected algorithms.
    :param G: The input graph.
    :param algo_str: A string saying what clustering algorithm to use.
    :return: A clustering object.
    """

    if algo_str == "components":
        return G.components()
    elif algo_str == "cohesive_blocks":
        return G.cohesive_blocks()
    elif algo_str == "community_edge_betweenness":
        return G.community_edge_betweenness()
    elif algo_str == "community_fastgreedy":
        return G.community_fastgreedy()
    elif algo_str == "community_infomap":
        return G.community_infomap()
    elif algo_str == "community_label_propagation":
        return G.community_label_propagation()
    elif algo_str == "community_leading_eigenvector":
        return G.community_leading_eigenvector()
    elif algo_str == "community_leading_eigenvector_naive":
        return G.community_leading_eigenvector_naive()
    elif algo_str == "community_leiden":
        return G.community_leiden()
    elif algo_str == "community_multilevel":
        return G.community_multilevel()
    elif algo_str == "community_optimal_modularity":
        return G.community_optimal_modularity()
    elif algo_str == "community_spinglass":
        return G.community_spinglass()
    elif algo_str == "community_walktrap":
        return G.community_walktrap()
    else:
        print(algo_str)
        raise ValueError("Invalid clustering algorithm name.")


output_formats = ["pdf", "png", "svg", "ps", "eps"]
input_formats = ["lgl", "adjacency", "dimacs", "dl", "edgelist", "edges", "edge", "graphviz", "dot", "gml", "graphml",
                 "graphmlz", "leda", "ncol", "pajek", "net", "pickle"]


def main():
    args = parser.parse_args()
    # Path Arguments
    input_path = args.input_path
    # Check if the format is valid.
    if input_path.split(".")[-1].lower() not in input_formats:
        print("The input file extension is " + input_path.split(".")[-1].lower() + " is not a supported input format.")
        raise TypeError("The input file extension should be one of: " + str(input_formats))

    # Check if the format is valid.
    output_path = args.output_path
    if output_path.split(".")[-1].lower() not in output_formats:
        print(
            "The input file extension is " + output_path.split(".")[-1].lower() + " is not a supported output format.")
        raise TypeError("The output file extension should be one of: " + str(output_formats))

    # Modifications to the plot
    contract = args.contract
    color = args.color
    scale = args.scale
    drop_isolates = args.drop_isolates

    print(f"Contract: {contract} Drop Isolates: {drop_isolates}")
    # Set the output size
    output_width, output_height = int(args.output_width), int(args.output_height)
    # Layout algorithm
    layout_algorithm = args.layout_algorithm
    # The clustering algorithm to try.
    clusterings = args.cluster

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

    if color or contract:
        G.to_undirected()
        warnings.warn(UserWarning("Clustering will convert the graph to undirected."))

        best_cluster = None
        best_score = 0
        for clustering in clusterings:
            if best_cluster is None:
                best_cluster = cluster(G, clustering)
                best_score = float(best_cluster.modularity)
            else:
                curr_cluster = cluster(G, clustering)
                curr_score = float(curr_cluster.modularity)
                if best_score < float(curr_score):
                    print(best_score < curr_score)
                    print(clustering)
                    print(curr_cluster.summary())
                    best_cluster = curr_cluster
                    best_score = curr_score

    if contract:

        if scale != "degree":
            old_G = G.copy()

        G = best_cluster.cluster_graph()

        if color == "comm_coloring":
            G.vs['color'] = np.random.choice(colors, size=(G.vcount(),), replace=True)

    if color == "comm_coloring" and not contract:
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
