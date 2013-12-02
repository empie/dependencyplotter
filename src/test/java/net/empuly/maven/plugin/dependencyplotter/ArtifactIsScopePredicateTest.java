package net.empuly.maven.plugin.dependencyplotter;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.apache.maven.artifact.Artifact;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

public class ArtifactIsScopePredicateTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	@Rule
	public MockitoRule mockitoRule = new MockitoRule(this);

	@Mock
	private Artifact artifactMock;

	@Test
	public void kanScopeFilteringPredicateEnkelAanmakenMetScopeDieNietNullIs() {
		expectedException.expect(IllegalArgumentException.class);
		new ArtifactIsScopePredicate(null);
	}

	@Test
	public void kanScopeFilteringPredicateEnkelAanmakenMetScopeDieNietLeegIs() {
		expectedException.expect(IllegalArgumentException.class);
		new ArtifactIsScopePredicate(" ");
	}

	@Test
	public void kanScopeFilteringPredicateEnkelAanmakenMetScopeDieGekendIsAlsMavenArtifactScope() {
		expectedException.expect(IllegalArgumentException.class);
		new ArtifactIsScopePredicate("koekoek");
	}

	@Test
	public void kanScopeFilteringPredicateAanmakenGekendeMavenScope() {
		ArtifactIsScopePredicate predicate = new ArtifactIsScopePredicate(Artifact.SCOPE_COMPILE);
		assertThat(predicate).isNotNull();
	}

	@Test
	public void gegevenEenArtifactDieDeJuisteScopeHeeft_wanneerApplyPredicate_danTrue() {
		when(artifactMock.getScope()).thenReturn(Artifact.SCOPE_COMPILE);
		ArtifactIsScopePredicate predicate = new ArtifactIsScopePredicate(Artifact.SCOPE_COMPILE);
		assertThat(predicate.apply(artifactMock)).isTrue();
	}

	@Test
	public void gegevenEenArtifactDieNietDeJuisteScopeHeeft_wanneerApplyPredicate_danFalse() {
		when(artifactMock.getScope()).thenReturn(Artifact.SCOPE_PROVIDED);
		ArtifactIsScopePredicate predicate = new ArtifactIsScopePredicate(Artifact.SCOPE_COMPILE);
		assertThat(predicate.apply(artifactMock)).isFalse();
	}

}
