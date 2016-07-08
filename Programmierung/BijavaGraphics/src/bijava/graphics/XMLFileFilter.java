package bijava.graphics;

import java.io.*;

/**
 *
 * @author  Peter
 */
public class XMLFileFilter extends javax.swing.filechooser.FileFilter
{
	public XMLFileFilter()
	{
		super();
	}

	public boolean accept(File file)
	{
		if (file.isDirectory())
		  return true;

		if (file.getName().indexOf(".xml")>0 || file.getName().indexOf(".xml".toUpperCase())>0 || file.getName().indexOf(".xml".toLowerCase())>0)
			return true;

		return false;
	}

	public String getDescription()
	{
		StringBuffer str=new StringBuffer("xml-Steuerdatei (.xml)");
		return str.toString();
	}

}