import java.io.*;
import java.util.*;

class Result {
    static List<List<Integer>> tree;                 // Adjacency list of the tree
    static Set<String> uniqueHashes;                 // Stores unique subtree hash strings
    static Set<String> visitedSubgraphRoots;         // Avoids recomputing identical subgraphs

    public static int jennysSubtrees(int n, int r, List<List<Integer>> edges) {
        // Step 1: Build tree from edge list
        tree = new ArrayList<>();
        for (int i = 0; i <= n; i++) tree.add(new ArrayList<>());
        for (List<Integer> edge : edges) {
            int u = edge.get(0);
            int v = edge.get(1);
            tree.get(u).add(v);
            tree.get(v).add(u);
        }

        uniqueHashes = new HashSet<>();
        visitedSubgraphRoots = new HashSet<>();

        // Step 2: For each node, extract its radius-r subtree
        for (int i = 1; i <= n; i++) {
            String key = i + "_" + r;
            if (visitedSubgraphRoots.contains(key)) continue;
            visitedSubgraphRoots.add(key);

            Map<Integer, List<Integer>> subgraph = bfsSubtree(i, r);
            Set<Integer> nodes = subgraph.keySet();
            if (nodes.isEmpty()) continue;

            // Step 3: Find centroids of the subtree
            List<Integer> centroids = findCentroids(subgraph, nodes);

            // Step 4: Generate canonical string hash from centroid(s)
            String minHash = null;
            for (int c : centroids) {
                String hash = canonicalFormHash(c, -1, subgraph);
                if (minHash == null || hash.compareTo(minHash) < 0) {
                    minHash = hash;
                }
            }

            // Step 5: Track distinct subtree shapes
            uniqueHashes.add(minHash);
        }

        return uniqueHashes.size(); // Total distinct subtree structures of radius r
    }

    // BFS to collect all nodes within radius 'r' from 'start'
    private static Map<Integer, List<Integer>> bfsSubtree(int start, int radius) {
        Map<Integer, List<Integer>> subgraph = new HashMap<>();
        Queue<int[]> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.offer(new int[]{start, 0});
        visited.add(start);

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int node = current[0], dist = current[1];
            subgraph.putIfAbsent(node, new ArrayList<>());

            if (dist == radius) continue;

            for (int neighbor : tree.get(node)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(new int[]{neighbor, dist + 1});
                    subgraph.putIfAbsent(neighbor, new ArrayList<>());
                    subgraph.get(node).add(neighbor);
                    subgraph.get(neighbor).add(node);
                }
            }
        }
        return subgraph;
    }

    // Finds all centroids of a tree using DFS size tracking
    private static List<Integer> findCentroids(Map<Integer, List<Integer>> graph, Set<Integer> nodes) {
        int total = nodes.size();
        Map<Integer, Integer> size = new HashMap<>();
        List<Integer> centroids = new ArrayList<>();
        dfsSize(nodes.iterator().next(), -1, graph, size, centroids, total);
        return centroids;
    }

    // Calculates subtree sizes and detects centroids
    private static int dfsSize(int node, int parent, Map<Integer, List<Integer>> graph,
                               Map<Integer, Integer> size, List<Integer> centroids, int total) {
        int s = 1;
        boolean isCentroid = true;

        for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
            if (neighbor == parent) continue;
            int childSize = dfsSize(neighbor, node, graph, size, centroids, total);
            if (childSize > total / 2) isCentroid = false;
            s += childSize;
        }

        if (total - s > total / 2) isCentroid = false;
        if (isCentroid) centroids.add(node);
        size.put(node, s);
        return s;
    }

    // Recursively generate a canonical form string for the subtree
    private static String canonicalFormHash(int node, int parent, Map<Integer, List<Integer>> graph) {
        List<String> childHashes = new ArrayList<>();
        for (int child : graph.getOrDefault(node, Collections.emptyList())) {
            if (child != parent) {
                childHashes.add(canonicalFormHash(child, node, graph));
            }
        }
        Collections.sort(childHashes); // Ensures structure-only (not node value) comparison
        return "(" + String.join("", childHashes) + ")";
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String[] firstMultipleInput = bufferedReader.readLine().trim().split(" ");
        int n = Integer.parseInt(firstMultipleInput[0]);
        int r = Integer.parseInt(firstMultipleInput[1]);

        List<List<Integer>> edges = new ArrayList<>();
        for (int i = 0; i < n - 1; i++) {
            String[] edge = bufferedReader.readLine().trim().split(" ");
            edges.add(Arrays.asList(Integer.parseInt(edge[0]), Integer.parseInt(edge[1])));
        }

        int result = Result.jennysSubtrees(n, r, edges);
        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
