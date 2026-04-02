// LeetCode #1319

class NetworkConnected {
    private int[] parent;
    public int makeConnected(int n, int[][] connections) {
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        int unusedCables = 0;
        for(int[] connection : connections) {
            int node1 = connection[0];
            int node2 = connection[1];

            int parentOfNode1 = find(node1);
            int parentOfNode2 = find(node2);

            if (parentOfNode1 == parentOfNode2) {
                unusedCables++;
            } else {
                parent[parentOfNode1] = parentOfNode2;
                n--;
            }
        }
        int usedCables = n - 1;
        return usedCables > unusedCables ? -1 : usedCables;
    }

    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }
}
