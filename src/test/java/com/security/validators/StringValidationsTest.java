package com.security.validators;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringValidationsTest {

	@Test
	public void hasValidSize() {
		
		StringValidations strVals = new StringValidations();
		
		boolean result = strVals.hasValidSize("jovani", 2, 8);
		assertEquals(true, result);
		
		result = strVals.hasValidSize("jovani", 2, 5);
		assertEquals(false, result);
		
		result = strVals.hasValidSize("jovani", 2, 6);
		assertEquals(true, result);
		
		result = strVals.hasValidSize("jovani", 7, 10);
		assertEquals(false, result);
		
		result = strVals.hasValidSize("jovani", 6, 6);
		assertEquals(true, result);
		
		
	}
	
	
}
