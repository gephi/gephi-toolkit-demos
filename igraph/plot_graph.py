import numpy as np

import igraph
import warnings
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("--input_path", required=True, type=str, help="Path to input file.")
parser.add_argument("--output_path", required=True, type=str, help="Path to output file.")
parser.add_argument("-algo", "--layout_algorithm", required=False, default="auto", type=str, help="The layout algorithm"
                                                                                                  "that iGraph should "
                                                                                                  "use.")
parser.add_argument('--contract', required=False, type=bool, help="If this flag is provided, the script will "
                                                                  "attempt"
                                                                  "to contract nodes into their clusters. "
                                                                  "Recommended for larger graphs ~100k+.")
parser.add_argument("--color", required=False, type=bool, help="If this flag is provided, the script will try different"
                                                               "clustering, then, color the nodes according to "
                                                               "cluster.")


def main():
    args = parser.parse_args()
    input_path = args.input_path
    output_path = args.output_path
    contract = args.contract
    color = args.color

    layout_algorithm = args.layout_algorithm

    G = igraph.Graph.Load(input_path)
    if contract:
        warnings.warn(UserWarning("Contract will convert the graph to undirected."))
        # TODO: Provide a more sophisticated contraction logic.
        G.to_undirected()
        # G = G.community_leiden().cluster_graph()
        cluster = G.community_multilevel()
        if cluster.modularity < .25:
            cluster = G.community_leiden()
        G = cluster.cluster_graph()

    if color:
        warnings.warn(UserWarning("Contract will convert the graph to undirected."))
        G.to_undirected()
        # G = G.community_leiden().cluster_graph()
        cluster = G.community_multilevel()
        if cluster.modularity < .25:
            cluster = G.community_leiden()

        pal = igraph.drawing.colors.ClusterColoringPalette(len(cluster))
        G.vs['color'] = pal.get_many(cluster.membership)

    deg = G.degree()
    layout = G.layout(layout_algorithm)
    pixel_width, pixel_length = 2000, 1000
    total_pixels = pixel_width * pixel_length
    vertex_size = min(total_pixels / ((G.vcount() * 5) + 1), 10)
    arrow_size = min(total_pixels / ((G.ecount() * 45) + 1), 10)
    vertex_size = (((deg - np.mean(deg)) / ((2 * np.std(deg)) + 1)) * vertex_size) + vertex_size

    igraph.plot(G, layout=layout, vertex_size=vertex_size, edge_arrow_size=arrow_size,
                bbox=(pixel_width, pixel_length),
                target=output_path)


if __name__ == '__main__':
    main()
