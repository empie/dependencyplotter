package net.empuly.maven.plugin.dependencyplotter;

import static org.fest.assertions.Assertions.assertThat;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MinimalMavenDependencyDescriptionTest {

	private static final String GROUP_ID = "groupId";
	private static final String ARTIFACT_ID = "artifactId";
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void kanMinimalMavenDependencyDescriptionNietMakenMetGroupIdNull() {
		expectedException.expect(IllegalArgumentException.class);
		new MinimalMavenDependencyDescription(null, ARTIFACT_ID);
	}

	@Test
	public void kanMinimalMavenDependencyDescriptionNietMakenMetArtifactIdNull() {
		expectedException.expect(IllegalArgumentException.class);
		new MinimalMavenDependencyDescription(GROUP_ID, null);
	}

	@Test
	public void kanMinimalMavenDependencyDescriptionNietMakenMetGroupIdLeeg() {
		expectedException.expect(IllegalArgumentException.class);
		new MinimalMavenDependencyDescription(" ", ARTIFACT_ID);
	}

	@Test
	public void kanMinimalMavenDependencyDescriptionNietMakenMetArtifactIdLeeg() {
		expectedException.expect(IllegalArgumentException.class);
		new MinimalMavenDependencyDescription(GROUP_ID, " ");
	}

	@Test
	public void kanMinimalMavenDependencyDescriptionAanMaken() {
		MinimalMavenDependencyDescription description = new MinimalMavenDependencyDescription(GROUP_ID, ARTIFACT_ID);
		assertThat(description.getArtifactId()).isEqualTo(ARTIFACT_ID);
		assertThat(description.getGroupId()).isEqualTo(GROUP_ID);
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(MinimalMavenDependencyDescription.class).verify();
		assertThat(new MinimalMavenDependencyDescription(GROUP_ID, ARTIFACT_ID).toString()).isNotEmpty();
	}
}
