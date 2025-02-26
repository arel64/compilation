package IR;

import java.util.*;
import TEMP.*;

public class InterferenceGraph {
    private Map<TEMP, Set<TEMP>> graph = new HashMap<>();
    
    public void addEdge(TEMP t1, TEMP t2) {
        graph.computeIfAbsent(t1, k -> new HashSet<>()).add(t2);
        graph.computeIfAbsent(t2, k -> new HashSet<>()).add(t1);
    }
    
    public Map<TEMP, Integer> colorGraph() {
        System.out.println("Starting graph coloring with " + graph.size() + " nodes");
        List<TEMP> stack = new ArrayList<>();
        Set<TEMP> removed = new HashSet<>();
        Map<TEMP, Integer> colors = new HashMap<>();
        
        // If graph is empty, return empty mapping
        if (graph.isEmpty()) {
            System.out.println("Empty graph, no coloring needed");
            return colors;
        }
        
        // Remove nodes with degree < K (K = 10 registers)
        while (removed.size() < graph.size()) {
            boolean found = false;
            for (TEMP temp : graph.keySet()) {
                if (!removed.contains(temp)) {
                    Set<TEMP> neighbors = graph.get(temp);
                    neighbors.removeAll(removed);
                    if (neighbors.size() < 10) {
                        stack.add(temp);
                        removed.add(temp);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                // Graph is not 10-colorable, need to spill
                TEMP spillNode = graph.keySet().stream()
                    .filter(t -> !removed.contains(t))
                    .findFirst()
                    .get();
                stack.add(spillNode);
                removed.add(spillNode);
            }
        }
        
        // Assign colors
        while (!stack.isEmpty()) {
            TEMP temp = stack.remove(stack.size() - 1);
            Set<Integer> usedColors = new HashSet<>();
            for (TEMP neighbor : graph.get(temp)) {
                if (colors.containsKey(neighbor)) {
                    usedColors.add(colors.get(neighbor));
                }
            }
            
            // Find smallest available color
            int color = 0;
            while (usedColors.contains(color)) color++;
            colors.put(temp, color);
        }
        
        System.out.println("Final coloring: " + colors.size() + " temps assigned colors");
        return colors;
    }

    public int getNodeCount() {
        return graph.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Interference Graph:\n");
        for (Map.Entry<TEMP, Set<TEMP>> entry : graph.entrySet()) {
            sb.append(String.format("TEMP_%d interferes with: ", entry.getKey().getSerialNumber()));
            for (TEMP temp : entry.getValue()) {
                sb.append(String.format("TEMP_%d ", temp.getSerialNumber()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
} 