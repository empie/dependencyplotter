package net.empuly.dependencyenforcer.maven.plugin.dependencyenforcer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import net.empuly.dependencyenforcer.maven.plugin.dependencyenforcer.IsDependencyPredicate;
import net.empuly.dependencyenforcer.maven.plugin.dependencyenforcer.MinimalMavenDependencyDescription;

import org.apache.maven.artifact.Artifact;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

public class IsDependencyPredicateTest {
    
    private static final String ARTIFACT_ID = "artifactId";
    private static final String GROUP_ID = "groupId";
    private static final String OTHER_ARTIFACT_ID = "artifactId2";
    private static final String OTHER_GROUP_ID = "groupId2";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public MockitoRule mockitoRule = new MockitoRule(this);
    
    @Mock
    private MinimalMavenDependencyDescription dependencyMock;
    @Mock
    private Artifact artifactMock;
    
    @SuppressWarnings("unused")
    @Test
    public void kanIsDependencyPredicateEnkelAanmakenMetDependencyToFindDieNietNullIs() {
        expectedException.expect(NullPointerException.class);
        new IsDependencyPredicate(null);
    }
    
    @Test
    public void anIsDependencyPredicateAanmakenMetDependencyToFindDieNietNullIs() {
        IsDependencyPredicate predicate = new IsDependencyPredicate(dependencyMock);
        assertThat(predicate).isNotNull();
    }
    
    @Test
    public void gegevenEenArtifactDieVerschillendeGroupIdHeeft_wanneerApplyPredicate_danFalse() {
        when(dependencyMock.getGroupId()).thenReturn(GROUP_ID);
        when(dependencyMock.getArtifactId()).thenReturn(ARTIFACT_ID);
        when(artifactMock.getGroupId()).thenReturn(OTHER_GROUP_ID);
        when(artifactMock.getArtifactId()).thenReturn(ARTIFACT_ID);
        IsDependencyPredicate predicate = new IsDependencyPredicate(dependencyMock);
        assertThat(predicate.apply(artifactMock)).isFalse();
    }
    
    @Test
    public void gegevenEenArtifactDieVerschillendeArtifactIdHeeft_wanneerApplyPredicate_danFalse() {
        when(dependencyMock.getGroupId()).thenReturn(GROUP_ID);
        when(dependencyMock.getArtifactId()).thenReturn(ARTIFACT_ID);
        when(artifactMock.getGroupId()).thenReturn(GROUP_ID);
        when(artifactMock.getArtifactId()).thenReturn(OTHER_ARTIFACT_ID);
        IsDependencyPredicate predicate = new IsDependencyPredicate(dependencyMock);
        assertThat(predicate.apply(artifactMock)).isFalse();
    }
    
    @Test
    public void gegevenEenArtifactDieZelfdeArtifactIdEnGroupIdHeeft_wanneerApplyPredicate_danTrue() {
        when(dependencyMock.getGroupId()).thenReturn(GROUP_ID);
        when(dependencyMock.getArtifactId()).thenReturn(ARTIFACT_ID);
        when(artifactMock.getGroupId()).thenReturn(GROUP_ID);
        when(artifactMock.getArtifactId()).thenReturn(ARTIFACT_ID);
        IsDependencyPredicate predicate = new IsDependencyPredicate(dependencyMock);
        assertThat(predicate.apply(artifactMock)).isTrue();
    }
    
}
