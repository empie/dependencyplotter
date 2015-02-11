package net.empuly.dependencyenforcer.maven.plugin.dependencyenforcer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.apache.maven.artifact.Artifact;

import com.google.common.base.Predicate;

public class ArtifactHeeftNaamDieStartMetEenVanDeNamenPredicate implements Predicate<Artifact> {
    
    private final Set<MinimalMavenDependencyDescription> listOfDependencyNamesToIncludeInGraph;
    
    public ArtifactHeeftNaamDieStartMetEenVanDeNamenPredicate(
            Set<MinimalMavenDependencyDescription> listOfDependencyNamesToIncludeInGraph) {
        checkNotNull(listOfDependencyNamesToIncludeInGraph);
        this.listOfDependencyNamesToIncludeInGraph = listOfDependencyNamesToIncludeInGraph;
    }
    
    @Override
    public boolean apply(Artifact artifact) {
        for (MinimalMavenDependencyDescription minimalMavenDependencyDescription : listOfDependencyNamesToIncludeInGraph) {
            if (new ArtifactHeeftNaamDieMatchtMetMinimalMavenDependencyPredicate(minimalMavenDependencyDescription)
                    .apply(artifact)) {
                return true;
            }
        }
        return false;
    }
}
