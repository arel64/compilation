package IR;

import java.util.*;
import TEMP.*;
import MIPS.MIPSGenerator;

public class InterferenceGraph {
    private Map<TEMP, Set<TEMP>> graph = new HashMap<>();
    
    public void addEdge(TEMP t1, TEMP t2) {
        // DEBUG: Check for null arguments
        if (t1 == null || t2 == null) {
            
            System.err.printf("addEdge called with null TEMP! t1=%s, t2=%s\n", t1, t2);
            throw new RuntimeException("addEdge called with null TEMP! t1=" + t1 + ", t2=" + t2);

        }
        
        if (!t1.equals(t2)) {
            graph.computeIfAbsent(t1, k -> new HashSet<>()).add(t2);
            graph.computeIfAbsent(t2, k -> new HashSet<>()).add(t1);
        }
    }
    
    public Map<TEMP, Integer> colorGraph(Set<TEMP> allTemps) {
        System.out.println("Starting graph coloring with " + graph.size() + " nodes involved in interference.");
        List<TEMP> stack = new ArrayList<>();
        Set<TEMP> removed = new HashSet<>();
        Map<TEMP, Integer> colors = new HashMap<>();
        
        // Remove nodes with degree < K (K = 8 registers now)
        while (removed.size() < graph.size()) {
            boolean found = false;
            for (TEMP temp : graph.keySet()) {
                if (!removed.contains(temp)) {
                    Set<TEMP> neighbors = graph.get(temp);
                    neighbors.removeAll(removed);
                    if (neighbors.size() < MIPSGenerator.NUM_ALLOCATABLE_REGISTERS) {
                        stack.add(temp);
                        removed.add(temp);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                // Graph is not 8-colorable, need to spill
                TEMP spillNode = graph.keySet().stream()
                    .filter(t -> !removed.contains(t))
                    .max(Comparator.comparingInt(t -> (int) graph.get(t).stream().filter(n -> !removed.contains(n)).count()))
                    .get();

                TEMP n1 = TEMP_FACTORY.getInstance().getFreshTEMP();
                TEMP n2 = TEMP_FACTORY.getInstance().getFreshTEMP();

                int index = 0;
                int size = graph.get(spillNode).size() / 2;
                HashSet<TEMP> n1Neighbors = new HashSet<>();
                HashSet<TEMP> n2Neighbors = new HashSet<>();
                for (TEMP item : graph.get(spillNode)) {
                    if (index < size) {
                        n1Neighbors.add(item);
                    } else {
                        n2Neighbors.add(item);
                    }
                    index++;
                }

                for (TEMP t : n1Neighbors) {
                    graph.get(t).add(n1);
                    graph.get(t).remove(spillNode);
                }
                for (TEMP t : n2Neighbors) {
                    graph.get(t).add(n2);
                    graph.get(t).remove(spillNode);
                }

                graph.put(n1, n1Neighbors);
                graph.put(n2, n2Neighbors);
                graph.remove(spillNode);

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
            
            // Find smallest available color (0 to K-1)
            int color = 0;
            while (usedColors.contains(color) || color >= MIPSGenerator.NUM_ALLOCATABLE_REGISTERS) { // Ensure color is < K
                 color++;
            }
            // Check if a valid color was found
            if (color >= MIPSGenerator.NUM_ALLOCATABLE_REGISTERS) {
                 System.err.println("ERROR: Failed to assign a color < " + MIPSGenerator.NUM_ALLOCATABLE_REGISTERS + " to TEMP " + temp + ". Max color should be " + (MIPSGenerator.NUM_ALLOCATABLE_REGISTERS - 1) + ".");
                 // Handle error appropriately - maybe throw exception or assign a default/spill?
                 // For now, let's throw an exception, as this indicates a logic error in simplification/spilling
                 throw new RuntimeException("Register allocation failed: Could not find color < " + MIPSGenerator.NUM_ALLOCATABLE_REGISTERS + " for TEMP " + temp);
             }
            colors.put(temp, color);
        }
        
        // Assign a default color (0) to any TEMPs that exist but had no interferences
        int defaultColor = 0; // Corresponds to $t0 in current MIPSGenerator
        for (TEMP temp : allTemps) {
            if (!colors.containsKey(temp)) {
                // This TEMP exists but wasn't involved in interference or spilling
                // Assign it the default color.
                colors.put(temp, defaultColor);
                System.out.println("Assigning default color " + defaultColor + " to non-interfering TEMP: " + temp);
            }
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
            if (entry == null || entry.getKey() == null || entry.getValue() == null || graph == null)
            {
                System.out.println("entry is" + entry  );
            }
            sb.append(String.format("TEMP_%d interferes with: ", entry.getKey().getSerialNumber()));
            for (TEMP temp : entry.getValue()) {
                sb.append(String.format("TEMP_%d ", temp.getSerialNumber()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
} 