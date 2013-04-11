package edu.scripps.fl.test;

import edu.scripps.fl.pubchem.cpdp.CPDPException;

public class CPDPExceptionTest {

	public static void f() throws CPDPException {
		System.out.println("Throwing CPDPException from f()");
		throw new CPDPException();
	}

	public static void g() throws CPDPException {
		System.out.println("Throwing CPDPException from g()");
		throw new CPDPException("Originated in g()");
	}
	
	public static void k() throws Exception {
		throw new Exception("k exception");
	}
	
	public static void j() throws Exception{
		k();
	}

	public static void main(String[] args) throws CPDPException {
//		try {
//			f();
//		}
//		catch (CPDPException e) {
//			e.printStackTrace();
//		}
//		try {
//			g();
//		}
//		catch (CPDPException e) {
//			e.printStackTrace();
//		}
		
		try{
			j();
		}catch(Exception ex){
			CPDPException cex = new CPDPException("Problem in j: " + ex.getMessage());
			cex.setStackTrace(ex.getStackTrace());
			throw cex;
		}
	}

}
