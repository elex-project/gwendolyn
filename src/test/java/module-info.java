module com.elex_project.ghoul.test {
	requires static lombok;
	requires org.junit.jupiter.api;
	requires com.elex_project.ghoul;
	requires org.slf4j;
	opens com.elex_project.gwendolyn.test to org.junit.platform.commons;
}
