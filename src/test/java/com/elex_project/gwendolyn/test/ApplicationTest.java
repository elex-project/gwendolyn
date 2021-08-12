/*
 * Project Gwendolyn
 *
 * Copyright (c) 2021-2021. Elex.
 * https://www.elex-project.com/
 */

package com.elex_project.gwendolyn.test;

import com.elex_project.gwendolyn.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class ApplicationTest {

	@Test
	void test() throws IOException {
		System.out.println(Application.getVersion());
	}

}
