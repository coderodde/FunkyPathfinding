package net.coderodde.funky.pathfinding;

import java.util.Collection;

public interface NodeExpander<N> {

    public Collection<N> expand(N node);
}
