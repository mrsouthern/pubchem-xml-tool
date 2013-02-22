package edu.scripps.fl.pubchem.cpdp;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPDPXMLProcessServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(CPDPXMLProcessServlet.class);
	private static final int BUFSIZE = 4096;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		DOMConfigurator.configure(CPDPXMLProcessServlet.class.getClassLoader().getResource("log4j.config.xml"));
		super.init(config);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		
		if (!isMultipart)
			return;
	
		// Create a factory for disk-based file items
		String tempDir = System.getProperty("java.io.tmpdir");
		FileItemFactory factory = new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, new File(tempDir));
		
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		
		try {
			List<FileItem> items = new ArrayList<FileItem>();
			items = upload.parseRequest(req);
			String format = "";
			InputStream stream = null;
			for (FileItem item : items) {
				if (!item.isFormField()) {
					log.info("File name: " + item.getName());
					String streamName = item.getName();
					stream = item.getInputStream();

				}
				else {
					String name = item.getFieldName();
					String value = item.getString();
					if ("format".equals(name))
						format = value;
				}
			}
			
			if(stream != null){
				
				File file = null;
				if("xml".equalsIgnoreCase(format)){
					file = CPDPXMLProcess.createPubChemXMLFile(stream);
					log.info("PubChem XML: " + file.getName());
				}else{
//					Desktop.getDesktop().open(CPDPXMLProcess.createExcel(stream));
					file = CPDPXMLProcess.createExcel(stream);
					log.info("PubChem Excel: " + file.getName());
				}
				
				//http://www.java-forums.org/blogs/servlet/668-how-write-servlet-sends-file-user-download.html
				int length   = 0;
			        
			        ServletContext context  = getServletContext();
			        String mimetype = context.getMimeType(file.getName());
			        
			        // sets response content type
			        if (mimetype == null) {
			            mimetype = "application/octet-stream";
			        }
			        resp.setContentType(mimetype);
			        resp.setContentLength((int)file.length());
			        String fileName = file.getName();
			        
			        // sets HTTP header
			        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			        
			        byte[] byteBuffer = new byte[BUFSIZE];
			        DataInputStream in = new DataInputStream(new FileInputStream(file));
			        ServletOutputStream outStream = resp.getOutputStream();
			        // reads the file's bytes and writes them to the response stream
			        while ((in != null) && ((length = in.read(byteBuffer)) != -1))
			        {
			            outStream.write(byteBuffer,0,length);
			        }
			        
			        in.close();
			        outStream.close();
			}
		}
		catch (Exception e) {
			resp.setStatus(resp.SC_BAD_REQUEST);
			e.printStackTrace(resp.getWriter());
			e.printStackTrace();
		}
		

	}

}
