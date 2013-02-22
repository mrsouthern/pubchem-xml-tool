/*
 * Copyright 2010 The Scripps Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.scripps.fl.pubchem.xmltool.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class GUIComponent {
		
	
	private JFileChooser jfcFiles;
	private String version ="April 6th, 2012";
	private static final Logger log = LoggerFactory.getLogger(GUIComponent.class);
	
	protected void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
       
        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
        
        s = doc.addStyle("right", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_RIGHT);
    }
	
	
	
	public File checkFileExtension(String ext){
		File file = jfcFiles.getSelectedFile();
		String name = file.getName();
		if (name.indexOf('.')==-1) {
			name += ext;
			file = new File(file.getParentFile(), name);
		}
		return file;
	}
	
	public void openPDF(boolean isInternalR, File pdf, Component comp) throws FileNotFoundException{
		if(isInternalR){
			try{
				Desktop.getDesktop().open(pdf);
			}
			catch(Exception ex){
				log.info(ex.getMessage());
				String msg = "Unable to open PDF format through java. Would you like to save " + pdf.getAbsoluteFile() + " in a new location?";
				JOptionPane pane = new JOptionPane(msg, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION,null, null,JOptionPane.YES_OPTION);
				JDialog dialog = pane.createDialog(comp, "Report PDF");
				dialog.setVisible(true);
				Object choice = pane.getValue();
				if(choice.equals(JOptionPane.YES_OPTION)){
					jfcFiles = new JFileChooser();
					jfcFiles.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);	
					int state = jfcFiles.showSaveDialog(comp);
					
					if(state == JFileChooser.APPROVE_OPTION){
						File file = checkFileExtension(".pdf");
						log.info("Opening: " + file.getName() + ".");
						if(file != null)
							pdf.renameTo(file);
						}
					else{
						log.info("Open command cancelled by user.");
					}		
				}
			}
		}
	}
	
	
	public ImageIcon createGeneralIcon(String imageName) {
	        String imgLocation = "toolbarButtonGraphics/general/"
	                             + imageName
	                             + ".gif";
	        java.net.URL imageURL = getClass().getClassLoader().getResource(imgLocation);

	        if (imageURL == null) {
	            log.error("Resource not found: "
	                               + imgLocation);
	            return null;
	        } else {
	            return new ImageIcon(imageURL);
	        }
	    }
	
	public GridBagConstraints createGridBagConstraint(int gridx, int gridy, Component component, String position){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.ipady = 4;
		gbc.insets = new Insets(5,5,5,5);
		if(position.equals("line start"))
		gbc.anchor = GridBagConstraints.LINE_START;
		else if(position.equals("center"))
			gbc.anchor = GridBagConstraints.CENTER;
		else if(position.equals("line end"))
			gbc.anchor = GridBagConstraints.LINE_END;
		return gbc;
	}
	
	public JButton createJButton(String text, String toolTip, String type){
		JButton jbn = null;
		if(type.equals("icon")){
			jbn = new JButton(createGeneralIcon(text));
		}
		else if(type.equals("text")){
		Font jbnFont = new Font(Font.SANS_SERIF, Font.BOLD, 10);
		jbn = new JButton(text);
		jbn.setFont(jbnFont);
		}
		jbn.setOpaque(true);
		jbn.setToolTipText(toolTip);
		return jbn;
	}
	
	public JTextPane createJTextPane(String text){
		JTextPane jtp = new JTextPane();
		jtp.setText(text);
		SimpleAttributeSet underline = new SimpleAttributeSet();
		StyleConstants.setUnderline(underline, true);	
		jtp.getStyledDocument().setCharacterAttributes(0,text.length() , underline,true);
		jtp.setEditable(false);
		jtp.setOpaque(false);
		jtp.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		jtp.setBorder(BorderFactory.createEmptyBorder());
		jtp.setForeground(Color.blue);
		jtp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		return jtp;
	}
	
	 public JLabel createJLabel(String name){
			Font jlbFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
			JLabel jlb = new JLabel(name);
			jlb.setFont(jlbFont);
			return jlb;
		}
	 public JTextField createJTextField(String content){
		Font jtfFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
		JTextField jtf = new JTextField(content);
		jtf.setColumns(50);
		jtf.setFont(jtfFont);
		return jtf;
	}
	 
		public JTextPane createJTextPane(){
			String newline = "\n";
			String[] description =
				{ 	"PubChem XML Tool (Version: " + version + ")" + newline,
					"By: S. Canny (scanny@scripps.edu) and M. Southern (southern@scripps.edu)" + newline,
					"" + newline,
					"PubChem XML Tool main functions:" + newline,
					"1. Create a PubChem XML that can include Assay, Result TIDs, Xrefs, Panel, and Categorized Comments." + newline,
					"2. Extract Assay, Result TID, Xref, Panel, and Categorized Comment information from a PubChem XML." + newline,
					"3. Create a report from an Excel workbook or PubChem XMLs." + newline,
					"" + newline,
					"Other features:" + newline,
					"1. Automatically adds reference section to description of PubChem XML or a report if placeholder is used." + newline,
					"2. Checks proteins, genes, omims, and taxonomies for connections when creating PubChem XML or a report." + newline,
					"3. Can retrieve on-hold and newly deposited assays from deposition system to extract or create report." + newline,
					"" + newline,
					"\t\t\t(c) 2010, The Scripps Research Institute- Florida"
				};
			String[] styles = { "bold", "regular", "regular", "regular", "regular", "regular", "regular", "regular", "regular","regular",  "regular", "regular", "regular", "regular", "right" };
			
			JTextPane jtp = new JTextPane();
			StyledDocument doc = jtp.getStyledDocument();
			addStylesToDocument(doc);
			
			try{
				for(int ii = 0; ii < description.length; ii++)
					doc.insertString(doc.getLength(), description[ii], doc.getStyle(styles[ii]));
			}
			catch(BadLocationException ble){
				log.error(ble.getMessage(), ble);
			}
			jtp.setOpaque(false);
			jtp.setEditable(false);
			jtp.setPreferredSize(new Dimension(640, 230));

			return jtp;
		}
		
		public File fileChooser(JTextField jtf, String ext, String openOrSave){
			jfcFiles = new JFileChooser(jtf.getText());
			if(! ext.equals(""))
				jfcFiles.addChoosableFileFilter(new MyFileFilter(ext));
			jfcFiles.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);	
			
			int state;
			if(openOrSave.equals("open"))
				state = jfcFiles.showOpenDialog(null);
			else
				state = jfcFiles.showSaveDialog(null);
			
			if(state == JFileChooser.APPROVE_OPTION){
				File file = checkFileExtension(ext);
				jtf.setText(file.getPath());
				log.info("Opening: " + file.getName() + ".");
				return file;
				}
			else{
				log.info("Open command cancelled by user.");
				return null;
			}		
		}
		
		public ImageIcon loadIcon(String name) {
		    URL imgUrl = GUIComponent.class.getClassLoader().getResource(name);
		    	return new ImageIcon(imgUrl);
		}


}
