package io.github.johnjcool.keycloak.broker.cas.mappers;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class AbstractAttributeMapperTest {

	@Test
	public void getAttributeValueListTest() {
		Object userAttribute = "[jioij098, kjh#´lkjkj, oijoiuo]";
		Object list = AbstractAttributeMapper.getAttributeValue(userAttribute);
		Assert.assertTrue(list instanceof ArrayList);
	}

	@Test
	public void getAttributeValueStringTest() {
		Object userAttribute = "fsiudhfk";
		Assert.assertTrue(AbstractAttributeMapper.getAttributeValue(userAttribute) instanceof String);
	}

	@Test
	public void getAttributeValueObjectTest() {
		Object userAttribute = new Object();
		Assert.assertTrue(AbstractAttributeMapper.getAttributeValue(userAttribute) instanceof Object);
	}
}
