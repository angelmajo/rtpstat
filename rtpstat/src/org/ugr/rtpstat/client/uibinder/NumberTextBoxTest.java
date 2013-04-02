package org.ugr.rtpstat.client.uibinder;


public class NumberTextBoxTest{}/* extends GWTTestCase {
	private NumberTextBox ntb;

	@Override
	public String getModuleName() {
		return "org.ugr.rtpstat.Rtpstat";
	}

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		ntb = new NumberTextBox();
	}

	public void testFloatValido() {
		ntb.setValue("1.2");
		assertTrue(ntb.isValid(false));
	}

	public void testFloatNoValido() {
		ntb.setValue("1.2.");
		assertFalse(ntb.isValid(false));
		assertEquals(ntb.getTitle(), NumberTextBox.MSG_SOLO_NUMEROS);
	}

	public void testFloatEnNatural() {
		ntb.setTipoNumero(TipoNumero.NATURAL);
		ntb.setValue("1.2");
		assertFalse(ntb.isValid(false));
		assertEquals(ntb.getTitle(), NumberTextBox.MSG_SOLO_NATURALES);
	}

	public void testCambioFloatANatural() {
		ntb.setValue("1.2");
		ntb.setTipoNumero(TipoNumero.NATURAL);
		assertFalse(ntb.isValid(false));
		assertEquals(ntb.getTitle(), NumberTextBox.MSG_SOLO_NATURALES);
	}

	public void testNaturalNegativo() {
		ntb.setTipoNumero(TipoNumero.NATURAL);
		ntb.setValue("-1");
		assertFalse(ntb.isValid(false));
		assertEquals(ntb.getTitle(), NumberTextBox.MSG_SOLO_NATURALES);
	}

	public void testPrecisionNaturalEntero() {
		ntb.setTipoNumero(TipoNumero.NATURAL);
		boolean haFallado = false;
		try {
			ntb.setPrecision(2);
		} catch (IllegalStateException e) {
			haFallado = true;
		}
		assertTrue(haFallado);
	}

	public void testPrecisionAntesDeSetValue() {
		ntb.setPrecision(5);
		ntb.setValue("0");
		assertEquals("0.00000", ntb.getValue());
	}

	public void testPrecisionDespuesDeSetValueAumentando() {
		ntb.setValue("0");
		ntb.setPrecision(5);
		assertEquals("0.00000", ntb.getValue());
	}

}*/
