package edu.scripps.fl.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;


public class UserInfoTest {
	
	public static void main(String[] args) throws Exception{
		
		Properties loginProps = new Properties();
		File file = new File(System.getProperty("user.home")+"\\PubChemXMLToolProperties.txt");
		if(file.exists() == true){
			FileInputStream in = new FileInputStream(file);
		if(in != null){
			loginProps.load(in);
			in.close();
			System.out.println(loginProps.getProperty("user.name"));
		}
		}
		else
			System.out.println("File does not exist");
		
	}
	
	public void logonToPubChem() throws Exception{
		
		Properties loginProps = new Properties();
		File file = new File(System.getProperty("user.home") + "\\PubChemXMLToolProperties.txt");
		if (file.exists() == true) {
			FileInputStream in = new FileInputStream(file);
			if (in != null) {
				loginProps.load(in);
				in.close();
				System.out.println(loginProps.getProperty("user.name"));
			}
		} else
			System.out.println("File does not exist");
		
	}
	
	
}
