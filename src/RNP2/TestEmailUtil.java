package RNP2;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class TestEmailUtil {

	@Test
	public void test() {
		EmailUtil eu = new EmailUtil();
		assertEquals(eu.addKonto("pop.gmail.com", "d.suewolto@gmail.com", "", "995", "smtp.gmail.com", "465"),true);
	}

}
